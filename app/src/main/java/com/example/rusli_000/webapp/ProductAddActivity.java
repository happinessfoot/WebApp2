package com.example.rusli_000.webapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ProductAddActivity extends AppCompatActivity {

    TextInputEditText name;
    TextInputEditText price;
    TextInputEditText description;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_add);
        name = findViewById(R.id.name);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);

    }
    public void OnButtonClick(View v)
    {
        switch (v.getId())
        {
            case R.id.add:
            {
                Log.d("ADD","Pressed add button");
                new AddProduct().execute();
                break;
            }
        }
    }
    class AddProduct extends AsyncTask<String,String,String>
    {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ProductAddActivity.this);
            pDialog.setMessage("Добавляем");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try
            {

                String params = "name="+name.getText()+"&price="+price.getText()+"&description="+description.getText();
                Log.d("PARAMS",params);
                byte[] postData;
                url = new URL("https://ruslik2014.000webhostapp.com/add_product.php");
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
                Intent i = getIntent();
                setResult(100,i);


            }catch (Exception e)
            {
                FileInputStream fin = null;
                String resultJSON = "";
                try {
                    fin = openFileInput("products.json");
                    byte[] bytes = new byte[fin.available()];
                    fin.read(bytes);
                    String text = new String (bytes);
                    Log.d("TEXT",text);
                    resultJSON = text;
                    JSONObject jsonObject = new JSONObject(resultJSON);
                    JSONArray array = jsonObject.getJSONArray("products");
                    JSONObject object = new JSONObject();
                    object.put("pid",(array.length()+1)+"");
                    object.put("name",""+name.getText()+"");
                    object.put("price",""+price.getText()+"");
                    object.put("description",description.getText());
                    array.put(array.length(),object);
                    jsonObject.remove("products");
                    jsonObject.put("products",array);
                    Log.d("ARRAYFILELENGTH",array.length()+"");
                    Log.d("ARRAYFILELENGTH",array.toString()+"");
                    Log.d("jsonObj",jsonObject.toString());
                    resultJSON = jsonObject.toString();
                    FileOutputStream fos = null;
                    fos = openFileOutput("products.json", MODE_PRIVATE);
                    fos.write(resultJSON.getBytes());
                }catch (Exception ex){
                    Log.d("Excep",ex.getMessage());
                }
                Log.d("AddException",e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            pDialog.dismiss();
        }
    }
}
