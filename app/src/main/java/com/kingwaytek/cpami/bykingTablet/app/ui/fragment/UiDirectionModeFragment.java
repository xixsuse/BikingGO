package com.kingwaytek.cpami.bykingTablet.app.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.ApiUrls;
import com.kingwaytek.cpami.bykingTablet.app.model.CommonBundle;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPathStep;
import com.kingwaytek.cpami.bykingTablet.app.ui.UiMainMapActivity;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PathStepsAdapter;

/**
 * Created by vincent.chang on 2016/8/15.
 */
public class UiDirectionModeFragment extends Fragment implements CommonBundle, ApiUrls {

    private static final String TAG = "UiDirectionModeFragment";

    public static final int MODE_WALK = 1;
    public static final int MODE_TRANSIT = 2;

    private int directionMode;
    private String[] fromTo;

    private ListView pathListView;
    private ProgressBar loadingCircle;


    private static class SingleInstance {
        private static final UiDirectionModeFragment INSTANCE_WALK = new UiDirectionModeFragment();
        private static final UiDirectionModeFragment INSTANCE_TRANSIT = new UiDirectionModeFragment();
    }

    public static UiDirectionModeFragment getInstance(int directionMode, String[] fromTo) {
        UiDirectionModeFragment instance;

        if (directionMode == MODE_WALK)
            instance = SingleInstance.INSTANCE_WALK;
        else
            instance = SingleInstance.INSTANCE_TRANSIT;

        Bundle arg = new Bundle();
        arg.putInt(BUNDLE_DIRECTION_MODE, directionMode);
        arg.putStringArray(BUNDLE_FRAGMENT_DEFAULT_ARG, fromTo);
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreateView!!!!");
        View rootView = inflater.inflate(R.layout.fragment_direction_mode, container, false);

        getDefaultValue();
        findRootViews(rootView);
        setListener();

        setListByMode();

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void getDefaultValue() {
        directionMode = getArguments().getInt(BUNDLE_DIRECTION_MODE);
        fromTo = getArguments().getStringArray(BUNDLE_FRAGMENT_DEFAULT_ARG);
    }

    private void findRootViews(View rootView) {
        pathListView = (ListView) rootView.findViewById(R.id.pathListView);
        loadingCircle = (ProgressBar) rootView.findViewById(R.id.pathLoadingCircle);
    }

    private void setListener() {
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

    private void setListByMode() {
        switch (directionMode) {
            case MODE_WALK:
                getPathStepsInfo();
                break;

            case MODE_TRANSIT:

                break;
        }
    }

    private void getPathStepsInfo() {
        showLoadingCircle(true);

        String avoidOption = getAvoidOptions(DIR_AVOID_TOLLS, DIR_AVOID_HIGHWAYS);

        WebAgent.getDirectionsData(fromTo[0], fromTo[1], DIR_MODE_WALKING, avoidOption, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                pathListView.setAdapter(new PathStepsAdapter(getContext(), JsonParser.parseAnGetDirectionItems(response), false));
                showLoadingCircle(false);
            }

            @Override
            public void onResultFail(String errorMessage) {
                Log.e(TAG, errorMessage);
                showLoadingCircle(false);
            }
        });
    }

    private String getAvoidOptions(String... avoidOptions) {
        StringBuilder sb = new StringBuilder();

        sb.append("&avoid=");

        for (int i = 0; i < avoidOptions.length; i++) {
            if (i != 0)
                sb.append("|");
            sb.append(avoidOptions[i]);
        }
        return sb.toString();
    }

    public void updateData(String origin, String destination) {
        if (!fromTo[0].equals(origin) || !fromTo[1].equals(destination)) {
            this.fromTo = new String[]{origin, destination};
            setListByMode();
        }
    }

    private void showLoadingCircle(boolean isShow) {
        loadingCircle.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
