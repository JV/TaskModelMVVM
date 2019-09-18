package com.example.taskmodelmvvm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import com.example.taskmodelmvvm.activities.AddEditActivity;
import com.example.taskmodelmvvm.adapters.ElementModelRwAdapter;
import com.example.taskmodelmvvm.entity.ElementModel;
import com.example.taskmodelmvvm.services.ServiceIntent;
import com.example.taskmodelmvvm.viewmodel.ElementViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    public static final int ADD_ELEMENT_REQUEST = 1;
    public static final int EDIT_ELEMENT_REQUEST = 2;

    private ElementViewModel elementViewModel;

    private float screenHeight;
    private float screenWidth;
    private RecyclerView recyclerViewMain;
    private List<ElementModel> elementModels = new ArrayList<>();
    private List<List<Integer>> coordinates = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private FloatingActionButton floatingActionButton;
    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;

    private ElementModelRwAdapter adapter;
    private MainActivity mainActivity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getDisplay();
        setContentView(R.layout.activity_main);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        initViews();
        setupListeners();
        startThread();
        setUpScreen();

        if (!sharedPreferences.getBoolean("UserEdited", false)) {
            recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ElementModelRwAdapter(screenHeight, this, coordinates, mainActivity);

            recyclerViewMain.setAdapter(adapter);
            refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    getNewerData();
                }
            });
            elementViewModel = ViewModelProviders.of(this).get(ElementViewModel.class);

            elementViewModel.getAllElements().observe(this, new Observer<List<ElementModel>>() {

                @Override
                public void onChanged(List<ElementModel> elementModels) {
                    Log.d("Listpassing", "onChanged: " + elementModels.toString());
                    startLongTask(elementModels);
                    adapter.submitList(elementModels);


                }
            });

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {


                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    elementViewModel.delete(adapter.getElementAt(viewHolder.getAdapterPosition()));
                    sharedPreferences.edit().putBoolean("UserEdited", true).commit();
                }

            }).attachToRecyclerView(recyclerViewMain);

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {

//                int fromPosition = viewHolder.getAdapterPosition();
//                int toPosition = target.getAdapterPosition();
//
//                if (fromPosition < toPosition) {
//                    for (int i = fromPosition; i < toPosition; i++) {
//                        Collections.swap(mWordEntities, i, i + 1);
//
//                        int order1 = mWordEntities.get(i).getOrder();
//                        int order2 = mWordEntities.get(i + 1).getOrder();
//                        mWordEntities.get(i).setOrder(order2);
//                        mWordEntities.get(i + 1).setOrder(order1);
//                    }
//                } else {
//                    for (int i = fromPosition; i > toPosition; i--) {
//                        Collections.swap(mWordEntities, i, i - 1);
//
//                        int order1 = mWordEntities.get(i).getOrder();
//                        int order2 = mWordEntities.get(i - 1).getOrder();
//                        mWordEntities.get(i).setOrder(order2);
//                        mWordEntities.get(i - 1).setOrder(order1);
//                    }
//                }
                    sharedPreferences.edit().putBoolean("UserEdited", true).commit();
                    return true;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                }

            }).attachToRecyclerView(recyclerViewMain);

            adapter.setOnClickListener(new ElementModelRwAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ElementModel elementModel) {
                    Intent intent = new Intent(MainActivity.this, AddEditActivity.class);

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

        } else {
            recyclerViewMain.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ElementModelRwAdapter(screenHeight, this, coordinates, mainActivity);
            recyclerViewMain.setAdapter(adapter);
            elementViewModel = ViewModelProviders.of(this).get(ElementViewModel.class);

            elementViewModel.getAllElementsMoved().observe(this, new Observer<List<ElementModel>>() {

                @Override
                public void onChanged(List<ElementModel> elementModels) {
                    Log.d("Listpassing", "onChanged: " + elementModels.toString());
                    startLongTask(elementModels);
                    adapter.submitList(elementModels);
                }
            });

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                    return false;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                    elementViewModel.delete(adapter.getElementAt(viewHolder.getAdapterPosition()));
                }
            }).attachToRecyclerView(recyclerViewMain);

            new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, 0) {

                @Override
                public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {


                    elementViewModel.moveElement(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//                int fromPosition = viewHolder.getAdapterPosition();
//                int toPosition = target.getAdapterPosition();
//
//                if (fromPosition < toPosition) {
//                    for (int i = fromPosition; i < toPosition; i++) {
//                        Collections.swap(mWordEntities, i, i + 1);
//
//                        int order1 = mWordEntities.get(i).getOrder();
//                        int order2 = mWordEntities.get(i + 1).getOrder();
//                        mWordEntities.get(i).setOrder(order2);
//                        mWordEntities.get(i + 1).setOrder(order1);
//                    }
//                } else {
//                    for (int i = fromPosition; i > toPosition; i--) {
//                        Collections.swap(mWordEntities, i, i - 1);
//
//                        int order1 = mWordEntities.get(i).getOrder();
//                        int order2 = mWordEntities.get(i - 1).getOrder();
//                        mWordEntities.get(i).setOrder(order2);
//                        mWordEntities.get(i - 1).setOrder(order1);
//                    }
//                }

                    return true;
                }

                @Override
                public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                }

            }).attachToRecyclerView(recyclerViewMain);

            adapter.setOnClickListener(new ElementModelRwAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(ElementModel elementModel) {
                    Intent intent = new Intent(MainActivity.this, AddEditActivity.class);

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
        }


    }

    private void getNewerData() {

        //async
    }

    private void startThread() {
        Intent serviceIntent = new Intent(this, ServiceIntent.class);
        serviceIntent.putExtra("startThread", true);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    public void startLongTask(List<ElementModel> elementModels) {
        Intent serviceIntent = new Intent(this, ServiceIntent.class);
        serviceIntent.putExtra("rawData", (Serializable) elementModels);
        serviceIntent.putExtra("startLong", true);
        ContextCompat.startForegroundService(this, serviceIntent);
    }

    private void setUpScreen() {
        setSupportActionBar(toolbar);
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
            ElementModel elementModel = new ElementModel(naziv, pocetak, kraj, tag, position + 1);
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
            ElementModel elementModel = new ElementModel(naziv, pocetak, kraj, tag, currentPosition);
            elementModel.setId(id);
            elementViewModel.update(elementModel);
            Toast.makeText(this, "Element updated", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "No changes added", Toast.LENGTH_SHORT).show();
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
        Log.d("mainonDestroy", "onDestroy: Main destrtroyed");
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
