package com.example.jesta.common;

public class Consts {

    public static final String TOKEN = "Token";
    public static final String EMAIL = "Email";
    public static final String PASSWORD = "Password";
    public static final String INVALID_STRING = "None";
    public static final String TOKEN_PREFIX ="Bearer";


    // region Validation

    public static final String EMAIL_VALIDATOR = "^[\\w!#$%&'*+/=?`{|}~^-]+(?:\\.[\\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
    public static final String PASSWORD_VALIDATOR = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,20}$";
    public static final String PHONE_VALIDATOR = "^\\d{10}$";

    // endregion

}
