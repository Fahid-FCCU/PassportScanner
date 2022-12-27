package com.example.passportscanner;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.provider.ContactsContract;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView historyRv;
    HistoryAdapter adapter;
    DatabaseHelper mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        mDb = DatabaseHelper.getInstance(this);
        ArrayList<ScannedDataModel> scannedItems = mDb.loadAllScannedItems();

        historyRv = findViewById(R.id.history_rv);
        historyRv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter(this, scannedItems);
        historyRv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

    }


}