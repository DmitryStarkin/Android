package com.hplasplas.task6.activitys;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hplasplas.task6.R;
import com.hplasplas.task6.adapters.PictureInFolderAdapter;
import com.hplasplas.task6.dialogs.FileNameInputDialog;
import com.hplasplas.task6.dialogs.RenameErrorDialog;
import com.hplasplas.task6.loaders.BitmapInThreadLoader;
import com.hplasplas.task6.managers.FileSystemManager;
import com.hplasplas.task6.models.ListItemModel;
import com.hplasplas.task6.util.CustomPopupMenu;
import com.hplasplas.task6.util.MainExecutor;
import com.starsoft.rvclicksupport.ItemClickSupport;

import java.io.File;
import java.util.ArrayList;

import static com.hplasplas.task6.setting.Constants.*;

public class CamCapture extends AppCompatActivity implements BitmapInThreadLoader.BitmapLoaderListener,
        CustomPopupMenu.OnMenuItemClickListener, FileNameInputDialog.FileNameInputDialogListener {
    
    private final String TAG = getClass().getSimpleName();
    
    public ArrayList<ListItemModel> mFilesItemList;
    private ImageView mImageView;
    private TextView mFilesInFolderText;
    private FloatingActionButton mButton;
    private CardView mFilesInFolderTextCard;
    private BottomSheetBehavior<LinearLayout> mBottomSheetBehavior;
    private ProgressBar mainProgressBar;
    private RecyclerView mRecyclerView;
    private PictureInFolderAdapter mPictureInFolderAdapter;
    private File mCurrentPictureFile;
    
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
        showBottomPanel();
        setInterfaceElementsScale(1);
        mButton.setEnabled(true);
    }
    
    @Override
    protected void onResume() {
        
        if (DEBUG) {
            Log.d(TAG, "onResume: ");
        }
        firstInitActivity();
        loadMainBitmap();
        scrollToMainBitmapPosition();
        super.onResume();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        
        if (event.getAction() == MotionEvent.ACTION_UP) {
            changeBottomPanelVisibility();
        }
        return true;
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
    
    private void firstInitActivity() {
        
        mCurrentPictureFile = new File(getMyPreferences().getString(PREF_FOR_LAST_FILE_NAME, NO_EXISTING_FILE_NAME));
        setFilesInFolderText(FileSystemManager.getFilesCount());
        createFilesItemList(FileSystemManager.getDirectory());
        mPictureInFolderAdapter = setAdapter(mRecyclerView, mFilesItemList);
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
    
    private void findViews() {
        
        mImageView = (ImageView) findViewById(R.id.foto_frame);
        mainProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);
        mFilesInFolderText = (TextView) findViewById(R.id.files_in_folder);
        mFilesInFolderTextCard = (CardView) findViewById(R.id.files_in_folder_card);
        mButton = (FloatingActionButton) findViewById(R.id.fab_photo);
        LinearLayout bottomPanel = (LinearLayout) findViewById(R.id.photo_list_container);
        mBottomSheetBehavior = BottomSheetBehavior.from(bottomPanel);
        mRecyclerView = (RecyclerView) findViewById(R.id.photo_list);
    }
    
    private void scrollToMainBitmapPosition() {
        
        int position = getFilePositionInList(mCurrentPictureFile, mFilesItemList);
        if (position >= 0) {
            mRecyclerView.scrollToPosition(position);
        }
    }
    
    private void changeBottomPanelVisibility() {
        
        if (mBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_HIDDEN) {
            showBottomPanel();
        } else {
            hideBottomPanel();
        }
    }
    
    private void hideBottomPanel() {
        
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
    
    private void showBottomPanel() {
        
        mBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }
    
    private void adjustViews() {
        
        mButton.setOnClickListener(v -> {
            mButton.setEnabled(false);
            makePhoto();
        });
        mBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                
                if (BottomSheetBehavior.STATE_HIDDEN == newState) {
                    mButton.animate().scaleX(0).scaleY(0).setDuration(FAB_ANIMATION_DURATION).start();
                    mFilesInFolderTextCard.animate().scaleX(0).scaleY(0).setDuration(FAB_ANIMATION_DURATION).start();
                } else if (BottomSheetBehavior.STATE_EXPANDED == newState) {
                    mButton.animate().scaleX(1).scaleY(1).setDuration(FAB_ANIMATION_DURATION).start();
                    mFilesInFolderTextCard.animate().scaleX(1).scaleY(1).setDuration(FAB_ANIMATION_DURATION).start();
                }
            }
            
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                
            }
        });
        hideBottomPanel();
        setInterfaceElementsScale(0);
        mainProgressBar.setVisibility(View.VISIBLE);
    }
    
    private void setInterfaceElementsScale(float scale) {
        
        mButton.setScaleX(scale);
        mButton.setScaleY(scale);
        mFilesInFolderTextCard.setScaleX(scale);
        mFilesInFolderTextCard.setScaleY(scale);
    }
    
    private void adjustRecyclerView() {
        
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener((recyclerView, position, v) -> onRecyclerViewItemClicked(position, v));
        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener((recyclerView, position, v) -> onRecyclerViewItemLongClicked(position, v));
    }
    
    private PictureInFolderAdapter setAdapter(RecyclerView recyclerView, ArrayList<ListItemModel> itemList) {
        
        PictureInFolderAdapter adapter = new PictureInFolderAdapter(itemList, this);
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
    
    private void setFilesInFolderText(int filesInFolder) {
        
        mFilesInFolderText.setText(getString(R.string.files_in_folder, filesInFolder));
    }
    
    private void onRecyclerViewItemClicked(int position, View v) {
        
        if (!isMainPictureLoading()) {
            File clickedFile = mFilesItemList.get(position).getPictureFile();
            if (mCurrentPictureFile == null || !mCurrentPictureFile.equals(clickedFile)) {
                mCurrentPictureFile = clickedFile;
                loadMainBitmap(mCurrentPictureFile.getPath());
            }
        }
    }
    
    private boolean onRecyclerViewItemLongClicked(int position, View v) {
        
        CustomPopupMenu popup = new CustomPopupMenu(this, v, position);
        popup.inflate(R.menu.item_context_menu);
        popup.setOnMenuItemClickListener(this);
        popup.show();
        return false;
    }
    
    public void makePhoto() {
        
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mCurrentPictureFile = FileSystemManager.generateFileForPicture();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentPictureFile));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(intent, GET_PICTURE_REQUEST_CODE);
        }
    }
    
    private void createFilesItemList(File dir) {
        
        mFilesItemList = new ArrayList<>();
        if (dir != null && dir.listFiles() != null) {
            File[] filesList = dir.listFiles();
            for (int i = 0, fileCount = filesList.length; i < fileCount; i++) {
                mFilesItemList.add(new ListItemModel(filesList[i]));
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
        } else {
            loadMainBitmap(mCurrentPictureFile.getPath());
        }
    }
    
    private void loadMainBitmap(String fileName) {
        
        loadMainBitmap(fileName, getMainBitmapRequestedHeight(), getMainBitmapRequestedWidth());
    }
    
    private void loadMainBitmap(String fileName, int requestedHeight, int requestedWidth) {
        
        int orientation = (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) ?
                Configuration.ORIENTATION_PORTRAIT : Configuration.ORIENTATION_LANDSCAPE;
        mainProgressBar.setVisibility(View.VISIBLE);
        MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, MAIN_PICTURE_INDEX, requestedHeight, requestedWidth, 0, orientation)));
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
        
        return createBundleBitmap(fileName, index, 0, 0);
    }
    
    private Bundle createBundleBitmap(String fileName, int index, int requestedHeight, int requestedWidth) {
        
        return createBundleBitmap(fileName, index, requestedHeight, requestedWidth, 0, Configuration.ORIENTATION_PORTRAIT);
    }
    
    private Bundle createBundleBitmap(String fileName, int index, int requestedHeight, int requestedWidth, int sampleSize, int orientation) {
        
        Bundle bundle = new Bundle();
        bundle.putString(FILE_NAME_TO_LOAD, fileName);
        bundle.putInt(LIST_INDEX, index);
        bundle.putInt(REQUESTED_ORIENTATION, orientation);
        bundle.putInt(REQUESTED_PICTURE_HEIGHT, requestedHeight);
        bundle.putInt(REQUESTED_PICTURE_WIDTH, requestedWidth);
        bundle.putInt(REQUESTED_SAMPLE_SIZE, sampleSize);
        return bundle;
    }
    
    private Bundle createBundleBitmap(String fileName, int index, int sampleSize) {
        
        return createBundleBitmap(fileName, index, 0, 0, sampleSize, Configuration.ORIENTATION_PORTRAIT);
    }
    
    public void loadPreview(int index) {
        
        loadPreview(mFilesItemList.get(index).getPictureFile().getPath(), index);
    }
    
    private void loadPreview(String fileName, int index) {
        
        if (RESIZE_WITH_SAMPLE) {
            MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, index, PREVIEW_SAMPLE_SIZE)));
        } else {
            MainExecutor.getExecutor().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, index, PREVIEW_PICTURE_HEIGHT, PREVIEW_PICTURE_WIDTH)));
        }
    }
    
    public void stopLoadPreview(int index) {
        
        if (mFilesItemList.size() > index) {
            stopLoadPreview(mFilesItemList.get(index).getPictureFile().getPath(), index);
        }
    }
    
    public void stopLoadPreview(String fileName, int index) {
        
        MainExecutor.getExecutor().remove(new BitmapInThreadLoader(this, createBundleBitmap(fileName, index, PREVIEW_PICTURE_HEIGHT, PREVIEW_PICTURE_WIDTH)));
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
            if (bitmap != null) {
                mImageView.setImageBitmap(bitmap);
            }
            mainProgressBar.setVisibility(View.INVISIBLE);
        } else {
            setPreview(bitmap, index, fileName);
        }
    }
    
    private boolean isMainPictureLoading() {
        
        return mainProgressBar.isShown();
    }
    
    @Override
    public synchronized boolean isRelevant() {
        
        return !this.isFinishing();
    }
    
    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        
        File clickedItemFile = mFilesItemList.get(position).getPictureFile();
        switch (item.getItemId()) {
            case R.id.menu_delete:
                if (clickedItemFile.delete()) {
                    loadPreview(position);
                    setFilesInFolderText(FileSystemManager.getFilesCount());
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
            File newFile = FileSystemManager.generateFileForPicture(newFileName);
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
