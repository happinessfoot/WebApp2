package com.example.rusli_000.webapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    Button showBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showBtn = (Button) findViewById(R.id.btnShow);
        Log.d("TAGGG","ТЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕЕСССССССССТТТТ");
    }
    public void onButtonClick(View v)
    {
        switch (v.getId()) {
            case R.id.btnShow:
            {
                Intent intent = new Intent(this,ShowActivity.class);
                startActivity(intent);
                break;
            }

        }
    }
}
