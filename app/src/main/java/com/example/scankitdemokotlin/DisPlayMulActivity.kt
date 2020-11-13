package com.example.scankitdemokotlin

import android.app.Activity
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.example.scankitdemo.R
import com.example.scankitdemokotlin.MainActivity.Companion.RESULT
import com.huawei.hms.ml.scan.HmsScan

class DisPlayMulActivity:Activity() {
    private var backBtn: ImageView? = null
    private var codeFormat: TextView? = null
    private var resultType: TextView? = null
    private var rawResult: TextView? = null
    private var scrollView: LinearLayout? = null
    private var view: View? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_display_mul)
        backBtn = findViewById(R.id.result_back_img_in)
        backBtn?.setOnClickListener(View.OnClickListener { finish() })
        scrollView = findViewById(R.id.scroll_item)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            val window = window
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
            window?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS or WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION)
            val relativeLayout = findViewById<RelativeLayout>(R.id.result_title)
            if (relativeLayout != null) {
                val lp = RelativeLayout.LayoutParams(relativeLayout.layoutParams.width, relativeLayout.layoutParams.height)
                lp.setMargins(0, getStatusBarHeight(), 0, 0)
                relativeLayout.layoutParams = lp
            }
        }
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(300)
        val layoutInflater = LayoutInflater.from(this)
        //Obtain the scanning result.
        val obj = intent.getParcelableArrayExtra(RESULT)
        if (obj != null) {
            for (i in obj.indices) {
                if (obj[i] is HmsScan && !TextUtils.isEmpty((obj[i] as HmsScan).getOriginalValue())) {
                    view = layoutInflater.inflate(R.layout.activity_display_item, null)
                    scrollView?.addView(view)
                    valueFillIn(obj[i] as HmsScan, view)
                }
            }
        }
        view?.findViewById<View>(R.id.line)?.visibility = View.GONE
    }

    /**
     * Display the scanning result in TextView.
     */
    private fun valueFillIn(hmsScan: HmsScan, view: View?) {
        codeFormat = view?.findViewById(R.id.barcode_type)
        resultType = view?.findViewById(R.id.barcode_type_mon)
        rawResult = view?.findViewById(R.id.barcode_rawValue)
        rawResult?.setText(hmsScan.getOriginalValue())
        if (hmsScan.getScanType() == HmsScan.QRCODE_SCAN_TYPE) {
            codeFormat?.setText("QR code")
        } else if (hmsScan.getScanType() == HmsScan.AZTEC_SCAN_TYPE) {
            codeFormat?.setText("AZTEC code")
        } else if (hmsScan.getScanType() == HmsScan.DATAMATRIX_SCAN_TYPE) {
            codeFormat?.setText("DATAMATRIX code")
        } else if (hmsScan.getScanType() == HmsScan.PDF417_SCAN_TYPE) {
            codeFormat?.setText("PDF417 code")
        } else if (hmsScan.getScanType() == HmsScan.CODE93_SCAN_TYPE) {
            codeFormat?.setText("CODE93")
        } else if (hmsScan.getScanType() == HmsScan.CODE39_SCAN_TYPE) {
            codeFormat?.setText("CODE39")
        } else if (hmsScan.getScanType() == HmsScan.CODE128_SCAN_TYPE) {
            codeFormat?.setText("CODE128")
        } else if (hmsScan.getScanType() == HmsScan.EAN13_SCAN_TYPE) {
            codeFormat?.setText("EAN13 code")
        } else if (hmsScan.getScanType() == HmsScan.EAN8_SCAN_TYPE) {
            codeFormat?.setText("EAN8 code")
        } else if (hmsScan.getScanType() == HmsScan.ITF14_SCAN_TYPE) {
            codeFormat?.setText("ITF14 code")
        } else if (hmsScan.getScanType() == HmsScan.UPCCODE_A_SCAN_TYPE) {
            codeFormat?.setText("UPCCODE_A")
        } else if (hmsScan.getScanType() == HmsScan.UPCCODE_E_SCAN_TYPE) {
            codeFormat?.setText("UPCCODE_E")
        } else if (hmsScan.getScanType() == HmsScan.CODABAR_SCAN_TYPE) {
            codeFormat?.setText("CODABAR")
        }
        if (hmsScan.getScanType() == HmsScan.QRCODE_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.PURE_TEXT_FORM) {
                resultType?.setText("Text")
            } else if (hmsScan.getScanTypeForm() == HmsScan.EVENT_INFO_FORM) {
                resultType?.setText("Event")
            } else if (hmsScan.getScanTypeForm() == HmsScan.CONTACT_DETAIL_FORM) {
                resultType?.setText("Contact")
            } else if (hmsScan.getScanTypeForm() == HmsScan.DRIVER_INFO_FORM) {
                resultType?.setText("License")
            } else if (hmsScan.getScanTypeForm() == HmsScan.EMAIL_CONTENT_FORM) {
                resultType?.setText("Email")
            } else if (hmsScan.getScanTypeForm() == HmsScan.TEL_PHONE_NUMBER_FORM) {
                resultType?.setText("Tel")
            } else if (hmsScan.getScanTypeForm() == HmsScan.SMS_FORM) {
                resultType?.setText("SMS")
            } else if (hmsScan.getScanTypeForm() == HmsScan.WIFI_CONNECT_INFO_FORM) {
                resultType?.setText("Wi-Fi")
            } else if (hmsScan.getScanTypeForm() == HmsScan.URL_FORM) {
                resultType?.setText("WebSite")
            } else if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                resultType?.setText("Product")
            } else {
                resultType?.setText("Text")
            }
        } else if (hmsScan.getScanType() == HmsScan.EAN13_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.ISBN_NUMBER_FORM) {
                resultType?.setText("ISBN")
            } else if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                resultType?.setText("Product")
            } else {
                resultType?.setText("Text")
            }
        } else if (hmsScan.getScanType() == HmsScan.EAN8_SCAN_TYPE || hmsScan.getScanType() == HmsScan.UPCCODE_A_SCAN_TYPE || hmsScan.getScanType() == HmsScan.UPCCODE_E_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                resultType?.setText("Product")
            } else {
                resultType?.setText("Text")
            }
        } else {
            resultType?.setText("Text")
        }
    }

    protected fun getStatusBarHeight(): Int {
        var result = 0
        // Obtain the ID.
        if (resources != null) {
            val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                result = resources.getDimensionPixelSize(resourceId)
            }
        }
        return result
    }
}