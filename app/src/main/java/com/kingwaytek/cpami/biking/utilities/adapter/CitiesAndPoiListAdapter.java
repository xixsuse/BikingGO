package com.kingwaytek.cpami.biking.utilities.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.kingwaytek.cpami.biking.R;
import com.kingwaytek.cpami.biking.app.model.items.ItemsCitiesAndPOI;

import java.util.ArrayList;

/**
 * Created by vincent.chang on 2016/9/6.
 */
public class CitiesAndPoiListAdapter extends BaseAdapter {

    private boolean isViewingCities;

    private ArrayList<ItemsCitiesAndPOI> citiesAndPoiList;
    private LayoutInflater inflater;

    private ArrayList<ItemsCitiesAndPOI> originalList;

    public CitiesAndPoiListAdapter(Context context, boolean isViewingCities, ArrayList<ItemsCitiesAndPOI> citiesAndPoiList) {
        this.isViewingCities = isViewingCities;
        this.citiesAndPoiList = citiesAndPoiList;
        inflater = LayoutInflater.from(context);

        originalList = new ArrayList<>();
        originalList.addAll(citiesAndPoiList);
    }

    public void resetList(boolean isViewingCities, ArrayList<ItemsCitiesAndPOI> citiesAndPoiList) {
        this.isViewingCities = isViewingCities;
        this.citiesAndPoiList = citiesAndPoiList;

        originalList.clear();
        originalList.addAll(citiesAndPoiList);

        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return citiesAndPoiList.size();
    }

    @Override
    public Object getItem(int position) {
        if (isViewingCities)
            return citiesAndPoiList.get(position).CITY_NAME;
        else
            return citiesAndPoiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        if (!isViewingCities)
            return citiesAndPoiList.get(position).POI_ID;
        else
            return -1;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.inflate_city_list, null);

            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.text_cityName);

            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder) convertView.getTag();

        holder.name.setText(isViewingCities ? citiesAndPoiList.get(position).CITY_NAME : citiesAndPoiList.get(position).POI_NAME);

        return convertView;
    }

    public boolean isViewingCities() {
        return isViewingCities;
    }

    private class ViewHolder {
        TextView name;
    }

    public void filterData(String queryString) {
        citiesAndPoiList.clear();

        if (queryString.isEmpty()) {
            citiesAndPoiList.addAll(originalList);
        }
        else {
            for (ItemsCitiesAndPOI citiesAndPOI : originalList) {
                if (citiesAndPOI.POI_NAME != null && citiesAndPOI.POI_NAME.contains(queryString)) {
                    citiesAndPoiList.add(citiesAndPOI);
                    Log.i("CitiesAndPoiListAdapter", "ListAdded: " + citiesAndPOI.POI_NAME);
                }
            }
        }
        notifyDataSetChanged();
    }
}
