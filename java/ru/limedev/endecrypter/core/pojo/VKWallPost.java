package ru.limedev.endecrypter.core.pojo;

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

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.limedev.endecrypter.MainActivity;
import ru.limedev.endecrypter.R;

import static ru.limedev.endecrypter.core.Constants.*;

public class VKWallPost {

    private static int lastId;

    private static List<View> viewList = new ArrayList<>();
    private final TextView textView;
    private final Button button;

    public VKWallPost(Context context) {
        this.textView = new TextView(context);
        this.button = new Button(context);
    }

    public void addViewsToList() {
        viewList.add(getTextView());
        viewList.add(getButton());
    }

    public static void cleanViewsFromList(RelativeLayout relativeLayout) {
        for (View view : viewList) {
            relativeLayout.removeView(view);
        }
        viewList.clear();
    }

    private int getLastId() {
        return lastId;
    }

    public void setLastId(int lastId) {
        VKWallPost.lastId = lastId;
    }

    public TextView getTextView() {
        return textView;
    }

    public Button getButton() {
        return button;
    }

    public void setTextView(RelativeLayout relativeLayout, String text) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        getTextView().setText(text);
        getTextView().setLayoutParams(params);
        relativeLayout.addView(getTextView());
        getTextView().setId(View.generateViewId());
        setLastId(getTextView().getId());
    }

    public void setButton(RelativeLayout relativeLayout, final Context context) {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.
                LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);
        getButton().setText(DECRYPT);
        getButton().setLayoutParams(params);
        getButton().setBackgroundResource(R.drawable.layout_button_bg);
        getButton().setTextColor(Color.parseColor(COLOR_WHITE));
        getButton().getLayoutParams().width = ViewGroup.LayoutParams.MATCH_PARENT;
        getButton().getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
        relativeLayout.addView(getButton());
        getButton().setId(View.generateViewId());
        setLastId(getButton().getId());

        getButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MainActivity.class);
                intent.putExtra(TEXT_FROM_WALL, getTextView().getText());
                context.startActivity(intent);
            }
        });
    }

    private void setLayoutParams(RelativeLayout.LayoutParams params) {
        params.leftMargin = MARGIN_SIDES;
        params.rightMargin = MARGIN_SIDES;
        params.topMargin = MARGIN_TOP_BOTTOM;
        params.bottomMargin = MARGIN_TOP_BOTTOM;
        params.addRule(RelativeLayout.BELOW, getLastId());
    }
}
