package com.example.android.loa.network;

import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Box;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Employee;
import com.example.android.loa.network.models.Event;
import com.example.android.loa.network.models.Extraction;
import com.example.android.loa.network.models.Income;
import com.example.android.loa.network.models.Item_employee;
import com.example.android.loa.network.models.Item_file;
import com.example.android.loa.network.models.Operation;
import com.example.android.loa.network.models.ParallelMoneyMovement;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.ReportEntrie;
import com.example.android.loa.network.models.ReportExtraction;
import com.example.android.loa.network.models.GeneralStock;
import com.example.android.loa.network.models.ReportIncome;
import com.example.android.loa.network.models.ReportItemEmployee;
import com.example.android.loa.network.models.ReportItemFileClientEvent;
import com.example.android.loa.network.models.ReportMonthBox;
import com.example.android.loa.network.models.ReportNewBox;
import com.example.android.loa.network.models.ReportParallelMoneyMovement;
import com.example.android.loa.network.models.ReportPriceEvent;
import com.example.android.loa.network.models.ReportPrices;
import com.example.android.loa.network.models.ReportProduct;
import com.example.android.loa.network.models.ReportSale;
import com.example.android.loa.network.models.ReportSimpelClient;
import com.example.android.loa.network.models.ReportStockEvent;
import com.example.android.loa.network.models.ReportSumByPeriodBox;
import com.example.android.loa.network.models.ResponseData;
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.network.models.SpinnerItem;
import com.example.android.loa.network.models.SpinnerType;
import com.example.android.loa.network.models.Spinners;
import com.example.android.loa.network.models.StockEvent;
import com.example.android.loa.network.models.User;
import com.example.android.loa.network.models.UserToken;

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

    //ITEM_EMPLOYEE

    @PUT("items_employee_file.php")
    Observable<Response<Item_employee>> putItemEmployee(@Body Item_employee e);

    @GET("items_employee_file.php")
    Observable<Response<List<ReportItemEmployee>>> getHoursByMonth(@Query("method") String method, @Query("page") Integer page, @Query("employee_id") Long id);

    @GET("items_employee_file.php")
    Observable<Response<List<Item_employee>>> getItemsEmployeeByEmployeeIdByMonth(@Query("employee_id") Long id,
                                                                                        @Query("since")String  month1,@Query("to") String month2);

    @DELETE("items_employee_file.php")
    Observable<ResponseBody>  deleteItemEmployee(@Query("id") Long id);

    //EMPLOYEE

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

    //CLIENTS

    @PUT("clients.php")
    Observable<Response<Client>> putClient(@Body Client c);

    @GET("clients.php")
    Observable<Response<Client>> getClient(@Query("id") Long id);

    @GET("clients.php")
    Observable<Response<List<Client>>> getClientsByPage(@Query("page") Integer page, @Query("query") String query,@Query("order") String order );

    @GET("clients.php")
    Observable<Response<List<ReportSimpelClient>>> getClients(@Query("method") String method);


    @GET("clients.php")
    Observable<Response<List<Client>>> getClientsByPageByCreator(@Query("page") Integer page,@Query("employee_creator_id") String name );

    @POST("clients.php")
    Observable<Response<Client>> postClient(@Body Client c);

    @DELETE("clients.php")
    Observable<ResponseBody>  deleteClient(@Query("id") Long id);

    //ITEMS_FILE

    @PUT("items_file.php")
    Observable<Response<Item_file>> putItemFile(@Body Item_file item);

    @GET("items_file.php")
    Observable<Response<List<Operation>>> getItemsByClientIdByPage(@Query("method") String method, @Query("page") Integer page, @Query("client_id") Long client_id);


    @POST("items_file.php")
    Observable<Response<Item_file>> postItemFile(@Body Item_file item);

    @GET("items_file.php")
    Observable<Response<AmountResult>> getTotalAmount(@Query("method") String method);

    @DELETE("items_file.php")
    Observable<ResponseBody>  deleteItemFile(@Query("id") Long id,@Query("modify") String modify);




    @GET("extractions.php")
    Observable<Response<AmountResult>> getTotalExtractionsAmount(@Query("method") String method, @Query("date") String date, @Query("dateTo") String dateTo);


    @PUT("extractions.php")
    Observable<Response<Extraction>> putExtraction(@Body Extraction e);

    @GET("extractions.php")
    Observable<Response<Extraction>> getExtraction(@Query("id") Long id);

    @GET("extractions.php")
    Observable<Response<List<Extraction>>> getExtractionsByPage(@Query("page") Integer page,@Query("since") String created);

    @GET("extractions.php")
    Observable<Response<List<ReportExtraction>>> getExtractions(@Query("method") String method,@Query("page") Integer page,@Query("type") String type ,@Query("groupby") String groupby );

    @GET("extractions.php")
    Observable<Response<List<Extraction>>> getExtractionsByPageAndDate(@Query("page") Integer page,@Query("since") String created,@Query("to") String next );


    @GET("boxes.php")
    Observable<Response<List<Box>>> getBoxesByPageAndDate(@Query("page") Integer page,@Query("since") String created,@Query("to") String next );

    @GET("boxes.php")
    Observable<Response<ReportNewBox>> getPreviousBox(@Query("method") String method, @Query("to") String created, @Query("date") String date, @Query("dateTo") String dateTo);


    @POST("extractions.php")
    Observable<Response<Extraction>> postExtraction(@Body Extraction e);

    @DELETE("extractions.php")
    Observable<ResponseBody>  deleteExtraction(@Query("id") Long id);

    //PARALLEL MONEY MOVEMENTS

    @POST("parallel_money_movements.php")
    Observable<Response<ParallelMoneyMovement>> postMoneyMovement(@Body ParallelMoneyMovement m);

    @DELETE("parallel_money_movements.php")
    Observable<ResponseBody>  deleteMoneyMovement(@Query("id") Long id);

    @PUT("parallel_money_movements.php")
    Observable<Response<ParallelMoneyMovement>> putMoneyMovement(@Body ParallelMoneyMovement e);


    @GET("parallel_money_movements.php")
    Observable<Response<List<ReportParallelMoneyMovement>>> getReportMoneyMovements(@Query("method") String m,@Query("page") Integer page, @Query("type") String type ,@Query("groupby") String groupby,@Query("billed") String billed );

    //INCOMES

    @POST("incomes.php")
    Observable<Response<Income>> postIncome(@Body Income in);


    @GET("incomes.php")
    Observable<Response<List<ReportIncome>>> getReportIncomes(@Query("method") String method, @Query("page") Integer page, @Query("state") String state ,@Query("groupby") String groupby);

    @PUT("incomes.php")
    Observable<Response<Income>> putIncome(@Body Income i);

    @DELETE("incomes.php")
    Observable<ResponseBody>  deleteIncome(@Query("id") Long id);

    @PUT("boxes.php")
    Observable<Response<Box>> putBox(@Body Box b);

    @GET("boxes.php")
    Observable<Response<Box>> getBox(@Query("id") Long id);

    @GET("boxes.php")
    Observable<Response<ReportSumByPeriodBox>> getAmountByPeriod(@Query("method") String m, @Query("since") String since,@Query("to") String to);

    @GET("boxes.php")
    Observable<Response<List<ReportMonthBox>>> getTotalMonthBoxes(@Query("method") String m, @Query("page") Integer page);

    @GET("boxes.php")
    Observable<Response<List<Box>>> getBoxesByPage2(@Query("page") Integer page);

    @GET("boxes.php")
    Observable<Response<List<Box>>> getBoxesByPageByPeriod(@Query("method") String method,@Query("page") Integer page,@Query("since") String since,@Query("to") String to);

    @GET("events.php")
    Observable<Response<List<Event>>> getEventsByPage(@Query("page") Integer page );

    @GET("events.php")
    Observable<Response<List<Event>>> getEventsByPageByClientId(@Query("page") Integer page,@Query("client_id") Long client_id );

    @POST("boxes.php")
    Observable<Response<Box>> postBox(@Body Box b);

    @DELETE("boxes.php")
    Observable<ResponseBody>  deleteBox(@Query("id") Long id);

    @GET("products.php")
    Observable<Response<List<Product>>> getProductsByPageByItemByBrandAndType(@Query("method") String method,@Query("page") Integer page,@Query("item") String item,@Query("brand") String brand,@Query("type") String type,
            @Query("model") String model, @Query("deleted") String deleted);

    @GET("products.php")
    Observable<Response<List<ReportProduct>>> getProductsByPageByItemByBrandAndTypePriceManager(@Query("method") String method, @Query("page") Integer page, @Query("item") String item, @Query("brand") String brand, @Query("type") String type,
                                                                                                @Query("model") String model, @Query("deleted") String deleted);

    @GET("products.php")
    Observable<Response<List<Product>>> getProductsByItemType(@Query("method") String method,@Query("item") String item,@Query("type") String type);

    @GET("products.php")
    Observable<Response<List<Product>>> getDeletedProducts(@Query("method") String method,@Query("page") Integer page,@Query("deleted") String item);


    @GET("products.php")
    Observable<Response<Integer>> getSumStockByFilterProducts(@Query("method") String method, @Query("item") String item,
                                                                     @Query("brand") String brand, @Query("type") String type2,@Query("model") String model, @Query("deleted") String deleted);



    @GET("products.php")
    Observable<Response<ResponseData>> updatePrices(@Query("method") String method,  @Query("selected_products") String prods, @Query("number") Double price);

    @POST("products.php")
    Observable<Response<Product>> postProduct(@Body Product p);

    @PUT("products.php")
    Observable<Response<Product>> putProduct(@Body Product p);



    //GENERAL STOCK EVENTS

    @GET("stock_general_events.php")
    Observable<Response<List<GeneralStock>>> getGeneralStockEvents(@Query("method") String method,@Query("page") Integer page, @Query("item") String item, @Query("type") String type);

    @POST("stock_general_events.php")
    Observable<Response<GeneralStock>> postGeneralStockEvent(@Body GeneralStock s);

    @PUT("stock_general_events.php")
    Observable<Response<GeneralStock>> putGeneralStock(@Body GeneralStock s);

    //STOCK EVENTS

    @POST("stock_events.php")
    Observable<Response<StockEvent>> postStockEvent(@Body StockEvent s,@Query("balance") String balance);

    @PUT("stock_events.php")
    Observable<Response<StockEvent>> putStockEvent(@Body StockEvent s);

    @DELETE("stock_events.php")
    Observable<ResponseBody> deleteStockEvent(@Query("id") Long id);

    @GET("stock_events.php")
    Observable<Response<List<StockEvent>>> getSotckeventsByPageSinceTo(@Query("page") Integer page,@Query("since") String created,@Query("to") String next, @Query("id_product") Long id_product);

    @GET("stock_events.php")
    Observable<Response<List<ReportStockEvent>>> getStockeventsSales(@Query("method") String method, @Query("created") String created,@Query("item") String item,@Query("groupby") String groupby);


    @GET("stock_events.php")
    Observable<Response<List<ReportStockEvent>>> getStockeventsEntries(@Query("method") String method, @Query("created") String created,@Query("item") String item,@Query("groupby") String groupby);

    @GET("stock_events.php")
    Observable<Response<List<ReportItemFileClientEvent>>> getSotckeventsFileSales(@Query("method") String method,@Query("created") String created,@Query("groupby") String groupby);

    @GET("stock_events.php")
    Observable<Response<List<StockEvent>>> getSotckeventsByPage(@Query("page") Integer page, @Query("id_product") Long id_product);


    @GET("stock_events.php")
    Observable<Response<List<ReportStockEvent>>> getReportStockEvents(@Query("page") Integer page, @Query("method") String method,
                                                                      @Query("since_s") String created, @Query("to_s") String createdTo, @Query("item") String item);

    @GET("stock_events.php")
    Observable<Response<AmountResult>> getAmountSales( @Query("method") String method,
                                                                      @Query("created") String created);

    @GET("stock_events.php")
    Observable<Response<ReportStockEvent>> updateItemStockEventReport( @Query("method") String method, @Query("value") Double value,
                                                       @Query("date") String date,@Query("payment_method") String payment_method,@Query("detail") String detail,@Query("id") Long id);

    @GET("stock_events.php")
    Observable<Response<AmountResult>> getAmountSalesCard( @Query("method") String method,
                                                       @Query("created") String created);


    @GET("products.php")
    Observable<Response<SpinnerData>> deleteProduct(@Query("method") String method,@Query("id") Long id_product);

    @GET("products.php")
    Observable<Response<Spinners>> getSpinners(@Query("method") String method, @Query("item") String item,
                                                @Query("brand") String brand, @Query("type") String type2, @Query("model") String model, @Query("deleted") String deleted);

    @GET("products.php")
    Observable<Response<List<SpinnerType>>> getTypes(@Query("method") String method, @Query("item") String item);

    @GET("products.php")
    Observable<Response<List<SpinnerItem>>> getItems(@Query("method") String method);


    @GET("products.php")
    Observable<Response<ResponseData>> checkExistProduct(@Query("item") String item,@Query("brand") String brand,@Query("type") String type,@Query("model") String model,@Query("method") String method);

    @GET("products.php")
    Observable<Response<List<SpinnerType>>> getSpinnerType(@Query("method") String method, @Query("tt") String type);


    @POST("users.php")
    Observable<Response<User>> postUser(@Body User u);


    @GET("login.php")
    Observable<Response<UserToken>> login(@Query("name") String name, @Query("hash_password") String password,
                                          @Query("method") String method);

    @POST("login.php")
    Observable<Response<User>> register(@Body User u,@Query("key_access") String key,@Query("method") String method);


    //price_events

    @GET("price_events.php")
    Observable<Response<List<ReportPriceEvent>>> getPriceEvents(@Query("page") Integer page, @Query("method") String method);

    @GET("stock_events.php")
    Observable<Response<List<ReportSale>>> getSales(@Query("page") Integer page,@Query("item") String item,@Query("groupby") String groupby, @Query("method") String method);


    @GET("stock_events.php")
    Observable<Response<List<ReportEntrie>>> getEntries(@Query("page") Integer page, @Query("item") String item, @Query("groupby") String groupby, @Query("method") String method);


    @GET("stock_events.php")
    Observable<Response<List<ReportStockEvent>>> getEvents(@Query("page") Integer page, @Query("item") String item, @Query("groupby") String groupby, @Query("method") String method);
}


