package ru.limedev.endecrypter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.Layout;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.gson.JsonSyntaxException;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import ru.limedev.endecrypter.core.UserSettings;
import ru.limedev.endecrypter.core.adapters.MyDialogAdapter;
import ru.limedev.endecrypter.core.commands.messages.*;
import ru.limedev.endecrypter.core.exceptions.IllegalAlgorithmException;
import ru.limedev.endecrypter.core.impl.ShowcaseViewCircle;
import ru.limedev.endecrypter.core.models.VKMessage;
import ru.limedev.endecrypter.core.models.VKUser;

import static ru.limedev.endecrypter.core.Constants.*;
import static ru.limedev.endecrypter.core.UserSettings.appMyDialogMessagesCount;
import static ru.limedev.endecrypter.core.UserSettings.appPreferences;
import static ru.limedev.endecrypter.core.UserSettings.appSwitchTurboVersion;
import static ru.limedev.endecrypter.core.pojo.VKPollServer.*;
import static ru.limedev.endecrypter.core.pojo.VKPollServer.updateServerPoll;
import static ru.limedev.endecrypter.core.utilities.UtilitiesCrypto.useDecryptAlgorithm;
import static ru.limedev.endecrypter.core.utilities.UtilitiesCrypto.useEncryptAlgorithm;
import static ru.limedev.endecrypter.core.utilities.UtilitiesMain.isNetworkAvailable;

@SuppressLint("Registered")
public class VKMyDialogActivity extends AppCompatActivity {

    private int companionId;
    private ListView listView;
    private ArrayAdapter<VKMessage> myDialogAdapter;

    private Runnable vkDataRunnable;
    private Handler handler;
    private List<VKMessage> pollDialogMessagesList;

