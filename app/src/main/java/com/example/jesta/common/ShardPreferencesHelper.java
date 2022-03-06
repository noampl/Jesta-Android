package com.example.jesta.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import java.io.IOException;
import java.security.GeneralSecurityException;


public class ShardPreferencesHelper {

    private static SharedPreferences _sharedPreferences;

    public static void init(Context context) throws GeneralSecurityException, IOException {
        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
        String mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

        String sharedPrefsFile = "jesta-preferences";
        _sharedPreferences = EncryptedSharedPreferences.create(
                sharedPrefsFile,
                mainKeyAlias,
                context,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        );

    }
    // region Write

    public static void writeToken(String token){
        writeItem(Consts.TOKEN,token);
    }

    public static void writeEmail(String email){
        writeItem(Consts.EMAIL,email);
    }

    public static void writePassword(String password){
        writeItem(Consts.PASSWORD,password);
    }

    // endregion

    // region Read

    public static String readToken(){
        return _sharedPreferences.getString(Consts.TOKEN,Consts.INVALID_STRING);
    }

    public static String readEmail(){
        return _sharedPreferences.getString(Consts.EMAIL,Consts.INVALID_STRING);
    }

    public static String readPassword(){
        return _sharedPreferences.getString(Consts.PASSWORD,Consts.INVALID_STRING);
    }

    // endregion


    // region Private Methods

    private static void writeItem(String key, String data){
            SharedPreferences.Editor editor = _sharedPreferences.edit();
            editor.clear();
            editor.putString(key,data);
            editor.apply();
    }

    // endregion
}
