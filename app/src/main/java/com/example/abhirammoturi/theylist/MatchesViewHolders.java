package com.example.abhirammoturi.theylist;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.abhirammoturi.theylist.R;

/**
 * Created by Abhiram Moturi on 1/17/2018.
 */

public class MatchesViewHolders extends RecyclerView.ViewHolder {
    public TextView mMatchId, mMatchName;
    public ImageView mMatchImage;
    public MatchesViewHolders(View itemView) {
        super(itemView);

        mMatchName = (TextView) itemView.findViewById(R.id.MatchName);

        mMatchImage = (ImageView) itemView.findViewById(R.id.MatchImage);
    }
}
