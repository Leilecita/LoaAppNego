package com.example.android.loa.network;

public class ApiUtils {
    private ApiUtils() {}

    //loa dev es la que esta acitva
    // public static final String BASE_URL = "http://loadev.abarbieri.com.ar/";

    //esta la tenia en el celu fede. se mirgaron los datos a loadev
 //  public static final String BASE_URL = "http://loa.abarbieri.com.ar/";

    //public static final String BASE_URL = "http://192.168.88.9/loaserver/";
   // public static final String BASE_URL = "http://192.168.0.16/loaserver/";
    public static final String BASE_URL = "http://192.168.88.6/loaserver/";

    public static APIService getAPIService() {
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static APIService getAPISessionService() {
        return RetrofitClient.getSessionClient(BASE_URL).create(APIService.class);
    }

    public static final String getImageUrl(String imagePath){
        if(imagePath.startsWith("/")){
            imagePath = imagePath.replaceFirst("/","");
        }
        return BASE_URL + imagePath;
    }
}
