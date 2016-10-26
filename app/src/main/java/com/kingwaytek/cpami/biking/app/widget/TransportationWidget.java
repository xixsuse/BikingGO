package com.kingwaytek.cpami.biking.app.widget;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kingwaytek.cpami.biking.AppController;
import com.kingwaytek.cpami.biking.R;

/**
 * Created by vincent.chang on 2016/9/12.
 */
public class TransportationWidget extends LinearLayout {

    private ImageView vehicleIcon;
    private TextView metroText;
    private TextView moveTime;
    private ImageView rightArrow;

    public static final String VEHICLE_TYPE_METRO = "SUBWAY";
    public static final String VEHICLE_TYPE_BUS = "BUS";
    public static final String VEHICLE_TYPE_TRAIN = "HEAVY_RAIL";

    public static final String METRO_LINE_NAME_ORANGE = "中和新蘆線";
    public static final String METRO_LINE_NAME_BLUE = "板南線";
    public static final String METRO_LINE_NAME_GREEN = "松山新店線";
    public static final String METRO_LINE_NAME_RED = "淡水信義線";
    public static final String METRO_LINE_NAME_BROWN = "文湖線";
    public static final String KRT_LINE_NAME_RED = "紅線";
    public static final String KRT_LINE_NAME_ORANGE = "橘線";
    public static final String KRT_LINE_NAME_LIGHT_RAIL = "輕軌";

    public TransportationWidget(Context context) {
        super(context);
        init();
    }

    public TransportationWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TransportationWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        initView();
        findViews();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.inflate_transportation_widget_layout, null);
        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        this.addView(view);
    }

    private void findViews() {
        vehicleIcon = (ImageView) findViewById(R.id.vehicleIcon);
        metroText = (TextView) findViewById(R.id.text_metroLine);
        moveTime = (TextView) findViewById(R.id.text_moveTime);
        rightArrow = (ImageView) findViewById(R.id.transportationRightArrow);
    }

    public void showVehicleStep(String vehicleType, String shortName, String iconUrl, boolean hasNext) {
        moveTime.setVisibility(View.GONE);
        metroText.setVisibility(View.VISIBLE);
        rightArrow.setVisibility(hasNext ? View.VISIBLE : View.GONE);

        if (vehicleType.equals(VEHICLE_TYPE_METRO)) {
            switch (shortName) {
                case METRO_LINE_NAME_ORANGE:
                case KRT_LINE_NAME_ORANGE:
                    metroText.setBackgroundResource(R.drawable.background_metro_label_orange);
                    break;

                case METRO_LINE_NAME_BLUE:
                    metroText.setBackgroundResource(R.drawable.background_metro_label_blue);
                    break;

                case METRO_LINE_NAME_GREEN:
                case KRT_LINE_NAME_LIGHT_RAIL:
                    metroText.setBackgroundResource(R.drawable.background_metro_label_green);
                    break;

                case METRO_LINE_NAME_RED:
                case KRT_LINE_NAME_RED:
                    metroText.setBackgroundResource(R.drawable.background_metro_label_red);
                    break;

                case METRO_LINE_NAME_BROWN:
                    metroText.setBackgroundResource(R.drawable.background_metro_label_brown);
                    break;
            }
            vehicleIcon.setImageResource(R.drawable.ic_taipei_metro);
            metroText.setTextColor(ContextCompat.getColor(AppController.getInstance().getAppContext(), R.color.md_grey_50));
        }
        else {
            if (!iconUrl.isEmpty())
                Glide.with(getContext()).load(iconUrl).into(vehicleIcon);

            metroText.setBackgroundResource(R.drawable.background_common_vehicle_short_name);
            metroText.setTextColor(ContextCompat.getColor(AppController.getInstance().getAppContext(), R.color.md_black_1000));
        }

        metroText.setText(shortName);
    }

    public void showWalkStep(int estimateTime, boolean hasNext) {
        metroText.setVisibility(View.GONE);
        moveTime.setVisibility(View.VISIBLE);
        rightArrow.setVisibility(hasNext ? View.VISIBLE : View.GONE);

        vehicleIcon.setImageResource(R.drawable.ic_directions_walk);
        moveTime.setText(String.valueOf(estimateTime));
    }
}
