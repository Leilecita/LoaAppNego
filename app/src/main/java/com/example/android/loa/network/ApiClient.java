package com.example.android.loa.network;

import android.graphics.Path;
import android.util.Log;

import com.example.android.loa.network.models.AmountResult;
import com.example.android.loa.network.models.Box;
import com.example.android.loa.network.models.Client;
import com.example.android.loa.network.models.Employee;
import com.example.android.loa.network.models.Extraction;
import com.example.android.loa.network.models.Item_employee;
import com.example.android.loa.network.models.Item_file;
import com.example.android.loa.network.models.Operation;
import com.example.android.loa.network.models.Event;
import com.example.android.loa.network.models.Product;
import com.example.android.loa.network.models.QuantityProducts;
import com.example.android.loa.network.models.ResponseData;
import com.example.android.loa.network.models.SpinnerData;
import com.example.android.loa.network.models.SpinnerItem;
import com.example.android.loa.network.models.SpinnerType;
import com.example.android.loa.network.models.Spinners;
import com.example.android.loa.network.models.StockEvent;
import com.example.android.loa.network.models.User;
import com.example.android.loa.network.models.UserToken;
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


    public void putItemEmployee(Item_employee e, GenericCallback<Item_employee> callback){
        handleRequest( ApiUtils.getAPISessionService().putItemEmployee(e), callback);
    }

    public void getItemEmployee(Long id, GenericCallback<Item_employee> callback){
        handleRequest( ApiUtils.getAPISessionService().getItemEmployee(id), callback);
    }

    public void getItemEmployeeByDateAndEmployeeId(Long id,String date, GenericCallback<Item_employee> callback){
        handleRequest( ApiUtils.getAPISessionService().getItemEmployeeByDateAndEmployeeid(id,date), callback);
    }

    public void getItemsEmployeeByPageByEmployeeId(Integer page,Long employee_id,final GenericCallback<List<Item_employee>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getItemsEmployeeByPageByEmployeeId("listHours",page,employee_id), callback);
    }

    public void getItemsEmployeeByPageByEmployeeIdByMonth(Integer page,Long employee_id,String month1,String month2,final GenericCallback<List<Item_employee>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getItemsEmployeeByPageByEmployeeIdByMonth(page,employee_id,month1,month2), callback);
    }
    public void getItemsEmployeeByEmployeeIdByMonth(Long employee_id,String month1,String month2,final GenericCallback<List<Item_employee>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getItemsEmployeeByEmployeeIdByMonth(employee_id,month1,month2), callback);
    }


    public void getAmountHoursByMonth(String month1,String month2,Long employee_id, GenericCallback<AmountResult> callback){
        handleRequest( ApiUtils.getAPISessionService().getAmountHoursByMonth("amountHoursByMonth",month1,month2,employee_id), callback);
    }


    public void postItemEmploye(Item_employee e,GenericCallback<Item_employee> callback){
        handleRequest( ApiUtils.getAPISessionService().postItemEmploye(e), callback);
    }

    public void deleteItemEmployee(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPISessionService().deleteItemEmployee(id), callback);
    }


    public void putEmployee(Employee e, GenericCallback<Employee> callback){
        handleRequest( ApiUtils.getAPISessionService().putEmployee(e), callback);
    }

    public void getEmployee(Long id, GenericCallback<Employee> callback){
        handleRequest( ApiUtils.getAPISessionService().getEmployee(id), callback);
    }

    public void getEmployees( GenericCallback<List<Employee>> callback){
        handleRequest( ApiUtils.getAPISessionService().getEmployees(), callback);
    }

    public void getEmployeesByPage( Integer page,final GenericCallback<List<Employee>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getEmployeesByPage(page), callback);
    }
    public void postEmployee(Employee e,GenericCallback<Employee> callback){
        handleRequest( ApiUtils.getAPISessionService().postEmployee(e), callback);
    }

    public void deleteEmployee(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPISessionService().deleteEmployee(id), callback);
    }


    public void searchClients(String query, Integer page,String order,final GenericCallback<List<Client>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getClientsByPage(page,query,order), callback);
    }

    public void getClientsByCreator(Integer page,String name,final GenericCallback<List<Client>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getClientsByPageByCreator(page,name), callback);
    }

    public void putClient(Client c, GenericCallback<Client> callback){
        handleRequest( ApiUtils.getAPISessionService().putClient(c), callback);
    }

    public void getClient(Long id, GenericCallback<Client> callback){
        handleRequest( ApiUtils.getAPISessionService().getClient(id), callback);
    }

    public void postClient(Client c,GenericCallback<Client> callback){
        handleRequest( ApiUtils.getAPISessionService().postClient(c), callback);
    }

    public void deleteClient(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPISessionService().deleteClient(id), callback);
    }

    public void putItemFile(Item_file item, GenericCallback<Item_file> callback){
        handleRequest( ApiUtils.getAPISessionService().putItemFile(item), callback);
    }

    public void getItemFile(Long id, GenericCallback<Item_file> callback){
        handleRequest( ApiUtils.getAPISessionService().getItemFile(id), callback);
    }

    public void getOperationAcum(Long client_id,GenericCallback<AmountResult> callback){
        handleRequest( ApiUtils.getAPISessionService().getOperationAcum("amountByClientId",client_id), callback);
    }

    public void getTotalAmount(GenericCallback<AmountResult> callback){
        handleRequest( ApiUtils.getAPISessionService().getTotalAmount("totalAmount"), callback);
    }




    public void getItemsByClientIdByPage(Integer page,Long client_id, GenericCallback<List<Operation>> callback){
        handleRequest( ApiUtils.getAPISessionService().getItemsByClientIdByPage("listDebtsByClientId",page,client_id), callback);
    }

    public void postItemfile(Item_file item,GenericCallback<Item_file> callback){
        handleRequest( ApiUtils.getAPISessionService().postItemFile(item), callback);
    }

    public void deleteItemFile(Long id,String modify, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPISessionService().deleteItemFile(id,modify), callback);
    }
    public void deleteItemFileByModify(String modify,Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPISessionService().deleteItemFileByModify("delete2",modify,id), callback);
    }

    public void getExtractionsByPage( Integer page,String created,final GenericCallback<List<Extraction>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getExtractionsByPage(page,created), callback);
    }
    public void getExtractionsByPageAndDate( Integer page,String created,String next,final GenericCallback<List<Extraction>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getExtractionsByPageAndDate(page,created,next), callback);
    }


    public void getBoxesByPageAndDate( Integer page,String created,String next,final GenericCallback<List<Box>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getBoxesByPageAndDate(page,created,next), callback);
    }

    public void putExtraction(Extraction e, GenericCallback<Extraction> callback){
        handleRequest( ApiUtils.getAPISessionService().putExtraction(e), callback);
    }

    public void getExtraction(Long id, GenericCallback<Extraction> callback){
        handleRequest( ApiUtils.getAPISessionService().getExtraction(id), callback);
    }

    public void getTotalExtractionAmount(String date,String dateTo,GenericCallback<AmountResult> callback){
        handleRequest( ApiUtils.getAPISessionService().getTotalExtractionsAmount("amountExtractions",date,dateTo), callback);
    }

    public void postExtraction(Extraction e,GenericCallback<Extraction> callback){
        handleRequest( ApiUtils.getAPISessionService().postExtraction(e), callback);
    }

    public void deleteExtraction(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPISessionService().deleteExtraction(id), callback);
    }


    public void getBoxesByPage2(Integer page,final GenericCallback<List<Box>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getBoxesByPage2(page),callback);
    }

    public void getEventsByPage(Integer page,final GenericCallback<List<Event>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getEventsByPage(page), callback);
    }

    public void getEventsByPageByClientId(Integer page,Long client_id,final GenericCallback<List<Event>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getEventsByPageByClientId(page,client_id), callback);
    }

    public void putBox(Box b, GenericCallback<Box> callback){
        handleRequest( ApiUtils.getAPISessionService().putBox(b), callback);
    }

    public void getBox(Long id, GenericCallback<Box> callback){
        handleRequest( ApiUtils.getAPISessionService().getBox(id), callback);
    }

    public void postBox(Box b,GenericCallback<Box> callback){
        handleRequest( ApiUtils.getAPISessionService().postBox(b), callback);
    }

    public void deleteBox(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPISessionService().deleteBox(id), callback);
    }



  /*  public void deleteProduct(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPISessionService().deleteProduct(id), callback);
    }*/

    public void deleteProduct( Long id,GenericCallback<SpinnerData> callback){
        handleRequest( ApiUtils.getAPISessionService().deleteProduct("deleteProduct",id), callback);
    }

    public void putProduct(Product p, GenericCallback<Product> callback){
        handleRequest( ApiUtils.getAPISessionService().putProduct(p), callback);
    }

    public void getProduct(Long id, GenericCallback<Product> callback){
        handleRequest( ApiUtils.getAPISessionService().getProduct(id), callback);
    }


    public void getProductsByPage( Integer page,final GenericCallback<List<Product>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getProductsByPage(page), callback);
    }

    public void getProductsByPageByBrandAndType( Integer page,String brand,String type,final GenericCallback<List<Product>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getProductsByPageByBrandAndType("getProducts",page,brand,type), callback);
    }

    public void getProductsByPageByItemByBrandAndType( Integer page,String item,String brand,String type,String model,String deleted,final GenericCallback<List<Product>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getProductsByPageByItemByBrandAndType("getProducts2",page,item,brand,type,model,deleted), callback);
    }

    public void getDeletedProducts( Integer page,String deleted,final GenericCallback<List<Product>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getDeletedProducts("getDeletedProducts",page,deleted), callback);
    }


    public void postProduct(Product p,GenericCallback<Product> callback){
        handleRequest( ApiUtils.getAPISessionService().postProduct(p), callback);
    }
    public void postStockEvent(StockEvent s,String balance, GenericCallback<StockEvent> callback){
        handleRequest( ApiUtils.getAPISessionService().postStockEvent(s,balance), callback);
    }

    public void putStockEvent(StockEvent s, GenericCallback<StockEvent> callback){
        handleRequest( ApiUtils.getAPISessionService().putStockEvent(s), callback);
    }

    public void deleteStockEvent(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPISessionService().deleteStockEvent(id), callback);
    }


    public void getSpinner( String type,GenericCallback<List<SpinnerData>> callback){
        handleRequest( ApiUtils.getAPISessionService().getSpinner("spinner",type), callback);
    }

    public void checkExistProduct(String item,String brand,String type,String model, GenericCallback<ResponseData> callback){
        handleRequest( ApiUtils.getAPISessionService().checkExistProduct(item,brand,type,model,"checkExistProduct"), callback);
    }

  /*  public void getSpinnerByItemByTypeByBrand( String type, String item, String brand,String type2,GenericCallback<List<SpinnerData>> callback){
        handleRequest( ApiUtils.getAPISessionService().getSpinnerByItemByTypeByBrand("spinner",type,item,brand,type2), callback);
    }

    public void getSpinnerByItemByTypeByBrandType( String type, String item, String brand,String type2,GenericCallback<List<SpinnerType>> callback){
        handleRequest( ApiUtils.getAPISessionService().getSpinnerByItemByTypeByBrandType("spinner",type,item,brand,type2), callback);
    }

    public void getSpinnerItemByItemByTypeByBrandType( String type, String item, String brand,String type2,GenericCallback<List<SpinnerItem>> callback){
        handleRequest( ApiUtils.getAPISessionService().getSpinnerItemByItemByTypeByBrandType("spinner",type,item,brand,type2), callback);
    }*/

    public void getSpinners(  String item, String brand,String type2,String model,String deleted,GenericCallback<Spinners> callback){
        handleRequest( ApiUtils.getAPISessionService().getSpinners("getSpinners",item,brand,type2,model,deleted), callback);
    }

    public void getSumStockByFilterProducts( String item, String brand,String type2,String model,String deleted,GenericCallback<Integer> callback){
        handleRequest( ApiUtils.getAPISessionService().getSumStockByFilterProducts("sumAllStock",item,brand,type2,model,deleted), callback);
    }

    public void getSpinnerType( String type,GenericCallback<List<SpinnerType>> callback){
        handleRequest( ApiUtils.getAPISessionService().getSpinnerType("spinner",type), callback);
    }


    public void getSotckeventsByPageSinceTo( Integer page,String since,String to,Long id_product,final GenericCallback<List<StockEvent>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getSotckeventsByPageSinceTo(page,since,to,id_product), callback);
    }
    public void getSotckeventsByPage( Integer page,Long id_product,final GenericCallback<List<StockEvent>> callback ){
        handleRequest( ApiUtils.getAPISessionService().getSotckeventsByPage(page,id_product), callback);
    }



    public void login(String name,String password, GenericCallback<UserToken> callback){
        handleRequest( ApiUtils.getAPIService().login(name,password,"login"), callback);
    }

    public void register(User u, String key, GenericCallback<User> callback){
        handleRequest( ApiUtils.getAPIService().register(u,key,"register"), callback);
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

    private <T> void handleRequest2(Observable<Response<T>> request, final GenericCallback<T> callback){
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
