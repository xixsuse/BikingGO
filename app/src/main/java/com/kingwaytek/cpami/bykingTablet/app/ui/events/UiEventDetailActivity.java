package com.kingwaytek.cpami.bykingTablet.app.ui.events;

import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsEvents;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

/**
 * 活動訊息詳細頁面
 *
 * @author Vincent (2016/7/11)
 */
public class UiEventDetailActivity extends BaseActivity implements ViewTreeObserver.OnPreDrawListener {

    private ItemsEvents eventItem;

    private static final int DEFAULT_LINES = 5;
    private int maxLines;
    private boolean hasMeasured;
    private boolean isExpanded;

    private TextView text_title;
    private TextView text_startTime;
    private TextView text_endTime;
    private TextView text_website;
    private TextView text_description;
    private TextView text_keepReading;
    private TextView text_location;
    private TextView trafficInfoText;
    private TextView text_trafficInfo;
    private TextView parkingInfoText;
    private TextView text_parkingInfo;
    private ViewPager photoPager;

    private CallbackManager callBackManager;
    private ShareDialog shareDialog;
    private static final int FB_LINK_DESCRIBE_LENGTH = 40;

    @Override
    protected void init() {
        getBundle();
        setInformation();
        getDescriptionMaxLines();
        initFBCallBack();
    }

    private void getBundle() {
        eventItem = (ItemsEvents) getIntent().getSerializableExtra(BUNDLE_EVENT_DETAIL);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_events);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_event_detail;
    }

    @Override
    protected void findViews() {
        text_title = (TextView) findViewById(R.id.text_eventTitle);
        text_startTime = (TextView) findViewById(R.id.text_eventStartTime);
        text_endTime = (TextView) findViewById(R.id.text_eventEndTime);
        text_website = (TextView) findViewById(R.id.text_eventWebsite);
        text_description = (TextView) findViewById(R.id.text_eventDescription);
        text_keepReading = (TextView) findViewById(R.id.text_continueReading);
        text_location = (TextView) findViewById(R.id.text_eventLocation);
        trafficInfoText = (TextView) findViewById(R.id.trafficInfoText);
        text_trafficInfo = (TextView) findViewById(R.id.text_eventTraffic);
        parkingInfoText = (TextView) findViewById(R.id.parkingInfoText);
        text_parkingInfo = (TextView) findViewById(R.id.text_eventParking);
        photoPager = (ViewPager) findViewById(R.id.eventPhotoPager);
    }

    @Override
    protected void setListener() {
        text_keepReading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isExpanded) {
                    text_description.setMaxLines(DEFAULT_LINES);
                    text_keepReading.setText(getString(R.string.events_more_text));
                    isExpanded = false;
                }
                else {
                    text_description.setMaxLines(maxLines);
                    text_keepReading.setText(getString(R.string.events_less_text));
                    text_keepReading.setVisibility(View.GONE);
                    isExpanded = true;
                }
                text_keepReading.setVisibility(View.VISIBLE);
                text_description.postInvalidate();
            }
        });
    }

    private void setInformation() {
        String location = eventItem.LOCATION + " - " + eventItem.ADDRESS;

        text_title.setText(eventItem.NAME);
        text_startTime.setText(eventItem.START_TIME);
        text_endTime.setText(eventItem.END_TIME);
        text_website.setText(eventItem.WEBSITE);

        eventItem.DESCRIPTION = eventItem.DESCRIPTION.replaceAll("&amp;", "&").replaceAll("nbsp;", " ");
        text_description.setText(eventItem.DESCRIPTION);

        text_location.setText(location);

        if (eventItem.TRAVEL_INFO.isEmpty()) {
            trafficInfoText.setVisibility(View.GONE);
            text_trafficInfo.setVisibility(View.GONE);
        }
        else
            text_trafficInfo.setText(eventItem.TRAVEL_INFO);

        if (eventItem.PARKING_INFO.isEmpty()) {
            parkingInfoText.setVisibility(View.GONE);
            text_parkingInfo.setVisibility(View.GONE);
        }
        else
            text_parkingInfo.setText(eventItem.PARKING_INFO);
    }

    private void getDescriptionMaxLines() {
        ViewTreeObserver observer = text_description.getViewTreeObserver();
        observer.addOnPreDrawListener(this);
    }

    @Override
    public boolean onPreDraw() {
        if (!hasMeasured) {
            maxLines = text_description.getLineCount();
            text_description.setMaxLines(DEFAULT_LINES);

            if (maxLines <= DEFAULT_LINES)
                text_keepReading.setVisibility(View.GONE);

            hasMeasured = true;

            Log.i(TAG, "MaxLines: " + maxLines);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_FB_SHARE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {
            case ACTION_FB_SHARE:
                shareLinkByFacebook();
                break;
        }

        return true;
    }

    private void initFBCallBack() {
        if (callBackManager == null)
            callBackManager = CallbackManager.Factory.create();

        shareDialog = new ShareDialog(this);
        shareDialog.registerCallback(callBackManager, new FacebookCallback<Sharer.Result>() {
            @Override
            public void onSuccess(Sharer.Result result) {
                Utility.toastShort("Success! " + result.toString());
            }

            @Override
            public void onCancel() {
                Utility.toastShort("Canceled!");
            }

            @Override
            public void onError(FacebookException error) {
                Utility.toastShort("Error!!! " + error.getMessage());
            }
        });
    }

    private void shareLinkByFacebook() {
        int len;

        if (eventItem.DESCRIPTION.length() > FB_LINK_DESCRIBE_LENGTH)
            len = FB_LINK_DESCRIBE_LENGTH;
        else
            len = eventItem.DESCRIPTION.length();

        ShareLinkContent shareContent = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(eventItem.WEBSITE))
                .setContentTitle(eventItem.NAME)
                .setContentDescription(eventItem.DESCRIPTION.substring(0, len))
                .build();

        shareDialog.show(shareContent);
    }
}
