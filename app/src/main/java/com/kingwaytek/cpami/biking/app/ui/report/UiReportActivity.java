package com.kingwaytek.cpami.biking.app.ui.report;

import android.location.Location;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.ApiUrls;
import com.kingwaytek.cpami.biking.app.ui.BaseActivity;
import com.kingwaytek.cpami.biking.app.web.WebAgent;
import com.kingwaytek.cpami.biking.hardware.MyLocationManager;
import com.kingwaytek.cpami.biking.utilities.DialogHelper;
import com.kingwaytek.cpami.biking.utilities.MD5Util;
import com.kingwaytek.cpami.biking.utilities.MenuHelper;
import com.kingwaytek.cpami.biking.utilities.Utility;

import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 車友通報頁面
 *
 * @author Vincent (2016/7/6)
 */
public class UiReportActivity extends BaseActivity {

    private EditText edit_name;
    private Spinner spinner_type;
    private EditText edit_title;
    private TextView text_startTime;
    private TextView text_endTime;
    private Spinner spinner_location;
    private EditText edit_email;
    private EditText edit_description;

    private static final long LATER_TIME = 1000 * 60 * 10;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd  HH:mm", Locale.TAIWAN);
    private SimpleDateFormat sendingFormat = new SimpleDateFormat("yyyyMMddHHmm", Locale.TAIWAN);

    private long startTime;
    private long endTime;

    private static final String REPORT_RESULT_OK = "0";

    @Override
    protected void init() {
        initSpinners();
    }

    @Override
    public void onResume() {
        super.onResume();
        initTimeHint();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_report);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_SEND);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case ACTION_SEND:
                uploadReport();
                break;
        }

        return true;
    }

    @Override
    protected void findViews() {
        edit_name = (EditText) findViewById(R.id.edit_reporterName);
        spinner_type = (Spinner) findViewById(R.id.spinner_reportType);
        edit_title = (EditText) findViewById(R.id.edit_reportTitle);
        text_startTime = (TextView) findViewById(R.id.text_reportStartTime);
        text_endTime = (TextView) findViewById(R.id.text_reportEndTime);
        spinner_location = (Spinner) findViewById(R.id.spinner_reportLocation);
        edit_email = (EditText) findViewById(R.id.edit_reportEmail);
        edit_description = (EditText) findViewById(R.id.edit_reportDescription);
    }

    @Override
    protected void setListener() {
        text_startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.showPickersDialog(UiReportActivity.this, false, new DialogHelper.OnTimeSetCallBack() {
                    @Override
                    public void onTimeSet(String year, String month, String day, String hours, String minutes) {
                        String timeString = year + "/" + month + "/" + day + "  " + hours + ":" + minutes;

                        try {
                            Date startDate = dateFormat.parse(timeString);
                            startTime = startDate.getTime();

                            String formattedTime = dateFormat.format(startDate);
                            text_startTime.setText(formattedTime);

                            if (endTime > 0 && startTime > endTime) {
                                Utility.toastLong(getString(R.string.report_require_later_than_start));
                                text_endTime.setText("");
                            }
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        text_endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.showPickersDialog(UiReportActivity.this, true, new DialogHelper.OnTimeSetCallBack() {
                    @Override
                    public void onTimeSet(String year, String month, String day, String hours, String minutes) {
                        String timeString = year + "/" + month + "/" + day + "  " + hours + ":" + minutes;

                        try {
                            Date endDate = dateFormat.parse(timeString);
                            endTime = endDate.getTime();

                            if (endTime > startTime) {
                                String formattedTime = dateFormat.format(endDate);
                                text_endTime.setText(formattedTime);
                            }
                            else
                                Utility.toastLong(getString(R.string.report_require_later_than_start));
                        }
                        catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
    }

    private void initSpinners() {
        String[] typeArray = getResources().getStringArray(R.array.type_array);
        String[] locationArray = getResources().getStringArray(R.array.location_array);

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this, R.layout.listview_simple_layout_black, typeArray);
        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(this, R.layout.listview_simple_layout_black, locationArray);

        typeAdapter.setDropDownViewResource(R.layout.listview_simple_layout_with_right_arrow);
        locationAdapter.setDropDownViewResource(R.layout.listview_simple_layout_with_right_arrow);

        spinner_type.setAdapter(typeAdapter);
        spinner_location.setAdapter(locationAdapter);
    }

    private void initTimeHint() {
        String now = dateFormat.format(new Date());

        long laterLong = System.currentTimeMillis() + LATER_TIME;
        String later = dateFormat.format(new Date(laterLong));

        text_startTime.setHint(now);
        text_endTime.setHint(later);
    }

    private int getReportType() {
        return spinner_type.getSelectedItemPosition() + 1;
    }

    private int getReportLocation() {
        return spinner_location.getSelectedItemPosition() + 1;
    }

    private String getReporterName() {
        return edit_name.getText().toString();
    }

    private String getReportTitle() {
        return edit_title.getText().toString();
    }

    private String getStartTime() {
        String timeString;

        if (text_startTime.getText().toString().isEmpty())
            timeString = text_startTime.getHint().toString();
        else
            timeString = text_startTime.getText().toString();

        try {
            timeString = sendingFormat.format(dateFormat.parse(timeString));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return timeString;
    }

    private String getEndTime() {
        String timeString;

        if (text_endTime.getText().toString().isEmpty())
            timeString = text_endTime.getHint().toString();
        else
            timeString = text_endTime.getText().toString();

        try {
            timeString = sendingFormat.format(dateFormat.parse(timeString));
        }
        catch (ParseException e) {
            e.printStackTrace();
        }

        return timeString;
    }

    private String getDescription() {
        return edit_description.getText().toString();
    }

    private String getReporterEmail() {
        return edit_email.getText().toString();
    }

    private boolean isAllowUpload() {
        if (!getReporterName().isEmpty() && !getReportTitle().isEmpty() && !getDescription().isEmpty())
            return true;
        else {
            if (getReporterName().isEmpty())
                Utility.toastShort(getString(R.string.report_require_name));
            if (getReportTitle().isEmpty())
                Utility.toastShort(getString(R.string.report_require_title));
            if (getDescription().isEmpty())
                Utility.toastShort(getString(R.string.report_require_description));

            return false;
        }
    }

    private void uploadReport() {
        if (isAllowUpload()) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss", Locale.TAIWAN);
            String now = formatter.format(new Date());

            Location location = MyLocationManager.getLastLocation();
            String lng = "";
            String lat = "";

            if (notNull(location)) {
                lng = String.valueOf(location.getLongitude()).substring(0, 8);
                lat = String.valueOf(location.getLatitude()).substring(0, 8);
            }

            String reportUrl = MessageFormat.format(ApiUrls.API_REPORT, getReporterName(), getReportType(), getReportTitle(),
                    getStartTime(), getEndTime(), getReportLocation(), getDescription(), lng, lat, now, getReporterEmail(),
                    MD5Util.getMD5Code(MD5Util.SERVICE_NUMBER_REPORT));

            Log.i(TAG, "ReportUrl: " + reportUrl);

            WebAgent.sendPostToUrl(reportUrl, new WebAgent.WebResultImplement() {
                @Override
                public void onResultSucceed(String response) {
                    if (response.equals(REPORT_RESULT_OK)) {
                        Utility.toastShort(getString(R.string.report_result_ok));
                        finish();
                    }
                    else
                        Utility.toastShort(getString(R.string.report_result_failed));
                }

                @Override
                public void onResultFail(String errorMessage) {
                    Utility.toastShort(getString(R.string.report_result_failed) + " " + errorMessage);
                }
            });
        }
    }
}
