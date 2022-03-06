package com.example.jesta.bindingAdapters;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import androidx.databinding.BindingAdapter;

import com.example.jesta.R;
import com.squareup.picasso.Picasso;

public class JestaBindingAdapters {

    @BindingAdapter("setProfilePlaceHolder")
    public static void setProfilePlaceHolder(ImageView imageView, Drawable res){
        if (res != null)
        Picasso.with(imageView.getContext()).load(R.drawable.no_profile_picture).fit().into(imageView);
    }

}
