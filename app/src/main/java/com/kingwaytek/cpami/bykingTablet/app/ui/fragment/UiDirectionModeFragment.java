package com.kingwaytek.cpami.bykingTablet.app.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPathStep;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTransitOverview;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTransitStep;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseFragment;
import com.kingwaytek.cpami.bykingTablet.app.ui.UiMainMapActivity;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.app.widget.TransitOverviewLayout;
import com.kingwaytek.cpami.bykingTablet.app.widget.TransportationWidget;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PathStepsAdapter;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.TransitStepAdapter;

/**
 * Created by vincent.chang on 2016/8/15.
 */
public class UiDirectionModeFragment extends BaseFragment {

    public static final int MODE_WALK = 1;
    public static final int MODE_TRANSIT = 2;

    private static final String TRAVEL_MODE_WALK = "WALKING";
    private static final String TRAVEL_MODE_TRANSIT = "TRANSIT";

    private int directionMode;
    private String jsonString;

    private TransitOverviewLayout overviewLayout;
    private ListView pathListView;
    private ProgressBar loadingCircle;
    private Button transitConnectionRetryBtn;
    private TextView text_noSuggestion;

    private TransitStepAdapter transitStepAdapter;

    private static class SingleInstance {
        private static final UiDirectionModeFragment INSTANCE_WALK = new UiDirectionModeFragment();
        private static final UiDirectionModeFragment INSTANCE_TRANSIT = new UiDirectionModeFragment();
    }

    public static UiDirectionModeFragment getInstance(int directionMode, String jsonString) {
        UiDirectionModeFragment instance;

        Bundle arg = new Bundle();

        if (directionMode == MODE_WALK)
            instance = SingleInstance.INSTANCE_WALK;
        else
            instance = SingleInstance.INSTANCE_TRANSIT;

        arg.putInt(BUNDLE_DIRECTION_MODE, directionMode);
        arg.putString(BUNDLE_FRAGMENT_DEFAULT_ARG, jsonString);
        instance.setArguments(arg);

        return instance;
    }

    public static UiDirectionModeFragment getInstance(int directionMode) {
        return directionMode == MODE_WALK ? SingleInstance.INSTANCE_WALK : SingleInstance.INSTANCE_TRANSIT;
    }

    public static UiDirectionModeFragment newInstance(int directionMode, String[] fromTo) {
        UiDirectionModeFragment instance = new UiDirectionModeFragment();

        Bundle arg = new Bundle();
        arg.putInt(BUNDLE_DIRECTION_MODE, directionMode);
        arg.putStringArray(BUNDLE_FRAGMENT_DEFAULT_ARG, fromTo);
        instance.setArguments(arg);

        return instance;
    }

    @Override
    protected void init() {
        setListByMode();
    }

