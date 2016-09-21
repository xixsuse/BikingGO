package com.kingwaytek.cpami.bykingTablet.app.ui.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPathStep;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTransitOverview;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsTransitStep;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseFragment;
import com.kingwaytek.cpami.bykingTablet.app.ui.UiMainMapActivity;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.app.widget.TransitOverviewLayout;
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
        if (directionMode == MODE_TRANSIT)
            overviewLayout = (TransitOverviewLayout) rootView.findViewById(R.id.transitOverviewLayoutWidget);
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

        WebAgent.getDirectionsData(fromTo[0], fromTo[1], DIR_MODE_TRANSIT, null, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                setTransitOverviewAndList(response);
                loadingCircle.setVisibility(View.GONE);
            }

            @Override
            public void onResultFail(String errorMessage) {
                Utility.toastShort(getString(R.string.network_connection_error_please_retry));
            }
        });
    }

    private void setTransitOverviewAndList(String jsonString) {
        ItemsTransitOverview transitOverview = JsonParser.parseAndGetTransitData(jsonString);

        if (transitOverview != null) {
            Log.i(TAG, "setTransitOverviewAndList!");

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

            if (transitStepAdapter == null) {
                transitStepAdapter = new TransitStepAdapter(getContext(), transitOverview.TRANSIT_STEP_ITEM);
                pathListView.setAdapter(transitStepAdapter);
            }
            else
                transitStepAdapter.refreshData(transitOverview.TRANSIT_STEP_ITEM);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
