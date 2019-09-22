package com.example.taskmodelmvvm.tasks;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.taskmodelmvvm.persistance.ElementModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ServiceIntent extends IntentService {

    private static final int UI_TASK = 1;
    private UiHandlerThread uiHandlerThread = new UiHandlerThread(this);
    private SharedPreferences sharedPreferences;
    private List<ElementModel> elementModels = new ArrayList<>();
    private Set<String> differentTagsLimit = new LinkedHashSet<>();
    private int tagPosition = 0;
    private List<String> allTags;
    private int limit;
    boolean firstPosition;
    boolean secondPosition;
    private List<List<Integer>> coordinates = new ArrayList<>();

    private PowerManager.WakeLock wakeLock;
    private static final String TAG = "ServiceIntent";
    public static final int STATUS_RUNNING = 0;
    public static final int STATUS_FINISHED = 1;
    public static final int STATUS_ERROR = 2;

    public ServiceIntent() {
        super("ServiceIntent");
        setIntentRedelivery(true);
    }


    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        if (intent.getBooleanExtra("startThread", false)) {
            uiHandlerThread.start();
            Log.d(TAG, "onStartCommand: thread started");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public ServiceIntent(String name) {
        super(name);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        if (intent.getBooleanExtra("startThread", false)) {

            Log.d(TAG, "onHandleIntent: thread started in start command");
        }
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        if (intent.getBooleanExtra("startLong", false)) {

            Log.d(TAG, "onHandleIntent: got instruction to do long task");
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            elementModels = intent.getParcelableArrayListExtra("rawData");

            Log.d(TAG, "onHandleIntent: " + elementModels.toString());

            Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {

                    performLongTask(elementModels);

                    Log.d(TAG, "handleMessage:  long task done if this under coords");
                }
            });

//            Message msg = Message.obtain();
//
//            while (msg == null) {
//                try {
//                    Thread.sleep(2);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                msg = Message.obtain();
//            }
//            msg.what = UI_TASK;
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("rawData", (Serializable) elementModels);
//            msg.setData(bundle);
//            while (uiHandlerThread.getHandler() == null) {
//                try {
//                    Thread.sleep(2);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//            uiHandlerThread.getHandler().sendMessage(msg);


            if (intent.getBooleanExtra("stopThread", false)) {
                uiHandlerThread.quit();
                Log.d(TAG, "onHandleIntent:  thread stopped by intent");
                return;
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "TaskModel: WakeLock");
        wakeLock.acquire(60000);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wakeLock.release();

        Log.d(TAG, "onDestroy: RUN");
    }

    private void performLongTask(List<ElementModel> elementModels) {

        saveDifferentTags();
        prepareElementData();
        calculateCoordinates();

    }

    private void calculateCoordinates() {
        allTags = new ArrayList<>(differentTagsLimit);
        limit = differentTagsLimit.size();

        tagPosition = 0;
        firstPosition = false;
        secondPosition = false;
        coordinates.clear();

        for (int i = 0; i < limit; i++) {

            coordinates.add(new ArrayList<Integer>());
            findFirst(tagPosition);
            if (firstPosition) {

                findLast(tagPosition);
                tagPosition++;
                if (secondPosition) {
                    secondPosition = false;
                }
            }
        }

        Gson gson = new Gson();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        String json2 = gson.toJson(coordinates);
        editor.putString("CoordinatesList", json2);
        editor.commit();
        Log.d(TAG, "calculateCoordinates: " + coordinates.toString());
    }

    private void findLast(int tagPosition) {
        reverseList(elementModels);

        for (int j = 0; j < elementModels.size(); j++) {

            int distanceFromEnd = elementModels.size() - 1 - coordinates.get(tagPosition).get(0);

            String searchTarget = allTags.get(tagPosition);
            secondPosition = elementModels.get(j).getTag().equals(searchTarget);

            if (secondPosition) {

                if (j == distanceFromEnd) {
                    coordinates.get(tagPosition).add(-1);
                    reverseList(elementModels);
                    return;
                } else {
                    int secondPositionC = elementModels.size() - j - 1;
                    coordinates.get(tagPosition).add(secondPositionC);
                    reverseList(elementModels);
                    return;
                }
            }
        }
        if (!secondPosition) {
            coordinates.get(tagPosition).add(-1);
        }
        reverseList(elementModels);
    }

    private void findFirst(int tagPosition) {

        for (int x = 0; x < elementModels.size(); x++) {

            String searchItem = allTags.get(tagPosition);
            firstPosition = elementModels.get(x).getTag().equals(searchItem);
            if (firstPosition) {
                coordinates.get(tagPosition).add(x);
            }
            if (firstPosition) {
                return;
            }
        }
        if (!firstPosition) {
            coordinates.remove(coordinates.get(tagPosition));
        }
    }

    private void reverseList(List<ElementModel> elementModels) {
        for (int i = 0; i < elementModels.size(); i++) {
            elementModels.add(i, elementModels.remove(elementModels.size() - 1));
        }
    }

    private void prepareElementData() {

        if (!sharedPreferences.getBoolean("listMovedAround", false)) {
            Collections.sort(elementModels, new Comparator<ElementModel>() {

                @Override
                public int compare(ElementModel elementModel, ElementModel t1) {
                    return t1.getPocetak() < elementModel.getPocetak() ? -1 : (t1.getPocetak() >
                            elementModel.getPocetak()) ? 1 : 0;
                }
            });
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(elementModels);
        editor.putString("MyObjectsList", json);
        editor.commit();
    }

    private void saveDifferentTags() {

        for (ElementModel elementModel : elementModels) {
            differentTagsLimit.add(elementModel.getTag());
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(differentTagsLimit);
        editor.putString("DifferentTagList", json);
        editor.commit();
    }
}
