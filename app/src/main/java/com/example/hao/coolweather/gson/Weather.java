package com.example.hao.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Hao on 2017/12/27.
 * Weather 类中，我们对 Basic 、AQI 、Now 、Suggestion 、Forecast 类进行了引用。
 * 其中，由于 daily_forecast 中包含的是一个数组，因此这里使用了 List 集合来引用 Forecast类
 * 返回的天气数据中还会包含一项 status 数据，成功返回饨，失败则会返回具体的原因，那么这里也需要添加一个对应的 status 字段。
 */


//        {
//            "HeWeather":{
//                    {
//                        "status":"OK",
//                        "basic":{},
//                        "aqi":{},
//                        "now":{},
//                        "suggestion":{},
//                        "daily_forecast":[]
//                    }
//                }
//        }


public class Weather {
    public String status;
    public Basic basic;
    public AQI aqi;
    public Now now;
    public Suggestion suggestion;

    @SerializedName("daily_forecast")
    public List<Forecast> forecastList;

}
