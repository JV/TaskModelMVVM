package com.example.taskmodelmvvm.threads;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.taskmodelmvvm.MainActivity;
import com.example.taskmodelmvvm.entity.ElementModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class UiHandlerThread extends HandlerThread {

    private static final String TAG = "HandlerThread";
    private static final int UI_TASK = 1;
    private Handler handler;
    boolean firstPosition;
    boolean secondPosition;
    private Set<String> differentTagsLimit = new LinkedHashSet<>();
    private int tagPosition = 0;
    private List<String> allTags;
    private int limit;
    private List<List<Integer>> coordinates = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private List<ElementModel> elementModels = new ArrayList<>();
    private Context context;

    public UiHandlerThread(Context context) {
        super("HandlerThread", Process.THREAD_PRIORITY_BACKGROUND);

        Log.d(TAG, "UiHandlerThread: constructor");
        this.context = context;


    }

    @SuppressLint("HandlerLeak")
    @Override
    protected void onLooperPrepared() {



        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {


                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                elementModels = (List<ElementModel>) msg.getData().getSerializable("rawData");

                switch (msg.what) {
                    case UI_TASK:
                        performLongTask();
                        Log.d(TAG, "handleMessage:  long task done if this under coords");
                        break;
                }
            }
        };
        Log.d(TAG, "onLooperPrepared:  creating");
    }

    public Handler getHandler() {
        return handler;
    }



    private void performLongTask() {

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
        editor.apply();
    }

    private void saveDifferentTags() {

        for (ElementModel elementModel : elementModels) {
            differentTagsLimit.add(elementModel.getTag());
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(differentTagsLimit);
        editor.putString("DifferentTagList", json);
        editor.apply();
    }
}