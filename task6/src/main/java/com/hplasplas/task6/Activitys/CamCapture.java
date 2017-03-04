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
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.hplasplas.task6.Loaders.BitmapLoader;
import com.hplasplas.task6.R;

import java.io.File;

import static com.hplasplas.task6.Setting.Constants.DEBUG;
import static com.hplasplas.task6.Setting.Constants.FILE_NAME_TO_LOAD;
import static com.hplasplas.task6.Setting.Constants.FIRST_INIT_FILE_NAME;
import static com.hplasplas.task6.Setting.Constants.PICTURE_FOLDER_NAME;
import static com.hplasplas.task6.Setting.Constants.PREFERENCES_FILE;
import static com.hplasplas.task6.Setting.Constants.PREF_FOR_LAST_FILE_NAME;
import static com.hplasplas.task6.Setting.Constants.REQUEST_CODE;

public class CamCapture extends AppCompatActivity implements View.OnClickListener, LoaderManager.LoaderCallbacks<Bitmap> {
    
    private final String TAG = getClass().getSimpleName();
    private final int LOADER_ID = 0;
    private SharedPreferences myPreferences;
    private ImageView myImageView;
    private Button myButton;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private File currentPictureFile;
    private File pictureDirectory;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_capture_activity);
        myButton = (Button) findViewById(R.id.foto_button);
        mRecyclerView = (RecyclerView) findViewById(R.id.foto_list);
        myImageView = (ImageView) findViewById(R.id.foto_frame);
        createDirectory();
        myPreferences = this.getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
        Bundle bundle = new Bundle();
        bundle.putString(FILE_NAME_TO_LOAD, myPreferences.getString(PREF_FOR_LAST_FILE_NAME, FIRST_INIT_FILE_NAME));
        getSupportLoaderManager().initLoader(LOADER_ID, bundle, this);
        myButton.setOnClickListener(this);
    }
    
    private void loadBitmap(String fileName) {
        
        Bundle bundle = new Bundle();
        bundle.putString(FILE_NAME_TO_LOAD, fileName);
        getSupportLoaderManager().restartLoader(LOADER_ID, bundle, this);
    }
    
    private void createDirectory() {
        
        pictureDirectory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PICTURE_FOLDER_NAME);
        if (!pictureDirectory.exists()) {
            pictureDirectory.mkdirs();
        }
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
    }
    
    @Override
    public void onLoaderReset(Loader<Bitmap> loader) {
        
    }
    
    @Override
    public void onClick(View v) {
        
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        currentPictureFile = generatePictureFile();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(currentPictureFile));
        startActivityForResult(intent, REQUEST_CODE);
    }
    
    private File generatePictureFile() {
        
        return new File(pictureDirectory.getPath() + "/" + "photo_" + System.currentTimeMillis() + ".jpg");
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
                    .putString(currentPictureFile.getPath(), FIRST_INIT_FILE_NAME)
                    .apply();
        }
        super.onPause();
    }
}
