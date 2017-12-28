package com.example.hao.coolweather.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Hao on 2017/12/27.
 */


//        "now":{
//                "tmp":"29"
//                "cond":{
//                    "txt":"阵雨"
//                    }
//                }


public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More more;

    public class More{
        @SerializedName("txt")
        public String info;
    }
}
