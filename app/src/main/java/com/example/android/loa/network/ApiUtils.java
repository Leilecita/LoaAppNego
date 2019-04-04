package com.example.android.loa.network;

public class ApiUtils {
    private ApiUtils() {}

   //public static final String BASE_URL = "http://loadev.abarbieri.com.ar/";
    //public static final String BASE_URL = "http://loa.abarbieri.com.ar/";
   public static final String BASE_URL = "http://192.168.0.87/loaserver/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static final String getImageUrl(String imagePath){
        if(imagePath.startsWith("/")){
            imagePath = imagePath.replaceFirst("/","");
        }
        return BASE_URL + imagePath;
    }
}
