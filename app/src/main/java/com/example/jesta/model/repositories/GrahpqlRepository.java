package com.example.jesta.model.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.apollographql.apollo3.ApolloCall;
import com.apollographql.apollo3.ApolloClient;
import com.apollographql.apollo3.api.ApolloResponse;
import com.apollographql.apollo3.api.Error;
import com.apollographql.apollo3.api.Optional;
import com.apollographql.apollo3.api.Upload;
import com.apollographql.apollo3.rx3.Rx3Apollo;
import com.example.jesta.ApproveFavorSuggestionMutation;
import com.example.jesta.CancelFavorTransactionMutation;
import com.example.jesta.CreateFavorTransactionRequestMutation;
import com.example.jesta.CreateFavorWithImageMutation;
import com.example.jesta.ExecutorFinishFavorMutation;
import com.example.jesta.GetAllUserFavorTransactionByFavorIdQuery;
import com.example.jesta.GetAllUserFavorsRequestedTransactionQuery;
import com.example.jesta.GetJestaQuery;
import com.example.jesta.GetJestasInRadiusQuery;
import com.example.jesta.GetUserByIdQuery;
import com.example.jesta.GetUserQuery;
import com.example.jesta.LoginMutation;
import com.example.jesta.OwnerFinishFavorMutation;
import com.example.jesta.SignUpMutation;
import com.example.jesta.UpdateUserMutation;
import com.example.jesta.adapter.GetAllUserFavorTransactionByFavorIdQuery_ResponseAdapter;
import com.example.jesta.common.enums.FavorTransactionStatus;
import com.example.jesta.model.enteties.User;
import com.example.jesta.type.FavorInput;
import com.example.jesta.type.UserCreateInput;
import com.example.jesta.common.Consts;
import com.example.jesta.common.ShardPreferencesHelper;
import com.example.jesta.type.UserUpdateInput;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class GrahpqlRepository {
  
     // region Conts  
  
    private final String SERVER_POST_FIX = "graphql";
    private final String SERVER_URL= Consts.SERVER_PRE_FIX + SERVER_POST_FIX;

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

    // region Server Interactions Method

    // region Mutations

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
     * Create new Jesta
     *
     * @param favorInput The jseta details
     * @param images the Images of the jesta;
     */
    public void createJesta( Optional<FavorInput> favorInput, Optional<List<Upload>> images){
        ApolloCall<CreateFavorWithImageMutation.Data> createFavor = _apolloClient.mutation(new CreateFavorWithImageMutation(favorInput, images));
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
     * Update the logged in user parameters
     *
     * @param updatedUser The new parameters
     */
    public void UpdateUser(Optional<UserUpdateInput> updatedUser ){
        ApolloCall<UpdateUserMutation.Data> updateUser = _apolloClient.mutation(
                new UpdateUserMutation(new Optional.Present<>(UsersRepository.getInstance()
                        .get_myUser().getValue().get_id()),updatedUser));
        Single<ApolloResponse<UpdateUserMutation.Data>> responseSingle = Rx3Apollo.single(updateUser);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<UpdateUserMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<UpdateUserMutation.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors()){
                    Log.d("UpdateUser", "Success");
                    UsersRepository.getInstance().set_isUserChanged(true);
                }
                else{
                    Log.d("UpdateUser", "Failed " + dataApolloResponse.errors.get(0));
                    UsersRepository.getInstance().set_isUserChanged(false);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("UpdateUser", e.getMessage());
            }
        });
    }

    /**
     * Suggest help for a nearby jesta
     *
     * @param favorId The jesta Id
     * @param comment Comment for help
     */
    public void suggestHelp(String favorId, Optional<String> comment ) {
        ApolloCall<CreateFavorTransactionRequestMutation.Data> mutation = _apolloClient.mutation(new CreateFavorTransactionRequestMutation(favorId,comment));
        Single<ApolloResponse<CreateFavorTransactionRequestMutation.Data>> responseSingle = Rx3Apollo.single(mutation);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<CreateFavorTransactionRequestMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<CreateFavorTransactionRequestMutation.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors()){
                    JestaRepository.getInstance().set_isSuggestHelp(true);
                    GetAllUserFavorTransactionByFavorIdQuery.GetAllUserFavorTransactionByFavorId transaction = synchronizeGetTransaction(favorId);
                    if (transaction != null){
                        JestaRepository.getInstance().set_favorTransactionStatus(transaction.status);
                    }
                    else{
                        JestaRepository.getInstance().set_favorTransactionStatus(null);
                    }
                }
                else{
                    Log.e("suggestHelp", dataApolloResponse.errors.get(0).getMessage());
                    JestaRepository.getInstance().set_isSuggestHelp(false);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("suggestHelp", e.getMessage());
                JestaRepository.getInstance().set_isSuggestHelp(false);
            }
        });
    }

    /**
     * Approve favors suggestion of other users
     *
     * @param transactionId the transzation Id
     * @param comment Comment of the transaction
     */
    public void approveFavorSuggestion(String transactionId, String comment){
        ApolloCall<ApproveFavorSuggestionMutation.Data> mutation = _apolloClient.mutation(new ApproveFavorSuggestionMutation(transactionId, new Optional.Present<>(comment)));
        Single<ApolloResponse<ApproveFavorSuggestionMutation.Data>> responseSingle = Rx3Apollo.single(mutation);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<ApproveFavorSuggestionMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<ApproveFavorSuggestionMutation.Data> dataApolloResponse) {
                if (dataApolloResponse.hasErrors()){
                    for (Error e : dataApolloResponse.errors)
                        Log.e("ApproveFavorSuggestion", e.getMessage());
                    // TODO consider raise an error to user
                }
                else{
                    Log.d("ApproveFavorSuggestion", "mutation success " + dataApolloResponse.data.handleFavorTransactionRequest);
                    getFavorTransaction();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("ApproveFavorSuggestion", e.getMessage());
            }
        });
    }

    /**
     * Executor notifu he finish the favor transaction
     * @param favorId The favor id related to the transaction
     */
    public void executorFinishFavor(String favorId){
        _executorService.execute(()->{
            GetAllUserFavorTransactionByFavorIdQuery.GetAllUserFavorTransactionByFavorId transaction = synchronizeGetTransaction(favorId);
            if (transaction != null){
                ApolloCall<ExecutorFinishFavorMutation.Data> mutation = _apolloClient.mutation(new ExecutorFinishFavorMutation(transaction._id));
                Single<ApolloResponse<ExecutorFinishFavorMutation.Data>> responseSingle = Rx3Apollo.single(mutation);
                ApolloResponse<ExecutorFinishFavorMutation.Data> dataApolloResponse = responseSingle.blockingGet();
                if (dataApolloResponse.hasErrors()){
                    for (Error e : dataApolloResponse.errors)
                        Log.e("executorFinishFavor", e.getMessage());
                }
                else{
                    Log.d("executorFinishFavor", dataApolloResponse.data.executorNotifyDoneFavor);
                    JestaRepository.getInstance().set_favorTransactionStatus(FavorTransactionStatus.EXECUTOR_FINISH_JESTA.toString());
                }
            }
        });
    }

    /**
     * Onwer rating the jestioner performance
     *
     * @param id The TransactionId
     * @param rate The Rate (1-5)
     */
    public void ownerFinishFavor(String id, int rate){
        ApolloCall<OwnerFinishFavorMutation.Data> mutation = _apolloClient.mutation(new OwnerFinishFavorMutation(id, new Optional.Present<>(rate)));
        Single<ApolloResponse<OwnerFinishFavorMutation.Data>> responseSingle = Rx3Apollo.single(mutation);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<OwnerFinishFavorMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<OwnerFinishFavorMutation.Data> dataApolloResponse) {
                if (dataApolloResponse.hasErrors()){
                    for (Error e : dataApolloResponse.errors)
                        Log.e("ownerFinishFavor", e.getMessage());
                }
                else{
                    Log.d("ownerFinishFavor", "Rate transaction " + id + " " + rate);
                    getFavorTransaction();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("ownerFinishFavor", e.getMessage());
            }
        });
    }

    // endregion

    // region Queries

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
                            (dataApolloResponse.data.getUser.birthday != null ? dataApolloResponse.data.getUser.birthday.toString() : null),
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

    /**
     * Get JEsta Details
     * @param id Rhe Jesta ID
     */
    public void getJestaDetails(String id){
        _executorService.execute(()->{
            GetJestaQuery.GetFavor favor = synchronizeJestDetails(id);
            if (favor != null){
                JestaRepository.getInstance().set_jestaDetails(favor);
                GetAllUserFavorTransactionByFavorIdQuery.GetAllUserFavorTransactionByFavorId transaction = synchronizeGetTransaction(id);
                if (transaction != null){
                    JestaRepository.getInstance().set_favorTransactionStatus(transaction.status);
                }
                else{
                    JestaRepository.getInstance().set_favorTransactionStatus(null);
                }
            }
        });
    }

    /**
     * Gets all favorTransaction - AKA as notification of jesta
     */
    public void getFavorTransaction(){
        if (_apolloClient == null)
            return;
        ApolloCall<GetAllUserFavorsRequestedTransactionQuery.Data> query = _apolloClient.query(new GetAllUserFavorsRequestedTransactionQuery());
        Single<ApolloResponse<GetAllUserFavorsRequestedTransactionQuery.Data>> responseSingle = Rx3Apollo.single(query);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<GetAllUserFavorsRequestedTransactionQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetAllUserFavorsRequestedTransactionQuery.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors()) {
                    NotificationRepository.getInstance().set_notificationTransaction(dataApolloResponse.data.getAllUserFavorsRequestedTransaction);
//                            dataApolloResponse.data.getAllUserFavorsRequestedTransaction.stream().filter(t-> t.status.equals(FavorTransactionStatus.JESTA_DONE.toString())).collect(Collectors.toList()));
                }
                else {
                    for (Error e : dataApolloResponse.errors)
                        Log.e("getFavorTransaction", e.getMessage());
                }
                NotificationRepository.getInstance().set_isTransactionLoading(false);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("getFavotTransaction", e.getMessage());
                NotificationRepository.getInstance().set_isTransactionLoading(false);
            }
        });
    }

    /**
     * Cancel Transaction
     * @param favorId The id of the favorId connect to the transaction cancel
     */
    public void cancelFavorTransaction(String favorId){
        _executorService.execute(()->{
            GetAllUserFavorTransactionByFavorIdQuery.GetAllUserFavorTransactionByFavorId transaction = synchronizeGetTransaction(favorId);
            if (transaction != null){
                ApolloCall<CancelFavorTransactionMutation.Data> mutaion = _apolloClient.mutation(new CancelFavorTransactionMutation(transaction._id));
                Single<ApolloResponse<CancelFavorTransactionMutation.Data>> responseSingle = Rx3Apollo.single(mutaion);
                ApolloResponse<CancelFavorTransactionMutation.Data> dataApolloResponse = responseSingle.blockingGet();

                if (dataApolloResponse.hasErrors()){
                    for (Error e : dataApolloResponse.errors)
                        Log.e("cancelFavorTransaction", e.getMessage());
                }
                else{
                    Log.d("cancelFavorTransaction", "Cancel transaction " + transaction._id);
                    JestaRepository.getInstance().set_favorTransactionStatus(FavorTransactionStatus.CANCELED.toString());
                }
            }
        });

    }

    // endregion

    // endregion

    // region Private Synchronized Methods

    private GetJestaQuery.GetFavor synchronizeJestDetails(String id){
        ApolloCall<GetJestaQuery.Data> query = _apolloClient.query(new GetJestaQuery(new Optional.Present<>(id)));
        Single<ApolloResponse<GetJestaQuery.Data>> responseSingle = Rx3Apollo.single(query);
        ApolloResponse<GetJestaQuery.Data> dataApolloResponse = responseSingle.blockingGet();
        if (dataApolloResponse.hasErrors()){
            for (Error e : dataApolloResponse.errors)
                Log.e("synchronyzeJestaDetails", e.getMessage());
            return null;
        }
        return dataApolloResponse.data.getFavor;
    }

    private GetUserByIdQuery.GetUser synchronizeGetUser(String userId){
        ApolloCall<GetUserByIdQuery.Data> query = _apolloClient.query(new GetUserByIdQuery(new Optional.Present<>(userId)));
        Single<ApolloResponse<GetUserByIdQuery.Data>> responseSingle = Rx3Apollo.single(query);
        ApolloResponse<GetUserByIdQuery.Data> dataApolloResponse = responseSingle.blockingGet();
        if (dataApolloResponse.hasErrors()){
            for (Error e : dataApolloResponse.errors)
                Log.e("synchronyzeJestaDetails", e.getMessage());
            return null;
        }
        else{
            return dataApolloResponse.data.getUser;
        }
    }

    private GetAllUserFavorTransactionByFavorIdQuery.GetAllUserFavorTransactionByFavorId synchronizeGetTransaction(String favorId){
        ApolloCall<GetAllUserFavorTransactionByFavorIdQuery.Data> query = _apolloClient.query(new GetAllUserFavorTransactionByFavorIdQuery(favorId));
        Single<ApolloResponse<GetAllUserFavorTransactionByFavorIdQuery.Data>> responseSingle = Rx3Apollo.single(query);
        ApolloResponse<GetAllUserFavorTransactionByFavorIdQuery.Data> dataApolloResponse = responseSingle.blockingGet();
        if (dataApolloResponse.hasErrors() || dataApolloResponse.data == null ||
                dataApolloResponse.data.getAllUserFavorTransactionByFavorId == null){
            if (dataApolloResponse.errors != null){
                for (Error e : dataApolloResponse.errors)
                    Log.e("synchronizeGetTransaction", e.getMessage());
            }
            return null;
        }
            return dataApolloResponse.data.getAllUserFavorTransactionByFavorId;
    }


    // endregion
}
