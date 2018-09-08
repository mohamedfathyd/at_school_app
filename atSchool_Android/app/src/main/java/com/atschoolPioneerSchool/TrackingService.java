package com.atschoolPioneerSchool;

import android.*;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.ContextCompat;

import com.atschoolPioneerSchool.data.Constant;

import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by OmarA on 26/11/2017.
 */

public class TrackingService extends Service {

    LocationManager mLocationManager;
    MyListener myListener = new MyListener();

    public TrackingService() {

        try {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                        // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 25, myListener);
                        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, myListener);
                    }
                }
            });
        } catch (Exception exc1) {
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        try {
            if (ContextCompat.checkSelfPermission(getBaseContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationManager.removeUpdates(myListener);
            }
        } catch (Exception exc1) {
        }
    }

    public void sendLocation(Location location) {
        try {
            if (location == null) {
                return;
            }

            if (!isNetworkAvailable(getBaseContext())) {
                return;
            }

            SharedPreferences sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
            SharedPreferences.Editor edt;

            final String LastValidUserId = sharedpref.getString("LastValidUserId", "");
            final String Track_Trans_Order_Id = sharedpref.getString("Track_Trans_Order_Id", "");
            final String Track_Trans_Trip_Id = sharedpref.getString("Track_Trans_Trip_Id", "");

            Calendar c = Calendar.getInstance();

            SimpleDateFormat dt = new SimpleDateFormat("HH:mm");
            final String arrivalTime = dt.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                    .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();


            SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
            final String arrivalDate = df.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                    .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();

            if (!Track_Trans_Trip_Id.equals("")) {

                final String lat = String.valueOf(location.getLatitude());
                final String lng = String.valueOf(location.getLongitude());

                edt = sharedpref.edit();
                edt.putString("LastCoordinateLat", lat);
                edt.putString("LastCoordinateLng", lng);

                edt.commit();

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {

                            //http://localhost:5149/API_Mobile.aspx?events=39&Device_Date=20181818&Device_Time=10:10&Track_Trans_Order_Id=1&Track_Trans_Trip_Id=1&Sender_User_Master_Id=3182&lat=31.99765675&lng=36.02306964

                            String url = getResources().getString(R.string.Web_URL);
                            HttpGet httpget = new HttpGet(url + "events=39&Device_Date=" + arrivalDate + "&Device_Time=" + arrivalTime
                                    + "&Track_Trans_Order_Id=" + Track_Trans_Order_Id + "&Track_Trans_Trip_Id=" + Track_Trans_Trip_Id +
                                    "&Sender_User_Master_Id=" + LastValidUserId + "&lat=" + lat + "&lng=" + lng);
                            ResponseHandler<String> responseHandler = new BasicResponseHandler();
                            new DefaultHttpClient().execute(httpget, responseHandler);
                        } catch (Exception xxx) {
                            String sdsd = xxx.getMessage();
                        }
                    }
                }).start();

            }
        } catch (Exception exc1) {
        }
    }

    public class MyListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            sendLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {


        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
        }


    }

    public boolean isNetworkAvailable(Context ctx) {
        if (ctx == null)
            return false;

        ConnectivityManager cm =
                (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}

