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
package com.example.scankitdemo.action;

import android.content.ContentValues;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.util.SparseArray;

import com.huawei.hms.ml.scan.HmsScan;

import java.util.ArrayList;


public class ContactInfoAction {
    private static final SparseArray<Integer> addressMap = new SparseArray();
    private static final SparseArray<Integer> phoneMap = new SparseArray<>();
    private static final SparseArray<Integer> emailMap = new SparseArray<>();

    static {
        addressMap.put(HmsScan.AddressInfo.OTHER_USE_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER);
        addressMap.put(HmsScan.AddressInfo.OFFICE_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_WORK);
        addressMap.put(HmsScan.AddressInfo.RESIDENTIAL_USE_TYPE, ContactsContract.CommonDataKinds.StructuredPostal.TYPE_HOME);

        phoneMap.put(HmsScan.TelPhoneNumber.OTHER_USE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);
        phoneMap.put(HmsScan.TelPhoneNumber.OFFICE_USE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_WORK);
        phoneMap.put(HmsScan.TelPhoneNumber.RESIDENTIAL_USE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_HOME);
        phoneMap.put(HmsScan.TelPhoneNumber.FAX_USE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_OTHER_FAX);
        phoneMap.put(HmsScan.TelPhoneNumber.CELLPHONE_NUMBER_USE_TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);

        emailMap.put(HmsScan.EmailContent.OTHER_USE_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_OTHER);
        emailMap.put(HmsScan.EmailContent.OFFICE_USE_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_WORK);
        emailMap.put(HmsScan.EmailContent.RESIDENTIAL_USE_TYPE, ContactsContract.CommonDataKinds.Email.TYPE_HOME);
    }

    public static Intent getContactInfoIntent(HmsScan.ContactDetail contactInfo) {
        Intent intent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);
        try {
            intent.putExtra(ContactsContract.Intents.Insert.NAME, contactInfo.getPeopleName().getFullName());
            intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, contactInfo.getTitle());
            intent.putExtra(ContactsContract.Intents.Insert.COMPANY, contactInfo.getCompany());
            ArrayList<ContentValues> data = new ArrayList<>();
            data.addAll(getAddresses(contactInfo));
            data.addAll(getPhones(contactInfo));
            data.addAll(getEmails(contactInfo));
            data.addAll(getUrls(contactInfo));
            intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data);
        } catch (NullPointerException e) {
            Log.w("getCalendarEventIntent", e);
        }
        return intent;
    }

    private static ArrayList<ContentValues> getAddresses(HmsScan.ContactDetail contactInfo) {
        ArrayList<ContentValues> data = new ArrayList<>();
        if ((contactInfo.getAddressesInfos() != null)) {
            for (HmsScan.AddressInfo address : contactInfo.getAddressesInfos()) {
                if (address.getAddressDetails() != null) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredPostal.CONTENT_ITEM_TYPE);
                    StringBuilder addressBuilder = new StringBuilder();
                    for (String addressLine : address.getAddressDetails()) {
                        addressBuilder.append(addressLine);
                    }
                    contentValues.put(ContactsContract.CommonDataKinds.StructuredPostal.FORMATTED_ADDRESS, addressBuilder.toString());
                    Integer type = addressMap.get(address.getAddressType());
                    contentValues.put(ContactsContract.CommonDataKinds.StructuredPostal.TYPE,
                            type != null ? type : ContactsContract.CommonDataKinds.StructuredPostal.TYPE_OTHER);
                    data.add(contentValues);
                }
            }
        }
        return data;
    }

    private static ArrayList<ContentValues> getPhones(HmsScan.ContactDetail contactInfo) {
        ArrayList<ContentValues> data = new ArrayList<>();
        if ((contactInfo.getTelPhoneNumbers() != null)) {
            for (HmsScan.TelPhoneNumber phone : contactInfo.getTelPhoneNumbers()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
                contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phone.getTelPhoneNumber());
                Integer type = phoneMap.get(phone.getUseType());
                contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, type != null ? type : ContactsContract.CommonDataKinds.Phone.TYPE_OTHER);
                data.add(contentValues);
            }
        }
        return data;
    }

    private static ArrayList<ContentValues> getEmails(HmsScan.ContactDetail contactInfo) {
        ArrayList<ContentValues> data = new ArrayList<>();
        if ((contactInfo.getEmailContents() != null)) {
            for (HmsScan.EmailContent email : contactInfo.getEmailContents()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Email.CONTENT_ITEM_TYPE);
                contentValues.put(ContactsContract.CommonDataKinds.Email.ADDRESS, email.getAddressInfo());
                Integer type = emailMap.get(email.getAddressType());
                contentValues.put(ContactsContract.CommonDataKinds.Email.TYPE, type != null ? type : ContactsContract.CommonDataKinds.Email.TYPE_OTHER);
                data.add(contentValues);
            }
        }
        return data;
    }

    private static ArrayList<ContentValues> getUrls(HmsScan.ContactDetail contactInfo) {
        ArrayList<ContentValues> data = new ArrayList<>();
        if (contactInfo.getContactLinks() != null) {
            for (String url : contactInfo.getContactLinks()) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Website.CONTENT_ITEM_TYPE);
                contentValues.put(ContactsContract.CommonDataKinds.Website.URL, url);
                data.add(contentValues);
            }
        }
        return data;
    }
}