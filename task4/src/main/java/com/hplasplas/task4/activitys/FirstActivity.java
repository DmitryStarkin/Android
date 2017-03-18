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

public class FirstActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    private TextView mPreviousHashCode;
    private String mPressedText;
    private String mCurrentHashCode = ((Integer) this.hashCode()).toString();
    private String mPreviousHashCodeText;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        Button button = ((Button) findViewById(R.id.StartButton));
        TextView desk = (TextView) findViewById(R.id.ActivityDeck);
        TextView hashCode = (TextView) findViewById(R.id.HashCode);
        mPreviousHashCode = (TextView) findViewById(R.id.previousHashCode);
        button.setText(R.string.next_activity);
        button.setOnClickListener(this);
        desk.setText(TAG);
        mPressedText = getResources().getString(R.string.pressed);
        hashCode.setText(getResources().getString(R.string.current_hash_code, mCurrentHashCode));

        if (savedInstanceState != null) {
            mPreviousHashCodeText = getResources().getString(R.string.previous_hash_code, savedInstanceState.getString("hashCode"));
        }

        if (DEBUG) {
            Log.d(TAG, "onCreate: mCurrentHashCode " + mCurrentHashCode);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        if (DEBUG) {
            Log.d(TAG, "onPostCreate: mCurrentHashCode " + mCurrentHashCode);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        super.onConfigurationChanged(newConfig);
        if (DEBUG) {
            Log.d(TAG, "onConfigurationChanged: mCurrentHashCode " + mCurrentHashCode);
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        if (DEBUG) {
            Log.d(TAG, "onStart: mCurrentHashCode " + mCurrentHashCode);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        if (DEBUG) {
            Log.d(TAG, "onStop: mCurrentHashCode" + mCurrentHashCode);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (DEBUG) {
            Log.d(TAG, "onDestroy: mCurrentHashCode " + mCurrentHashCode);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("hashCode", mCurrentHashCode);
        if (DEBUG) {
            Log.d(TAG, "onSaveInstanceState: mCurrentHashCode " + mCurrentHashCode);
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {

        super.onRestoreInstanceState(savedInstanceState);
        if (DEBUG) {
            Log.d(TAG, "onRestoreInstanceState: mCurrentHashCode " + mCurrentHashCode);
        }
    }

    @Override
    protected void onRestart() {

        super.onRestart();
        if (DEBUG) {
            Log.d(TAG, "onRestart: mCurrentHashCode " + mCurrentHashCode);
        }
    }

    @Override
    protected void onUserLeaveHint() {

        super.onUserLeaveHint();
        if (DEBUG) {
            Log.d(TAG, "onUserLeaveHint: mCurrentHashCode " + mCurrentHashCode);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (DEBUG) {
            Log.d(TAG, "onKeyDown: mCurrentHashCode " + mCurrentHashCode + " Key code " + keyCode);
        }

        switch (keyCode) {

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Toast.makeText(this, getResources().getString(R.string.volume_down_button) + mPressedText, Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                Toast.makeText(this, getResources().getString(R.string.volume_up_button) + mPressedText, Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_HOME:
                Toast.makeText(this, getResources().getString(R.string.home_button) + mPressedText, Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_MENU:
                Toast.makeText(this, getResources().getString(R.string.menu_button) + mPressedText, Toast.LENGTH_SHORT).show();
                break;
            case KeyEvent.KEYCODE_BACK:
                break;
            default:
                Toast.makeText(this, getResources().getString(R.string.unknown_button) + mPressedText, Toast.LENGTH_SHORT).show();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        Toast.makeText(this, getResources().getString(R.string.back_button) + mPressedText, Toast.LENGTH_SHORT).show();
        if (DEBUG) {
            Log.d(TAG, "onBackPressed: mCurrentHashCode " + mCurrentHashCode);
        }
    }

    @Override
    protected void onPause() {

        mPreviousHashCodeText = null;
        super.onPause();
        if (DEBUG) {
            Log.d(TAG, "onPause: mCurrentHashCode " + mCurrentHashCode);
        }
    }

    @Override
    protected void onResume() {

        if (mPreviousHashCodeText != null) {
            mPreviousHashCode.setText(mPreviousHashCodeText);
        } else {
            mPreviousHashCode.setText(getResources().getString(R.string.first_created));
        }
        super.onResume();
        if (DEBUG) {
            Log.d(TAG, "onResume: mCurrentHashCode " + mCurrentHashCode);
        }
    }

    @Override
    public void onClick(View v) {

        Toast.makeText(this, ((Button) v).getText() + mPressedText, Toast.LENGTH_SHORT).show();
        if (DEBUG) {
            Log.d(TAG, "onClick: mCurrentHashCode " + mCurrentHashCode);
        }
        Intent intent = new Intent(this, TwoActivity.class);
        startActivity(intent);
    }

}
