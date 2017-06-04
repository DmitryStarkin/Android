package com.hplasplas.cam_capture.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hplasplas.cam_capture.R;
import com.hplasplas.cam_capture.activitys.CamCapture;
import com.hplasplas.cam_capture.models.ListItemModel;

import java.util.ArrayList;

import static com.hplasplas.cam_capture.setting.Constants.FILE_NOT_EXIST;
import static com.hplasplas.cam_capture.setting.Constants.PREVIEW_ANIMATION_DURATION;
import static com.hplasplas.cam_capture.setting.Constants.PREVIEW_ANIMATION_START_DELAY;

/**
 * Created by StarkinDG on 06.03.2017.
 */

public class PictureInFolderAdapter extends RecyclerView.Adapter<PictureInFolderAdapter.ViewHolder> {
    
    private ArrayList<ListItemModel> mFilesList;
    private CamCapture activity;
    
    public PictureInFolderAdapter(ArrayList<ListItemModel> PictureFilesList, CamCapture activity) {
        
        this.mFilesList = PictureFilesList;
        this.activity = activity;
    }
    
    @Override
    public PictureInFolderAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_item, parent, false);
        return new ViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(PictureInFolderAdapter.ViewHolder holder, int position) {
        
        try {
            Bitmap preview;
            if ((preview = mFilesList.get(position).getPicturePreview()) == null) {
                holder.mPictureLoadBar.setVisibility(View.VISIBLE);
                activity.loadPreview(position);
            } else {
                setImageScale(holder.mPicturePreview, 0);
                holder.mPictureLoadBar.setVisibility(View.INVISIBLE);
                holder.mPicturePreview.setImageBitmap(preview);
                holder.mPicturePreview.animate().scaleX(1).scaleY(1).setStartDelay(PREVIEW_ANIMATION_START_DELAY).setDuration(PREVIEW_ANIMATION_DURATION).start();
            }
            holder.mPictureDescription.setText(mFilesList.get(position).getPictureFile().getName());
        } catch (IndexOutOfBoundsException e) {
            holder.mPictureDescription.setText(FILE_NOT_EXIST);
        }
    }
    
    private void setImageScale(ImageView view, float scale) {
        
        view.setScaleX(scale);
        view.setScaleY(scale);
    }
    
    @Override
    public int getItemCount() {
        
        return mFilesList.size();
    }
    
    @Override
    public void onViewRecycled(ViewHolder holder) {
        
        int position = holder.getAdapterPosition();
        if (position != RecyclerView.NO_POSITION) {
            mFilesList.get(position).clearPreview();
            activity.stopLoadPreview(position);
        }
        super.onViewRecycled(holder);
    }
    
    public void setFilesList(ArrayList<ListItemModel> filesList) {
        
        this.mFilesList = filesList;
    }
    
    class ViewHolder extends RecyclerView.ViewHolder {
        
        private TextView mPictureDescription;
        private ImageView mPicturePreview;
        private ProgressBar mPictureLoadBar;
        
        ViewHolder(View itemView) {
            
            super(itemView);
            mPictureDescription = (TextView) itemView.findViewById(R.id.pictureFileDescription);
            mPicturePreview = (ImageView) itemView.findViewById(R.id.picturePreview);
            mPictureLoadBar = (ProgressBar) itemView.findViewById(R.id.pictureLoadBar);
        }
    }
}
 
    
    

