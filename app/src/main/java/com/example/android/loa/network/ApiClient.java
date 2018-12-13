package com.example.android.loa.network;

import android.graphics.Path;
import android.util.Log;

import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Item_file;
import com.example.android.loa.network.models.Operation;
import com.google.gson.Gson;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.adapter.rxjava.HttpException;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class ApiClient {

    private static ApiClient INSTANCE = new ApiClient();

    private ApiClient(){}

    public static ApiClient get(){
        return INSTANCE;
    }

    public void searchClients(String query, Integer page,final GenericCallback<List<Client>> callback ){
        handleRequest( ApiUtils.getAPIService().getClientsByPage(page,query), callback);
    }

    public void putClient(Client c, GenericCallback<Client> callback){
        handleRequest( ApiUtils.getAPIService().putClient(c), callback);
    }

    public void getClient(Long id, GenericCallback<Client> callback){
        handleRequest( ApiUtils.getAPIService().getClient(id), callback);
    }

    public void postClient(Client c,GenericCallback<Client> callback){
        handleRequest( ApiUtils.getAPIService().postClient(c), callback);
    }

    public void deleteClient(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPIService().deleteClient(id), callback);
    }

    public void putItemFile(Item_file item, GenericCallback<Item_file> callback){
        handleRequest( ApiUtils.getAPIService().putItemFile(item), callback);
    }

    public void getItemFile(Long id, GenericCallback<Item_file> callback){
        handleRequest( ApiUtils.getAPIService().getItemFile(id), callback);
    }

    public void getOperationAcum(Long client_id,GenericCallback<AmountResult> callback){
        handleRequest( ApiUtils.getAPIService().getOperationAcum("amountByClientId",client_id), callback);
    }

    public void getItemsByClientIdByPage(Integer page,Long client_id, GenericCallback<List<Operation>> callback){
        handleRequest( ApiUtils.getAPIService().getItemsByClientIdByPage("listDebtsByClientId",page,client_id), callback);
    }

    public void postItemfile(Item_file item,GenericCallback<Item_file> callback){
        handleRequest( ApiUtils.getAPIService().postItemFile(item), callback);
    }

    public void deleteItemFile(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPIService().deleteItemFile(id), callback);
    }

    private <T> void handleRequest(Observable<Response<T>> request, final GenericCallback<T> callback){
        request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<T>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Error error = new Error();
                        error.result = "error";
                        error.message = "Generic Error";
                        e.printStackTrace();
                        if( e instanceof HttpException){
                            try {
                                String body = ((HttpException) e).response().errorBody().string();
                                Gson gson = new Gson();
                                error =  gson.fromJson(body,Error.class);
                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                        }
                        callback.onError(error);
                    }

                    @Override
                    public void onNext(Response<T>  response) {
                        callback.onSuccess(response.data);
                    }
                });
    }

    private void handleDeleteRequest(Observable<ResponseBody> request, final GenericCallback<Void> callback){
        request.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<ResponseBody>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Error error = new Error();
                        error.result = "error";
                        error.message = "Generic Error";
                        e.printStackTrace();
                        Log.e("RETRO", e.getMessage());
                        if( e instanceof HttpException){
                            try {
                                String body = ((HttpException) e).response().errorBody().string();
                                Gson gson = new Gson();
                                error =  gson.fromJson(body,Error.class);
                                Log.e("RETRO", body);
                            }catch (Exception e1){
                                e1.printStackTrace();
                            }
                        }
                        callback.onError(error);
                    }

                    @Override
                    public void onNext(ResponseBody response) {
                        callback.onSuccess(null);
                    }
                });
    }

}
