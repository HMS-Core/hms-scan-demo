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

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.mlsdk.common.internal.client.SmartLog;
import com.example.scankitdemo.scanshopping.util.Constant;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Map<String, String> barcodeToProduct = new HashMap<>();

    private int REQUEST_QUERY_PRODUCT = 1001;
    private int REQUEST_ADD_PRODUCT = 1002;
    private static final int PERMISSION_REQUESTS = 1;

    private Bitmap originBitmap;

    Dialog dia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!allPermissionsGranted()) {
            getRuntimePermissions();
        }
    }

    private void getRuntimePermissions() {
        List<String> allNeededPermissions = new ArrayList<>();
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                allNeededPermissions.add(permission);
            }
        }
        if (!allNeededPermissions.isEmpty()) {
            ActivityCompat.requestPermissions(
                    this, allNeededPermissions.toArray(new String[0]), PERMISSION_REQUESTS);
        }
    }

    private boolean allPermissionsGranted() {
        for (String permission : getRequiredPermissions()) {
            if (!isPermissionGranted(this, permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean isPermissionGranted(Context context, String permission) {
        if (ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED) {
            SmartLog.i(TAG, "Permission granted: " + permission);
            return true;
        }
        SmartLog.i(TAG, "Permission NOT granted: " + permission);
        return false;
    }

    private String[] getRequiredPermissions() {
        try {
            PackageInfo info =
                    this.getPackageManager()
                            .getPackageInfo(this.getPackageName(), PackageManager.GET_PERMISSIONS);
            String[] ps = info.requestedPermissions;
            if (ps != null && ps.length > 0) {
                return ps;
            } else {
                return new String[0];
            }
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            return new String[0];
        }
    }

    public void addProduct(View view) {
        Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
        startActivityForResult(intent, REQUEST_ADD_PRODUCT);
    }

    public void queryProduct(View view) {
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(HmsScan.ALL_SCAN_TYPE).create();
        ScanUtil.startScan(this, REQUEST_QUERY_PRODUCT, options);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data == null) {
            return;
        }
        if ((requestCode == this.REQUEST_ADD_PRODUCT) && (resultCode == Activity.RESULT_OK)) {
            barcodeToProduct.put(data.getStringExtra(Constant.BARCODE_VALUE), data.getStringExtra(Constant.IMAGE_PATH_VALUE));
        } else if ((requestCode == this.REQUEST_QUERY_PRODUCT) && (resultCode == Activity.RESULT_OK)) {
            HmsScan obj = data.getParcelableExtra(ScanUtil.RESULT);
            String path = "";
            if (obj != null && obj.getOriginalValue() != null) {
                path = barcodeToProduct.get(obj.getOriginalValue());
            }
            if (path != null && !path.equals("")) {
                loadCameraImage(path);
                showPictures();
            }
        }
    }

    private void loadCameraImage(String path) {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(path);
            this.originBitmap = BitmapFactory.decodeStream(fis);
            this.originBitmap = this.originBitmap.copy(Bitmap.Config.ARGB_4444, true);
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

    private void showPictures() {
        dia = new Dialog(MainActivity.this, R.style.edit_AlertDialog_style);
        dia.setContentView(R.layout.activity_start_dialog);
        ImageView imageView = (ImageView) dia.findViewById(R.id.start_img);
        imageView.setImageBitmap(originBitmap);
        dia.setCanceledOnTouchOutside(true); // Sets whether this dialog is
        Window w = dia.getWindow();
        WindowManager.LayoutParams lp = w.getAttributes();
        lp.x = 0;
        lp.y = 40;
        dia.onWindowAttributesChanged(lp);
        imageView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dia.dismiss();
                    }
                });
        dia.show();
    }
}
