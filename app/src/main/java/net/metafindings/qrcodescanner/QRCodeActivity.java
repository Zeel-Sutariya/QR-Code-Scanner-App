package net.metafindings.qrcodescanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import yuku.ambilwarna.AmbilWarnaDialog;

public class QRCodeActivity extends AppCompatActivity {

    private EditText editTextContent;
    private ImageView imageViewQRCode;
    private int foreground = Color.BLACK;
    private int background = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        editTextContent = findViewById(R.id.editTextContent);
        imageViewQRCode = findViewById(R.id.imageViewQRCode);
        Button buttonGenerate = findViewById(R.id.buttonGenerate);
        buttonGenerate.setOnClickListener(view -> generateQRCode());

        Button colorPickerForegroundButton = findViewById(R.id.color_picker_foreground_button);
        colorPickerForegroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPickerDialogForeground();
            }
        });
        Button colorPickerBackgroundButton = findViewById(R.id.color_picker_background_button);
        colorPickerBackgroundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openColorPickerDialogBackground();
            }
        });

        FloatingActionButton button3 = findViewById(R.id.floatingActionButton);
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareQRCode();
            }
        });

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.navigation_item2);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            if (item.getItemId() == R.id.navigation_item1) {
                startActivity(new Intent(QRCodeActivity.this, MainActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            else if(item.getItemId() == R.id.navigation_item2) {
                startActivity(new Intent(QRCodeActivity.this, QRCodeActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            else if(item.getItemId() == R.id.navigation_item3) {
                startActivity(new Intent(QRCodeActivity.this, SettingsActivity.class));
                overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                return true;
            }
            else
                return false;
        });

        AdView mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

    }

    private void generateQRCode() {
        String content = editTextContent.getText().toString().trim();
        int width = 300;
        int height = 300;
        String charset = "UTF-8";

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, charset);

        Bitmap bitmap = null;
        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? foreground : background;
                }
            }

            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            imageViewQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        FloatingActionButton button3 = findViewById(R.id.floatingActionButton);
        button3.setEnabled(true);
    }
    private void shareQRCode() {
        String content = editTextContent.getText().toString().trim();
        int width = 300;
        int height = 300;
        String charset = "UTF-8";

        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, charset);

        try {
            BitMatrix bitMatrix = new QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            int[] pixels = new int[width * height];
            for (int y = 0; y < height; y++) {
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = bitMatrix.get(x, y) ? foreground : background;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);

            // Create a content URI for the QR code image
            Uri imageUri = getImageUri(bitmap);

            // Create an intent to share the QR code image
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("image/png");
            shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            // Start the activity for sharing
            startActivity(Intent.createChooser(shareIntent, "Share QR Code"));
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private Uri getImageUri(Bitmap bitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "QR Code", null);
        return Uri.parse(path);
    }


    private void openColorPickerDialogForeground() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, foreground, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // Handle dialog cancellation
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                foreground = color;
                // Do something with the selected color
            }

        });
        colorPicker.show();
    }

    private void openColorPickerDialogBackground() {
        AmbilWarnaDialog colorPicker = new AmbilWarnaDialog(this, background, new AmbilWarnaDialog.OnAmbilWarnaListener() {
            @Override
            public void onCancel(AmbilWarnaDialog dialog) {
                // Handle dialog cancellation
            }

            @Override
            public void onOk(AmbilWarnaDialog dialog, int color) {
                background = color;
                // Do something with the selected color
            }

        });
        colorPicker.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

}
