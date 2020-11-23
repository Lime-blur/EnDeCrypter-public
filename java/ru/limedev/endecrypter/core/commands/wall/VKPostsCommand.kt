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

package ru.limedev.endecrypter.core.commands.wall

import ru.limedev.endecrypter.core.models.VKPost
import com.vk.api.sdk.*
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

class VKPostsCommand(private val ownerId: Int = 0,
                     private val count: Int = 5): ApiCommand<List<VKPost>>() {

    override fun onExecute(manager: VKApiManager): List<VKPost> {
        val call = VKMethodCall.Builder()
                .method("wall.get")
                .args("owner_id", ownerId)
                .args("count", count)
                .version(manager.config.version)
                .build()
        return manager.execute(call, ResponseApiParser())
    }

    private class ResponseApiParser : VKApiResponseParser<List<VKPost>> {
        override fun parse(response: String): List<VKPost> {
            try {
                val ja = JSONObject(response).getJSONObject("response").getJSONArray("items")
                val r = ArrayList<VKPost>(ja.length())
                for (i in 0 until ja.length()) {
                    val post = VKPost.parse(ja.getJSONObject(i))
                    r.add(post)
                }
                return r
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}