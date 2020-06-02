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
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.huawei.hms.ml.scan.HmsScan;


public class DisPlayMulActivity extends Activity {
    //Define a view.
    private ImageView backBtn;
    private TextView codeFormat;
    private TextView resultType;
    private TextView rawResult;
    private LinearLayout scrollView;
    private View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_display_mul);
        backBtn = findViewById(R.id.result_back_img_in);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisPlayMulActivity.this.finish();
            }
        });
        scrollView = findViewById(R.id.scroll_item);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            if (window != null) {
                window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            }
            RelativeLayout relativeLayout = findViewById(R.id.result_title);
            if (relativeLayout != null) {
                RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(relativeLayout.getLayoutParams().width, relativeLayout.getLayoutParams().height);
                lp.setMargins(0, getStatusBarHeight(), 0, 0);
                relativeLayout.setLayoutParams(lp);
            }
        }
        Vibrator vibrator = (Vibrator) this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
        LayoutInflater layoutInflater = LayoutInflater.from(this);
        //Obtain the scanning result.
        Parcelable[] obj = getIntent().getParcelableArrayExtra(MainActivity.RESULT);
        if (obj != null) {
            for (int i = 0; i < obj.length; i++) {
                if (obj[i] instanceof HmsScan && !TextUtils.isEmpty(((HmsScan) obj[i]).getOriginalValue())) {
                    view = layoutInflater.inflate(R.layout.activity_display_item, null);
                    scrollView.addView(view);
                    valueFillIn((HmsScan) obj[i], view);
                }
            }
        }
        view.findViewById(R.id.line).setVisibility(View.GONE);
    }

    /**
     * Display the scanning result in TextView.
     */
    private void valueFillIn(final HmsScan hmsScan, View view) {
        codeFormat = view.findViewById(R.id.barcode_type);

        resultType = view.findViewById(R.id.barcode_type_mon);
        rawResult = view.findViewById(R.id.barcode_rawValue);

        rawResult.setText(hmsScan.getOriginalValue());
        if (hmsScan.getScanType() == HmsScan.QRCODE_SCAN_TYPE) {
            codeFormat.setText("QR code");
        } else if (hmsScan.getScanType() == HmsScan.AZTEC_SCAN_TYPE) {
            codeFormat.setText("AZTEC code");
        } else if (hmsScan.getScanType() == HmsScan.DATAMATRIX_SCAN_TYPE) {
            codeFormat.setText("DATAMATRIX code");
        } else if (hmsScan.getScanType() == HmsScan.PDF417_SCAN_TYPE) {
            codeFormat.setText("PDF417 code");
        } else if (hmsScan.getScanType() == HmsScan.CODE93_SCAN_TYPE) {
            codeFormat.setText("CODE93");
        } else if (hmsScan.getScanType() == HmsScan.CODE39_SCAN_TYPE) {
            codeFormat.setText("CODE39");
        } else if (hmsScan.getScanType() == HmsScan.CODE128_SCAN_TYPE) {
            codeFormat.setText("CODE128");

        } else if (hmsScan.getScanType() == HmsScan.EAN13_SCAN_TYPE) {
            codeFormat.setText("EAN13 code");

        } else if (hmsScan.getScanType() == HmsScan.EAN8_SCAN_TYPE) {
            codeFormat.setText("EAN8 code");

        } else if (hmsScan.getScanType() == HmsScan.ITF14_SCAN_TYPE) {
            codeFormat.setText("ITF14 code");

        } else if (hmsScan.getScanType() == HmsScan.UPCCODE_A_SCAN_TYPE) {
            codeFormat.setText("UPCCODE_A");

        } else if (hmsScan.getScanType() == HmsScan.UPCCODE_E_SCAN_TYPE) {
            codeFormat.setText("UPCCODE_E");

        } else if (hmsScan.getScanType() == HmsScan.CODABAR_SCAN_TYPE) {
            codeFormat.setText("CODABAR");
        }
        if (hmsScan.getScanType() == HmsScan.QRCODE_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.PURE_TEXT_FORM) {
                resultType.setText("Text");
            } else if (hmsScan.getScanTypeForm() == HmsScan.EVENT_INFO_FORM) {
                resultType.setText("Event");
            } else if (hmsScan.getScanTypeForm() == HmsScan.CONTACT_DETAIL_FORM) {
                resultType.setText("Contact");
            } else if (hmsScan.getScanTypeForm() == HmsScan.DRIVER_INFO_FORM) {
                resultType.setText("License");
            } else if (hmsScan.getScanTypeForm() == HmsScan.EMAIL_CONTENT_FORM) {
                resultType.setText("Email");
            } else if (hmsScan.getScanTypeForm() == HmsScan.TEL_PHONE_NUMBER_FORM) {
                resultType.setText("Tel");
            } else if (hmsScan.getScanTypeForm() == HmsScan.SMS_FORM) {
                resultType.setText("SMS");

            } else if (hmsScan.getScanTypeForm() == HmsScan.WIFI_CONNECT_INFO_FORM) {
                resultType.setText("Wi-Fi");

            } else if (hmsScan.getScanTypeForm() == HmsScan.URL_FORM) {
                resultType.setText("WebSite");

            } else if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                resultType.setText("Product");

            } else {
                resultType.setText("Text");
            }
        } else if (hmsScan.getScanType() == HmsScan.EAN13_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.ISBN_NUMBER_FORM) {
                resultType.setText("ISBN");
            } else if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                resultType.setText("Product");
            } else {
                resultType.setText("Text");
            }
        } else if (hmsScan.getScanType() == HmsScan.EAN8_SCAN_TYPE || hmsScan.getScanType() == HmsScan.UPCCODE_A_SCAN_TYPE
                || hmsScan.getScanType() == HmsScan.UPCCODE_E_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                resultType.setText("Product");
            } else {
                resultType.setText("Text");
            }
        } else {
            resultType.setText("Text");
        }
    }

    protected int getStatusBarHeight() {
        int result = 0;
        // Obtain the ID.
        if (getResources() != null) {
            int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
            if (resourceId > 0) {
                result = getResources().getDimensionPixelSize(resourceId);
            }
        }
        return result;
    }
}
