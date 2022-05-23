package com.example.jesta.model.enteties;


import java.util.List;
import java.util.Objects;

public class Jesta {

    // region Properties

    private String _id;
    private String status;
    private String ownerId;
    private Address sourceAddress;
    private int numOfPeople;
    private String dateToExecute;
    private String dateToFinishExecute;
    private List<String> categories;

    // endregion

    // region C'tor

    public Jesta(String _id, String status, String ownerId, Address sourceAddress) {
        this._id = _id;
        this.status = status;
        this.ownerId = ownerId;
        this.sourceAddress = sourceAddress;
    }

    public Jesta(String _id, String status, String ownerId, Address sourceAddress, int numOfPeople,
                 String dateToExecute, String dateToFinishExecute) {
        this._id = _id;
        this.status = status;
        this.ownerId = ownerId;
        this.sourceAddress = sourceAddress;
        this.numOfPeople = numOfPeople;
        this.dateToExecute = dateToExecute;
        this.dateToFinishExecute = dateToFinishExecute;
    }

    public Jesta(String _id, String status, String ownerId, Address sourceAddress, int numOfPeople,
                 String dateToExecute, String dateToFinishExecute, List<String> categories) {
        this._id = _id;
        this.status = status;
        this.ownerId = ownerId;
        this.sourceAddress = sourceAddress;
        this.numOfPeople = numOfPeople;
        this.dateToExecute = dateToExecute;
        this.dateToFinishExecute = dateToFinishExecute;
        this.categories = categories;
    }

    public Jesta(String id, String status, String ownerId) {
        this._id = id;
        this.status = status;
        this.ownerId = ownerId;
    }

    // endregion

    // region Properties

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public Address getSourceAddress() {
        return sourceAddress;
    }

    public void setSourceAddress(Address sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public int getNumOfPeople() {
        return numOfPeople;
    }

    public void setNumOfPeople(int numOfPeople) {
        this.numOfPeople = numOfPeople;
    }

    public String getDateToExecute() {
        return dateToExecute;
    }

    public void setDateToExecute(String dateToExecute) {
        this.dateToExecute = dateToExecute;
    }

    public String getDateToFinishExecute() {
        return dateToFinishExecute;
    }

    public void setDateToFinishExecute(String dateToFinishExecute) {
        this.dateToFinishExecute = dateToFinishExecute;
    }

    // endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Jesta jesta = (Jesta) o;
        return numOfPeople == jesta.numOfPeople && _id.equals(jesta._id) && status.equals(jesta.status) && ownerId.equals(jesta.ownerId) && sourceAddress.equals(jesta.sourceAddress);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, status, ownerId);
    }
}
