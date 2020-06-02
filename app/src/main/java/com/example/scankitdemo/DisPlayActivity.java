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
import android.Manifest;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.provider.Settings;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;

import com.example.scankitdemo.action.CalendarEventAction;
import com.example.scankitdemo.action.ContactInfoAction;
import com.example.scankitdemo.action.DialAction;
import com.example.scankitdemo.action.EmailAction;
import com.example.scankitdemo.action.LocationAction;
import com.example.scankitdemo.action.SMSAction;
import com.example.scankitdemo.action.WifiAdmin;
import com.huawei.hms.hmsscankit.ScanUtil;
import com.huawei.hms.ml.scan.HmsScan;

public class DisPlayActivity extends Activity {
    //Define a view.
    private ImageView backBtn;
    private Button copyButton;
    private TextView codeFormat;
    private TextView resultType;
    private TextView rawResult;
    private ImageView icon;
    private TextView iconText;
    private TextView resultTypeTitle;
    private HmsScan.WiFiConnectionInfo wiFiConnectionInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_display);
        backBtn = findViewById(R.id.result_back_img_in);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisPlayActivity.this.finish();
            }
        });
        codeFormat = findViewById(R.id.barcode_type);
        icon = findViewById(R.id.diplay_icon);
        iconText = findViewById(R.id.diplay_text);
        resultType = findViewById(R.id.barcode_type_mon);
        rawResult = findViewById(R.id.barcode_rawValue);
        resultTypeTitle = findViewById(R.id.barcode_type_mon_t);
        copyButton = findViewById(R.id.button_operate);
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
            //Obtain the scanning result.
            HmsScan obj = getIntent().getParcelableExtra(ScanUtil.RESULT);
            try {
                valueFillIn(obj);
            } catch (Exception e) {

            }
        }
        Vibrator vibrator = (Vibrator)this.getSystemService(this.VIBRATOR_SERVICE);
        vibrator.vibrate(300);
    }

    private void valueFillIn(final HmsScan hmsScan) {
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

        //Show the barcode result.
        if (hmsScan.getScanType() == HmsScan.QRCODE_SCAN_TYPE) {
            resultType.setVisibility(View.VISIBLE);
            resultTypeTitle.setVisibility(View.VISIBLE);
            if (hmsScan.getScanTypeForm() == HmsScan.PURE_TEXT_FORM) {
                icon.setImageResource(R.drawable.text);
                iconText.setText("Text");
                resultType.setText("Text");
                copyButton.setText(getText(R.string.copy));
                copyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                            cm.setPrimaryClip(mClipData);
                            Toast.makeText(DisPlayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (hmsScan.getScanTypeForm() == HmsScan.EVENT_INFO_FORM) {
                icon.setImageResource(R.drawable.event);
                iconText.setText("Event");
                resultType.setText("Event");
                copyButton.setText(getText(R.string.add_calendar));
                copyButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        startActivity(CalendarEventAction.getCalendarEventIntent(hmsScan.getEventInfo()));
                        DisPlayActivity.this.finish();
                    }
                });
            } else if (hmsScan.getScanTypeForm() == HmsScan.CONTACT_DETAIL_FORM) {
                icon.setImageResource(R.drawable.contact);
                iconText.setText("Contact");
                resultType.setText("Contact");
                copyButton.setText(getText(R.string.add_contact));
                copyButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        startActivity(ContactInfoAction.getContactInfoIntent(hmsScan.getContactDetail()));
                        DisPlayActivity.this.finish();
                    }
                });
            } else if (hmsScan.getScanTypeForm() == HmsScan.DRIVER_INFO_FORM) {
                icon.setImageResource(R.drawable.text);
                iconText.setText("Text");
                resultType.setText("License");
                copyButton.setText(getText(R.string.copy));
                copyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                            cm.setPrimaryClip(mClipData);
                            Toast.makeText(DisPlayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else if (hmsScan.getScanTypeForm() == HmsScan.EMAIL_CONTENT_FORM) {
                icon.setImageResource(R.drawable.email);
                iconText.setText("Email");
                resultType.setText("Email");
                copyButton.setText(getText(R.string.send_email));
                copyButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        startActivity(Intent.createChooser(EmailAction.getEmailInfo(hmsScan.getEmailContent()), "Select email application."));
                        //startActivity(EmailAction.getContactInfoIntent(result.getEmailContent()));
                        DisPlayActivity.this.finish();
                    }
                });
            } else if (hmsScan.getScanTypeForm() == HmsScan.LOCATION_COORDINATE_FORM) {
                icon.setImageResource(R.drawable.location);
                iconText.setText("Location");
                resultType.setText("Location");
                if (LocationAction.checkMapAppExist(getApplicationContext())) {
                    copyButton.setText(getText(R.string.nivigation));
                    copyButton.setOnClickListener(new View.OnClickListener() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onClick(View v) {
                            try {
                                startActivity(LocationAction.getLoactionInfo(hmsScan.getLocationCoordinate()));
                                DisPlayActivity.this.finish();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                } else {
                    copyButton.setText(getText(R.string.copy));
                    copyButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                                cm.setPrimaryClip(mClipData);
                                Toast.makeText(DisPlayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            } else if (hmsScan.getScanTypeForm() == HmsScan.TEL_PHONE_NUMBER_FORM) {
                icon.setImageResource(R.drawable.tel);
                iconText.setText("Tel");
                resultType.setText("Tel");
                copyButton.setText(getText(R.string.call));
                copyButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        try {
                            startActivity(DialAction.getDialIntent(hmsScan.getTelPhoneNumber()));
                            DisPlayActivity.this.finish();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            } else if (hmsScan.getScanTypeForm() == HmsScan.SMS_FORM) {
                icon.setImageResource(R.drawable.sms);
                iconText.setText("SMS");
                resultType.setText("SMS");
                copyButton.setText(getText(R.string.send_sms));
                copyButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        startActivity(SMSAction.getSMSInfo(hmsScan.getSmsContent()));
                        DisPlayActivity.this.finish();
                    }
                });
            } else if (hmsScan.getScanTypeForm() == HmsScan.WIFI_CONNECT_INFO_FORM) {
                icon.setImageResource(R.drawable.wifi);
                iconText.setText("Wi-Fi");
                resultType.setText("Wi-Fi");
                copyButton.setText(getText(R.string.connect_network));
                wiFiConnectionInfo = hmsScan.wifiConnectionInfo;
                copyButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        String permissionWifi = Manifest.permission.ACCESS_WIFI_STATE;
                        String permissionWifi2 = Manifest.permission.CHANGE_WIFI_STATE;
                        String[] permission = new String[]{permissionWifi, permissionWifi2};
                        ActivityCompat.requestPermissions(DisPlayActivity.this, permission, CalendarEvent);
                    }
                });

            } else if (hmsScan.getScanTypeForm() == HmsScan.URL_FORM) {
                icon.setImageResource(R.drawable.website);
                iconText.setText("WebSite");
                resultType.setText("WebSite");
                copyButton.setText(getText(R.string.open_browser));
                copyButton.setOnClickListener(new View.OnClickListener() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onClick(View v) {
                        Uri webpage = Uri.parse(hmsScan.getOriginalValue());
                        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                        if (intent.resolveActivity(getPackageManager()) != null) {
                            startActivity(intent);
                        }
                    }
                });
                resultType.setText("WebSite");
            } else {
                icon.setImageResource(R.drawable.text);
                iconText.setText("Text");
                resultType.setText("Text");
                copyButton.setText(getText(R.string.copy));
                copyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                            ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                            cm.setPrimaryClip(mClipData);
                            Toast.makeText(DisPlayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } else if (hmsScan.getScanType() == HmsScan.EAN13_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.ISBN_NUMBER_FORM) {
                icon.setImageResource(R.drawable.isbn);
                iconText.setText("ISBN");
                resultType.setVisibility(View.VISIBLE);
                resultTypeTitle.setVisibility(View.VISIBLE);
                resultType.setText("ISBN");
            } else if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                icon.setImageResource(R.drawable.product);
                iconText.setText("Product");
                resultType.setVisibility(View.VISIBLE);
                resultTypeTitle.setVisibility(View.VISIBLE);
                resultType.setText("Product");
            } else {
                icon.setImageResource(R.drawable.text);
                iconText.setText("Text");
                resultType.setVisibility(View.GONE);
                resultTypeTitle.setVisibility(View.GONE);
            }
            copyButton.setText(getText(R.string.copy));
            copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                        //Obtain the clipboard manager.
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(DisPlayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else if (hmsScan.getScanType() == HmsScan.EAN8_SCAN_TYPE || hmsScan.getScanType() == HmsScan.UPCCODE_A_SCAN_TYPE
                || hmsScan.getScanType() == HmsScan.UPCCODE_E_SCAN_TYPE) {
            if (hmsScan.getScanTypeForm() == HmsScan.ARTICLE_NUMBER_FORM) {
                icon.setImageResource(R.drawable.product);
                iconText.setText("Product");
                resultType.setVisibility(View.VISIBLE);
                resultTypeTitle.setVisibility(View.VISIBLE);
                resultType.setText("Product");
            } else {
                icon.setImageResource(R.drawable.text);
                iconText.setText("Text");
                resultType.setVisibility(View.GONE);
                resultTypeTitle.setVisibility(View.GONE);
            }
            copyButton.setText(getText(R.string.copy));
            copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(DisPlayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            icon.setImageResource(R.drawable.text);
            iconText.setText("Text");
            copyButton.setText(getText(R.string.copy));
            copyButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (rawResult != null && !TextUtils.isEmpty(rawResult.getText())) {
                        ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData mClipData = ClipData.newPlainText("Label", rawResult.getText());
                        cm.setPrimaryClip(mClipData);
                        Toast.makeText(DisPlayActivity.this, getText(R.string.copy_toast), Toast.LENGTH_SHORT).show();
                    }
                }
            });
            resultType.setVisibility(View.GONE);
            resultTypeTitle.setVisibility(View.GONE);
        }
    }

    final int CalendarEvent = 0x3300;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CalendarEvent: {
                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    if (wiFiConnectionInfo != null) {
                        new WifiAdmin(DisPlayActivity.this).Connect(wiFiConnectionInfo.getSsidNumber(),
                                wiFiConnectionInfo.getPassword(), wiFiConnectionInfo.getCipherMode());
                        DisPlayActivity.this.startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        DisPlayActivity.this.finish();
                    }
                }
            }
            break;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStop() {
        super.onStop();
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
