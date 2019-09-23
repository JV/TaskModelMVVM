package com.example.taskmodelmvvm.tasks;

import android.os.AsyncTask;
import android.util.Log;

import com.example.taskmodelmvvm.persistance.ElementModel;

import java.util.List;

public class WorkTask extends AsyncTask<Object, Object, Object> {

    private List<ElementModel> elementModels;

    public WorkTask() {

    }

    @Override
    protected Void doInBackground(Object... objects) {

        Log.d("WorkTask", "doInBackground: WORKING");
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {

    }
}
