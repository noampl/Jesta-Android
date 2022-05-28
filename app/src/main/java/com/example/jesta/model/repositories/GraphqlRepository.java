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
import com.example.jesta.AddUserTokenMutation;
import com.example.jesta.ApproveFavorSuggestionMutation;
import com.example.jesta.CancelFavorTransactionMutation;
import com.example.jesta.CloseTransactionMutation;
import com.example.jesta.CreateFavorTransactionRequestMutation;
import com.example.jesta.CreateFavorWithImageMutation;
import com.example.jesta.ExecutorFinishFavorMutation;
import com.example.jesta.GetAllCategoriesQuery;
import com.example.jesta.GetAllExecutorFavorTransactionByStatusQuery;
import com.example.jesta.GetAllFavorTransactionByFavorIdWhenOwnerQuery;
import com.example.jesta.GetAllFavorTransactionQuery;
import com.example.jesta.GetAllOwnerFavorTransactionByStatusQuery;
import com.example.jesta.GetAllSubCategoriesByParentIdQuery;
import com.example.jesta.GetAllTransactionNotificationsQuery;
import com.example.jesta.GetAllUserFavorTransactionByFavorIdQuery;
import com.example.jesta.GetAllUserFavorsQuery;
import com.example.jesta.GetAllUserHandledFavorTransactionByStatusQuery;
import com.example.jesta.GetFavorsByRadiosTimeAndDateQuery;
import com.example.jesta.GetJestaQuery;
import com.example.jesta.GetTransactionByIdQuery;
import com.example.jesta.GetUserByIdQuery;
import com.example.jesta.GetUserQuery;
import com.example.jesta.LoginMutation;
import com.example.jesta.OwnerNotifyJestaHasBeenDoneMutation;
import com.example.jesta.SignUpMutation;
import com.example.jesta.UpdateUserMutation;
import com.example.jesta.UpdateUserPhotoMutation;
import com.example.jesta.common.enums.FavorTransactionStatus;
import com.example.jesta.model.enteties.Address;
import com.example.jesta.model.enteties.Category;
import com.example.jesta.model.enteties.Jesta;
import com.example.jesta.model.enteties.Transaction;
import com.example.jesta.model.enteties.User;
import com.example.jesta.type.FavorInput;
import com.example.jesta.type.UserCreateInput;
import com.example.jesta.common.Consts;
import com.example.jesta.common.ShardPreferencesHelper;
import com.example.jesta.type.UserUpdateInput;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.Disposable;

public class GraphqlRepository {

    // region Conts

    private final String SERVER_POST_FIX = "graphql";
    private final String SERVER_URL = Consts.SERVER_PRE_FIX + SERVER_POST_FIX;

    // endregion

    // region Members

    private ApolloClient _apolloClient;
    private final ExecutorService _executorService;
    private final MutableLiveData<Boolean> _isLoggedIn;
    private final MutableLiveData<String> _serverError;
    private final AtomicInteger _categoryCounter;
    private final MutableLiveData<String> _serverInteractionResult;

    // endregion

    // region Singleton

    private static GraphqlRepository instance = null;

    public static GraphqlRepository getInstance() {
        if (instance == null)
            instance = new GraphqlRepository();
        return instance;
    }

    public static void cleanInstance() {
        instance = new GraphqlRepository();
    }

    private GraphqlRepository() {
        _apolloClient = new ApolloClient.Builder()
                .serverUrl(SERVER_URL)
                .build();
        _serverInteractionResult = new MutableLiveData<>(Consts.INVALID_STRING);
        _executorService = Executors.newFixedThreadPool(4);
        _isLoggedIn = new MutableLiveData<>();
        _serverError = new MutableLiveData<>();
        _categoryCounter = new AtomicInteger(1);
        new Thread(() -> {
            while (_categoryCounter.get() > 0) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            _isLoggedIn.postValue(true);
        }).start();
    }

    // endregion

    // region Properties

    public ApolloClient getApolloClient() {
        return _apolloClient;
    }

    public MutableLiveData<Boolean> getIsLoggedIn() {
        return _isLoggedIn;
    }

    public MutableLiveData<String> getServerErrorMsg() {
        return _serverError;
    }

    public MutableLiveData<String> get_serverInteractionResult() {
        return _serverInteractionResult;
    }
    // endregion

    // region Server Interactions Method

    public void fetchDoneJesta() {
        _executorService.execute(() -> {
            List<Transaction> doneTransaction, closedTransaction, finishTransaction;
            List<Transaction> resTransaction = new ArrayList<>();

            doneTransaction = synchronizegetExecuterFavorTransaction(com.example.jesta.type.FavorTransactionStatus.JESTA_DONE);
            if (doneTransaction != null) {
                resTransaction.addAll(doneTransaction);
            }
            closedTransaction = synchronizegetExecuterFavorTransaction(com.example.jesta.type.FavorTransactionStatus.CLOSED);
            if (closedTransaction != null) {
                resTransaction.addAll(closedTransaction);
            }
            finishTransaction = synchronizegetExecuterFavorTransaction(com.example.jesta.type.FavorTransactionStatus.EXECUTOR_FINISH_JESTA);
            if (finishTransaction != null) {
                resTransaction.addAll(finishTransaction);
            }
            JestasListsRepository.getInstance().setTransactions(resTransaction);
        });
    }

    // region Mutations

