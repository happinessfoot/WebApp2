package com.example.rusli_000.webapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ProductEditActivity extends AppCompatActivity {

    String resultJSON = "";
    TextInputLayout name;
    TextView pid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_edit);
        Intent intent = getIntent();
        pid = (TextView)findViewById(R.id.pid);
        pid.setText(intent.getStringExtra("pid"));
        Log.d("PIDTEXT",pid.getText().toString());

    }
    public void OnButtonClick(View v)
    {
        switch(v.getId())
        {
            case R.id.saveBtn:
            {
                Log.d("SAVEBTN",pid.getText().toString());
                break;
            }
        }
    }
    class GetProductDetails extends AsyncTask<String,String,String>
    {
        protected void OnPreExecute(){

        }
        @Override
        protected String doInBackground(String[] params)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                        int success;
                        URL url = null;
                        StringBuilder result = new StringBuilder();
                        HttpURLConnection urlConnection = null;
                        try{
                            url = new URL("https://ruslik2014.000webhostapp.com/get_product.php?pid="+R.id.pid);
                            urlConnection.setRequestMethod("GET");
                        urlConnection = (HttpURLConnection) url.openConnection();
                            InputStream in = new BufferedInputStream(urlConnection.getInputStream());


                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));

                            String line;
                            while ((line = reader.readLine()) != null) {
                                result.append(line);
                                Log.d("EDIT", line);
                            }
                        }catch (MalformedURLException e)
                        {
                            Log.d("EDIT",e.getMessage());
                        }catch (ProtocolException e)
                        {
                            Log.d("EDIT",e.getMessage());
                        }catch (IOException e)
                        {
                            Log.d("EDIT",e.getMessage());
                        }
                        resultJSON = result.toString();

                }
            });
            return resultJSON;
        }
        protected void OnPostExecute()
        {

        }
    }
}
