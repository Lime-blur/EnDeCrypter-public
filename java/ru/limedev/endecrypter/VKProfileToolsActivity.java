package ru.limedev.endecrypter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.vk.api.sdk.VK;

import ru.limedev.endecrypter.core.UserSettings;

import static ru.limedev.endecrypter.core.Constants.CAN_NOT_WRITE_VALUES;
import static ru.limedev.endecrypter.core.Constants.SETTINGS_SAVED;

@SuppressLint("Registered")
public class VKProfileToolsActivity extends AppCompatActivity {

    private SharedPreferences edcSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile_tools);

        edcSettings = getSharedPreferences(UserSettings.appPreferences, Context.MODE_PRIVATE);

        EditText friendsCount = findViewById(R.id.friendsCount);
        EditText postsCount = findViewById(R.id.postsCount);
        EditText dialogsCount = findViewById(R.id.dialogsCount);
        EditText myDialogMessagesCount = findViewById(R.id.myDialogMessagesCount);
        Switch switchTurboVersion = findViewById(R.id.switchTurboVersion);

        friendsCount.setText(String.valueOf(UserSettings.appFriendsCount));
        postsCount.setText(String.valueOf(UserSettings.appPostsCount));
        dialogsCount.setText(String.valueOf(UserSettings.appDialogsCount));
        myDialogMessagesCount.setText(String.valueOf(UserSettings.appMyDialogMessagesCount));
        switchTurboVersion.setChecked(UserSettings.appSwitchTurboVersion);
    }

    public void saveProfileSettings(View view) {
        EditText friendsCount = findViewById(R.id.friendsCount);
        String friendsCountText = friendsCount.getText().toString();
        EditText postsCount = findViewById(R.id.postsCount);
        String postsCountText = postsCount.getText().toString();
        EditText dialogsCount = findViewById(R.id.dialogsCount);
        String dialogsCountText = dialogsCount.getText().toString();
        EditText myDialogMessagesCount = findViewById(R.id.myDialogMessagesCount);
        String myDialogMessagesCountText = myDialogMessagesCount.getText().toString();
        Switch switchTurboVersion = findViewById(R.id.switchTurboVersion);
        Boolean isSwitchTurboVersion = switchTurboVersion.isChecked();

        try {
            int friendsCountNumber = Integer.parseInt(friendsCountText);
            int postsCountNumber = Integer.parseInt(postsCountText);
            int dialogsCountNumber = Integer.parseInt(dialogsCountText);
            int myDialogMessagesCountNumber = Integer.parseInt(myDialogMessagesCountText);
            SharedPreferences.Editor editor = edcSettings.edit();

            editor.putInt(UserSettings.appFriendsCountName, friendsCountNumber);
            editor.putInt(UserSettings.appPostsCountName, postsCountNumber);
            editor.putInt(UserSettings.appDialogsCountName, dialogsCountNumber);
            editor.putInt(UserSettings.appMyDialogMessagesCountName, myDialogMessagesCountNumber);
            editor.putBoolean(UserSettings.appSwitchTurboVersionName, isSwitchTurboVersion);
            UserSettings.appFriendsCount = friendsCountNumber;
            UserSettings.appPostsCount = postsCountNumber;
            UserSettings.appDialogsCount = dialogsCountNumber;
            UserSettings.appMyDialogMessagesCount = myDialogMessagesCountNumber;
            UserSettings.appSwitchTurboVersion = isSwitchTurboVersion;

            editor.apply();

            Intent intent = new Intent(VKProfileToolsActivity.this, VKProfileActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, SETTINGS_SAVED, Toast.LENGTH_LONG).show();
        } catch (NumberFormatException e) {
            Toast.makeText(this, CAN_NOT_WRITE_VALUES, Toast.LENGTH_LONG).show();
        }
    }

    public void profileLogout(View view) {
        if (VK.isLoggedIn()) {
            VK.logout();
            Intent intent = new Intent(VKProfileToolsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
