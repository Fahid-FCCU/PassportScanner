package com.example.passportscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    ArrayList<ScannedDataModel> scannedItems;
    Context context;


    public HistoryAdapter(Context context, ArrayList<ScannedDataModel> scannedItems) {
        super();
        this.context = context;
        this.scannedItems = scannedItems;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.history_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ScannedDataModel model = scannedItems.get(position);
        holder.scannedTextTv.setText(model.getText());
        holder.dateTv.setText(getFormattedDateAndTime(model.getTimestamp()));
        holder.typeTv.setText(model.getType());
    }

    @Override
    public int getItemCount() {
        if (scannedItems == null) return 0;
        return scannedItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView scannedTextTv, dateTv, typeTv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            scannedTextTv = itemView.findViewById(R.id.scanned_text_item_tv);
            dateTv = itemView.findViewById(R.id.date_item_tv);
            typeTv = itemView.findViewById(R.id.type_item_tv);
        }
    }

    // This method will return a string of formatted date and time
    public static String getFormattedDateAndTime(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy kk:mm:ss", Locale.ENGLISH);
        return formatter.format(timestamp);
    }
}
