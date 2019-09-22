package com.example.taskmodelmvvm.persistance;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.taskmodelmvvm.R;

public class AddEditActivity extends AppCompatActivity {

    public static final String EXTRA_NAZIV =
            "com.example.taskmodelmvvm.activities.EXTRA_NAZIV";
    public static final String EXTRA_POCETAK =
            "com.example.taskmodelmvvm.activities.EXTRA_POCETAK";
    public static final String EXTRA_KRAJ =
            "com.example.taskmodelmvvm.activities.EXTRA_KRAJ";
    public static final String EXTRA_TAG =
            "com.example.taskmodelmvvm.activities.EXTRA_TAG";
    public static final String EXTRA_ID =
            "com.example.taskmodelmvvm.activities.EXTRA_ID";
    public static final String EXTRA_CURRENT_POSITION =
            "com.example.taskmodelmvvm.activities.EXTRA_CURRENT_POSITION";
    public static final String EXTRA_TOP_POSITION =
            "com.example.taskmodelmvvm.activities.EXTRA_TOP_POSITION";

    // add timestamp

    EditText etNaziv;
    EditText etPocetak;
    EditText etTag;
    TimePicker timePicker;
    Button btnAddElement;
    Button btnCancelAddElement;
    private Toolbar toolbar;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        etNaziv = findViewById(R.id.etNaziv);
        etPocetak = findViewById(R.id.etPocetak);
        etTag = findViewById(R.id.etTag);
        timePicker = findViewById(R.id.timepicker);
        btnAddElement = findViewById(R.id.btnAddElement);
        btnCancelAddElement = findViewById(R.id.btnCancelAddElement);

        btnAddElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveElement();
            }
        });
        btnCancelAddElement.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        Intent intent = getIntent();
        if(intent.hasExtra(EXTRA_ID) && intent.hasExtra(EXTRA_CURRENT_POSITION)) {
            setTitle("Edit element");
            etNaziv.setText(intent.getStringExtra(EXTRA_NAZIV));
            etPocetak.setText(String.valueOf(intent.getLongExtra(EXTRA_POCETAK, 0)));
            etTag.setText(intent.getStringExtra(EXTRA_TAG));
            long totalTimeGet = intent.getLongExtra(EXTRA_KRAJ, 0);
            long hourSet = totalTimeGet / 60;
            long minuteSet = totalTimeGet % 60;
            timePicker.setHour((int) hourSet);
            timePicker.setMinute((int) minuteSet);
        } else {
            setTitle("Add element");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.save_note:
//                saveElement();
//                return true;
//                default:
//                    return super.onOptionsItemSelected(item);
//        }
//
//    }

    private void saveElement() {

        Intent intent = getIntent();
        int currentPosition = intent.getIntExtra(EXTRA_CURRENT_POSITION, -1);
        int topPosition = intent.getIntExtra("topPosition", -1) +1;

        String naziv = etNaziv.getText().toString();
        Long pocetak = Long.valueOf(etPocetak.getText().toString());
        long totalTime = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            long hour, minute;
            hour = (long) timePicker.getHour();
            minute = (long) timePicker.getMinute();
            totalTime = hour * 60 + minute;
        }
        Long kraj = totalTime;
        String tag = etTag.getText().toString();

        if (etNaziv.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter name", Toast.LENGTH_LONG).show();
            etNaziv.requestFocus();
            return;
        }

        if (etPocetak.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter beginning", Toast.LENGTH_LONG).show();
            etPocetak.requestFocus();
            return;
        }

        if (etTag.getText().toString().trim().isEmpty()) {
            Toast.makeText(getApplicationContext(), "Please enter tag", Toast.LENGTH_LONG).show();
            etTag.requestFocus();
            return;
        }

        Intent data = new Intent();



        data.putExtra(EXTRA_NAZIV, naziv);
        data.putExtra(EXTRA_POCETAK, pocetak);
        data.putExtra(EXTRA_KRAJ, kraj);
        data.putExtra(EXTRA_TAG, tag);
        data.putExtra(EXTRA_CURRENT_POSITION, currentPosition);


        int id = getIntent().getIntExtra(EXTRA_ID, -1);
        if(id != -1) {
            data.putExtra(EXTRA_ID, id);
            data.putExtra(EXTRA_TOP_POSITION, topPosition);
        }

        setResult(RESULT_OK, data);
        finish();

    }
}
