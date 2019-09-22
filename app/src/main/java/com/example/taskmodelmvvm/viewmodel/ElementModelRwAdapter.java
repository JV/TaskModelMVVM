package com.example.taskmodelmvvm.viewmodel;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Build;
import android.preference.PreferenceManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.taskmodelmvvm.MainActivity;
import com.example.taskmodelmvvm.R;
import com.example.taskmodelmvvm.persistance.ElementModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ElementModelRwAdapter extends ListAdapter<ElementModel,
        ElementModelRwAdapter.ElementHolder> {


    private OnItemClickListener listener;
    private OnLongTaskDoneListener taskListener;
    private float screenWidth;
    private float screenHeight;
    private List<List<Integer>> coordinates;
    private int numberOfMaxShownRows = 5;
    private SharedPreferences sharedPreferences;
    private Context context;
    private ElementViewModel elementViewModel;
    private MainActivity mainActivity;
    private List<ElementModel> elementModels = new ArrayList<>();
    private int lineWidth = 10; // takes padding/margin into account
    private int maxNumberOfLines = (int) ((screenWidth / 2) / lineWidth);


    public ElementModelRwAdapter(float screenHeight, Context context,
                                 List<List<Integer>> coordinates, MainActivity mainActivity) {
        super(DIFF_CALLBACK);
        this.mainActivity = mainActivity;
        this.context = context;
//        elementViewModel = ViewModelProviders.of(this.mainActivity).get(ElementViewModel.class);


        this.screenHeight = screenHeight;
        Gson gson = new Gson();
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String json = sharedPreferences.getString("CoordinatesList", "");
        Type type = new TypeToken<List<List<Integer>>>() {
        }.getType();
        this.coordinates = gson.fromJson(json, type);
//        if (taskListener != null) {
//            taskListener.onLongTaskDoneListener(this);
//        }
    }

    private static final DiffUtil.ItemCallback<ElementModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<ElementModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ElementModel oldItem, @NonNull ElementModel newItem) {

            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ElementModel oldItem, @NonNull ElementModel newItem) {


            return oldItem.getNaziv().equals(newItem.getNaziv()) &&
                    oldItem.getPocetak().equals(newItem.getPocetak()) &&
                    oldItem.getKraj().equals(newItem.getKraj()) &&
                    oldItem.getTag().equals(newItem.getTag());
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @NonNull
    @Override
    public ElementHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_item_card,
                parent, false);
        int height = (int) (screenHeight / numberOfMaxShownRows);
        view.setMinimumHeight(height);
        return new ElementHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ElementHolder holder, int position) {



        ElementModel currentElement = getItem(position);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("CoordinatesList", "");
        Type type = new TypeToken<List<List<Integer>>>() {
        }.getType();
        this.coordinates = gson.fromJson(json, type);

        holder.tvNaziv.setText(currentElement.getNaziv());
        holder.tvPocetak.setText(String.valueOf(currentElement.getPocetak()));
        holder.tvKraj.setText(String.valueOf(currentElement.getKraj()));
        holder.connectionHolders.removeAllViews();

        if (coordinates == null) {

        } else {
            for (int i = 0; i < coordinates.size(); i++) {

                int startX;
                int startY;
                int stopX;
                int stopY;
                int height = (int) (screenHeight / numberOfMaxShownRows);
                startX = 0;
                startY = 0;
                stopX = 0;
                stopY = height;
                boolean matchFirst = false;
                boolean matchLast = false;
                boolean matchUnique = false;
                boolean belongs = false;
                boolean noMatch = false;

                LineView lineView = new LineView(holder.holderContex, stopY);

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(20, stopY);

                holder.connectionHolders.addView(lineView, layoutParams);
                if (position < coordinates.get(i).get(0) | position > coordinates.get(i).get(1) |
                        coordinates.get(i).get(1) == -1) {

                    LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(20, stopY);
                    lineView.setLayoutParams(layoutParams2);
                    lineView.setVisibility(View.INVISIBLE);

                } else {

                    if (position == coordinates.get(i).get(0) | position == coordinates.get(i).get(1)) {

                        if (position == coordinates.get(i).get(0)) {

                            matchFirst = true;

                            LinearLayout.LayoutParams layoutParams3 = new LinearLayout.LayoutParams
                                    (20, stopY / 2);
                            layoutParams3.gravity = Gravity.BOTTOM;
                            lineView.setLayoutParams(layoutParams3);
                            lineView.setVisibility(View.VISIBLE);

                        } else if (position == coordinates.get(i).get(1)) {

                            matchLast = true;

                            LinearLayout.LayoutParams layoutParams4 = new LinearLayout.LayoutParams
                                    (20, stopY / 2);
                            lineView.setLayoutParams(layoutParams4);
                            lineView.setVisibility(View.VISIBLE);

                        }
                    } else {

                        LinearLayout.LayoutParams layoutParams5 = new LinearLayout.LayoutParams
                                (20, stopY);
                        lineView.setLayoutParams(layoutParams5);
                        lineView.setVisibility(View.VISIBLE);
                    }
                }
            }
        }
    }

    public ElementModel getElementAt(int position) {
        return getItem(position);
    }

    class ElementHolder extends RecyclerView.ViewHolder {

        TextView tvNaziv;
        TextView tvPocetak;
        TextView tvKraj;
        LinearLayout connectionHolders;
        Context holderContex;
        Canvas canvas;
        Bitmap bitmap;
        Paint paint;
        LinearLayout connectionHolder;
        Path path;
        float startX;
        float startY;
        float stopX;
        float stopY;

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public ElementHolder(@NonNull View itemView) {

            super(itemView);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(getItem(position));
                    }
                }
            });
            path = new Path();
            holderContex = itemView.getContext();
            paint = new Paint();
            bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
            canvas = new Canvas(bitmap);
            tvNaziv = itemView.findViewById(R.id.tvNaziv);
            tvPocetak = itemView.findViewById(R.id.tvPocetak);
            tvKraj = itemView.findViewById(R.id.tvKraj);
            connectionHolders = itemView.findViewById(R.id.conncectionHolders);
            connectionHolder = itemView.findViewById(R.id.connectionHolder);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(ElementModel elementModel);
    }

    public void setOnClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public interface OnLongTaskDoneListener {
        void onLongTaskDoneListener(ElementModelRwAdapter elementModelRwAdapter);
    }

    public void setOnLongTaskDoneListener(OnLongTaskDoneListener listener) {
        this.taskListener = listener;
    }
}
