package com.example.taskmodelmvvm.persistance;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.Collections;
import java.util.List;


public class ElementModelRepository {

    private static ElementModelRepository instance;
    private MutableLiveData<List<ElementModel>> mutableLiveData;
    private LiveData<List<ElementModel>> allElements;
    private LiveData<List<ElementModel>> allElementsMoved;
    private ElementModelDao elementModelDao;
    private List<ElementModel> elementModels;

    public ElementModelRepository(Context context) {

        ElementModelDatabase elementModelDatabase = ElementModelDatabase.getInstance(context);
        elementModelDao = elementModelDatabase.elementModelDao();
        allElements = elementModelDao.getAllElements();
        allElementsMoved = elementModelDao.getAllElementsMoved();
        elementModels = elementModelDao.getAllElementsList();
        mutableLiveData = new MutableLiveData<>();

    }


    public static ElementModelRepository getInstance(Context context) {
        if (instance == null) {
            instance = new ElementModelRepository(context);
        }
        return instance;
    }

    public LiveData<List<ElementModel>> getMutableElements() {
        return mutableLiveData;
    }

    public List<ElementModel> getAllElementsList() {
        return elementModels;
    }

    public void insert(ElementModel elementModel) {
        new InsertElementAsyncTask(elementModelDao).execute(elementModel);
    }

    public void update(ElementModel elementModel) {
        new UpdateElementAsyncTask(elementModelDao).execute(elementModel);
    }

    public void delete(ElementModel elementModel) {
        new DeleteElementAsyncTask(elementModelDao).execute(elementModel);
    }

    public void deleteAllElements() {
        new DeleteAllElementsAsyncTask(elementModelDao).execute();
    }

    public LiveData<List<ElementModel>> getAllElements() {
        return allElements;
    }

    public LiveData<List<ElementModel>> getAllElementsMoved() {
        return allElementsMoved;
    }

    public void move(int from, int to) {
        new MoveAsyncTask(elementModelDao).execute(from, to);
    }


    private static class MoveAsyncTask extends AsyncTask<Integer, Void, Void> {
        private ElementModelDao elementModelDao;

        private MoveAsyncTask(ElementModelDao elementModelDao) {
            this.elementModelDao = elementModelDao;

        }

        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        protected Void doInBackground(final Integer... params) {

            int from = params[0];
            int to = params[1];

            if (from < to) {
                for (int i = from; i < to; i++) {
                    Collections.swap(elementModelDao.getAllElementsList(), i, i + 1);

                    int order1 = elementModelDao.getAllElementsList().get(i).getCurrentPosition();
                    int order2 = elementModelDao.getAllElementsList().get(i + 1).getCurrentPosition();
                    elementModelDao.getAllElementsList().get(i).setCurrentPosition(order2);
                    elementModelDao.getAllElementsList().get(i + 1).setCurrentPosition(order1);

                    elementModelDao.update(elementModelDao.getAllElementsList().get(i));
                    elementModelDao.update(elementModelDao.getAllElementsList().get(i + 1));

                    Log.d("BCGMOVE", "doInBackground: " + elementModelDao.getAllElementsList().toString());

                }
            } else {
                for (int i = from; i > to; i--) {
                    Collections.swap(elementModelDao.getAllElementsList(), i, i - 1);

                    int order1 = elementModelDao.getAllElementsList().get(i).getCurrentPosition();
                    int order2 = elementModelDao.getAllElementsList().get(i - 1).getCurrentPosition();
                    elementModelDao.getAllElementsList().get(i).setCurrentPosition(order2);
                    elementModelDao.getAllElementsList().get(i - 1).setCurrentPosition(order1);

                    elementModelDao.update(elementModelDao.getAllElementsList().get(i));
                    elementModelDao.update(elementModelDao.getAllElementsList().get(i - 1));

                    Log.d("BCGMOVE", "doInBackground: " + elementModelDao.getAllElementsList().toString());
                }
            }

            return null;
        }
    }

    private static class InsertElementAsyncTask extends AsyncTask<ElementModel, Void, Void> {
        private ElementModelDao elementModelDao;

        private InsertElementAsyncTask(ElementModelDao elementModelDao) {
            this.elementModelDao = elementModelDao;
        }

        @Override
        protected Void doInBackground(ElementModel... elementModels) {
            elementModelDao.insert(elementModels[0]);
            return null;
        }
    }

    private static class UpdateElementAsyncTask extends AsyncTask<ElementModel, Void, Void> {
        private ElementModelDao elementModelDao;

        private UpdateElementAsyncTask(ElementModelDao elementModelDao) {
            this.elementModelDao = elementModelDao;
        }

        @Override
        protected Void doInBackground(ElementModel... elementModels) {
            elementModelDao.update(elementModels[0]);
            return null;
        }
    }

    private static class DeleteElementAsyncTask extends AsyncTask<ElementModel, Void, Void> {
        private ElementModelDao elementModelDao;

        private DeleteElementAsyncTask(ElementModelDao elementModelDao) {
            this.elementModelDao = elementModelDao;
        }

        @Override
        protected Void doInBackground(ElementModel... elementModels) {
            elementModelDao.delete(elementModels[0]);
            return null;
        }
    }

    private static class DeleteAllElementsAsyncTask extends AsyncTask<Void, Void, Void> {
        private ElementModelDao elementModelDao;

        private DeleteAllElementsAsyncTask(ElementModelDao elementModelDao) {
            this.elementModelDao = elementModelDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            elementModelDao.deleteAllElements();
            return null;
        }
    }
}
