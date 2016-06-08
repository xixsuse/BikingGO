package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.CommonBundle;

/**
 * Created by vincent.chang on 2016/6/8.
 */
public class UiPathInfoFragment extends Fragment implements CommonBundle {

    private TextView startAndEndName;
    private TextView distance;
    private TextView duration;

    public static UiPathInfoFragment newInstance(String startName, String endName, String distance, String duration) {
        UiPathInfoFragment instance = new UiPathInfoFragment();

        Bundle arg = new Bundle();
        arg.putString(BUNDLE_PLAN_STEP_START_NAME, startName);
        arg.putString(BUNDLE_PLAN_STEP_END_NAME, endName);
        arg.putString(BUNDLE_PLAN_STEP_DISTANCE, distance);
        arg.putString(BUNDLE_PLAN_STEP_DURATION, duration);

        instance.setArguments(arg);
        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.view_path_info, container, false);
        findViews(view);

        return view;
    }

    private void findViews(View view) {
        startAndEndName = (TextView) view.findViewById(R.id.text_startAndEndName);
        distance = (TextView) view.findViewById(R.id.text_pathInfoDistance);
        duration = (TextView) view.findViewById(R.id.text_pathInfoDuration);
    }

    @Override
    public void onResume() {
        super.onResume();
        setInformation();
    }

    private void setInformation() {
        String startName = getArguments().getString(BUNDLE_PLAN_STEP_START_NAME);
        String endName = getArguments().getString(BUNDLE_PLAN_STEP_END_NAME);
        String distanceString = getArguments().getString(BUNDLE_PLAN_STEP_DISTANCE);
        String durationString = getArguments().getString(BUNDLE_PLAN_STEP_DURATION);

        startAndEndName.setText(getString(R.string.plan_start_name_and_end_name, startName, endName));
        distance.setText(distanceString);
        duration.setText(durationString);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
