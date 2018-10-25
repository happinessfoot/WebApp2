package com.example.rusli_000.webapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class ProductEditActivity extends AppCompatActivity {

    String resultJSON = "";
    TextInputEditText name;
    TextInputEditText price;
    TextInputEditText description;
    TextView pid;
    String pidText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);
        Intent intent = getIntent();
        pid = (TextView)findViewById(R.id.pid);
        pid.setText(intent.getStringExtra("pid"));
        pidText = pid.getText().toString();
        name = (TextInputEditText) findViewById(R.id.name);
        price = (TextInputEditText) findViewById(R.id.priceProduct);
        description = (TextInputEditText) findViewById(R.id.descriptionProduct);
        new GetProductDetails().execute();

    }
    public void OnButtonClick(View v)
    {
        switch(v.getId())
        {
            case R.id.saveBtn:
            {
                Log.d("SAVEBTN","PRESSED "+name.getText());
                new SaveProductDetails().execute();
                break;
            }
        }
    }
    class SaveProductDetails extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String... strings) {
            URL url = null;
            StringBuilder result = new StringBuilder();
            HttpURLConnection urlConnection = null;
            try
            {

                String params = "pid="+pidText+"&name="+name.getText()+"&price="+price.getText()+"&description="+description.getText();
                Log.d("PARAMS",params);
                byte[] postData;
                url = new URL("https://ruslik2014.000webhostapp.com/update_product.php");
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
                Log.d("SaveExceptionUrl",e.getMessage());
            }
            return null;
        }
    }
    class GetProductDetails extends AsyncTask<String,String,String>
    {
        @Override
        protected String doInBackground(String[] params)
        {
                        URL url = null;
                        StringBuilder result = new StringBuilder();
                        HttpURLConnection urlConnection = null;
                        try{
                            url = new URL("https://ruslik2014.000webhostapp.com/get_product.php?pid="+pidText);

                        urlConnection = (HttpURLConnection) url.openConnection();
                            //urlConnection.setRequestMethod("GET");
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());


                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                            String line;
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                                Log.d("EDIT", line);
                            }
                            result.toString();
                            resultJSON = result.toString();
                        }catch (Exception e)
                        {
                            Log.d("EDIT",e.getMessage());
                        }
            urlConnection.disconnect();


            Log.d("ResultJSON",resultJSON);
            return resultJSON;
        }
        protected void onPostExecute(String result)
        {
            Log.d("postExecute", "COME IN");

            try {
                JSONObject jsonObject = new JSONObject(resultJSON).getJSONArray("product").getJSONObject(0);
                String productName = jsonObject.getString("name");
                String priceName = jsonObject.getString("price");
                String descriptionName = jsonObject.getString("description");
                name.setText(productName);
                price.setText(priceName);
                description.setText(descriptionName);

            }catch(JSONException e)
            {
                Log.d("JSONException",e.getMessage());
            }
        }
    }
}
