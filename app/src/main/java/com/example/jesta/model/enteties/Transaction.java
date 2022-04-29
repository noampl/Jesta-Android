package com.example.jesta.model.enteties;

import com.example.jesta.GetUserQuery;
import com.example.jesta.common.enums.FavorTransactionStatus;


import java.util.Objects;

public class Transaction {

    // region Members

    private String _id;
    private FavorTransactionStatus status;
    private Jesta favorId;
    private User favorOwnerId;
    private User handledByUserId;
    private String dateLastModified;

    // endregion

    public Transaction(String _id, FavorTransactionStatus status, Jesta favorId, User favorOwnerId, User handledByUserId, String dateLastModified) {
        this._id = _id;
        this.status = status;
        this.favorId = favorId;
        this.favorOwnerId = favorOwnerId;
        this.handledByUserId = handledByUserId;
        this.dateLastModified = dateLastModified;
    }

    // endregion

    //region Properties

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public FavorTransactionStatus getStatus() {
        return status;
    }

    public void setStatus(FavorTransactionStatus status) {
        this.status = status;
    }

    public Jesta getFavorId() {
        return favorId;
    }

    public void setFavorId(Jesta favorId) {
        this.favorId = favorId;
    }

    public User getFavorOwnerId() {
        return favorOwnerId;
    }

    public void setFavorOwnerId(User favorOwnerId) {
        this.favorOwnerId = favorOwnerId;
    }

    public User getHandledByUserId() {
        return handledByUserId;
    }

    public void setHandledByUserId(User handledByUserId) {
        this.handledByUserId = handledByUserId;
    }

    public String getDateLastModified() {
        return dateLastModified;
    }

    public void setDateLastModified(String dateLastModified) {
        this.dateLastModified = dateLastModified;
    }


    // endregion

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return _id.equals(that._id) && status.equals(that.status) && favorId.equals(that.favorId) && favorOwnerId.equals(that.favorOwnerId) && handledByUserId.equals(that.handledByUserId) && dateLastModified.equals(that.dateLastModified);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, status, favorId, favorOwnerId, handledByUserId, dateLastModified);
    }
}
