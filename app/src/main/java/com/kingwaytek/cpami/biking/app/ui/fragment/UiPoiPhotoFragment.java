package com.kingwaytek.cpami.biking.app.ui.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.ui.BaseFragment;
import com.kingwaytek.cpami.biking.app.web.WebAgent;
import com.kingwaytek.cpami.biking.utilities.Utility;

import java.text.MessageFormat;

/**
 * 顯示景點書上的圖片<p>
 *
 * Load images by using Volley's ImageLoader.
 *
 * @author Vincent (2016/9/7)
 */
public class UiPoiPhotoFragment extends BaseFragment {

    private ImageView poiImageView;
    private ProgressBar loadingCircle;

    private static class SingleInstance {
        private static final UiPoiPhotoFragment INSTANCE_1 = new UiPoiPhotoFragment();
        private static final UiPoiPhotoFragment INSTANCE_2 = new UiPoiPhotoFragment();
        private static final UiPoiPhotoFragment INSTANCE_3 = new UiPoiPhotoFragment();
    }

    public static UiPoiPhotoFragment getInstance(String photoPath, int index) {
        UiPoiPhotoFragment instance;

        if (index == 1)
            instance = SingleInstance.INSTANCE_1;
        else if (index == 2)
            instance = SingleInstance.INSTANCE_2;
        else
            instance = SingleInstance.INSTANCE_3;

        if (!instance.isAdded() || !instance.isResumed()) {
            Bundle arg = new Bundle();
            arg.putString(BUNDLE_FRAGMENT_DEFAULT_ARG, photoPath);
            instance.setArguments(arg);
        }

        return instance;
    }

    @Override
    protected void init() {
        getPhoto();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_poi_photo;
    }

    @Override
    protected void findRootViews(View rootView) {
        poiImageView = (ImageView) rootView.findViewById(R.id.poiImageView);
        loadingCircle = (ProgressBar) rootView.findViewById(R.id.poiPhotoLoadingCircle);
    }

    @Override
    protected void setListener() {

    }

    private void getPhoto() {
        final String url = MessageFormat.format(API_POI_URL, getArguments().getString(BUNDLE_FRAGMENT_DEFAULT_ARG));

        final int width = Utility.getScreenWidth();
        final int height = (Utility.getScreenWidth() / 3) * 2;

        WebAgent.getImageByUrl(url, new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                if (response.getBitmap() != null) {
                    poiImageView.setImageBitmap(response.getBitmap());
                    loadingCircle.setVisibility(View.GONE);
                }
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                Utility.toastShort("ImageDownloadError: " + error.getMessage());
                poiImageView.setImageResource(R.drawable.ic_empty_image);
                loadingCircle.setVisibility(View.GONE);
            }
        }, width, height);
    }
}
