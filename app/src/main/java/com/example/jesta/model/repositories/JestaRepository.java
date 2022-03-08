package com.example.jesta.model.repositories;

import android.graphics.Bitmap;
import android.location.Address;

import com.example.jesta.interfaces.ITabsNavigationHelper;

import java.util.Date;

public class JestaRepository {

    // region Members

    private Integer _category;
    private String _description;
    private Integer _numOfPeople;
    private Bitmap image1;
    private Bitmap image2;
    private Bitmap image3;
    private Date _startDate;
    private Long _startTime;
    private Date _endDate;
    private Long _endTime;
    private Boolean _isRepeatedly;
    private Address _source;
    private Address _destention;
    //private paymentMethod
    private Double _amount;
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

    }

    // endregion

    // region Properties

    public ITabsNavigationHelper get_tabsNavigationHelper() {
        return _tabsNavigationHelper;
    }

    public void set_tabsNavigationHelper(ITabsNavigationHelper _tabsNavigationHelper) {
        this._tabsNavigationHelper = _tabsNavigationHelper;
    }

    // end region


}
