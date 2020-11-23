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
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ru.limedev.endecrypter.core.UserSettings;
import ru.limedev.endecrypter.core.commands.users.VKUsersCommand;
import ru.limedev.endecrypter.core.impl.CircleTransform;
import ru.limedev.endecrypter.core.impl.ShowcaseViewRectangle;
import ru.limedev.endecrypter.core.models.VKUser;
import ru.limedev.endecrypter.core.commands.friends.VKFriendsCommand;

import static ru.limedev.endecrypter.core.Constants.*;
import static ru.limedev.endecrypter.core.UserSettings.*;
import static ru.limedev.endecrypter.core.utilities.UtilitiesMain.getKey;
import static ru.limedev.endecrypter.core.utilities.UtilitiesMain.isNetworkAvailable;

@SuppressLint("Registered")
public class VKProfileActivity extends AppCompatActivity {

    private Map<Integer, String> friendsMap;
    private List<String> friendsList;
    private List<String> listItems;
    private ArrayAdapter<String> adapter;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vk_profile);
        SharedPreferences edcSettings = getSharedPreferences(appPreferences, Context.MODE_PRIVATE);

        final ListView listView = findViewById(R.id.friendsListView);
        final EditText editText = findViewById(R.id.editTextFind);

        listView.setOnTouchListener(new ListView.OnTouchListener() {
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

        friendsMap = new HashMap<>();
        VKUser me = new VKUser();

        friendsMap.put(me.getId(), ME);

        findViewById(R.id.progressBar).setVisibility(View.VISIBLE);
        VK.execute(new VKFriendsCommand(0, "hints", appFriendsCount),
                new VKApiCallback<List<? extends VKUser>>() {

            @Override
            public void success(List<? extends VKUser> result) {
                if (!result.isEmpty()) {
                    for (Object userObject : result) {
                        VKUser user = (VKUser) userObject;
                        friendsMap.put(user.getId(), user.getFirstName() + " " + user.getLastName());
                    }
                    finishProcess();
                }
            }

            @Override
            public void fail(@NotNull Exception error) {
                Log.e("ContentValues", error.toString());
                finishProcess();
            }

            void finishProcess() {
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                addUsersInList(listView, editText, friendsMap);
            }
        });

        VK.execute(new VKUsersCommand(), new VKApiCallback<List<? extends VKUser>>() {

            @Override
            public void success(List<? extends VKUser> result) {
                if (!result.isEmpty()) {
                    VKUser user = result.get(0);
                    String userPhoto = user.getPhoto200();

                    if (!TextUtils.isEmpty(userPhoto)) {
                        ImageView avatar = findViewById(R.id.profileImageItem);
                        Picasso.get()
                                .load(userPhoto)
                                .transform(new CircleTransform())
                                .error(R.drawable.ic_account_circle)
                                .into(avatar);
                    }

                    TextView myName = findViewById(R.id.profileName);
                    myName.setText(user.getFirstName() + " " + user.getLastName());

                    TextView myOnline = findViewById(R.id.profileOnline);
                    if (user.getOnline() == 1) {
                        myOnline.setText(R.string.online);
                    }

                    TextView myStatus = findViewById(R.id.profileStatus);
                    myStatus.setText(user.getStatus());
                }
            }

            @Override
            public void fail(@NotNull Exception error) {
                Log.e("ContentValues", error.toString());
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.profile_bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        boolean knewTutorial = edcSettings.getBoolean(UserSettings.wallTutorialName, DEFAULT_WALL_TUTORIAL);

        if (!knewTutorial) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);

            Button closeButton = new Button(this);
            closeButton.setBackgroundResource(R.drawable.layout_button_bg);
            closeButton.setTextColor(Color.parseColor(COLOR_WHITE));
            closeButton.setText(CLOSE);

            new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(R.id.editTextFind, this))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .replaceEndButton(closeButton)
                    .blockAllTouches()
                    .setShowcaseDrawer(new ShowcaseViewRectangle(getResources(), size.x, 180, true))
                    .setContentTitle(WALL_ENCRYPT_TEXT)
                    .setContentText(WALL_ENCRYPT_INFO_TEXT)
                    .build();

            SharedPreferences.Editor editor = edcSettings.edit();
            editor.putBoolean(UserSettings.wallTutorialName, true);
            editor.apply();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.profile_action_menu, menu);
        return true;
    }

    private BottomNavigationView.OnNavigationItemSelectedListener
            navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.redactor_page:
                    intent = new Intent(VKProfileActivity.this, MainActivity.class);
                    break;
                case R.id.dialogs_page:
                    if (checkNetwork()) {
                        intent = new Intent(VKProfileActivity.this, VKDialogsActivity.class);
                    } else {
                        intent = new Intent(VKProfileActivity.this, NetworkActivity.class);
                    }
                    break;
            }
            if (intent == null) {
                return false;
            }
            startActivity(intent);
            finish();
            return true;
        }
    };

    private boolean checkNetwork() {
        return isNetworkAvailable(this);
    }

    public void searchItem(String textToSearch) {
        for (String item : friendsList) {
            if (!item.toLowerCase().contains(textToSearch.toLowerCase())) {
                listItems.remove(item);
            }
        }
        adapter.notifyDataSetChanged();
    }

    private void addUsersInList(final ListView listView, final EditText editText,
                                final Map<Integer, String> friendsMap) {

        final Context context = this;
        friendsList = new ArrayList<>(friendsMap.values());
        listItems = new ArrayList<>(friendsList);
        adapter = new ArrayAdapter<>(this, R.layout.friends_list_item, R.id.textItem, listItems);

        listView.setAdapter(adapter);
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().isEmpty()) {
                    addUsersInList(listView, editText, friendsMap);
                } else {
                    searchItem(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (checkNetwork()) {
                    if (!friendsMap.isEmpty()) {
                        String selectedFromList = listView.getItemAtPosition(position).toString();
                        Intent intent = new Intent(context, VKFriendsWallActivity.class);
                        intent.putExtra(TEXT_TO_WALL, getKey(friendsMap, selectedFromList));
                        context.startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(VKProfileActivity.this, NetworkActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }

    public void openProfileToolsWindow(MenuItem item) {
        Intent intent = new Intent(VKProfileActivity.this, VKProfileToolsActivity.class);
        startActivity(intent);
    }
}
