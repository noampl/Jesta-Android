package com.example.jesta.model.enteties;


public class Jesta {

    // region Properties

    private String _id;
    private String status;
    private String ownerId;
    private Address sourceAddress;

    // endregion

    // region C'tor

    public Jesta(String _id, String status, String ownerId, Address sourceAddress) {
        this._id = _id;
        this.status = status;
        this.ownerId = ownerId;
        this.sourceAddress = sourceAddress;
    }

    // endregion

    // region Properties

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

    // endregion

}