/*

    @GET("items_employee_file.php")
    Observable<Response<Item_employee>> getItemEmployeeByDateAndEmployeeid(@Query("employee_id") Long id,
                                                                                        @Query("date")String  month1);

    @GET("boxes.php")
    Observable<Response<List<Box>>> getBoxesByPage(@Query("method") String m, @Query("page") Integer page,@Query("created") String created );


    @PUT("products.php")
    Observable<Response<Product>> putProduct(@Body Product p);

    @GET("products.php")
    Observable<Response<Product>> getProduct(@Query("id") Long id);

      @GET("products.php")
    Observable<Response<List<SpinnerData>>> getSpinner(@Query("method") String method,@Query("tt") String type);

    @GET("products.php")
    Observable<Response<List<Product>>> getProductsByPageByBrandAndType(@Query("method") String method,@Query("page") Integer page,@Query("brand") String brand,@Query("type") String type);


   @DELETE("items_file.php")
    Observable<ResponseBody>  deleteItemFileByModify(@Query("method") String method,@Query("modify") String modify,@Query("id") Long id);

    @GET("items_employee_file.php")
    Observable<Response<AmountResult>> getAmountHoursByMonth(@Query("method") String method,@Query("since")String  month1,@Query("to") String month2,@Query("employee_id") Long id);

    @POST("items_employee_file.php")
    Observable<Response<Item_employee>> postItemEmploye(@Body  Item_employee e);


  @GET("items_employee_file.php")
    Observable<Response<Item_employee>> getItemEmployee(@Query("id") Long id);


    @GET("items_employee_file.php")
    Observable<Response<List<Item_employee>>> getItemsEmployeeByPageByEmployeeId(@Query("method") String method,@Query("page") Integer page,@Query("employee_id") Long id);

    @GET("items_employee_file.php")
    Observable<Response<List<Item_employee>>> getItemsEmployeeByPageByEmployeeIdByMonth(@Query("method") String method,@Query("page") Integer page,@Query("employee_id") Long id,
                                                                                        @Query("since")String  month1,@Query("to") String month2);

     @GET("stock_events.php")
    Observable<Response<List<ReportStockEvent>>> getStockeventsSalesPaginator(@Query("method") String method, @Query("created") String created,@Query("item") String item,@Query("groupby") String groupby, @Query("page") Integer page);

                                                                                         @GET("incomes.php")
    Observable<Response<List<Income>>> getIncomes(@Query("method") String method,@Query("page") Integer page);

/*
    @GET("products.php")
    Observable<Response<List<SpinnerData>>> getSpinnerByItemByTypeByBrand(@Query("method") String method,@Query("tt") String type,@Query("item") String item,
                                                                          @Query("brand") String brand,  @Query("type") String type2);

    @GET("products.php")
    Observable<Response<List<SpinnerType>>> getSpinnerByItemByTypeByBrandType(@Query("method") String method,@Query("tt") String type,@Query("item") String item,
                                                                          @Query("brand") String brand,  @Query("type") String type2);

    @GET("products.php")
    Observable<Response<List<SpinnerItem>>> getSpinnerItemByItemByTypeByBrandType(@Query("method") String method, @Query("tt") String type, @Query("item") String item,
                                                                                  @Query("brand") String brand, @Query("type") String type2);

                                                                                    @GET("items_file.php")
    Observable<Response<AmountResult>> getAmountItemFile( @Query("method") String method,
                                                           @Query("created") String created);

    @GET("items_file.php")
    Observable<Response<List<ReportItemFileClientEvent>>> getItemsFileClientEvent(  @Query("method") String method,@Query("since") String created, @Query("to") String next);


   /* @DELETE("products.php")
    Observable<ResponseBody>  deleteProduct(@Query("id") Long id);

        @GET("products.php")
    Observable<Response<List<Product>>> getProductsByPage(@Query("page") Integer page);
    */

