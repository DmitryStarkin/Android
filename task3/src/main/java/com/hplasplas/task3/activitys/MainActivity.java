package com.hplasplas.task3.activitys;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hplasplas.task3.R;
import com.hplasplas.task3.loaders.TextLoader;

import static com.hplasplas.task3.setting.Constants.BUTTON_PRESSED;
import static com.hplasplas.task3.setting.Constants.DEBUG;
import static com.hplasplas.task3.setting.Constants.LOADER_ID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<String> {
    
    private final String TAG = getClass().getSimpleName();
    private ProgressBar myProgressBar;
    private TextView mTextView;
    private TextView mTitle;
    private ScrollView mScrollView;
    private Button mLoadButton;
    private boolean mPressed;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViews();
        initViews();
        
        if (savedInstanceState != null) {
            if (mPressed = savedInstanceState.getBoolean(BUTTON_PRESSED)) {
                changeLoadState();
                initLoader();
            }
        }
        if (DEBUG) {
            Log.d(TAG, "onCreate: ");
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        
        if (DEBUG) {
            Log.d(TAG, "onSaveInstanceState: ");
        }
        outState.putBoolean(BUTTON_PRESSED, mPressed);
        
        super.onSaveInstanceState(outState);
    }
    
    private void findViews() {
        
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mTextView = (TextView) findViewById(R.id.text);
        mTitle = (TextView) findViewById(R.id.title);
        mScrollView = (ScrollView) findViewById(R.id.view_for_text);
        mLoadButton = (Button) findViewById(R.id.LoadButton);
    }
    
    private void initViews() {
        
        mTitle.setText(getResources().getString(R.string.title));
        mScrollView.setVisibility(View.INVISIBLE);
        myProgressBar.setVisibility(View.INVISIBLE);
        mLoadButton.setOnClickListener(this);
    }
    
    @Override
    public void onClick(View v) {
        
        mPressed = true;
        changeLoadState();
        initLoader();
        if (DEBUG) {
            Log.d(TAG, "onClick: ");
        }
    }
    
    private void changeLoadState() {
        
        mLoadButton.setVisibility(View.INVISIBLE);
        myProgressBar.setVisibility(View.VISIBLE);
    }
    
    private void setText(String text) {
        
        mTextView.setText(text);
        myProgressBar.setVisibility(View.INVISIBLE);
        mScrollView.setVisibility(View.VISIBLE);
    }
    
    private void initLoader() {
        
        String textFileName = getResources().getString(R.string.file_name);
        Bundle bundle = new Bundle();
        bundle.putString("fileName", textFileName);
        getSupportLoaderManager().initLoader(LOADER_ID, bundle, this);
    }
    
    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {
        
        if (DEBUG) {
            Log.d(TAG, "onCreateLoader: ");
        }
        return new TextLoader(this, args);
    }
    
    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        
        if (DEBUG) {
            Log.d(TAG, "onLoadFinished: ");
        }
        if (data != null) {
            setText(data);
        } else {
            setText(getResources().getString(R.string.load_failure));
        }
    }
    
    @Override
    public void onLoaderReset(Loader<String> loader) {
        
        if (DEBUG) {
            Log.d(TAG, "onLoaderReset: ");
        }
    }
}
