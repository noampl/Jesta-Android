package com.example.jesta.model.enteties;


import com.example.jesta.GetUserQuery;

public class User {

    // region Members

    private String _id;
    private String _firstName;
    private String _lastName;
    private String _birthday;
    private String _email;
    private String _phone;
    private String _role;
    private String _imagePath;
    private String _description;
    private GetUserQuery.Address _address;
    private double _rating;
    private String _dateRegistered;
    private int _numOfJestasDone;
    private boolean _isTopJestioner;

    // endregion

    // region C'tor

    public User(String _id){
        this._id = _id;
    }

    public User(String _id, String _firstName, String _lastName, String _birthday, String _email, String _phone, String _role, String _imagePath, GetUserQuery.Address _address) {
        this._id = _id;
        this._firstName = _firstName;
        this._lastName = _lastName;
        this._birthday = _birthday;
        this._email = _email;
        this._phone = _phone;
        this._role = _role;
        this._imagePath = _imagePath;
        this._address = _address;
    }

    public User(String _id, String firstName, String lastName) {
        this._id = _id;
        this._firstName = firstName;
        this._lastName = lastName;
    }

    public User(String _id, String firstName, String lastName, double _rating, String _imagePath) {
        this._id = _id;
        this._firstName = firstName;
        this._lastName = lastName;
        this._rating = _rating;
        this._imagePath = _imagePath;
    }

    // endregion

    // region Properties

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String get_firstName() {
        return _firstName;
    }

    public void set_firstName(String _firstName) {
        this._firstName = _firstName;
    }

    public String get_lastName() {
        return _lastName;
    }

    public void set_lastName(String _lastName) {
        this._lastName = _lastName;
    }

    public String get_birthday() {
        return _birthday;
    }

    public void set_birthday(String _birthday) {
        this._birthday = _birthday;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_phone() {
        return _phone;
    }

    public void set_phone(String _phone) {
        this._phone = _phone;
    }

    public String get_role() {
        return _role;
    }

    public void set_role(String _role) {
        this._role = _role;
    }

    public String get_imagePath() {
        return _imagePath;
    }

    public void set_imagePath(String _imagePath) {
        this._imagePath = _imagePath;
    }

    public GetUserQuery.Address get_address() {
        return _address;
    }

    public void set_fullAddress(String _address) {
        this._address.fullAddress = _address;
    }

    public double get_rating() {
        return _rating;
    }

    public void set_rating(double _rating) {
        this._rating = _rating;
    }

    public void setDescription(String description) {
        _description = description;
    }

    public String getDescription() {
        return _description;
    }

    public void setDateRegistered(String dateRegistered) {
        _dateRegistered = dateRegistered;
    }

    public String getDateRegistered() {
        return _dateRegistered;
    }

    public int get_numOfJestasDone() {
        return _numOfJestasDone;
    }

    public void set_numOfJestasDone(int _numOfJestasDone) {
        this._numOfJestasDone = _numOfJestasDone;
    }

    public boolean is_isTopJestioner() {
        return _isTopJestioner;
    }

    public void set_isTopJestioner(boolean _isTopJestioner) {
        this._isTopJestioner = _isTopJestioner;
    }

    // endregion

    public String getFullName() {
        return _firstName + " " + _lastName;
    }
}
