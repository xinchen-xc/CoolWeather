package com.example.hao.coolweather.gson;

/**
 * Created by Hao on 2017/12/27.
 */


//        "aqi":{
//            "city":{
//                  "aqi":"44";
//                  "pm25":"13"
//            }
//        }


public class AQI {
    public AQICity city;

    public class AQICity {
        public String aqi;
        public String pm25;
    }
}
