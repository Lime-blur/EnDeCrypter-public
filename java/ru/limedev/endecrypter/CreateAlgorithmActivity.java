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
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.json.JSONObject;

import java.util.LinkedHashMap;
import java.util.Map;

import ru.limedev.endecrypter.core.UserSettings;
import ru.limedev.endecrypter.core.exceptions.IllegalAlgorithmException;
import ru.limedev.endecrypter.core.impl.ShowcaseViewRectangle;

import static ru.limedev.endecrypter.core.Constants.*;
import static ru.limedev.endecrypter.core.UserSettings.*;
import static ru.limedev.endecrypter.core.utilities.UtilitiesCrypto.*;
import static ru.limedev.endecrypter.core.utilities.UtilitiesMain.*;

@SuppressLint("Registered")
public class CreateAlgorithmActivity extends AppCompatActivity {

    private SharedPreferences edcSettings;
    private static Map<String, String> algorithmMap = new LinkedHashMap<>();
    private static String algorithmMapKey;
    private static int currentSteps = 0;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_algorithm);

        algorithmMap.clear();
        currentSteps = 0;
        edcSettings = getSharedPreferences(appPreferences, Context.MODE_PRIVATE);

        final EditText saveAlgorithmEditName = findViewById(R.id.algorithmResult);
        saveAlgorithmEditName.setText(encryptAppValues(UserSettings.saveAlgorithm));

        final Spinner algorithmSpinner = findViewById(R.id.algorithmSpinner);

        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(this, R.array.cryptNames,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        algorithmSpinner.setAdapter(adapter);

        algorithmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            public void onItemSelected(AdapterView<?> parent,
                                       View itemSelected, int selectedItemPosition, long selectedId) {
                String[] choose = getResources().getStringArray(R.array.cryptNames);
                String selected = choose[selectedItemPosition];
                findViewById(R.id.algorithmKeyArea).setVisibility(View.VISIBLE);
                if (selected.equals(BASE64) || selected.equals(REVERSE)) {
                    findViewById(R.id.algorithmKeyArea).setVisibility(View.GONE);
                }
                algorithmMapKey = selected;
            }

            public void onNothingSelected(AdapterView<?> parent) {}
        });

        boolean knewTutorial = edcSettings.getBoolean(UserSettings.algorithmTutorialName, DEFAULT_ALGORITHM_TUTORIAL);

        if (!knewTutorial) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            Button goButton = new Button(this);
            goButton.setBackgroundResource(R.drawable.layout_button_bg);
            goButton.setTextColor(Color.parseColor(COLOR_WHITE));
            goButton.setWidth(350);
            goButton.setText(GO);

            new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(R.id.algorithmKeyArea, this))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .replaceEndButton(goButton)
                    .blockAllTouches()
                    .setShowcaseDrawer(new ShowcaseViewRectangle(getResources(), size.x, 180, true))
                    .setContentTitle(CREATE_ENCRYPT_ALGORITHM_TEXT)
                    .setContentText(KEY_INFO_TEXT)
                    .build();

            SharedPreferences.Editor editor = edcSettings.edit();
            editor.putBoolean(UserSettings.algorithmTutorialName, true);
            editor.apply();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void saveAlgorithmStep(View view) {

        if (currentSteps < MAX_CRYPT_STEPS) {
            final EditText algorithmResultEdit = findViewById(R.id.algorithmResult);
            String algorithmKeyText = NONE_KEY_TEXT;

            if (!algorithmMapKey.equals(BASE64) && !algorithmMapKey.equals(REVERSE)) {
                final EditText algorithmKey = findViewById(R.id.algorithmKeyArea);
                algorithmKeyText = algorithmKey.getText().toString();
            }
            if (isValidEnglishWord(algorithmKeyText)) {
                try {
                    algorithmMap.put(algorithmMap.size() + UNDERSCORE + algorithmMapKey, algorithmKeyText);
                    algorithmResultEdit.setText(encryptAppValues(new JSONObject(algorithmMap).toString()));
                    currentSteps++;
                } catch (IllegalAlgorithmException e) {
                    Toast.makeText(this, READ_CODE_ERROR, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, CAN_NOT_WRITE_VALUES, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, ERROR_STEPS, Toast.LENGTH_LONG).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void applyAlgorithm(View view) {

        final EditText algorithmResultEdit = findViewById(R.id.algorithmResult);
        String algorithmResultText = algorithmResultEdit.getText().toString();

        if (isValidBase64String(algorithmResultText)) {
            try {
                algorithmResultText = decryptAppValues(algorithmResultText);
                SharedPreferences.Editor editor = edcSettings.edit();

                editor.putString(UserSettings.saveAlgorithmName, algorithmResultText);
                UserSettings.saveAlgorithm = algorithmResultText;

                editor.apply();

                Intent intent = new Intent(CreateAlgorithmActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(this, SETTINGS_SAVED, Toast.LENGTH_LONG).show();
            } catch (IllegalAlgorithmException e) {
                Toast.makeText(this, READ_CODE_ERROR, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, READ_CODE_ERROR, Toast.LENGTH_LONG).show();
        }
    }

    public void shareAlgorithm(View view) {
        EditText algorithmCode = findViewById(R.id.algorithmResult);
        String algorithmCodeText = algorithmCode.getText().toString();
        Intent sendIntent = new Intent();

        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, algorithmCodeText);
        sendIntent.setType("text/plain");
        startActivity(Intent.createChooser(sendIntent,SHARE_ALGORITHM_TITLE));
    }
}
