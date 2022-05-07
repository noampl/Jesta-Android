package com.example.jesta.model.repositories;

import android.location.Address;
import android.net.Uri;

import androidx.core.util.Pair;
import androidx.lifecycle.MutableLiveData;

import com.example.jesta.GetFavorsByRadiosTimeAndDateQuery;
import com.example.jesta.GetJestaQuery;
import com.example.jesta.interfaces.ITabsNavigationHelper;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.type.PaymentType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.model.Place;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okio.Source;

public class JestaRepository {

    // region Members

    private final MutableLiveData<Integer> _category;
    private final MutableLiveData<String> _description;
    private final MutableLiveData<Integer> _numOfPeople;
    private final MutableLiveData<Pair<Uri, Source>> image1;
    private final MutableLiveData<Pair<Uri, Source>> image2;
    private final MutableLiveData<Pair<Uri, Source>> image3;
    private final MutableLiveData<Date> _startDate;
    private final MutableLiveData<Long> _startTime;
    private final MutableLiveData<Date> _endDate;
    private final MutableLiveData<Long> _endTime;
    private final MutableLiveData<Boolean> _isRepeatedly;
    private final MutableLiveData<Place> _source;
    private final MutableLiveData<Place> _destination;
    private final MutableLiveData<PaymentType> _paymentType;
    private final MutableLiveData<Integer> _amount;
    private final MutableLiveData<GetJestaQuery.GetFavor> _jestaDetails;
    private final MutableLiveData<Boolean> _isSuggestHelp;
    private String _comment;

    private ITabsNavigationHelper _tabsNavigationHelper;
    private ExecutorService _executorService;
    private final MutableLiveData<List<GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable>> _jestas;
    private final MutableLiveData<String> _favorTransactionStatus;
    private final MutableLiveData<Transaction> _detailsTransaction;

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
        _destination = new MutableLiveData<>();
        _paymentType = new MutableLiveData<>(PaymentType.FREE);
        _amount = new MutableLiveData<>(0);
        _jestas = new MutableLiveData<>(new ArrayList<>());
        _jestaDetails = new MutableLiveData<>();
        _executorService = Executors.newSingleThreadExecutor();
        _comment = "";
        _isSuggestHelp = new MutableLiveData<>(false);
        _favorTransactionStatus = new MutableLiveData<>();
        _detailsTransaction = new MutableLiveData<>();
    }

    // endregion

    // region Properties

    public MutableLiveData<Transaction> get_detailsTransaction() {
        return _detailsTransaction;
    }

    public MutableLiveData<String> get_favorTransactionStatus() {
        return _favorTransactionStatus;
    }

    public MutableLiveData<Boolean> get_isSuggestHelp() {
        return _isSuggestHelp;
    }

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

    public MutableLiveData<Pair<Uri, Source>> getImage1() {
        return image1;
    }

    public MutableLiveData<Pair<Uri, Source>> getImage2() {
        return image2;
    }

    public MutableLiveData<Pair<Uri, Source>> getImage3() {
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

    public MutableLiveData<Place> get_destination() {
        return _destination;
    }

    public MutableLiveData<PaymentType> get_paymentType() {
        return _paymentType;
    }

    public MutableLiveData<Integer> get_amount() {
        return _amount;
    }

    public MutableLiveData<List<GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable>> get_jestas(){return _jestas;}

    public MutableLiveData<GetJestaQuery.GetFavor> get_jestaDetails() {
        return _jestaDetails;
    }

    public void set_jestas(List<GetFavorsByRadiosTimeAndDateQuery.GetByRadiosAndDateAndOnlyAvailable> jestas){
        _jestas.postValue(jestas);
    }

    public void set_jestaDetails(GetJestaQuery.GetFavor jestaDetails){
        _jestaDetails.postValue(jestaDetails);
    }

    public String get_comment() {
        return _comment;
    }

    public void set_isSuggestHelp(Boolean bool){
        _isSuggestHelp.postValue(bool);
    }

    public void set_favorTransactionStatus(String status){
        _favorTransactionStatus.postValue(status);
    }

    // endregion


    // region Public Methods

    public void setSourceAddressByCurrentLocation() {
        _executorService.execute(()->{
            Address address = MapRepository.getInstance().setAddressByCurrentLocation();
            _source.postValue(Place.builder().setAddress(address.getAddressLine(0))
                    .setLatLng(new LatLng(address.getLatitude(), address.getLongitude())).build());
        });
    }

    public void setTransaction(Transaction transaction) {
        _detailsTransaction.postValue(transaction);
    }

    // ednregion
}
