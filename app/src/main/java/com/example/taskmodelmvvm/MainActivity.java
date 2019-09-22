package com.example.taskmodelmvvm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.taskmodelmvvm.persistance.AddEditActivity;
import com.example.taskmodelmvvm.persistance.ElementModel;
import com.example.taskmodelmvvm.persistance.ElementModelRepository;
import com.example.taskmodelmvvm.viewmodel.ElementModelRwAdapter;
import com.example.taskmodelmvvm.viewmodel.ElementViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_ELEMENT_REQUEST = 1;
    public static final int EDIT_ELEMENT_REQUEST = 2;

    private ElementViewModel elementViewModel;
    private List<ElementModel> elementModels = new ArrayList<>();
    private float screenHeight;
    private float screenWidth;
    private RecyclerView recyclerViewMain;
    private List<List<Integer>> coordinates = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private FloatingActionButton floatingActionButton;
    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;

    private ElementModelRwAdapter adapter;
    private MainActivity mainActivity = this;
    private Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // move all to fragment to not interfere with main thread
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // do from fragment?
        startThread();


        elementViewModel = ViewModelProviders.of(this).get(ElementViewModel.class);
        startLongTask(elementModels);
        getDisplay();
        //load / call  db / net ?
        subscribeObservers();
        initViews();
        setUpScreen();
        setupListeners();
        doStuffWithImage();
    }

    private void doStuffWithImage() {
        bitmap = BitmapFactory.decodeResource(getResources(),R.drawable.bitmap_to_array);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

                byte [] bytes = outputStream.toByteArray();

                String encodeImage = Base64.encodeToString(bytes, Base64.DEFAULT);

                return encodeImage;
            }

            @Override
            protected void onPostExecute(String s) {
                Toast.makeText(getApplicationContext(), s, Toast.LENGTH_SHORT).show();
            }
        }.execute();
    }

    private void subscribeObservers() {


        if (!sharedPreferences.getBoolean("UserEdited", false)) {
            elementViewModel.getAllElements().observe(this, new Observer<List<ElementModel>>() {
                @Override
                public void onChanged(List<ElementModel> elementModels) {


                    Log.d("Unedited", "onChanged: " + elementModels.toString());
                    adapter.submitList(elementModels);

                }
            });
        } else {
            elementViewModel.getAllElementsMoved().observe(this, new Observer<List<ElementModel>>() {
                @Override
                public void onChanged(List<ElementModel> elementModels) {


                    Log.d("Edited", "onChanged: " + elementModels.toString());
                    adapter.submitList(elementModels);
                }
            });
        }
    }

    private void getNewerData() {
        // load / call from net?
    }

    private void startThread() {
        Intent serviceIntent = new Intent(this, ServiceIntent.class);
        serviceIntent.putExtra("startThread", true);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void startLongTask(List<ElementModel> elementModels) {
        Intent serviceIntent = new Intent(this, ServiceIntent.class);
        serviceIntent.putExtra("rawData", (Serializable) elementModels);

        Log.d("Main", "startLongTask: " + elementModels.toString());
        serviceIntent.putExtra("startLong", true);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void setUpScreen() {
        setSupportActionBar(toolbar);
        recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ElementModelRwAdapter(screenHeight, this, coordinates, mainActivity);
        recyclerViewMain.setAdapter(adapter);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getNewerData();
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
                return super.getMovementFlags(recyclerView, viewHolder);
            }

            @Override
            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
                super.onSelectedChanged(viewHolder, actionState);
            }

            @Override
            public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
            }

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {


                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//                elementModels.remove(viewHolder.getAdapterPosition());
                elementViewModel.delete(adapter.getElementAt(viewHolder.getAdapterPosition()));
                sharedPreferences.edit().putBoolean("UserEdited", true).commit();
            }

        }).attachToRecyclerView(recyclerViewMain);
    }

    private void getDisplay() {
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    private void initViews() {
        refreshLayout = findViewById(R.id.swipeRefresh);
        recyclerViewMain = findViewById(R.id.recyclerviewMain);
        floatingActionButton = findViewById(R.id.floating_action_button_main);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);

                intent.putExtra("topPosition", adapter.getItemCount());
                Log.d("TOPADD", "onClick: " + adapter.getItemCount());
                startActivityForResult(intent, ADD_ELEMENT_REQUEST);
                floatingActionButton.hide();
            }
        });

        adapter.setOnClickListener(new ElementModelRwAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ElementModel elementModel) {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);


                //replace with less code with parcelable
                intent.putExtra(AddEditActivity.EXTRA_ID, elementModel.getId());
                intent.putExtra(AddEditActivity.EXTRA_NAZIV, elementModel.getNaziv());
                intent.putExtra(AddEditActivity.EXTRA_POCETAK, elementModel.getPocetak());
                intent.putExtra(AddEditActivity.EXTRA_KRAJ, elementModel.getKraj());
                intent.putExtra(AddEditActivity.EXTRA_TAG, elementModel.getTag());
                intent.putExtra(AddEditActivity.EXTRA_CURRENT_POSITION, elementModel.getCurrentPosition());
                intent.putExtra(AddEditActivity.EXTRA_TOP_POSITION, (adapter.getItemCount() - 1));
                Log.d("EDITTOP", "onItemClick: " + adapter.getItemCount());
                Log.d("EDITPOS", "onItemClick: " + elementModel.getCurrentPosition());
                startActivityForResult(intent, EDIT_ELEMENT_REQUEST);
            }
        });

        adapter.setOnLongTaskDoneListener(new ElementModelRwAdapter.OnLongTaskDoneListener() {
            @Override
            public void onLongTaskDoneListener(ElementModelRwAdapter elementModelRwAdapter) {
               Toast.makeText(getApplicationContext(), "Hello", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_ELEMENT_REQUEST && resultCode == RESULT_OK) {
            String naziv = data.getStringExtra(AddEditActivity.EXTRA_NAZIV);
            long pocetak = data.getLongExtra(AddEditActivity.EXTRA_POCETAK, 0);
            long kraj = data.getLongExtra(AddEditActivity.EXTRA_KRAJ, 0);
            String tag = data.getStringExtra(AddEditActivity.EXTRA_TAG);
            int position = data.getIntExtra("topPosition", -1);

            Log.d("Addedtolist", "onActivityResult: " + position);
            ElementModel elementModel = new ElementModel(naziv, pocetak, kraj, tag, position + 1, "0");
            elementViewModel.insert(elementModel);
            Toast.makeText(this, "Element added", Toast.LENGTH_SHORT).show();

        } else if (requestCode == EDIT_ELEMENT_REQUEST && resultCode == RESULT_OK) {

            int id = data.getIntExtra(AddEditActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(this, "Element not updated", Toast.LENGTH_SHORT).show();
                return;
            }
            String naziv = data.getStringExtra(AddEditActivity.EXTRA_NAZIV);
            long pocetak = data.getLongExtra(AddEditActivity.EXTRA_POCETAK, 0);
            long kraj = data.getLongExtra(AddEditActivity.EXTRA_KRAJ, 0);
            String tag = data.getStringExtra(AddEditActivity.EXTRA_TAG);
            int currentPosition = data.getIntExtra(AddEditActivity.EXTRA_CURRENT_POSITION, -1);

            Log.d("Editedposition", "onActivityResult: " + currentPosition);
            ElementModel elementModel = new ElementModel(naziv, pocetak, kraj, tag, currentPosition, "0");
            elementModel.setId(id);
            elementViewModel.update(elementModel);
            Toast.makeText(this, "Element updated", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "No changes detected", Toast.LENGTH_SHORT).show();
        }
        floatingActionButton.show();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0)
            getSupportFragmentManager().popBackStackImmediate();
        else super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopThread();
    }

    private void stopThread() {
        Intent serviceIntent = new Intent(this, ServiceIntent.class);
        serviceIntent.putExtra("stopThread", true);
        stopService(serviceIntent);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.deleteAllElements:
                elementViewModel.deleteAllElements();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
