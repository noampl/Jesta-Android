package com.example.jesta.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.security.keystore.KeyGenParameterSpec;

import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.example.MyApplication;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;


public class ShardPreferencesHelper {

    private static SharedPreferences _sharedPreferences;

    public static void init() throws GeneralSecurityException, IOException {
        KeyGenParameterSpec keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC;
        String mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec);

        String sharedPrefsFile = "jesta-preferences";
        _sharedPreferences = EncryptedSharedPreferences.create(
                sharedPrefsFile,
                mainKeyAlias,
                MyApplication.getAppContext(),
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

    public static void writeId(String Id){
        writeItem(Consts.ID, Id);
    }

    public static void writeRadius(double radius){writeItem(Consts.RADIUS, String.valueOf(radius));}

    public static void writeLat(double lat){writeItem(Consts.LAT, String.valueOf(lat));}

    public static void writeLng(double lng){writeItem(Consts.LNG, String.valueOf(lng));}


    // endregion

    // region Read

    public static String readToken(){
        return readItem(Consts.TOKEN);
    }

    public static String readEmail(){
        return readItem(Consts.EMAIL);
    }

    public static String readPassword(){
        return readItem(Consts.PASSWORD);
    }

    public static String readId(){return  readItem(Consts.ID);}

    public static double readRadius(){
        String d = readItem(Consts.RADIUS);
        if (d.equals(Consts.INVALID_STRING)){
            return 50;
        }
        return Double.parseDouble(d);
    }

    public static double readLat(){
        String d = readItem(Consts.LAT);
        if (d.equals(Consts.INVALID_STRING)){
            return 32.0860661;
        }
        return Double.parseDouble(d);
    }

    public static double readLng(){
        String d = readItem(Consts.LNG);
        if (d.equals(Consts.INVALID_STRING)){
            return 34.7908255;
        }
        return Double.parseDouble(d);
    }


    // endregion


    // region Private Methods

    private static void writeItem(String key, String data){
        if (_sharedPreferences == null){
            try {
                init();

            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
                return;
            }
        }
        SharedPreferences.Editor editor = _sharedPreferences.edit();
        editor.putString(key,data);
        editor.apply();
    }

    private static String readItem(String key){
        if (_sharedPreferences == null){
            try {
                init();
            } catch (GeneralSecurityException | IOException e) {
                e.printStackTrace();
                return Consts.INVALID_STRING;
            }
        }
        Map<String, String > map = (Map<String, String>) _sharedPreferences.getAll();
        return map.getOrDefault(key, Consts.INVALID_STRING);
    }

    public static void logout() {
        writeId(Consts.INVALID_STRING);
        writePassword(Consts.INVALID_STRING);
        writeEmail(Consts.INVALID_STRING);
        writeToken(Consts.INVALID_STRING);
    }


    // endregion
}
