package com.kingwaytek.cpami.bykingTablet.view;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * Enum Constants for Views
 * 
 * @author Andy Chiao (andy.chiao@kingwaytek.com)
 */
public class ViewConstant {

	public enum ActivityCaller { // need reverse
		FAVORITE(391), HISTORY(393), ADDRESS(395), NAVIGATION(392), POI(394), SPOI(
				396), TRACK(397), MAP(398),PHOTO(399),REPORT(400),SPOI_CATALOG(401), MAIN(402), RENT(403);

		private static final Map<Integer, ActivityCaller> callerMap = new HashMap<Integer, ActivityCaller>();
		private final int value;

		static {
			for (ActivityCaller ct : EnumSet.allOf(ActivityCaller.class)) {
				callerMap.put(ct.getValue(), ct);
			}
		}

		ActivityCaller(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static ActivityCaller get(int value) {
			return callerMap.get(value);
		}
	}

	public enum CursorListType {
		FAVORITE(391), HISTORY(393), ADDRESS(395);

		private static final Map<Integer, CursorListType> typeMap = new HashMap<Integer, CursorListType>();
		private final int value;

		static {
			for (CursorListType ct : EnumSet.allOf(CursorListType.class)) {
				typeMap.put(ct.getValue(), ct);
			}
		}

		CursorListType(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static CursorListType get(int value) {
			return typeMap.get(value);
		}
	}

	public enum ListMode {
		SINGLE(303), MULTIPLE(305), FIND(307);

		private static final Map<Integer, CursorListMode> modeMap = new HashMap<Integer, CursorListMode>();
		private final int value;

		static {
			for (CursorListMode ct : EnumSet.allOf(CursorListMode.class)) {
				modeMap.put(ct.getValue(), ct);
			}
		}

		ListMode(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static CursorListMode get(int value) {
			return modeMap.get(value);
		}
	}

	public enum CursorListMode {
		NORMAL(303), DELETE(305), SEARCH(307);

		private static final Map<Integer, CursorListMode> modeMap = new HashMap<Integer, CursorListMode>();
		private final int value;

		static {
			for (CursorListMode ct : EnumSet.allOf(CursorListMode.class)) {
				modeMap.put(ct.getValue(), ct);
			}
		}

		CursorListMode(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}

		public static CursorListMode get(int value) {
			return modeMap.get(value);
		}
	}

	public enum SearchMode {
		BY_KEYWORD(203, "關鍵字查詢"), BY_SURROUNDING(205, "週邊查詢");

		private static final Map<String, SearchMode> modeMap = new HashMap<String, SearchMode>();
		private final int value;
		private final String title;

		static {
			for (SearchMode ct : EnumSet.allOf(SearchMode.class)) {
				modeMap.put(ct.getTitle(), ct);
			}
		}

		SearchMode(int value, String name) {
			this.value = value;
			title = name;
		}

		public int getValue() {
			return value;
		}

		public String getTitle() {
			return title;
		}

		public static SearchMode get(String title) {
			return modeMap.get(title);
		}
	}

	public enum CursorListMenu { // need reverse
		MULTI_DELETE(309, "多筆刪除"), ALL_DELETE(311, "全部刪除"), MULTI_EXPORT(313,
				"多筆匯出"), IMPORT(315, "下載路線"), NEW_TRACK(317, "錄製軌跡");

		private static final Map<Integer, CursorListMenu> menuMap = new HashMap<Integer, CursorListMenu>();
		private final int id;
		private final String title;

		static {
			for (CursorListMenu ct : EnumSet.allOf(CursorListMenu.class)) {
				menuMap.put(ct.getId(), ct);
			}
		}

		CursorListMenu(int value, String title) {
			id = value;
			this.title = title;
		}

		public int getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public static CursorListMenu get(int value) {
			return menuMap.get(value);
		}
	}

	public enum NaviSetupAction { // need reverse
		SET_ORIGIN(631, "startButton"), SET_DESTINATION(632, "endButton"), SET_VIA1(
				633, "ess1Button"), SET_VIA2(634, "ess2Button");

		private static final Map<String, NaviSetupAction> actionMap = new HashMap<String, NaviSetupAction>();
		private final int id;
		private final String title;

