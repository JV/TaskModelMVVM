package com.example.taskmodelmvvm.repository;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.example.taskmodelmvvm.dao.ElementModelDao;
import com.example.taskmodelmvvm.database.ElementModelDatabase;
import com.example.taskmodelmvvm.entity.ElementModel;

import java.util.List;

public class ElementModelRepository {

    private ElementModelDao elementModelDao;
    private LiveData<List<ElementModel>> allElements;

    private LiveData<List<ElementModel>> allElementsMoved;


    public ElementModelRepository(Application application) {
        ElementModelDatabase database = ElementModelDatabase.getInstance(application);
        elementModelDao = database.elementModelDao();
        allElements = elementModelDao.getAllElements();
        allElementsMoved = elementModelDao.getAllElementsMoved();
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

            if (from > to) {



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
