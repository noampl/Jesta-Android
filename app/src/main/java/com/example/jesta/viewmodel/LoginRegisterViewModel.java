package com.example.jesta.viewmodel;

import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.DefaultUpload;
import com.apollographql.apollo3.api.Optional;
import com.apollographql.apollo3.api.Upload;
import com.example.MyApplication;
import com.example.jesta.type.UserCreateInput;
import com.example.jesta.common.Consts;
import com.example.jesta.common.ShardPreferencesHelper;
import com.example.jesta.model.repositories.GraphqlRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.regex.Pattern;

import okio.Okio;
import okio.Source;


public class LoginRegisterViewModel extends ViewModel {

    // region Members

    private final ApolloClient _apolloClient;
    private final MutableLiveData<Boolean> isLoggedIn;
    private final MutableLiveData<String> _serverErrorMsg;

    // endregion

    // region C'tor

    public LoginRegisterViewModel() {
        this._apolloClient = GraphqlRepository.getInstance().getApolloClient();
        isLoggedIn = GraphqlRepository.getInstance().getIsLoggedIn();
        _serverErrorMsg = GraphqlRepository.getInstance().getServerErrorMsg();
    }

    // endregion

    // region Properties

    public MutableLiveData<Boolean> getIsLoggedIn() {
        return isLoggedIn;
    }

    public void setIsLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn.setValue(isLoggedIn);
    }

    public MutableLiveData<String> getServerErrorMsg() {
        return _serverErrorMsg;
    }

    public void setServerErrorMsg(String msg){
        _serverErrorMsg.setValue(msg);
    }

    // endregion

    // region Public Methods

    /**
     * Try to connect to the remote server with the shared preferences saved information
     */
    public void initLogin(){
        try {
            ShardPreferencesHelper.init();
            GraphqlRepository.getInstance().login(ShardPreferencesHelper.readEmail(), ShardPreferencesHelper.readPassword());
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            isLoggedIn.setValue(false);
        }

    }

    /**
     * Login the server
     * @param email The email of the user
     * @param password The password
     *
     * @return
     */
    public boolean login(String email, String password) {
        boolean res = true;
        if (email == null ) {
            res = false;
        }
        else{
            ShardPreferencesHelper.writeEmail(email);
        }
        if (password == null) {
            res = false;
        }
        else{
            ShardPreferencesHelper.writePassword(password);
        }
        if (res)
            GraphqlRepository.getInstance().login(email,password);
        return res;
    }

    public void register(String firstName, String lastName, String birthday , String email,
                         String password, String phone, String address, Source filePath, double lng, double lat,
                        String description){
        Optional<Upload> optionalFile = null;
        if (filePath != null && !filePath.toString().equals(Consts.INVALID_STRING)){
            DefaultUpload upload = new DefaultUpload.Builder()
                    .content(Okio.buffer(filePath))
                    .fileName(firstName + "_" + lastName + Consts.JPG)
                    .build();
            optionalFile = new Optional.Present<Upload>(upload);
        }
        UserCreateInput userCreateInput = new UserCreateInput(firstName, lastName, new Optional.Present<>(birthday),
                email, password, new Optional.Present<>(phone),new Optional.Present<>(address),new Optional.Present<>(lng),
                new Optional.Present<>(lat), new Optional.Present<>(description));

        GraphqlRepository.getInstance().register(userCreateInput, optionalFile);
    }

    /**
     * Check if passwords match
     *
     * @param pass1 First password
     * @param pass2 Second password
     *
     * @return Tru if passwords match
     */
    public boolean doesPasswordsMatch(String pass1, String pass2){
        boolean doesGood = doesPasswordValid(pass1) && doesPasswordValid(pass2);
        return doesGood && pass1.equals(pass2);
    }

    /**
     * Valid a password
     *
     * @param pass1 The password
     *
     * @return True id valid
     */
    public boolean doesPasswordValid(String pass1){
        if (pass1 == null)
            return false;
        return Pattern.matches(Consts.PASSWORD_VALIDATOR, pass1);
    }

    /**
     * Valid email address
     *
     * @param email The address
     * @return True if valid
     */
    public boolean doesEmailValid(String email){
        if (email == null)
            return false;
        return Pattern.matches(Consts.EMAIL_VALIDATOR, email);
    }

    // endregion

    // region Private Methods

    private byte[] convertImagePathToData(Uri uri){

        Bitmap bitmap = null;
        ByteArrayOutputStream bao;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(MyApplication.getAppContext().getContentResolver(), uri);
            bao = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bao);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return bao.toByteArray();
    }

    // endregion

}
