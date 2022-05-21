package com.example.jesta.bindingAdapters;

import static com.example.jesta.common.Consts.SERVER_PRE_FIX;

import android.annotation.SuppressLint;

import android.graphics.drawable.Drawable;

import android.os.Build;
import android.util.Log;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.example.jesta.GetJestaQuery;
import com.example.jesta.R;
import com.example.jesta.common.Consts;
import com.example.jesta.common.Utilities;
import com.example.jesta.common.enums.FavorTransactionStatus;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.model.enteties.User;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class JestaBindingAdapters {

    // region Consts

    private static final long DAY_IN_MS = 86400000;

    // endregion

    @BindingAdapter("setProfilePlaceHolder")
    public static void setProfilePlaceHolder(ImageView imageView, Drawable res) {
        if (res != null)
            Picasso.with(imageView.getContext()).load(R.drawable.no_profile_picture).fit().into(imageView);
    }

    @BindingAdapter("profileImage")
    public static void setProfileImage(ImageView imageView, String path) {
        if (path == null)
            Picasso.with(imageView.getContext()).load(R.drawable.no_profile_picture).fit().into(imageView);
        else {
            Picasso.with(imageView.getContext()).load(SERVER_PRE_FIX + path).placeholder(R.drawable.no_profile_picture).into(imageView);
        }

    }

    @BindingAdapter("setImage")
    public static void setImage(ImageView imageView, String path) {
        if (path != null && path.length() > 5) {
            String fullPath = Consts.SERVER_PRE_FIX + path;
            Picasso.with(imageView.getContext()).load(fullPath).fit().into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_baseline_account_circle_24);
        }
    }

    @BindingAdapter("setImageNotification")
    public static void setImage(ImageView imageView, Transaction transaction) {
        if (transaction.getStatus() == null)
            return;
        int resId = 0;
        if (FavorTransactionStatus.PENDING_FOR_OWNER.equals(transaction.getStatus())) {
            resId = R.drawable.ic_baseline_waving_hand_24;
        } else if (FavorTransactionStatus.JESTA_DONE.equals(transaction.getStatus()) ||
                FavorTransactionStatus.EXECUTOR_FINISH_JESTA.equals(transaction.getStatus())) {
            resId = R.drawable.ic_baseline_check_circle_24;
        } else if (FavorTransactionStatus.WAITING_FOR_JESTA_EXECUTION_TIME.equals(transaction.getStatus())) {
            resId = R.drawable.ic_baseline_handshake_24;
        } else {
            resId = R.drawable.ic_baseline_thumb_up_off_alt_24;
        }
        imageView.setImageResource(resId);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    @BindingAdapter("shadow")
    public static void setShadow(androidx.cardview.widget.CardView cardView, FavorTransactionStatus status) {
        if (status == null) {
            return;
        }
        int resId = 0;
        if (FavorTransactionStatus.PENDING_FOR_OWNER.equals(status)) {
            resId = R.color.light_orange;
        } else if (FavorTransactionStatus.JESTA_DONE.equals(status)) {
            resId = R.color.green;
        } else if (FavorTransactionStatus.EXECUTOR_FINISH_JESTA.equals(status)) {
            resId = R.color.blue;
        }
        cardView.setOutlineAmbientShadowColor(resId);
        cardView.setOutlineSpotShadowColor(resId);

    }

    @SuppressLint("ResourceAsColor")
    @BindingAdapter("setColor")
    public static void setColor(Button btn, FavorTransactionStatus status) {
        if (status == null) {
            return;
        }
        int resId = 0, titleId = 0;
        if (FavorTransactionStatus.PENDING_FOR_OWNER.equals(status)) {
            resId = R.color.light_orange;
            titleId = R.string.accepted_by_me;
        } else if (FavorTransactionStatus.JESTA_DONE.equals(status)) {
            resId = R.color.green;
            titleId = R.string.view_rating;
        } else if (FavorTransactionStatus.WAITING_FOR_JESTA_EXECUTION_TIME.equals(status)) {
            resId = R.color.blue;
            titleId = R.string.navigate_now;
        } else if (FavorTransactionStatus.EXECUTOR_FINISH_JESTA.equals(status)) {
            resId = R.color.green;
            titleId = R.string.write_rating;
        } else {
            resId = R.color.black;
            titleId = R.string.ready_to_execute;
        }
        btn.setText(btn.getContext().getText(titleId));
        btn.setBackgroundResource(resId);
    }

    @BindingAdapter({"layoutVisibility", "currentUser", "ownerId"})
    public static void setVisibility(View view, String status, String currentUser, String ownerId) {
        if (status == null) {
            view.setVisibility(View.GONE);
            System.out.println("peleg - status is null");
            return;
        }
        System.out.println("peleg - status is " + status);
        if (FavorTransactionStatus.WAITING_FOR_JESTA_EXECUTION_TIME.toString().equals(status) ||
                FavorTransactionStatus.EXECUTOR_FINISH_JESTA.toString().equals(status) ||
                (FavorTransactionStatus.PENDING_FOR_OWNER.toString().equals(status) && currentUser.equals(ownerId))) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @SuppressLint("ResourceAsColor")
    @BindingAdapter("btnStatus")
    public static void setButton(AppCompatButton btn, String status) {
        if (status == null) {
            btn.setBackgroundTintList(null);
            btn.setBackgroundResource(R.color.black);
            btn.setText(R.string.suggest_help);
            btn.setTextColor(R.color.white);
            return;
        }
        if (status.equals(FavorTransactionStatus.WAITING_FOR_JESTA_EXECUTION_TIME.toString())) {
            btn.setBackgroundResource(R.color.green);
            btn.setText(R.string.approve_for_doing);
            btn.setTextColor(R.color.black);
        } else if (status.equals(FavorTransactionStatus.JESTA_DONE.toString()) ||
                status.equals(FavorTransactionStatus.EXECUTOR_FINISH_JESTA.toString())) {
            btn.setBackgroundResource(R.color.green);
            btn.setText(R.string.finished);
            btn.setTextColor(R.color.black);
        } else if (status.equals(FavorTransactionStatus.PENDING_FOR_OWNER.toString())) {
            btn.setBackgroundResource(R.color.light_orange);
            btn.setText(R.string.sent);
            btn.setTextColor(R.color.black);
        } else {
            btn.setBackgroundResource(R.color.black);
            btn.setText(R.string.suggest_help);
            btn.setTextColor(R.color.white);
        }
    }

    @BindingAdapter({"visibilityUserId", "visibilityOwnerId"})
    public static void setIconVisibility(View view, String userId, String ownerId) {
        if (ownerId == null)
            return;
        if (ownerId.equals(userId)) {
            view.setVisibility(View.VISIBLE);
        } else {
            view.setVisibility(View.GONE);
        }
    }

    @BindingAdapter({"status", "userId", "transaction"})
    public static void setStatus(TextView textView, String status, String currentId, Transaction transaction) {
        if (status == null)
            return;
        if (FavorTransactionStatus.JESTA_DONE.toString().equals(status) ||
                FavorTransactionStatus.CLOSED.toString().equals(status)) {
            textView.setText(R.string.finished);
        } else if (transaction != null && FavorTransactionStatus.PENDING_FOR_OWNER.toString().equals(status) &&
                currentId.equals(transaction.getFavorOwnerId().get_id())) {
            String title = transaction.getHandledByUserId().getFullName();
            textView.setText(title);
        } else if (FavorTransactionStatus.PENDING_FOR_OWNER.toString().equals(status)) {
            textView.setText(R.string.pending_for_owner);
        } else if (FavorTransactionStatus.WAITING_FOR_JESTA_EXECUTION_TIME.toString().equals(status)) {
            textView.setText(R.string.pendig_for_execution_time);
        } else if (FavorTransactionStatus.EXECUTOR_FINISH_JESTA.toString().equals(status)) {
            textView.setText(R.string.executor_finish_jesta);
        } else if (FavorTransactionStatus.CANCELED.toString().equals(status)) {
            textView.setText(R.string.canceled);
        }
    }

    @BindingAdapter("textStatus")
    public static void innerStatus(TextView textView, Transaction transaction) {
        if (transaction == null) {
            return;
        }
        switch (transaction.getStatus()) {
            case CLOSED:
            case JESTA_DONE:
                textView.setText(R.string.finished);
                break;
            case CANCELED:
                textView.setText(R.string.canceled);

                break;
            case PENDING_FOR_OWNER:
                textView.setText(R.string.pending_for_owner);

                break;
            case EXECUTOR_FINISH_JESTA:
                textView.setText(R.string.executor_finish_jesta);

                break;
            case WAITING_FOR_JESTA_EXECUTION_TIME:
                textView.setText(R.string.pendig_for_execution_time);

            default:
                Log.d("innerStatus", "status unrecognized " + transaction.getStatus());
                break;
        }
    }

    @BindingAdapter("setJestaImages")
    public static void setJestaImage(ImageView imageView, List<String> path) {
        if (path != null && path.size() > 0 && path.get(0).length() > 5) {
            String fullPath = Consts.SERVER_PRE_FIX + path.get(0);
            Picasso.with(imageView.getContext()).load(fullPath).into(imageView);
        }
    }

    @BindingAdapter({"userId", "transaction"})
    public static void setButtonVisibility(AppCompatButton button, String userId, Transaction transaction) {
        if (transaction == null || !transaction.getFavorOwnerId().get_id().equals(userId)) {
            button.setVisibility(View.GONE);
        } else {
            if (FavorTransactionStatus.PENDING_FOR_OWNER == transaction.getStatus()) {
                if (button.getId() == R.id.approve) {
                    button.setText(R.string.approve);
                } else {
                    button.setText(R.string.reject);
                }
                button.setVisibility(View.VISIBLE);
            } else if (FavorTransactionStatus.WAITING_FOR_JESTA_EXECUTION_TIME == transaction.getStatus()) {
                if (button.getId() == R.id.approve) {
                    button.setText(R.string.report);
                } else {
                    button.setText(R.string.cancel);
                }
                button.setVisibility(View.VISIBLE);
            } else if (FavorTransactionStatus.EXECUTOR_FINISH_JESTA == transaction.getStatus()) {
                if (button.getId() == R.id.approve) {
                    button.setText(R.string.write_rating);
                } else {
                    button.setText(R.string.back);
                }
                button.setVisibility(View.VISIBLE);
            } else {
                button.setVisibility(View.GONE);
            }
        }
    }
//
//    @BindingAdapter({"doneUserId", "doneOwnerId", "doneTransaction"})
//    public static void doneVisibilityAndClickability(AppCompatButton button, String userId, String ownerId, Transaction transaction){
//        if (userId.equals(ownerId)){
//            button.setVisibility(View.INVISIBLE);
//            button.setClickable(false);
//        }
//        else {
//            if (transaction == null){
//                System.out.println("peleg - doneVisibilityAndClickability transaction is null");
//                button.setVisibility(View.INVISIBLE);
//                button.setClickable(false);
//                return;
//            }
//            if (transaction.getStatus()==FavorTransactionStatus.WAITING_FOR_JESTA_EXECUTION_TIME){
//                button.setVisibility(View.VISIBLE);
//                button.setClickable(true);
//            }
//            else {
//                button.setVisibility(View.INVISIBLE);
//                button.setClickable(false);
//            }
//        }
//    }


    @BindingAdapter({"setText", "defaultText"})
    public static void setText(TextView view, String text, int resId) {
        if (text != null && !text.equals("") && !text.equals(Consts.INVALID_STRING)) {
            view.setText(text);
        } else {
            view.setText(view.getContext().getText(resId));
        }
    }

    @BindingAdapter({"OwnerId", "userId"})
    public static void setStatusTitle(TextView textView, String ownerId, String userID) {
        if (ownerId == null)
            return;
        if (ownerId.equals(userID)) {
            textView.setText(R.string.got_offer_from);
        } else {
            textView.setText(R.string.status);
        }
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter({"UserFullName"})
    public static void setUserName(TextView textView, User user) {
        if (user != null && user.get_firstName() != null && user.get_lastName() != null) {
            textView.setText(user.get_firstName() + " " + user.get_lastName());
        } else {
            textView.setText(R.string.full_name);
        }
    }

    @BindingAdapter({"firstName", "secondName"})
    public static void concatNames(TextView textView, String firstName, String secondName) {
        String title = firstName + " " + secondName;
        textView.setText(title);
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter({"setBirthday"})
    public static void setBirthday(TextView textView, String birthday) {
        if (birthday != null && !birthday.equals("")) {
            if (birthday.contains("T")) {
                String[] preBirthday = birthday.split("T");
                String[] unOrderDate = preBirthday[0].split("-");
                textView.setText(unOrderDate[1] + "/" + unOrderDate[2] + "/" + unOrderDate[0].substring(2, 4)); // TODO make sure this is correct
            } else {
                textView.setText(birthday);
            }
        } else {
            textView.setText(R.string.birthday);
        }
    }

    @BindingAdapter({"setPhone"})
    public static void setPhone(TextView textView, String phone) {
        if (phone != null && !phone.equals("")) {
            textView.setText(phone);
        } else {
            textView.setText(R.string.phone);
        }
    }

    @BindingAdapter({"notificationTransaction"})
    public static void setTitle(TextView textView, Transaction transaction) {
        String title = "";
        if (transaction.getStatus() == null)
            return;
        if (FavorTransactionStatus.JESTA_DONE.equals(transaction.getStatus())) {
            // 4
            title = textView.getContext().getString(R.string.favor_finish, transaction.getFavorOwnerId().get_firstName());
        } else if (FavorTransactionStatus.EXECUTOR_FINISH_JESTA.equals(transaction.getStatus())) {
            // 3
            title = textView.getContext().getString(R.string.executor_finish_favor, transaction.getHandledByUserId().get_firstName());
        } else if (FavorTransactionStatus.WAITING_FOR_JESTA_EXECUTION_TIME.equals(transaction.getStatus())) {
            //2
            title = textView.getContext().getString(R.string.favor_approved, transaction.getFavorOwnerId().get_firstName());
        } else if (FavorTransactionStatus.PENDING_FOR_OWNER.equals(transaction.getStatus())) {
            //1
            title = textView.getContext().getString(R.string.offer_favor, transaction.getHandledByUserId().get_firstName());
        } else if (FavorTransactionStatus.CANCELED.equals(transaction.getStatus())) {
            // both
        } else {
            Log.d("NotificationTitle", "Unrecognized status " + transaction.getStatus());
        }
        textView.setText(title);
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter("userAddress")
    public static void setUserAddressTitle(TextView textView, Place address) {
        if (address != null) {
            textView.setText(address.getAddress());
        } else {
            textView.setText(R.string.enter_address);
        }
    }

    @BindingAdapter(value = {"setDate", "defaultDateTitle"}, requireAll = false)
    public static void setDate(MaterialButton button, Date date, String defaultString) {
        if (date != null && date.getTime() > 0) {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            button.setText(sdf.format(date));
        } else {
            button.setText(defaultString);
        }
    }

    @SuppressLint("SetTextI18n")
    @BindingAdapter({"setAddress", "addressPrefix"})
    public static void setAddress(TextView textView, Place place, String prefix) {
        if (place != null) {
            textView.setText(prefix + " " + place.getAddress());
        }
    }

    @BindingAdapter(value = {"srcHour", "srcDate", "dstHour", "dstDate"}, requireAll = false)
    public static void setHoursTitle(TextView textView, Long srcHour, Date srcDate, Long dstHour, Date dstDate) {
        String srcDateStr, srcHourStr = "", dstHourSr = "", dstDateStr = "";
        if (srcDate == null) {
            srcDate = new Date(MaterialDatePicker.todayInUtcMilliseconds());
        }
        srcDateStr = dateConverter(textView, srcDate);
        if (srcHour != null) {
            srcHourStr = hourConverter(srcHour);
        }
        if (dstDate != null) {
            dstDateStr = dateConverter(textView, dstDate);
        }
        if (dstHour != null) {
            dstHourSr = hourConverter(dstHour);
        }
        String res = srcDateStr + " " + srcHourStr + " - " + dstDateStr + " " + dstHourSr;
        textView.setText(res);
    }

    @BindingAdapter({"sourceDate", "destDate"})
    public static void setTimeTitle(TextView textView, String src, String dest) {
        if (src == null)
            return;
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date dstDate = null, srcDate = null;
        String srcD = "", destD = "";
        try {
            srcDate = format.parse(src);
            if (dest != null)
                dstDate = format.parse(dest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (srcDate != null) {
            srcD = dateConverter(textView, srcDate);
        }
        if (dstDate != null) {
            destD = dateConverter(textView, dstDate);
        }
        textView.setText(srcD + " - " + destD);

    }

    @BindingAdapter("notificationDate")
    public static void notificationDate(TextView textView, String date) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        Date date1 = null;
        try {
            date1 = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if (date1.after(new Date(MaterialDatePicker.todayInUtcMilliseconds()))) {
            SimpleDateFormat newFormat = new SimpleDateFormat("HH:mm");
            textView.setText(newFormat.format(date1));
        } else if (date1.after(new Date(MaterialDatePicker.todayInUtcMilliseconds() - DAY_IN_MS))) {
            textView.setText(textView.getContext().getText(R.string.yesterday));
        } else {
            SimpleDateFormat newFormat = new SimpleDateFormat("dd/MM");
            textView.setText(newFormat.format(date1));
        }
    }

    @BindingAdapter({"jestaLocation", "myLocation"})
    public static void calacDistance(TextView textView, List<Double> jesstaLocation, LatLng myLocation) {
        if (myLocation == null || jesstaLocation == null)
            return;
        double distance = Utilities.calcCoordinateDistance(jesstaLocation, myLocation);
        String title = "";
        if (distance >= 1) {
            distance = Math.floor(distance);
            title = "" + (int) distance + " " + textView.getContext().getText(R.string.km);
        } else {
            distance = Math.floor(distance * 1000);
            title = "" + (int) distance + " " + textView.getContext().getText(R.string.meter);

        }
        textView.setText(title);
    }

    @BindingAdapter({"sourceLocation", "destLocation"})
    public static void calacDistanceList(TextView textView, GetJestaQuery.SourceAddress source, GetJestaQuery.DestinationAddress dest) {
        if (source != null && dest != null)
            calacDistance(textView, source.location.coordinates, new LatLng(dest.location.coordinates.get(0), dest.location.coordinates.get(1)));
    }

    @BindingAdapter("notificationImage")
    public static void notificationImage(ImageView imageView, FavorTransactionStatus status) {

    }

    @BindingAdapter("registeredSince")
    public static void registeredSince(TextView textView, String date) {
        // TODO: implement method
        textView.setText("כיום"); // TODO: use strings here
    }

    // region Private methods

    private static String dateConverter(View view, Date date) {
        String res = "";
        if (date.getTime() == MaterialDatePicker.todayInUtcMilliseconds()) {
            res = view.getContext().getString(R.string.today);
        } else if (date.getTime() == MaterialDatePicker.todayInUtcMilliseconds() + DAY_IN_MS) {
            res = view.getContext().getString(R.string.tommorow);
        } else {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
            res = sdf.format(date);
        }
        return res;
    }

    private static String hourConverter(Long hour) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        return sdf.format(new Date(hour));
    }

    // endregion
}
