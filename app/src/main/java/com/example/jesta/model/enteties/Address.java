package com.example.jesta.model.enteties;

import java.util.List;

public class Address {

    // region Members

    public String fullAddress;
    private List<Double> coordinates;

    // endregion

    // region C'tor

    public Address(String fullAddress, List<Double> coordinates) {
        this.fullAddress = fullAddress;
        this.coordinates = coordinates;
    }

    public Address(List<Double> coordinates) {
        this.coordinates = coordinates;
    }

    // endregion

    // region Properties

    public String getFullAddress() {
        return fullAddress;
    }

    public void setFullAddress(String fullAddress) {
        this.fullAddress = fullAddress;
    }

    public List<Double> getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(List<Double> coordinates) {
        this.coordinates = coordinates;
    }


    // ednregion

}
