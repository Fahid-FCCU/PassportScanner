package com.example.passportscanner;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class OcrScannerActivity extends AppCompatActivity {

    EditText editText;
    ImageView gallery, camera, ivSelectedImage;
    private Uri uri;
    private ActivityResultLauncher<Uri> saveImageFromCamera;
    Bitmap bitmap;
    private TextRecognizer recognizer;
    Button btnCopyOcr;
    private ClipboardManager myClipboard;
    private ClipData myClip;
    private DatabaseHelper mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ocr_scanner);
        initViews();
        mDb = DatabaseHelper.getInstance(this);
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);
        setListeners();
    }

    private void setListeners() {
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Dexter.withContext(OcrScannerActivity.this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent(Intent.ACTION_PICK);
                                intent.setType("image/*");
                                startActivityForResult(Intent.createChooser(intent, "Select Image"), 1);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }


                        }).check();
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCameraPermissionGranted()) {
                    launchCameraIntent();
                }
            }
        });

        btnCopyOcr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myClip = ClipData.newPlainText("text", editText.getText().toString());
                myClipboard.setPrimaryClip(myClip);

                Toast.makeText(getApplicationContext(), "Text Copied",
                        Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void initViews() {
        gallery = findViewById(R.id.ivGallery);
        camera = findViewById(R.id.ivCamera);
        editText = findViewById(R.id.etOcr);
        btnCopyOcr = findViewById(R.id.btnCopyOcr);
        ivSelectedImage = findViewById(R.id.ivSelectedImage);
        myClipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        saveImageFromCamera = registerForActivityResult(new ActivityResultContracts.TakePicture(), new ActivityResultCallback<Boolean>() {
            @Override
            public void onActivityResult(Boolean result) {
                if (!result) {
                    uri = Uri.parse("");
                } else {
                    loadImage();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == 1 && resultCode == RESULT_OK) {
            uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                bitmap = BitmapFactory.decodeStream(inputStream);
                ivSelectedImage.setImageBitmap(bitmap);
                processImage(bitmap);
            } catch (Exception ignored) {

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private boolean isCameraPermissionGranted() {
        if (this.checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            // Ask for Permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            return false;
        }
    }

    private void launchCameraIntent() {
        File f = new File(this.getExternalCacheDir(), "image_" + System.currentTimeMillis() + ".jpg");
        uri = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                BuildConfig.APPLICATION_ID + ".provider", f);
        saveImageFromCamera.launch(uri);
    }

    private void loadImage() {

        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ivSelectedImage.setImageBitmap(bitmap);
            ivSelectedImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
            processImage(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void processImage(Bitmap bitmap) {
        recognizer.process(InputImage.fromBitmap(bitmap, fixRotation(uri))).addOnSuccessListener(
                new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        editText.setText(text.getText());
                        //Toast.makeText(OcrScannerActivity.this,text.getText().toString(),Toast.LENGTH_SHORT).show();
                        ScannedDataModel model = new ScannedDataModel(System.currentTimeMillis(), text.getText(), ScannedDataModel.TYPE_OCR);
                        mDb.insertData(model);
                    }
                });
    }

    public int fixRotation(@NonNull Uri uri) {
        ExifInterface ei;
        int fixOrientation = 0;
        try {
            InputStream input = getContentResolver().openInputStream(uri);
            ei = new ExifInterface(input);

            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            switch (orientation) {

                case ExifInterface.ORIENTATION_ROTATE_90:
                    fixOrientation = 90;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_180:
                    fixOrientation = 80;
                    break;

                case ExifInterface.ORIENTATION_ROTATE_270:
                    fixOrientation = 270;
                    break;

                case ExifInterface.ORIENTATION_NORMAL:
                default:
                    fixOrientation = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return fixOrientation;
    }
}