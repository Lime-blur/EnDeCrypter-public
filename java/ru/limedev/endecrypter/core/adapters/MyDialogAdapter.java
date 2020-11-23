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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.Collections;
import java.util.List;

import ru.limedev.endecrypter.R;
import ru.limedev.endecrypter.core.models.VKMessage;

import static ru.limedev.endecrypter.core.Constants.ATTACHMENT;
import static ru.limedev.endecrypter.core.utilities.UtilitiesMain.convertUnixDate;

public class MyDialogAdapter extends ArrayAdapter<VKMessage> {

    public MyDialogAdapter(Context context, List<VKMessage> myDialogList) {
        super(context, R.layout.my_dialog_list_item, myDialogList);
        Collections.reverse(myDialogList);
    }

    @SuppressLint({"RtlHardcoded", "ResourceAsColor"})
    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        VKMessage currentMessage = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.my_dialog_list_item, parent,
                    false);
        }

        if (currentMessage != null) {
            RelativeLayout layout = convertView.findViewById(R.id.myDialogForm);
            TextView message = convertView.findViewById(R.id.myDialogTextItem);
            TextView time = convertView.findViewById(R.id.myDialogTime);

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                    LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            String messageText = currentMessage.getText();
            if (messageText.isEmpty()) {
                messageText = ATTACHMENT;
            }
            if (currentMessage.getOut() == 1) {
                params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                layout.setLayoutParams(params);
            } else {
                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                layout.setLayoutParams(params);
            }
            message.setText(messageText);
            time.setText(convertUnixDate(currentMessage.getDate()));
        }

        return convertView;
    }
}
