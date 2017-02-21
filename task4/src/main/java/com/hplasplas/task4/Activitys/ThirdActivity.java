package com.hplasplas.task4.Activitys;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static com.hplasplas.task4.Setting.Constants.DEBUG;

/**
 * Created by StarkinDG on 21.02.2017.
 */

public class ThirdActivity extends AppCompatActivity implements View.OnClickListener {

    private final String TAG = getClass().getSimpleName();
    Button myButton;
    TextView myDesk;
    TextView mHashCode;
    TextView mPreviousHashCode;
    String pressedText;
    String backButtonText;
    String homeButtonText;
    String currentHashCode = ((Integer) this.hashCode()).toString();
    String previousHashCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
        myButton = (Button) findViewById(R.id.StartButton);
        myDesk = (TextView) findViewById(R.id.ActivityDeck);
        mHashCode = (TextView) findViewById(R.id.HashCode);
        mPreviousHashCode = (TextView) findViewById(R.id.previousHashCode);
        myButton.setText(R.string.previous_activity);
        myButton.setOnClickListener(this);
        myDesk.setText(TAG);
        pressedText = getResources().getString(R.string.pressed);
        backButtonText = getResources().getString(R.string.back_button);
        homeButtonText = getResources().getString(R.string.home_button);
        mHashCode.setText(getResources().getString(R.string.current_hash_code, currentHashCode));

        if (savedInstanceState != null) {
            previousHashCode = getResources().getString(R.string.previous_hash_code, savedInstanceState.getString("hashCode"));
        }

        if (DEBUG) {
            Log.d(TAG, "onCreate: currentHashCode");
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {

        super.onPostCreate(savedInstanceState);
        if (DEBUG) {
            Log.d(TAG, "onPostCreate: currentHashCode" + currentHashCode);
        }
    }

    @Override
    protected void onStart() {

        super.onStart();
        if (DEBUG) {
            Log.d(TAG, "onStart: currentHashCode" + currentHashCode);
        }
    }

    @Override
    protected void onStop() {

        super.onStop();
        if (DEBUG) {
            Log.d(TAG, "onStop: currentHashCode" + currentHashCode);
        }
    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
        if (DEBUG) {
            Log.d(TAG, "onDestroy: currentHashCode" + currentHashCode);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        super.onSaveInstanceState(outState);
        outState.putString("hashCode", currentHashCode);
        if (DEBUG) {
            Log.d(TAG, "onSaveInstanceState: currentHashCode");
        }

    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState, PersistableBundle persistentState) {

        super.onRestoreInstanceState(savedInstanceState, persistentState);
        if (DEBUG) {
            Log.d(TAG, "onRestoreInstanceState: currentHashCode" + currentHashCode);
        }
    }

    @Override
    protected void onRestart() {

        super.onRestart();
        if (DEBUG) {
            Log.d(TAG, "onRestart: currentHashCode" + currentHashCode);
        }
    }

    @Override
    protected void onUserLeaveHint() {

        super.onUserLeaveHint();
        Toast.makeText(this, homeButtonText + pressedText, Toast.LENGTH_SHORT).show();
        if (DEBUG) {
            Log.d(TAG, "onUserLeaveHint: Home button pressed");
        }
    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

        Toast.makeText(this, backButtonText + pressedText, Toast.LENGTH_SHORT).show();
        if (DEBUG) {
            Log.d(TAG, "onBackPressed: ");
        }
    }

    @Override
    protected void onPause() {

        previousHashCode = null;
        super.onPause();
        if (DEBUG) {
            Log.d(TAG, "onPause: currentHashCode" + currentHashCode);
        }
    }

    @Override
    protected void onResume() {

        if (previousHashCode != null) {
            mPreviousHashCode.setText(previousHashCode);
        } else {
            mPreviousHashCode.setText(getResources().getString(R.string.first_created));
        }
        super.onResume();
        if (DEBUG) {
            Log.d(TAG, "onResume: currentHashCode" + currentHashCode);
        }
    }

    @Override
    public void onClick(View v) {

        Toast.makeText(this, ((Button) v).getText() + pressedText, Toast.LENGTH_SHORT).show();
        if (DEBUG) {
            Log.d(TAG, "onClick: ");
        }
        this.finish();
    }
}
