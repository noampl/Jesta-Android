package com.example.jesta.viewmodel;


import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.location.Address;
import android.net.Uri;
import android.widget.CompoundButton;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.jesta.common.enums.PaymentMethod;
import com.example.jesta.interfaces.ITabsNavigationHelper;
import com.example.jesta.model.repositories.JestaRepository;
import com.google.android.libraries.places.api.model.Place;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CreateJestaViewModel extends ViewModel {

    // region Members
    private final MutableLiveData<Integer> _category;
    private ITabsNavigationHelper navigationHelper;
    private final MutableLiveData<String> _description;
    private final MutableLiveData<Uri> image1;
    private final MutableLiveData<Uri> image2;
    private final MutableLiveData<Uri> image3;
    private final MutableLiveData<Date> _startDate;
    private final MutableLiveData<Long> _startTime;
    private final MutableLiveData<Date> _endDate;
    private final MutableLiveData<Long> _endTime;
    private final MutableLiveData<Boolean> _isRepeatedly;
    private final MutableLiveData<Place> _source;
    private final MutableLiveData<Place> _destention;
    private final MutableLiveData<PaymentMethod> _paymentMethod;
    private final MutableLiveData<Integer> _numOfPeople;
    private final MutableLiveData<Integer> _amount;

    // endregion

    // region C'tor

    public CreateJestaViewModel() {
        navigationHelper = JestaRepository.getInstance().get_tabsNavigationHelper();
        _description = JestaRepository.getInstance().get_description();
        image1 = JestaRepository.getInstance().getImage1();
        image2 = JestaRepository.getInstance().getImage2();
        image3 = JestaRepository.getInstance().getImage3();
        _startDate = JestaRepository.getInstance().get_startDate();
        _endDate = JestaRepository.getInstance().get_endDate();
        _startTime = JestaRepository.getInstance().get_startTime();
        _endTime = JestaRepository.getInstance().get_endTime();
        _isRepeatedly = JestaRepository.getInstance().get_isRepeatedly();
        _source = JestaRepository.getInstance().get_source();
        _destention = JestaRepository.getInstance().get_destention();
        _paymentMethod = JestaRepository.getInstance().get_paymentMethod();
        _category = JestaRepository.getInstance().get_category();
        _numOfPeople = JestaRepository.getInstance().get_numOfPeople();
        _amount = JestaRepository.getInstance().get_amount();
    }

    // endregion

    // region Properties


    public ITabsNavigationHelper getNavigationHelper() {
        return navigationHelper;
    }

    public void setNavigationHelper(ITabsNavigationHelper iTabsNavigationHelper){
        JestaRepository.getInstance().set_tabsNavigationHelper(iTabsNavigationHelper);
        navigationHelper = iTabsNavigationHelper;
    }

    public MutableLiveData<Integer> get_category() {
        return _category;
    }

    public void set_category(Integer _category) {
        this._category.setValue(_category);
    }

    public MutableLiveData<Integer> get_numOfPeople() {
        return _numOfPeople;
    }

    public void set_numOfPeople(Integer _numOfPeople) {
        this._numOfPeople.setValue(_numOfPeople);
    }

    public MutableLiveData<Integer> get_amount() {
        return _amount;
    }

    public void set_amount(Integer _amount) {
        this._amount.setValue(_amount);
    }

    public MutableLiveData<String> get_description() {
        return _description;
    }

    public void set_description(String _description) {
        this._description.setValue(_description);
    }

    public MutableLiveData<Uri> getImage1() {
        return image1;
    }

    public void setImage1(Uri image1) {
        this.image1.setValue(image1);
    }

    public MutableLiveData<Uri> getImage2() {
        return image2;
    }

    public void setImage2(Uri image2) {
        this.image2.setValue(image2);
    }

    public MutableLiveData<Uri> getImage3() {
        return image3;
    }

    public void setImage3(Uri image3) {
        this.image3.setValue(image3);
    }

    public MutableLiveData<Date> get_startDate() {
        return _startDate;
    }

    public void set_startDate(Long _startDate) {
        this._startDate.setValue(new Date(_startDate));
    }

    public MutableLiveData<Long> get_startTime() {
        return _startTime;
    }

    public void set_startTime(Long _startTime) {
        this._startTime.setValue(_startTime);
    }

    public MutableLiveData<Date> get_endDate() {
        return _endDate;
    }

    public void set_endDate(Long _endDate) {
        this._endDate.setValue(new Date(_endDate));
    }

    public MutableLiveData<Long> get_endTime() {
        return _endTime;
    }

    public void set_endTime(Long _endTime) {
        this._endTime.setValue(_endTime);
    }

    public MutableLiveData<Boolean> get_isRepeatedly() {
        return _isRepeatedly;
    }

    public void set_isRepeatedly(Boolean _isRepeatedly) {
        this._isRepeatedly.setValue(_isRepeatedly);
    }

    public MutableLiveData<Place> get_source() {
        return _source;
    }

    public void set_source(Place _source) {
        this._source.setValue(_source);
    }

    public MutableLiveData<Place> get_destention() {
        return _destention;
    }

    public void set_destention(Place _destention) {
        this._destention.setValue(_destention);
    }

    public MutableLiveData<PaymentMethod> get_paymentMethod() {
        return _paymentMethod;
    }

    public void set_paymentMethod(PaymentMethod _paymentMethod) {
        this._paymentMethod.setValue(_paymentMethod);
    }


    // endregion

    // region Public Methods

    public void setCategorySelected(int i) {
        set_category(i);
    }

    public void setOnAmountSpinnerSelected(int i) {
        set_numOfPeople(i);
    }

    /**
     * React to reapt checks
     *
     * @param compoundButton The btn who changes
     * @param b - the val of the btn
     */
    public void onRepeatChecked(CompoundButton compoundButton, boolean b){
        set_isRepeatedly(b);
    }

    /**
     * React to description change events
     * @param s The new description
     * @param start
     * @param before
     * @param count
     */
    public void OnDescriptionChange(CharSequence s, int start, int before, int count){
        set_description(s.toString());
    }

    /**
     * React to amount changes event
     *
     * @param s
     * @param start
     * @param before
     * @param count
     */
    public void onAmountChange(CharSequence s, int start, int before, int count){
        if (s != null && !s.toString().equals(""))
            set_amount(Integer.parseInt(s.toString()));
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
