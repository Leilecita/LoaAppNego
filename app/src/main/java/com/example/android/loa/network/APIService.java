package com.example.android.loa.network;

import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Box;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Employee;
import com.example.android.loa.network.models.Event;
import com.example.android.loa.network.models.Extraction;
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

    @GET("items_employee_file.php")
    Observable<Response<List<Item_employee>>> getItemsEmployeeByPageByEmployeeIdByMonth(@Query("page") Integer page,@Query("employee_id") Long id,
                                                                                        @Query("since")String  month1,@Query("to") String month2);

    @GET("items_employee_file.php")
    Observable<Response<List<Item_employee>>> getItemsEmployeeByEmployeeIdByMonth(@Query("employee_id") Long id,
                                                                                        @Query("since")String  month1,@Query("to") String month2);

    @GET("items_employee_file.php")
    Observable<Response<Item_employee>> getItemEmployeeByDateAndEmployeeid(@Query("employee_id") Long id,
                                                                                        @Query("date")String  month1);



    @GET("items_employee_file.php")
    Observable<Response<AmountResult>> getAmountHoursByMonth(@Query("method") String method,@Query("since")String  month1,@Query("to") String month2,@Query("employee_id") Long id);

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

    @GET("clients.php")
    Observable<Response<List<Client>>> getClientsByPageByCreator(@Query("page") Integer page,@Query("employee_creator_id") String name );

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
    Observable<ResponseBody>  deleteItemFile(@Query("id") Long id,@Query("modify") String modify);

    @DELETE("items_file.php")
    Observable<ResponseBody>  deleteItemFileByModify(@Query("method") String method,@Query("modify") String modify,@Query("id") Long id);


    @GET("extractions.php")
    Observable<Response<AmountResult>> getTotalExtractionsAmount(@Query("method") String method, @Query("date") String date, @Query("dateTo") String dateTo);


    @PUT("extractions.php")
    Observable<Response<Extraction>> putExtraction(@Body Extraction e);

    @GET("extractions.php")
    Observable<Response<Extraction>> getExtraction(@Query("id") Long id);

    @GET("extractions.php")
    Observable<Response<List<Extraction>>> getExtractionsByPage(@Query("page") Integer page,@Query("since") String created);

    @GET("extractions.php")
    Observable<Response<List<Extraction>>> getExtractionsByPageAndDate(@Query("page") Integer page,@Query("since") String created,@Query("to") String next );


    @GET("boxes.php")
    Observable<Response<List<Box>>> getBoxesByPageAndDate(@Query("page") Integer page,@Query("since") String created,@Query("to") String next );



    @POST("extractions.php")
    Observable<Response<Extraction>> postExtraction(@Body Extraction e);

    @DELETE("extractions.php")
    Observable<ResponseBody>  deleteExtraction(@Query("id") Long id);

    @PUT("boxes.php")
    Observable<Response<Box>> putBox(@Body Box b);

    @GET("boxes.php")
    Observable<Response<Box>> getBox(@Query("id") Long id);

    @GET("boxes.php")
    Observable<Response<List<Box>>> getBoxesByPage(@Query("method") String m, @Query("page") Integer page,@Query("created") String created );

    @GET("events.php")
    Observable<Response<List<Event>>> getEventsByPage(@Query("page") Integer page );

    @GET("events.php")
    Observable<Response<List<Event>>> getEventsByPageByClientId(@Query("page") Integer page,@Query("client_id") Long client_id );

    @POST("boxes.php")
    Observable<Response<Box>> postBox(@Body Box b);

    @DELETE("boxes.php")
    Observable<ResponseBody>  deleteBox(@Query("id") Long id);

}
