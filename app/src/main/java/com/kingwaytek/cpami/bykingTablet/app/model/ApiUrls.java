package com.kingwaytek.cpami.bykingTablet.app.model;

/**
 * 所有需要呼叫的 API URL都應該集中放在這裡，<br>
 * implements這裡後使用。
 *
 * @author Vincent (2016/4/18)
 */
public interface ApiUrls {

    String API_GOOGLE_GEOCODE = "http://maps.google.com/maps/api/geocode/json?address={0}&language={1}";
    String GZ_UBIKE_TAIPEI = "http://data.taipei/youbike";
    String API_UBIKE_NEW_TAIPEI = "http://data.ntpc.gov.tw/od/data/api/54DDDC93-589C-4858-9C95-18B2046CC1FC?$format=json";
}
