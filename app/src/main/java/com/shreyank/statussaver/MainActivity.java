package com.shreyank.statussaver;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;
import android.view.ViewGroup;
import android.view.View;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public class MainActivity extends Activity {

    LinearLayout statusContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusContainer = findViewById(R.id.statusContainer);

        Button refresh = findViewById(R.id.refreshButton);
        refresh.setOnClickListener(v -> loadStatuses());

        loadStatuses();
    }

    private void loadStatuses() {
        statusContainer.removeAllViews();  // Clear old

        File statusDir = new File(Environment.getExternalStorageDirectory() +
                "/WhatsApp/Media/.Statuses");

        if (!statusDir.exists()) {
            Toast.makeText(this, "Status folder not found!", Toast.LENGTH_LONG).show();
            return;
        }

        File[] statusFiles = statusDir.listFiles();
        if (statusFiles == null) return;

        for (File file : statusFiles) {
            if (file.getName().endsWith(".jpg")) {
                addStatusToUI(file);
            }
        }
    }

    private void addStatusToUI(File statusFile) {
        // Create ImageView
        ImageView image = new ImageView(this);
        image.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, 600));
        image.setPadding(0, 20, 0, 10);

        try {
            FileInputStream fis = new FileInputStream(statusFile);
            Bitmap bmp = BitmapFactory.decodeStream(fis);
            image.setImageBitmap(bmp);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create Button
        Button downloadBtn = new Button(this);
        downloadBtn.setText("Download");
        downloadBtn.setOnClickListener(v -> saveStatus(statusFile));

        // Add to layout
        statusContainer.addView(image);
        statusContainer.addView(downloadBtn);
    }

    private void saveStatus(File from) {
        try {
            File destDir = new File(Environment.getExternalStorageDirectory() + "/StatusSaver");
            if (!destDir.exists()) destDir.mkdirs();

            File out = new File(destDir, from.getName());
            FileInputStream in = new FileInputStream(from);
            FileOutputStream outStream = new FileOutputStream(out);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) > 0) {
                outStream.write(buffer, 0, len);
            }

            in.close();
            outStream.close();

            Toast.makeText(this, "Saved to /StatusSaver", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Failed to save!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}

