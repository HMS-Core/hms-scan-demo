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

import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.provider.CalendarContract;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.huawei.hms.ml.scan.HmsScan;


public class CalendarEventAction {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static Intent getCalendarEventIntent(HmsScan.EventInfo calendarEvent) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        try {
            intent.setData(CalendarContract.Events.CONTENT_URI)
                    .putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, getTime(calendarEvent.beginTime))
                    .putExtra(CalendarContract.EXTRA_EVENT_END_TIME, getTime(calendarEvent.closeTime))
                    .putExtra(CalendarContract.Events.TITLE, calendarEvent.getTheme())
                    .putExtra(CalendarContract.Events.DESCRIPTION, calendarEvent.getAbstractInfo())
                    .putExtra(CalendarContract.Events.EVENT_LOCATION, calendarEvent.getPlaceInfo())
                    .putExtra(CalendarContract.Events.ORGANIZER, calendarEvent.getSponsor())
                    .putExtra(CalendarContract.Events.STATUS, calendarEvent.getCondition());
        } catch (NullPointerException e) {
            Log.w("getCalendarEventIntent", e);
        }
        return intent;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static long getTime(HmsScan.EventTime calendarDateTime) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(calendarDateTime.getYear(), calendarDateTime.getMonth() - 1, calendarDateTime.getDay(),
                calendarDateTime.getHours(), calendarDateTime.getMinutes(), calendarDateTime.getSeconds());
        return calendar.getTime().getTime();
    }
}
