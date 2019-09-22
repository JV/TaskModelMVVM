package com.example.taskmodelmvvm.persistance;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.time.Instant;

@Database(entities = ElementModel.class, version = 1)
public abstract class ElementModelDatabase extends RoomDatabase {

    public static final String DATABASE_NAME = "elementModelDatabase.db";
    private static ElementModelDatabase instance;

    public abstract ElementModelDao elementModelDao();

    public static synchronized ElementModelDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    ElementModelDatabase.class, DATABASE_NAME)
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
            // call data?

        }
    };

    private static class PopulateDbAsyncTask extends AsyncTask<Void, Void, Void> {
        private ElementModelDao elementModelDao;

        private PopulateDbAsyncTask(ElementModelDatabase database) {
            elementModelDao = database.elementModelDao();
        }


        @RequiresApi(api = Build.VERSION_CODES.O)
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
                String ts = String.valueOf(Instant.now().getNano());
                Log.d("Timestamp test", "doInBackground: " + ts);
                elementModelDao.insert(new ElementModel("Ele" + i, (long) i,
                        (long) i + i, tag, i, ts));
                i++;
            }
            return null;
        }
    }
}
