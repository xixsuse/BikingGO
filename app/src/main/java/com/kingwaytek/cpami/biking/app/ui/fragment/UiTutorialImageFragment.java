package com.kingwaytek.cpami.biking.app.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.ui.BaseFragment;

/**
 * Created by vincent.chang on 2016/11/2.
 */

public class UiTutorialImageFragment extends BaseFragment {

    private ImageView tutorialImage;

    private static final int[] imageResArray = {
            R.drawable.ic_biking_logo,
            R.drawable.ic_button_add,
            R.drawable.ic_button_directions,
            R.drawable.ic_around_off,
            R.drawable.ic_button_return};

    public static UiTutorialImageFragment newInstance(int imageIndex) {
        UiTutorialImageFragment instance = new UiTutorialImageFragment();
        Bundle arg = new Bundle();
        arg.putInt(BUNDLE_FRAGMENT_DEFAULT_ARG, imageIndex);
        instance.setArguments(arg);
        return instance;
    }

    @Override
    protected void init() {
        setImages();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_tutorial_image;
    }

    @Override
    protected void findRootViews(View rootView) {
        tutorialImage = (ImageView) rootView.findViewById(R.id.tutorialImageView);
    }

    @Override
    protected void setListener() {

    }

    private void setImages() {
        int index = getArguments().getInt(BUNDLE_FRAGMENT_DEFAULT_ARG);

        tutorialImage.setImageResource(imageResArray[index]);
    }
}
