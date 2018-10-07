package com.example.dung.googlemap;

import android.content.Context;
import android.location.LocationManager;


public class Util {//chek xem gps da dc bat hay chua
    public static boolean isGpsOpen(Context context) {//new bta roi thi tim vi tri con chua bat thif bat len
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);//neu khac null va dc dc kich hoat
    }

}
