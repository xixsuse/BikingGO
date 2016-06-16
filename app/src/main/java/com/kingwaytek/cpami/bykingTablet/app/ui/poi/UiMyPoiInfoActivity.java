package com.kingwaytek.cpami.bykingTablet.app.ui.poi;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.callbacks.OnPhotoRemovedCallBack;
import com.kingwaytek.cpami.bykingTablet.utilities.BitmapUtility;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.ImageSelectHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.File;

/**
 * My POI Detail Information!!!
 *
 * @author Vincent (2016/5/23)
 */
public class UiMyPoiInfoActivity extends BaseActivity implements OnPhotoRemovedCallBack{

    private ItemsMyPOI poiItem;

    private ScrollView rootLayout;

    private TextView poiTitle;
    private TextView poiContent;
    private ImageView poiBigPhotoView;
    private TextView poiLocation;

    private ImageButton btn_poiEdit;
    private ImageButton btn_poiDelete;
    private ImageButton btn_fbShare;

    private ImageView poiImageView;
    private String photoPath;

    private boolean isPoiExisted;
    private boolean isFromMap;

    @Override
    protected void init() {
        getPoiItem();
        setPoiInfo();
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.menu_my_poi);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_poi_info;
    }

    @Override
    protected void findViews() {
        rootLayout = (ScrollView) findViewById(R.id.poiInfoLayout);
        poiTitle = (TextView) findViewById(R.id.text_poiTitle);
        poiContent = (TextView) findViewById(R.id.text_poiContent);
        poiBigPhotoView = (ImageView) findViewById(R.id.image_poiPhoto);
        poiLocation = (TextView) findViewById(R.id.text_poiLocation);
        btn_poiEdit = (ImageButton) findViewById(R.id.btn_poiEdit);
        btn_poiDelete = (ImageButton) findViewById(R.id.btn_poiDelete);
        btn_fbShare = (ImageButton) findViewById(R.id.btn_facebookShare);
    }

    @Override
    protected void setListener() {
        poiBigPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //DialogHelper.showImageViewDialog(UiMyPoiInfoActivity.this, poiItem.TITLE, poiItem.PHOTO_PATH);
                if (!poiItem.PHOTO_PATH.isEmpty()) {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(new File(poiItem.PHOTO_PATH)), "image/*");
                    startActivity(intent);
                }
            }
        });

        btn_poiEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMyPoi();
            }
        });

        btn_poiDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteMyPoi();
            }
        });
    }

    private void getPoiItem() {
        Intent intent = getIntent();
        poiItem = (ItemsMyPOI) intent.getSerializableExtra(BUNDLE_MY_POI_INFO);

        isPoiExisted = FavoriteHelper.isPoiExisted(poiItem.LAT, poiItem.LNG);
        isFromMap = intent.getBooleanExtra(BUNDLE_MAP_TO_POI_INFO, false);
    }

    private void setPoiInfo() {
        this.poiLocation.setText(poiItem.ADDRESS);

        poiTitle.setText(poiItem.TITLE);
        poiContent.setText(poiItem.DESCRIPTION);

        if (!poiItem.PHOTO_PATH.isEmpty()) {
            setImageViewHeight();
            int imageViewHeight = poiBigPhotoView.getLayoutParams().height;

            poiBigPhotoView.setVisibility(View.VISIBLE);
            poiBigPhotoView.setImageBitmap(BitmapUtility.getDecodedBitmapInFullWidth(poiItem.PHOTO_PATH, imageViewHeight));
        }
        else {
            poiBigPhotoView.setVisibility(View.GONE);
            poiBigPhotoView.setImageResource(0);
        }
    }

    private void setImageViewHeight() {
        int height = (Utility.getScreenWidth() / 3) * 2; // 寬高比 3:2
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, height);
        poiBigPhotoView.setLayoutParams(params);
    }

    private void editMyPoi() {
        View view = PopWindowHelper.getFullScreenPoiEditView(rootLayout);

        TextView poiBanner = (TextView) view.findViewById(R.id.poiBanner);
        final EditText poiTitle = (EditText) view.findViewById(R.id.edit_poiTitle);
        final EditText poiLocation = (EditText) view.findViewById(R.id.edit_poiLocation);
        final EditText poiContent = (EditText) view.findViewById(R.id.edit_poiContent);
        poiImageView = (ImageView) view.findViewById(R.id.image_poiPhoto);
        Button poiBtnSave = (Button) view.findViewById(R.id.btn_poiSave);
        Button poiBtnCancel = (Button) view.findViewById(R.id.btn_poiCancel);

        poiBanner.setText(getString(R.string.poi_edit_a_exist_one));


        poiTitle.setText(poiItem.TITLE);
        poiTitle.setSelection(poiItem.TITLE.length());

        poiLocation.setText(poiItem.ADDRESS);
        poiLocation.setSelection(poiItem.ADDRESS.length());

        poiContent.setText(poiItem.DESCRIPTION);
        poiContent.setSelection(poiItem.DESCRIPTION.length());

        photoPath = poiItem.PHOTO_PATH;

        if (!poiItem.PHOTO_PATH.isEmpty())
            setPoiEditImageView(poiItem.PHOTO_PATH);

        poiBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = poiTitle.getText().toString();
                String address = poiLocation.getText().toString();
                String content = poiContent.getText().toString();

                if (!title.isEmpty()) {
                    if (isPoiExisted) {
                        FavoriteHelper.updateMyPoi(title, address, content, photoPath);
                        Utility.toastShort(getString(R.string.poi_update_done));
                        PopWindowHelper.dismissPopWindow();

                        resetPoiInfo();
                    }
                }
                else
                    Utility.toastShort(getString(R.string.poi_require_title));
            }
        });

        poiBtnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopWindowHelper.dismissPopWindow();
            }
        });

        setImageClickListener();
    }

    private void setImageClickListener() {
        if (photoPath == null || photoPath.isEmpty())
            poiImageView.setOnClickListener(ImageSelectHelper.getImageClick(this, null));
        else
            poiImageView.setOnClickListener(ImageSelectHelper.getImageClick(this, this));
    }

    private void setPoiEditImageView(String photoPath) {
        int reqSize = getResources().getDimensionPixelSize(R.dimen.poi_photo_edit_view);
        poiImageView.setImageBitmap(BitmapUtility.getDecodedBitmap(photoPath, reqSize, reqSize));
    }

    @Override
    public void onPhotoRemoved() {
        photoPath = "";
        poiImageView.setImageResource(R.drawable.selector_add_photo);
        setImageClickListener();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.i(TAG, "onActivityResult: " + requestCode + " " + resultCode);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_PHOTO_FROM_GALLERY:
                case REQUEST_PHOTO_FROM_CAMERA:
                    getPhotoPathAndSetImageView(requestCode, data);
                    break;
            }
        }
    }

    private void getPhotoPathAndSetImageView(int requestCode, Intent data) {
        photoPath = ImageSelectHelper.getPhotoPath(this, requestCode, data);
        setPoiEditImageView(photoPath);
        setImageClickListener();
    }

    private void resetPoiInfo() {
        poiItem = FavoriteHelper.getMyPoiItem();
        setPoiInfo();
    }

    private void deleteMyPoi() {
        if (isPoiExisted) {
            DialogHelper.showDeleteConfirmDialog(this, poiItem.TITLE, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (isFromMap) {
                        Intent intent = new Intent();
                        intent.putExtra(BUNDLE_DELETE_POI, new double[]{poiItem.LAT, poiItem.LNG});
                        setResult(RESULT_DELETE, intent);
                    }
                    else
                        FavoriteHelper.removeMyPoi(poiItem.LAT, poiItem.LNG);

                    finish();
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                break;
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        if (isFromMap) {
            setResult(RESULT_OK);
            super.onBackPressed();
        }
        else
            super.onBackPressed();
    }
}
