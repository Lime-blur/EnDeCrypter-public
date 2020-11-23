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

import ru.limedev.endecrypter.core.models.VKLongPoll
import com.vk.api.sdk.VKApiManager
import com.vk.api.sdk.VKApiResponseParser
import com.vk.api.sdk.VKMethodCall
import com.vk.api.sdk.exceptions.VKApiIllegalResponseException
import com.vk.api.sdk.internal.ApiCommand
import org.json.JSONException
import org.json.JSONObject

class VKGetLongPollServer(private val needPts: Int = 0,
                          private val lpVersion: Int = 3): ApiCommand<VKLongPoll>() {

    override fun onExecute(manager: VKApiManager): VKLongPoll {
        val call = VKMethodCall.Builder()
                .method("messages.getLongPollServer")
                .args("need_pts", needPts)
                .args("lp_version", lpVersion)
                .version(manager.config.version)
                .build()
        return manager.execute(call, ResponseApiParser())
    }

    private class ResponseApiParser : VKApiResponseParser<VKLongPoll> {
        override fun parse(response: String): VKLongPoll {
            try {
                val ja = JSONObject(response)
                return VKLongPoll.parse(ja.getJSONObject("response"))
            } catch (ex: JSONException) {
                throw VKApiIllegalResponseException(ex)
            }
        }
    }
}