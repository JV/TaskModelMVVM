package com.example.taskmodelmvvm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.taskmodelmvvm.persistance.AddEditActivity;
import com.example.taskmodelmvvm.persistance.ElementModel;
import com.example.taskmodelmvvm.tasks.OnLongTaskCompleted;
import com.example.taskmodelmvvm.tasks.ServiceIntent;
import com.example.taskmodelmvvm.tasks.WorkTask;
import com.example.taskmodelmvvm.viewmodel.ElementModelRwAdapter;
import com.example.taskmodelmvvm.viewmodel.ElementViewModel;
import com.example.taskmodelmvvm.viewmodel.SimpleItemTouchHelperCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public class MainFragment extends Fragment {

    private ElementViewModel elementViewModel;
    private static final int ADD_ELEMENT_REQUEST = 1;
    private static final int EDIT_ELEMENT_REQUEST = 2;

    private List<ElementModel> elementModels = new ArrayList<>();
    private List<Fragment> fragments = new ArrayList<>();
    private float screenHeight;
    private float screenWidth;
    private RecyclerView recyclerViewMain;
    private List<List<Integer>> coordinates = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private FloatingActionButton floatingActionButton;
    private Toolbar toolbar;
    private SwipeRefreshLayout refreshLayout;

    private ElementModelRwAdapter adapter;
    private Bitmap bitmap;
    private boolean mOrderChanged;

    public MainFragment() {
    }

    public static MainFragment newInstance() {
        return new MainFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        initViews(container);
        return inflater.inflate(R.layout.main_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDisplay();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        elementViewModel = ViewModelProviders.of(this).get(ElementViewModel.class);

        subscribeObservers();
        setUpScreen();
        setupListeners();

    }

    private void subscribeObservers() {

        if (!sharedPreferences.getBoolean("UserEdited", false)) {
            elementViewModel.getAllElements().observe(this, new Observer<List<ElementModel>>() {
                @Override
                public void onChanged(List<ElementModel> elementModels) {

                    new WorkTask(getActivity().getApplicationContext(), elementModels, adapter).execute();
                    adapter.submitList(elementModels);

                }
            });
        } else {
            elementViewModel.getAllElementsMoved().observe(this, new Observer<List<ElementModel>>() {
                @Override
                public void onChanged(List<ElementModel> elementModels) {

                    new WorkTask(getActivity().getApplicationContext(), elementModels, adapter).execute();
                    adapter.submitList(elementModels);

                }
            });
        }
    }

    private void setUpScreen() {

        recyclerViewMain.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
        adapter = new ElementModelRwAdapter(screenHeight, getActivity().getApplicationContext(), coordinates, elementViewModel);
        SimpleItemTouchHelperCallback simpleItemTouchHelperCallback =
                new SimpleItemTouchHelperCallback(adapter, getActivity().getApplicationContext(),
                        adapter, recyclerViewMain, elementViewModel.getAllElementsList(), sharedPreferences);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchHelperCallback);
        recyclerViewMain.setAdapter(adapter);
        itemTouchHelper.attachToRecyclerView(recyclerViewMain);
//        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//
//            }
//        });
    }

    private void getDisplay() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
    }

    private void initViews(ViewGroup container) {
        refreshLayout = container.findViewById(R.id.swipeRefresh);
        recyclerViewMain = container.findViewById(R.id.recyclerviewMain);
        floatingActionButton = container.findViewById(R.id.floating_action_button_main);
        toolbar = container.findViewById(R.id.toolbar);
    }

    private void setupListeners() {
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), AddEditActivity.class);
                intent.putExtra(AddEditActivity.EXTRA_TOP_POSITION, adapter.getItemCount());
                Log.d("TOPADD", "onClick: " + adapter.getItemCount());
                startActivityForResult(intent, ADD_ELEMENT_REQUEST);
                floatingActionButton.hide();
            }
        });

        adapter.setOnClickListener(new ElementModelRwAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(ElementModel elementModel) {
                Intent intent = new Intent(getContext(), AddEditActivity.class);

                //replace with less code with parcelable?
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

        adapter.setOnLongTaskCompletedListener(new OnLongTaskCompleted() {
            @Override
            public void onLongTaskCompleted() {

//                recyclerViewMain.setAdapter(recyclerViewMain.getAdapter());

                Log.d("TASKRECEIVEDBACK", "onLongTaskCompleted: RECEIVED");
            }
        });
    }

    //missing timestamp update
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_ELEMENT_REQUEST && resultCode == RESULT_OK) {
            String naziv = data.getStringExtra(AddEditActivity.EXTRA_NAZIV);
            long pocetak = data.getLongExtra(AddEditActivity.EXTRA_POCETAK, 0);
            long kraj = data.getLongExtra(AddEditActivity.EXTRA_KRAJ, 0);
            String tag = data.getStringExtra(AddEditActivity.EXTRA_TAG);
            int position = data.getIntExtra(AddEditActivity.EXTRA_TOP_POSITION, -1);

            Log.d("Addedtolist", "onActivityResult: " + position);
            ElementModel elementModel = new ElementModel(naziv, pocetak, kraj, tag, position + 1, "0");
            elementViewModel.insert(elementModel);
            sharedPreferences.edit().putBoolean("UserEdited", true).apply();
            Toast.makeText(getContext(), "Element added", Toast.LENGTH_SHORT).show();

        } else if (requestCode == EDIT_ELEMENT_REQUEST && resultCode == RESULT_OK) {

            int id = data.getIntExtra(AddEditActivity.EXTRA_ID, -1);
            if (id == -1) {
                Toast.makeText(getContext(), "Element not updated", Toast.LENGTH_SHORT).show();
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
            sharedPreferences.edit().putBoolean("UserEdited", true).apply();
            Toast.makeText(getContext(), "Element updated", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(getContext(), "No changes detected", Toast.LENGTH_SHORT).show();
        }
        floatingActionButton.show();
    }
}

