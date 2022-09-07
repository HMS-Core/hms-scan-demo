package com.example.scankitdemokotlin

import android.app.Activity
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import com.example.scankitdemo.R
import com.huawei.hms.hmsscankit.OnLightVisibleCallBack
import com.huawei.hms.hmsscankit.OnResultCallback
import com.huawei.hms.hmsscankit.RemoteView
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions
import java.io.IOException

class DefinedActivity:Activity() {
    private val img = intArrayOf(R.drawable.flashlight_on, R.drawable.flashlight_off)

    var frameLayout:FrameLayout? = null
    var remoteView:RemoteView? = null
    var backBtn:ImageView? = null
    var imgBtn:ImageView? = null
    var flushBtn:ImageView? = null

    var mScreenWidth = 0
    var mScreenHeight = 0

    companion object{
        private const val SCAN_FRAME_SIZE = 240
        const val SCAN_RESULT = "scanResult"
        const val REQUEST_CODE_PHOTO = 0X1113
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_defined)

        frameLayout = findViewById(R.id.rim)

        //1. Obtain the screen density to calculate the viewfinder's rectangle.
        val dm = resources.displayMetrics
        val density = dm.density
        //2. Obtain the screen size.
        //2. Obtain the screen size.
        mScreenWidth = resources.displayMetrics.widthPixels
        mScreenHeight = resources.displayMetrics.heightPixels

        val scanFrameSize = (SCAN_FRAME_SIZE * density).toInt()

        //3. Calculate the viewfinder's rectangle, which in the middle of the layout.
        //Set the scanning area. (Optional. Rect can be null. If no settings are specified, it will be located in the middle of the layout.)

        //3. Calculate the viewfinder's rectangle, which in the middle of the layout.
        //Set the scanning area. (Optional. Rect can be null. If no settings are specified, it will be located in the middle of the layout.)
        val rect = Rect()
        rect.left = mScreenWidth / 2 - scanFrameSize / 2
        rect.right = mScreenWidth / 2 + scanFrameSize / 2
        rect.top = mScreenHeight / 2 - scanFrameSize / 2
        rect.bottom = mScreenHeight / 2 + scanFrameSize / 2


        //Initialize the RemoteView instance, and set callback for the scanning result.


        //Initialize the RemoteView instance, and set callback for the scanning result.
        remoteView = RemoteView.Builder()
            .setContext(this)
            .setBoundingBox(rect)
            .setFormat(HmsScan.ALL_SCAN_TYPE)
            .build()
        // When the light is dim, this API is called back to display the flashlight switch.
        // When the light is dim, this API is called back to display the flashlight switch.
        flushBtn = findViewById(R.id.flush_btn)
        remoteView?.setOnLightVisibleCallback { visible ->
            if (visible) {
                flushBtn?.visibility = View.VISIBLE
            }
        }
        // Subscribe to the scanning result callback event.
        // Subscribe to the scanning result callback event.
        remoteView?.setOnResultCallback { result -> //Check the result.
            if (result != null && result.isNotEmpty() && result[0] != null && !TextUtils.isEmpty(result[0].getOriginalValue())) {
                val intent = Intent()
                intent.putExtra(SCAN_RESULT, result[0])
                setResult(RESULT_OK, intent)
                finish()
            }
        }

        // Load the customized view to the activity.
        // Load the customized view to the activity.
        remoteView?.onCreate(savedInstanceState)
        val params = FrameLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)
        frameLayout?.addView(remoteView, params)
        // Set the back, photo scanning, and flashlight operations.
        // Set the back, photo scanning, and flashlight operations.
        setBackOperation()
        setPictureScanOperation()
        setFlashOperation()
    }

    private fun setPictureScanOperation() {
        imgBtn = findViewById(R.id.img_btn)
        imgBtn?.setOnClickListener {
            val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            this@DefinedActivity.startActivityForResult(pickIntent, REQUEST_CODE_PHOTO)
        }
    }

    private fun setFlashOperation() {
        flushBtn?.setOnClickListener {
            if (remoteView?.lightStatus == true) {
                remoteView?.switchLight()
                flushBtn?.setImageResource(img.last())
            } else {
                remoteView?.switchLight()
                flushBtn?.setImageResource(img.first())
            }
        }
    }

    private fun setBackOperation() {
        backBtn = findViewById(R.id.back_img)
        backBtn?.setOnClickListener { finish() }
    }

    override fun onStart() {
        super.onStart()
        remoteView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        remoteView?.onResume()
    }

    override fun onPause() {
        super.onPause()
        remoteView?.onPause()
    }

    override fun onStop() {
        super.onStop()
        remoteView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        remoteView?.onDestroy()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE_PHOTO) {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                val hmsScans = ScanUtil.decodeWithBitmap(this@DefinedActivity, bitmap, HmsScanAnalyzerOptions.Creator().setPhotoMode(true).create())
                if (hmsScans != null && hmsScans.isNotEmpty() && hmsScans[0] != null && !TextUtils.isEmpty(hmsScans[0]!!.getOriginalValue())) {
                    val intent = Intent()
                    intent.putExtra(SCAN_RESULT, hmsScans[0])
                    setResult(RESULT_OK, intent)
                    finish()
                }
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}