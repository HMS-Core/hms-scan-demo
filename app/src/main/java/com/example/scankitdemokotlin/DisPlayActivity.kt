package com.example.scankitdemokotlin

import android.Manifest
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Vibrator
import android.provider.Settings
import android.text.TextUtils
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.*
import androidx.core.app.ActivityCompat
import com.example.scankitdemo.R
import com.example.scankitdemo.action.*
import com.huawei.hms.hmsscankit.ScanUtil
import com.huawei.hms.ml.scan.HmsScan
import com.huawei.hms.ml.scan.HmsScan.WiFiConnectionInfo

class DisPlayActivity :Activity(){
    //Define a view.
    private var backBtn: ImageView? = null
    private var copyButton: Button? = null
    private var codeFormat: TextView? = null
    private var resultType: TextView? = null
    private var rawResult: TextView? = null
    private var icon: ImageView? = null
    private var iconText: TextView? = null
    private var resultTypeTitle: TextView? = null
    private var wiFiConnectionInfo: WiFiConnectionInfo? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_display)
        backBtn = findViewById(R.id.result_back_img_in)
        backBtn?.setOnClickListener(View.OnClickListener { finish() })
        codeFormat = findViewById(R.id.barcode_type)
        icon = findViewById(R.id.diplay_icon)
        iconText = findViewById(R.id.diplay_text)
        resultType = findViewById(R.id.barcode_type_mon)
        rawResult = findViewById(R.id.barcode_rawValue)
        resultTypeTitle = findViewById(R.id.barcode_type_mon_t)
        copyButton = findViewById(R.id.button_operate)
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
            //Obtain the scanning result.
            val obj: HmsScan = intent.getParcelableExtra(ScanUtil.RESULT)
            try {
                valueFillIn(obj)
            } catch (e: Exception) {
            }
        }
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        vibrator.vibrate(300)
    }

    private fun valueFillIn(hmsScan: HmsScan) {
        rawResult!!.text = hmsScan.getOriginalValue()
        if (hmsScan.getScanType() == HmsScan.QRCODE_SCAN_TYPE) {
            codeFormat!!.text = "QR code"
        } else if (hmsScan.getScanType() == HmsScan.AZTEC_SCAN_TYPE) {
            codeFormat!!.text = "AZTEC code"
        } else if (hmsScan.getScanType() == HmsScan.DATAMATRIX_SCAN_TYPE) {
            codeFormat!!.text = "DATAMATRIX code"
        } else if (hmsScan.getScanType() == HmsScan.PDF417_SCAN_TYPE) {
            codeFormat!!.text = "PDF417 code"
        } else if (hmsScan.getScanType() == HmsScan.CODE93_SCAN_TYPE) {
            codeFormat!!.text = "CODE93"
        } else if (hmsScan.getScanType() == HmsScan.CODE39_SCAN_TYPE) {
            codeFormat!!.text = "CODE39"
        } else if (hmsScan.getScanType() == HmsScan.CODE128_SCAN_TYPE) {
            codeFormat!!.text = "CODE128"
        } else if (hmsScan.getScanType() == HmsScan.EAN13_SCAN_TYPE) {
            codeFormat!!.text = "EAN13 code"
        } else if (hmsScan.getScanType() == HmsScan.EAN8_SCAN_TYPE) {
            codeFormat!!.text = "EAN8 code"
        } else if (hmsScan.getScanType() == HmsScan.ITF14_SCAN_TYPE) {
            codeFormat!!.text = "ITF14 code"
        } else if (hmsScan.getScanType() == HmsScan.UPCCODE_A_SCAN_TYPE) {
            codeFormat!!.text = "UPCCODE_A"
        } else if (hmsScan.getScanType() == HmsScan.UPCCODE_E_SCAN_TYPE) {
            codeFormat!!.text = "UPCCODE_E"
        } else if (hmsScan.getScanType() == HmsScan.CODABAR_SCAN_TYPE) {
            codeFormat!!.text = "CODABAR"
        }

        //Show the barcode result.
        if (hmsScan.getScanType() == HmsScan.QRCODE_SCAN_TYPE) {
            resultType!!.visibility = View.VISIBLE
            resultTypeTitle!!.visibility = View.VISIBLE
            if (hmsScan.getScanTypeForm() == HmsScan.PURE_TEXT_FORM) {
                icon!!.setImageResource(R.drawable.text)
                iconText!!.text = "Text"
                resultType!!.text = "Text"
                copyButton!!.text = getText(R.string.copy)
                copyButton!!.setOnClickListener {
                    if (rawResult != null && !TextUtils.isEmpty(rawResult!!.text)) {
                        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val mClipData = ClipData.newPlainText("Label", rawResult!!.text)
                        cm.setPrimaryClip(mClipData)
                        Toast.makeText(this@DisPlayActivity, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (hmsScan.getScanTypeForm() == HmsScan.EVENT_INFO_FORM) {
                icon!!.setImageResource(R.drawable.event)
                iconText!!.text = "Event"
                resultType!!.text = "Event"
                copyButton!!.text = getText(R.string.add_calendar)
                copyButton!!.setOnClickListener {
                    startActivity(CalendarEventAction.getCalendarEventIntent(hmsScan.getEventInfo()))
                    finish()
                }
            } else if (hmsScan.getScanTypeForm() == HmsScan.CONTACT_DETAIL_FORM) {
                icon!!.setImageResource(R.drawable.contact)
                iconText!!.text = "Contact"
                resultType!!.text = "Contact"
                copyButton!!.text = getText(R.string.add_contact)
                copyButton!!.setOnClickListener {
                    startActivity(ContactInfoAction.getContactInfoIntent(hmsScan.getContactDetail()))
                    finish()
                }
            } else if (hmsScan.getScanTypeForm() == HmsScan.DRIVER_INFO_FORM) {
                icon!!.setImageResource(R.drawable.text)
                iconText!!.text = "Text"
                resultType!!.text = "License"
                copyButton!!.text = getText(R.string.copy)
                copyButton!!.setOnClickListener {
                    if (rawResult != null && !TextUtils.isEmpty(rawResult!!.text)) {
                        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val mClipData = ClipData.newPlainText("Label", rawResult!!.text)
                        cm.setPrimaryClip(mClipData)
                        Toast.makeText(this@DisPlayActivity, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show()
                    }
                }
            } else if (hmsScan.getScanTypeForm() == HmsScan.EMAIL_CONTENT_FORM) {
                icon!!.setImageResource(R.drawable.email)
                iconText!!.text = "Email"
                resultType!!.text = "Email"
                copyButton!!.text = getText(R.string.send_email)
                copyButton!!.setOnClickListener {
                    startActivity(Intent.createChooser(EmailAction.getEmailInfo(hmsScan.getEmailContent()), "Select email application."))
                    //startActivity(EmailAction.getContactInfoIntent(result.getEmailContent()));
                    finish()
                }
            } else if (hmsScan.getScanTypeForm() == HmsScan.LOCATION_COORDINATE_FORM) {
                icon!!.setImageResource(R.drawable.location)
                iconText!!.text = "Location"
                resultType!!.text = "Location"
                if (LocationAction.checkMapAppExist(applicationContext)) {
                    copyButton!!.text = getText(R.string.nivigation)
                    copyButton!!.setOnClickListener {
                        try {
                            startActivity(LocationAction.getLoactionInfo(hmsScan.getLocationCoordinate()))
                            finish()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                } else {
                    copyButton!!.text = getText(R.string.copy)
                    copyButton!!.setOnClickListener {
                        if (rawResult != null && !TextUtils.isEmpty(rawResult!!.text)) {
                            val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                            val mClipData = ClipData.newPlainText("Label", rawResult!!.text)
                            cm.setPrimaryClip(mClipData)
                            Toast.makeText(this@DisPlayActivity, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else if (hmsScan.getScanTypeForm() == HmsScan.TEL_PHONE_NUMBER_FORM) {
                icon!!.setImageResource(R.drawable.tel)
                iconText!!.text = "Tel"
                resultType!!.text = "Tel"
                copyButton!!.text = getText(R.string.call)
                copyButton!!.setOnClickListener {
                    try {
                        startActivity(DialAction.getDialIntent(hmsScan.getTelPhoneNumber()))
                        finish()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } else if (hmsScan.getScanTypeForm() == HmsScan.SMS_FORM) {
                icon!!.setImageResource(R.drawable.sms)
                iconText!!.text = "SMS"
                resultType!!.text = "SMS"
                copyButton!!.text = getText(R.string.send_sms)
                copyButton!!.setOnClickListener {
                    startActivity(SMSAction.getSMSInfo(hmsScan.getSmsContent()))
                    finish()
                }
            } else if (hmsScan.getScanTypeForm() == HmsScan.WIFI_CONNECT_INFO_FORM) {
                icon!!.setImageResource(R.drawable.wifi)
                iconText!!.text = "Wi-Fi"
                resultType!!.text = "Wi-Fi"
                copyButton!!.text = getText(R.string.connect_network)
                wiFiConnectionInfo = hmsScan.wifiConnectionInfo
                copyButton!!.setOnClickListener {
                    val permissionWifi = Manifest.permission.ACCESS_WIFI_STATE
                    val permissionWifi2 = Manifest.permission.CHANGE_WIFI_STATE
                    val permission = arrayOf(permissionWifi, permissionWifi2)
                    ActivityCompat.requestPermissions(this@DisPlayActivity, permission, CalendarEvent)
                }
            } else if (hmsScan.getScanTypeForm() == HmsScan.URL_FORM) {
                icon!!.setImageResource(R.drawable.website)
                iconText!!.text = "WebSite"
                resultType!!.text = "WebSite"
                copyButton!!.text = getText(R.string.open_browser)
                copyButton!!.setOnClickListener {
                    val webpage = Uri.parse(hmsScan.getOriginalValue())
                    val intent = Intent(Intent.ACTION_VIEW, webpage)
                    if (intent.resolveActivity(packageManager) != null) {
                        startActivity(intent)
                    }
                }
                resultType!!.text = "WebSite"
            } else {
                icon!!.setImageResource(R.drawable.text)
                iconText!!.text = "Text"
                resultType!!.text = "Text"
                copyButton!!.text = getText(R.string.copy)
                copyButton!!.setOnClickListener {
                    if (rawResult != null && !TextUtils.isEmpty(rawResult!!.text)) {
                        val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                        val mClipData = ClipData.newPlainText("Label", rawResult!!.text)
                        cm.setPrimaryClip(mClipData)
                        Toast.makeText(this@DisPlayActivity, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else if (hmsScan.getScanType() == HmsScan.EAN13_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.ISBN_NUMBER_FORM) {
                icon!!.setImageResource(R.drawable.isbn)
                iconText!!.text = "ISBN"
                resultType!!.visibility = View.VISIBLE
                resultTypeTitle!!.visibility = View.VISIBLE
                resultType!!.text = "ISBN"
            } else if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                icon!!.setImageResource(R.drawable.product)
                iconText!!.text = "Product"
                resultType!!.visibility = View.VISIBLE
                resultTypeTitle!!.visibility = View.VISIBLE
                resultType!!.text = "Product"
            } else {
                icon!!.setImageResource(R.drawable.text)
                iconText!!.text = "Text"
                resultType!!.visibility = View.GONE
                resultTypeTitle!!.visibility = View.GONE
            }
            copyButton!!.text = getText(R.string.copy)
            copyButton!!.setOnClickListener {
                if (rawResult != null && !TextUtils.isEmpty(rawResult!!.text)) {
                    //Obtain the clipboard manager.
                    val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val mClipData = ClipData.newPlainText("Label", rawResult!!.text)
                    cm.setPrimaryClip(mClipData)
                    Toast.makeText(this@DisPlayActivity, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show()
                }
            }
        } else if (hmsScan.getScanType() == HmsScan.EAN8_SCAN_TYPE || hmsScan.getScanType() == HmsScan.UPCCODE_A_SCAN_TYPE || hmsScan.getScanType() == HmsScan.UPCCODE_E_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                icon!!.setImageResource(R.drawable.product)
                iconText!!.text = "Product"
                resultType!!.visibility = View.VISIBLE
                resultTypeTitle!!.visibility = View.VISIBLE
                resultType!!.text = "Product"
            } else {
                icon!!.setImageResource(R.drawable.text)
                iconText!!.text = "Text"
                resultType!!.visibility = View.GONE
                resultTypeTitle!!.visibility = View.GONE
            }
            copyButton!!.text = getText(R.string.copy)
            copyButton!!.setOnClickListener {
                if (rawResult != null && !TextUtils.isEmpty(rawResult!!.text)) {
                    val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val mClipData = ClipData.newPlainText("Label", rawResult!!.text)
                    cm.setPrimaryClip(mClipData)
                    Toast.makeText(this@DisPlayActivity, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            icon!!.setImageResource(R.drawable.text)
            iconText!!.text = "Text"
            copyButton!!.text = getText(R.string.copy)
            copyButton!!.setOnClickListener {
                if (rawResult != null && !TextUtils.isEmpty(rawResult!!.text)) {
                    val cm = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                    val mClipData = ClipData.newPlainText("Label", rawResult!!.text)
                    cm.setPrimaryClip(mClipData)
                    Toast.makeText(this@DisPlayActivity, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show()
                }
            }
            resultType!!.visibility = View.GONE
            resultTypeTitle!!.visibility = View.GONE
        }
    }

    val CalendarEvent = 0x3300

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        when (requestCode) {
            CalendarEvent -> {
                if (grantResults.size > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (wiFiConnectionInfo != null) {
                        WifiAdmin(this@DisPlayActivity).Connect(wiFiConnectionInfo!!.getSsidNumber(),
                                wiFiConnectionInfo!!.getPassword(), wiFiConnectionInfo!!.getCipherMode())
                        this@DisPlayActivity.startActivity(Intent(Settings.ACTION_WIFI_SETTINGS))
                        finish()
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStop() {
        super.onStop()
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