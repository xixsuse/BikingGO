package com.kingwaytek.cpami.biking.app.ui.poi_book;

import android.content.Intent;
import android.location.Location;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.items.ItemsPoiDetail;
import com.kingwaytek.cpami.biking.app.ui.BaseActivity;
import com.kingwaytek.cpami.biking.app.ui.UiMainMapActivity;
import com.kingwaytek.cpami.biking.app.web.WebAgent;
import com.kingwaytek.cpami.biking.hardware.MyLocationManager;
import com.kingwaytek.cpami.biking.utilities.DialogHelper;
import com.kingwaytek.cpami.biking.utilities.JsonParser;
import com.kingwaytek.cpami.biking.utilities.Utility;
import com.kingwaytek.cpami.biking.utilities.adapter.PoiPhotoPagerAdapter;

/**
 * 景點書：景點詳細資訊
 *
 * @author Vincent (2016/9/6)
 */
public class UiPoiDetailActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    private ViewPager photoPager;
    private LinearLayout dotsLayout;
    private TextView text_title;
    private TextView text_address;
    private TextView text_description;
    private FloatingActionButton floatingBtn_direction;

    private ItemsPoiDetail poiItem;

    private int pageSize;
    private ImageView pageDots[];

    @Override
    protected void init() {
        getBundleAndParseData();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_poi_book);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_poi_detail;
    }

    @Override
    protected void findViews() {
        photoPager = (ViewPager) findViewById(R.id.poiPhotoPager);
        dotsLayout = (LinearLayout) findViewById(R.id.pageDotLayout);
        text_title = (TextView) findViewById(R.id.text_poiTitle);
        text_address = (TextView) findViewById(R.id.text_poiAddress);
        text_description = (TextView) findViewById(R.id.text_poiDescription);
        floatingBtn_direction = (FloatingActionButton) findViewById(R.id.floatingBtn_poiDirection);
    }

    @Override
    protected void setListener() {
        floatingBtn_direction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDirectionAndBackToMainMap();
            }
        });
    }

    private void getBundleAndParseData() {
        poiItem = JsonParser.parseAndGetPoiDetail(getIntent().getStringExtra(BUNDLE_POI_DETAIL));
        setPoiInfo();
        setPhotoPager();
    }

    private void setPoiInfo() {
        text_title.setText(poiItem.NAME);
        text_address.setText(poiItem.ADDRESS);
        text_description.setText(poiItem.DESCRIPTION);
    }

    private void setPhotoPager() {
        int height = (Utility.getScreenWidth() / 3) * 2; // 寬高比 3:2
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, height);
        photoPager.setLayoutParams(params);

        photoPager.setAdapter(new PoiPhotoPagerAdapter(getSupportFragmentManager(), poiItem.PHOTO_PATH));
        addPageDots();
        photoPager.addOnPageChangeListener(this);
    }

    private void addPageDots() {
        pageSize = poiItem.PHOTO_PATH.size();

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
            setPageDotState(0);
        }
    }

    private void setPageDotState(int position) {
        for (int i = 0; i < pageSize; i++) {
            if (i == position)
                pageDots[i].setImageResource(R.drawable.ic_page_dot_white_on);
            else
                pageDots[i].setImageResource(R.drawable.ic_page_dot_white_off);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        if (pageSize > 1)
            setPageDotState(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}


    private void startDirectionAndBackToMainMap() {
        Location location = MyLocationManager.getLastLocation();
        if (location == null) {
            Utility.toastShort(getString(R.string.gps_unable_to_get_location));
            return;
        }

        DialogHelper.showLoadingDialog(this);

        final String origin = location.getLatitude() + "," + location.getLongitude();
        final String destination = poiItem.LAT + "," + poiItem.LNG;

        String avoidOption = getAvoidOptions(DIR_AVOID_TOLLS, DIR_AVOID_HIGHWAYS);

        WebAgent.getDirectionsData(origin, destination, DIR_MODE_WALKING, avoidOption, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                Intent intent = new Intent(UiPoiDetailActivity.this, UiMainMapActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                intent.putExtra(BUNDLE_DIRECTION_FROM_POI_BOOK, new String[]{response, origin + "&" + destination});
                intent.putExtra(BUNDLE_PUT_MARKER_TITLE, poiItem.NAME);
                intent.putExtra(BUNDLE_PUT_MARKER_SNIPPET, poiItem.ADDRESS);
                intent.putExtra(BUNDLE_PUT_MARKER_COORDINATES, new double[]{poiItem.LAT, poiItem.LNG});

                DialogHelper.dismissDialog();

                startActivity(intent);
            }

            @Override
            public void onResultFail(String errorMessage) {
                DialogHelper.dismissDialog();
                Utility.toastShort(errorMessage);
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        photoPager.removeOnPageChangeListener(this);
    }
}