		static {
			for (NaviSetupAction ct : EnumSet.allOf(NaviSetupAction.class)) {
				actionMap.put(ct.getTitle(), ct);
			}
		}

		NaviSetupAction(int value, String title) {
			id = value;
			this.title = title;
		}

		public int getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public static NaviSetupAction get(String value) {
			return actionMap.get(value);
		}
	}

	public enum POIMenu { // need reverse
		ADD_FAVORITE(611, "加入最愛"), DELETE(612, "刪除"), SHARE(613, "分享"), SET_ORIGIN(
				614, "設定起點"), DRAW_MAP(615, "標示地圖"), SET_DESTINATION(616,
				"設定目的地"), SET_VIA(617, "設定經過點"), NAVIGATION(618, "前往");

		private static final Map<Integer, POIMenu> menuMap = new HashMap<Integer, POIMenu>();
		private final int id;
		private final String title;

		static {
			for (POIMenu ct : EnumSet.allOf(POIMenu.class)) {
				menuMap.put(ct.getId(), ct);
			}
		}

		POIMenu(int value, String title) {
			id = value;
			this.title = title;
		}

		public int getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public static POIMenu get(int value) {
			return menuMap.get(value);
		}
	}

	public enum TrackMenu { // need reverse
		ADD_FAVORITE(611, "加入最愛"), DELETE(612, "刪除"), SHARE(613, "分享"), SHOW(
				614, "標示軌跡"), EXPORT(615, "匯出"), SET_LOCATION(616, "設定起訖點"),SET_ORIGIN(
						617, "設定起點"),SET_DESTINATION(618,
				"設定目的地"), SET_VIA(619, "設定經過點"), NAVIGATION(620, "前往");;

		private static final Map<Integer, TrackMenu> menuMap = new HashMap<Integer, TrackMenu>();
		private final int id;
		private final String title;

		static {
			for (TrackMenu ct : EnumSet.allOf(TrackMenu.class)) {
				menuMap.put(ct.getId(), ct);
			}
		}

		TrackMenu(int value, String title) {
			id = value;
			this.title = title;
		}

		public int getId() {
			return id;
		}

		public String getTitle() {
			return title;
		}

		public static TrackMenu get(int value) {
			return menuMap.get(value);
		}
	}

	public enum City {
		TAIPEI_CITY(65, "台北市"), TAIPEI_COUNTY(70, "新北市"),KEELING_CITY(67,"基隆市"),
		TAOYUAN_COUNTY(72,"桃園縣"),HSINCHU_COUNTY(74,"新竹縣"),HSINCHU_CITY(79,"新竹市"),
		MIAOLI_COUNTY(75,"苗栗縣"),YILAN_COUNTY(71,"宜蘭縣");

		private static final Map<String, City> cityMap = new HashMap<String, City>();
		private final int value;
		private final String name;

		static {
			for (City ct : EnumSet.allOf(City.class)) {
				cityMap.put(ct.getName(), ct);
			}
		}

		City(int value, String name) {
			this.value = value;
			this.name = name;
		}

		public int getValue() {
			return value;
		}

		public String getName() {
			return name;
		}

		public static City get(String name) {
			return cityMap.get(name);
		}
	}

	public enum ContextMenuOptions {
		SET_ORIGIN(129, "設為起點"), SET_DESTINATION(131, "設為目的地"), DRAW_MAP(133,
				"標示於地圖"), SET_LOCATION(135, "設為起訖點"), DRAW_TRACK(136, "顯示軌跡"), NAVIGATION(
				137, "前往"), SET_VIA(138, "設定經過點");

		private static final Map<String, ContextMenuOptions> menuMap = new HashMap<String, ContextMenuOptions>();
		private final int value;
		private final String text;

		static {
			for (ContextMenuOptions cm : EnumSet
					.allOf(ContextMenuOptions.class)) {
				menuMap.put(cm.getTitle(), cm);
			}
		}

		ContextMenuOptions(int value, String text) {
			this.value = value;
			this.text = text;
		}

		public int getValue() {
			return value;
		}

		public String getTitle() {
			return text;
		}

		public static ContextMenuOptions get(String name) {
			return menuMap.get(name);
		}
	}
}
