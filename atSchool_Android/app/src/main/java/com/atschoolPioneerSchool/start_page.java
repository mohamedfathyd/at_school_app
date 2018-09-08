package com.atschoolPioneerSchool;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Locale;

public class start_page extends AppCompatActivity {
    private Button btn_enter,btn_about;
    private TextView txt;
    private Spinner spinner;
    ArrayAdapter<CharSequence> lang_adapter;
    private String selected_lang= null;

    private Locale Language;

    String _switchLang = "";
    int pos=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_page);
        btn_enter=(Button) findViewById(R.id.button_enter);
        btn_about=(Button)findViewById(R.id.button_about);
        txt=(TextView)findViewById(R.id.textView);
        spinner=(Spinner)findViewById(R.id.spinner2);
        lang_adapter=ArrayAdapter.createFromResource(this,R.array.languages,android.R.layout.simple_spinner_item);
        lang_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(lang_adapter);
        spinner.setBackground(getResources().getDrawable(R.drawable.spinerbg));

        //..............................................................................................

        Typeface T1;
        T1=Typeface.createFromAsset(getAssets(),"bon.otf");
        btn_about.setTypeface(T1);
        btn_enter.setTypeface(T1);
        txt.setTypeface(T1);

        //...............................................................................................

        btn_enter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selected_lang= spinner.getSelectedItem().toString();
                if(selected_lang.equals("أختر اللغة")){
                    selected_lang="en-us";
                }
                else if(selected_lang.equals("English")){
                    selected_lang="en-us";
                }
                else if(selected_lang.equals("عربي")){
                    selected_lang="ar";
                }
                else if(selected_lang.equals("french")){
                    selected_lang="fr";
                }
                Intent intent1=new Intent(start_page.this,ActivityLogin.class);
                intent1.putExtra("language",selected_lang);
                startActivity(intent1);
            }
        });
        pos =getIntent().getIntExtra("position",0);
        //..............................................................................................


        spinner.setSelection(pos);
        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Intent intent2=new Intent(start_page.this,intro_slider.class);
              startActivity(intent2);

            }
        });
        //.............................................................................................
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selected_lang= spinner.getSelectedItem().toString();
                if(selected_lang.equals("أختر اللغة")){
                    selected_lang="ar";
                }
                else if(selected_lang.equals("English")&&pos!=spinner.getSelectedItemPosition()){
                    selected_lang="en-us";
                    int pos= spinner.getSelectedItemPosition();
                    ChangeLanguage(selected_lang,pos);
                }
                else if(selected_lang.equals("عربي")&&pos!=spinner.getSelectedItemPosition()){
                    selected_lang="ar";
                    int pos= spinner.getSelectedItemPosition();
                    ChangeLanguage(selected_lang,pos);
                }
                else if(selected_lang.equals("french")&&pos!=spinner.getSelectedItemPosition()){
                    selected_lang="fr";
                    int pos= spinner.getSelectedItemPosition();
                    ChangeLanguage(selected_lang,pos);
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                selected_lang="AR";
            }
        });



    }

    public void ChangeLanguage(String languagee,int pos) {

        _switchLang = languagee;
        Locale myLocale = new Locale(languagee);

        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        conf.setLayoutDirection(myLocale);
        Intent refresh = new Intent(this, start_page.class);
        refresh.putExtra("position",pos);
        finish();
        startActivity(refresh);
    }

}
