package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.DataArray;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPathList;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPathStep;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlanItem;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsPlans;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseGoogleApiActivity;
import com.kingwaytek.cpami.bykingTablet.app.web.WebAgent;
import com.kingwaytek.cpami.bykingTablet.utilities.BitmapUtility;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PolyHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PathListPagerAdapter;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PathStepsAdapter;

import java.util.ArrayList;

/**
 * <p>
 * For Drawing multi points direction Polyline,<br>
 * And show path listView, which contains ViewPager and interactive functions with map.
 * </p>
 *
 * (UiMainMapActivity開始有點肥大了，所以把這部分獨立出來)
 *
 * @author Vincent (2016/6/16)
 */
public class UiPlanDirectionMapActivity extends BaseGoogleApiActivity implements ViewPager.OnPageChangeListener {

    private ViewPager pathListPager;
    private PathListPagerAdapter pagerAdapter;
    private ListView pathListView;

    private ArrayList<Polyline> highLightPolyList;

    private int pageSize;
    private ImageView pageDots[];
    private int lastSelectedPage = 0;

    private boolean moveCameraWhilePageSelected;

    private LinearLayout planTitleLayout;
    private TextView text_planTitle;

    private boolean entryTypeIsDirection;

    private int planIndex;
    private ItemsPlans planItem;

    @Override
    protected void onApiReady() {
        getBundleAndDraw();
    }

    @Override
    protected void findViews() {
        super.findViews();
        planTitleLayout = (LinearLayout) findViewById(R.id.planTitleLayout);
        text_planTitle = (TextView) findViewById(R.id.text_planTitle);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (notNull(pathListPager))
            pathListPager.removeOnPageChangeListener(this);

        planItem = null;
    }

