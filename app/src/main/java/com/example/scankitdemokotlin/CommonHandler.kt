package com.example.scankitdemokotlin

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.os.Handler
import android.os.HandlerThread
import android.os.Message
import android.text.TextUtils
import android.util.Log
import com.example.scankitdemo.CameraOperation

import com.example.scankitdemo.draw.ScanResultView.HmsScanGraphic
import com.example.scankitdemokotlin.CommonActivity.Companion.SCAN_RESULT
import com.example.scankitdemokotlin.MainActivity.Companion.BITMAP_CODE
import com.example.scankitdemokotlin.MainActivity.Companion.MULTIPROCESSOR_ASYN_CODE
import com.example.scankitdemokotlin.MainActivity.Companion.MULTIPROCESSOR_SYN_CODE
import com.huawei.hmf.tasks.OnSuccessListener
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzer
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import com.huawei.hms.mlsdk.common.MLFrame
import java.io.ByteArrayOutputStream

class CommonHandler(val activity: Activity, val cameraOperation: CameraOperation, val mode: Int) :Handler() {
    private var decodeThread: HandlerThread? = null
    private var decodeHandle: Handler? = null

    companion object {
        const val TAG = "MainHandler"
        const val DEFAULT_ZOOM:Double = 1.00
    }

    init {
        decodeThread = HandlerThread("DecodeThread")
        decodeThread?.start()
        decodeHandle = object : Handler(decodeThread?.getLooper()) {
            override fun handleMessage(msg: Message) {
                if (msg == null) {
                    return
                }
                if (mode == BITMAP_CODE || mode == MULTIPROCESSOR_SYN_CODE) {
                    var result: Array<HmsScan?>? = decodeSyn(msg.arg1, msg.arg2, msg.obj as ByteArray, activity, HmsScan.ALL_SCAN_TYPE, mode)
                    if (result == null || result.size == 0) {
                        restart(DEFAULT_ZOOM)
                    } else if (TextUtils.isEmpty(result[0]!!.getOriginalValue()) && result[0]!!.getZoomValue() != 1.0) {
                        restart(result[0]!!.getZoomValue())
                    } else if (!TextUtils.isEmpty(result[0]!!.getOriginalValue())) {
                        val message = Message()
                        message.what = msg.what
                        message.obj = result
                        this@CommonHandler.sendMessage(message)
                        restart(DEFAULT_ZOOM)
                    } else {
                        restart(DEFAULT_ZOOM)
                    }
                }
                if (mode == MULTIPROCESSOR_ASYN_CODE) {
                    decodeAsyn(msg.arg1, msg.arg2, msg.obj as ByteArray, activity, HmsScan.ALL_SCAN_TYPE)
                }
            }
        }
        cameraOperation.startPreview()
        restart(DEFAULT_ZOOM)
    }

    /**
     * Call the MultiProcessor API in synchronous mode.
     */
    private fun decodeSyn(width: Int, height: Int, data: ByteArray, activity: Activity, type: Int, mode: Int): Array<HmsScan?>? {
        val bitmap = convertToBitmap(width, height, data)
        if (mode == BITMAP_CODE) {
            val options = HmsScanAnalyzerOptions.Creator().setHmsScanTypes(type).setPhotoMode(false).create()
            return ScanUtil.decodeWithBitmap(activity, bitmap, options)
        } else if (mode == MULTIPROCESSOR_SYN_CODE) {
            val image = MLFrame.fromBitmap(bitmap)
            val options = HmsScanAnalyzerOptions.Creator().setHmsScanTypes(type).create()
            val analyzer = HmsScanAnalyzer(options)
            val result = analyzer.analyseFrame(image)
            if (result != null && result.size() > 0 && result.valueAt(0) != null && !TextUtils.isEmpty(result.valueAt(0)!!.getOriginalValue())) {
                val info = arrayOfNulls<HmsScan>(result.size())
                for (index in 0 until result.size()) {
                    info[index] = result.valueAt(index)
                }
                return info
            }
        }
        return null
    }

