package com.example.scankitdemokotlin

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.example.scankitdemo.R
import com.example.scankitdemokotlin.DefinedActivity.Companion.SCAN_RESULT
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScanAnalyzerOptions

class MainActivity: Activity(),ActivityCompat.OnRequestPermissionsResultCallback {

    companion object {
        const val CAMERA_REQ_CODE = 111
        const val DEFINED_CODE = 222
        const val BITMAP_CODE = 333
        const val MULTIPROCESSOR_SYN_CODE = 444
        const val MULTIPROCESSOR_ASYN_CODE = 555
        const val GENERATE_CODE = 666
        const val DECODE = 1
        const val GENERATE = 2
        const val REQUEST_CODE_SCAN_ONE = 0X01
        const val REQUEST_CODE_DEFINE = 0X0111
        const val REQUEST_CODE_SCAN_MULTI = 0X011
        const val DECODE_MODE = "decode_mode"
        const val RESULT = "SCAN_RESULT"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_mwcmain)
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        //Set noTitleBar.
        //Set noTitleBar.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = window
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
    }

    fun loadScanKitBtnClick(view: View?) {
        requestPermission(CAMERA_REQ_CODE, DECODE)
    }

    /**
     * Call the customized view.
     */
    fun newViewBtnClick(view: View?) {
        requestPermission(DEFINED_CODE, DECODE)
    }

    /**
     * Call the bitmap.
     */
    fun bitmapBtnClick(view: View?) {
        requestPermission(BITMAP_CODE, DECODE)
    }

    /**
     * Call the MultiProcessor API in synchronous mode.
     */
    fun multiProcessorSynBtnClick(view: View?) {
        requestPermission(MULTIPROCESSOR_SYN_CODE, DECODE)
    }

    /**
     * Call the MultiProcessor API in asynchronous mode.
     */
    fun multiProcessorAsynBtnClick(view: View?) {
        requestPermission(MULTIPROCESSOR_ASYN_CODE, DECODE)
    }

    /**
     * Start generating the barcode.
     */
    fun generateQRCodeBtnClick(view: View?) {
        requestPermission(GENERATE_CODE, GENERATE)
    }

    /**
     * Apply for permissions.
     */
    private fun requestPermission(requestCode: Int, mode: Int) {
        if (mode == DECODE) {
            decodePermission(requestCode)
        } else if (mode == GENERATE) {
            generatePermission(requestCode)
        }
    }

    /**
     * Apply for permissions.
     */
    private fun decodePermission(requestCode: Int) {
        ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE),
                requestCode)
    }

    /**
     * Apply for permissions.
     */
    private fun generatePermission(requestCode: Int) {
        ActivityCompat.requestPermissions(
                this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                requestCode)
    }

    /**
     * Call back the permission application result. If the permission application is successful, the barcode scanning view will be displayed.
     * @param requestCode Permission application code.
     * @param permissions Permission array.
     * @param grantResults: Permission application result array.
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (permissions == null || grantResults == null) {
            return
        }
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED && requestCode == GENERATE_CODE) {
            val intent = Intent(this, GenerateCodeActivity::class.java)
            this.startActivity(intent)
        }
        if (grantResults.size < 2 || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED) {
            return
        }
        //Default View Mode
        if (requestCode == CAMERA_REQ_CODE) {
            ScanUtil.startScan(this, REQUEST_CODE_SCAN_ONE, HmsScanAnalyzerOptions.Creator().create())
        }
        //Customized View Mode
        if (requestCode == DEFINED_CODE) {
            val intent = Intent(this, DefinedActivity::class.java)
            this.startActivityForResult(intent, REQUEST_CODE_DEFINE)
        }
        //Bitmap Mode
        if (requestCode == BITMAP_CODE) {
            val intent = Intent(this, CommonActivity::class.java)
            intent.putExtra(DECODE_MODE, BITMAP_CODE)
            this.startActivityForResult(intent, REQUEST_CODE_SCAN_MULTI)
        }
        //Multiprocessor Synchronous Mode
        if (requestCode == MULTIPROCESSOR_SYN_CODE) {
            val intent = Intent(this, CommonActivity::class.java)
            intent.putExtra(DECODE_MODE, MULTIPROCESSOR_SYN_CODE)
            this.startActivityForResult(intent, REQUEST_CODE_SCAN_MULTI)
        }
        //Multiprocessor Asynchronous Mode
        if (requestCode == MULTIPROCESSOR_ASYN_CODE) {
            val intent = Intent(this, CommonActivity::class.java)
            intent.putExtra(DECODE_MODE, MULTIPROCESSOR_ASYN_CODE)
            this.startActivityForResult(intent, REQUEST_CODE_SCAN_MULTI)
        }
    }

    /**
     * Event for receiving the activity result.
     *
     * @param requestCode Request code.
     * @param resultCode Result code.
     * @param data        Result.
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode != RESULT_OK || data == null) {
            return
        }
        //Default View
        if (requestCode == REQUEST_CODE_SCAN_ONE) {
            val obj: HmsScan = data.getParcelableExtra(ScanUtil.RESULT)
            if (obj != null) {
                val intent = Intent(this, DisPlayActivity::class.java)
                intent.putExtra(RESULT, obj)
                startActivity(intent)
            }
            //MultiProcessor & Bitmap
        } else if (requestCode == REQUEST_CODE_SCAN_MULTI) {
            val obj = data.getParcelableArrayExtra(SCAN_RESULT)
            if (obj != null && obj.size > 0) {
                //Get one result.
                if (obj.size == 1) {
                    if (obj[0] != null && !TextUtils.isEmpty((obj[0] as HmsScan).getOriginalValue())) {
                        val intent = Intent(this, DisPlayActivity::class.java)
                        intent.putExtra(RESULT, obj[0])
                        startActivity(intent)
                    }
                } else {
                    val intent = Intent(this, DisPlayMulActivity::class.java)
                    intent.putExtra(RESULT, obj)
                    startActivity(intent)
                }
            }
            //Customized View
        } else if (requestCode == REQUEST_CODE_DEFINE) {
            val obj: HmsScan = data.getParcelableExtra(DefinedActivity.SCAN_RESULT)
            if (obj != null) {
                val intent = Intent(this, DisPlayActivity::class.java)
                intent.putExtra(RESULT, obj)
                startActivity(intent)
            }
        }
    }

}