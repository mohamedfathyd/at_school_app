package com.atschoolPioneerSchool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.atschoolPioneerSchool.adapter.ReceiptVoucherListAdapter;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.ReceiptVoucher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class activity_school_vouchers extends AppCompatActivity {

    GetDataTask myTask = null;
    private RecyclerView recyclerView;
    public ReceiptVoucherListAdapter mAdapter;
    private ProgressBar progressBar;
    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;
    public String username;
    public String pass;
    public String AuthenticationKey;
    public String GUARDIAN_NO;
    public static final String EXTRA_OBJCT = "com.atschoolPioneerSchool.ITEM";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_vouchers);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // animation transition
        ViewCompat.setTransitionName(findViewById(R.id.image), EXTRA_OBJCT);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG).setAction("Action", null).show();

                //back to main menu
                onBackPressed();
            }
        });

        this.setTitle(getString(R.string.strVouchers));

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);

        username = sharedpref.getString("LastValidUserName", "").trim();
        pass = sharedpref.getString("LastValidPassword", "").trim();
        AuthenticationKey = sharedpref.getString("AuthenticationKey", "").trim();
        GUARDIAN_NO = sharedpref.getString("ResidencyNO", "").trim();

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);


        //fill data
        if (!isNetworkAvailable(getBaseContext())) {
            Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
        } else {
            myTask = new activity_school_vouchers.GetDataTask(this);
            myTask.execute("");
        }
    }

    @Override
    public void onBackPressed() {
        if (myTask != null) {
            myTask.cancel(true);
        }
        //close activity
        finish();
    }

    private class GetDataTask extends AsyncTask<String, ReceiptVoucher, String> {

        private Context mContext;
        private int cntall;
        private List<ReceiptVoucher> items = new ArrayList<>();
        private double Balance = 0;

        //http://schoolrootweb.controporal.com/API_Mobile.aspx?events=4&username=omar1&pass=omar1&AuthenticationKey=604531&GUARDIAN_NO=1022088460
        String tag[] = {"events", "username", "pass", "AuthenticationKey", "GUARDIAN_NO"};
        String value[] = {"5", username, pass, AuthenticationKey, GUARDIAN_NO};

        final SharedPreferences.Editor edt = sharedpref.edit();

        public GetDataTask(Context context) {
            mContext = context;

            //set Balance in title
            //  ((activity_school_vouchers) mContext).setTitle(" كشف حساب " + "    " + " الرصيد " + Balance);
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            super.onPreExecute();

            json_code = "";

            edt.commit();
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                if (isCancelled()) {
                    return null;
                }

                try {
                    //http://schoolrootweb.controporal.com/API_Mobile.aspx?events=1&username=omar1&pass=omar1
                    String url = getResources().getString(R.string.Web_URL);
                    json_code = new post_connection_json().makePostRequest(url, tag, value);

                } catch (Exception e) {
                    e.printStackTrace();

                    Toast.makeText(getBaseContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();

                }

                try {

                    if (!json_code.equals("")) {

                        json_code = json_code.replace("\\", "/");
                        jsonArray = new JSONArray(json_code);
                        cntall = jsonArray.length();
                        JSONObject json;
                        if (jsonArray.length() > 0) {

                            int i;
                            double Message_Id = 0;

                            for (i = 0; i < cntall; i++) {

                                if (isCancelled()) {
                                    return null;
                                }

                                try {
                                    json = jsonArray.getJSONObject(i);

                                    Message_Id = json.getDouble("Message_Id");
                                    String msg = json.getString("msg");
                                    String msgA = json.getString("msgA");

                                    if (Message_Id > 0) {
                                        return msgA;

                                    } else {

                                        ReceiptVoucher objReceiptVoucher = new ReceiptVoucher(i + 1, 0, "", "", json.getDouble("TRANS_AMOUNT_DEBIT"), json.getDouble("TRANS_AMOUNT_CREDIT")
                                                , json.getDouble("TOTAL_TRANS_AMOUNT_DEBIT"), json.getDouble("TOTAL_TRANS_AMOUNT_CREDIT")
                                                , json.getString("ACC_TRANS_TYPE_NAME")
                                                , json.getString("ACC_TRANS_TYPE_NAMEA")
                                                , json.getString("Date_G"), json.getString("Book_Voucher_No"), false);

                                        items.add(objReceiptVoucher);

                                        //set Balance in title
                                        if (i == 0) {
                                            Balance = json.getDouble("TOTAL_TRANS_AMOUNT_CREDIT");

                                            // publishProgress(objReceiptVoucher);

                                        }
                                       /* try {
                                            Thread.sleep(10);
                                        } catch (Exception ex) {
                                        }*/
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            //add last row the balance
                            if (Message_Id == 0) {
                                ReceiptVoucher objReceiptVoucher = new ReceiptVoucher(i + 1, 0, "", "", 0, 0, 0, Balance, "", "", "", "", true);

                                items.add(objReceiptVoucher);
                            }

                        } else {
                            Toast.makeText(getBaseContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(getBaseContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();

                        return String.valueOf(R.string.msgFaildLogin);
                    }
                } catch (JSONException e) {
                    Toast.makeText(getBaseContext(), R.string.msgServiceisnotavailable, Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }


            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(ReceiptVoucher... values) {


        }


        @Override
        protected void onPostExecute(String msg) {

            progressBar.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            mAdapter = new ReceiptVoucherListAdapter(mContext, items);
            recyclerView.setAdapter(mAdapter);


            if (msg != null) {
                if (!msg.isEmpty()) {
                    // Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();

                    if (msg.equals("Authentication key is wrong") || msg.equals("رمز التأكيد خاطأ")) {
                        Intent i = new Intent(getApplicationContext(), ActivityLogin.class);
                        startActivity(i);
                    }
                }
            }


            super.onPostExecute(msg);
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
