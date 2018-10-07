package com.example.dung.googlemap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import androidx.annotation.NonNull;

public class MapManager implements GoogleMap.OnMyLocationButtonClickListener {
    private static final String TAG = "MapManager";

    //xur lys o day nhung gi len quan den map su ly ben nay
    private GoogleMap googleMap;
    private Context context;
    private LocationRequest locationRequest;//add co them thu vien
    private LocationSettingsRequest.Builder builder;


    //b2
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location myLocation;
    private Marker myMarker;
    private LocationCallback locationCallback;


    public MapManager(Context context, GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.context = context;

        locationRequest = new LocationRequest();//doi tuong dung de requestLocation
        locationRequest.setInterval(10000);//la k   hoang thoi gian gia 2 lan cap nhat vi tri //gui len google roi google gui len ve tinh,la bao nhieu gia requet de hoi,10s la len google de xin
        locationRequest.setFastestInterval(3000);//thoi gian ngan nhat giua 2 la giu lieu moi de xua lys con 10 laan gui yeu cau len 1 lan,de may khoong bi qua tai ,khi nhan dc nhieu request qua
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//PRIORITY_HIGH_ACCURACY  day la thong so de request


        //b2
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);//tao doi tuong de lay location cuar chung ta

        configMap();
    }

    private void configMap() {//cau hinh

//        googleMap.getUiSettings().setCompassEnabled(false);//daay la an na ban di mac dinh no la true

        googleMap.getUiSettings().setMapToolbarEnabled(false);//an buton dieu huong bat snag map

        googleMap.getUiSettings().setMyLocationButtonEnabled(true);//nut vi tri cua minh tru se hien thi
        googleMap.getUiSettings().setZoomControlsEnabled(true);//co the zoom

//        b2
        googleMap.setInfoWindowAdapter(new MyInfoWindown());//cutom titlemap

        try {//them lenh nay de no cap location thì cai nut tren moi hoat dong

            googleMap.setMyLocationEnabled(true);//cung cap vi tri cua minh,can try cacth xin quyen
        } catch (SecurityException e) {

        }
        googleMap.setOnMyLocationButtonClickListener(this);//dNG KY


    }

    @Override//an ok la tu dong bat len can cai requet
    public boolean onMyLocationButtonClick() {//co gps//word khi da ban san location ,no da tim den vi tri cua minh
        if (Util.isGpsOpen(context)) {

            getMyLocation();//cho nay da co gps(di chuyen den vi tri cua minh va lay vi tri cua minh)

            return true;//no bat roi he thong lam tiesp ,
        } else {//bat GPS

            reqquestGps();
            return false;
        }
//return true;
//        return false;//false la de mình lam xong no lam iep lý tiep con true thì no dung ơ ngay cho  do
    }

    private void reqquestGps() {//bat dau request,neu bat roi k vao ray chay return face
        builder = new LocationSettingsRequest.Builder();//mang di request
        builder.addLocationRequest(locationRequest);
        SettingsClient settingsClient = LocationServices.getSettingsClient(context);//dang tao 1 requet vao may va lasy ra thong so

        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());//day la lenh cua thu vien

        task.addOnFailureListener(new OnFailureListener() {//lang nghe su kien bi fai kho k lay dc gps,boi nnhieu nhie nghuyen nhan 1 co the la chua bat,
            @Override
            public void onFailure(@NonNull Exception e) {//requye
                if (e instanceof ResolvableApiException) {//chua bat
                    ResolvableApiException resolvableApiException = (ResolvableApiException) e;//bat man hinh hien thi bat gps
                    ((MainActivity) context).requestOpenGps(resolvableApiException);
                }
            }
        });
    }


    //b2
    private void getMyLocation() {//khi da co gps
        try {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {

                    if (location != null) {//no co the =null
                        myLocation = location;
                        //Marker la ve len ui vi tri
                        showMarker();//khi da co vi tri
                        drawMap();
                    } else {
                        Log.d(TAG, "location null");
                    }
                }
            });
        } catch (SecurityException e) {//
            e.getMessage();
        }
    }

    private void showMarker() {//an nhieu lam se bi nhieu marker

        if (myMarker == null) {
            MarkerOptions options = new MarkerOptions();//1 option co 2 thu k thieu vi tri ,hinh anh
            options.position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));//vi tri kinh do vi do,no tra ve vi tri cuoi cung gps nhan dc ,neu
            options.title("dung123");
            options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
            //newu chua co thif sedc sinh ra bang cach add
            googleMap.addMarker(options);
//            myMarker.setTag("Hello");//co the truyen doi tuig vao duoi window marker getTag
        } else {
            myMarker.setPosition(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
        }
    }

    //lang nghe vi trri no thay doi
    public void startLocationUpdate() {
        if (locationCallback != null) {
            try {
                locationCallback = new LocationCallback() {//khoi tao dinh nghia (extent)

                    @Override
                    public void onLocationResult(LocationResult locationResult) {
                        super.onLocationResult(locationResult);
                        Log.d(TAG, "onLocationResult: ");
                        if (locationResult != null) {
                            Log.d(TAG, "onLocationResult: loaction!==null");
                            myLocation = locationResult.getLastLocation();
                            showMarker();//de show hinh len
                        }
                    }
                };
                fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);//dang ky voi he thong app lang nghe su thay doi cua location

            } catch (SecurityException e) {
                e.getMessage();

            }
        }
    }

    public void stopLocationUpdate() {

        if (locationCallback != null) {
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        }

    }

    private class MyInfoWindown implements GoogleMap.InfoWindowAdapter {//1 marker co 1 infoWindown,data phai dua vao Marke(du lieu) roi moi vao Window( UI )  de llay ra
        private LayoutInflater inflater;

        public MyInfoWindown() {
            inflater = LayoutInflater.from(context);
        }

        @Override
        public View getInfoWindow(Marker marker) {//thay the toan bo view cua lo thanh view cua minh

            return null;
        }

        @Override
        public View getInfoContents(Marker marker) {//thay the noi dung ben trong cua no thanh cua minh
//            String message = (String) marker.getTag();
            View view = inflater.inflate(R.layout.layout_info_window, null, false);
            TextView txtName = view.findViewById(R.id.txtName);
            txtName.setText(marker.getTitle());
//            txtName.setText(message);
            return view;
        }
    }

    private void drawMap() {
        CircleOptions options = new CircleOptions();
        options.center(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()));
        options.radius(1000);//ban kinh 1 km
        options.fillColor(Color.parseColor("#6df5ff"));//mau len ben trong
        options.strokeColor(Color.BLACK);

        googleMap.addCircle(options);
    }


}
