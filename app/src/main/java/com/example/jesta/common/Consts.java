package com.example.jesta.common;

public class Consts {

    public static final String TOKEN = "Token";
    public static final String EMAIL = "Email";
    public static final String ID = "Id";
    public static final String PASSWORD = "Password";
    public static final String INVALID_STRING = "None";
    public static final String TOKEN_PREFIX ="Bearer";


    // region Validation

    public static final String EMAIL_VALIDATOR = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    public static final String PASSWORD_VALIDATOR = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$";
    public static final String PHONE_VALIDATOR = "^\\d{10}$";

    // endregion

    // region Google Maps

    public static final int CLOSE_ZOOM = 15;
    public static final int CITY_ZOOM = 10;
    public static final int COUNTRY_ZOOM = 7;
    public static final int INVALID_INTEGER = -1;

    // endregion


}
