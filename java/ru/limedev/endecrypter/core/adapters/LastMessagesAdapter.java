package ru.limedev.endecrypter.core.adapters;

/*
 * MIT License
 *
 * Copyright (c) 2020 Tim Meleshko
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
 */

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Map;

import ru.limedev.endecrypter.R;
import ru.limedev.endecrypter.core.impl.CircleTransform;
import ru.limedev.endecrypter.core.models.VKMessage;
import ru.limedev.endecrypter.core.models.VKUser;

import static ru.limedev.endecrypter.core.Constants.ATTACHMENT;
import static ru.limedev.endecrypter.core.Constants.ELLIPSIS;
import static ru.limedev.endecrypter.core.Constants.MAX_LAST_MESSAGE_LENGTH;
import static ru.limedev.endecrypter.core.utilities.UtilitiesMain.convertUnixDate;

public class LastMessagesAdapter extends ArrayAdapter<VKMessage> {

    private Map<VKMessage, VKUser> lastMessagesMap;

    public LastMessagesAdapter(Context context, Map<VKMessage, VKUser> lastMessagesMap) {
        super(context, R.layout.dialog_list_item, new ArrayList<>(lastMessagesMap.keySet()));
        this.lastMessagesMap = lastMessagesMap;
    }

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        VKMessage currentLastMessage = getItem(position);
        VKUser currentUser = lastMessagesMap.get(currentLastMessage);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_list_item,
                    parent, false);
        }

        if (currentLastMessage != null && currentUser != null) {
            TextView userName = convertView.findViewById(R.id.dialogsNameItem);
            userName.setText(currentUser.getFirstName() + " " + currentUser.getLastName());

            TextView messageTime = convertView.findViewById(R.id.dialogsTimeItem);
            messageTime.setText(convertUnixDate(currentLastMessage.getDate()));

            TextView message = convertView.findViewById(R.id.dialogsTextItem);
            String messageText = currentLastMessage.getText();
            if (messageText.isEmpty()) {
                messageText = ATTACHMENT;
            }
            if (messageText.length() > MAX_LAST_MESSAGE_LENGTH) {
                messageText = messageText.substring(0, MAX_LAST_MESSAGE_LENGTH) + ELLIPSIS;
            }
            message.setText(messageText);

            String userPhoto = currentUser.getPhoto();
            if (!TextUtils.isEmpty(userPhoto)) {
                ImageView avatar = convertView.findViewById(R.id.dialogsImageItem);
                Picasso.get()
                        .load(userPhoto)
                        .transform(new CircleTransform())
                        .error(R.drawable.ic_account_circle)
                        .into(avatar);
            }
        }

        return convertView;
    }
}
