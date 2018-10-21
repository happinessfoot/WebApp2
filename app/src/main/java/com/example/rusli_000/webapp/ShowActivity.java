package com.example.rusli_000.webapp;

import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import static android.util.Log.d;


public class ShowActivity extends AppCompatActivity {

    static ArrayList<HashMap<String, String>> productsList;
    ListView lvMain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        String url = "http://localhost/webapps/get_all_product.php";

        productsList = new ArrayList<HashMap<String, String>>();
        lvMain = (ListView) findViewById(R.id.list);
        new LoadAllProducts().execute();


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
            super.onPostExecute(result);
            Log.d("postExecute", "COME IN");

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray products = jsonObject.getJSONArray("products");
                Log.d("JSON", " "+products.length());
                for (int i = 0; i < products.length(); i++) {
                    JSONObject product = products.getJSONObject(i);
                    String id = product.getString("pid");
                    String name = product.getString("name");
                    String price = product.getString("price");
                    HashMap<String,String> map = new HashMap<String,String>();
                    map.put("pid",id);
                    map.put("name",name+"\n"+price);
                    productsList.add(map);
                    Log.d("JSON", id);
                    Log.d("JSON", name);
                    Log.d("JSON", price);
                }
            }catch(JSONException e)
            {
                Log.d("JSONException",e.getMessage());
            }
            ListAdapter adapter = new SimpleAdapter(
                    ShowActivity.this,productsList,
                    R.layout.list_item, new String[] {"pid","name"},
                    new int[] {R.id.pid,R.id.name});
            lvMain.setAdapter(adapter);
        }
    }

}