    @Override
    protected int getLayoutId() {
        getDefaultValue();
        return directionMode == MODE_WALK ? R.layout.fragment_direction_mode_walk : R.layout.fragment_direction_mode_transit;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void findRootViews(View rootView) {
        if (directionMode == MODE_TRANSIT) {
            overviewLayout = (TransitOverviewLayout) rootView.findViewById(R.id.transitOverviewLayoutWidget);
            transitConnectionRetryBtn = (Button) rootView.findViewById(R.id.button_transitConnectRetry);
            text_noSuggestion = (TextView) rootView.findViewById(R.id.text_noTransitSuggestion);
        }
        pathListView = (ListView) rootView.findViewById(R.id.pathListView);
        loadingCircle = (ProgressBar) rootView.findViewById(R.id.pathLoadingCircle);
    }

    @Override
    protected void setListener() {
        pathListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (directionMode) {
                    case MODE_WALK:
                        ((UiMainMapActivity) getContext()).onPathStepClick((ItemsPathStep) parent.getItemAtPosition(position));
                        break;

                    case MODE_TRANSIT:
                        ItemsTransitStep transitStep = (ItemsTransitStep) parent.getItemAtPosition(position);

                        if (transitStep != null)
                            getColorResAndDrawHighlight(transitStep);

                        break;
                }
            }
        });
    }

    private void getDefaultValue() {
        directionMode = getArguments().getInt(BUNDLE_DIRECTION_MODE);
        jsonString = getArguments().getString(BUNDLE_FRAGMENT_DEFAULT_ARG);
    }

    private void setListByMode() {
        switch (directionMode) {
            case MODE_WALK:
                getPathStepsInfo();
                break;

            case MODE_TRANSIT:
                getTransitData();
                break;
        }
    }

    private void getPathStepsInfo() {
        pathListView.setAdapter(new PathStepsAdapter(getContext(), JsonParser.parseAnGetDirectionItems(jsonString), false));
        showLoadingCircle(false);
    }

    public void updateData(String updateString) {
        this.jsonString = updateString;
        Log.i(TAG, "DataUpdated!! DirectionMode: " + directionMode + " \nUpdateString: " + updateString);
        setListByMode();
    }

    private void showLoadingCircle(boolean isShow) {
        loadingCircle.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    private void getTransitData() {
        String[] fromTo = jsonString.split("&");
        Log.i(TAG, "FromTo: " + jsonString);

        showLoadingCircle(true);
        transitConnectionRetryBtn.setVisibility(View.GONE);

        WebAgent.getDirectionsData(fromTo[0], fromTo[1], DIR_MODE_TRANSIT, null, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                setTransitOverviewAndList(response);
                showLoadingCircle(false);
                transitConnectionRetryBtn.setOnClickListener(null);
            }

            @Override
            public void onResultFail(String errorMessage) {
                Utility.toastShort(getString(R.string.network_connection_error_please_retry));
                showLoadingCircle(false);

                transitConnectionRetryBtn.setVisibility(View.VISIBLE);
                transitConnectionRetryBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getTransitData();
                    }
                });
            }
        });
    }

    private void setTransitOverviewAndList(String jsonString) {
        ItemsTransitOverview transitOverview = JsonParser.parseAndGetTransitData(jsonString);

        if (transitOverview != null) {
            Log.i(TAG, "setTransitOverviewAndList!");
            text_noSuggestion.setVisibility(View.GONE);

            overviewLayout.removeTransportationWidgets();
            overviewLayout.setTotalTime(transitOverview.DURATION);

            int len = transitOverview.TRANSIT_STEP_ITEM.size();
            boolean hasNext;

            for (int i = 0; i < len; i++) {
                hasNext = i < (len - 1);

                ItemsTransitStep transitStep = transitOverview.TRANSIT_STEP_ITEM.get(i);

                if (transitStep.TRAVEL_MODE.equals(TRAVEL_MODE_WALK))
                    overviewLayout.addWalkStep(transitStep.DURATION, hasNext);
                else
                    overviewLayout.addVehicleStep(transitStep.TRANSIT_VEHICLE_TYPE, transitStep.TRANSIT_SHORT_NAME,
                            transitStep.TRANSIT_VEHICLE_ICON_URL, hasNext);
            }

            if (transitStepAdapter == null || transitStepAdapter.isContextDestroyed()) {
                transitStepAdapter = new TransitStepAdapter(getContext(), transitOverview.TRANSIT_STEP_ITEM, transitOverview.FARE);
                pathListView.setAdapter(transitStepAdapter);
            }
            else
                transitStepAdapter.refreshData(transitOverview.TRANSIT_STEP_ITEM, transitOverview.FARE);

            ((UiMainMapActivity) getContext()).setPolylineOverviewAndDraw(transitOverview.POLY_LINE, false);
        }
        else
            text_noSuggestion.setVisibility(View.VISIBLE);
    }

    private void getColorResAndDrawHighlight(ItemsTransitStep transitStep) {
        int colorRes = 0;

        if (transitStep.TRAVEL_MODE.equals(TRAVEL_MODE_TRANSIT))
        {
            if (transitStep.TRANSIT_VEHICLE_TYPE.equals(TransportationWidget.VEHICLE_TYPE_METRO)) {
                switch (transitStep.TRANSIT_SHORT_NAME) {
                    case TransportationWidget.METRO_LINE_NAME_BLUE:
                        colorRes = R.color.metro_blue;
                        break;

                    case TransportationWidget.METRO_LINE_NAME_BROWN:
                        colorRes = R.color.metro_brown;
                        break;

                    case TransportationWidget.METRO_LINE_NAME_GREEN:
                    case TransportationWidget.KRT_LINE_NAME_LIGHT_RAIL:
                        colorRes = R.color.metro_green;
                        break;

                    case TransportationWidget.METRO_LINE_NAME_ORANGE:
                    case TransportationWidget.KRT_LINE_NAME_ORANGE:
                        colorRes = R.color.metro_orange;
                        break;

                    case TransportationWidget.METRO_LINE_NAME_RED:
                    case TransportationWidget.KRT_LINE_NAME_RED:
                        colorRes = R.color.metro_red;
                        break;
                }
            }
            else
                colorRes = R.color.md_teal_A700;
        }

        ((UiMainMapActivity)getContext()).onTransitStepClick(transitStep.START_LAT_LNG, transitStep.POLY_LINE, colorRes);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
