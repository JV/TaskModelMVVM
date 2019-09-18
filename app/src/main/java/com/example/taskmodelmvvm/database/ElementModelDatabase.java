package com.example.taskmodelmvvm.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.taskmodelmvvm.dao.ElementModelDao;
import com.example.taskmodelmvvm.entity.ElementModel;

@Database(entities = ElementModel.class, version = 1)
public abstract class ElementModelDatabase extends RoomDatabase {

    private static ElementModelDatabase instance;

    public abstract ElementModelDao elementModelDao();

    public static synchronized ElementModelDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ElementModelDatabase.class, "elementModelDatabase")
                    .fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;
    }

    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDbAsyncTask(instance).execute();
        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private ElementModelDao elementModelDao;

        private PopulateDbAsyncTask(ElementModelDatabase database) {
            elementModelDao = database.elementModelDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            int i = 0;
            while (i < 24) {
                String tag;

                if (i % 2 != 0) {
                    tag = "1";
                } else {
                    tag = "" + i % 2;
                }
                elementModelDao.insert(new ElementModel("Ele" + i, (long)i, (long)i + i, tag, i));
                i++;
            }
            return null;
        }
    }
}
