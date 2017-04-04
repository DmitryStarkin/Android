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
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDialogFragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hplasplas.task6.R;
import com.hplasplas.task6.ThisApplication;
import com.hplasplas.task6.adapters.PictureInFolderAdapter;
import com.hplasplas.task6.dialogs.FileNameInputDialog;
import com.hplasplas.task6.dialogs.ErrorDialog;
import com.hplasplas.task6.loaders.BitmapInThreadLoader;
import com.hplasplas.task6.managers.CollapsedElementsManager;
import com.hplasplas.task6.managers.FileSystemManager;
import com.hplasplas.task6.models.ListItemModel;
import com.hplasplas.task6.util.RecyclerViewPopupMenu;
import com.hplasplas.task6.util.MainExecutor;
import com.hplasplas.task6.util.MainHandler;
import com.starsoft.rvclicksupport.ItemClickSupport;

import java.io.File;
import java.util.ArrayList;

import static com.hplasplas.task6.setting.Constants.*;

public class CamCapture extends AppCompatActivity implements BitmapInThreadLoader.BitmapLoaderListener,
        RecyclerViewPopupMenu.OnMenuItemClickListener, FileNameInputDialog.FileNameInputDialogListener, PopupMenu.OnDismissListener {
    
    private final String TAG = getClass().getSimpleName();
    
    public ArrayList<ListItemModel> mFilesItemList;
    private CollapsedElementsManager mCollapsedElementsManager;
    private ImageView mImageView;
    private ProgressBar mainProgressBar;
    private RecyclerView mRecyclerView;
    private PictureInFolderAdapter mPictureInFolderAdapter;
    private File mCurrentPictureFile;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        
        super.onCreate(savedInstanceState);
        if (DEBUG) {
            Log.d(TAG, "onCreate: ");
        }
        setContentView(R.layout.cam_capture_activity);
        mCollapsedElementsManager = new CollapsedElementsManager(this);
        if (savedInstanceState == null) {
            mCollapsedElementsManager.hideBottomPanel();
        }
        hideStatusPanelIfNeed();
        findViews();
        adjustViews();
        adjustRecyclerView();
        setVmPolicyIfNeed();
        requestWriteExtStorageIfNeed();
    }
    
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE && grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            ShowNoPermissionMessage();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        
        if (DEBUG) {
            Log.d(TAG, "onActivityResult: ");
        }
        mCollapsedElementsManager.enableButton(true);
    }
    
    @Override
    protected void onResume() {
        
        super.onResume();
        if (DEBUG) {
            Log.d(TAG, "onResume: ");
        }
        firstInitActivity();
        loadMainBitmap();
        scrollToMainBitmapPosition();
        mCollapsedElementsManager.setRightVisibilityInterfaceElements();
        mCollapsedElementsManager.startTimerIfNeed();
    }
    
    @Override
    protected void onPause() {
        
        super.onPause();
        if (DEBUG) {
            Log.d(TAG, "onPause: ");
        }
        if (mCurrentPictureFile != null) {
            getMyPreferences().edit()
                    .putString(PREF_FOR_LAST_FILE_NAME, mCurrentPictureFile.getPath())
                    .apply();
        }
        stopLoadPreview();
        mCollapsedElementsManager.stopTimer();
    }
    
    private void stopLoadPreview(){
        MainExecutor.getInstance().purge();
        MainHandler.getInstance().removeMessages(MESSAGE_BITMAP_LOAD);
    }
    
    private void hideStatusPanelIfNeed() {
        
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
        }
    }
    
    private void setVmPolicyIfNeed() {
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
            StrictMode.setVmPolicy(builder.build());
        }
    }
    
    private void firstInitActivity() {
        
        mCurrentPictureFile = new File(getMyPreferences().getString(PREF_FOR_LAST_FILE_NAME, NO_EXISTING_FILE_NAME));
        mCollapsedElementsManager.setFilesInFolderText(FileSystemManager.getFilesCount());
        createFilesItemList(FileSystemManager.getDirectory());
        mPictureInFolderAdapter = setAdapter(mRecyclerView, mFilesItemList);
    }
    
    private void requestWriteExtStorageIfNeed() {
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !NEED_PRIVATE_FOLDER &&
                ContextCompat.checkSelfPermission(ThisApplication.getInstance().getApplicationContext(),
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
            if (!requestPermissionWithRationale()) {
                requestWriteExtStorage(PERMISSION_REQUEST_CODE);
            }
        }
    }
    
    private void requestWriteExtStorage(int requestCode) {
        
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
    }
    
    public boolean requestPermissionWithRationale() {
        
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            final String message = getResources().getString(R.string.permission_request_message);
            Snackbar.make(mImageView, message, Snackbar.LENGTH_LONG)
                    .setAction("GRANT", v -> requestWriteExtStorage(PERMISSION_REQUEST_CODE))
                    .setDuration(10000)
                    .show();
            return true;
        } else {
            return false;
        }
    }
    
    private void ShowNoPermissionMessage() {
        
        final String message = getResources().getString(R.string.no_permission_message);
        Snackbar.make(mImageView, message, Snackbar.LENGTH_LONG)
                .setAction(getResources().getString(R.string.button_ok), null)
                .setDuration(10000)
                .show();
    }
    
    private void findViews() {
        
        mImageView = (ImageView) findViewById(R.id.foto_frame);
        mainProgressBar = (ProgressBar) findViewById(R.id.mainProgressBar);
        mRecyclerView = (RecyclerView) findViewById(R.id.photo_list);
    }
    
    private void scrollToMainBitmapPosition() {
        
        int position = getFilePositionInList(mCurrentPictureFile, mFilesItemList);
        if (position >= 0) {
            mRecyclerView.scrollToPosition(position);
        }
    }
    
    private void adjustViews() {
        
        mainProgressBar.setVisibility(View.VISIBLE);
    }
    
    private void adjustRecyclerView() {
        
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener((recyclerView, position, v) -> onRecyclerViewItemClicked(position, v));
        ItemClickSupport.addTo(mRecyclerView).setOnItemLongClickListener((recyclerView, position, v) -> onRecyclerViewItemLongClicked(position, v));
        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                
                if (e.getAction() == MotionEvent.ACTION_DOWN) {
                    mCollapsedElementsManager.restartTimer();
                }
                return false;
            }
            
            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                
            }
            
            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
                
            }
        });
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.DOWN | ItemTouchHelper.UP) {
            
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                
                return false;
            }
            
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                
                deleteItem(viewHolder.getAdapterPosition());
            }
        });
        itemTouchHelper.attachToRecyclerView(mRecyclerView);
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
        
        RecyclerViewPopupMenu popup = new RecyclerViewPopupMenu(this, v, position);
        popup.inflate(R.menu.item_context_menu);
        popup.setOnMenuItemClickListener(this);
        popup.setOnDismissListener(this);
        popup.show();
        mCollapsedElementsManager.stopTimer();
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
        MainExecutor.getInstance().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, MAIN_PICTURE_INDEX, requestedHeight, requestedWidth, 0, orientation)));
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
            MainExecutor.getInstance().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, index, PREVIEW_SAMPLE_SIZE)));
        } else {
            MainExecutor.getInstance().execute(new BitmapInThreadLoader(this, createBundleBitmap(fileName, index, PREVIEW_PICTURE_HEIGHT, PREVIEW_PICTURE_WIDTH)));
        }
    }
    
    public void stopLoadPreview(int index) {
        
        if (mFilesItemList.size() > index) {
            stopLoadPreview(mFilesItemList.get(index).getPictureFile().getPath(), index);
        }
    }
    
    public void stopLoadPreview(String fileName, int index) {
        
        MainExecutor.getInstance().remove(new BitmapInThreadLoader(this, createBundleBitmap(fileName, index, PREVIEW_PICTURE_HEIGHT, PREVIEW_PICTURE_WIDTH)));
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
        
        return filesItemList.size() > position && filesItemList.get(position).getPictureFile().equals(previewFile);
    }
    
    private void deleteItem(int position) {
        
        if (mFilesItemList.get(position).getPictureFile().delete()) {
            mFilesItemList.remove(position);
            mPictureInFolderAdapter.notifyItemRemoved(position);
            mCollapsedElementsManager.setFilesInFolderText(FileSystemManager.getFilesCount());
            loadMainBitmap();
        }
    }
    
    private void renameItem(int position) {
        
        File clickedItemFile = mFilesItemList.get(position).getPictureFile();
        if (clickedItemFile.exists()) {
            FileNameInputDialog fileRenameDialog = FileNameInputDialog.newInstance(clickedItemFile);
            fileRenameDialog.show(getSupportFragmentManager(), FILE_RENAME_DIALOG_TAG);
        }
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
        
        switch (item.getItemId()) {
            case R.id.menu_delete:
                deleteItem(position);
                mCollapsedElementsManager.restartTimer();
                break;
            case R.id.menu_rename:
                renameItem(position);
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
                ErrorDialog.newInstance(getString(R.string.rename_failed)).show(getSupportFragmentManager(), ERROR_DIALOG_TAG);
            } else {
                mFilesItemList.get(position).setPictureFile(newFile);
                mPictureInFolderAdapter.notifyItemChanged(position);
            }
        } else {
            ErrorDialog.newInstance(getString(R.string.invalid_file_name)).show(getSupportFragmentManager(), ERROR_DIALOG_TAG);
        }
        mCollapsedElementsManager.restartTimer();
    }
    
    @Override
    public void onDialogNegativeClick(AppCompatDialogFragment dialog) {
        
        mCollapsedElementsManager.restartTimer();
    }
    
    @Override
    public void onDismiss(PopupMenu menu) {
        
        mCollapsedElementsManager.restartTimer();
    }
}
