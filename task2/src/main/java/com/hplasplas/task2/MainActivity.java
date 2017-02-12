package com.hplasplas.task2;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
Button myButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myButton=(Button) findViewById(R.id.button);
        myButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        myButton.setVisibility(View.INVISIBLE);
    }
}
