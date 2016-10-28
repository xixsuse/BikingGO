package com.kingwaytek.cpami.biking.app.ui;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.kingwaytek.cpami.biking.R;

/**
 * Created by vincent.chang on 2016/10/26.
 */

public class UiTutorialActivity extends FragmentActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;

    private int pageSize;
    private ImageView pageDots[];


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setWindowFeatures();
        setContentView(R.layout.activity_tutorial);
        findViews();
        init();
    }

    private void setWindowFeatures() {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void findViews() {
        viewPager = (ViewPager) findViewById(R.id.tutorialPager);
        dotsLayout = (LinearLayout) findViewById(R.id.pageDotLayout);
    }

    private void init() {
        addPageDots();
    }

    private void addPageDots() {
        pageSize = 5;

        if (pageSize > 1) {
            pageDots = new ImageView[pageSize];

            int width = getResources().getDimensionPixelSize(R.dimen.padding_size_l);
            int marginWidth = getResources().getDimensionPixelSize(R.dimen.padding_size_m);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);

            for (int i = 0; i < pageSize; i++) {
                pageDots[i] = new ImageView(this);
                if (i != 0)
                    params.setMargins(marginWidth, 0, 0, 0);

                pageDots[i].setLayoutParams(params);
                dotsLayout.addView(pageDots[i]);
            }
            //setPageDotState(0);
        }
    }
}
