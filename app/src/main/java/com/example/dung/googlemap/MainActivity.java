package com.example.dung.googlemap;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_GPS = 100;

    private SupportMapFragment frgGooGleMap;
    private MapManager mapManager;

    private boolean isMapResdy;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        getActionBar().hide();

        init();


    }

    private void init() {//ve lam trong suot thnah stas bar

        frgGooGleMap = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.frgGoogleMap);
        frgGooGleMap.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {//doi ai map tai thanh cong len
                //o daay nhan doi tuong googlemap dequan ly
                isMapResdy = true;
                mapManager = new MapManager(MainActivity.this, googleMap);
                mapManager.startLocationUpdate();

            }
        });

    }


    public void requestOpenGps(ResolvableApiException e) {

        ResolvableApiException resolvableApiException = (ResolvableApiException) e;//doan nay bat len cua activity

        try {
            resolvableApiException.startResolutionForResult(this, REQUEST_CODE_GPS);//tham so 2 la request code
        } catch (IntentSender.SendIntentException e1) {

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_GPS) {
            if (requestCode == Activity.RESULT_OK) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mapManager.onMyLocationButtonClick();
                    }
                }, 5000);
//                mapManager.onMyLocationButtonClick();//goi lai su kien clickde no  bat den vi tri cua minh
            } else {
                Toast.makeText(MainActivity.this, "Bat di...", Toast.LENGTH_SHORT).show();

            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isMapResdy) {

            mapManager.startLocationUpdate();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapManager.stopLocationUpdate();
    }

// c1: obj bien thanh String(json o dang sTring)   Marker windown o day loi STring ra  thanh obj thu vien Gson
    //c2: setTag
}
