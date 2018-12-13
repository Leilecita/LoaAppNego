package com.example.android.loa.network;

import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Item_file;
import com.example.android.loa.network.models.Operation;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;
import rx.Observable;

public interface APIService {

    @PUT("clients.php")
    Observable<Response<Client>> putClient(@Body Client c);

    @GET("clients.php")
    Observable<Response<Client>> getClient(@Query("id") Long id);

    @GET("clients.php")
    Observable<Response<List<Client>>> getClientsByPage(@Query("page") Integer page, @Query("query") String query );

    @POST("clients.php")
    Observable<Response<Client>> postClient(@Body Client c);

    @DELETE("clients.php")
    Observable<ResponseBody>  deleteClient(@Query("id") Long id);

    @PUT("items_file.php")
    Observable<Response<Item_file>> putItemFile(@Body Item_file item);

    @GET("items_file.php")
    Observable<Response<Item_file>> getItemFile(@Query("id") Long id);

    @GET("items_file.php")
    Observable<Response<List<Operation>>> getItemsByClientIdByPage(@Query("method") String method, @Query("page") Integer page, @Query("client_id") Long client_id);

    @POST("items_file.php")
    Observable<Response<Item_file>> postItemFile(@Body Item_file item);

    @GET("items_file.php")
    Observable<Response<AmountResult>> getOperationAcum(@Query("method") String method, @Query("client_id") Long client_id);

    @DELETE("items_file.php")
    Observable<ResponseBody>  deleteItemFile(@Query("id") Long id);



}
