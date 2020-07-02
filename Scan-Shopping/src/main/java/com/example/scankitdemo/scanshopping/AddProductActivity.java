/**
 * Copyright 2020. Huawei Technologies Co., Ltd. All rights reserved.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.example.scankitdemo.scanshopping;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.example.scankitdemo.scanshopping.camera.CapturePhotoActivity;
import com.example.scankitdemo.scanshopping.util.Constant;

import java.io.FileInputStream;
import java.io.IOException;

public class AddProductActivity extends AppCompatActivity {
    private String barcode = null;
    private String path = null;
    private ImageView preview;
    private RelativeLayout relativeLayoutScan, relativeLayoutTakePhoto, relativeLayoutSave;
    private int REQUEST_CODE_SCAN_ALL = 1;
    private int REQUEST_TAKE_PHOTO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addproduct);
        this.initView();
        this.initAction();
    }

    private void initView() {
        this.relativeLayoutScan = this.findViewById(R.id.relativate_scan);
        this.relativeLayoutTakePhoto = this.findViewById(R.id.relativate_camera);
        this.relativeLayoutSave = this.findViewById(R.id.relativate_save);
        this.preview = this.findViewById(R.id.previewPane);
    }

    private void initAction() {
        this.relativeLayoutScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProductActivity.this.scanBarcode(AddProductActivity.this.REQUEST_CODE_SCAN_ALL);
            }
        });

        // Outline the edge.
        this.relativeLayoutSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AddProductActivity.this.path == null
                ) {
                    Toast.makeText(AddProductActivity.this.getApplicationContext(), R.string.please_take_picture, Toast.LENGTH_SHORT).show();
                } else if (AddProductActivity.this.barcode == null) {
                    Toast.makeText(AddProductActivity.this.getApplicationContext(), R.string.please_scan_barcode, Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    intent.putExtra(Constant.IMAGE_PATH_VALUE, AddProductActivity.this.path);
                    intent.putExtra(Constant.BARCODE_VALUE, AddProductActivity.this.barcode);
                    setResult(Activity.RESULT_OK, intent);
                    AddProductActivity.this.finish();
                }
            }
        });

        this.relativeLayoutTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddProductActivity.this.takePhoto(AddProductActivity.this.REQUEST_TAKE_PHOTO);
            }
        });
    }

    private void takePhoto(int requestCode) {
        Intent intent = new Intent(AddProductActivity.this, CapturePhotoActivity.class);
        this.startActivityForResult(intent, requestCode);
    }

    private void scanBarcode(int requestCode) {
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create();
        ScanUtil.startScan(this, requestCode, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if ((requestCode == this.REQUEST_CODE_SCAN_ALL)
                && (resultCode == Activity.RESULT_OK)) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            if (obj != null && obj.getOriginalValue() != null) {
                this.barcode = obj.getOriginalValue();
            }

        } else if ((requestCode == this.REQUEST_TAKE_PHOTO)
                && (resultCode == Activity.RESULT_OK)) {
            this.path = data.getStringExtra(Constant.IMAGE_PATH_VALUE);
            this.loadCameraImage();
        }
    }

    private void loadCameraImage() {
        if (this.path == null) {
            return;
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            Bitmap originBitmap = BitmapFactory.decodeStream(fis);
            originBitmap = originBitmap.copy(Bitmap.Config.ARGB_4444, true);
            this.preview.setImageBitmap(originBitmap);
        } catch (IOException error) {
            error.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException error) {
                    error.printStackTrace();
                }
            }
        }
    }
}
