package com.example.hao.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hao on 2017/12/27.
 */


//        "suggestion":{
//                "comf":{
//                    "txt":"白天天气较热，虽然有雨，但仍然无法削弱较高气温给人们带来的暑意， 这种天气会让您感到不很舒适。
//                }
//
//                "cw" :{
//                    "txt":"不宜洗车，未来 24小时内有雨，如果在此期间洗车，雨水和路上的泥水 可能会再次弄脏您的爱车。"
//                }
//
//                "sport":{
//                    "txt":"有降水，且风力较强，推荐您在室内进行低强度运动;若坚持户外运动， 请选择避雨防风的地点。"
//                }
//            }


public class Suggestion {
    @SerializedName("comf")
    public Comfort comfort;

    @SerializedName("cw")
    public CarWash carWash;

    public Sport sport;

    public class Comfort{
        @SerializedName("txt")
        public String info;
    }

    public class CarWash{
        @SerializedName("txt")
        public String info;
    }

    public class Sport{
        @SerializedName("txt")
        public String info;
    }

}
