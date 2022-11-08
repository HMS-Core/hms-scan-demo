/*
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.scankitdemo;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.hmsscankit.WriterException;
import com.huawei.hms.ml.scan.HmsBuildBitmapOption;
import com.huawei.hms.ml.scan.HmsScan;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Objects;

public class GenerateCodeActivity extends Activity {

    private static final String TAG = "GenerateCodeActivity" ;
    private static final int[] BARCODE_TYPES = {HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE, HmsScan.PDF417_SCAN_TYPE, HmsScan.AZTEC_SCAN_TYPE,
            HmsScan.EAN8_SCAN_TYPE, HmsScan.EAN13_SCAN_TYPE, HmsScan.UPCCODE_A_SCAN_TYPE, HmsScan.UPCCODE_E_SCAN_TYPE, HmsScan.CODABAR_SCAN_TYPE,
            HmsScan.CODE39_SCAN_TYPE, HmsScan.CODE93_SCAN_TYPE, HmsScan.CODE128_SCAN_TYPE, HmsScan.ITF14_SCAN_TYPE};
    private static  final int[] COLOR = {Color.BLACK, Color.BLUE, Color.GRAY, Color.GREEN, Color.RED, Color.YELLOW};
    private static  final int[] BACKGROUND = {Color.WHITE, Color.YELLOW, Color.RED, Color.GREEN, Color.GRAY, Color.BLUE, Color.BLACK};
    //Define a view.
    private EditText inputContent;
    private Spinner generateType;
    private Spinner generateMargin;
    private Spinner generateColor;
    private Spinner generateBackground;
    private ImageView barcodeImage;
    private EditText barcodeWidth, barcodeHeight;
    private String content;
    private int width, height;
    private Bitmap resultImage;
    private int type = 0;
    private int margin = 1;
    private int color = Color.BLACK;
    private int background = Color.WHITE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_generate);
        this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
        }
        inputContent = findViewById(R.id.barcode_content);
        generateType = findViewById(R.id.generate_type);
        generateMargin = findViewById(R.id.generate_margin);
        generateColor = findViewById(R.id.generate_color);
        generateBackground = findViewById(R.id.generate_backgroundcolor);
        barcodeImage = findViewById(R.id.barcode_image);
        barcodeWidth = findViewById(R.id.barcode_width);
        barcodeHeight = findViewById(R.id.barcode_height);
        //Set the barcode type.
        generateType.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                type = BARCODE_TYPES[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                type = BARCODE_TYPES[0];
            }
        });

        //Set the barcode margin.
        generateMargin.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                margin = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                margin = 1;
            }
        });

        //Set the barcode color.
        generateColor.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                color = COLOR[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                color = COLOR[0];
            }
        });

        //Set the barcode background color.
        generateBackground.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                background = BACKGROUND[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                background = BACKGROUND[0];
            }
        });
    }

    /**
     * Generate a barcode.
     */
    public void generateCodeBtnClick(View v) {
        content = inputContent.getText().toString();
        String inputWidth = barcodeWidth.getText().toString();
        String inputHeight = barcodeHeight.getText().toString();
        //Set the barcode width and height.
        if (inputWidth.length() <= 0 || inputHeight.length() <= 0) {
            width = 700;
            height = 700;
        } else {
            width = Integer.parseInt(inputWidth);
            height = Integer.parseInt(inputHeight);
        }
        //Set the barcode content.
        if (content.length() <= 0) {
            Toast.makeText(this, "Please input content first!", Toast.LENGTH_SHORT).show();
            return;
        }
        if (color == background) {
            Toast.makeText(this, "The color and background cannot be the same!", Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            //Generate the barcode.
            HmsBuildBitmapOption options = new HmsBuildBitmapOption.Creator().setBitmapMargin(margin).setBitmapColor(color).setBitmapBackgroundColor(background).create();
            resultImage = ScanUtil.buildBitmap(content, type, width, height, options);
            barcodeImage.setImageBitmap(resultImage);
        } catch (WriterException e) {
            Toast.makeText(this, "Parameter Error!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Save the barcode.
     */
    public void saveCodeBtnClick(View v) {
        if (resultImage == null) {
            Toast.makeText(GenerateCodeActivity.this, "Please generate barcode first!", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            String fileName = System.currentTimeMillis() + ".jpg";
            String storePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File appDir = new File(storePath);
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            File file = new File(appDir, fileName);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            boolean isSuccess = resultImage.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            Uri uri = Uri.fromFile(file);
            GenerateCodeActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
            if (isSuccess) {
                Toast.makeText(GenerateCodeActivity.this, "Barcode has been saved locally", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(GenerateCodeActivity.this, "Barcode save failed", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            Log.w(TAG, Objects.requireNonNull(e.getMessage()));
            Toast.makeText(GenerateCodeActivity.this, "Unkown Error", Toast.LENGTH_SHORT).show();
        }

    }
}