    private static boolean stopHandler = false;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_vk_my_dialog);
        SharedPreferences edcSettings = getSharedPreferences(appPreferences, Context.MODE_PRIVATE);

        listView = findViewById(R.id.myDialogListView);
        final VKUser interlocutor = getIntent().getParcelableExtra(HISTORY_FOR_USER);
        final Context context = this;

        int userId = 0;

        if (interlocutor != null) {
            userId = interlocutor.getId();
        }

        findViewById(R.id.myDialogProgressBar).setVisibility(View.VISIBLE);
        VK.execute(new VKHistoryCommand(0, appMyDialogMessagesCount, userId, 0),
                new VKApiCallback<List<? extends VKMessage>>() {

            @Override
            public void success(List<? extends VKMessage> result) {
                if (!result.isEmpty()) {
                    addMessagesInList(result);
                    finishProcess();
                }
            }

            @Override
            public void fail(@NotNull Exception error) {
                Log.e("ContentValues", error.toString());
                finishProcess();
            }

            void finishProcess() {
                findViewById(R.id.myDialogProgressBar).setVisibility(View.GONE);
            }
        });

        final EditText textBox = findViewById(R.id.sendMessageEdit);
        final ImageButton imageButton = findViewById(R.id.imageButtonSend);

        textBox.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                if (s.length() != 0) {
                    imageButton.setVisibility(View.VISIBLE);
                } else {
                    imageButton.setVisibility(View.GONE);
                }
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                VKMessage selectedFromList = (VKMessage) listView.getItemAtPosition(position);
                String selectedMessageText = selectedFromList.getText();

                if (!selectedMessageText.trim().isEmpty()) {
                    try {
                        String decryptedText = useDecryptAlgorithm(selectedMessageText);
                        MyDialogAdapter adapter = (MyDialogAdapter) parent.getAdapter();
                        Objects.requireNonNull(adapter.getItem(position)).setText(decryptedText);
                        adapter.notifyDataSetChanged();
                    } catch (JsonSyntaxException | IllegalAlgorithmException e) {
                        Toast.makeText(context, ERROR_CRYPT, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(context, EMPTY_TEXT_FIELD, Toast.LENGTH_LONG).show();
                }
            }
        });

        boolean knewTutorial = edcSettings.getBoolean(UserSettings.myDialogTutorialName, DEFAULT_MY_DIALOG_TUTORIAL);

        if (!knewTutorial) {
            Button closeButton = new Button(this);
            closeButton.setBackgroundResource(R.drawable.layout_button_bg);
            closeButton.setTextColor(Color.parseColor(COLOR_WHITE));
            closeButton.setText(CLOSE);

            ShowcaseView showcaseView = new ShowcaseView.Builder(this)
                    .setTarget(new ViewTarget(R.id.imageButtonEncrypt, this))
                    .setStyle(R.style.CustomShowcaseTheme)
                    .setShowcaseDrawer(new ShowcaseViewCircle(getResources(), 130f))
                    .setContentTitle(MESSAGES_ENCRYPT_TEXT)
                    .setContentText(MESSAGES_ENCRYPT_INFO_TEXT)
                    .replaceEndButton(closeButton)
                    .blockAllTouches()
                    .build();
            showcaseView.setTitleTextAlignment(Layout.Alignment.ALIGN_CENTER);
            showcaseView.setDetailTextAlignment(Layout.Alignment.ALIGN_CENTER);
            showcaseView.forceTextPosition(ShowcaseView.ABOVE_SHOWCASE);

            SharedPreferences.Editor editor = edcSettings.edit();
            editor.putBoolean(UserSettings.myDialogTutorialName, true);
            editor.apply();
        }
    }

    private void initLongPollTimer() {
        handler = new Handler();
        final Context context = this;
        vkDataRunnable = new Runnable() {
            public void run() {
                if (isNetworkAvailable(context) && !stopHandler) {
                    if (pollDialogMessagesList != null && !pollDialogMessagesList.isEmpty()) {
                        updateDialogMessagesList();
                    }
                    if (pollIterator++ >= MAX_CALLS_BEFORE_UPDATE) {
                        updateServerPoll();
                        pollIterator = 0;
                    }
                }
                handler.postDelayed(this, appSwitchTurboVersion ? VK_CALL_DELAY_TURBO : VK_CALL_DELAY);
            }
        };
        handler.postDelayed(vkDataRunnable, VK_RUNNABLE_DELAY);
    }

    private void updateDialogMessagesList() {

        final List<Integer> messageIdList = new ArrayList<>();
        final int listPeerId = pollDialogMessagesList.get(0).getPeerId();

        for (VKMessage vkMessage : pollDialogMessagesList) {
            messageIdList.add(vkMessage.getId());
        }

        VK.execute(new VKLongPollHistory(longPollServer.getTs(), 3),
                new VKApiCallback<Map<? extends VKMessage, ? extends VKUser>>() {

            @Override
            public void success(Map<? extends VKMessage, ? extends VKUser> result) {
                if (!result.isEmpty()) {
                    boolean goDown = false;
                    for (Map.Entry<? extends VKMessage, ? extends VKUser> entry: result.entrySet()) {
                        VKMessage vkMessageFromPoll = entry.getKey();
                        if (!messageIdList.contains(vkMessageFromPoll.getId())
                                && vkMessageFromPoll.getPeerId() == listPeerId) {
                            pollDialogMessagesList.add(vkMessageFromPoll);
                            goDown = true;
                        }
                    }
                    if (goDown) {
                        myDialogAdapter.notifyDataSetChanged();
                        listView.setSelection(myDialogAdapter.getCount() - 1);
                    }
                }
            }

            @Override
            public void fail(@NotNull Exception error) {
                Log.e("ContentValues", error.toString());
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        vkDataRunnable = null;
        handler = null;
        stopHandler = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopHandler = true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        stopHandler = false;
    }

    private void addMessagesInList(final List<? extends VKMessage> messageList) {

        if (!messageList.isEmpty()) {
            pollDialogMessagesList = new ArrayList<>(messageList);
            companionId = messageList.get(0).getPeerId();
        }

        myDialogAdapter = new MyDialogAdapter(this, pollDialogMessagesList);

        listView.setAdapter(myDialogAdapter);
        listView.setSelection(myDialogAdapter.getCount() - 1);
        initLongPollTimer();
    }

    public void sendMessage(View view) {

        final EditText textBox = findViewById(R.id.sendMessageEdit);
        String data = textBox.getText().toString();

        if (isNetworkAvailable(this)) {
            if (data.length() <= 4096) {
                int currentTime = (int) (new Date().getTime() / 1000);
                final Context context = this;
                textBox.getText().clear();

                VK.execute(new VKMessagesSend(companionId, currentTime, companionId, data), new VKApiCallback<Integer>() {

                    @Override
                    public void success(Integer result) {
                    }

                    @Override
                    public void fail(@NotNull Exception error) {
                        Log.e("ContentValues", error.toString());
                        if (error.getMessage() != null) {
                            String[] stringArray = error.getMessage().split(SPLITTER);
                            Toast.makeText(context, stringArray[0].trim(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
            } else {
                Toast.makeText(this, ERROR_MESSAGE_LENGTH, Toast.LENGTH_LONG).show();
            }
        } else {
            Intent intent = new Intent(VKMyDialogActivity.this, NetworkActivity.class);
            startActivity(intent);
            finish();
        }
    }

    public void encryptMessage(View view) {
        final EditText textBox = findViewById(R.id.sendMessageEdit);
        String data = textBox.getText().toString();

        if (data.length() <= MAX_DIALOG_ENCRYPT_LENGTH) {
            if (!data.trim().isEmpty()) {
                try {
                    String encryptedText = useEncryptAlgorithm(data);
                    textBox.setText(encryptedText);
                } catch (JsonSyntaxException | IllegalAlgorithmException e) {
                    Toast.makeText(this, ERROR_CRYPT, Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, EMPTY_TEXT_FIELD, Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, ERROR_DIALOG_ENCRYPT_LENGTH, Toast.LENGTH_LONG).show();
        }
    }
}
