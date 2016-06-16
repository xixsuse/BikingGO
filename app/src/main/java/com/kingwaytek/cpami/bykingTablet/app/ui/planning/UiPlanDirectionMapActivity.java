package com.kingwaytek.cpami.bykingTablet.app.ui.planning;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseGoogleApiActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.JsonParser;
import com.kingwaytek.cpami.bykingTablet.utilities.PolyHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PathListPagerAdapter;
import com.kingwaytek.cpami.bykingTablet.utilities.adapter.PathListViewAdapter;

import java.io.InputStream;
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

    @Override
    protected void onApiReady() {
        getBundleAndDraw();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (notNull(pathListPager))
            pathListPager.removeOnPageChangeListener(this);
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
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case ACTION_LIST:
                showPathListView();
                break;
        }

        return true;
    }

    private void getBundleAndDraw() {
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        String jsonString = bundle.getString(BUNDLE_PLAN_DIRECTION_JSON);
        int planIndex = bundle.getInt(BUNDLE_PLAN_EDIT_INDEX);

        drawMultiPointsLine(jsonString, planIndex);
    }

    private void drawMultiPointsLine(String jsonString, int planIndex) {
        String polyOverview = JsonParser.getPolyLineOverview(jsonString);

        if (notNull(polyOverview)) {
            ArrayList<LatLng> linePoints = PolyHelper.decodePolyLine(polyOverview);
            ArrayList<ItemsPlanItem> planItems = DataArray.getPlansData().get(planIndex).PLAN_ITEMS;

            PolylineOptions polyOptions = new PolylineOptions();

            for (LatLng latLng : linePoints) {
                polyOptions.add(latLng);
            }
            polyOptions.color(ContextCompat.getColor(AppController.getInstance().getAppContext(), R.color.md_light_blue_300));
            polyOptions.width(15);

            MarkerOptions marker = new MarkerOptions();

            marker.position(linePoints.get(0));
            marker.title("1." + planItems.get(0).TITLE);
            InputStream is = getResources().openRawResource(+ R.drawable.ic_start);
            marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

            map.addMarker(marker);

            marker.position(linePoints.get(linePoints.size() - 1));
            marker.title(planItems.size() + "." + planItems.get(planItems.size() - 1).TITLE);
            is = getResources().openRawResource(+ R.drawable.ic_end);
            marker.icon(BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is)));

            map.addMarker(marker);

            if (planItems.size() > 2) {
                is = getResources().openRawResource(+ R.drawable.ic_search_result);
                BitmapDescriptor markerIcon = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeStream(is));

                MarkerOptions waypointsMarker = new MarkerOptions();

                for (int i = 0; i < planItems.size(); i ++) {
                    if (i != 0 && i != planItems.size() -1) {
                        waypointsMarker.position(new LatLng(planItems.get(i).LAT, planItems.get(i).LNG));
                        waypointsMarker.title((i + 1) + "." + planItems.get(i).TITLE);
                        waypointsMarker.icon(markerIcon);
                        map.addMarker(waypointsMarker);
                    }
                }
            }

            map.addPolyline(polyOptions);

            PopWindowHelper.dismissPopWindow();
            moveCameraAndZoom(linePoints.get(0), 16);

            closeInputStream(is);
        }
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
                ArrayList<String[]> namePairList = getNamePairs(bundle.getInt(BUNDLE_PLAN_EDIT_INDEX));

                DataArray.getDirectionPathListData(jsonString, namePairList, new DataArray.OnDataGetCallBack() {
                    @Override
                    public void onDataGet() {
                        setPathListData(view);
                    }
                });
            }
        }
    }

    private ArrayList<String[]> getNamePairs(int index) {
        ArrayList<ItemsPlanItem> planItems = DataArray.getPlansData().get(index).PLAN_ITEMS;
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
            ArrayList<String[]> namePairList = getNamePairs(bundle.getInt(BUNDLE_PLAN_EDIT_INDEX));

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

        pathListView.setAdapter(new PathListViewAdapter(this, pathList.PATH_STEPS));

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

    @Override
    public void onBackPressed() {
        if (PopWindowHelper.isPopWindowShowing())
            PopWindowHelper.dismissPopWindow();
        else
            super.onBackPressed();
    }
}