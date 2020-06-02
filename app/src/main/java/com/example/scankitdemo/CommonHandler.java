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
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.example.scankitdemo.draw.ScanResultView;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;
import com.huawei.hms.ml.scan.HmsScanAnalyzer;
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions;
import com.huawei.hms.mlsdk.common.MLFrame;

import java.io.ByteArrayOutputStream;
import java.util.List;

import static android.app.Activity.RESULT_OK;

public final class CommonHandler extends Handler {

    private static final String TAG = "MainHandler";
    private static final double DEFAULT_ZOOM = 1.0;
    private CameraOperation cameraOperation;
    private HandlerThread decodeThread;
    private Handler decodeHandle;
    private Activity activity;
    private int mode;

    public CommonHandler(final Activity activity, CameraOperation cameraOperation, final int mode) {
        this.cameraOperation = cameraOperation;
        this.activity = activity;
        this.mode = mode;
        decodeThread = new HandlerThread("DecodeThread");
        decodeThread.start();
        decodeHandle = new Handler(decodeThread.getLooper()){
            @Override
            public void handleMessage(Message msg) {
                if (msg == null) {
                    return;
                }
                if (mode == MainActivity.BITMAP_CODE || mode == MainActivity.MULTIPROCESSOR_SYN_CODE) {
                    HmsScan[] result = decodeSyn(msg.arg1, msg.arg2, (byte[]) msg.obj, activity, HmsScan.ALL_SCAN_TYPE, mode);
                    if (result == null || result.length == 0) {
                        restart(DEFAULT_ZOOM);
                    } else if (TextUtils.isEmpty(result[0].getOriginalValue()) && result[0].getZoomValue() != 1.0) {
                        restart(result[0].getZoomValue());
                    } else if (!TextUtils.isEmpty(result[0].getOriginalValue())) {
                        Message message = new Message();
                        message.what = msg.what;
                        message.obj = result;
                        CommonHandler.this.sendMessage(message);
                        restart(DEFAULT_ZOOM);
                    } else{
                         restart(DEFAULT_ZOOM);
                    }
                }
                if (mode == MainActivity.MULTIPROCESSOR_ASYN_CODE) {
                    decodeAsyn(msg.arg1, msg.arg2, (byte[]) msg.obj, activity, HmsScan.ALL_SCAN_TYPE);
                }
            }
        };
        cameraOperation.startPreview();
        restart(DEFAULT_ZOOM);
    }

    /**
     * Call the MultiProcessor API in synchronous mode.
     */
    private HmsScan[] decodeSyn(int width, int height, byte[] data, final Activity activity, int type, int mode) {
        Bitmap bitmap = convertToBitmap(width, height, data);
        if (mode == MainActivity.BITMAP_CODE) {
            HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(type).setPhotoMode(false).create();
            return ScanUtil.decodeWithBitmap(activity, bitmap, options);
        } else if (mode == MainActivity.MULTIPROCESSOR_SYN_CODE) {
            MLFrame image = MLFrame.fromBitmap(bitmap);
            HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(type).create();
            HmsScanAnalyzer analyzer = new HmsScanAnalyzer(options);;
            SparseArray<HmsScan> result = analyzer.analyseFrame(image);
            if (result != null && result.size() > 0 && result.valueAt(0) != null && !TextUtils.isEmpty(result.valueAt(0).getOriginalValue())) {
                HmsScan[] info = new HmsScan[result.size()];
                for (int index = 0; index < result.size(); index++) {
                    info[index] = result.valueAt(index);
                }
                return info;
            }
        }
        return null;
    }

    /**
     * Convert camera data into bitmap data.
     */
    private Bitmap convertToBitmap(int width, int height, byte[] data) {
        YuvImage yuv = new YuvImage(data, ImageFormat.NV21, width, height, null);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        yuv.compressToJpeg(new Rect(0, 0, width, height), 100, stream);
        return BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().length);
    }

    /**
     * Call the MultiProcessor API in asynchronous mode.
     */
    private void decodeAsyn(int width, int height, byte[] data, final Activity activity, int type) {
        final Bitmap bitmap = convertToBitmap(width, height, data);
        MLFrame image = MLFrame.fromBitmap(bitmap);
        HmsScanAnalyzerOptions options = new HmsScanAnalyzerOptions.Creator().setHmsScanTypes(type).create();
        HmsScanAnalyzer analyzer = new HmsScanAnalyzer(options);
        analyzer.analyzInAsyn(image).addOnSuccessListener(new OnSuccessListener<List<HmsScan>>() {
            @Override
            public void onSuccess(List<HmsScan> hmsScans) {
                if (hmsScans != null && hmsScans.size() > 0 && hmsScans.get(0) != null && !TextUtils.isEmpty(hmsScans.get(0).getOriginalValue())) {
                    HmsScan[] infos = new HmsScan[hmsScans.size()];
                    Message message = new Message();
                    message.obj = hmsScans.toArray(infos);
                    CommonHandler.this.sendMessage(message);
                    restart(DEFAULT_ZOOM);
                } else {
                    restart(DEFAULT_ZOOM);
                }
                bitmap.recycle();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.w(TAG, e);
                restart(DEFAULT_ZOOM);
                bitmap.recycle();
            }
        });
    }

    @Override
    public void handleMessage(Message message) {
        Log.e(TAG, String.valueOf(message.what));
        removeMessages(1);
        if (message.what == 0) {
            CommonActivity commonActivity1 = (CommonActivity) activity;
            commonActivity1.scanResultView.clear();
            Intent intent = new Intent();
            intent.putExtra(CommonActivity.SCAN_RESULT, (HmsScan[]) message.obj);
            activity.setResult(RESULT_OK, intent);
            //Show the scanning result on the screen.
            if (mode == MainActivity.MULTIPROCESSOR_ASYN_CODE || mode == MainActivity.MULTIPROCESSOR_SYN_CODE) {
                CommonActivity commonActivity = (CommonActivity) activity;

                HmsScan[] arr = (HmsScan[]) message.obj;
                for (int i = 0; i < arr.length; i++) {
                    if (i == 0) {
                        commonActivity.scanResultView.add(new ScanResultView.HmsScanGraphic(commonActivity.scanResultView, arr[i], Color.YELLOW));
                    } else if (i == 1) {
                        commonActivity.scanResultView.add(new ScanResultView.HmsScanGraphic(commonActivity.scanResultView, arr[i], Color.BLUE));
                    } else if (i == 2){
                        commonActivity.scanResultView.add(new ScanResultView.HmsScanGraphic(commonActivity.scanResultView, arr[i], Color.RED));
                    } else if (i == 3){
                        commonActivity.scanResultView.add(new ScanResultView.HmsScanGraphic(commonActivity.scanResultView, arr[i], Color.GREEN));
                    } else {
                        commonActivity.scanResultView.add(new ScanResultView.HmsScanGraphic(commonActivity.scanResultView, arr[i]));
                    }
                }
                commonActivity.scanResultView.setCameraInfo(1080, 1920);
                commonActivity.scanResultView.invalidate();
                sendEmptyMessageDelayed(1,1000);
            } else {
                activity.finish();
            }
        }else if(message.what == 1){
            CommonActivity commonActivity1 = (CommonActivity) activity;
            commonActivity1.scanResultView.clear();
        }
    }

    public void quit() {
        try {
            cameraOperation.stopPreview();
            decodeHandle.getLooper().quit();
            decodeThread.join(500);
        } catch (InterruptedException e) {
            Log.w(TAG, e);
        }
    }

    public void restart(double zoomValue) {
        cameraOperation.callbackFrame(decodeHandle, zoomValue);
    }
}
