package com.hplasplas.task6.Activitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hplasplas.task6.Adapters.PictureInFolderAdapter;
import com.hplasplas.task6.Loaders.BitmapLoader;
import com.hplasplas.task6.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.hplasplas.task6.Setting.Constants.ARG_FILE_NAME_TO_LOAD;
import static com.hplasplas.task6.Setting.Constants.DEBUG;
import static com.hplasplas.task6.Setting.Constants.FILE_NAME_PREFIX;
import static com.hplasplas.task6.Setting.Constants.FILE_NAME_SUFFIX;
import static com.hplasplas.task6.Setting.Constants.FIRST_INIT_FILE_NAME;
import static com.hplasplas.task6.Setting.Constants.GET_PICTURE_REQUEST_CODE;
import static com.hplasplas.task6.Setting.Constants.GET_PRIVATE_FOLDER;
import static com.hplasplas.task6.Setting.Constants.MAIN_PICTURE_LOADER_ID;
import static com.hplasplas.task6.Setting.Constants.PICTURE_FOLDER_NAME;
import static com.hplasplas.task6.Setting.Constants.PREFERENCES_FILE;
import static com.hplasplas.task6.Setting.Constants.PREF_FOR_LAST_FILE_NAME;

public class CamCapture extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Bitmap> {
    
    private final String TAG = getClass().getSimpleName();
    private SharedPreferences myPreferences;
    private ImageView myImageView;
    private Button myButton;
    private ProgressBar mainProgressBar;
    private RecyclerView myRecyclerView;
    private RecyclerView.Adapter myPictureInFolderAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private File currentPictureFile;
    private File pictureDirectory;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_capture_activity);
        myImageView = (ImageView) findViewById(R.id.foto_frame);
        mainProgressBar =(ProgressBar) findViewById(R.id.mainProgressBar);
        mainProgressBar.setVisibility(View.VISIBLE);
        createDirectory(GET_PRIVATE_FOLDER);
        myButton = (Button) findViewById(R.id.foto_button);
        myButton.setOnClickListener(this);
        myRecyclerView = (RecyclerView) findViewById(R.id.foto_list);
        myRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myRecyclerView.setLayoutManager(mLayoutManager);
        //TODO
        myPictureInFolderAdapter = new PictureInFolderAdapter();
        myRecyclerView.setAdapter(myPictureInFolderAdapter);
    
    
        myPreferences = this.getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
        Bundle bundle = new Bundle();
        bundle.putString(ARG_FILE_NAME_TO_LOAD, myPreferences.getString(PREF_FOR_LAST_FILE_NAME, FIRST_INIT_FILE_NAME));
        getSupportLoaderManager().initLoader(MAIN_PICTURE_LOADER_ID, bundle, this);
    }
    
    private void loadBitmap(String fileName) {
        mainProgressBar.setVisibility(View.VISIBLE);
        Bundle bundle = new Bundle();
        bundle.putString(ARG_FILE_NAME_TO_LOAD, fileName);
        getSupportLoaderManager().restartLoader(MAIN_PICTURE_LOADER_ID, bundle, this);
    }
    
    @Override
    public Loader<Bitmap> onCreateLoader(int id, Bundle args) {
        
        if (DEBUG) {
            Log.d(TAG, "onCreateLoader: ");
        }
        return new BitmapLoader(this, args);
    }
    
    @Override
    public void onLoadFinished(Loader<Bitmap> loader, Bitmap data) {
        
        myImageView.setImageBitmap(data);
        mainProgressBar.setVisibility(View.INVISIBLE);
    }
    
    @Override
    public void onLoaderReset(Loader<Bitmap> loader) {
        
    }
    
    @Override
    public void onClick(View v) {
        
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        currentPictureFile = generatePictureFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentPictureFile));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, GET_PICTURE_REQUEST_CODE);
        }
    }
    
    private void createDirectory(boolean isPrivate) {
        
        if (isPrivate) {
            pictureDirectory = getExternalFilesDir(PICTURE_FOLDER_NAME);
        } else {
            pictureDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PICTURE_FOLDER_NAME);
        }
    }
    
    private File generatePictureFile() {
        
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return new File(pictureDirectory.getPath() + "/" + FILE_NAME_PREFIX + timeStamp + FILE_NAME_SUFFIX);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //TODO checkers
        
        loadBitmap(currentPictureFile.getPath());
    }
    
    @Override
    protected void onPause() {
        
        if (currentPictureFile != null) {
            myPreferences.edit()
                    .putString(PREF_FOR_LAST_FILE_NAME, currentPictureFile.getPath())
                    .apply();
        }
        super.onPause();
    }
}