    /**
     * Login to the remote server
     *
     * @param email    The email to login
     * @param password The password
     */
    public void login(String email, String password) {
        if (email == null || password == null) {
            _isLoggedIn.setValue(false);
            return;
        }
        ApolloCall<LoginMutation.Data> mutationCall = _apolloClient.mutation(new LoginMutation(email, password));
        Single<ApolloResponse<LoginMutation.Data>> responseSingle = Rx3Apollo.single(mutationCall);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<LoginMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<LoginMutation.Data> dataApolloResponse) {
                // Check if the server return good answer
                if (!dataApolloResponse.hasErrors() && dataApolloResponse.data != null) {
                    ShardPreferencesHelper.writeToken(dataApolloResponse.data.connectUser.token);
                    ShardPreferencesHelper.writeEmail(email);
                    ShardPreferencesHelper.writePassword(password);
                    ShardPreferencesHelper.writeId(dataApolloResponse.data.connectUser.userId);
                    // Note that all server query should be after this line
                    _apolloClient = _apolloClient.newBuilder().addHttpHeader(Consts.AUTHORIZATION, dataApolloResponse.data.connectUser.token).build();
                    getMyUserInformation(email);
                    getParentCategories();
                } else {
                    _isLoggedIn.postValue(false);
                    _serverError.postValue(dataApolloResponse.errors.get(0).getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                _isLoggedIn.postValue(false);
                _serverError.postValue(e.getMessage());
            }
        });

    }

    /**
     * Register new user
     *
     * @param userCreateInput The user information
     * @param fileUpload      The user image
     */
    public void register(UserCreateInput userCreateInput, Optional<Upload> fileUpload) {
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
                if (!dataApolloResponse.hasErrors() && dataApolloResponse.data != null) {
                    ShardPreferencesHelper.writeToken(dataApolloResponse.data.signUpUser.token);
                    ShardPreferencesHelper.writeEmail(userCreateInput.email);
                    ShardPreferencesHelper.writePassword(userCreateInput.hashedPassword);
                    _apolloClient = _apolloClient.newBuilder().addHttpHeader(Consts.AUTHORIZATION, dataApolloResponse.data.signUpUser.token).build(); // Check if this is working
                    getMyUserInformation(userCreateInput.email);
                    getParentCategories();
                    Log.d("register", Consts.SUCCESS);
                } else {
                    _isLoggedIn.postValue(false);
                    _serverError.postValue(dataApolloResponse.errors.get(0).getMessage());
                    Log.e("register", dataApolloResponse.errors.get(0).getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                _serverError.postValue(e.getMessage());
            }
        });
    }

    /**
     * Create new Jesta
     *
     * @param favorInput The jseta details
     * @param images     the Images of the jesta;
     */
    public void createJesta(Optional<FavorInput> favorInput, Optional<List<Upload>> images) {
        ApolloCall<CreateFavorWithImageMutation.Data> createFavor = _apolloClient.mutation(new CreateFavorWithImageMutation(favorInput, images));
        Single<ApolloResponse<CreateFavorWithImageMutation.Data>> responseSignle = Rx3Apollo.single(createFavor);
        responseSignle.subscribe(new SingleObserver<ApolloResponse<CreateFavorWithImageMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<CreateFavorWithImageMutation.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors() && dataApolloResponse.data != null) {
                    System.out.println("peleg - createJesta");
                    _serverInteractionResult.postValue(Consts.SUCCESS);
                } else {
                    Log.d("peleg - server return CreateJesta", dataApolloResponse.errors.get(0).getMessage());
                    _serverInteractionResult.postValue(dataApolloResponse.errors.get(0).getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("peleg - CreateJesta", e.getMessage());
                _serverInteractionResult.postValue(e.getMessage());
            }
        });
    }

    /**
     * Update the logged in user parameters
     *
     * @param updatedUser The new parameters
     */
    public void UpdateUser(Optional<UserUpdateInput> updatedUser) {
        ApolloCall<UpdateUserMutation.Data> updateUser = _apolloClient.mutation(
                new UpdateUserMutation(new Optional.Present<>(UsersRepository.getInstance()
                        .get_myUser().getValue().get_id()), updatedUser));
        Single<ApolloResponse<UpdateUserMutation.Data>> responseSingle = Rx3Apollo.single(updateUser);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<UpdateUserMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<UpdateUserMutation.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors()) {
                    Log.d("UpdateUser", "Success");
                    UsersRepository.getInstance().set_isUserChanged(true);
                    _serverInteractionResult.postValue(Consts.SUCCESS);
                } else {
                    Log.d("UpdateUser", "Failed " + dataApolloResponse.errors.get(0));
                    UsersRepository.getInstance().set_isUserChanged(false);
                    _serverInteractionResult.postValue(dataApolloResponse.errors.get(0).getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.d("UpdateUser", e.getMessage());
                _serverInteractionResult.postValue(e.getMessage());
            }
        });
    }

    /**
     * Upload picture to user
     *
     * @param uploadPresent The picture to upload
     * @param id            The Id of the user
     */
    public void uploadPhoto(Optional.Present<Upload> uploadPresent, String id) {
        ApolloCall<UpdateUserPhotoMutation.Data> mutation = _apolloClient.mutation(new UpdateUserPhotoMutation(uploadPresent, new Optional.Present<>(id),
                new Optional.Present<>(null), new Optional.Present<>(null)));
        Single<ApolloResponse<UpdateUserPhotoMutation.Data>> responseSingle = Rx3Apollo.single(mutation);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<UpdateUserPhotoMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<UpdateUserPhotoMutation.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors()) {
                    Log.d("uploadPhoto", "upload photo seccess");
                } else {

                    for (Error e : dataApolloResponse.errors)
                        Log.e("uploadPhotoOnSuccess", e.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("uploadPhotoError", e.getMessage());
            }
        });
    }

    /**
     * Suggest help for a nearby jesta
     *
     * @param favorId The jesta Id
     * @param comment Comment for help
     */
    public void suggestHelp(String favorId, Optional<String> comment) {
        ApolloCall<CreateFavorTransactionRequestMutation.Data> mutation = _apolloClient.mutation(new CreateFavorTransactionRequestMutation(favorId, comment));
        Single<ApolloResponse<CreateFavorTransactionRequestMutation.Data>> responseSingle = Rx3Apollo.single(mutation);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<CreateFavorTransactionRequestMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<CreateFavorTransactionRequestMutation.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors()) {
                    JestaRepository.getInstance().set_isSuggestHelp(true);
                    GetAllUserFavorTransactionByFavorIdQuery.GetAllUserFavorTransactionByFavorId transaction = synchronizeGetTransaction(favorId);
                    if (transaction != null) {
                        JestaRepository.getInstance().set_favorTransactionStatus(transaction.status);
                    } else {
//                        JestaRepository.getInstance().set_favorTransactionStatus(null);
                    }
                } else {
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
     * @param comment       Comment of the transaction
     */
    public void approveFavorSuggestion(String transactionId, String comment) {
        ApolloCall<ApproveFavorSuggestionMutation.Data> mutation = _apolloClient.mutation(new ApproveFavorSuggestionMutation(transactionId, new Optional.Present<>(comment)));
        Single<ApolloResponse<ApproveFavorSuggestionMutation.Data>> responseSingle = Rx3Apollo.single(mutation);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<ApproveFavorSuggestionMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<ApproveFavorSuggestionMutation.Data> dataApolloResponse) {
                if (dataApolloResponse.hasErrors()) {
                    for (Error e : dataApolloResponse.errors) {
                        Log.e("ApproveFavorSuggestion", e.getMessage());
                        JestaRepository.getInstance().set_approveServerMsg(e.getMessage());
                    }
                } else {
                    Log.d("ApproveFavorSuggestion", "mutation success " + dataApolloResponse.data.handleFavorTransactionRequest);
                    getAllFavorTransaction();
                    JestaRepository.getInstance().set_approveServerMsg(Consts.SUCCESS);
                }
                JestaRepository.getInstance().set_jestaDetailsLoading(false);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("ApproveFavorSuggestion", e.getMessage());
                JestaRepository.getInstance().set_approveServerMsg(e.getMessage());
                JestaRepository.getInstance().set_jestaDetailsLoading(false);
            }
        });
    }

    /**
     * Executor notifu he finish the favor transaction
     *
     * @param favorId The favor id related to the transaction
     */
    public void executorFinishFavor(String favorId) {
        _executorService.execute(() -> {
            GetAllUserFavorTransactionByFavorIdQuery.GetAllUserFavorTransactionByFavorId transaction = synchronizeGetTransaction(favorId);
            if (transaction != null) {
                ApolloCall<ExecutorFinishFavorMutation.Data> mutation = _apolloClient.mutation(new ExecutorFinishFavorMutation(transaction._id));
                Single<ApolloResponse<ExecutorFinishFavorMutation.Data>> responseSingle = Rx3Apollo.single(mutation);
                ApolloResponse<ExecutorFinishFavorMutation.Data> dataApolloResponse = responseSingle.blockingGet();
                if (dataApolloResponse.hasErrors()) {
                    for (Error e : dataApolloResponse.errors)
                        Log.e("executorFinishFavor", e.getMessage());
                } else {
                    Log.d("executorFinishFavor", dataApolloResponse.data.executorNotifyDoneFavor);
                    JestaRepository.getInstance().set_favorTransactionStatus(FavorTransactionStatus.EXECUTOR_FINISH_JESTA.toString());
                }
            }
        });
    }

    /**
     * Onwer rating the jestioner performance
     *
     * @param id      The TransactionId
     * @param rate    The Rate (1-5)
     * @param comment Comment about the jestioner handler
     */
    public void ownerFinishFavor(String id, double rate, String comment) {
        ApolloCall<OwnerNotifyJestaHasBeenDoneMutation.Data> mutation = _apolloClient.mutation(
                new OwnerNotifyJestaHasBeenDoneMutation(id, new Optional.Present<>(rate), new Optional.Present<>(comment)));
        Single<ApolloResponse<OwnerNotifyJestaHasBeenDoneMutation.Data>> responseSingle = Rx3Apollo.single(mutation);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<OwnerNotifyJestaHasBeenDoneMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<OwnerNotifyJestaHasBeenDoneMutation.Data> dataApolloResponse) {
                if (dataApolloResponse.hasErrors()) {
                    for (Error e : dataApolloResponse.errors)
                        Log.e("ownerFinishFavor", e.getMessage());
                } else {
                    Log.d("ownerFinishFavor", "Rate transaction " + id + " " + rate);
                    getAllFavorTransaction();
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("ownerFinishFavor", e.getMessage());
            }
        });
    }

    /**
     * Cancel Transaction
     *
     * @param favorId The id of the favorId connect to the transaction cancel
     */
    public void cancelFavorTransaction(String favorId) {
        _executorService.execute(() -> {
            GetAllUserFavorTransactionByFavorIdQuery.GetAllUserFavorTransactionByFavorId transaction = synchronizeGetTransaction(favorId);
            if (transaction != null) {
                ApolloCall<CancelFavorTransactionMutation.Data> mutaion = _apolloClient.mutation(new CancelFavorTransactionMutation(transaction._id));
                Single<ApolloResponse<CancelFavorTransactionMutation.Data>> responseSingle = Rx3Apollo.single(mutaion);
                ApolloResponse<CancelFavorTransactionMutation.Data> dataApolloResponse = responseSingle.blockingGet();

                if (dataApolloResponse.hasErrors()) {
                    for (Error e : dataApolloResponse.errors)
                        Log.e("cancelFavorTransaction", e.getMessage());
                } else {
                    Log.d("cancelFavorTransaction", "Cancel transaction " + transaction._id);
                    JestaRepository.getInstance().set_favorTransactionStatus(FavorTransactionStatus.CANCELED.toString());
                }
            }
        });
    }

    /**
     * Cancel Transaction
     *
     * @param transactionId The id of the favorId connect to the transaction cancel
     */
    public void cancelTransaction(String transactionId) {
        _executorService.execute(() -> {
            ApolloCall<CancelFavorTransactionMutation.Data> mutaion = _apolloClient.mutation(new CancelFavorTransactionMutation(transactionId));
            Single<ApolloResponse<CancelFavorTransactionMutation.Data>> responseSingle = Rx3Apollo.single(mutaion);
            ApolloResponse<CancelFavorTransactionMutation.Data> dataApolloResponse = responseSingle.blockingGet();

            if (dataApolloResponse.hasErrors()) {
                for (Error e : dataApolloResponse.errors) {
                    Log.e("cancelFavorTransaction", e.getMessage());
                    JestaRepository.getInstance().set_rejectServerMsg(e.getMessage());
                }
            } else {
                Log.d("cancelFavorTransaction", "Cancel transaction " + transactionId);
                JestaRepository.getInstance().set_favorTransactionStatus(FavorTransactionStatus.CANCELED.toString());
                JestaRepository.getInstance().set_rejectServerMsg(Consts.SUCCESS);
            }
            JestaRepository.getInstance().set_jestaDetailsLoading(false);
        });

    }

    /**
     * Send the server the firebase token
     *
     * @param token the firebase token
     */
    public void addUserToken(String token) {
        if (_apolloClient == null)
            return;
        ApolloCall<AddUserTokenMutation.Data> mutation = _apolloClient.mutation(new AddUserTokenMutation(new Optional.Present<>(token)));
        Single<ApolloResponse<AddUserTokenMutation.Data>> responseSingle = Rx3Apollo.single(mutation);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<AddUserTokenMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<AddUserTokenMutation.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors()) {
                    Log.d("addUserToken", "Seccess");
                } else {
                    Log.d("addUserToken", dataApolloResponse.errors.get(0).getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("addUserToken", e.getMessage());
            }
        });
    }

    /**
     * Move transaction stratus to close
     *
     * @param transactionId the id
     */
    public void closeNotification(String transactionId) {
        ApolloCall<CloseTransactionMutation.Data> mutation = _apolloClient.mutation(new CloseTransactionMutation(transactionId));
        Single<ApolloResponse<CloseTransactionMutation.Data>> responseSingle = Rx3Apollo.single(mutation);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<CloseTransactionMutation.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<CloseTransactionMutation.Data> dataApolloResponse) {
                if (dataApolloResponse.hasErrors()) {
                    for (Error e : dataApolloResponse.errors)
                        Log.e("closeNotification", e.getMessage());
                } else {
                    Log.d("closeNotification", "close transaction successfully " + transactionId);
                    NotificationRepository.getInstance().removeTransactionById(transactionId);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("closeNotification", e.getMessage());
            }
        });
    }


    // endregion

    // region Queries

    /**
     * Gets all the requested jestas of the logged in user
     */
    public void GetAllUserFavors() {
        ApolloCall<GetAllUserFavorsQuery.Data> query = _apolloClient.query(new GetAllUserFavorsQuery());
        Single<ApolloResponse<GetAllUserFavorsQuery.Data>> responseSingle = Rx3Apollo.single(query);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<GetAllUserFavorsQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetAllUserFavorsQuery.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors()) {
                    Map<Jesta, List<Transaction>> jestaListMap = new HashMap<>();
                    dataApolloResponse.data.getAllUserFavors.forEach(j -> {
                        List<Transaction> transactions = synchronizeGetOwnerTransaction(j._id);
                        Jesta jesta = new Jesta(j._id, j.status, j.ownerId,
                                new Address(j.sourceAddress.fullAddress, j.sourceAddress.location.coordinates),
                                j.numOfPeopleNeeded, j.dateToExecute != null ? j.dateToExecute.toString() : null,
                                j.dateToFinishExecute != null ? j.dateToFinishExecute.toString() : null, j.categoryId);
                        jestaListMap.put(jesta, transactions);
                    });
                    JestasListsRepository.getInstance().set_jestasMap(jestaListMap);
                } else {
                    for (Error e : dataApolloResponse.errors) {
                        Log.e("GetAllUserFavors", e.getMessage());
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("GetAllUserFavors", e.getMessage());

            }
        });
    }

    /**
     * Gets User Information by email
     *
     * @param email The user email
     */
    public void getMyUserInformation(String email) {
        ApolloCall<GetUserQuery.Data> getUser = _apolloClient.query(new GetUserQuery(new Optional.Present<>(email)));
        Single<ApolloResponse<GetUserQuery.Data>> apolloResponseSingle = Rx3Apollo.single(getUser);
        apolloResponseSingle.subscribe(new SingleObserver<ApolloResponse<GetUserQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetUserQuery.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors() && dataApolloResponse.data != null) {
                    User user = new User(
                            dataApolloResponse.data.getUser._id,
                            dataApolloResponse.data.getUser.firstName,
                            dataApolloResponse.data.getUser.lastName,
                            (dataApolloResponse.data.getUser.birthday != null ? dataApolloResponse.data.getUser.birthday.toString() : null),
                            dataApolloResponse.data.getUser.email,
                            dataApolloResponse.data.getUser.phone,
                            dataApolloResponse.data.getUser.role,
                            dataApolloResponse.data.getUser.imagePath,
                            dataApolloResponse.data.getUser.address);
                    user.setDateRegistered(dataApolloResponse.data.getUser.created_date.toString());
                    user.setDescription(dataApolloResponse.data.getUser.description);
                    UsersRepository.getInstance().set_myUser(user);
                } else {
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
     *
     * @param center The Center of the circle where we start searching from
     * @param radius The radius in KM
     */
    public void GetRemoteJestas(Optional<List<Double>> center, Optional<Double> radius) {
        if (_apolloClient == null)
            return;
        ApolloCall<GetFavorsByRadiosTimeAndDateQuery.Data> getJestas = _apolloClient.query(
                new GetFavorsByRadiosTimeAndDateQuery(new Optional.Present<>(true), center, radius, new Optional.Present<>(null), new Optional.Present<>(null)));
        Single<ApolloResponse<GetFavorsByRadiosTimeAndDateQuery.Data>> responseSingle = Rx3Apollo.single(getJestas);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<GetFavorsByRadiosTimeAndDateQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetFavorsByRadiosTimeAndDateQuery.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors() && dataApolloResponse.data != null) {
                    JestaRepository.getInstance().set_jestas(dataApolloResponse.data.getByRadiosAndDateAndOnlyAvailable);
                } else {
                    for (Error e : dataApolloResponse.errors)
                        Log.e("GetRemoteJestas", e.getMessage());
                    System.out.println("peleg - GetRemoteJestas failed");
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("GetRemoteJestas", e.getMessage());
                System.out.println("peleg - GetRemoteJestas failed");
            }
        });
    }

    /**
     * Get JEsta Details
     *
     * @param id Rhe Jesta ID
     */
    public void getJestaDetails(String id) {
        _executorService.execute(() -> {
            GetJestaQuery.GetFavor favor = synchronizeJestDetails(id);
            if (favor != null) {
                JestaRepository.getInstance().set_jestaDetails(favor);
                GetAllUserFavorTransactionByFavorIdQuery.GetAllUserFavorTransactionByFavorId transaction = synchronizeGetTransaction(id);

                if (transaction != null) {
                    JestaRepository.getInstance().set_favorTransactionStatus(transaction.status);
                }
                JestaRepository.getInstance().set_jestaDetailsLoading(false);
            }
        });
    }

    /**
     * Gets all favorTransaction - AKA as notification of jesta
     */
    public synchronized void getAllFavorTransaction() {
        if (_apolloClient == null)
            return;
//        NotificationRepository.getInstance().get_notificationTransaction().getValue().clear();
        NotificationRepository.getInstance().set_notificationTransaction(new ArrayList<>());
        Date threeDayasAgo = new Date(new Date().getTime() - Consts.THREE_DAYS);

        getAllFavorTransaction(com.example.jesta.type.FavorTransactionStatus.PENDING_FOR_OWNER,
                com.example.jesta.type.FavorTransactionStatus.WAITING_FOR_JESTA_EXECUTION_TIME, null);
        getAllFavorTransaction(com.example.jesta.type.FavorTransactionStatus.EXECUTOR_FINISH_JESTA,
                com.example.jesta.type.FavorTransactionStatus.CANCELED, threeDayasAgo.toString());
        getAllFavorTransaction(com.example.jesta.type.FavorTransactionStatus.CANCELED,
                com.example.jesta.type.FavorTransactionStatus.JESTA_DONE, threeDayasAgo.toString());
    }

    public void getExecuterFavorTransaction(com.example.jesta.type.FavorTransactionStatus status) {
        ApolloCall<GetAllExecutorFavorTransactionByStatusQuery.Data> quary = _apolloClient.query(new GetAllExecutorFavorTransactionByStatusQuery(new Optional.Present<>(status)));
        Single<ApolloResponse<GetAllExecutorFavorTransactionByStatusQuery.Data>> responseSingle = Rx3Apollo.single(quary);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<GetAllExecutorFavorTransactionByStatusQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetAllExecutorFavorTransactionByStatusQuery.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors() && dataApolloResponse.data != null) {
                    List<Transaction> transactions = new ArrayList<>();
                    dataApolloResponse.data.getAllExecutorFavorTransactionByStatus.forEach(t -> {
                        transactions.add(new Transaction(t._id, FavorTransactionStatus.valueOf(t.status), new Jesta(t.favorId._id, t.favorId.status,
                                t.favorId.ownerId, new Address(t.favorId.sourceAddress.fullAddress, t.favorId.sourceAddress.location.coordinates),
                                t.favorId.numOfPeopleNeeded, t.favorId.dateToExecute != null ? t.favorId.dateToExecute.toString() : null,
                                t.favorId.dateToFinishExecute != null ? t.favorId.dateToFinishExecute.toString() : null, t.favorId.categoryId),
                                new User(t.favorOwnerId._id, t.favorOwnerId.firstName, t.favorOwnerId.lastName),
                                new User(t.handledByUserId._id, t.handledByUserId.firstName, t.handledByUserId.lastName,
                                        t.handledByUserId.rating != null ? t.handledByUserId.rating : 0.0, t.handledByUserId.imagePath),
                                t.dateLastModified.toString(), t.rating != null ? t.rating : 0));
                    });
                    JestasListsRepository.getInstance().setTransactions(transactions);
                } else {
                    for (Error e : dataApolloResponse.errors)
                        Log.e("getExecuterFavorTransaction", e.getMessage());
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("getExecuterFavorTransaction", e.getMessage());

            }
        });
    }

    public void getTransactionById(String transactionId) {
        ApolloCall<GetTransactionByIdQuery.Data> query = _apolloClient.query(new GetTransactionByIdQuery(new Optional.Present<>(transactionId)));
        Single<ApolloResponse<GetTransactionByIdQuery.Data>> responseSingle = Rx3Apollo.single(query);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<GetTransactionByIdQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetTransactionByIdQuery.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors()) {
                    GetTransactionByIdQuery.GetTransactionById t = dataApolloResponse.data.getTransactionById;
                    JestaRepository.getInstance().setTransaction(new Transaction(t._id, FavorTransactionStatus.valueOf(t.status),
                            new Jesta(t.favorId._id, t.favorId.status, t.favorId.ownerId),
                            new User(t.favorOwnerId._id, t.favorOwnerId.firstName, t.favorOwnerId.lastName),
                            new User(t.handledByUserId._id, t.handledByUserId.firstName, t.handledByUserId.lastName,
                                    t.handledByUserId.rating, t.handledByUserId.imagePath),
                            t.dateLastModified != null ? t.dateLastModified.toString() : null, t.rating != null ? t.rating : 0));
                } else {
                    for (Error e : dataApolloResponse.errors) {
                        Log.e("getTransactionById", e.getMessage());
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("getTransactionById", e.getMessage());
            }
        });
    }

    public void getMyFavorTransaction() {
        ApolloCall<GetAllFavorTransactionQuery.Data> query = _apolloClient.query(new GetAllFavorTransactionQuery());
        Single<ApolloResponse<GetAllFavorTransactionQuery.Data>> responseSingle = Rx3Apollo.single(query);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<GetAllFavorTransactionQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetAllFavorTransactionQuery.Data> dataApolloResponse) {
                // TODO dix thiss!!
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void getUserDetails(String id) {
        ApolloCall<GetUserByIdQuery.Data> query = _apolloClient.query(new GetUserByIdQuery(new Optional.Present<>(id)));
        Single<ApolloResponse<GetUserByIdQuery.Data>> responseSingle = Rx3Apollo.single(query);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<GetUserByIdQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetUserByIdQuery.Data> dataApolloResponse) {
                if (dataApolloResponse.hasErrors()) {
                    for (Error e : dataApolloResponse.errors) {
                        Log.e("getUserDetails", e.getMessage());
                    }
                } else {
                    GetUserByIdQuery.GetUser u = dataApolloResponse.data.getUser;
                    User user = new User(u._id, u.firstName, u.lastName,
                            u.birthday != null ? u.birthday.toString() : null,
                            u.email, u.phone, u.role, u.imagePath,
                            u.address != null ? new GetUserQuery.Address(u.address.fullAddress) : null
                    );
                    user.set_numOfJestasDone(u.numberOfExecutedJesta);
                    user.set_isTopJestioner(u.mostVolunteered);
                    user.setDescription(u.description);
                    user.setDateRegistered(u.created_date);
                    user.set_rating(u.rating);
                    UsersRepository.getInstance().set_myUser(user);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

            }
        });
    }

    public void getParentCategories() {
        ApolloCall<GetAllCategoriesQuery.Data> query = _apolloClient.query(new GetAllCategoriesQuery());
        Single<ApolloResponse<GetAllCategoriesQuery.Data>> responseSingle = Rx3Apollo.single(query);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<GetAllCategoriesQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetAllCategoriesQuery.Data> dataApolloResponse) {
                if (dataApolloResponse.hasErrors()) {
                    for (Error e : dataApolloResponse.errors) {
                        Log.e("getCategories", e.getMessage());
                    }
                } else {
                    HashMap<Category, List<Category>> categories = new HashMap<>();
                    dataApolloResponse.data.getAllParentCategories.forEach(c -> {
                        _categoryCounter.incrementAndGet();
                        categories.put(new Category(c._id, c.name), new ArrayList<>());
                    });
                    _categoryCounter.decrementAndGet();
                    CategoriesRepository.getInstance().setMapCategoryToSubCategory(categories);
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("getCategories", e.getMessage());
                _isLoggedIn.postValue(true);
            }
        });
    }

    public void getChildCategoryByParent(Category parent) {
        ApolloCall<GetAllSubCategoriesByParentIdQuery.Data> query = _apolloClient.query(new GetAllSubCategoriesByParentIdQuery(new Optional.Present<>(parent.get_id())));
        Single<ApolloResponse<GetAllSubCategoriesByParentIdQuery.Data>> responseSingle = Rx3Apollo.single(query);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<GetAllSubCategoriesByParentIdQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetAllSubCategoriesByParentIdQuery.Data> dataApolloResponse) {

                if (dataApolloResponse.hasErrors()) {
                    for (Error e : dataApolloResponse.errors) {
                        Log.e("getChildCategoryBtParent", e.getMessage());
                    }
                } else {
                    List<Category> categories = new ArrayList<>();
                    dataApolloResponse.data.getAllSubCategoriesByParentCategory.forEach(c -> {
                        categories.add(new Category(c._id, c.name));
                    });
                    CategoriesRepository.getInstance().addSubCategories(categories, parent);
                }
                _categoryCounter.decrementAndGet();
            }

            @Override
            public void onError(@NonNull Throwable e) {
                Log.e("getChildCategoryBtParent", e.getMessage());
                _categoryCounter.decrementAndGet();
            }
        });
    }

    public void getCommentsOnUserId(String userId) {
        _executorService.execute(() -> {
            List<Transaction> closeTransaction, doneTransaction;
            List<Transaction> resTransaction = new ArrayList<>();
            closeTransaction = synchronizeGetHandlerTranzactionByStatus(com.example.jesta.type.FavorTransactionStatus.CLOSED, userId);

            if (closeTransaction != null)
                resTransaction.addAll(closeTransaction);
            doneTransaction = synchronizeGetHandlerTranzactionByStatus(com.example.jesta.type.FavorTransactionStatus.JESTA_DONE, userId);

            if (doneTransaction != null)
                resTransaction.addAll(doneTransaction);
            CommentsRepository.getInstance().set_commenets(resTransaction);
        });
    }


    // endregion

    // endregion

    // region Private Methods

    /**
     * Get favor notifications by status
     *
     * @param ownerStatus    return the notification if transaction has this status and owner Id equals to the user owner Id
     * @param executerStatus return the notification if transaction has this status and handheld by id  equals to the user owner Id
     */
    private void getAllFavorTransaction(com.example.jesta.type.FavorTransactionStatus ownerStatus, com.example.jesta.type.FavorTransactionStatus executerStatus, String date) {
        ApolloCall<GetAllTransactionNotificationsQuery.Data> query = _apolloClient.query(new GetAllTransactionNotificationsQuery(new Optional.Present<>(executerStatus),
                new Optional.Present<>(ownerStatus), new Optional.Present<>(date)));
        Single<ApolloResponse<GetAllTransactionNotificationsQuery.Data>> responseSingle = Rx3Apollo.single(query);
        responseSingle.subscribe(new SingleObserver<ApolloResponse<GetAllTransactionNotificationsQuery.Data>>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {

            }

            @Override
            public void onSuccess(@NonNull ApolloResponse<GetAllTransactionNotificationsQuery.Data> dataApolloResponse) {
                if (!dataApolloResponse.hasErrors()) {
                    List<Transaction> transactions = new ArrayList<>();
                    dataApolloResponse.data.getAllExecutorFavorTransactionByStatus.forEach(t -> {
                        transactions.add(new Transaction(t._id, FavorTransactionStatus.valueOf(t.status), new Jesta(t.favorId._id, t.favorId.status,
                                t.favorId.ownerId, new Address(t.favorId.sourceAddress.location.coordinates)),
                                new User(t.favorOwnerId._id, t.favorOwnerId.firstName, t.favorOwnerId.lastName),
                                new User(t.handledByUserId._id, t.handledByUserId.firstName, t.handledByUserId.lastName),
                                t.dateLastModified.toString(), t.rating != null ? t.rating : 0, t.handlerComment));
                    });
                    dataApolloResponse.data.getAllOwnerFavorTransactionByStatus.forEach(t -> {
                        transactions.add(new Transaction(t._id, FavorTransactionStatus.valueOf(t.status), new Jesta(t.favorId._id, t.favorId.status,
                                t.favorId.ownerId, new Address(t.favorId.sourceAddress.location.coordinates)),
                                new User(t.favorOwnerId._id, t.favorOwnerId.firstName, t.favorOwnerId.lastName),
                                new User(t.handledByUserId._id, t.handledByUserId.firstName, t.handledByUserId.lastName),
                                t.dateLastModified.toString(), t.rating != null ? t.rating : 0, t.handlerComment));
                    });

                    NotificationRepository.getInstance().add_notificationTransaction(
                            transactions.stream().
                                    filter(t -> !t.getStatus().equals(FavorTransactionStatus.CANCELED)).collect(Collectors.toList()));
                } else {
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

    // region Private Synchronized Methods

    private GetJestaQuery.GetFavor synchronizeJestDetails(String id) {
        ApolloCall<GetJestaQuery.Data> query = _apolloClient.query(new GetJestaQuery(new Optional.Present<>(id)));
        Single<ApolloResponse<GetJestaQuery.Data>> responseSingle = Rx3Apollo.single(query);
        ApolloResponse<GetJestaQuery.Data> dataApolloResponse = responseSingle.blockingGet();
        if (dataApolloResponse.hasErrors()) {
            for (Error e : dataApolloResponse.errors)
                Log.e("synchronyzeJestaDetails", e.getMessage());
            return null;
        }
        return dataApolloResponse.data.getFavor;
    }

    private GetUserByIdQuery.GetUser synchronizeGetUser(String userId) {
        ApolloCall<GetUserByIdQuery.Data> query = _apolloClient.query(new GetUserByIdQuery(new Optional.Present<>(userId)));
        Single<ApolloResponse<GetUserByIdQuery.Data>> responseSingle = Rx3Apollo.single(query);
        ApolloResponse<GetUserByIdQuery.Data> dataApolloResponse = responseSingle.blockingGet();
        if (dataApolloResponse.hasErrors()) {
            for (Error e : dataApolloResponse.errors)
                Log.e("synchronyzeJestaDetails", e.getMessage());
            return null;
        } else {
            return dataApolloResponse.data.getUser;
        }
    }

    private GetAllUserFavorTransactionByFavorIdQuery.GetAllUserFavorTransactionByFavorId synchronizeGetTransaction(String favorId) {
        ApolloCall<GetAllUserFavorTransactionByFavorIdQuery.Data> query = _apolloClient.query(new GetAllUserFavorTransactionByFavorIdQuery(favorId));
        Single<ApolloResponse<GetAllUserFavorTransactionByFavorIdQuery.Data>> responseSingle = Rx3Apollo.single(query);
        ApolloResponse<GetAllUserFavorTransactionByFavorIdQuery.Data> dataApolloResponse = responseSingle.blockingGet();
        if (dataApolloResponse.hasErrors() || dataApolloResponse.data == null ||
                dataApolloResponse.data.getAllUserFavorTransactionByFavorId == null) {
            if (dataApolloResponse.errors != null) {
                for (Error e : dataApolloResponse.errors)
                    Log.e("synchronizeGetTransaction", e.getMessage());
            }
            return null;
        }
        return dataApolloResponse.data.getAllUserFavorTransactionByFavorId;
    }

    private List<Transaction> synchronizeGetOwnerTransaction(String favorId) {
        List<Transaction> transactions = new ArrayList<>();
        ApolloCall<GetAllFavorTransactionByFavorIdWhenOwnerQuery.Data> query = _apolloClient.query(new GetAllFavorTransactionByFavorIdWhenOwnerQuery(favorId));
        Single<ApolloResponse<GetAllFavorTransactionByFavorIdWhenOwnerQuery.Data>> responseSingle = Rx3Apollo.single(query);
        ApolloResponse<GetAllFavorTransactionByFavorIdWhenOwnerQuery.Data> dataApolloResponse = responseSingle.blockingGet();
        if (dataApolloResponse.hasErrors()) {
            for (Error e : dataApolloResponse.errors)
                Log.e("synchronizeGetOwnerTransaction", e.getMessage());
            return null;
        }
        dataApolloResponse.data.getAllFavorTransactionByFavorIdWhenOwner.forEach(t -> transactions
                .add(new Transaction(t._id, FavorTransactionStatus.valueOf(t.status), new Jesta(t.favorId._id, t.favorId.status,
                        t.favorId.ownerId, new Address(t.favorId.sourceAddress.location.coordinates)),
                        new User(t.favorOwnerId._id, t.favorOwnerId.firstName, t.favorOwnerId.lastName),
                        new User(t.handledByUserId._id, t.handledByUserId.firstName, t.handledByUserId.lastName,
                                t.handledByUserId.rating != null ? t.handledByUserId.rating : 0, t.handledByUserId.imagePath),
                        t.dateLastModified.toString(), t.rating != null ? t.rating : 0)));
        return transactions;
    }

    private List<Transaction> synchronizegetExecuterFavorTransaction(com.example.jesta.type.FavorTransactionStatus status) {
        ApolloCall<GetAllExecutorFavorTransactionByStatusQuery.Data> quary = _apolloClient.query(new GetAllExecutorFavorTransactionByStatusQuery(new Optional.Present<>(status)));
        Single<ApolloResponse<GetAllExecutorFavorTransactionByStatusQuery.Data>> responseSingle = Rx3Apollo.single(quary);
        ApolloResponse<GetAllExecutorFavorTransactionByStatusQuery.Data> dataApolloResponse = responseSingle.blockingGet();
        if (dataApolloResponse.hasErrors()) {
            for (Error e : dataApolloResponse.errors)
                Log.e("synchronizegetExecuterFavorTransaction", e.getMessage());
            return null;
        }
        List<Transaction> transactions = new ArrayList<>();
        dataApolloResponse.data.getAllExecutorFavorTransactionByStatus.forEach(t -> {
            transactions.add(new Transaction(t._id, FavorTransactionStatus.valueOf(t.status), new Jesta(t.favorId._id, t.favorId.status,
                    t.favorId.ownerId, new Address(t.favorId.sourceAddress.fullAddress, t.favorId.sourceAddress.location.coordinates),
                    t.favorId.numOfPeopleNeeded, t.favorId.dateToExecute != null ? t.favorId.dateToExecute.toString() : null,
                    t.favorId.dateToFinishExecute != null ? t.favorId.dateToFinishExecute.toString() : null, t.favorId.categoryId),
                    new User(t.favorOwnerId._id, t.favorOwnerId.firstName, t.favorOwnerId.lastName),
                    new User(t.handledByUserId._id, t.handledByUserId.firstName, t.handledByUserId.lastName,
                            t.handledByUserId.rating != null ? t.handledByUserId.rating : 0.0, t.handledByUserId.imagePath),
                    t.dateLastModified.toString(), t.rating != null ? t.rating : 0));
        });
        return transactions;
    }

    private List<Transaction> synchronizeGetHandlerTranzactionByStatus(com.example.jesta.type.FavorTransactionStatus status, String handlerId) {
        ApolloCall<GetAllUserHandledFavorTransactionByStatusQuery.Data> query =
                _apolloClient.query(new GetAllUserHandledFavorTransactionByStatusQuery(
                        new Optional.Present<>(status),
                        new Optional.Present<>(handlerId)));
        Single<ApolloResponse<GetAllUserHandledFavorTransactionByStatusQuery.Data>> responseSingle = Rx3Apollo.single(query);
        ApolloResponse<GetAllUserHandledFavorTransactionByStatusQuery.Data> dataApolloResponse = responseSingle.blockingGet();
        if (dataApolloResponse.hasErrors()) {
            for (Error e : dataApolloResponse.errors)
                Log.e("synchronizeGetHandlerTranzactionByStatus", e.getMessage());
            return null;
        }
        List<Transaction> transactions = new ArrayList<>();
        dataApolloResponse.data.getAllUserHandledFavorTransactionByStatus.forEach(t -> {
            Transaction transaction = new Transaction(t._id);
            transaction.setStatus(FavorTransactionStatus.valueOf(t.status));
            transaction.setComment(t.handlerComment);
            transaction.setRating(t.rating != null ? t.rating : 0);
            transaction.setFavorOwnerId(new User(t.favorOwnerId._id,
                    t.favorOwnerId.firstName, t.favorOwnerId.lastName));
            transactions.add(transaction);
        });
        return transactions;
    }


    // endregion

    // endregion
}
