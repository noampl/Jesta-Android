package com.example.jesta.model.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.api.FileUpload;
import com.apollographql.apollo3.api.Optional;
import com.apollographql.apollo3.api.Upload;
import com.apollographql.apollo3.cache.http.HttpCache;
import com.apollographql.apollo3.cache.normalized.NormalizedCache;
import com.apollographql.apollo3.cache.normalized.api.FieldPolicyCacheResolver;
import com.apollographql.apollo3.cache.normalized.api.MemoryCacheFactory;
import com.apollographql.apollo3.cache.normalized.api.TypePolicyCacheKeyGenerator;
import com.apollographql.apollo3.rx3.Rx3Apollo;
import com.example.jesta.LoginMutation;
import com.example.jesta.SignUpMutation;
import com.example.jesta.type.UserCreateInput;
import com.example.jesta.common.Consts;
import com.example.jesta.common.ShardPreferencesHelper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;

public class GrahpqlRepository {

    // region Consts
    /**
     * Peleg local Server Address, need to change the ip for each user
     */
    private final String SERVER_URL="http://10.100.102.10:4111/graphql";

    // endregion

    // region Members

    private final ApolloClient _apolloClient;
    private final ExecutorService _executorService;
    private final MutableLiveData<Boolean> _isLoggedIn;
    private final MutableLiveData<String> _serverError;
    private String _token;

    // endregion

    // region Singleton

    private static GrahpqlRepository instance = null;

    public static GrahpqlRepository getInstance(){
        if (instance == null)
            instance = new GrahpqlRepository();
        return instance;
    }

    private GrahpqlRepository() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();

//        ApolloClient.Builder builder = new ApolloClient.Builder()
//                .serverUrl("http://localhost:4000/graphql");
//
//// Optionally, set an http cache
//        HttpCache.configureApolloClientBuilder(builder, cacheDirectory, cacheMaxSize);
//
//// Optionally, set a normalized cache
//        NormalizedCache.configureApolloClientBuilder(
//                builder,
//                new MemoryCacheFactory(10 * 1024 * 1024, -1),
//                TypePolicyCacheKeyGenerator.INSTANCE,
//                FieldPolicyCacheResolver.INSTANCE,
//                false
//        );
//
//        ApolloClient client = builder.build();


        _apolloClient = new ApolloClient.Builder()
                .serverUrl(SERVER_URL)
                .build();
        _executorService = Executors.newFixedThreadPool(4);
        _isLoggedIn = new MutableLiveData<>();
        _serverError = new MutableLiveData<>();
        _token = Consts.INVALID_STRING;
    }

    // endregion

    // region Properties

    public ApolloClient getApolloClient() {
        return _apolloClient;
    }

    public MutableLiveData<Boolean> getIsLoggedIn(){
        return _isLoggedIn;
    }

    public MutableLiveData<String> getServerErrorMsg(){
        return _serverError;
    }

    // endregion

    // region Public Method

    /**
     * Login to the remote server
     *
     * @param email The email to login
     * @param password The password
     */
    public void login(String email, String password){
        if (email == null || password == null){
            _isLoggedIn.setValue(false);
            return;
        }
        // First way to preform the task async
        _executorService.execute(()->{
            ApolloCall<LoginMutation.Data> mutationCall = _apolloClient.mutation(new LoginMutation(email,password));
            Single<ApolloResponse<LoginMutation.Data>> responseSingle = Rx3Apollo.single(mutationCall);
            ApolloResponse<LoginMutation.Data> response = responseSingle.blockingGet();

            // Check if the server return good answer
             if (!response.hasErrors() && response.data != null){
                 ShardPreferencesHelper.writeToken(response.data.connectUser.token);
                 ShardPreferencesHelper.writeEmail(email);
                 ShardPreferencesHelper.writePassword(password);
                 _token = response.data.connectUser.token;
                 _isLoggedIn.postValue(true);
             }
             else {
                 _isLoggedIn.postValue(false);
                 _serverError.postValue(response.errors.get(0).getMessage());
             }
        });
    }

    /**
     * Register new user
     *
     * @param userCreateInput The user information
     * @param fileUpload The user image
     */
    public void register(UserCreateInput userCreateInput, Optional<Upload> fileUpload){
        ApolloCall<SignUpMutation.Data> mutisioncall = _apolloClient.mutation(new SignUpMutation(userCreateInput, fileUpload));
        Single<ApolloResponse<SignUpMutation.Data>> responseSingle = Rx3Apollo.single(mutisioncall);
        // Other way to preform the task async, using rxjava
        responseSingle.subscribe(new SingleObserver<ApolloResponse<SignUpMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<SignUpMutation.Data> dataApolloResponse) {
                // The server return answer, check if this a good answer
                if (!dataApolloResponse.hasErrors() && dataApolloResponse.data != null){
                    ShardPreferencesHelper.writeToken(dataApolloResponse.data.signUpUser.token);
                    _token = dataApolloResponse.data.signUpUser.token;
                    _isLoggedIn.postValue(true);
                }
                else{
                    _isLoggedIn.postValue(false);
                    _serverError.postValue(dataApolloResponse.errors.get(0).getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                System.out.println("peleg - Error");
                _serverError.postValue(e.getMessage());
            }
        });
    }

    // endregion
}
