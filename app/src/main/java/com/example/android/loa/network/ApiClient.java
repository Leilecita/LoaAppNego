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
        handleRequest( ApiUtils.getAPIService().putItemEmployee(e), callback);
    }

    public void getItemEmployee(Long id, GenericCallback<Item_employee> callback){
        handleRequest( ApiUtils.getAPIService().getItemEmployee(id), callback);
    }

    public void getItemEmployeeByDateAndEmployeeId(Long id,String date, GenericCallback<Item_employee> callback){
        handleRequest( ApiUtils.getAPIService().getItemEmployeeByDateAndEmployeeid(id,date), callback);
    }

    public void getItemsEmployeeByPageByEmployeeId(Integer page,Long employee_id,final GenericCallback<List<Item_employee>> callback ){
        handleRequest( ApiUtils.getAPIService().getItemsEmployeeByPageByEmployeeId("listHours",page,employee_id), callback);
    }

    public void getItemsEmployeeByPageByEmployeeIdByMonth(Integer page,Long employee_id,String month1,String month2,final GenericCallback<List<Item_employee>> callback ){
        handleRequest( ApiUtils.getAPIService().getItemsEmployeeByPageByEmployeeIdByMonth(page,employee_id,month1,month2), callback);
    }
    public void getItemsEmployeeByEmployeeIdByMonth(Long employee_id,String month1,String month2,final GenericCallback<List<Item_employee>> callback ){
        handleRequest( ApiUtils.getAPIService().getItemsEmployeeByEmployeeIdByMonth(employee_id,month1,month2), callback);
    }


    public void getAmountHoursByMonth(String month1,String month2,Long employee_id, GenericCallback<AmountResult> callback){
        handleRequest( ApiUtils.getAPIService().getAmountHoursByMonth("amountHoursByMonth",month1,month2,employee_id), callback);
    }


    public void postItemEmploye(Item_employee e,GenericCallback<Item_employee> callback){
        handleRequest( ApiUtils.getAPIService().postItemEmploye(e), callback);
    }

    public void deleteItemEmployee(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPIService().deleteItemEmployee(id), callback);
    }


    public void putEmployee(Employee e, GenericCallback<Employee> callback){
        handleRequest( ApiUtils.getAPIService().putEmployee(e), callback);
    }

    public void getEmployee(Long id, GenericCallback<Employee> callback){
        handleRequest( ApiUtils.getAPIService().getEmployee(id), callback);
    }

    public void getEmployees( GenericCallback<List<Employee>> callback){
        handleRequest( ApiUtils.getAPIService().getEmployees(), callback);
    }

    public void getEmployeesByPage( Integer page,final GenericCallback<List<Employee>> callback ){
        handleRequest( ApiUtils.getAPIService().getEmployeesByPage(page), callback);
    }
    public void postEmployee(Employee e,GenericCallback<Employee> callback){
        handleRequest( ApiUtils.getAPIService().postEmployee(e), callback);
    }

    public void deleteEmployee(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPIService().deleteEmployee(id), callback);
    }


    public void searchClients(String query, Integer page,String order,final GenericCallback<List<Client>> callback ){
        handleRequest( ApiUtils.getAPIService().getClientsByPage(page,query,order), callback);
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

    public void getTotalAmount(GenericCallback<AmountResult> callback){
        handleRequest( ApiUtils.getAPIService().getTotalAmount("totalAmount"), callback);
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

    public void getExtractionsByPage( Integer page,String created,final GenericCallback<List<Extraction>> callback ){
        handleRequest( ApiUtils.getAPIService().getExtractionsByPage(page,created), callback);
    }
    public void getExtractionsByPageAndDate( Integer page,String created,String next,final GenericCallback<List<Extraction>> callback ){
        handleRequest( ApiUtils.getAPIService().getExtractionsByPageAndDate(page,created,next), callback);
    }


    public void getBoxesByPageAndDate( Integer page,String created,String next,final GenericCallback<List<Box>> callback ){
        handleRequest( ApiUtils.getAPIService().getBoxesByPageAndDate(page,created,next), callback);
    }

    public void putExtraction(Extraction e, GenericCallback<Extraction> callback){
        handleRequest( ApiUtils.getAPIService().putExtraction(e), callback);
    }

    public void getExtraction(Long id, GenericCallback<Extraction> callback){
        handleRequest( ApiUtils.getAPIService().getExtraction(id), callback);
    }

    public void getTotalExtractionAmount(String date,String dateTo,GenericCallback<AmountResult> callback){
        handleRequest( ApiUtils.getAPIService().getTotalExtractionsAmount("amountExtractions",date,dateTo), callback);
    }

    public void postExtraction(Extraction e,GenericCallback<Extraction> callback){
        handleRequest( ApiUtils.getAPIService().postExtraction(e), callback);
    }

    public void deleteExtraction(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPIService().deleteExtraction(id), callback);
    }



    public void getBoxesByPage(Integer page,String created,final GenericCallback<List<Box>> callback ){
        handleRequest( ApiUtils.getAPIService().getBoxesByPage("getBoxes",page,created), callback);
    }

    public void getEventsByPage(Integer page,final GenericCallback<List<Event>> callback ){
        handleRequest( ApiUtils.getAPIService().getEventsByPage(page), callback);
    }

    public void putBox(Box b, GenericCallback<Box> callback){
        handleRequest( ApiUtils.getAPIService().putBox(b), callback);
    }

    public void getBox(Long id, GenericCallback<Box> callback){
        handleRequest( ApiUtils.getAPIService().getBox(id), callback);
    }

    public void postBox(Box b,GenericCallback<Box> callback){
        handleRequest( ApiUtils.getAPIService().postBox(b), callback);
    }

    public void deleteBox(Long id, final GenericCallback<Void> callback){
        handleDeleteRequest( ApiUtils.getAPIService().deleteBox(id), callback);
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
