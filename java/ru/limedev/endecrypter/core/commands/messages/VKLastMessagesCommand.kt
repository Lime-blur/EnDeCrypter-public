/*******************************************************************************
 * The MIT License (MIT)
 *
 * Copyright (c) 2019 vk.com
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package ru.limedev.endecrypter.core.commands.messages

import ru.limedev.endecrypter.core.models.VKMessage
import ru.limedev.endecrypter.core.models.VKUser
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

class VKLastMessagesCommand(private val offset: Int = 0,
                            private val count: Int = 20,
                            private val filter: String = "all"): ApiCommand<Map<VKMessage, VKUser>>() {

    override fun onExecute(manager: VKApiManager): Map<VKMessage, VKUser> {
        val call = VKMethodCall.Builder()
                .method("messages.getConversations")
                .args("offset", offset)
                .args("count", count)
                .args("filter", filter)
                .args("extended", 1)
                .args("fields", "photo_100")
                .version(manager.config.version)
                .build()
        return manager.execute(call, ResponseApiParser())
    }

    private class ResponseApiParser : VKApiResponseParser<Map<VKMessage, VKUser>> {
        override fun parse(response: String): Map<VKMessage, VKUser> {
            try {
                val responseObject = JSONObject(response).getJSONObject("response")
                val profiles = responseObject.getJSONArray("profiles")
                val usersList = ArrayList<VKUser>(profiles.length())
                for (i in 0 until profiles.length()) {
                    val user = VKUser.parse(profiles.getJSONObject(i))
                    usersList.add(user)
                }
                val ja = responseObject.getJSONArray("items")
                val r = HashMap<VKMessage, VKUser>(ja.length())
                for (i in 0 until ja.length()) {
                    val lastMessageObject = ja.getJSONObject(i).getJSONObject("last_message")
                    val peerId = lastMessageObject.getInt("peer_id")
                    val vkUser = usersList.find { user -> peerId == user.id }
                    val lastMessage = VKMessage.parse(lastMessageObject)
                    if (vkUser != null) {
                        r[lastMessage] = vkUser
                    }
                }
                return r
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}