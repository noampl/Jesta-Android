package com.example.jesta.model.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloRequest;
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
import com.example.jesta.CreateFavorWithImageMutation;
import com.example.jesta.CreateFavorWithoutImageMutation;
import com.example.jesta.GetJestasInRadiusQuery;
import com.example.jesta.GetUserQuery;
import com.example.jesta.LoginMutation;
import com.example.jesta.SignUpMutation;
import com.example.jesta.model.enteties.User;
import com.example.jesta.type.DateTime;
import com.example.jesta.type.FavorInput;
import com.example.jesta.type.UserCreateInput;
import com.example.jesta.common.Consts;
import com.example.jesta.common.ShardPreferencesHelper;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.Response;

public class GrahpqlRepository {

    // region Consts
    /**
     * Peleg local Server Address, need to change the ip for each user
     */
    private final String SERVER_URL="http://192.168.155.138:4111/graphql";

    // endregion

    // region Members

    private ApolloClient _apolloClient;
    private final ExecutorService _executorService;
    private final MutableLiveData<Boolean> _isLoggedIn;
    private final MutableLiveData<String> _serverError;

    // endregion

    // region Singleton

    private static GrahpqlRepository instance = null;

    public static GrahpqlRepository getInstance(){
        if (instance == null)
            instance = new GrahpqlRepository();
        return instance;
    }

    private GrahpqlRepository() {
        _apolloClient = new ApolloClient.Builder()
                .serverUrl(SERVER_URL)
                .build();
        _executorService = Executors.newFixedThreadPool(4);
        _isLoggedIn = new MutableLiveData<>();
        _serverError = new MutableLiveData<>();
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
                 ShardPreferencesHelper.writeId(response.data.connectUser.userId);

                 _isLoggedIn.postValue(true);
                 _apolloClient = _apolloClient.newBuilder().addHttpHeader(Consts.AUTHORIZATION, response.data.connectUser.token).build(); // Check if this is working
                 getMyUserInformation(email);
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
                    _isLoggedIn.postValue(true);
                    _apolloClient.newBuilder().addHttpHeader("Authorization", dataApolloResponse.data.signUpUser.token).build(); // Check if this is working
                    getMyUserInformation(userCreateInput.email);
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

    /**
     * Gets User Information by email
     *
     * @param email The user email
     */
    public void getMyUserInformation(String email){
        ApolloCall<GetUserQuery.Data> getUser = _apolloClient.query(new GetUserQuery(new Optional.Present<>(email)));
        Single<ApolloResponse<GetUserQuery.Data>> apolloResponseSingle = Rx3Apollo.single(getUser);
        apolloResponseSingle.subscribe(new SingleObserver<ApolloResponse<GetUserQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetUserQuery.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors() && dataApolloResponse.data != null){
                    UsersRepository.getInstance().set_myUser(new User(
                            dataApolloResponse.data.getUser._id,
                            dataApolloResponse.data.getUser.firstName,
                            dataApolloResponse.data.getUser.lastName,
                            dataApolloResponse.data.getUser.birthday.toString(),
                            dataApolloResponse.data.getUser.email,
                            dataApolloResponse.data.getUser.phone,
                            dataApolloResponse.data.getUser.role,
                            dataApolloResponse.data.getUser.imagePath,
                            dataApolloResponse.data.getUser.address));
                }
                else {
                    Log.e("getUser", dataApolloResponse.errors.get(0).getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("getuser", e.getMessage());
            }
        });
    }

    /**
     * Create new Jesta
     *
     * @param favorInput The jseta details
     * @param image the Image of the jesta;
     */
    public void createJesta( Optional<FavorInput> favorInput, Optional<Upload> image){
        ApolloCall<CreateFavorWithImageMutation.Data> createFavor = _apolloClient.mutation(new CreateFavorWithImageMutation(favorInput, image));
        Single<ApolloResponse<CreateFavorWithImageMutation.Data>> responseSignle = Rx3Apollo.single(createFavor);
        responseSignle.subscribe(new SingleObserver<ApolloResponse<CreateFavorWithImageMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<CreateFavorWithImageMutation.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors() && dataApolloResponse.data != null){
                    System.out.println("peleg - createJesta");
                }
                else{
                    Log.d("CreateJesta", dataApolloResponse.errors.get(0).getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                    Log.e("CreateJesta", e.getMessage());
            }
        });
    }

    /**
     * Get all the Needed jestas in the nearby area
     * @param center The Center of the circle where we start searching from
     * @param radius The radius in KM
     */
    public void GetRemoteJestas(Optional<List<Double>> center, Optional<Double> radius){
        ApolloCall<GetJestasInRadiusQuery.Data> getJestas = _apolloClient.query(new GetJestasInRadiusQuery(center,radius));
        Single<ApolloResponse<GetJestasInRadiusQuery.Data>> responseSingle = Rx3Apollo.single(getJestas);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<GetJestasInRadiusQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetJestasInRadiusQuery.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors() && dataApolloResponse.data != null){
                    JestaRepository.getInstance().set_jestas(dataApolloResponse.data.getFavorsInRadios);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    // endregion
}
