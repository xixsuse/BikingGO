package com.kingwaytek.cpami.bykingTablet.utilities.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.model.CommonBundle;
import com.kingwaytek.cpami.bykingTablet.app.model.items.ItemsMyPOI;
import com.kingwaytek.cpami.bykingTablet.app.ui.poi.UiMyPoiInfoActivity;
import com.kingwaytek.cpami.bykingTablet.utilities.BitmapUtility;
import com.kingwaytek.cpami.bykingTablet.utilities.DialogHelper;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/5/23.
 */
public class MyPoiListAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<ItemsMyPOI> poiList;
    private LayoutInflater inflater;
    private final int imageSize;

    public MyPoiListAdapter(Context context, ArrayList<ItemsMyPOI> poiList) {
        this.context = context;
        this.poiList = poiList;
        inflater = LayoutInflater.from(context);
        imageSize = context.getResources().getDimensionPixelSize(R.dimen.icon_common_size_xl);
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
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_my_poi_list, null);

            holder = new ViewHolder();
            holder.poiPhoto = (ImageView) convertView.findViewById(R.id.image_poiThumbnail);
            holder.poiTitle = (TextView) convertView.findViewById(R.id.text_poiTitle);
            holder.poiInfo = (ImageButton) convertView.findViewById(R.id.btn_poiInfo);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.poiInfo.setFocusable(false);
        holder.poiInfo.setFocusableInTouchMode(false);
        holder.poiInfo.setClickable(true);

        holder.poiPhoto.setOnClickListener(getPhotoClick(position));

        if (!poiList.get(position).PHOTO_PATH.isEmpty())
            holder.poiPhoto.setImageBitmap(BitmapUtility.getDecodedBitmap(poiList.get(position).PHOTO_PATH, imageSize, imageSize));
        else
            holder.poiPhoto.setImageResource(R.drawable.ic_empty_image);

        holder.poiTitle.setText(poiList.get(position).TITLE);
        holder.poiInfo.setOnClickListener(getInfoButtonClick(position));

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
                Intent intent = new Intent(context, UiMyPoiInfoActivity.class);
                Bundle bundle = new Bundle();

                bundle.putSerializable(CommonBundle.BUNDLE_MY_POI_INFO, poiList.get(position));
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                context.startActivity(intent);
            }
        };
    }

    class ViewHolder {
        ImageView poiPhoto;
        TextView poiTitle;
        ImageButton poiInfo;
    }
}
