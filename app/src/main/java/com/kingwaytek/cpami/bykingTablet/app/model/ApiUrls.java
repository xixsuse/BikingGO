package com.kingwaytek.cpami.bykingTablet.app.model;

/**
 * 所有需要呼叫的 API URL都應該集中放在這裡，<br>
 * implements這裡後使用。
 *
 * @author Vincent (2016/4/18)
 */
public interface ApiUrls {

    String API_GOOGLE_GEOCODE = "http://maps.google.com/maps/api/geocode/json?address={0}&language={1}";
    String API_UBIKE_TAIPEI = "https://tcgbusfs.blob.core.windows.net/blobyoubike/YouBikeTP.gz";
    String API_UBIKE_NEW_TAIPEI = "http://data.ntpc.gov.tw/od/data/api/54DDDC93-589C-4858-9C95-18B2046CC1FC?$format=json";
    String API_UBIKE_ALL = "http://biking.cpami.gov.tw/bikerec/ubike/all.json";

    String API_GOOGLE_DIRECTION =
            "https://maps.googleapis.com/maps/api/directions/json?origin={0}&destination={1}&mode={2}{3}&language={4}&key={5}";

    String API_GOOGLE_DIRECTION_MULTI_POINT =
            "https://maps.googleapis.com/maps/api/directions/json?origin={0}&destination={1}&waypoints=optimize:{2}|{3}" +
                    "&mode={4}{5}&language={6}&key={7}";

    String API_REPORT = "http://biking.cpami.gov.tw/Service/TrafficAlertUpload?" +
            "Reporter={0}&" +
            "AlertTypeID={1}&" +
            "AlertTitle={2}&" +
            "StartTime={3}&" +
            "EndTime={4}&" +
            "CityID={5}&" +
            "Detail={6}&" +
            "Lon={7}&" +
            "Lat={8}&" +
            "ReportTime={9}&" +
            "ReportEmail={10}&" +
            "Code={11}";

    String API_EVENTS = "http://biking.cpami.gov.tw/bikerec/ubike/activity_C_f.json";

    String API_BIKING_SERVICE = "http://biking.cpami.gov.tw/bikerec/db1.ashx";
    String API_POI_URL = "http://biking.cpami.gov.tw/bikerec/pic/{0}.jpg";
}
