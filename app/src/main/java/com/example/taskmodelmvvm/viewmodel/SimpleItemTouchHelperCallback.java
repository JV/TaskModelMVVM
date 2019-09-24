package com.example.taskmodelmvvm.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmodelmvvm.persistance.ElementModel;

import java.util.ArrayList;
import java.util.List;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {

    private ItemTouchHelperAdapter mAdapter;
    private Context mContext;
    private boolean mOrderChanged;

    private List<ElementModel> elementModels;
    private ElementModelRwAdapter mmAdapter;
    private RecyclerView recyclerViewMain;
    private SharedPreferences sharedPreferences;


    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter adapter, Context context,
                                         ElementModelRwAdapter mmAdapter,
                                         RecyclerView recyclerViewMain, List elementModels,
                                         SharedPreferences sharedPreferences) {

        this.mAdapter = adapter;
        this.mContext = context;
        this.mmAdapter = mmAdapter;
        this.recyclerViewMain = recyclerViewMain;
        this.elementModels = elementModels;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public void onMoved(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, int fromPos, @NonNull RecyclerView.ViewHolder target, int toPos, int x, int y) {
        super.onMoved(recyclerView, viewHolder, fromPos, target, toPos, x, y);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {

        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        mOrderChanged = true;
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());

    }

    @Override
    public void onSelectedChanged(@Nullable RecyclerView.ViewHolder viewHolder, int actionState) {
        super.onSelectedChanged(viewHolder, actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_IDLE && mOrderChanged) {

            mOrderChanged = false;
        }
    }

    public interface ItemTouchHelperAdapter {

        void onItemMove(int oldPosition, int newPosition);

        void onItemDismiss(int position);
    }
}