package com.example.jesta.model.repositories;

import android.graphics.Bitmap;
import android.location.Address;
import android.net.Uri;

import androidx.lifecycle.MutableLiveData;

import com.example.jesta.common.enums.PaymentMethod;
import com.example.jesta.interfaces.ITabsNavigationHelper;
import com.google.android.libraries.places.api.model.Place;

import java.util.Date;

public class JestaRepository {

    // region Members

    private MutableLiveData<Integer> _category;
    private MutableLiveData<String> _description;
    private MutableLiveData<Integer> _numOfPeople;
    private MutableLiveData<Uri> image1;
    private MutableLiveData<Uri> image2;
    private MutableLiveData<Uri> image3;
    private MutableLiveData<Date> _startDate;
    private MutableLiveData<Long> _startTime;
    private MutableLiveData<Date> _endDate;
    private MutableLiveData<Long> _endTime;
    private MutableLiveData<Boolean> _isRepeatedly;
    private MutableLiveData<Place> _source;
    private MutableLiveData<Place> _destention;
    private MutableLiveData<PaymentMethod> _paymentMethod;
    private MutableLiveData<Integer> _amount;

    private ITabsNavigationHelper _tabsNavigationHelper;


    // endregion


    // region Singleton

    private static JestaRepository instance = null;

    public static JestaRepository getInstance(){
        if (instance == null)
            instance = new JestaRepository();
        return instance;
    }

    private JestaRepository(){
        _category = new MutableLiveData<>(0);
        _description = new MutableLiveData<>("");
        _numOfPeople = new MutableLiveData<>(0);
        image1 = new MutableLiveData<>();
        image2 = new MutableLiveData<>();
        image3 = new MutableLiveData<>();
        _startDate = new MutableLiveData<>();
        _startTime = new MutableLiveData<>();
        _endDate = new MutableLiveData<>();
        _endTime = new MutableLiveData<>();
        _isRepeatedly = new MutableLiveData<>(false);
        _source = new MutableLiveData<>();
        _destention = new MutableLiveData<>();
        _paymentMethod = new MutableLiveData<>(PaymentMethod.MOVE_IT_FORWARD);
        _amount = new MutableLiveData<>(0);
    }

    // endregion

    // region Properties

    public ITabsNavigationHelper get_tabsNavigationHelper() {
        return _tabsNavigationHelper;
    }

    public void set_tabsNavigationHelper(ITabsNavigationHelper _tabsNavigationHelper) {
        this._tabsNavigationHelper = _tabsNavigationHelper;
    }

    public MutableLiveData<Integer> get_category() {
        return _category;
    }

    public MutableLiveData<String> get_description() {
        return _description;
    }

    public MutableLiveData<Integer> get_numOfPeople() {
        return _numOfPeople;
    }

    public MutableLiveData<Uri> getImage1() {
        return image1;
    }

    public MutableLiveData<Uri> getImage2() {
        return image2;
    }

    public MutableLiveData<Uri> getImage3() {
        return image3;
    }

    public MutableLiveData<Date> get_startDate() {
        return _startDate;
    }

    public MutableLiveData<Long> get_startTime() {
        return _startTime;
    }

    public MutableLiveData<Date> get_endDate() {
        return _endDate;
    }

    public MutableLiveData<Long> get_endTime() {
        return _endTime;
    }

    public MutableLiveData<Boolean> get_isRepeatedly() {
        return _isRepeatedly;
    }

    public MutableLiveData<Place> get_source() {
        return _source;
    }

    public MutableLiveData<Place> get_destention() {
        return _destention;
    }

    public MutableLiveData<PaymentMethod> get_paymentMethod() {
        return _paymentMethod;
    }

    public MutableLiveData<Integer> get_amount() {
        return _amount;
    }

    // endregion


}
