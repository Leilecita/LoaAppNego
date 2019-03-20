package com.example.android.loa;

public class MathHelper {


        private static MathHelper INSTANCE = new MathHelper();

        private MathHelper(){

        }

        public static MathHelper get(){
            return INSTANCE;
        }


        public String getIntegerQuantity(Double val){
            String[] arr=String.valueOf(val).split("\\.");
            int[] intArr=new int[2];
            intArr[0]=Integer.parseInt(arr[0]);
            intArr[1]=Integer.parseInt(arr[1]);
            if(intArr[1] == 0){
                return String.valueOf(intArr[0]);
            }else{
                return String.valueOf(val);
            }
        }
}
