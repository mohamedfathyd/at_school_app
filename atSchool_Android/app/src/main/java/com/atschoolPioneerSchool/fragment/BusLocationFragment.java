package com.atschoolPioneerSchool.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class BusLocationFragment extends Fragment {

    BusLocationFragment.GetBusLocationTask myTask = null;

    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;

    GoogleMap googleMap;
    ArrayList<Marker> markers = new ArrayList<>();
    ArrayList<LatLng> LatLngList = new ArrayList<>();
    ArrayList<String> NamesList = new ArrayList<>();
    ArrayList<String> StudentImageList = new ArrayList<>();

    Polyline line = null;

    View view;
    public static final String EXTRA_OBJCT = "com.atschoolPioneerSchool.ITEM";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bus_location, container, false);
        Activity host = ((Activity) view.getContext());
        MapFragment map = ((MapFragment) host.getFragmentManager().findFragmentById(R.id.map));

        sharedpref = getContext().getSharedPreferences("atSchool", Context.MODE_PRIVATE);

        map.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {
                googleMap = map;

                //fill data
                if (!isNetworkAvailable(getActivity().getBaseContext())) {
                    Toast.makeText(getActivity().getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
                } else {
                    myTask = new GetBusLocationTask();
                    myTask.execute("");
                }
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (myTask != null) {
            myTask.cancel(true);
        }
    }

    private class GetBusLocationTask extends AsyncTask<String, String, String> {

        String loginUserMasterId = sharedpref.getString("USER_MASTER_Id", "").trim();
        private int cntall;
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        String HomeLAT = "";
        String HomeLNG = "";
        String Trip_Date = "";
        String StepIn_Time = "";

        public GetBusLocationTask() {


        }

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            json_code = "";
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(1000);


                try {
                    Calendar c = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
                    String tDate = df.format(c.getTime()).replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                            .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();

                    String tag[] = {"events", "USER_MASTER_Id", "Trip_Date"};
                    String value[] = {"42", loginUserMasterId, tDate};

                    String url = getResources().getString(R.string.Web_URL);
                    json_code = new post_connection_json().makePostRequest(url, tag, value);

                } catch (Exception e) {
                    e.printStackTrace();

                    return e.getMessage();
                }

                try {
                    if (!json_code.equals("")) {

                        json_code = json_code.replace("\\", "/");
                        JSONObject json;

                        if (json_code.length() > 0) {

                            jsonArray = new JSONArray(json_code);
                            cntall = jsonArray.length();

                            int i;

                            for (i = 0; i < cntall; i++) {

                                try {
                                    json = jsonArray.getJSONObject(i);

                                    //   Id	Student_Id	Trip_Date	StepIn_Time	Buslat	BusLng	HomeLAT	HomeLNG	StudentImageName

                                    String Id = json.getString("Id");
                                    String Student_Id = json.getString("Student_Id");
                                    Trip_Date = json.getString("Trip_Date");
                                    StepIn_Time = json.getString("StepIn_Time");
                                    String Buslat = json.getString("Buslat");
                                    String BusLng = json.getString("BusLng");
                                    HomeLAT = json.getString("HomeLAT");
                                    HomeLNG = json.getString("HomeLNG");
                                    String StudentImageName = json.getString("StudentImageName");
                                    String FName = json.getString("FName");
                                    String FNameA = json.getString("FNameA");

                                    if (i == 0) {
                                        if (!HomeLAT.equals("") && !HomeLNG.equals("")) {
                                            NamesList.add("Home");
                                            StudentImageList.add("");
                                            LatLng ll = new LatLng(Double.valueOf(HomeLAT), Double.valueOf(HomeLNG));
                                            LatLngList.add(ll);
                                        }
                                    }

                                    if (!Id.equals("0") && !Buslat.equals("") && !BusLng.equals("")) {
                                        NamesList.add(FName + " " + Trip_Date + " : " + StepIn_Time);
                                        StudentImageList.add("StudentImageName");

                                        LatLng ll = new LatLng(Double.valueOf(Buslat) + (0.00400111 * i), Double.valueOf(BusLng) + (0.00400111 * i));
                                        LatLngList.add(ll);

                                    } else {

                                        return getString(R.string.msgFaildLogin);
                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    return e.getMessage();
                                }
                            }
                        } else {

                            return getString(R.string.msgFaildLogin);
                        }

                    } else {
                        return getString(R.string.msgFaildLogin);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                    return e.getMessage();
                }

            } catch (
                    InterruptedException e)

            {
                e.printStackTrace();
                return e.getMessage();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            try {

                for (int i = 0; i < markers.size(); i++) {
                    markers.get(i).remove();
                }
                markers.clear();

                if (line != null) {
                    line.remove();
                    line = null;
                }

                PolylineOptions line1 = new PolylineOptions();
                line1.width(3);
                Marker previousMarker = null;

                //marker with an icon
                int height = 100;
                int width = 100;


                //Make Roundness in the Corners
                for (int i = 0; i < LatLngList.size(); i++) {
                    // LatLng ll = new LatLng(31.97233135 + (0.00100111 * i), 35.87905411 + (0.00100111 * i));
                    LatLng ll = LatLngList.get(i);
                    Marker marker = null;

                    if (i == 0 && !HomeLAT.equals("") && !HomeLNG.equals("")) {
                        //draw home location

                        Bitmap mbitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.homelocation)).getBitmap();
                        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
                        Canvas canvas = new Canvas(imageRounded);
                        Paint mpaint = new Paint();
                        mpaint.setAntiAlias(true);
                        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 100, 100, mpaint);// Round Image Corner 100 100 100 100

                        Bitmap b = imageRounded;
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);


                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(ll)
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                .title(getString(R.string.strHome)));
                        marker.showInfoWindow();

                    } else {
                        //marker with an icon
                        Bitmap mbitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.buslocationonmap)).getBitmap();


                        Bitmap imageRounded = Bitmap.createBitmap(mbitmap.getWidth(), mbitmap.getHeight(), mbitmap.getConfig());
                        Canvas canvas = new Canvas(imageRounded);
                        Paint mpaint = new Paint();
                        mpaint.setAntiAlias(true);
                        mpaint.setShader(new BitmapShader(mbitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
                        canvas.drawRoundRect((new RectF(0, 0, mbitmap.getWidth(), mbitmap.getHeight())), 100, 100, mpaint);// Round Image Corner 100 100 100 100

                        Bitmap b = imageRounded;
                        Bitmap smallMarker = Bitmap.createScaledBitmap(b, width, height, false);

                        marker = googleMap.addMarker(new MarkerOptions()
                                .position(ll)
                                .icon(BitmapDescriptorFactory.fromBitmap(smallMarker))
                                .title(NamesList.get(i)));
                        marker.showInfoWindow();
                    }
                    previousMarker = marker;
                    markers.add(marker);

                    // line1.add(marker.getPosition());
                    builder.include(ll);
                }


                line = googleMap.addPolyline(line1);

                if (markers.size() == 0) {
                    Toast.makeText(getContext(), "No Points Found", Toast.LENGTH_LONG).show();
                }


                LatLngBounds bounds = builder.build();

                googleMap.getUiSettings().setMapToolbarEnabled(true);
                googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 15));


            } catch (Exception x) {
                String c = x.getMessage();
            }

            super.onPostExecute(s);
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
