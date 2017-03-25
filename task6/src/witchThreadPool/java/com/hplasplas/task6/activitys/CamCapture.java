package com.hplasplas.task6.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hplasplas.task6.R;
import com.hplasplas.task6.adapters.PictureInFolderAdapter;
import com.hplasplas.task6.dialogs.FileNameInputDialog;
import com.hplasplas.task6.dialogs.RenameErrorDialog;
import com.hplasplas.task6.loaders.BitmapInThreadLoader;
import com.hplasplas.task6.models.ListItemModel;
import com.hplasplas.task6.util.MainExecutor;
import com.starsoft.rvclicksupport.ItemClickSupport;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.hplasplas.task6.setting.Constants.*;

public class CamCapture extends AppCompatActivity implements View.OnClickListener, BitmapInThreadLoader.BitmapLoaderListener,
        PopupMenu.OnMenuItemClickListener, FileNameInputDialog.FileNameInputDialogListener {
    
    private final String TAG = getClass().getSimpleName();
    
    public ArrayList<ListItemModel> mFilesItemList;
    private boolean mMainPictureLoaded;
    private boolean mNewPhoto;
    private int mContextMenuPosition = -1;
    private int mFilesInFolder;
    private ImageView mImageView;
    private TextView mFilesInFolderText;
    private Bitmap mMainBitmap;
    private Button mButton;
    private ProgressBar mainProgressBar;
    private RecyclerView mRecyclerView;
    private PictureInFolderAdapter mPictureInFolderAdapter;
    private File mCurrentPictureFile;
    private File mPictureDirectory;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        if (DEBUG) {
            Log.d(TAG, "onCreate: ");
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.cam_capture_activity);
        findViews();
        adjustViews();
        adjustRecyclerView();
        setVmPolicyIfNeed();
        loadMainBitmap(getMyPreferences());
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 1) {
            //TODO  check result
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        
        if (DEBUG) {
            Log.d(TAG, "onActivityResult: ");
        }
        if (mNewPhoto) {
            if (requestCode == GET_PICTURE_REQUEST_CODE && resultCode == RESULT_OK) {
                loadMainBitmap(mCurrentPictureFile.getPath());
                mFilesItemList.add(new ListItemModel(mCurrentPictureFile));
                mPictureInFolderAdapter.notifyItemInserted(mFilesItemList.size() - 1);
                loadPreview(mFilesItemList.size() - 1);
                mRecyclerView.scrollToPosition(mFilesItemList.size() - 1);
                setFilesInFolderText(++mFilesInFolder);
            }
        }
        mButton.setEnabled(true);
    }
    
    @Override
    protected void onResume() {
        
        if (DEBUG) {
            Log.d(TAG, "onResume: ");
        }
        if (!mNewPhoto) {
            firstInitActivity();
        } else {
            mNewPhoto = false;
        }
        loadMainBitmap();
        super.onResume();
    }
    
    @Override
    public void onClick(View v) {
        
        mNewPhoto = true;
        mButton.setEnabled(false);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCurrentPictureFile = generateFileForPicture();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPictureFile));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, GET_PICTURE_REQUEST_CODE);
        }
    }
    
    @Override
    protected void onPause() {
        
        if (DEBUG) {
            Log.d(TAG, "onPause: ");
        }
        if (mCurrentPictureFile != null) {
            getMyPreferences().edit()
                    .putString(PREF_FOR_LAST_FILE_NAME, mCurrentPictureFile.getPath())
                    .apply();
        }
        MainExecutor.getExecutor().getQueue().clear();
        super.onPause();
    }
    
    @Override
    protected void onDestroy() {
        
        if (DEBUG) {
            Log.d(TAG, "onDestroy: ");
            if (mMainBitmap != null) {
                mMainBitmap.recycle();
            }
            recyclePreviewBitmaps(mFilesItemList);
        }
        super.onDestroy();
    }
    
    private File generateFileForPicture() {
        
        String fileName = DEFAULT_FILE_NAME_PREFIX + new SimpleDateFormat(TIME_STAMP_PATTERN, Locale.getDefault()).format(new Date()) + FILE_NAME_SUFFIX;
        return generateFileForPicture(fileName);
    }
    
    private File generateFileForPicture(String fileName) {
        
        return new File(getDirectory().getPath() + "/" + fileName);
    }
    
    private File getDirectory(boolean needPrivate) {
        
        File dir;
        if (needPrivate) {
            dir = getExternalFilesDir(PICTURE_FOLDER_NAME);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_DENIED) {
                //TODO request permissions in onResume and check it
                dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
            } else {
                dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), PICTURE_FOLDER_NAME);
            }
        }
        if (dir != null && !dir.exists() && !dir.mkdir()) {
            throw new IllegalStateException("Dir create error");
        }
        return dir;
    }
    
    private void firstInitActivity() {
        
        mFilesInFolder = getFilesCount(getDirectory());
        setFilesInFolderText(mFilesInFolder);
        createFilesItemList(getDirectory());
        mPictureInFolderAdapter = setAdapter(mRecyclerView, mFilesItemList);
    }
    
    private File getDirectory() {
        
        if (mPictureDirectory == null) {
            mPictureDirectory = getDirectory(NEED_PRIVATE_FOLDER);
        }
        return mPictureDirectory;
    }
    
    private void requestWriteExtStorage() {
        
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
    }
    
    public void requestPermissionWithRationale() {
        
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            final String message = "Storage permission is needed to show files count";
            //TODO show request
        } else {
            requestWriteExtStorage();
        }
    }
    
    private void recyclePreviewBitmaps(ArrayList<ListItemModel> filesItemList) {
        
        for (int i = 0, y = filesItemList.size(); i < y; i++) {
            Bitmap currentBitmap = filesItemList.get(i).getPicturePreview();
            if (currentBitmap != null) {
                currentBitmap.recycle();
            }
        }
    }
    
    private void findViews() {
        
        mImageView = (ImageView) findViewById(R.id.foto_frame);
        mainProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);
        mFilesInFolderText = (TextView) findViewById(R.id.files_in_folder);
        mButton = (Button) findViewById(R.id.foto_button);
        mRecyclerView = (RecyclerView) findViewById(R.id.foto_list);
    }
    
    private void adjustViews() {
        
        mButton.setOnClickListener(this);
        mainProgressBar.setVisibility(View.VISIBLE);
    }
    
    private void adjustRecyclerView() {
        
        mRecyclerView.setHasFixedSize(true);
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, ROWS_IN_TABLE, LinearLayoutManager.HORIZONTAL, false));
            mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        }
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener((recyclerView, position, v) -> onRecyclerViewItemClicked(position, v));
        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener((recyclerView, position, v) -> onRecyclerViewItemLongClicked(position, v));
    }
    
    private PictureInFolderAdapter setAdapter(RecyclerView recyclerView, ArrayList<ListItemModel> itemList) {
        
        PictureInFolderAdapter adapter = new PictureInFolderAdapter(itemList);
        if (recyclerView.getAdapter() == null) {
            recyclerView.setAdapter(adapter);
        } else {
            recyclerView.swapAdapter(adapter, true);
        }
        return adapter;
    }
    
    private SharedPreferences getMyPreferences() {
        
        return this.getSharedPreferences(PREFERENCES_FILE, MODE_PRIVATE);
    }
    
    private void setVmPolicyIfNeed() {
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }
    
    private int getFilesCount(File dir) {
        
        if (dir == null || dir.listFiles() == null) {
            return 0;
        } else {
            return dir.listFiles().length;
        }
    }
    
    private void setFilesInFolderText(int filesInFolder) {
        
        mFilesInFolderText.setText(getString(R.string.files_in_folder, filesInFolder));
    }
    
    private void onRecyclerViewItemClicked(int position, View v) {
        
        if (!mMainPictureLoaded) {
            File clickedFile = mFilesItemList.get(position).getPictureFile();
            if (mCurrentPictureFile == null || !mCurrentPictureFile.equals(clickedFile)) {
                mCurrentPictureFile = clickedFile;
                loadMainBitmap(mCurrentPictureFile.getPath());
            }
        }
    }
    
    private boolean onRecyclerViewItemLongClicked(int position, View v) {
        
        PopupMenu popup = new PopupMenu(this, v);
        popup.inflate(R.menu.item_context_menu);
        popup.setOnMenuItemClickListener(this);
        mContextMenuPosition = position;
        popup.show();
        
        return false;
    }
    
    private void createFilesItemList(File dir) {
        
        mFilesItemList = new ArrayList<>();
        if (dir != null && dir.listFiles() != null) {
            File[] filesList = dir.listFiles();
            for (int i = 0, fileCount = filesList.length; i < fileCount; i++) {
                mFilesItemList.add(new ListItemModel(filesList[i]));
                loadPreview(i);
            }
        }
    }
    
    private int getFilePositionInList(File fileToSearch, ArrayList<ListItemModel> filesItemList) {
        
        for (int i = 0, y = filesItemList.size(); i < y; i++) {
            if (filesItemList.get(i).getPictureFile().equals(fileToSearch)) {
                return i;
            }
        }
        return -1;
    }
    
    private void loadMainBitmap() {
        
        if (!mCurrentPictureFile.exists() && !mFilesItemList.isEmpty()) {
            mCurrentPictureFile = mFilesItemList.get(mFilesItemList.size() - 1).getPictureFile();
            loadMainBitmap(mCurrentPictureFile.getPath());
        } else if (mFilesItemList.isEmpty()) {
            loadMainBitmap(NO_EXISTING_FILE_NAME);
        }
    }
    
    private void loadMainBitmap(SharedPreferences preferences) {
        
        beforeLoadMainBitmap();
        mCurrentPictureFile = new File(preferences.getString(PREF_FOR_LAST_FILE_NAME, NO_EXISTING_FILE_NAME));
        MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(mCurrentPictureFile.getPath(), MAIN_PICTURE_INDEX)));
    }
    
    private void loadMainBitmap(String fileName) {
        
        beforeLoadMainBitmap();
        MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, MAIN_PICTURE_INDEX)));
    }
    
    private void loadMainBitmap(String fileName, int requestedHeight, int requestedWidth) {
        
        beforeLoadMainBitmap();
        MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, MAIN_PICTURE_INDEX, requestedHeight, requestedWidth)));
    }
    
    private void beforeLoadMainBitmap() {
        
        mMainPictureLoaded = true;
        mainProgressBar.setVisibility(View.VISIBLE);
    }
    
    private int getMainBitmapRequestedWidth() {
        
        int requestedWidth;
        if ((requestedWidth = mImageView.getWidth()) == 0) {
            requestedWidth = FIRST_LOAD_PICTURE_WIDTH;
        }
        return requestedWidth;
    }
    
    private int getMainBitmapRequestedHeight() {
        
        int requestedHeight;
        if ((requestedHeight = mImageView.getHeight()) == 0) {
            requestedHeight = FIRST_LOAD_PICTURE_HEIGHT;
        }
        return requestedHeight;
    }
    
    private Bundle createBundleBitmap(String fileName, int index) {
        
        return createBundleBitmap(fileName, index, getMainBitmapRequestedHeight(), getMainBitmapRequestedWidth());
    }
    
    private Bundle createBundleBitmap(String fileName, int index, int requestedHeight, int requestedWidth) {
        
        return createBundleBitmap(fileName, index, requestedHeight, requestedWidth, 0);
    }
    
    private Bundle createBundleBitmap(String fileName, int index, int requestedHeight, int requestedWidth, int sampleSize) {
        
        Bundle bundle = new Bundle();
        bundle.putString(FILE_NAME_TO_LOAD, fileName);
        bundle.putInt(LIST_INDEX, index);
        bundle.putInt(REQUESTED_PICTURE_HEIGHT, requestedHeight);
        bundle.putInt(REQUESTED_PICTURE_WIDTH, requestedWidth);
        bundle.putInt(REQUESTED_SAMPLE_SIZE, sampleSize);
        return bundle;
    }
    
    private Bundle createBundleBitmap(String fileName, int index, int sampleSize) {
        
        return createBundleBitmap(fileName, index, 0, 0, sampleSize);
    }
    
    private void loadPreview(int index) {
        
        loadPreview(mFilesItemList.get(index).getPictureFile().getPath(), index);
    }
    
    private void loadPreview(String fileName, int index) {
        
        if (RESIZE_WITH_SAMPLE) {
            MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, index, PREVIEW_SAMPLE_SIZE)));
        } else {
            MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, index, PREVIEW_PICTURE_HEIGHT, PREVIEW_PICTURE_WIDTH)));
        }
    }
    
    private void setPreview(Bitmap bitmap, int position, String fileName) {
        
        File previewFile = new File(fileName);
        if (isThisFilePosition(mFilesItemList, position, previewFile)) {
            setPreview(bitmap, position);
        } else if ((position = getFilePositionInList(previewFile, mFilesItemList)) >= 0) {
            setPreview(bitmap, position);
        }
    }
    
    private void setPreview(Bitmap bitmap, int position) {
        
        mFilesItemList.get(position).setPicturePreview(bitmap);
        mPictureInFolderAdapter.notifyItemChanged(position);
    }
    
    private boolean isThisFilePosition(ArrayList<ListItemModel> filesItemList, int position, File previewFile) {
        
        return filesItemList.get(position).getPictureFile().equals(previewFile);
    }
    
    @Override
    public void onBitmapLoadFinished(int index, String fileName, Bitmap bitmap) {
        
        if (index == MAIN_PICTURE_INDEX) {
            if (mMainBitmap != null) {
                mMainBitmap.recycle();
            }
            mMainBitmap = bitmap;
            if (mMainBitmap != null) {
                mImageView.setImageBitmap(mMainBitmap);
            }
            mainProgressBar.setVisibility(View.INVISIBLE);
            mMainPictureLoaded = false;
        } else {
            setPreview(bitmap, index, fileName);
        }
    }
    
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        
        File clickedItemFile = mFilesItemList.get(mContextMenuPosition).getPictureFile();
        switch (item.getItemId()) {
            case R.id.menu_delete:
                if (clickedItemFile.delete()) {
                    loadPreview(mContextMenuPosition);
                    mFilesInFolder--;
                    setFilesInFolderText(mFilesInFolder);
                }
                if (mCurrentPictureFile == null || !mCurrentPictureFile.exists()) {
                    loadMainBitmap(NO_EXISTING_FILE_NAME);
                }
                break;
            case R.id.menu_rename:
                if (clickedItemFile.exists()) {
                    FileNameInputDialog fileRenameDialog = FileNameInputDialog.newInstance(clickedItemFile);
                    fileRenameDialog.show(getSupportFragmentManager(), FILE_RENAME_DIALOG_TAG);
                }
                break;
        }
        return false;
    }
    
    @Override
    public void onOkButtonClick(AppCompatDialogFragment dialog, String newFileName, File renamedFile, boolean successfully) {
        
        if (successfully) {
            int position = getFilePositionInList(renamedFile, mFilesItemList);
            File newFile = generateFileForPicture(newFileName);
            if (position < 0 || newFile.exists() || !renamedFile.renameTo(newFile)) {
                RenameErrorDialog.newInstance(getString(R.string.rename_failed)).show(getSupportFragmentManager(), ERROR_DIALOG_TAG);
            } else {
                mFilesItemList.get(position).setPictureFile(newFile);
                mPictureInFolderAdapter.notifyItemChanged(position);
            }
        } else {
            RenameErrorDialog.newInstance(getString(R.string.invalid_file_name)).show(getSupportFragmentManager(), ERROR_DIALOG_TAG);
        }
    }
    
    @Override
    public void onDialogNegativeClick(AppCompatDialogFragment dialog) {
        
    }
}
