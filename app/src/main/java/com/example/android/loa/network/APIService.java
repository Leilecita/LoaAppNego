package com.example.android.loa.network;

import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Employee;
import com.example.android.loa.network.models.Item_employee;
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

    @PUT("items_employee_file.php")
    Observable<Response<Item_employee>> putItemEmployee(@Body Item_employee e);

    @GET("items_employee_file.php")
    Observable<Response<Item_employee>> getItemEmployee(@Query("id") Long id);


    @GET("items_employee_file.php")
    Observable<Response<List<Item_employee>>> getItemsEmployeeByPageByEmployeeId(@Query("method") String method,@Query("page") Integer page,@Query("employee_id") Long id);


    @POST("items_employee_file.php")
    Observable<Response<Item_employee>> postItemEmploye(@Body  Item_employee e);

    @DELETE("items_employee_file.php")
    Observable<ResponseBody>  deleteItemEmployee(@Query("id") Long id);




    @PUT("employees.php")
    Observable<Response<Employee>> putEmployee(@Body Employee e);

    @GET("employees.php")
    Observable<Response<Employee>> getEmployee(@Query("id") Long id);
    @GET("employees.php")
    Observable<Response<List<Employee>>> getEmployees();
    @GET("employees.php")
    Observable<Response<List<Employee>>> getEmployeesByPage(@Query("page") Integer page);


    @POST("employees.php")
    Observable<Response<Employee>> postEmployee(@Body Employee e);

    @DELETE("employees.php")
    Observable<ResponseBody>  deleteEmployee(@Query("id") Long id);

    @PUT("clients.php")
    Observable<Response<Client>> putClient(@Body Client c);

    @GET("clients.php")
    Observable<Response<Client>> getClient(@Query("id") Long id);

    @GET("clients.php")
    Observable<Response<List<Client>>> getClientsByPage(@Query("page") Integer page, @Query("query") String query,@Query("order") String order );

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

    @GET("items_file.php")
    Observable<Response<AmountResult>> getTotalAmount(@Query("method") String method);

    @DELETE("items_file.php")
    Observable<ResponseBody>  deleteItemFile(@Query("id") Long id);



}
