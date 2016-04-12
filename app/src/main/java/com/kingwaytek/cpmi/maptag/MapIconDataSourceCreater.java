package com.kingwaytek.cpmi.maptag;

import java.util.ArrayList;

import com.kingwaytek.cpami.bykingTablet.R;

public class MapIconDataSourceCreater {

	private static String[] MAP_TAG_DESCRIPTION = new String[] { "廁所",
			"河濱自行車道入口", "自行車停車場", "自行車打氣站", "消防局", "公用事業", "文教藝文", "居家修繕",
			"逛街購物", "休閒娛樂", "社會服務", "宗教民俗", "旅宿", "自行車出租", "自行車維修站", "派出所",
			"政府機關", "金融証卷", "餐飲美食", "交通運輸", "醫療保健", "公司行號", "殯葬禮儀" };

	private static int[] MAP_TAG_ICON = new int[] { R.drawable.b1,
			R.drawable.b2, R.drawable.b3, R.drawable.b4, R.drawable.b5,
			R.drawable.b6, R.drawable.b7, R.drawable.b8, R.drawable.b9,
			R.drawable.b10, R.drawable.b11, R.drawable.b12, R.drawable.b13,
			R.drawable.b14, R.drawable.b15, R.drawable.b16, R.drawable.b17,
			R.drawable.b18, R.drawable.b19, R.drawable.b20, R.drawable.b21,
			R.drawable.b22, R.drawable.b23 };

	public static ArrayList<MapIconDescriptionObject> getDataSource() {
		ArrayList<MapIconDescriptionObject> dataSource = new ArrayList<MapIconDescriptionObject>();

		for (int i = 0; i < MAP_TAG_DESCRIPTION.length; i++) {
			dataSource.add(MapIconDataSourceCreater.createTagDescription(
					MAP_TAG_DESCRIPTION[i], MAP_TAG_ICON[i]));
		}

		return dataSource;
	}

	public static MapIconDescriptionObject createTagDescription(
			String description, int icon) {
		MapIconDescriptionObject object = new MapIconDescriptionObject();
		object.setDescription(description);
		object.setIcon(icon);

		return object;
	}
}
