package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.AppController;
import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.ui.BaseActivity;
import com.kingwaytek.cpami.bykingTablet.app.ui.poi.UiMyPoiListActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.BitmapUtility;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vincent.chang on 2016/5/23.
 */
public class MyPoiListAdapter extends BaseAdapter {

    private static final String TAG = "MyPoiListAdapter";

    private Context context;
    private ArrayList<ItemsMyPOI> poiList;
    private LayoutInflater inflater;
    private final int imageSize;

    private boolean showCheckBox;
    private HashMap<Integer, Boolean> checkedMap;

    public MyPoiListAdapter(Context context, ArrayList<ItemsMyPOI> poiList) {
        this.context = context;
        this.poiList = poiList;
        inflater = LayoutInflater.from(context);
        imageSize = context.getResources().getDimensionPixelSize(R.dimen.icon_common_size_xl);
        checkedMap = new HashMap<>();
    }

    public void refreshList(ArrayList<ItemsMyPOI> poiList) {
        this.poiList = poiList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return poiList.size();
    }

    @Override
    public Object getItem(int position) {
        return poiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_my_poi_list, null);

            holder = new ViewHolder();
            holder.poiPhoto = (ImageView) convertView.findViewById(R.id.image_poiThumbnail);
            holder.poiTitle = (TextView) convertView.findViewById(R.id.text_poiTitle);
            holder.poiInfo = (ImageButton) convertView.findViewById(R.id.btn_poiInfo);
            holder.checkbox = (CheckBox) convertView.findViewById(R.id.checkbox_eachPoi);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.poiInfo.setFocusable(false);
        holder.poiInfo.setFocusableInTouchMode(false);
        holder.poiInfo.setClickable(true);

        holder.poiPhoto.setOnClickListener(getPhotoClick(position));

        String photoPath = poiList.get(position).PHOTO_PATH;

        if (!photoPath.isEmpty()) {
            Bitmap cachedPhoto = ((BaseActivity) context).getBitmapFromMemCache(photoPath);

            if (cachedPhoto == null) {
                Bitmap scaledPhoto = BitmapUtility.getDecodedBitmap(photoPath, imageSize, imageSize);
                ((BaseActivity) context).addBitmapToMemoryCache(photoPath, scaledPhoto);
                holder.poiPhoto.setImageBitmap(scaledPhoto);
            }
            else
                holder.poiPhoto.setImageBitmap(cachedPhoto);
        }
        else
            holder.poiPhoto.setImageResource(R.drawable.ic_empty_image);

        holder.poiTitle.setText(poiList.get(position).TITLE);
        holder.poiInfo.setOnClickListener(getInfoButtonClick(position));

        if (!checkedMap.containsKey(position)) {
            checkedMap.put(position, false);
            Log.i(TAG, "checkedMap key: " + position);
        }

        if (showCheckBox) {
            holder.checkbox.setVisibility(View.VISIBLE);
            holder.poiInfo.setVisibility(View.GONE);

            holder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    checkedMap.put(position, isChecked);
                    notifyDataSetChanged();
                }
            });
        }
        else {
            holder.checkbox.setVisibility(View.GONE);
            holder.poiInfo.setVisibility(View.VISIBLE);
            holder.checkbox.setOnCheckedChangeListener(null);
        }

        if (checkedMap.containsKey(position) && checkedMap.get(position)) {
            convertView.setBackgroundColor(ContextCompat.getColor(AppController.getInstance().getAppContext(), R.color.md_grey_300));
            holder.checkbox.setChecked(true);
        }
        else {
            convertView.setBackgroundColor(0);
            holder.checkbox.setChecked(false);
        }

        return convertView;
    }

    private View.OnClickListener getPhotoClick(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogHelper.showImageViewDialog(context, poiList.get(position).TITLE, poiList.get(position).PHOTO_PATH);
            }
        };
    }

    private View.OnClickListener getInfoButtonClick(final int position) {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((UiMyPoiListActivity) context).goToPoiInfo(poiList.get(position));
            }
        };
    }

    public void showCheckBox(boolean isShow) {
        showCheckBox = isShow;
    }

    public void setBoxChecked(int position) {
        if (checkedMap.containsKey(position))
            checkedMap.put(position, true);

        notifyDataSetChanged();
    }

    public void unCheckAllBox() {
        for (int i = 0; i < checkedMap.size(); i++) {
            if (checkedMap.containsKey(i))
                checkedMap.put(i, false);
        }
        showCheckBox(false);

        notifyDataSetChanged();
    }

    public boolean isCheckBoxShowing() {
        return showCheckBox;
    }

    public ArrayList<Integer> getCheckedList() {
        ArrayList<Integer> checkedList = new ArrayList<>();

        for (int i = 0; i < checkedMap.size(); i++) {
            if (checkedMap.containsKey(i) && checkedMap.get(i))
                checkedList.add(i);
        }

        return checkedList;
    }

    class ViewHolder {
        ImageView poiPhoto;
        TextView poiTitle;
        ImageButton poiInfo;
        CheckBox checkbox;
    }
}
