package com.hplasplas.task3.Activitys;

import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hplasplas.task3.Loaders.TextLoader;
import com.hplasplas.task3.R;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<String> {

    public final String TAG = getClass().getSimpleName();
    final int LOADER_ID = 0;
    ProgressBar myProgressBar;
    TextView myTextView;
    TextView myTitle;
    ScrollView myScrollView;
    Button loadButton;
    boolean pressed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        myTextView = (TextView) findViewById(R.id.text);
        myScrollView = (ScrollView) findViewById(R.id.view_for_text);
        loadButton = (Button) findViewById(R.id.LoadButton);
        myTitle = (TextView) findViewById(R.id.title);
        myTitle.setText(getResources().getString(R.string.title));
        myScrollView.setVisibility(View.INVISIBLE);
        myProgressBar.setVisibility(View.INVISIBLE);
        loadButton.setOnClickListener(this);
        if (savedInstanceState != null) {
            if (pressed = savedInstanceState.getBoolean("pressed")) {
                changeLoadState();
                initLoader();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putBoolean("pressed", pressed);

        super.onSaveInstanceState(outState);
    }

    @Override
    public void onClick(View v) {

        pressed = true;
        changeLoadState();
        initLoader();
    }

    void changeLoadState() {

        loadButton.setVisibility(View.INVISIBLE);
        myProgressBar.setVisibility(View.VISIBLE);
    }

    void setText(String text) {

        myTextView.setText(text);
        myProgressBar.setVisibility(View.INVISIBLE);
        myScrollView.setVisibility(View.VISIBLE);

    }

    void initLoader() {

        String textFileName = getResources().getString(R.string.file_name);
        Bundle bundle = new Bundle();
        bundle.putString("fileName", textFileName);
        getSupportLoaderManager().initLoader(LOADER_ID, bundle, this);
    }


    @Override
    public Loader<String> onCreateLoader(int id, Bundle args) {

        return new TextLoader(this, args);
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {

        setText(data);
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {

    }
}
