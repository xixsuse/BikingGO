package com.kingwaytek.cpami.bykingTablet.app.ui.poi;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.FavoriteHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.PopWindowHelper;
import com.kingwaytek.cpami.bykingTablet.utilities.Utility;

/**
 * My POI Detail Information!!!
 *
 * @author Vincent (2016/5/23)
 */
public class UiMyPoiInfoActivity extends BaseActivity {

    private ItemsMyPOI poiItem;

    private ScrollView rootLayout;

    private TextView poiTitle;
    private TextView poiContent;
    private ImageView poiPhoto;
    private TextView poiLatLng;

    private ImageButton btn_poiUpload;
    private ImageButton btn_poiEdit;
    private ImageButton btn_poiDelete;
    private ImageButton btn_fbShare;

    private ImageView poiImageView;
    private String photoPath;

    private boolean isPoiExisted;

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
        poiPhoto = (ImageView) findViewById(R.id.image_poiPhoto);
        poiLatLng = (TextView) findViewById(R.id.text_poiLatLng);
        btn_poiUpload = (ImageButton) findViewById(R.id.btn_poiUpload);
        btn_poiEdit = (ImageButton) findViewById(R.id.btn_poiEdit);
        btn_poiDelete = (ImageButton) findViewById(R.id.btn_poiDelete);
        btn_fbShare = (ImageButton) findViewById(R.id.btn_facebookShare);
    }

    @Override
    protected void setListener() {
        poiPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.showImageViewDialog(UiMyPoiInfoActivity.this, poiItem.PHOTO_PATH);
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
        poiItem = (ItemsMyPOI) intent.getSerializableExtra(MY_POI_INFO);

        isPoiExisted = FavoriteHelper.isPoiExisted(poiItem.LAT, poiItem.LNG);
    }

    private void setPoiInfo() {
        String poiLocation = String.valueOf(poiItem.LAT + ", " + poiItem.LNG);
        poiLatLng.setText(poiLocation);

        poiTitle.setText(poiItem.TITLE);
        poiContent.setText(poiItem.DESCRIPTION);

        int imageSize = getResources().getDimensionPixelSize(R.dimen.poi_photo_edit_view);

        if (!poiItem.PHOTO_PATH.isEmpty())
            poiPhoto.setImageBitmap(Utility.getDecodedBitmap(poiItem.PHOTO_PATH, imageSize, imageSize));
    }

    private void editMyPoi() {
        View view = PopWindowHelper.getFullScreenPoiEditView(rootLayout);

        TextView poiBanner = (TextView) view.findViewById(R.id.poiBanner);
        final EditText poiTitle = (EditText) view.findViewById(R.id.edit_poiTitle);
        final EditText poiContent = (EditText) view.findViewById(R.id.edit_poiContent);
        poiImageView = (ImageView) view.findViewById(R.id.image_poiPhoto);
        TextView poiLatLng = (TextView) view.findViewById(R.id.text_poiLatLng);
        Button poiBtnSave = (Button) view.findViewById(R.id.btn_poiSave);
        Button poiBtnCancel = (Button) view.findViewById(R.id.btn_poiCancel);

        poiBanner.setText(getString(R.string.poi_edit_a_exist_one));

        String poiLocation = String.valueOf("\n" + poiItem.LAT + ",\n" + poiItem.LNG);
        poiLatLng.setText(getString(R.string.poi_lat_lng, poiLocation));

        poiTitle.setText(poiItem.TITLE);
        poiTitle.setSelection(poiItem.TITLE.length());
        poiContent.setText(poiItem.DESCRIPTION);
        poiContent.setSelection(poiItem.DESCRIPTION.length());

        photoPath = poiItem.PHOTO_PATH;

        if (!poiItem.PHOTO_PATH.isEmpty())
            setPoiImageView(poiItem.PHOTO_PATH);

        poiImageView.setOnClickListener(getImageClickListener());

        poiBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = poiTitle.getText().toString();
                String content = poiContent.getText().toString();

                if (!title.isEmpty()) {
                    if (isPoiExisted) {
                        FavoriteHelper.updateMyPoi(title, content, photoPath);
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
    }

    private void setPoiImageView(String photoPath) {
        int reqSize = getResources().getDimensionPixelSize(R.dimen.poi_photo_edit_view);
        poiImageView.setImageBitmap(Utility.getDecodedBitmap(photoPath, reqSize, reqSize));
    }

    private View.OnClickListener getImageClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT < 19) {
                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, REQUEST_POI_PHOTO);
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                    intent.setType("image/*");
                    startActivityForResult(intent, REQUEST_POI_PHOTO_M);
                }
            }
        };
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_POI_PHOTO:
                case REQUEST_POI_PHOTO_M:
                    getPhotoPathAndSetImageView(requestCode, data);
                    break;
            }
        }
    }

    @SuppressWarnings("WrongConstant")
    @SuppressLint("NewApi")
    private void getPhotoPathAndSetImageView(int requestCode, Intent data) {
        Uri uri = data.getData();

        photoPath = uri.toString();

        switch (requestCode) {
            case REQUEST_POI_PHOTO:
                Cursor cursor = getContentResolver().query(uri, null, null, null, null);

                if (notNull(cursor)) {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    photoPath = cursor.getString(columnIndex);

                    Log.i(TAG, "ImageFilePath: " + photoPath);
                    cursor.close();
                }
                else
                    photoPath = uri.getPath();

                setPoiImageView(photoPath);
                break;

            case REQUEST_POI_PHOTO_M:
                final int takeFlags = data.getFlags() & (Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                getContentResolver().takePersistableUriPermission(uri, takeFlags);

                final String id = uri.getLastPathSegment().split(":")[1];
                final String[] imageColumns = {MediaStore.Images.Media.DATA};
                final String imageOrderBy = null;

                Uri storageUri = getStorageUri();

                Cursor imageCursor = getContentResolver().query(storageUri, imageColumns, MediaStore.Images.Media._ID + "="+id, null, imageOrderBy);

                if (notNull(imageCursor) && imageCursor.moveToFirst()) {
                    photoPath = imageCursor.getString(imageCursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    Log.i(TAG, "ImageFilePath_M: " + photoPath);

                    setPoiImageView(photoPath);

                    imageCursor.close();
                }
                break;
        }
    }

    private Uri getStorageUri() {
        String state = Environment.getExternalStorageState();

        if (state.equalsIgnoreCase(Environment.MEDIA_MOUNTED))
            return MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        else
            return MediaStore.Images.Media.INTERNAL_CONTENT_URI;
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
                    FavoriteHelper.removeMyPoi(poiItem.LAT, poiItem.LNG);
                    finish();
                }
            });
        }
    }
}
