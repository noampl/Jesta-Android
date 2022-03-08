package com.example.jesta.viewmodel;


import android.annotation.SuppressLint;

import androidx.lifecycle.ViewModel;

import com.example.jesta.interfaces.ITabsNavigationHelper;
import com.example.jesta.model.repositories.JestaRepository;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateJestaViewModel extends ViewModel {

    // region Members

    private ITabsNavigationHelper navigationHelper;

    // endregion

    // region C'tor

    public CreateJestaViewModel() {
        navigationHelper = JestaRepository.getInstance().get_tabsNavigationHelper();
    }

    // endregion

    // region Properties

    public void setNavigationHelper(ITabsNavigationHelper navigationHelper) {
        this.navigationHelper = navigationHelper;
        JestaRepository.getInstance().set_tabsNavigationHelper(navigationHelper);
    }

    public void setStartDate(Long selection) {
        // TODO
    }
    public void setEndDate(Long selection) {
        //TODO
    }

    // endregion

    // region Public Methods

    public void setCategorySelected(int i) {
    }

    public void setOnAmountSpinnerSelected(int i) {
    }

    /**
     * Navigate to a different tab in tab layout
     * @param position The position of the tab in the layout(0 - n-1)
     */
    public void navigateToTab(int position){
        navigationHelper.moveTab(position);
    }

    /**
     * Gets datapicker result and convert it to display text
     * @param selection The date selected;
     * @return The date in dd/MM/yy
     */
    public String convertDateSelection(Long selection) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        return sdf.format(new Date(selection));
    }

    /**
     * Convert hour and minute to string\
     *
     * @param hour the hour
     * @param minute the min
     *
     * @return "HH:MM"
     */
    public String convertTimeToText(int hour, int minute) {
        String res = "";
        if (hour < 10){
            res = "0" + (hour);
        }
        else {
            res = "" + (hour);
        }
        if (minute == 0){
            res = res + ":0" + minute;
        }
        else{
            res = res + ":" + minute;
        }
        return res;

    }

    // endregion
}
