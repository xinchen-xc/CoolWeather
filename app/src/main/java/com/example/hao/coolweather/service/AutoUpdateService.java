package com.example.hao.coolweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.telecom.Call;
import android.view.animation.AnticipateOvershootInterpolator;

import com.example.hao.coolweather.gson.Weather;
import com.example.hao.coolweather.util.HttpUtil;
import com.example.hao.coolweather.util.Utility;

import java.io.IOException;
import java.net.HttpURLConnection;

import okhttp3.Callback;
import okhttp3.Response;

public class AutoUpdateService extends Service {
    public AutoUpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
//        return null;
    }

    /**
     * 后台自动更新天气
     * @param intent
     * @param flags
     * @param startId
     * @return
     *
     * 在 onStartCommand() 方法中先是调用了 updateWeather() 方法来更新天气，
     * 然后调用了 updateBingPic() 方法来更新背景图片
     * WeatherActivity去激活 AutoUpdateService 这个服务
     *
     * 这里将更新后的数据直接存储到SharedPreferences 文件中就可以了，
     * 因为打开 WeatherActivity 的时候都会优先从 SharedPreferences缓存中读取数据。
     * 8小时后 AutoUpdateReceiver 的 onStartCommand ()方法就会重新执行，
     */
    @Override
    public int onStartCommand(Intent intent,int flags,int startId){
        updateWeather();
        updateBingPic();
        AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
        int anHour = 8*60*60*1000;  //8小时的毫秒数
        long triggerAtTime = SystemClock.elapsedRealtime() + anHour;
        Intent i = new Intent(this, AutoUpdateService.class);
        PendingIntent pi = PendingIntent.getService(this, 0, i, 0);
        manager.cancel(pi);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        return super.onStartCommand(intent,flags,startId);
    }

    /**
     * 更新天气信息
     */
    private void updateWeather(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String weatherString = prefs.getString("weather",null);
        if(weatherString != null){
            //有缓存是直接解析天气数据
            final Weather weather = Utility.handleWeatherResponse(weatherString);
            String weatherId = weather.basic.weatherId;
            String weatherUrl = "http://guolin.tech/api/weather=?cityid="+weatherId+"&key=91521742a97d4954aa5b9bf0e9c07731";
            HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
                @Override
                public void onFailure(okhttp3.Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(okhttp3.Call call, Response response) throws IOException {
                    String responseText = response.body().string();
                    Weather weather1 = Utility.handleWeatherResponse(responseText);
                    if(weather != null && "ok".equals(weather.status)){
                        SharedPreferences.Editor editer = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                        editer.putString("weather",null);
                        editer.apply();
                    }
                }
            });
        }
    }

    /**
     * 更新必应每日一图
     */
    private void updateBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, Response response) throws IOException {
                String responseText = response.body().string();
                Weather weather = Utility.handleWeatherResponse(responseText);
                if(weather != null && "ok".equals(weather.status)){
                    SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(AutoUpdateService.this).edit();
                    editor.putString("weather",responseText);
                    editor.apply();
                }
            }
        });
    }
}
