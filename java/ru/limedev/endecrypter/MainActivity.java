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
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.JsonSyntaxException;
import com.vk.api.sdk.VK;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import ru.limedev.endecrypter.core.UserSettings;
import ru.limedev.endecrypter.core.exceptions.IllegalAlgorithmException;
import ru.limedev.endecrypter.core.commands.wall.VKWallPostCommand;
import ru.limedev.endecrypter.core.impl.ShowcaseViewCircle;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static ru.limedev.endecrypter.core.Constants.*;
import static ru.limedev.endecrypter.core.UserSettings.*;
import static ru.limedev.endecrypter.core.utilities.UtilitiesCrypto.*;
import static ru.limedev.endecrypter.core.utilities.UtilitiesMain.*;

public class MainActivity extends AppCompatActivity {

    private SharedPreferences edcSettings;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        edcSettings = getSharedPreferences(appPreferences, Context.MODE_PRIVATE);

        final EditText textBox = findViewById(R.id.textArea);

        textBox.setOnTouchListener(new ListView.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();
                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                v.onTouchEvent(event);
                return true;
            }
        });

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String textFromVK = extras.getString(TEXT_FROM_WALL);
            textBox.setText(textFromVK);
        }

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        boolean knewTutorial = edcSettings.getBoolean(UserSettings.mainTutorialName, DEFAULT_MAIN_TUTORIAL);

        if (!knewTutorial) {
            Button closeButton = new Button(this);
            closeButton.setBackgroundResource(R.drawable.layout_button_bg);
            closeButton.setTextColor(Color.parseColor(COLOR_WHITE));
            closeButton.setText(CLOSE);

            ShowcaseView showcaseView = new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(R.id.createAlgorithmButton, this))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setShowcaseDrawer(new ShowcaseViewCircle(getResources(), 180f))
                    .setContentTitle(WELCOME)
                    .setContentText(FIRST_ENTER_TEXT)
                    .replaceEndButton(closeButton)
                    .blockAllTouches()
                    .build();
            showcaseView.setTitleTextAlignment(Layout.Alignment.ALIGN_CENTER);
            showcaseView.setDetailTextAlignment(Layout.Alignment.ALIGN_CENTER);

            SharedPreferences.Editor editor = edcSettings.edit();
            editor.putBoolean(UserSettings.mainTutorialName, true);
            editor.apply();
        }
    }

    private BottomNavigationView.OnNavigationItemSelectedListener
            navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.dialogs_page:
                    if (checkNetwork()) {
                        if (!VK.isLoggedIn()) {
                            intent = new Intent(MainActivity.this, VKLoginActivity.class);
                        } else {
                            intent = new Intent(MainActivity.this, VKDialogsActivity.class);
                        }
                    } else {
                        intent = new Intent(MainActivity.this, NetworkActivity.class);
                    }
                    break;
                case R.id.profile_page:
                    if (checkNetwork()) {
                        if (!VK.isLoggedIn()) {
                            intent = new Intent(MainActivity.this, VKLoginActivity.class);
                        } else {
                            intent = new Intent(MainActivity.this, VKProfileActivity.class);
                        }
                    } else {
                        intent = new Intent(MainActivity.this, NetworkActivity.class);
                    }
                    break;
            }
            if (intent == null) {
                return false;
            }
            startActivity(intent);
            if (VK.isLoggedIn() && checkNetwork()) {
                finish();
            }
            return true;
        }
    };

    private boolean checkNetwork() {
        return isNetworkAvailable(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_action_menu, menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        EditText textBox = findViewById(R.id.textArea);

        if (requestCode == PICKFILE_RESULT_CODE) {
            if (resultCode == RESULT_OK) {
                Uri uri = data.getData();
                if (uri != null) {
                    saveFile = getFileName(this, uri);
                    byte[] fileContent = readTextFile(this, uri);
                    String text = new String(fileContent, StandardCharsets.UTF_8);
                    textBox.setText(text);
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        appFriendsCount = edcSettings.getInt(appFriendsCountName, DEFAULT_FRIENDS_COUNT);
        appPostsCount = edcSettings.getInt(appPostsCountName, DEFAULT_POST_COUNT);
        appDialogsCount = edcSettings.getInt(appDialogsCountName, DEFAULT_DIALOGS_COUNT);
        appMyDialogMessagesCount = edcSettings.getInt(appMyDialogMessagesCountName, DEFAULT_DIALOG_MESSAGES_COUNT);
        saveFile = edcSettings.getString(saveFileName, DEFAULT_FILE_SAFE);
        saveAlgorithm = edcSettings.getString(saveAlgorithmName, DEFAULT_ALGORITHM_SAFE);
    }

    public void openTextFile(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType(INTENT_TYPE);
        startActivityForResult(intent, PICKFILE_RESULT_CODE);
    }

    public void writeTextFile(View view) {
        EditText textBox = findViewById(R.id.textArea);
        String data = textBox.getText().toString();

        if (checkAppPermission(this)) {
            ActivityCompat.requestPermissions(this, new String[]
                    {WRITE_EXTERNAL_STORAGE}, 0);
        } else {
            if (isExternalStorageWritable()) {
                File originalFile = new File(Environment.getExternalStoragePublicDirectory(
                        Environment.DIRECTORY_DOCUMENTS), saveFile);
                File textFile = changeFileExtension(originalFile, TXT_EXT);
                try {
                    FileOutputStream fos = new FileOutputStream(textFile);
                    fos.write(data.getBytes(StandardCharsets.UTF_8));
                    fos.close();
                    Toast.makeText(this, FILE_SAVED + textFile.getPath(), Toast.LENGTH_LONG).show();
                } catch (IOException ex) {
                    Toast.makeText(this, ex.getMessage(), Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, UNWRITABLE_STORAGE, Toast.LENGTH_LONG).show();
            }
        }
    }

    public void encryptText(View view) {
        EditText textBox = findViewById(R.id.textArea);
        String textData = textBox.getText().toString();
        if (!textData.trim().isEmpty()) {
            try {
                String encryptedText = useEncryptAlgorithm(textData);
                textBox.setText(encryptedText);
            } catch (JsonSyntaxException | IllegalAlgorithmException e) {
                Toast.makeText(this, ERROR_CRYPT, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, EMPTY_TEXT_FIELD, Toast.LENGTH_LONG).show();
        }
    }

    public void decryptText(View view) {
        EditText textBox = findViewById(R.id.textArea);
        String textData = textBox.getText().toString();
        if (!textData.trim().isEmpty()) {
            try {
                String decryptedText = useDecryptAlgorithm(textData);
                textBox.setText(decryptedText);
            } catch (JsonSyntaxException | IllegalAlgorithmException e) {
                Toast.makeText(this, ERROR_CRYPT, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, EMPTY_TEXT_FIELD, Toast.LENGTH_LONG).show();
        }
    }

    public void vkWallPublic(View view) {

        if (isNetworkAvailable(this)) {
            if (VK.isLoggedIn()) {
                EditText textBox = findViewById(R.id.textArea);
                String text = textBox.getText().toString();

                if (!text.isEmpty()) {
                    VKWallPostCommand vkWallPostCommand = new VKWallPostCommand(text, new ArrayList<Uri>(),
                            0, false, false);
                    VK.execute(vkWallPostCommand, null);
                    Toast.makeText(this, QUERY_SEND, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, EMPTY_TEXT_FIELD, Toast.LENGTH_LONG).show();
                }
            } else {
                Intent intent = new Intent(this, VKLoginActivity.class);
                startActivity(intent);
            }
        } else {
            Intent intent = new Intent(this, NetworkActivity.class);
            startActivity(intent);
        }
    }

    public void openToolsWindow(MenuItem item) {
        Intent intent = new Intent(MainActivity.this, ToolsActivity.class);
        startActivity(intent);
    }

    public void openAlgorithmWindow(View view) {
        Intent intent = new Intent(MainActivity.this, CreateAlgorithmActivity.class);
        startActivity(intent);
    }
}