    @Override
    protected void onLocateMyPosition(Location location) {}

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_planning);
    }

    @Override
    protected void setListener() {}

    @Override
    public void onInfoWindowClick(Marker marker) {}

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        switch (ENTRY_TYPE) {
            case ENTRY_TYPE_VIEW_SHARED_PLAN:
                MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_LIST, ACTION_LIKE);
                break;

            default:
                super.onCreateOptionsMenu(menu);
                break;
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case ACTION_LIST:
                showPathListView();
                break;

            case ACTION_LIKE:
                showRatingWindow();
                break;
        }

        return true;
    }

    private void getBundleAndDraw() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        entryTypeIsDirection = ENTRY_TYPE == ENTRY_TYPE_DIRECTIONS;

        String jsonString = bundle.getString(BUNDLE_PLAN_DIRECTION_JSON);

        if (entryTypeIsDirection)
            planIndex = bundle.getInt(BUNDLE_PLAN_EDIT_INDEX);
        else if (getIntent().hasExtra(BUNDLE_SHARED_ITEM))
            planItem = JsonParser.parseAndGetSharedPlan(getIntent().getStringExtra(BUNDLE_SHARED_ITEM));

        drawMultiPointsLine(jsonString);
        showPlanTitleLayout();
    }

    private void drawMultiPointsLine(String jsonString) {
        String polyOverview = JsonParser.getPolyLineOverview(jsonString);

        if (notNull(polyOverview)) {
            ArrayList<LatLng> linePoints = PolyHelper.decodePolyLine(polyOverview);

            ArrayList<ItemsPlanItem> planItems = new ArrayList<>();

            if (!entryTypeIsDirection && getIntent().hasExtra(BUNDLE_SHARED_ITEM)) {
                if (notNull(planItem))
                    planItems = planItem.PLAN_ITEMS;
            }
            else
                planItems = DataArray.getPlansData().get(planIndex).PLAN_ITEMS;

            PolylineOptions polyOptions = new PolylineOptions();

            for (LatLng latLng : linePoints) {
                polyOptions.add(latLng);
            }
            polyOptions.color(ContextCompat.getColor(AppController.getInstance().getAppContext(), R.color.md_light_blue_300));
            polyOptions.width(15);

            checkBitmapCache(R.drawable.ic_pin_place);
            MarkerOptions marker = new MarkerOptions();

            marker.position(linePoints.get(0));
            marker.title("1." + planItems.get(0).TITLE);
            marker.icon(BitmapDescriptorFactory.fromBitmap(getBitmapFromMemCache(BITMAP_KEY_PIN_PLACE)));

            map.addMarker(marker);

            marker.position(linePoints.get(linePoints.size() - 1));
            marker.title(planItems.size() + "." + planItems.get(planItems.size() - 1).TITLE);

            map.addMarker(marker);

            if (planItems.size() > 2) {
                checkBitmapCache(R.drawable.ic_pin_point);
                BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(getBitmapFromMemCache(BITMAP_KEY_PIN_POINT));

                MarkerOptions waypointsMarker = new MarkerOptions();
                waypointsMarker.icon(markerIcon);
                waypointsMarker.anchor(0.5f, 0.5f);

                for (int i = 0; i < planItems.size(); i ++) {
                    if (i != 0 && i != planItems.size() -1) {
                        waypointsMarker.position(new LatLng(planItems.get(i).LAT, planItems.get(i).LNG));
                        waypointsMarker.title((i + 1) + "." + planItems.get(i).TITLE);
                        map.addMarker(waypointsMarker);
                    }
                }
            }

            map.addPolyline(polyOptions);

            PopWindowHelper.dismissPopWindow();
            moveCameraAndZoom(linePoints.get(0), 16);
        }
    }

    private void checkBitmapCache(int iconRes) {
        switch (iconRes) {
            case R.drawable.ic_pin_place:
                if (getBitmapFromMemCache(BITMAP_KEY_PIN_PLACE) == null) {
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pin_place);
                    Bitmap bitmap = BitmapUtility.convertDrawableToBitmap(drawable, getResources().getDimensionPixelSize(R.dimen.icon_common_size));
                    addBitmapToMemoryCache(BITMAP_KEY_PIN_PLACE, bitmap);
                }
                break;

            case R.drawable.ic_pin_point:
                if (getBitmapFromMemCache(BITMAP_KEY_PIN_POINT) == null) {
                    Drawable drawable = ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_pin_point);
                    Bitmap bitmap = BitmapUtility.convertDrawableToBitmap(drawable, getResources().getDimensionPixelSize(R.dimen.font_text_size_l));
                    addBitmapToMemoryCache(BITMAP_KEY_PIN_POINT, bitmap);
                }
                break;
        }
    }

    private void showPlanTitleLayout() {
        planTitleLayout.setVisibility(View.VISIBLE);

        String planName = "";
        if (entryTypeIsDirection) {
            Log.i(TAG, "planIndex: " + planIndex);
            planName = DataArray.getPlansData().get(planIndex).NAME;
        }
        else if (notNull(planItem))
            planName = planItem.NAME;

        text_planTitle.setText(planName);
    }

    private void showPathListView() {
        if (PopWindowHelper.isPopWindowShowing()) {
            PopWindowHelper.dismissPopWindow();

            if (notNull(pathListPager))
                pathListPager.removeOnPageChangeListener(this);
        }
        else {
            final View view = PopWindowHelper.getPathListPopWindowView(mapRootLayout, this);
            Bundle bundle = getIntent().getExtras();

            if (notNull(view) && notNull(bundle)) {
                String jsonString = bundle.getString(BUNDLE_PLAN_DIRECTION_JSON);
                ArrayList<String[]> namePairList = getNamePairs();

                DataArray.getDirectionPathListData(jsonString, namePairList, new DataArray.OnDataGetCallBack() {
                    @Override
                    public void onDataGet() {
                        setPathListData(view);
                    }
                });
            }
        }
    }

    private ArrayList<String[]> getNamePairs() {
        ArrayList<ItemsPlanItem> planItems = new ArrayList<>();

        if (entryTypeIsDirection)
            planItems = DataArray.getPlansData().get(planIndex).PLAN_ITEMS;
        else if (notNull(planItem))
            planItems = planItem.PLAN_ITEMS;

        int size = planItems.size();

        ArrayList<String[]> namePairList = new ArrayList<>(size - 1);

        for (int i = 0; i < size; i++) {
            if (i + 1 == size)
                break;
            namePairList.add(new String[] {planItems.get(i).TITLE, planItems.get(i + 1).TITLE});
        }
        return namePairList;
    }

    private void setPathListData(View view) {
        ProgressBar loadingCircle = (ProgressBar) view.findViewById(R.id.pathListLoadingCircle);
        loadingCircle.setVisibility(View.GONE);

        pathListPager = (ViewPager) view.findViewById(R.id.pathListPager);
        pathListView = (ListView) view.findViewById(R.id.pathListView);

        if (pagerAdapter == null)
            pagerAdapter = new PathListPagerAdapter(getViewListAndSetContent());
        else
            pagerAdapter.refreshList(getViewListAndSetContent());

        addPageDotsToLayout(view);

        pathListPager.setOffscreenPageLimit(3);
        pathListPager.setAdapter(pagerAdapter);

        pathListPager.addOnPageChangeListener(this);

        moveCameraWhilePageSelected = false;
        onPageSelected(lastSelectedPage);
    }

    private void addPageDotsToLayout(View view) {
        LinearLayout dotLayout = (LinearLayout) view.findViewById(R.id.pageDotLayout);

        pageSize = pagerAdapter.getCount();
        pageDots = new ImageView[pageSize];

        int width = getResources().getDimensionPixelSize(R.dimen.padding_size_m);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.MATCH_PARENT);

        for (int i = 0; i < pageSize; i++) {
            pageDots[i] = new ImageView(this);
            if (i != 0)
                params.setMargins(width, 0, 0, 0);

            pageDots[i].setLayoutParams(params);
            dotLayout.addView(pageDots[i]);
        }
    }

    private void setPageDotState(int position) {
        for (int i = 0; i < pageSize; i++) {
            if (i == position)
                pageDots[i].setImageResource(R.drawable.ic_page_dot_on);
            else
                pageDots[i].setImageResource(R.drawable.ic_page_dot_off);
        }
    }

    private ArrayList<View> getViewListAndSetContent() {
        LayoutInflater inflater = LayoutInflater.from(this);
        ArrayList<View> viewList = new ArrayList<>();

        View view;
        for (ItemsPathList pathListItem : DataArray.list_pathList.get()) {
            view = inflater.inflate(R.layout.view_path_info, null);

            TextView startAndEndName = (TextView) view.findViewById(R.id.text_startAndEndName);
            TextView distance = (TextView) view.findViewById(R.id.text_pathInfoDistance);
            TextView duration = (TextView) view.findViewById(R.id.text_pathInfoDuration);

            String startName = pathListItem.START_NAME;
            String endName = pathListItem.END_NAME;
            String distanceString = pathListItem.DISTANCE;
            String durationString = pathListItem.DURATION;

            startAndEndName.setText(getString(R.string.plan_start_name_and_end_name, startName, endName));
            distance.setText(getString(R.string.plan_distance_is, distanceString));
            duration.setText(getString(R.string.plan_duration_is, durationString));

            viewList.add(view);
        }
        return viewList;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        moveCameraWhilePageSelected = true;
    }

    @Override
    public void onPageSelected(int position) {
        pathListPager.setCurrentItem(position);
        checkPathListData(position);
        setPageDotState(position);
        lastSelectedPage = position;
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    private void checkPathListData(final int position) {
        if (DataArray.list_pathList == null || DataArray.list_pathList.get() == null || DataArray.list_pathList.get().isEmpty()) {
            Bundle bundle = getIntent().getExtras();
            String jsonString = bundle.getString(BUNDLE_PLAN_DIRECTION_JSON);
            ArrayList<String[]> namePairList = getNamePairs();

            DataArray.getDirectionPathListData(jsonString, namePairList, new DataArray.OnDataGetCallBack() {
                @Override
                public void onDataGet() {
                    setPathListAndMoveCamera(position);
                }
            });
        }
        else
            setPathListAndMoveCamera(position);
    }

    private void setPathListAndMoveCamera(int position) {
        ItemsPathList pathList = DataArray.list_pathList.get().get(position);

        if (moveCameraWhilePageSelected)
            moveCamera(new LatLng(pathList.START_LAT, pathList.START_LNG));

        drawStepsHighLight(position);

        pathListView.setAdapter(new PathStepsAdapter(this, pathList.PATH_STEPS, true));

        pathListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ItemsPathStep pathStep = (ItemsPathStep) parent.getItemAtPosition(position);
                moveCameraAndZoom(new LatLng(pathStep.START_LAT, pathStep.START_LNG), 17);

                removeHighLightPolyline();
                drawHighLight(pathStep.POLY_LINE);

                PopWindowHelper.showPathStepPopWindow(mapRootLayout, pathStep.INSTRUCTIONS, pathStep.DISTANCE, pathStep.GO_ON_PATH);
            }
        });
    }

    private void drawStepsHighLight(int position) {
        removeHighLightPolyline();

        for (ItemsPathStep pathStep : DataArray.list_pathList.get().get(position).PATH_STEPS) {
            drawHighLight(pathStep.POLY_LINE);
        }
    }

    private void drawHighLight(String polyLine) {
        ArrayList<LatLng> linePoints = PolyHelper.decodePolyLine(polyLine);

        PolylineOptions polyOptions = new PolylineOptions();

        for (LatLng latLng : linePoints) {
            polyOptions.add(latLng);
        }
        polyOptions.color(ContextCompat.getColor(AppController.getInstance().getAppContext(), R.color.md_deep_purple_A400));
        polyOptions.width(22);

        Polyline highLightPolyLine = map.addPolyline(polyOptions);
        highLightPolyLine.setZIndex(1000);

        highLightPolyList.add(highLightPolyLine);
    }

    private void removeHighLightPolyline() {
        if (notNull(highLightPolyList)) {
            for (Polyline highLightPoly : highLightPolyList) {
                highLightPoly.remove();
            }
        }
        else
            highLightPolyList = new ArrayList<>();
    }

    private void showRatingWindow() {
        View view = PopWindowHelper.getSharedRatingWindow(this, mapRootLayout, true);

        final RatingBar ratingBar = (RatingBar) view.findViewById(R.id.sharedRatingBar);
        final TextView cancel = (TextView) view.findViewById(R.id.sharedCancel);
        final TextView send = (TextView) view.findViewById(R.id.sharedSend);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int rating = (int) ratingBar.getRating();
                if (rating != 0) {
                    sendRatingToService(rating);
                    cancel.callOnClick();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopWindowHelper.dismissPopWindow();
                cancel.setOnClickListener(null);
                send.setOnClickListener(null);
            }
        });
    }

    private void sendRatingToService(int rating) {
        DialogHelper.showLoadingDialog(this);

        String id = String.valueOf(getIntent().getIntExtra(BUNDLE_SHARED_ITEM_ID, 0));
        Log.i(TAG, "ItemID: " + getIntent().getIntExtra(BUNDLE_SHARED_ITEM_ID, 0) + " Rating: " + rating);

        WebAgent.sendRatingToBikingService(id, rating, new WebAgent.WebResultImplement() {
            @Override
            public void onResultSucceed(String response) {
                Utility.toastShort(getString(R.string.rating_completed));
                PopWindowHelper.dismissPopWindow();
                DialogHelper.dismissDialog();
            }

            @Override
            public void onResultFail(String errorMessage) {
                Utility.toastShort(errorMessage);
                DialogHelper.dismissDialog();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (PopWindowHelper.isPopWindowShowing())
            PopWindowHelper.dismissPopWindow();
        else
            super.onBackPressed();
    }
}