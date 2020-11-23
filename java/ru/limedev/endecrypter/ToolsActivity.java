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
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import ru.limedev.endecrypter.core.UserSettings;

import static ru.limedev.endecrypter.core.Constants.*;
import static ru.limedev.endecrypter.core.utilities.UtilitiesMain.isValidFilename;

@SuppressLint("Registered")
public class ToolsActivity extends AppCompatActivity {

    private SharedPreferences edcSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_tools);

        edcSettings = getSharedPreferences(UserSettings.appPreferences, Context.MODE_PRIVATE);

        EditText saveFileName = findViewById(R.id.saveFileName);

        saveFileName.setText(UserSettings.saveFile);
    }

    public void saveGeneralSettings(View view) {
        EditText saveFileEditName = findViewById(R.id.saveFileName);
        String saveFileEditNameText = saveFileEditName.getText().toString();

        try {
            if (isValidFilename(saveFileEditNameText)) {
                SharedPreferences.Editor editor = edcSettings.edit();

                editor.putString(UserSettings.saveFileName, saveFileEditNameText);
                UserSettings.saveFile = saveFileEditNameText;

                editor.apply();

                Intent intent = new Intent(ToolsActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(this, SETTINGS_SAVED, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, CAN_NOT_WRITE_VALUES, Toast.LENGTH_LONG).show();
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, CAN_NOT_WRITE_VALUES, Toast.LENGTH_LONG).show();
        }
    }
}
