package com.hplasplas.task6.adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hplasplas.task6.R;
import com.hplasplas.task6.models.ListItemModel;

import java.util.ArrayList;

import static com.hplasplas.task6.setting.Constants.FILE_NOT_EXIST;

/**
 * Created by StarkinDG on 06.03.2017.
 */

public class PictureInFolderAdapter extends RecyclerView.Adapter<PictureInFolderAdapter.ViewHolder> {
    
    private ArrayList<ListItemModel> mFilesList;
    
    public PictureInFolderAdapter(ArrayList<ListItemModel> PictureFilesList) {
        
        this.mFilesList = PictureFilesList;
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
            } else {
                holder.mPicturePreview.setImageBitmap(preview);
                holder.mPictureLoadBar.setVisibility(View.INVISIBLE);
            }
            holder.mPictureDescription.setText(mFilesList.get(position).getPictureFile().getName());
        } catch (IndexOutOfBoundsException e) {
            holder.mPictureDescription.setText(FILE_NOT_EXIST);
        }
    }
    
    @Override
    public int getItemCount() {
        
        return mFilesList.size();
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
 
    
    

