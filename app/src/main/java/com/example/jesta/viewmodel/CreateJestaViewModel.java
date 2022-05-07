package com.example.jesta.viewmodel;


import android.annotation.SuppressLint;
import android.net.Uri;
import android.widget.CompoundButton;

import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo3.api.DefaultUpload;
import com.apollographql.apollo3.api.Optional;
import com.apollographql.apollo3.api.Upload;
import com.example.jesta.common.Consts;
import com.example.jesta.interfaces.ITabsNavigationHelper;
import com.example.jesta.model.repositories.GraphqlRepository;
import com.example.jesta.model.repositories.JestaRepository;
import com.example.jesta.model.repositories.UsersRepository;
import com.example.jesta.type.AddressInput;

import com.example.jesta.type.CoordinatesInput;
import com.example.jesta.type.FavorInput;
import com.example.jesta.type.PaymentType;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okio.Okio;
import okio.Source;

public class CreateJestaViewModel extends ViewModel {

    // region Members
    private final MutableLiveData<Integer> _category;
    private ITabsNavigationHelper navigationHelper;
    private final MutableLiveData<String> _description;
    private final MutableLiveData<Pair<Uri, Source>> image1;
    private final MutableLiveData<Pair<Uri, Source>> image2;
    private final MutableLiveData<Pair<Uri, Source>> image3;
    private final MutableLiveData<Date> _startDate;
    private final MutableLiveData<Long> _startTime;
    private final MutableLiveData<Date> _endDate;
    private final MutableLiveData<Long> _endTime;
    private final MutableLiveData<Boolean> _isRepeatedly;
    private final MutableLiveData<Place> _source;
    private final MutableLiveData<Place> _destention;
    private final MutableLiveData<PaymentType> _paymentType;
    private final MutableLiveData<Integer> _numOfPeople;
    private final MutableLiveData<Integer> _amount;
    private List<Upload> _images;

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
        _destention = JestaRepository.getInstance().get_destination();
        _paymentType = JestaRepository.getInstance().get_paymentType();
        _category = JestaRepository.getInstance().get_category();
        _numOfPeople = JestaRepository.getInstance().get_numOfPeople();
        _amount = JestaRepository.getInstance().get_amount();
        _images = new ArrayList<>();
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

    public MutableLiveData<Pair<Uri, Source>> getImage1() {
        return image1;
    }

    public void setImage1(Pair<Uri, Source> image1) {
        this.image1.setValue(image1);
    }

    public MutableLiveData<Pair<Uri, Source>> getImage2() {
        return image2;
    }

    public void setImage2(Pair<Uri, Source> image2) {
        this.image2.setValue(image2);
    }

    public MutableLiveData<Pair<Uri, Source>> getImage3() {
        return image3;
    }

    public void setImage3(Pair<Uri, Source> image3) {
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

    public MutableLiveData<PaymentType> get_paymentType() {
        return _paymentType;
    }

    public void set_paymentType(PaymentType _paymentType) {
        this._paymentType.setValue(_paymentType);
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

    /**
     * Create new jesta by the vm params
     */
    public boolean createJesta() {
        if (get_category() == null || get_category().getValue() == 0){
            return false;
        }
        GraphqlRepository.getInstance().createJesta(jestaConverter(),new Optional.Present<>(_images));
        return true;
    }

    // endregion

    // region Private Methods

    /**
     * Convert the vm members data into Optional favor input
     *
     * @return the jesta data
     */
    private com.apollographql.apollo3.api.Optional<FavorInput> jestaConverter(){
        return new Optional.Present<>(
                new FavorInput(UsersRepository.getInstance().get_myUser().getValue().get_id(),
                               new ArrayList<String>(), new Optional.Present<>(get_numOfPeople().getValue()),
                               addressConverter(get_source().getValue()),
                               new Optional.Present<>(addressConverter(get_destention().getValue())),
                               new Optional.Present<>(get_description().getValue()),new Optional.Present<Double>(Double.valueOf(get_amount().getValue())),
                               get_paymentType().getValue(),new Optional.Present<>(null) , new Optional.Present<>(concatDateAndTime(get_startDate().getValue(),get_startTime().getValue())),
                               new Optional.Present<>(concatDateAndTime(get_endDate().getValue(), get_endTime().getValue())),new Optional.Present<>(null)));
    }

    /**
     * Convert Google Place to AddressInput
     *
     * @param address The Place to convert
     * @return An AddressInput
     */
    private AddressInput addressConverter(Place address) {
        if (address == null)
            return null;
        List<Double> coordinateList = new ArrayList<>();
        coordinateList.add(address.getLatLng().latitude);
        coordinateList.add(address.getLatLng().longitude);

        return new AddressInput(new Optional.Present<>(address.getAddress()),
                new Optional.Present<>(new CoordinatesInput(new Optional.Present<>(coordinateList))));

    }

    private Long concatDateAndTime(Date date, Long hour){
        if (date == null)
            return null;
        if (hour == null)
            hour = 0L;
        return date.getTime() + hour;
    }

    /**
     * Checks if the needed param are initalized;
     */
    public void validSummaryDetails() {
        if (get_source() == null || get_source().getValue() == null ||
                get_source().getValue().getAddress() == null || get_source().getValue().getAddress().length() < 2){
            JestaRepository.getInstance().setSourceAddressByCurrentLocation();
        }
        if (get_startDate().getValue() == null || get_startDate().getValue().getTime() == 0){
            set_startDate(MaterialDatePicker.todayInUtcMilliseconds());
        }
    }

    public void initUploadImage(Source filePath) {
        DefaultUpload upload = new DefaultUpload.Builder()
                .content(Okio.buffer(filePath))
                .fileName("_" + Consts.JPG)
                .build();
        if (!_images.contains(upload)){
            _images.add(upload);
        }
    }

    public void clearData() {
        navigationHelper = null;
        set_description("");
        _images.clear();
        _startDate.setValue(null);
        _endDate.setValue(null);
        _startTime.setValue(null);
        _endTime.setValue(null);
        _isRepeatedly.setValue(false);
        _source.setValue(null);
        _destention.setValue(null);
        _paymentType.setValue(PaymentType.FREE);
        _category.setValue(0);
        _numOfPeople.setValue(0);
        _amount.setValue(0);
    }

    // endregion
}
