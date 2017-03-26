package com.hplasplas.task6.managers;

import android.support.v7.widget.RecyclerView;

import com.hplasplas.task6.adapters.PictureInFolderAdapter;
import com.hplasplas.task6.models.ListItemModel;

import java.util.ArrayList;

/**
 * Created by StarkinDG on 26.03.2017.
 */

public class PreviewManager {
    private RecyclerView mRecyclerView;
    private PictureInFolderAdapter mPictureInFolderAdapter;
    public ArrayList<ListItemModel> mFilesItemList;
}
