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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKTokenExpiredHandler;
import com.vk.api.sdk.auth.VKAccessToken;
import com.vk.api.sdk.auth.VKAuthCallback;
import com.vk.api.sdk.auth.VKScope;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

import static ru.limedev.endecrypter.core.Constants.SUCCESSFULLY_LOGIN;
import static ru.limedev.endecrypter.core.Constants.UNSUCCESSFULLY_LOGIN;

@SuppressLint("Registered")
public class VKLoginActivity extends AppCompatActivity {

    private final VKTokenExpiredHandler tokenTracker = new VKTokenExpiredHandler() {
        public void onTokenExpired() {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_check_login);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        final Context context = this;

        if (!VK.onActivityResult(requestCode, resultCode, data, new VKAuthCallback() {

            @Override
            public void onLoginFailed(int i) {
                Toast.makeText(context, UNSUCCESSFULLY_LOGIN, Toast.LENGTH_LONG).show();
                finish();
            }

            @Override
            public void onLogin(@NotNull VKAccessToken vkAccessToken) {
                Toast.makeText(context, SUCCESSFULLY_LOGIN, Toast.LENGTH_LONG).show();
                finish();
            }
        })) {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void loginLogin(View view) {
        if (!VK.isLoggedIn()) {
            VK.login(this, Arrays.asList(VKScope.OFFLINE, VKScope.WALL, VKScope.PHOTOS, VKScope.STATUS, VKScope.MESSAGES));
            VK.addTokenExpiredHandler(tokenTracker);
        }
    }
}
