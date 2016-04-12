package com.kingwaytek.cpami.bykingTablet.TrafficCondition;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.ApplicationGlobal;
import com.kingwaytek.cpami.bykingTablet.app.CreatMD5Code;
import com.kingwaytek.cpami.bykingTablet.app.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.bus.PublicTransportList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Report extends Activity implements OnItemSelectedListener {
    private Button submit = null;
    private EditText nameEditText = null;
    private EditText titleEditText = null;
    private EditText startdateEditText = null;
    private EditText enddateEditText = null;
    private EditText starttimeEditText = null;
    private EditText endtimeEditText = null;
    private EditText describeEditText = null;
    private EditText emailEditText = null;
    private Handler uploadHandler = null;
    // private ProgressDialog WaitDialog = null;
    private UtilDialog progressDialog;
    private int ConditonTypeSpinner = 0;
    private int LocatonSpinner = 0;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private int mSecond;
    private static final int START_DATE_DIALOG_ID = 0;
    private static final int START_TIME_DIALOG_ID = 1;
    private static final int END_DATE_DIALOG_ID = 3;
    private static final int END_TIME_DIALOG_ID = 4;
    private int isStartOREndDateDialog = 0xffffff;
    private int isStartOREndTimeDialog = 0xffffff;
    private final int GET_WEB_FINISH = 1;
    private final int GET_WEB_FAIL = 2;
    private final int GET_GPS_FAIL = 3;
    private String ConditionStr = "";
    private String CityIDStr = "";

    private String name = "";
    private String type = "";
    private String title = "";
    private String startTime = "";
    private String endTime = "";
    private String cityID = "";
    private String Lon = "";
    private String Lat = "";
    private String decribe = "";
    private String reportTime = "";

    private String startYear = "";
    private String startMonth = "";
    private String startDay = "";
    private String startHour = "";
    private String startMinute = "";
    private String startSecond = "";

    private String endYear = "";
    private String endMonth = "";
    private String endDay = "";
    private String endHour = "";
    private String endMinute = "";
    private String endSecond = "";

    private Map<String, Object> CityID;
    private Map<String, Object> ConditionType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report);
        progressDialog = new UtilDialog(this);
        // get the current date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);
        mSecond = c.get(Calendar.SECOND);

        CityID = new HashMap<String, Object>();
        CityID.put("台北市", "1");
        CityID.put("新北市", "2");
        CityID.put("基隆市", "3");
        CityID.put("宜蘭縣", "4");
        CityID.put("桃園縣", "5");
        CityID.put("新竹市", "6");
        CityID.put("新竹縣", "7");
        CityID.put("苗栗縣", "8");

        ConditionType = new HashMap<String, Object>();
        ConditionType.put("其他", 1);
        ConditionType.put("交通事故", 2);
        ConditionType.put("道路施工", 3);
        ConditionType.put("交通管制", 4);
        ConditionType.put("號誌故障", 5);

        initialHandler();

        nameEditText = (EditText) findViewById(R.id.name_edit);
        titleEditText = (EditText) findViewById(R.id.title_edit);
        startdateEditText = (EditText) findViewById(R.id.startdate_edit);
        enddateEditText = (EditText) findViewById(R.id.enddate_edit);
        starttimeEditText = (EditText) findViewById(R.id.starttime_edit);
        endtimeEditText = (EditText) findViewById(R.id.endtime_edit);
        describeEditText = (EditText) findViewById(R.id.describe_edit);
        emailEditText = (EditText) findViewById(R.id.email_edit);

        // 狀況類別下拉選單
        Spinner spinner = (Spinner) findViewById(R.id.conditionType_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.type_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        ConditonTypeSpinner = spinner.getId();
        spinner.setOnItemSelectedListener(this);

        // 地點下拉選單
        Spinner location_spinner = (Spinner) findViewById(R.id.locaton_spinner);
        ArrayAdapter<CharSequence> location_adapter = ArrayAdapter
                .createFromResource(this, R.array.location_array,
                        android.R.layout.simple_spinner_item);
        location_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        location_spinner.setAdapter(location_adapter);
        LocatonSpinner = location_spinner.getId();
        location_spinner.setOnItemSelectedListener(this);

        Button startdate_button = (Button) findViewById(R.id.startdate_button);
        startdate_button.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isStartOREndDateDialog = START_DATE_DIALOG_ID;
                showDialog(START_DATE_DIALOG_ID);
            }
        });

        Button starttimeButton = (Button) findViewById(R.id.starttime_button);
        starttimeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isStartOREndTimeDialog = START_TIME_DIALOG_ID;
                showDialog(START_TIME_DIALOG_ID);
            }
        });

        Button enddateButton = (Button) findViewById(R.id.enddate_button);
        enddateButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isStartOREndDateDialog = END_DATE_DIALOG_ID;
                showDialog(END_DATE_DIALOG_ID);
            }
        });

        Button endtimeButton = (Button) findViewById(R.id.endtime_button);
        endtimeButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                isStartOREndTimeDialog = END_TIME_DIALOG_ID;
                showDialog(END_TIME_DIALOG_ID);
            }
        });

        submit = (Button) findViewById(R.id.report_submit);
        submit.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.i("Report.java", "nameEditText="
                        + nameEditText.getText().toString());
                if (nameEditText.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(Report.this, "請輸入名字", Toast.LENGTH_SHORT).show();
                    return;
                } else if (titleEditText.getText().toString()
                        .equalsIgnoreCase("")) {
                    Toast.makeText(Report.this, "請輸入標題", Toast.LENGTH_SHORT).show();
                    return;
                } else if (describeEditText.getText().toString()
                        .equalsIgnoreCase("")) {
                    Toast.makeText(Report.this, "請簡短描述", Toast.LENGTH_SHORT).show();
                    return;
                }

                // WaitDialog = ProgressDialog.show(Report.this, "請稍候片刻",
                // "上傳中...", true);
                progressDialog.progressDialog("請稍候片刻", "上傳中...");
                new Thread() {
                    private Dialog WeatherDialog;
                    private String email;

                    @Override
                    public void run() {
                        try {
                            String result = "";
                            double Lon = 0.0, Lat = 0.0;
                            Location loc = null;
                            if (ApplicationGlobal.gpsListener!= null) {
                                loc = ApplicationGlobal.gpsListener
                                        .getLastLocation();
                            }
                            if (loc != null) {
                                Lon = loc.getLongitude();
                                Lat = loc.getLatitude();
                            } else {
                                WeatherDialog.dismiss();
                                uploadHandler.sendMessage(uploadHandler
                                        .obtainMessage(GET_GPS_FAIL, "無法取得目前位置"));
                                return;
                            }
                            // Internet Connect
                            Date date = new Date();
                            String MD5Code = CreatMD5Code.getMD5((String
                                    .valueOf(((date.getMonth() + 1) + date
                                            .getHours())
                                            * (1208 + date.getDate())) + "Kingway")
                                    .getBytes());
                            // *範例
                            // String TrafficAlertUploadURL =
                            // "http://192.168.1.186:8080/BikeGo/TrafficAlertList?AlertStartTime=201111111334&AlertEndTime=201111111334&Code="+MD5Code;//updata的URL
                            // String TrafficAlertUploadURL =
                            // "http://192.168.1.186:8080/BikeGo/TrafficAlertUpload?"+"Reporter="+"王大明"+"&"+"AlertTypeID="+"1"+"&"+"AlertTitle="+"車禍"+"&"+
                            // "StartTime="+"192008011234"+"&"+"EndTime="+"192008011234"+"&"+"CityID="+"2"+"&"+"Detail="+"rtyuio"+"&"+"Lon="+String.valueOf(Lon).substring(0,
                            // 8)+"&"+"Lat="+
                            // String.valueOf(Lat).substring(0,
                            // 8)+"&"+"ReportTime="+"19200801123457"+"&Code="+MD5Code;

                            name = nameEditText.getText().toString();
                            type = String.valueOf(ConditionType
                                    .get(ConditionStr));
                            title = titleEditText.getText().toString();
                            cityID = String.valueOf(CityID.get(CityIDStr));
                            startTime = startYear + startMonth + startDay
                                    + startHour + startMinute;
                            endTime = endYear + endMonth + endDay + endHour
                                    + endMinute;
                            decribe = describeEditText.getText().toString();
                            reportTime = getReportTime();
                            email = emailEditText.getText().toString();

                            String TrafficAlertUploadURL = getResources()
                                    .getString(R.string.cpamiURL)
                                    + "TrafficAlertUpload?"
                                    + "Reporter="
                                    + name
                                    + "&"
                                    + "AlertTypeID="
                                    + type
                                    + "&"
                                    + "AlertTitle="
                                    + title
                                    + "&"
                                    + "StartTime="
                                    + startTime
                                    + "&"
                                    + "EndTime="
                                    + endTime
                                    + "&"
                                    + "CityID="
                                    + cityID
                                    + "&"
                                    + "Detail="
                                    + decribe
                                    + "&"
                                    + "Lon="
                                    + String.valueOf(Lon).substring(0, 8)
                                    + "&"
                                    + "Lat="
                                    + String.valueOf(Lat).substring(0, 8)
                                    + "&"
                                    + "ReportTime="
                                    + reportTime
                                    + "&"
                                    + "ReportEmail="
                                    + email
                                    + "&Code="
                                    + MD5Code;

                            Log.i("Report.java", "TrafficAlertUploadURL="
                                    + TrafficAlertUploadURL);
                            HttpClient cliente = new DefaultHttpClient();
                            HttpResponse response;
                            HttpPost httpPost = new HttpPost(
                                    TrafficAlertUploadURL);
                            response = cliente.execute(httpPost);
                            HttpEntity entity = response.getEntity();
                            if (entity != null) {
                                InputStream instream = entity.getContent();
                                result = PublicTransportList
                                        .convertStreamToString(instream);
                                instream.close();
                            }

                            if (result.equalsIgnoreCase("0")) {
                                progressDialog.dismiss();
                                uploadHandler.sendMessage(uploadHandler
                                        .obtainMessage(GET_WEB_FINISH,
                                                String.valueOf(0)));
                            } else {
                                progressDialog.dismiss();
                                uploadHandler.sendMessage(uploadHandler
                                        .obtainMessage(GET_WEB_FAIL,
                                                String.valueOf(0)));
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                            // WaitDialog.dismiss();
                            progressDialog.dismiss();
                            uploadHandler.sendMessage(uploadHandler
                                    .obtainMessage(GET_WEB_FAIL, ""));

                        }

                    }
                }.start();

            }
        });

    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {
        Log.i("Report.java", "onItemSelected");
        if (parent.getId() == ConditonTypeSpinner) {
            ConditionStr = parent.getItemAtPosition(pos).toString();
        } else if (parent.getId() == LocatonSpinner) {
            CityIDStr = parent.getItemAtPosition(pos).toString();
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Do nothing.

    }

    private void initialHandler() {
        uploadHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == GET_WEB_FINISH) {
                    Toast.makeText(Report.this, "上傳成功", Toast.LENGTH_LONG).show();
                } else if (msg.what == GET_GPS_FAIL) {

                    UtilDialog uit = new UtilDialog(Report.this);
                    uit.showDialog_route_plan_choice(
                            getString(R.string.dialog_gps_message), null,
                            getString(R.string.dialog_ok_button_text), null);
                } else if (msg.what == GET_WEB_FAIL) {
                    Toast.makeText(Report.this, "上傳失敗", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            String yearStr = Integer.toString(year);
            String monthStr;
            String dayStr;

            // 判斷如果是個位數的月份前面要補0
            if ((monthOfYear + 1) < 10) {
                monthStr = "0" + Integer.toString((monthOfYear + 1));
            } else {
                monthStr = Integer.toString((monthOfYear + 1));
            }

            // 判斷如果是個位數的天前面要補0
            if (dayOfMonth < 10) {
                dayStr = "0" + Integer.toString(dayOfMonth);
            } else {
                dayStr = Integer.toString(dayOfMonth);
            }

            if (isStartOREndDateDialog == START_DATE_DIALOG_ID) {
                startYear = yearStr;
                startMonth = monthStr;
                startDay = dayStr;
                startdateEditText.setText(year + "/" + monthStr + "/" + dayStr);

            } else if (isStartOREndDateDialog == END_DATE_DIALOG_ID) {
                endYear = yearStr;
                endMonth = monthStr;
                endDay = dayStr;
                enddateEditText.setText(year + "/" + monthStr + "/" + dayStr);
            }

        }
    };

    private OnTimeSetListener mTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String hourStr;
            String minuteStr;

            // 判斷如果是個位數的hour前面要補0
            if (hourOfDay < 10) {
                hourStr = "0" + Integer.toString(hourOfDay);
            } else {
                hourStr = Integer.toString(hourOfDay);
            }

            // 判斷如果是個位數的minute前面要補0
            if (minute < 10) {
                minuteStr = "0" + Integer.toString(minute);
            } else {
                minuteStr = Integer.toString(minute);
            }

            if (isStartOREndTimeDialog == START_TIME_DIALOG_ID) {
                startHour = hourStr;
                startMinute = minuteStr;
                starttimeEditText.setText(hourStr + ":" + minuteStr);
            } else if (isStartOREndTimeDialog == END_TIME_DIALOG_ID) {
                endHour = hourStr;
                endMinute = minuteStr;
                endtimeEditText.setText(hourStr + ":" + minuteStr);
            }

        }
    };

    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case START_DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
                        mDay);
            case END_DATE_DIALOG_ID:
                return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
                        mDay);
            case START_TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
                        true);
            case END_TIME_DIALOG_ID:
                return new TimePickerDialog(this, mTimeSetListener, mHour, mMinute,
                        true);
        }

        return null;
    }

    protected String getReportTime() {
        String year = String.valueOf(mYear);
        String month = String.valueOf(mMonth + 1);
        String day = String.valueOf(mDay);
        String hour = String.valueOf(mHour);
        String minute = String.valueOf(mMinute);
        String second = String.valueOf(mSecond);
        if (mMonth < 10) {
            month = "0" + month;
        }
        if (mDay < 10) {
            day = "0" + day;
        }
        if (mHour < 10) {
            hour = "0" + hour;
        }
        if (mMinute < 10) {
            minute = "0" + minute;
        }
        if (mSecond < 10) {
            second = "0" + second;
        }
        return year + month + day + hour + minute + second;
    }

    /***
     * Check text number
     *
     * @param text
     *            A string of text checked
     * @param number
     *            the maximum number of string length
     */
    protected boolean iswithinNumber(String text, int number) {
        boolean result = false;
        if (text.length() <= number) {
            result = true;
        } else {
            result = false;
        }
        return result;
    }
}