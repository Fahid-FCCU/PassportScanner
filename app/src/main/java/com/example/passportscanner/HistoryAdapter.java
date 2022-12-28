package com.example.passportscanner;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {

    ArrayList<ScannedDataModel> scannedItems;
    Context context;
    ClipboardManager clipboardManager;
    ClipData clip;

    public HistoryAdapter(Context context, ArrayList<ScannedDataModel> scannedItems) {
        super();
        this.context = context;
        this.scannedItems = scannedItems;
        clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
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
        String scannedText = model.getText();
        holder.scannedTextTv.setText(scannedText);
        holder.dateTv.setText(getFormattedDateAndTime(model.getTimestamp()));
        holder.typeTv.setText(model.getType());
        holder.copyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clip = ClipData.newPlainText("label", scannedText);
                clipboardManager.setPrimaryClip(clip);
                Toast.makeText(context, "Text copied to clipboard", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        if (scannedItems == null) return 0;
        return scannedItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView scannedTextTv, dateTv, typeTv;
        MaterialButton copyBtn;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            scannedTextTv = itemView.findViewById(R.id.scanned_text_item_tv);
            dateTv = itemView.findViewById(R.id.date_item_tv);
            typeTv = itemView.findViewById(R.id.type_item_tv);
            copyBtn = itemView.findViewById(R.id.copy_btn);
        }
    }

    // This method will return a string of formatted date and time
    public static String getFormattedDateAndTime(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM, yyyy kk:mm:ss", Locale.ENGLISH);
        return formatter.format(timestamp);
    }
}
