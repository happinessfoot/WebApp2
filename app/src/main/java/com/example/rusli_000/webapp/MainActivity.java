package com.example.rusli_000.webapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    static ArrayList<HashMap<String, String>> productsList;
    Button showBtn;
    Button saveBtn;
    int success = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showBtn = (Button) findViewById(R.id.btnShow);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        Log.d("TAGGG","ТЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕСССССССССТТТТ");
    }
    public void onButtonClick(View v)
    {
        switch (v.getId()) {
            case R.id.btnShow:
            {
                Log.d("FILE","button show pressed");
                Intent intent = new Intent(this,ShowActivity.class);
                startActivity(intent);
                break;
            }
            case R.id.saveBtn:
            {
                Log.d("FILE","button save pressed");
                new LoadAllProducts().execute();
                break;
            }
            case R.id.showFileBtn:
            {
                Log.d("FILE","show file button");
                openText();
                break;
            }
            case R.id.add:
            {
                Log.d("FILE","add button");
                Intent intent = new Intent(this,ProductAddActivity.class);
                startActivity(intent);
                break;
            }

        }
    }
    public void openText(){

        FileInputStream fin = null;
        try {
            fin = openFileInput("products.json");
            byte[] bytes = new byte[fin.available()];
            fin.read(bytes);
            String text = new String (bytes);
            Log.d("TEXT",text);
        }catch (IOException e){
            Log.d("IOExcep",e.getMessage());
        }
    }
     class LoadAllProducts extends AsyncTask<String,String,String> {
        private String resultJSON="";
        @Override
        protected String doInBackground(String... args) {
            StringBuilder result = new StringBuilder();

            HttpURLConnection urlConnection = null;
            try {
                URL url = new URL("https://ruslik2014.000webhostapp.com/get_all_product.php");
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                    Log.d("TAG",line);
                }
                resultJSON = result.toString();

            }catch( Exception e) {
                success = 1;
                e.printStackTrace();
                Log.d("TAG",e.getMessage());
            }
            finally {
                urlConnection.disconnect();
            }


            return resultJSON;
        }
        protected void onPostExecute(String result)
        {
            JSONObject jsonObject = null;
            super.onPostExecute(result);
            try {
                jsonObject = new JSONObject(result);
                if(jsonObject.getInt("success")==0)
                {
                    success=2;
                    result="{\"products\":}";
                    JSONObject object = new JSONObject();
                    JSONArray array = new JSONArray();

                    JSONObject obj2 = new JSONObject();
                    obj2.put("pid","1");
                    obj2.put("name","product");
                    obj2.put("price","200.00");
                    obj2.put("created_at","2018-10-26 18:16:22");
                    obj2.put("updated_at","0000-00-00 00:00:00");
                    array.put(0,obj2);
                    object.put("products",array);
                    Log.d("TESTJSON",object.toString());


                }
            }catch (Exception e)
            {
                e.printStackTrace();
            }
            if(success==2)
            {
                Log.d("SSS",success+"");
            }
            if(success==1)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Невозможно подключиться к интернету!")
                        .setMessage("Чтобы сохранить базу, нужно подключение к интернету").setCancelable(false)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
                return;
            }

            Log.d("postExecute", "COME IN");
            FileOutputStream fos = null;
            try {

                fos = openFileOutput("products.json", MODE_PRIVATE);
                fos.write(result.getBytes());
            }catch(IOException e)
            {
                Log.d("JSONException",e.getMessage());
            }
        }
    }
}