//    private void startThread() {
//        Intent serviceIntent = new Intent(getContext(), ServiceIntent.class);
//        serviceIntent.putExtra("startThread", true);
//
//        //add filter for build
//        ContextCompat.startForegroundService(getContext(), serviceIntent);
//    }
//
//    public void startLongTask(List<ElementModel> elementModels) {
//        Intent serviceIntent = new Intent(getContext(), ServiceIntent.class);
//        serviceIntent.putParcelableArrayListExtra("rawData", (ArrayList<? extends Parcelable>) elementModels);
//
//        Log.d("Main", "startLongTask: " + elementModels.toString());
//        serviceIntent.putExtra("startLong", true);
//
//        // add filter for build
//        ContextCompat.startForegroundService(getContext(), serviceIntent);
//    }

// non custom itemTouchHelper
//        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
//
//            @Override
//            public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
//                int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
//                int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
//                return makeMovementFlags(dragFlags, swipeFlags);
//            }
//
//            @Override
//            public boolean isLongPressDragEnabled() {
//                return true;
//            }
//
//            @Override
//            public boolean isItemViewSwipeEnabled() {
//                return true;
//            }
//
//            @Override
//            public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
//                super.onSelectedChanged(viewHolder, actionState);
//                sharedPreferences.edit().putBoolean("UserEdited", true).commit();
//                if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {
//
//
//                    // update element?
//                    mOrderChanged = false;
//                    adapter.notifyDataSetChanged();
//
//                }
//            }
//
//            @Override
//            public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
//                super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
//
////                elementViewModel.moveElement(fromPos, toPos);
//
//                adapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
//
////                elementViewModel.moveElement(viewHolder.getAdapterPosition(), target.getAdapterPosition());
//
//                mOrderChanged = true;
//                return true;
//            }
//
//            @Override
//            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
//
//                elementViewModel.delete(adapter.getElementAt(viewHolder.getAdapterPosition()));
//                sharedPreferences.edit().putBoolean("UserEdited", true).commit();
//            }
//
//        }).attachToRecyclerView(recyclerViewMain);