package com.atschoolPioneerSchool.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.News;
import com.atschoolPioneerSchool.start_page;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyViewPagerAdapter extends PagerAdapter {
    GetDataTask myTask = null;
    Context context;
    LayoutInflater layoutInflater;
    RelativeLayout slide_layout;

    private String json_code;
    private String json_code1;
    private JSONArray jsonArray;
    private JSONArray jsonArray1;
    private String about, vision, mission, about_desc, vision_desc, mission_desc;

    public MyViewPagerAdapter(Context context) {
        this.context = context;
    } //main class
//-------------------------------------------------------------------------------------------------
    //arrays


    public String[] headers = { //HEADERS
            "عن المدرسة",
            "رؤيتنا",
            "مهمتنا"
    };

    public int[] images = { //IMAGES

            R.drawable.schoolbuild,
            R.drawable.ourvision,
            R.drawable.ourmessage
    };

    public String[] descr = { //descriptions
            about_desc,
            vision_desc,
            mission_desc
    };
//--------------------------------------------------------------------------------------------------
    //get count method

    @Override
    public int getCount() {
        return headers.length;
    }

    //--------------------------------------------------------------------------------------------------
    //clear view layout type
    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout) o;
    }
//--------------------------------------------------------------------------------------------------

    @SuppressLint("SetTextI18n")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {

        myTask = new GetDataTask(context.getApplicationContext());
        myTask.execute();

        SharedPreferences sharedPreferences=context.getSharedPreferences("mfile",Context.MODE_PRIVATE);
        vision = sharedPreferences.getString("vision_desc", "");//"vision_description"
        mission=sharedPreferences.getString("mission_desc","");



        //............................................................................................
        // inflate layout
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.slide1, container, false);
        //.............................................................................................
        //casting items

        ImageView imageView = (ImageView) view.findViewById(R.id.image_slider);
        TextView header = (TextView) view.findViewById(R.id.header_text);
        TextView desc = (TextView) view.findViewById(R.id.desc_text);
        slide_layout = (RelativeLayout) view.findViewById(R.id.slide_layout);


        Typeface T1;
        Typeface T2;
    T1 = Typeface.createFromAsset(context.getAssets(), "bon.otf");
        T2 = Typeface.createFromAsset(context.getAssets(), "jana.ttf");
        desc.setTypeface(T2);
        header.setTypeface(T1);

        //...................................................................................
        //change background color

        switch (position) {
            case 0:
                //slide_layout.setBackgroundResource(R.drawable.bg);
                desc.setText("في المدرسه التطبيق الذكي للتواصل الدائم\n" +
                        "       مع ابنائك في المدرسه\n" +
                        "\nwww.pexels.com"+"\nwww.atSchool.com" +
                        "\nV 1.23");
                //header.setText(vision_desc);

                break;
            case 1:
                //slide_layout.setBackgroundResource(R.drawable.bg);
                desc.setText(vision);
                break;
            case 2:
               // slide_layout.setBackgroundResource(R.drawable.bg);
                desc.setText(mission);
                break;
        }
        //......................................................................................
        //set resorce
        imageView.setImageResource(images[position]);
        header.setText(headers[position]);
        //desc.setText(descr[position]);

        //......................................................................................
        //load the view
        container.addView(view);
        //......................................................................................
        //return view
        return view;

    }//instantiateItem end

    //-------------------------------------------------------------------------------------------------
    //destroy view method
    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout) object);
    }


    //-------------------------------------------------------------------------------------------------
    //get data from db
    private class GetDataTask extends AsyncTask<String, News, String> {

        private Context mContext;
        private int cntall;
        private int cntall1;
        private List<News> items = new ArrayList<>();
        private double Balance = 0;




        public GetDataTask(Context context) {
            mContext = context;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            json_code = "";
            json_code1 = "";


        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                if (isCancelled()) {
                    return null;
                }

                try {
                    //9 : get complaints


                                //هيبعت للبوست كونكشن اللينك والفاليو
                                String tag[] = {"events", "type"};
                                String value[] = {"13", "5"};
                                String url = context.getApplicationContext().getResources().getString(R.string.Web_URL);
                                json_code = new post_connection_json().makePostRequest(url, tag, value);


                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();

                }

                try {


                    if (isCancelled()) {
                        return null;
                    }
                    //اختبر لو القيمة مش فاضية
                    if (!json_code.equals("")) {

                        json_code = json_code.replace("\\", "/");



                        jsonArray = new JSONArray(json_code);

                        cntall = jsonArray.length();
                        JSONObject json;

                        if (jsonArray.length() > 0) {

                            int i;

                            for (i = 0; i < cntall; i++) {

                                try {

                                    if (isCancelled()) {
                                        return null;
                                    }

                                    json = jsonArray.getJSONObject(i);
                                    String DSC;
                                    DSC=json.getString("Description").replaceAll("/n", " ");
                                    SharedPreferences sharedPreferences=context.getSharedPreferences("mfile",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString("vision_desc",DSC);
                                    editor.commit();



                                        //.........................................................................................................................
                                        News objNews = new News(json.getString("Id"), json.getString("Name"), json.getString("NameA"),

                                                json.getString("Description").replaceAll("/n", " "), json.getString("DescriptionA").replaceAll("/n", " ")
                                                , json.getString("Img1"),
                                                json.getString("published_Date"), json.getString("RowNumber"), "6", "Vision");

                                        items.add(objNews);



                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    return e.getMessage();
                                }
                            }

                        } else {
                            return context.getString(R.string.msgNoData);
                        }

                    } else {
                        return context.getString(R.string.msgNoData);
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                    return e.getMessage();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }

//........................................................................................................................
            try {

                if (isCancelled()) {
                    return null;
                }

                try {
                    //9 : get complaints


                    //هيبعت للبوست كونكشن اللينك والفاليو
                    String tag1[] = {"events", "type"};
                    String value1[] = {"13", "6"};
                    String url1 = context.getApplicationContext().getResources().getString(R.string.Web_URL);
                    json_code1 = new post_connection_json().makePostRequest(url1, tag1, value1);


                } catch (Exception e) {
                    e.printStackTrace();
                    return e.getMessage();

                }

                try {


                    if (isCancelled()) {
                        return null;
                    }
                    //اختبر لو القيمة مش فاضية
                    if (!json_code1.equals("")) {

                        json_code1 = json_code1.replace("\\", "/");



                        jsonArray1 = new JSONArray(json_code1);

                        cntall1 = jsonArray1.length();
                        JSONObject json1;

                        if (jsonArray1.length() > 0) {

                            int x;

                            for (x = 0; x < cntall; x++) {

                                try {

                                    if (isCancelled()) {
                                        return null;
                                    }

                                    json1 = jsonArray1.getJSONObject(x);
                                    String DSC1;
                                    DSC1=json1.getString("Description").replaceAll("/n", " ");
                                    SharedPreferences sharedPreferences=context.getSharedPreferences("mfile",Context.MODE_PRIVATE);
                                    SharedPreferences.Editor editor=sharedPreferences.edit();
                                    editor.putString("mission_desc",DSC1);
                                    editor.commit();



                                    //.........................................................................................................................
                                    News objNews = new News(json1.getString("Id"), json1.getString("Name"), json1.getString("NameA"),

                                            json1.getString("Description").replaceAll("/n", " "), json1.getString("DescriptionA").replaceAll("/n", " ")
                                            , json1.getString("Img1"),
                                            json1.getString("published_Date"), json1.getString("RowNumber"), "6", "Vision");

                                    items.add(objNews);



                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    return e.getMessage();
                                }
                            }

                        } else {
                            return context.getString(R.string.msgNoData);
                        }
                        //-----------------------------------------------------------------------------------------------------------------------

                        //-----------------------------------------------------------------------------------------------------------------------

                    } else {
                        return context.getString(R.string.msgNoData);
                    }
                } catch (JSONException e) {

                    e.printStackTrace();
                    return e.getMessage();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return e.getMessage();
            }
 //........................................................................................................................
            return null;

        }

        @Override
        protected void onProgressUpdate(News... values) {

        }

        @Override
        protected void onPostExecute(String msg) {
            if (!isCancelled()) {


                if (msg != null) {
                    if (!msg.isEmpty()) {
                        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();

                        if (msg.equals("Authentication key is wrong") || msg.equals("رمز التأكيد خاطأ")) {
                            Intent i = new Intent(context, start_page.class);
                            context.startActivity(i);
                        }
                    }
                }
            }

            super.onPostExecute(msg);
        }
    }
}