    /**
     * Convert camera data into bitmap data.
     */
    private fun convertToBitmap(width: Int, height: Int, data: ByteArray): Bitmap {
        val yuv = YuvImage(data, ImageFormat.NV21, width, height, null)
        val stream = ByteArrayOutputStream()
        yuv.compressToJpeg(Rect(0, 0, width, height), 100, stream)
        return BitmapFactory.decodeByteArray(stream.toByteArray(), 0, stream.toByteArray().size)
    }

    /**
     * Call the MultiProcessor API in asynchronous mode.
     */
    private fun decodeAsyn(width: Int, height: Int, data: ByteArray, activity: Activity, type: Int) {
        var bitmap = convertToBitmap(width, height, data)
        val image = MLFrame.fromBitmap(bitmap)
        val options = HmsScanAnalyzerOptions.Creator().setHmsScanTypes(type).create()
        val analyzer = HmsScanAnalyzer(options)

        analyzer.analyzInAsyn(image).addOnSuccessListener(OnSuccessListener<MutableList<HmsScan?>?> {
            if (it != null && it.size > 0 && it[0] != null && !TextUtils.isEmpty(it[0]!!.getOriginalValue())) {
                //val infos:Array<HmsScan?> = arrayOfNulls<HmsScan?>(it.size)
                val message = Message()
                message.obj = it.toTypedArray()
                sendMessage(message)
                restart(DEFAULT_ZOOM)
            } else {
                restart(DEFAULT_ZOOM)
            }
            bitmap.recycle()
        }).addOnFailureListener { e ->
            Log.w(TAG, e)
            restart(DEFAULT_ZOOM)
            bitmap.recycle()
        }
    }

    override fun handleMessage(message: Message) {
        Log.e(TAG, message.what.toString())
        removeMessages(1)
        if (message.what == 0) {

            val commonActivity1 = activity as CommonActivity
            commonActivity1.scanResultView?.clear()
            val intent = Intent()
            intent.putExtra(SCAN_RESULT, message.obj as Array<HmsScan?>)
            activity.setResult(Activity.RESULT_OK, intent)
            //Show the scanning result on the screen.
            if (mode == MULTIPROCESSOR_ASYN_CODE || mode == MULTIPROCESSOR_SYN_CODE) {
                val commonActivity = activity
                val arr = message.obj as Array<HmsScan>
                if (arr!=null&&arr.size>=0) {
                    for (i in arr.indices) {
                        if (i == 0) {
                            commonActivity.scanResultView?.add(HmsScanGraphic(commonActivity.scanResultView!!, arr[i], Color.YELLOW))
                        } else if (i == 1) {
                            commonActivity.scanResultView?.add(HmsScanGraphic(commonActivity.scanResultView!!, arr[i], Color.BLUE))
                        } else if (i == 2) {
                            commonActivity.scanResultView?.add(HmsScanGraphic(commonActivity.scanResultView!!, arr[i], Color.RED))
                        } else if (i == 3) {
                            commonActivity.scanResultView?.add(HmsScanGraphic(commonActivity.scanResultView!!, arr[i], Color.GREEN))
                        } else {
                            commonActivity.scanResultView?.add(HmsScanGraphic(commonActivity.scanResultView!!, arr[i]))
                        }
                    }
                }
                commonActivity.scanResultView!!.setCameraInfo(1080, 1920)
                commonActivity.scanResultView!!.invalidate()
                sendEmptyMessageDelayed(1, 1000)
            } else {
                activity.finish()
            }
        } else if (message.what == 1) {
            val commonActivity1 = activity as CommonActivity
            commonActivity1.scanResultView?.clear()
        }
    }

    fun quit() {
        try {
            cameraOperation.stopPreview()
            decodeHandle!!.looper.quit()
            decodeThread!!.join(500)
        } catch (e: InterruptedException) {
            Log.w(TAG, e)
        }
    }

    fun restart(zoomValue: Double) {
        cameraOperation.callbackFrame(decodeHandle, zoomValue)
    }

}