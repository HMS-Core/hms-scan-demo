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

import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceHolder;

import java.io.IOException;
import java.util.List;

public class CameraOperation {

    private static final String TAG = "CameraOperation";
    private Camera camera = null;
    private Camera.Parameters parameters = null;
    private boolean isPreview = false;
    private FrameCallback frameCallback = new FrameCallback();
    private int width = 1920;
    private int height = 1080;
    private double defaultZoom = 1.0;

    /**
     * Open up the camera.
     */
    public synchronized void open(SurfaceHolder holder) throws IOException {
        camera = Camera.open();
        parameters = camera.getParameters();
        parameters.setPictureSize(width, height);
        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        parameters.setPictureFormat(ImageFormat.NV21);
        camera.setPreviewDisplay(holder);
        camera.setDisplayOrientation(90);
        camera.setParameters(parameters);
    }

    public synchronized void close() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

    public synchronized void startPreview() {
        if (camera != null && !isPreview) {
            camera.startPreview();
            isPreview = true;
        }
    }

    public synchronized void stopPreview() {
        if (camera != null && isPreview) {
            camera.stopPreview();
            frameCallback.setProperties(null);
            isPreview = false;
        }
    }

    public synchronized void callbackFrame(Handler handler, double zoomValue) {
        if (camera != null && isPreview) {
            frameCallback.setProperties(handler);
            if (camera.getParameters().isZoomSupported() && zoomValue != defaultZoom) {
                //Auto zoom.
                parameters.setZoom(convertZoomInt(zoomValue));
                camera.setParameters(parameters);
            }
            camera.setOneShotPreviewCallback(frameCallback);
        }
    }

    public int convertZoomInt(double zoomValue) {
        List<Integer> allZoomRatios = parameters.getZoomRatios();
        float maxZoom = Math.round(allZoomRatios.get(allZoomRatios.size() - 1) / 100f);
        if (zoomValue >= maxZoom) {
            return allZoomRatios.size() - 1;
        }
        for (int i = 1; i < allZoomRatios.size(); i++) {
            if (allZoomRatios.get(i) >= (zoomValue * 100) && allZoomRatios.get(i - 1) <= (zoomValue * 100)) {
                return i;
            }
        }
        return -1;
    }



    class FrameCallback implements Camera.PreviewCallback {

        private Handler handler;

        public void setProperties(Handler handler) {
            this.handler = handler;

        }

        @Override
        public void onPreviewFrame(byte[] data, Camera camera) {
            if (handler != null) {
                Message message = handler.obtainMessage(0, camera.getParameters().getPreviewSize().width,
                        camera.getParameters().getPreviewSize().height, data);
                message.sendToTarget();
                handler = null;
            }
        }
    }
}
