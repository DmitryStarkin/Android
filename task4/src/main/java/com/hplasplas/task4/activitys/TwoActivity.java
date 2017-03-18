package com.hplasplas.task4.activitys;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.hplasplas.task4.Setting.Constants.DEBUG;

/**
 * Created by StarkinDG on 21.02.2017.
 */

public class TwoActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private Button myButton;
    private TextView myDesk;
    private TextView myHashCode;
    private TextView myPreviousHashCode;
    private String myPressedText;
    private String myCurrentHashCode = ((Integer) this.hashCode()).toString();
    private String myPreviousHashCodeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        myButton = ((Button) findViewById(R.id.StartButton));
        myDesk = (TextView) findViewById(R.id.ActivityDeck);
        myHashCode = (TextView) findViewById(R.id.HashCode);
        myPreviousHashCode = (TextView) findViewById(R.id.previousHashCode);
        myButton.setText(R.string.next_activity);
        myButton.setOnClickListener(this);
        myDesk.setText(TAG);
        myPressedText = getResources().getString(R.string.pressed);
        myHashCode.setText(getResources().getString(R.string.current_hash_code, myCurrentHashCode));

        if (savedInstanceState != null) {
            myPreviousHashCodeText = getResources().getString(R.string.previous_hash_code, savedInstanceState.getString("hashCode"));
        }

        if (DEBUG) {
            Log.d(TAG, "onCreate: myCurrentHashCode " + myCurrentHashCode);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        if (DEBUG) {
            Log.d(TAG, "onPostCreate: myCurrentHashCode " + myCurrentHashCode);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        if (DEBUG) {
            Log.d(TAG, "onConfigurationChanged: myCurrentHashCode " + myCurrentHashCode);
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        if (DEBUG) {
            Log.d(TAG, "onStart: myCurrentHashCode " + myCurrentHashCode);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        if (DEBUG) {
            Log.d(TAG, "onStop: myCurrentHashCode" + myCurrentHashCode);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (DEBUG) {
            Log.d(TAG, "onDestroy: myCurrentHashCode " + myCurrentHashCode);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("hashCode", myCurrentHashCode);
        if (DEBUG) {
            Log.d(TAG, "onSaveInstanceState: myCurrentHashCode " + myCurrentHashCode);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        if (DEBUG) {
            Log.d(TAG, "onRestoreInstanceState: myCurrentHashCode " + myCurrentHashCode);
        }
    }

    @Override
    protected void onRestart() {

        super.onRestart();
        if (DEBUG) {
            Log.d(TAG, "onRestart: myCurrentHashCode " + myCurrentHashCode);
        }
    }

    @Override
    protected void onUserLeaveHint() {

        super.onUserLeaveHint();
        if (DEBUG) {
            Log.d(TAG, "onUserLeaveHint: myCurrentHashCode " + myCurrentHashCode);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (DEBUG) {
            Log.d(TAG, "onKeyDown: myCurrentHashCode " + myCurrentHashCode + " Key code " + keyCode);
        }

        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Toast.makeText(this, getResources().getString(R.string.volume_down_button) + myPressedText, Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                Toast.makeText(this, getResources().getString(R.string.volume_up_button) + myPressedText, Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_HOME:
                Toast.makeText(this, getResources().getString(R.string.home_button) + myPressedText, Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_MENU:
                Toast.makeText(this, getResources().getString(R.string.menu_button) + myPressedText, Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_BACK:
                break;
            default:
                Toast.makeText(this, getResources().getString(R.string.unknown_button) + myPressedText, Toast.LENGTH_SHORT).show();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Toast.makeText(this, getResources().getString(R.string.back_button) + myPressedText, Toast.LENGTH_SHORT).show();
        if (DEBUG) {
            Log.d(TAG, "onBackPressed: myCurrentHashCode " + myCurrentHashCode);
        }
    }

    @Override
    protected void onPause() {

        myPreviousHashCodeText = null;
        super.onPause();
        if (DEBUG) {
            Log.d(TAG, "onPause: myCurrentHashCode " + myCurrentHashCode);
        }
    }

    @Override
    protected void onResume() {

        if (myPreviousHashCodeText != null) {
            myPreviousHashCode.setText(myPreviousHashCodeText);
        } else {
            myPreviousHashCode.setText(getResources().getString(R.string.first_created));
        }
        super.onResume();
        if (DEBUG) {
            Log.d(TAG, "onResume: myCurrentHashCode " + myCurrentHashCode);
        }
    }

    @Override
    public void onClick(View v) {

        Toast.makeText(this, ((Button) v).getText() + myPressedText, Toast.LENGTH_SHORT).show();
        if (DEBUG) {
            Log.d(TAG, "onClick: " + myCurrentHashCode);
        }
        Intent intent = new Intent(this, ThirdActivity.class);
        startActivity(intent);
    }
}
