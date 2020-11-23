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

package ru.limedev.endecrypter.core.models

import android.os.Parcel
import android.os.Parcelable
import org.json.JSONObject

data class VKMessage(
        val id: Int = 0,
        val date: Int = 0,
        val peerId: Int = 0,
        val fromId: Int = 0,
        val out: Int = 0,
        var text: String = "") : Parcelable {

    constructor(parcel: Parcel) : this(
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readString()!!)

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeInt(date)
        parcel.writeInt(peerId)
        parcel.writeInt(fromId)
        parcel.writeInt(out)
        parcel.writeString(text)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<VKMessage> {
        override fun createFromParcel(parcel: Parcel): VKMessage {
            return VKMessage(parcel)
        }

        override fun newArray(size: Int): Array<VKMessage?> {
            return arrayOfNulls(size)
        }

        fun parse(json: JSONObject)
                = VKMessage(id = json.optInt("id", 0),
                date = json.optInt("date", 0),
                peerId = json.optInt("peer_id", 0),
                fromId = json.optInt("from_id", 0),
                out = json.optInt("out", 0),
                text = json.optString("text", ""))
    }

    fun set(value: String) {
        text = value
    }

    override fun equals(other: Any?): Boolean
            = (other is VKMessage)
            && id == other.id

    override fun hashCode(): Int {
        return id
    }
}