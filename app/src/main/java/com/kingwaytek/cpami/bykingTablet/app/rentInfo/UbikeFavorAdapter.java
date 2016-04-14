package com.kingwaytek.cpami.bykingTablet.app.rentInfo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.utilities.UtilDialog;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class UbikeFavorAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<UbikeObject> Ubike_list;
    private ArrayList<DistenceObject> distence_list;
    private ArrayList<Integer> UbikeFavorList;

    public UbikeFavorAdapter() {

    }

    public UbikeFavorAdapter(Context context, int layout, ArrayList<UbikeObject> Ubike_list,
                             ArrayList<DistenceObject> distence_list, ArrayList<Integer> UbikeFavorList) {

        this.context = context;
        this.layout = layout;
        this.Ubike_list = Ubike_list;
        this.distence_list = distence_list;
        this.UbikeFavorList = UbikeFavorList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return UbikeFavorList.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(parent.getContext()).inflate(this.layout, null);
            viewHolder.title = (TextView) convertView.findViewById(R.id.ubike_cell_title);
            viewHolder.dis = (TextView) convertView.findViewById(R.id.ubike_cell_dis);
            viewHolder.sbi = (TextView) convertView.findViewById(R.id.ubike_cell_sbi);
            viewHolder.bemp = (TextView) convertView.findViewById(R.id.ubike_cell_bemp);
            viewHolder.myfavorButton = (Button) convertView.findViewById(R.id.ubike_cell_myfavor);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        // -----
        if (distence_list.size() == 0) {
            for (int i = 0; i < Ubike_list.size(); i++) {
                if (Ubike_list.get(i).getsno() == UbikeFavorList.get(position)) {
                    viewHolder.title.setText(Ubike_list.get(i).getsna());
                    viewHolder.sbi.setText("" + Ubike_list.get(i).getsbi());
                    viewHolder.bemp.setText("" + Ubike_list.get(i).getbemp());
                    viewHolder.dis.setText("--km");
                    break;
                }
            }
        } else {
            double temp_dis = distence_list.get(position).getDis();
            int temp_sno = distence_list.get(position).getSno();
            DecimalFormat df = new DecimalFormat("#.##");

            for (int i = 0; i < Ubike_list.size(); i++) {
                if (Ubike_list.get(i).getsno() == UbikeFavorList.get(position)) {
                    viewHolder.title.setText(Ubike_list.get(i).getsna());
                    viewHolder.sbi.setText("" + Ubike_list.get(i).getsbi());
                    viewHolder.bemp.setText("" + Ubike_list.get(i).getbemp());
                    viewHolder.dis.setText("--km");
                    break;
                }
            }

            for (int j = 0; j < Ubike_list.size(); j++) {
                if (distence_list.get(j).getSno() == UbikeFavorList.get(position)) {
                    viewHolder.dis.setText("" + df.format(distence_list.get(j).getDis()) + "km");
                    break;
                }
            }

        }
        // -----
        viewHolder.myfavorButton.setVisibility(View.VISIBLE);
        viewHolder.myfavorButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {

                UtilDialog uit = new UtilDialog(context) {
                    @Override
                    public void click_btn_1() {
                        UbikeFavorList.remove(position);
                        notifyDataSetChanged();
                        super.click_btn_1();
                    }
                };
                uit.showDialog_route_plan_choice("是否確定刪除最愛？", null, "是", "否");
            }
        });
        return convertView;
    }

    private class ViewHolder {
        TextView title;
        TextView dis;
        TextView sbi;
        TextView bemp;
        Button myfavorButton;
    }

}