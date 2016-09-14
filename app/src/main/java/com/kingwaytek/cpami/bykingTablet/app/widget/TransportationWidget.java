package com.kingwaytek.cpami.bykingTablet.app.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;

/**
 * Created by vincent.chang on 2016/9/12.
 */
public class TransportationWidget extends LinearLayout {

    private ImageView vehicleIcon;
    private TextView metroText;
    private TextView moveTime;
    private ImageView rightArrow;

    public static final int METRO_COLOR_ORANGE = 1;
    public static final int METRO_COLOR_BLUE = 2;
    public static final int METRO_COLOR_GREEN = 3;
    public static final int METRO_COLOR_RED = 4;

    public static final int MOVE_TYPE_WALK = 5;
    public static final int MOVE_TYPE_BUS = 6;
    public static final int MOVE_TYPE_RAIL = 7;

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

    public void showMetroStep(int metroColor, String shortName, boolean hasNext) {
        moveTime.setVisibility(View.GONE);
        rightArrow.setVisibility(hasNext ? View.VISIBLE : View.GONE);

        vehicleIcon.setImageResource(R.drawable.ic_taipei_metro);

        switch (metroColor) {
            case METRO_COLOR_ORANGE:
                metroText.setBackgroundResource(R.drawable.background_metro_orange);
                break;

            case METRO_COLOR_BLUE:
                metroText.setBackgroundResource(R.drawable.background_metro_blue);
                break;

            case METRO_COLOR_GREEN:
                metroText.setBackgroundResource(R.drawable.background_metro_green);
                break;

            case METRO_COLOR_RED:
                metroText.setBackgroundResource(R.drawable.background_metro_red);
                break;
        }
        metroText.setText(shortName);
    }

    public void showMoveStep(int moveType, int estimateTime, boolean hasNext) {
        metroText.setVisibility(View.GONE);
        moveTime.setVisibility(View.VISIBLE);
        rightArrow.setVisibility(hasNext ? View.VISIBLE : View.GONE);

        switch (moveType) {
            case MOVE_TYPE_WALK:
                vehicleIcon.setImageResource(R.drawable.ic_directions_walk);
                break;

            case MOVE_TYPE_BUS:
                vehicleIcon.setImageResource(R.drawable.ic_directions_bus);
                break;

            case MOVE_TYPE_RAIL:
                vehicleIcon.setImageResource(R.drawable.ic_directions_transit);
                break;
        }
        moveTime.setText(String.valueOf(estimateTime));
    }
}
