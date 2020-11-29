package ru.limedev.endecrypter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.vk.api.sdk.VK;
import com.vk.api.sdk.VKApiCallback;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ru.limedev.endecrypter.core.adapters.LastMessagesAdapter;
import ru.limedev.endecrypter.core.commands.messages.VKLastMessagesCommand;
import ru.limedev.endecrypter.core.commands.messages.VKLongPollHistory;
import ru.limedev.endecrypter.core.comparators.LastMessagesComparator;
import ru.limedev.endecrypter.core.models.VKMessage;
import ru.limedev.endecrypter.core.models.VKUser;

import static ru.limedev.endecrypter.core.Constants.*;
import static ru.limedev.endecrypter.core.UserSettings.appDialogsCount;
import static ru.limedev.endecrypter.core.UserSettings.appSwitchTurboVersion;
import static ru.limedev.endecrypter.core.pojo.VKPollServer.*;
import static ru.limedev.endecrypter.core.utilities.UtilitiesMain.isNetworkAvailable;

@SuppressLint("Registered")
public class VKDialogsActivity extends AppCompatActivity {

    private ListView listView;
    private LastMessagesAdapter dialogsListAdapter;

    private Runnable vkDataRunnable;
    private Handler handler;
    private Map<VKMessage, VKUser> pollMessagesMap;

    private static boolean stopHandler = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vk_dialogs);

        listView = findViewById(R.id.dialogsListView);

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

        pollMessagesMap = new TreeMap<>(new LastMessagesComparator());

        findViewById(R.id.dialogsProgressBar).setVisibility(View.VISIBLE);
        VK.execute(new VKLastMessagesCommand(0, appDialogsCount, "all"), new VKApiCallback<Map<? extends VKMessage, ? extends VKUser>>() {

            @Override
            public void success(Map<? extends VKMessage, ? extends VKUser> result) {
                if (!result.isEmpty()) {
                    pollMessagesMap.putAll(result);
                    finishProcess();
                }
            }

            @Override
            public void fail(@NotNull Exception error) {
                Log.e("ContentValues", error.toString());
                finishProcess();
            }

            void finishProcess() {
                findViewById(R.id.dialogsProgressBar).setVisibility(View.GONE);
                addLastMessagesInList(listView);
            }
        });

        BottomNavigationView bottomNav = findViewById(R.id.dialogs_bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        updateServerPoll();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener
            navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Intent intent = null;
            switch (item.getItemId()) {
                case R.id.redactor_page:
                    intent = new Intent(VKDialogsActivity.this, MainActivity.class);
                    break;
                case R.id.profile_page:
                    if (checkNetwork()) {
                        intent = new Intent(VKDialogsActivity.this, VKProfileActivity.class);
                    } else {
                        intent = new Intent(VKDialogsActivity.this, NetworkActivity.class);
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

    private void initLongPollTimer() {
        handler = new Handler();
        final Context context = this;
        vkDataRunnable = new Runnable() {
            public void run() {
                if (isNetworkAvailable(context) && !stopHandler) {
                    if (pollMessagesMap != null && !pollMessagesMap.isEmpty()) {
                        updateMessagesMap();
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

    private void updateMessagesMap() {

        final List<Integer> userIdList = new ArrayList<>();

        for (Map.Entry<VKMessage, VKUser> entry: pollMessagesMap.entrySet()) {
            userIdList.add(entry.getValue().getId());
        }

        VK.execute(new VKLongPollHistory(longPollServer.getTs(), 3),
                new VKApiCallback<Map<? extends VKMessage, ? extends VKUser>>() {

            @Override
            public void success(Map<? extends VKMessage, ? extends VKUser> result) {
                if (!result.isEmpty()) {
                    for (Map.Entry<? extends VKMessage, ? extends VKUser> entry: result.entrySet()) {
                        VKUser vkUserFromPoll = entry.getValue();
                        if (userIdList.contains(vkUserFromPoll.getId())) {
                            pollMessagesMap.values().remove(vkUserFromPoll);
                        }
                        pollMessagesMap.put(entry.getKey(), entry.getValue());
                    }
                    complexAdapterUpload();
                }
            }

            @Override
            public void fail(@NotNull Exception error) {
                Log.e("ContentValues", error.toString());
            }
        });
    }

    private void complexAdapterUpload() {
        View someView = listView.getChildAt(0);

        int topOffset = (someView == null) ? 0 : someView.getTop();
        int lastViewedPosition = listView.getFirstVisiblePosition();

        dialogsListAdapter = new LastMessagesAdapter(this, pollMessagesMap);
        listView.setAdapter(dialogsListAdapter);
        listView.setSelectionFromTop(lastViewedPosition, topOffset);
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

    private void addLastMessagesInList(final ListView listView) {
        dialogsListAdapter = new LastMessagesAdapter(this, pollMessagesMap);
        listView.setAdapter(dialogsListAdapter);

        final Context context = this;

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                if (checkNetwork()) {
                    VKMessage selectedFromList = (VKMessage) listView.getItemAtPosition(position);
                    Intent intent = new Intent(context, VKMyDialogActivity.class);
                    intent.putExtra(HISTORY_FOR_USER, pollMessagesMap.get(selectedFromList));
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(VKDialogsActivity.this, NetworkActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        });

        initLongPollTimer();
    }
}
