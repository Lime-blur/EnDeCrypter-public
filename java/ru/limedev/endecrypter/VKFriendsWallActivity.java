package ru.limedev.endecrypter;

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
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import ru.limedev.endecrypter.core.commands.wall.VKPostsCommand;
import ru.limedev.endecrypter.core.models.VKPost;
import ru.limedev.endecrypter.core.pojo.VKWallPost;

import static ru.limedev.endecrypter.core.Constants.TEXT_TO_WALL;
import static ru.limedev.endecrypter.core.UserSettings.appPostsCount;
import static ru.limedev.endecrypter.core.pojo.VKWallPost.cleanViewsFromList;

@SuppressLint("Registered")
public class VKFriendsWallActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_wall);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            generateWallForUser(extras.getInt(TEXT_TO_WALL));
        }
    }

    private void generateWallForUser(int userId) {

        final Context context = this;

        findViewById(R.id.profileBottomProgressBar).setVisibility(View.VISIBLE);
        VK.execute(new VKPostsCommand(userId, appPostsCount), new VKApiCallback<List<? extends VKPost>>() {

            @Override
            public void success(List<? extends VKPost> result) {

                final RelativeLayout relativeLayout = findViewById(R.id.vkRelativeLayout);
                cleanViewsFromList(relativeLayout);
                int counter = 0;

                for (Object postObject : result) {
                    VKPost post = (VKPost) postObject;
                    String postText = post.getText().trim();
                    if (!postText.isEmpty()) {
                        VKWallPost vkWallPost = new VKWallPost(context);
                        if (counter == 0) {
                            vkWallPost.setLastId(R.id.profileBottomFriendsTitle);
                        }
                        vkWallPost.setTextView(relativeLayout, post.getText());
                        vkWallPost.setButton(relativeLayout, context);
                        vkWallPost.addViewsToList();
                        counter++;
                    }
                }
                finishProcess();
            }

            public void fail(@NotNull Exception error) {
                Log.e("ContentValues", error.toString());
                finishProcess();
            }

            void finishProcess() {
                findViewById(R.id.profileBottomProgressBar).setVisibility(View.GONE);
            }
        });
    }
}
