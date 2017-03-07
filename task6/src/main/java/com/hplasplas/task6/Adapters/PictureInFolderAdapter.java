package com.hplasplas.task6.Adapters;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hplasplas.task6.Models.ListItemModel;
import com.hplasplas.task6.R;

import java.util.ArrayList;

import static com.hplasplas.task6.Setting.Constants.FILE_NOT_EXIST;

/**
 * Created by StarkinDG on 06.03.2017.
 */

public class PictureInFolderAdapter extends RecyclerView.Adapter<PictureInFolderAdapter.ViewHolder> {
    
    private ArrayList<ListItemModel> filesList;
    
    public PictureInFolderAdapter(ArrayList<ListItemModel> PictureFilesList) {
        
        this.filesList = PictureFilesList;
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
            if ((preview = filesList.get(position).getPicturePreview()) == null) {
                holder.pictureLoadBar.setVisibility(View.VISIBLE);
            } else {
                holder.picturePreview.setImageBitmap(preview);
                holder.pictureLoadBar.setVisibility(View.INVISIBLE);
            }
            holder.pictureDescription.setText(filesList.get(position).getPictureFile().getName());
        } catch (IndexOutOfBoundsException e) {
            holder.pictureDescription.setText(FILE_NOT_EXIST);
        }
    }
    
    @Override
    public int getItemCount() {
        
        return filesList.size();
    }
    
    public void setFilesList(ArrayList<ListItemModel> filesList) {
        
        this.filesList = filesList;
    }
    
    public class ViewHolder extends RecyclerView.ViewHolder {
        
        private TextView pictureDescription;
        private ImageView picturePreview;
        private ProgressBar pictureLoadBar;
        
        public ViewHolder(View itemView) {
            
            super(itemView);
            pictureDescription = (TextView) itemView.findViewById(R.id.pictureFileDescription);
            picturePreview = (ImageView) itemView.findViewById(R.id.picturePreview);
            pictureLoadBar = (ProgressBar) itemView.findViewById(R.id.pictureLoadBar);
        }
    }
}
 
    
    

