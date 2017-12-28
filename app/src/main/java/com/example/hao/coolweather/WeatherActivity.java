 package com.example.hao.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hao.coolweather.gson.Forecast;
import com.example.hao.coolweather.gson.Weather;
import com.example.hao.coolweather.util.HttpUtil;
import com.example.hao.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

 public class WeatherActivity extends AppCompatActivity {

     private ScrollView weatherLayout;
     private TextView titleCity;
     private TextView titleUpdateTime;
     private TextView degreeText;
     private TextView weatherInfoText;
     private LinearLayout forecastLayout;
     private TextView aqiText;
     private TextView pm25Text;
     private TextView comfortText;
     private TextView carWashText;
     private TextView sportText;
     private ImageView bingPicImg;

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

         /**
          * 修改状态栏透明
          * 调用了 getWindow().getDecorView() 方法拿到当前活动的 DecorView ，
          * 再调用它setSystemUiVisibility() 方法来改变系统 UI 的显示，
          * 这里传入 View.SYSTEM UIFLAG-LAYOUT-FULLSCREEN 和 View.SYSTEM_UI_FLAG_LAYOUT_STABLE 就表示活动的布局会显示在状态栏上面，
          * 最后调用一下 setStatusBarColor() 方法将状态栏设置成透明色。
          */
         if (Build.VERSION.SDK_INT >= 21){
             View decorView = getWindow().getDecorView();
             decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN| View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
             getWindow().setStatusBarColor(Color.TRANSPARENT);
         }

        setContentView(R.layout.activity_weather);

         //初始化控件
         weatherLayout = (ScrollView)findViewById(R.id.weather_layout);
         titleCity = (TextView)findViewById(R.id.title_city);
         titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
         degreeText = (TextView)findViewById(R.id.degree_text);
         weatherInfoText = (TextView)findViewById(R.id.weather_info_text);
         forecastLayout = (LinearLayout)findViewById(R.id.forecast_layout);
         aqiText = (TextView)findViewById(R.id.aqi_text);
         pm25Text = (TextView)findViewById(R.id.PM25_text);
         comfortText = (TextView)findViewById(R.id.comfort_text);
         carWashText = (TextView)findViewById(R.id.car_wash_text);
         sportText = (TextView)findViewById(R.id.sport_text);

         //加背景图片
         bingPicImg = (ImageView)findViewById(R.id.bing_pic_img);

         SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
         String weatherString = prefs.getString("weather",null);
         if(weatherString != null){
            //有缓存时直接解析天气数据
             Weather weather = Utility.handleWeatherResponse(weatherString);
             showWeatherInfo(weather);
         }else {
             String weatherId = getIntent().getStringExtra("weather_id");
             weatherLayout.setVisibility(View.INVISIBLE);
             requestWeather(weatherId);
         }

         //加背景图片
         String bingPic = prefs.getString("bing_pic",null);
         if(bingPic != null){
             Glide.with(this).load(bingPic).into(bingPicImg);
         }else{
             loadBingPic();
         }
     }

     /**
      * 根据天气 id 请求城市天气信息
      * @param weatherId
      * 先是使用参数中传入的天气 id 和之前申请好的 APIKey 拼装出一个接口地址
      * 接着调用 HttpUtil.sendOkHttpRequest() 方法来向该地址发出请求，服务器会将相应城市的天气信息以 JSON 格式返回
      * 然后在 onResponse() 回调中先调用 Utility.handleWeatherResponse() 方法将返回的 JSON 数据转换成 Weather 对象
      * 再将当前线程切换到主线程
      * 然后进行判断，如果服务器返回的 status 状态是ok，就说明请求天气成功了，
      * 此时将返回的数据缓存到 SharedPreferences 当中，并调用 showWeatherInfo( )方法来进行内容显示
      */
     public void requestWeather(final String weatherId){
         String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=91521742a97d4954aa5b9bf0e9c07731";
         HttpUtil.sendOkHttpRequest(weatherUrl, new Callback(){

             @Override
             public void onFailure(Call call, IOException e) {
                 e.printStackTrace();
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_LONG).show();
                     }
                 });
             }

             @Override
             public void onResponse(Call call, Response response) throws IOException {
                 final String responseText = response.body().string();
                 final Weather weather = Utility.handleWeatherResponse(responseText);
                 runOnUiThread(new Runnable() {
                     @Override
                     public void run() {
                         if(weather != null&&"ok".equals(weather.status)){
                             SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                             editor.putString("weather",responseText);
                             editor.apply();
                             showWeatherInfo(weather);
                         }else{
                             Toast.makeText(WeatherActivity.this,"获取天气信息失败",Toast.LENGTH_LONG).show();
                         }
                     }
                 });
             }
         });
         loadBingPic();
     }

     /**
      * 处理并展示 Weather 实体类中的数据
      * @param weather
      * 从 Weather 对象中获取数据
      * 然后显示到相应的控件上
      * 注意在未来几天天气预报的部分我们使用了一个 for 循环来处理每天的天气信息，在循环中动态加载 forecast item.xml 布局并设置相应的数据
      * 然后添加到父布局当中
      * 设置完了所有数据之后，记得要将 ScrollView 重新变成可见。
      */
     private void showWeatherInfo(Weather weather){
         String cityName = weather.basic.cityName;
         String updateTime = weather.basic.update.updateTime.split(" ")[1];
         String degree = weather.now.temperature + "℃";
         String weatherInfo = weather.now.more.info;
         titleCity.setText(cityName);
         titleUpdateTime.setText(updateTime);
         degreeText.setText(degree);
         weatherInfoText.setText(weatherInfo);
         forecastLayout.removeAllViews();
         for (Forecast forecast : weather.forecastList){
             View view = LayoutInflater.from(this).inflate(R.layout.forecast_item , forecastLayout , false);
             TextView dateText = (TextView) view.findViewById(R.id.date_text);
             TextView infoText = (TextView) view.findViewById(R.id.info_text);
             TextView maxText = (TextView) view.findViewById(R.id.max_text);
             TextView minText = (TextView) view.findViewById(R.id.min_text);
             dateText.setText(forecast.date);
             infoText.setText(forecast.more.info);
             maxText.setText(forecast.temperature.max);
             minText.setText(forecast.temperature.min);
             forecastLayout.addView(view);
         }
         if(weather.aqi != null){
             aqiText.setText(weather.aqi.city.aqi);
             pm25Text.setText(weather.aqi.city.pm25);
         }
         String comfort = "舒适度: " + weather.suggestion.comfort.info;
         String carWash = "洗车指数: " + weather.suggestion.carWash.info;
         String sport = "运动建议: " + weather.suggestion.sport.info;
         comfortText.setText(comfort);
         carWashText.setText(carWash);
         sportText.setText(sport) ;
         weatherLayout.setVisibility(View.VISIBLE);
     }

     /**
      * 加载Bing每日一图
      * 从SharedPreferences 中读取缓存的背景图片。如果有缓存的话就直接使用 Glide 来加载这张图片，
      * 如果没有的话就调用 loadBingPic() 方法去请求今日的必应背景图。
      *
      * 先是调用了 HttpUtil.sendOkHttpRequest()方法获取到必应背景图的链接，
      * 然后将这个链接缓存到 SharedPreferences 当中，再将当前线程切换到主线程，
      * 最后使用 Glide 来加载这张图片就可以了
      *
      * 在 requestWeather( )方法的最后也需要调用一下 loadBingPic() 方法，这样在每次请求天气信息的时候同时也会刷新背景图片
      */
    private void loadBingPic(){
         String requestBingPic = "http://guolin.tech/api/bing_pic";
         HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
             @Override
             public void onFailure(Call call, IOException e) {
                 e.printStackTrace();
             }

             @Override
             public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic",bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(bingPic).into(bingPicImg);
                    }
                });
             }
         });
     }
 }
