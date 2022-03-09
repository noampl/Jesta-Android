package com.example.jesta.bindingAdapters;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.jesta.R;
import com.example.jesta.common.Consts;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JestaBindingAdapters {

    private static final long DAY_IN_MS = 86400000;

    @BindingAdapter("setProfilePlaceHolder")
    public static void setProfilePlaceHolder(ImageView imageView, Drawable res){
        if (res != null)
        Picasso.with(imageView.getContext()).load(R.drawable.no_profile_picture).fit().into(imageView);
    }

    @BindingAdapter({"setText","defaultText"})
    public static void setText(TextView view, String text, int resId){
        if (text != null && !text.equals("") && !text.equals(Consts.INVALID_STRING)){
            view.setText(text);
        }
        else {
            view.setText(view.getContext().getText(resId));
        }
    }

    @BindingAdapter(value = {"setDate", "defaultDateTitle"}, requireAll = false)
    public static void setDate(MaterialButton button, Date date, String defaultString) {
        if (date != null && date.getTime() > 0 ){
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            button.setText(sdf.format(date));
        }
        else{
            button.setText(defaultString);
        }
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter({"setAddress", "addressPrefix"})
    public static void setAddress(TextView textView, Place place, String prefix){
        if (place != null){
            textView.setText(prefix + " " + place.getAddress());
        }
    }

    @BindingAdapter({"srcHour","srcDate", "dstHour", "dstDate"})
    public static void setHoursTitle(TextView textView, Long srcHour, Date srcDate, Long dstHour, Date dstDate) {
        String srcDateStr, srcHourStr="", dstHourSr="", dstDateStr="";
        if (srcDate == null){
            srcDate= new Date(MaterialDatePicker.todayInUtcMilliseconds());
        }
        srcDateStr = dateConverter(textView, srcDate);
       if (srcHour != null){
           srcHourStr =hourConverter(srcHour);
       }
       if (dstDate != null){
           dstDateStr = dateConverter(textView, dstDate);
       }
       if (dstHour != null){
           dstHourSr = hourConverter(dstHour);
       }
       String res = srcDateStr + " " + srcHourStr + " - " + dstDateStr + " " + dstHourSr;
        textView.setText(res);

    }

    private static String dateConverter(View view, Date date){
        String res = "";
        if (date.getTime() == MaterialDatePicker.todayInUtcMilliseconds()){
            res = view.getContext().getString(R.string.today);
        }
        else if (date.getTime() == MaterialDatePicker.todayInUtcMilliseconds() + DAY_IN_MS) {
            res = view.getContext().getString(R.string.tommorow);
        }
        else{
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            res = sdf.format(date);
        }
        return res;
    }

    private static String hourConverter(Long hour){
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        return sdf.format(new Date(hour));
    }

}
