package com.example.rusli_000.webapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import java.io.DataOutputStream;
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
    private ProgressDialog pDialog;
    boolean checkLoadFile = false;
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
            case R.id.load:
            {
                Log.d("FILE","load button");
                new LoadFile().execute();
                break;
            }

        }
    }
    class LoadFile extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Загружаем...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            String resultFileText = "";
            checkLoadFile = false;
            FileInputStream fin = null;
            try {
                fin = openFileInput("products.json");
                byte[] bytes = new byte[fin.available()];
                StringBuilder fileContent = new StringBuilder("");
                int n;
                while((n = fin.read(bytes))!=-1)
                {
                    fileContent.append(n);
                }
                String text = new String (bytes);
                JSONArray array = new JSONObject(text).getJSONArray("products");
                resultFileText = array.toString();

            }catch (Exception e){
                Log.d("IOExcep",e.getMessage());
                return  null;
            }
            URL url = null;
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try
            {
                String params = "products="+resultFileText;
                Log.d("PARAMS",params);
                byte[] postData;
                url = new URL("https://ruslik2014.000webhostapp.com/create_table_product.php");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
                outputStream.writeBytes(params);
                outputStream.flush();
                outputStream.close();
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = in.readLine()) != null){
                    sb.append(line);
                }
                in.close();
                Log.d("SB",sb.toString());
            }catch (Exception e)
            {
                checkLoadFile = true;
                Log.d("ex",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            if(checkLoadFile)
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Невозможно подключиться к интернету!")
                        .setMessage("Подключитесь к интернету, чтобы загрузить базу на сервер").setCancelable(false)
                        .setNegativeButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
            pDialog.dismiss();
        }
    }
    public void openText(){

        FileInputStream fin = null;
        try {
            fin = openFileInput("products.json");
            byte[] bytes = new byte[fin.available()];
            StringBuilder fileContent = new StringBuilder("");
            int n;
            while((n = fin.read(bytes))!=-1)
            {
                fileContent.append(n);
            }
            String text = new String (bytes);
            Log.d("TEXT",text);

        }catch (IOException e){
            Log.d("IOExcep",e.getMessage());
        }
    }
     class LoadAllProducts extends AsyncTask<String,String,String> {
        private String resultJSON="";

         @Override
         protected void onPreExecute() {
             pDialog = new ProgressDialog(MainActivity.this);
             pDialog.setMessage("Сохраняем");
             pDialog.setIndeterminate(false);
             pDialog.setCancelable(true);
             pDialog.show();
         }

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
                    JSONObject object = new JSONObject();
                    JSONArray array = new JSONArray();
                    JSONObject obj2 = new JSONObject();
                    object.put("products",array);
                    Log.d("TESTJSON",object.toString());
                    result=object.toString();

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
                try {
                    JSONObject object = new JSONObject();

                    JSONArray array = new JSONArray();
                    JSONObject obj2 = new JSONObject();
                    object.put("products", array);
                    Log.d("TESTJSON", object.toString());
                    result = object.toString();
                }catch (Exception e)
                {
                    Log.d("EXCEP",e.getMessage());
                }
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
            pDialog.dismiss();
        }
    }
}
