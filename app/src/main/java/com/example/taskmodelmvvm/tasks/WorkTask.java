package com.example.taskmodelmvvm.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import com.example.taskmodelmvvm.persistance.ElementModel;
import com.example.taskmodelmvvm.viewmodel.ElementModelRwAdapter;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class WorkTask extends AsyncTask<Object, Object, Object> {

    private List<ElementModel> elementModels;
    private SharedPreferences sharedPreferences;
    private Set<String> differentTagsLimit = new LinkedHashSet<>();
    private int tagPosition = 0;
    private List<String> allTags;
    private int limit;
    boolean firstPosition;
    boolean secondPosition;
    private List<List<Integer>> coordinates = new ArrayList<>();
    private ElementModelRwAdapter adapter;


    public WorkTask(Context context, List<ElementModel> elementModels, ElementModelRwAdapter adapter) {
        this.elementModels = elementModels;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        this.adapter = adapter;
    }

    @Override
    protected Void doInBackground(Object... objects) {

        performLongTask(elementModels);
        Log.d("WorkTask", "doInBackground: WORKING" + elementModels.toString());
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

        adapter.notifyTaskDone();

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
        Log.d("WorkTaskCoords", "calculateCoordinates: " + coordinates.toString());
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