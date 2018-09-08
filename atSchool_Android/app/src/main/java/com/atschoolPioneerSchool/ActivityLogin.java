package com.atschoolPioneerSchool;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.data.Tools;
import com.atschoolPioneerSchool.data.post_connection_json;
import com.atschoolPioneerSchool.model.Student;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ActivityLogin extends AppCompatActivity {
    private EditText txtuserName, txtpassword;
    private TextInputLayout inputLayoutEmail, inputLayoutPassword;
    private Button btnLogin;
    private ProgressBar progressBar;
    private View parent_view;
    private CheckBox remember_me;

    private String json_code;
    private JSONArray jsonArray;
    private SharedPreferences sharedpref;
    private SharedPreferences.Editor edt;
    Switch switchLang;
    String _switchLang = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        parent_view = findViewById(android.R.id.content);

        inputLayoutEmail = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputLayoutPassword = (TextInputLayout) findViewById(R.id.input_layout_password);
        txtuserName = (EditText) findViewById(R.id.input_email);
        txtpassword = (EditText) findViewById(R.id.input_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        txtuserName.addTextChangedListener(new MyTextWatcher(txtuserName));
        txtpassword.addTextChangedListener(new MyTextWatcher(txtpassword));
        remember_me = (CheckBox) findViewById(R.id.remember_me);
        _switchLang= getResources().getConfiguration().locale.getLanguage();

        sharedpref = getSharedPreferences("atSchool", Context.MODE_PRIVATE);
        edt = sharedpref.edit();

        if (sharedpref.getString("remember_me", "").trim().equals("1")) {
            remember_me.setChecked(true);
        } else {
            remember_me.setChecked(false);
        }
        if (_switchLang.equals("ar")) {

            Constant.isArabic = true;
            edt.putString("switchLang", "ar");
            Constant.InterfaceLang = "ar";
            edt.commit();
            ChangeLanguage("ar");

        }
        else if(_switchLang.equals("fr")) {
            Constant.isArabic = false;
            edt.putString("switchLang", "fr");
            Constant.InterfaceLang = "fr";
            edt.commit();
            ChangeLanguage("fr");
        }
        else {

            Constant.isArabic = false;
            edt.putString("switchLang", "en");
            Constant.InterfaceLang = "en";
            edt.commit();
            ChangeLanguage("en");
        }

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //hide key board
                getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                hideKeyboard();

                submitForm();
            }
        });
        // for system bar in lollipop
        Tools.systemBarLolipop(this);

        _switchLang = sharedpref.getString("switchLang", "").trim();
       // Constant.InterfaceLang = _switchLang;

        txtuserName.setText(sharedpref.getString("LastValidUserName", "").trim());
        txtpassword.setText(sharedpref.getString("LastValidPassword", "").trim());


        switchLang = (Switch) findViewById(R.id.switchLang);
        switchLang.setVisibility(View.GONE);
        if (Constant.isArabic) {
            switchLang.setChecked(true);
        } else {
            switchLang.setChecked(false);
        }



        //hide key board
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        //if user and password entered login
        if (!String.valueOf(txtuserName.getText()).trim().isEmpty() && !String.valueOf(txtpassword.getText()).trim().isEmpty()) {
            if (Constant.runFirstOne) {

                btnLogin.performClick();
                Constant.runFirstOne = false;
            }
        }

    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void submitForm() {


        if (!validateEmail()) {
            return;
        }

        if (!validatePassword()) {
            return;
        }

        if (!isNetworkAvailable(getBaseContext())) {
            Toast.makeText(getBaseContext(), R.string.msgInternetNotAvailable, Toast.LENGTH_SHORT).show();
        } else {
            new LoginTask().execute("");
        }
    }

    private boolean validateEmail() {
        String email = txtuserName.getText().toString().trim();

        //save userName when change language to get the last entry
        edt.putString("LastValidUserName", email);
        edt.commit();


        if (email.isEmpty()) {
            inputLayoutEmail.setError(getString(R.string.err_msg_email));
            requestFocus(txtuserName);
            return false;
        } else {
            inputLayoutEmail.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validatePassword() {

        //save pass when change language to get the last entry
        edt.putString("LastValidPassword", txtpassword.getText().toString().trim());
        edt.commit();


        if (txtpassword.getText().toString().trim().isEmpty()) {
            inputLayoutPassword.setError(getString(R.string.err_msg_password));
            requestFocus(txtpassword);
            return false;
        } else if (txtpassword.getText().length() < 3) {
            inputLayoutPassword.setError(getString(R.string.inv_msg_password));
            requestFocus(txtpassword);
            return false;
        } else {
            inputLayoutPassword.setErrorEnabled(false);
        }

        return true;
    }

    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.input_email:
                    validateEmail();
                    break;
                case R.id.input_password:
                    validatePassword();
                    break;
            }
        }
    }

    private class LoginTask extends AsyncTask<String, String, String> {

        String userName = String.valueOf(txtuserName.getText()).trim().replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();

        String Pass = String.valueOf(txtpassword.getText()).trim().replaceAll("٠", "0").replaceAll("١", "1").replaceAll("٢", "2").replaceAll("٣", "3").replaceAll("٤", "4")
                .replaceAll("٥", "5").replaceAll("٦", "6").replaceAll("٧", "7").replaceAll("٨", "8").replaceAll("٩", "9").trim();

        String gcm_code = "";
        private int cntall;

        //new Code
        final SharedPreferences.Editor edt = sharedpref.edit();

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            btnLogin.setVisibility(View.GONE);
            super.onPreExecute();


            json_code = "";

            edt.putString("UserName", String.valueOf(txtuserName.getText()));

            if (remember_me.isChecked()) {
                edt.putString("Password", String.valueOf(txtpassword.getText()));
                edt.putString("remember_me", "1");
            } else {
                edt.putString("Password", "");
                edt.putString("remember_me", "0");
            }

            edt.commit();

        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Thread.sleep(1000);

                try {
                    //get GCM Token
                    InstanceID instanceID = InstanceID.getInstance(getBaseContext());
                    gcm_code = instanceID.getToken(getBaseContext().getString(R.string.gcm_defaultSenderId), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);

                } catch (Exception e) {
                    e.printStackTrace();
                    Snackbar.make(parent_view, "Error when get token", Snackbar.LENGTH_SHORT).show();

                }

                try {

                    String tag[] = {"events", "username", "pass", "GCM_Token", "APNS_Token"};
                    String value[] = {"1", userName, Pass, gcm_code, ""};


                    //http://schoolrootweb.controporal.com/API_Mobile.aspx?events=1&username=962795509099&pass=123
                    String url = getResources().getString(R.string.Web_URL);
                    json_code = new post_connection_json().makePostRequest(url, tag, value);

                } catch (Exception e) {
                    e.printStackTrace();

                    return e.getMessage();
                }

                try {

                    Constant.StudentList.clear();

                    if (!json_code.equals("")) {

                        json_code = json_code.replace("\\", "/");

                        JSONObject json;
                        String USER_MASTER_Id = "0";

                        if (json_code.length() > 0) {

                            jsonArray = new JSONArray(json_code);
                            cntall = jsonArray.length();


                            int i;

                            for (i = 0; i < cntall; i++) {

                                try {
                                    json = jsonArray.getJSONObject(i);


                                    String Message_Id = json.getString("Message_Id");
                                    USER_MASTER_Id = json.getString("USER_MASTER_Id");
                                    String NickName = json.getString("NickName");
                                    String AuthenticationKey = json.getString("AuthenticationKey");
                                    String msgA = json.getString("msgA");
                                    String msg = json.getString("msg");
                                    String ResidencyNO = json.getString("ResidencyNO");
                                    String Name = json.getString("Name");
                                    String NameA = json.getString("NameA");
                                    String ProfileId = json.getString("ProfileId");
                                    String Employee_Id = json.getString("Employee_Id");

                                    String USER_TYPE_Id = json.getString("USER_TYPE_Id");
                                    Constant.USER_TYPE_Id = USER_TYPE_Id;

                                    String School_Id = json.getString("School_Id");
                                    String GCM_Token = json.getString("GCM_Token");
                                    String SchoolFaceBook = json.getString("SchoolFaceBook");
                                    String SchoolWebsite = json.getString("SchoolWebsite");
                                    String SchoolYouTube = json.getString("SchoolYouTube");
                                    String ViewReceiptVoucherInMobile = json.getString("ViewReceiptVoucherInMobile");
                                    String ViewAccountStatementInMobile = json.getString("ViewAccountStatementInMobile");
                                    String ViewMaintenanceInMobile = json.getString("ViewMaintenanceInMobile");
                                    String ViewCallSonsInMobile = json.getString("ViewCallSonsInMobile");
                                    String Use_Maintenance_As_Responsible = json.getString("Use_Maintenance_As_Responsible");
                                    String Use_Suggestions_As_Responsible = json.getString("Use_Suggestions_As_Responsible");
                                    String Use_Complaint_As_Responsible = json.getString("Use_Complaint_As_Responsible");
                                    String Use_Needs_Orders_As_Responsible = json.getString("Use_Needs_Orders_As_Responsible");


                                    String ViewNewsInMobile = json.getString("ViewNewsInMobile");
                                    String ViewBussesInMobile = json.getString("ViewBussesInMobile");
                                    String ViewCommunicationInMobile = json.getString("ViewCommunicationInMobile");
                                    String ViewStudentsInMobile = json.getString("ViewStudentsInMobile");
                                    String ViewImageGalleryInMobile = json.getString("ViewImageGalleryInMobile");
                                    String ViewSuggestionInMobile = json.getString("ViewSuggestionInMobile");
                                    String ViewFaceBookInMobile = json.getString("ViewFaceBookInMobile");
                                    String ViewYouTubeInMobile = json.getString("ViewYouTubeInMobile");
                                    String ViewWebsiteInMobile = json.getString("ViewWebsiteInMobile");
                                    String ViewNotificationInMobile = json.getString("ViewNotificationInMobile");
                                    String ViewComplaintInMobile = json.getString("ViewComplaintInMobile");
                                    String ViewMarefahInMobile = json.getString("ViewMarefahInMobile");
                                    String ViewBusLocationInMobile = json.getString("ViewBusLocationInMobile");
                                    String ViewPicarsImagesInMobile = json.getString("ViewPicarsImagesInMobile");
                                    String Use__Bus_Assistant = json.getString("Use__Bus_Assistant");
                                    String ViewWeeklyPlanDaysInMobile = json.getString("ViewWeeklyPlanDaysInMobile");
                                    String ViewWeeklyPlanSubjectsInMobile = json.getString("ViewWeeklyPlanSubjectsInMobile");

                                    String HomeLAT = json.getString("HomeLAT");
                                    String HomeLNG = json.getString("HomeLNG");

                                    if (!USER_MASTER_Id.equals("0")) {
                                        edt.putString("HomeLAT", HomeLAT);
                                        edt.putString("HomeLNG", HomeLNG);
                                        edt.putString("ViewWeeklyPlanDaysInMobile", ViewWeeklyPlanDaysInMobile);
                                        edt.putString("ViewWeeklyPlanSubjectsInMobile", ViewWeeklyPlanSubjectsInMobile);
                                        edt.putString("ViewPicarsImagesInMobile", ViewPicarsImagesInMobile);
                                        edt.putString("Use__Bus_Assistant", Use__Bus_Assistant);
                                        edt.putString("Use_Maintenance_As_Responsible", Use_Maintenance_As_Responsible);
                                        edt.putString("Use_Suggestions_As_Responsible", Use_Suggestions_As_Responsible);
                                        edt.putString("Use_Complaint_As_Responsible", Use_Complaint_As_Responsible);
                                        edt.putString("Use_Needs_Orders_As_Responsible", Use_Needs_Orders_As_Responsible);

                                        edt.putString("ViewReceiptVoucherInMobile", ViewReceiptVoucherInMobile);
                                        edt.putString("ViewAccountStatementInMobile", ViewAccountStatementInMobile);
                                        edt.putString("ViewMaintenanceInMobile", ViewMaintenanceInMobile);
                                        edt.putString("ViewCallSonsInMobile", ViewCallSonsInMobile);

                                        edt.putString("ViewNewsInMobile", ViewNewsInMobile);
                                        edt.putString("ViewBussesInMobile", ViewBussesInMobile);
                                        edt.putString("ViewCommunicationInMobile", ViewCommunicationInMobile);
                                        edt.putString("ViewStudentsInMobile", ViewStudentsInMobile);
                                        edt.putString("ViewImageGalleryInMobile", ViewImageGalleryInMobile);
                                        edt.putString("ViewSuggestionInMobile", ViewSuggestionInMobile);
                                        edt.putString("ViewFaceBookInMobile", ViewFaceBookInMobile);
                                        edt.putString("ViewYouTubeInMobile", ViewYouTubeInMobile);
                                        edt.putString("ViewWebsiteInMobile", ViewWebsiteInMobile);
                                        edt.putString("ViewNotificationInMobile", ViewNotificationInMobile);
                                        edt.putString("ViewComplaintInMobile", ViewComplaintInMobile);
                                        edt.putString("ViewMarefahInMobile", ViewMarefahInMobile);
                                        edt.putString("ViewBusLocationInMobile", ViewBusLocationInMobile);


                                        edt.putString("ResidencyNO", ResidencyNO);
                                        edt.putString("USER_MASTER_Id", USER_MASTER_Id);
                                        edt.putString("Password", Pass);
                                        edt.putString("LastValidPassword", Pass);
                                        edt.putString("LastValidUserName", userName);
                                        edt.putString("LastValidUserId", USER_MASTER_Id);
                                        edt.putString("NickName", NickName);
                                        edt.putString("ProfileId", ProfileId);
                                        edt.putString("AuthenticationKey", AuthenticationKey);
                                        edt.putString("Employee_Id", Employee_Id);
                                        edt.putString("USER_TYPE_Id", USER_TYPE_Id);
                                        edt.putString("School_Id", School_Id);
                                        edt.putString("GCM_Token", GCM_Token);
                                        edt.putString("SchoolFaceBook", SchoolFaceBook);
                                        edt.putString("SchoolWebsite", SchoolWebsite);
                                        edt.putString("SchoolYouTube", SchoolYouTube);

                                        //save kids information

                                        Student objStudent = new Student();

                                        if (_switchLang.equals("ar")) {

                                            edt.putString("Name", NameA);
                                            Constant.AccountName = NameA;

                                            objStudent.StuName = json.getString("FNameA");
                                            objStudent.StuNameA = json.getString("FName");
                                            objStudent.ClassName = json.getString("ClassNameA");
                                            objStudent.ClassNameA = json.getString("ClassName");
                                            objStudent.SectionName = json.getString("SectionNameA");
                                            objStudent.SectionNameA = json.getString("SectionName");
                                            objStudent.StudentId = json.getInt("StudentId");
                                            objStudent.ImagePath = json.getString("Profile_Picture");
                                            objStudent.StudentImageName = json.getString("StudentImageName");
                                            objStudent.USER_MASTER_Id_Student = json.getString("USER_MASTER_Id_Student");
                                            objStudent.ResidencyNo_Student = json.getString("ResidencyNo_Student");
                                            objStudent.setClassId(json.getString("ClassId"));
                                            objStudent.CLASS_SECTION_ID = json.getString("CLASS_SECTION_ID");
                                            objStudent.SectionId = json.getString("SectionId");


                                        } else {
                                            edt.putString("Name", Name);
                                            Constant.AccountName = Name;

                                            objStudent.StuName = json.getString("FName");
                                            objStudent.StuNameA = json.getString("FNameA");
                                            objStudent.ClassName = json.getString("ClassName");
                                            objStudent.ClassNameA = json.getString("ClassNameA");
                                            objStudent.SectionName = json.getString("SectionName");
                                            objStudent.SectionNameA = json.getString("SectionNameA");
                                            objStudent.StudentId = json.getInt("StudentId");
                                            objStudent.ImagePath = json.getString("Profile_Picture");
                                            objStudent.StudentImageName = json.getString("StudentImageName");
                                            objStudent.USER_MASTER_Id_Student = json.getString("USER_MASTER_Id_Student");
                                            objStudent.ResidencyNo_Student = json.getString("ResidencyNo_Student");
                                            objStudent.setClassId(json.getString("ClassId"));
                                            objStudent.CLASS_SECTION_ID = json.getString("CLASS_SECTION_ID");
                                            objStudent.SectionId = json.getString("SectionId");
                                        }


                                        Constant.StudentList.add(objStudent);

                                        edt.commit();


                                    } else {
                                        edt.putString("HomeLAT", "");
                                        edt.putString("HomeLNG", "");
                                        edt.putString("ProfileId", "");
                                        edt.putString("ResidencyNO", "");
                                        edt.putString("UserName", "");
                                        edt.putString("Password", "");
                                        edt.putString("LastValidPassword", "");
                                        edt.putString("USER_MASTER_Id", "");
                                        edt.putString("NickName", "");
                                        edt.putString("Name", "");
                                        edt.putString("NameA", "");
                                        edt.putString("AuthenticationKey", "");
                                        edt.putString("Employee_Id", "");
                                        edt.putString("USER_TYPE_Id", "");
                                        edt.putString("School_Id", "");
                                        edt.putString("SchoolFaceBook", "");
                                        edt.putString("SchoolWebsite", "");
                                        edt.putString("SchoolYouTube", "");

                                        edt.putString("ViewReceiptVoucherInMobile", "");
                                        edt.putString("ViewAccountStatementInMobile", "");
                                        edt.putString("ViewMaintenanceInMobile", "");
                                        edt.putString("ViewCallSonsInMobile", "");
                                        edt.putString("ViewPicarsImagesInMobile", "");
                                        edt.putString("Use__Bus_Assistant", "0");
                                        edt.putString("ViewWeeklyPlanDaysInMobile", "0");
                                        edt.putString("ViewWeeklyPlanSubjectsInMobile", "0");

                                        edt.commit();

                                        return getString(R.string.msgFaildLogin);
                                    }

                                } catch (JSONException e) {

                                    e.printStackTrace();
                                    return e.getMessage();
                                }
                            }

                            if (!USER_MASTER_Id.equals("0")) {

                                Intent inte = new Intent(ActivityLogin.this, ActivityMain.class);
                                finish();
                                startActivity(inte);
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
            progressBar.setVisibility(View.GONE);
            btnLogin.setVisibility(View.VISIBLE);
            Snackbar.make(parent_view, s, Snackbar.LENGTH_SHORT).show();

            hideKeyboard();

            //finish();
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

    public void ChangeLanguage(String lang) {

        _switchLang = lang;
        Locale myLocale = new Locale(lang);

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        conf.setLayoutDirection(myLocale);
       /* Intent refresh = new Intent(this, ActivityLogin.class);
        finish();
        startActivity(refresh);*/
    }

    @Override
    public void onBackPressed() {
        finish();
        System.exit(0);
    }

}
