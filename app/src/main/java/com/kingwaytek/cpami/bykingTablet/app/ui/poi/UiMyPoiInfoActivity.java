package com.kingwaytek.cpami.bykingTablet.app.ui.poi;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.share.Sharer;
import com.facebook.share.model.ShareOpenGraphAction;
import com.facebook.share.model.ShareOpenGraphContent;
import com.facebook.share.model.ShareOpenGraphObject;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.widget.ShareDialog;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.callbacks.OnPhotoRemovedCallBack;
import com.kingwaytek.cpami.bykingTablet.utilities.BitmapUtility;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.ImageSelectHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.MenuHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

import java.io.File;

/**
 * My POI Detail Information!!!
 *
 * @author Vincent (2016/5/23)
 */
public class UiMyPoiInfoActivity extends BaseActivity implements OnPhotoRemovedCallBack {

    private ItemsMyPOI poiItem;

    private ScrollView rootLayout;

    private TextView poiTitle;
    private TextView poiContent;
    private ImageView poiBigPhotoView;
    private TextView poiLocation;

    private FloatingActionButton floatingBtn_poiEdit;

    private ImageView poiImageView;
    private String photoPath;

    private boolean isPoiExisted;
    private boolean isFromMap;

    private CallbackManager callBackManager;
    private ShareDialog shareDialog;

    @Override
    protected void init() {
        getPoiItem();
        setPoiInfo();
        //initCallback();
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
        floatingBtn_poiEdit = (FloatingActionButton) findViewById(R.id.floatingBtn_poiEdit);
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

        floatingBtn_poiEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editMyPoi();
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

        String description = poiItem.DESCRIPTION + "\n\n\n";
        poiContent.setText(description);

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

                        refreshPoiInfo();
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

        //callBackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void getPhotoPathAndSetImageView(int requestCode, Intent data) {
        photoPath = ImageSelectHelper.getPhotoPath(this, requestCode, data);
        setPoiEditImageView(photoPath);
        setImageClickListener();
    }

    private void refreshPoiInfo() {
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
                    else {
                        FavoriteHelper.removeMyPoi(poiItem.LAT, poiItem.LNG);
                        setResult(RESULT_DELETE);
                    }

                    finish();
                }
            });
        }
    }

    private void initCallback() {
        if (callBackManager == null)
            callBackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callBackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Utility.toastShort("Success! " + loginResult.toString());
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

    private void sharePoiToFacebook() {
        ShareOpenGraphObject object = new ShareOpenGraphObject.Builder()
                .putString("og:type", "fitness.course")
                .putString("og:title", getString(R.string.app_name))
                .putString("og:description", poiItem.TITLE)
                .putInt("fitness:duration:value", 100)
                .putString("fitness:duration:units", "s")
                .putInt("fitness:distance:value", 12)
                .putString("fitness:distance:units", "km")
                .putInt("fitness:speed:value", 5)
                .putString("fitness:speed:units", "m/s")
                .build();

        ShareOpenGraphAction action;

        if (poiItem.PHOTO_PATH.isEmpty()) {
            action = new ShareOpenGraphAction.Builder()
                    .setActionType("fitness.bikes")
                    .putObject("fitness", object)
                    .build();
        }
        else {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(BitmapFactory.decodeFile(poiItem.PHOTO_PATH))
                    .setCaption(poiItem.TITLE)
                    .build();

            action = new ShareOpenGraphAction.Builder()
                    .setActionType("fitness.bikes")
                    .putObject("fitness", object)
                    .putPhoto("image", photo)
                    .build();
        }

        ShareOpenGraphContent content = new ShareOpenGraphContent.Builder()
                .setPreviewPropertyName("fitness")
                .setAction(action)
                .build();

        shareDialog.show(content);
        /*
        if (poiItem.PHOTO_PATH.isEmpty()) {
            ShareLinkContent shareContent = new ShareLinkContent.Builder()
                    .setContentUrl(Uri.parse("https://www.google.com"))
                    .setContentTitle(poiItem.TITLE)
                    .setContentDescription(poiItem.DESCRIPTION)
                    .build();

            shareDialog.show(shareContent);
        }
        else {
            SharePhoto photo = new SharePhoto.Builder()
                    .setBitmap(BitmapFactory.decodeFile(poiItem.PHOTO_PATH))
                    .setCaption(poiItem.TITLE)
                    .build();

            SharePhotoContent shareContent = new SharePhotoContent.Builder()
                    .addPhoto(photo)
                    .build();

            shareDialog.show(shareContent);
        }
        */
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuHelper.setMenuOptionsByMenuAction(menu, ACTION_DELETE);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                setResult(RESULT_OK);
                finish();
                break;

            case ACTION_DELETE:
                deleteMyPoi();
                break;
            /*
            case ACTION_SHARE:
                //LoginManager.getInstance().logInWithReadPermissions(UiMyPoiInfoActivity.this, Arrays.asList("public_profile", "user_friends"));
                LoginManager.getInstance().logInWithPublishPermissions(UiMyPoiInfoActivity.this, Arrays.asList("publish_actions"));
                //sharePoiToFacebook();
                break;
            */
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
