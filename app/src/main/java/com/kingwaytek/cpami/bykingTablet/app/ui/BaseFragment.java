package com.kingwaytek.cpami.bykingTablet.app.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kingwaytek.cpami.bykingTablet.app.model.ApiUrls;
import com.kingwaytek.cpami.bykingTablet.app.model.CommonBundle;

/**
 * The Base for fragments.
 *
 * @author Vincent (2016/9/7)
 */
public abstract class BaseFragment extends Fragment implements CommonBundle, ApiUrls {

    protected String TAG = getClass().getSimpleName();

    protected abstract void init();
    protected abstract int getLayoutId();
    protected abstract void findRootViews(View rootView);
    protected abstract void setListener();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreateView!!!!");
        View rootView = inflater.inflate(getLayoutId(), container, false);

        findRootViews(rootView);
        setListener();

        init();

        return rootView;
    }
}
