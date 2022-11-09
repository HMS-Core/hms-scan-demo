package com.example.scankitdemokotlin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import com.example.scankitdemo.R
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.hmsscankit.WriterException
import com.huawei.hms.ml.scan.HmsBuildBitmapOption
import com.huawei.hms.ml.scan.HmsScan
import java.io.File
import java.io.FileOutputStream
import java.util.*

class GenerateCodeActivity:Activity() {

    private val TAG = "GenerateCodeActivity"
    private val BARCODE_TYPES = intArrayOf(HmsScan.QRCODE_SCAN_TYPE, HmsScan.DATAMATRIX_SCAN_TYPE, HmsScan.PDF417_SCAN_TYPE, HmsScan.AZTEC_SCAN_TYPE,
            HmsScan.EAN8_SCAN_TYPE, HmsScan.EAN13_SCAN_TYPE, HmsScan.UPCCODE_A_SCAN_TYPE, HmsScan.UPCCODE_E_SCAN_TYPE, HmsScan.CODABAR_SCAN_TYPE,
            HmsScan.CODE39_SCAN_TYPE, HmsScan.CODE93_SCAN_TYPE, HmsScan.CODE128_SCAN_TYPE, HmsScan.ITF14_SCAN_TYPE)
    private val COLOR = intArrayOf(Color.BLACK, Color.BLUE, Color.GRAY, Color.GREEN, Color.RED, Color.YELLOW)
    private val BACKGROUND = intArrayOf(Color.WHITE, Color.YELLOW, Color.RED, Color.GREEN, Color.GRAY, Color.BLUE, Color.BLACK)

    //Define a view.
    private var inputContent: EditText? = null
    private var generateType: Spinner? = null
    private var generateMargin: Spinner? = null
    private var generateColor: Spinner? = null
    private var generateBackground: Spinner? = null
    private var barcodeImage: ImageView? = null
    private var barcodeWidth: EditText? = null
    private var barcodeHeight:EditText? = null
    private var content: String? = null
    private var width = 0
    private  var height:Int = 0
    private var resultImage: Bitmap? = null
    private var type = 0
    private var margin = 1
    private var color = Color.BLACK
    private var background = Color.WHITE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_generate)
        this.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = window
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
        }
        inputContent = findViewById(R.id.barcode_content)
        generateType = findViewById(R.id.generate_type)
        generateMargin = findViewById(R.id.generate_margin)
        generateColor = findViewById(R.id.generate_color)
        generateBackground = findViewById(R.id.generate_backgroundcolor)
        barcodeImage = findViewById(R.id.barcode_image)
        barcodeWidth = findViewById(R.id.barcode_width)
        barcodeHeight = findViewById<EditText>(R.id.barcode_height)
        //Set the barcode type.
        generateType?.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                type = BARCODE_TYPES[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                type = BARCODE_TYPES[0]
            }
        })

        //Set the barcode margin.
        generateMargin?.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                margin = position + 1
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                margin = 1
            }
        })

        //Set the barcode color.
        generateColor?.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                color = COLOR[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                color = COLOR[0]
            }
        })

        //Set the barcode background color.
        generateBackground?.setOnItemSelectedListener(object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                background = BACKGROUND[position]
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                background = BACKGROUND[0]
            }
        })
    }

    /**
     * Generate a barcode.
     */
    fun generateCodeBtnClick(v: View?) {
        content = inputContent!!.text.toString()
        val inputWidth = barcodeWidth!!.text.toString()
        val inputHeight: String = barcodeHeight?.getText().toString()
        //Set the barcode width and height.
        if (inputWidth.length <= 0 || inputHeight.length <= 0) {
            width = 700
            height = 700
        } else {
            width = inputWidth.toInt()
            height = inputHeight.toInt()
        }
        //Set the barcode content.
        if (content!!.length <= 0) {
            Toast.makeText(this, "Please input content first!", Toast.LENGTH_SHORT).show()
            return
        }
        try {
            //Generate the barcode.
            val options = HmsBuildBitmapOption.Creator().setBitmapMargin(margin).setBitmapColor(color).setBitmapBackgroundColor(background).create()
            resultImage = ScanUtil.buildBitmap(content, type, width, height, options)
            barcodeImage!!.setImageBitmap(resultImage)
        } catch (e: WriterException) {
            Toast.makeText(this, "Parameter Error!", Toast.LENGTH_SHORT).show()
        }
    }

    /**
     * Save the barcode.
     */
    fun saveCodeBtnClick(v: View?) {
        if (resultImage == null) {
            Toast.makeText(this@GenerateCodeActivity, "Please generate barcode first!", Toast.LENGTH_LONG).show()
            return
        }
        try {
            val fileName = System.currentTimeMillis().toString() + ".jpg"
            val storePath = Environment.getExternalStorageDirectory().absolutePath
            val appDir = File(storePath)
            if (!appDir.exists()) {
                appDir.mkdir()
            }
            val file = File(appDir, fileName)
            val fileOutputStream = FileOutputStream(file)
            val isSuccess = resultImage!!.compress(Bitmap.CompressFormat.JPEG, 70, fileOutputStream)
            fileOutputStream.flush()
            fileOutputStream.close()
            val uri = Uri.fromFile(file)
            this@GenerateCodeActivity.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
            if (isSuccess) {
                Toast.makeText(this@GenerateCodeActivity, "Barcode has been saved locally", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@GenerateCodeActivity, "Barcode save failed", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.w(TAG, Objects.requireNonNull(e.message))
            Toast.makeText(this@GenerateCodeActivity, "Unkown Error", Toast.LENGTH_SHORT).show()
        }
    }
}