package com.example.hao.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hao on 2017/12/27.
 * 由于 JSON 中的一些字段可能不太适合直接作为 Java 宇段来命名，因此这里使用了
 * @SerializedName 注解的方式来让 JSON 宇段和 Java 字段之间建立映射关系。
 */


//        "basic":{
//                "city":"苏州"
//                "id":"CN101190401"
//                "update":{
//                    "loc":"2016-0808 21:58"
//                    }
//                }



public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public String updateTime;
    }
}
