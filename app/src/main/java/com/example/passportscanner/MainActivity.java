package com.example.passportscanner;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity {


    ImageView ocr, nfc;
    MaterialButton showHistoryBtn;
    DatabaseHelper mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        setListeners();
        mDb = DatabaseHelper.getInstance(this);
        if (mDb.loadAllScannedItems() == null || mDb.loadAllScannedItems().size() == 0) {
            insertDummyHistory();
        }
    }

    private void setListeners() {
        ocr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, OcrScannerActivity.class));
            }
        });
        nfc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NfcScannerActivity.class));
            }
        });
        showHistoryBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, HistoryActivity.class));
            }
        });
    }

    private void initViews() {
        ocr = findViewById(R.id.ivOCR);
        nfc = findViewById(R.id.ivNFC);
        showHistoryBtn = findViewById(R.id.show_history_btn);
    }

    private void insertDummyHistory() {
        String dummyText = "fjakljfif858";
        for (int i = 0; i < 10; i++) {
            String dummyText1 = dummyText + i;
            ScannedDataModel model = new ScannedDataModel(System.currentTimeMillis(), dummyText1, ScannedDataModel.TYPE_OCR);
            mDb.insertData(model);
        }
        Toast.makeText(this, "Dummy data inserted", Toast.LENGTH_SHORT).show();
    }
}