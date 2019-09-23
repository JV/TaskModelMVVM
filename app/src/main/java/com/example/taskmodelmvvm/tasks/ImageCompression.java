package com.example.taskmodelmvvm.tasks;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class ImageCompression extends AsyncTask<String, Void, String> {

    public AsyncResponse delegate = null;
    private Bitmap bitmap;


    public ImageCompression(AsyncResponse asyncResponse) {
        delegate = asyncResponse; //Assigning call back interfacethrough constructor
    }

    protected String doInBackground(String... filePaths) {

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

        byte[] bytes = outputStream.toByteArray();

        String encodeImage = Base64.encodeToString(bytes, Base64.DEFAULT);

        return encodeImage;
    }

    protected void onPostExecute(Bitmap result) {

        delegate.processFinish(result);


    }
}



interface AsyncResponse {
    void processFinish(Bitmap output);
}
