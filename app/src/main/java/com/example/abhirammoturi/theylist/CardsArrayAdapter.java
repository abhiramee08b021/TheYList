package com.example.abhirammoturi.theylist;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

/**
 * Created by Abhiram Moturi on 1/16/2018.
 */

public class CardsArrayAdapter extends ArrayAdapter<Cards> {
    Context context;

    public CardsArrayAdapter(@NonNull Context context, int resource, @NonNull List<Cards> objects) {
        super(context, resource, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent){
        Cards card_item = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item, parent, false);
        }

        TextView name = (TextView) convertView.findViewById(R.id.name);
        ImageView image = (ImageView) convertView.findViewById(R.id.image);

        name.setText(card_item.getName());
        switch(card_item.getProfileImageUrl()){
            case "default":
                switch (card_item.getGender()) {
                    case "Male":
                        Glide.with(convertView.getContext()).load(R.mipmap.generic_male).into(image);
                        break;
                    case "Female":
                        Glide.with(convertView.getContext()).load(R.mipmap.generic_female).into(image);
                        break;
                    case "Other":
                        Glide.with(convertView.getContext()).load(R.mipmap.generic_other).into(image);
                        break;
                    default:
                        Glide.with(convertView.getContext()).load(R.mipmap.generic_other).into(image);
                }
                break;
            default:
                Glide.with(convertView.getContext()).load(card_item.getProfileImageUrl()).into(image);
                break;
        }
        return convertView;
    }

}
