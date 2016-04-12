package com.kingwaytek.cpami.bykingTablet.sql;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.PreferenceActivity;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.CursorColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.POICategory;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.POIColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.POIKindColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TableName;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TrackColumn;

public class POI extends SQLiteBot {

	private int pId;
	private String pName;
	private String pShortName;
	private String pEnglishName;
	private String pSubBranch;
	private String pAddress;
	private String pTel;
	private String pTownCode;
	private String pKind;
	private String pFax;
	private String pZipCode;
	private String pWebSite;
	private String pTime;
	private String pCard;
	private String pContact;
	private String pBusinese;
	private String pAnnotation;
	private GeoPoint gpPoi;

	protected POI(Context context, TableName dbTable) {
		super(context.getString(R.string.SQLite_App_Database_Name), context
				.getString(R.string.SQLite_App_Database_Path),
				(dbTable == null ? TableName.POI : dbTable));
		ResetPOI();
	}

	public POI(Context context, int poiId) {
		super(context.getString(R.string.SQLite_App_Database_Name), context
				.getString(R.string.SQLite_App_Database_Path), TableName.POI);
		SetupPOI(poiId);
	}

	public int getID() {
		return pId;
	}

	public String getName() {
		return pName;
	}

	public String getShortName() {
		return pShortName;
	}

	public String getEnglishName() {
		return pEnglishName;
	}

	public String getSubBranch() {
		return pSubBranch;
	}

	public String getAddress() {
		return pAddress;
	}

	public String getTelNumber() {
		return pTel;
	}

	public String getTownCode() {
		return pTownCode;
	}

	public String getCategory() {
		return pKind;
	}

	public String getFaxNumber() {
		return pFax;
	}

	public String getZipCode() {
		return pZipCode;
	}

	public String getWebSite() {
		return pWebSite;
	}

	public String getBusineseHour() {
		return pTime;
	}

	public String getCreditCard() {
		return pCard;
	}

	public String getContact() {
		return pContact;
	}

	public String getDescription() {
		return pBusinese;
	}

	public String getAnnotation() {
		return pAnnotation;
	}

	public GeoPoint getPOIPoint() {
		return gpPoi;
	}

	protected void SetupPOI(int poiId) {
		super.setSQLCommand("select * from " + TableName.POI.getName()
				+ " where " + POIColumn.ID.getName() + "="
				+ String.valueOf(poiId));
		Log.i("POI", "sql command = " + super.getSQLCommand());
		Cursor curPOI = super.QueryWithCommand();

		if (curPOI.getCount() != 1) {
			// throw new Exception("loading poi from database failed.");
			Log.e("POI", "loading poi from database failed.");
			ResetPOI();
			return;
		}

		pId = poiId;
		pName = curPOI.getString(POIColumn.NAME.getIndex());
		pShortName = curPOI.getString(POIColumn.SHORT_NAME.getIndex());
		pEnglishName = curPOI.getString(POIColumn.ENGLISH_NAME.getIndex());
		pSubBranch = curPOI.getString(POIColumn.SUB_BRANCH.getIndex());
		pAddress = curPOI.getString(POIColumn.ADDRESS.getIndex());
		pTel = curPOI.getString(POIColumn.TELPHONE.getIndex());
		pTownCode = curPOI.getString(POIColumn.TOWN_CODE.getIndex());
		pKind = curPOI.getString(POIColumn.CATEGORY.getIndex());
		pFax = curPOI.getString(POIColumn.FAX.getIndex());
		pZipCode = curPOI.getString(POIColumn.ZIP_CODE.getIndex());
		pWebSite = curPOI.getString(POIColumn.WEB_SITE.getIndex());
		pTime = curPOI.getString(POIColumn.BUSINESE_HOUR.getIndex());
		pCard = curPOI.getString(POIColumn.CREDIT_CARD.getIndex());
		pContact = curPOI.getString(POIColumn.CONTACT.getIndex());
		pBusinese = curPOI.getString(POIColumn.BUSINESE_MODEL.getIndex());
		pAnnotation = curPOI.getString(POIColumn.ANNOTATION.getIndex());
		gpPoi = new GeoPoint(curPOI.getDouble(POIColumn.LONGITUDE.getIndex()),
				curPOI.getDouble(POIColumn.LATITUDE.getIndex()));

		curPOI.close();
	}

	protected void ResetPOI() {
		pId = -1;
		pName = pShortName = pEnglishName = pSubBranch = pAddress = pTel = pTownCode = pKind = "";
		pFax = pZipCode = pWebSite = pTime = pCard = pContact = pBusinese = pAnnotation = "";
		gpPoi = null;
	}

	public static Cursor GetCategoryList(Context context) {
		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_App_Database_Name), context
				.getString(R.string.SQLite_App_Database_Path), TableName.POI);

		// String sqlCommand = "select " + POIColumn.CATEGORY.getName() +
		// ", '' "
		// + CursorColumn.ID.get() + " from " + "(select '"
		// + POICategory.values()[0].getName() + "' "
		// + POIColumn.CATEGORY.getName() + " union select '";
		//
		// for (int i = 1; i < POICategory.values().length - 1; i++) {
		// sqlCommand += POICategory.values()[i].getName()
		// + "' union select '";
		// }
		//
		// sqlCommand += POICategory.values()[POICategory.values().length - 1]
		// .getName()
		// + "') order by " + POIColumn.CATEGORY.getName();

		String sqlCommand = "select " + POIKindColumn.NAME.getName() + ", "
				+ POIKindColumn.ID.getName() + " " + CursorColumn.ID.get()
				+ " from " + TableName.PoiKind.getName();

		sqliteDatabase.setSQLCommand(sqlCommand);

		Log.i("POI", "sql command = " + sqliteDatabase.getSQLCommand());

		return sqliteDatabase.QueryWithCommand();
	}

	/**
	 * count pois in given city from result of keyword search
	 * 
	 * @param keyword
	 * @param citycode
	 * @return count
	 */
	public static int SearchCount(Context context, String keyword,
			String citycode) {
		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_App_Database_Name), context
				.getString(R.string.SQLite_App_Database_Path), TableName.POI);

		sqliteDatabase.setSQLCommand("select count(*) count from "
				+ TableName.POI.getName() + " where "
				+ POIColumn.TOWN_CODE.getName() + " like '" + citycode
				+ "%' and " + POIColumn.NAME.getName() + " like '%" + keyword
				+ "%'");
		Log.i("POI", "sql command = " + sqliteDatabase.getSQLCommand());

		Cursor curCount = sqliteDatabase.QueryWithCommand();
		if (curCount.getCount() == 0) {
			Log.e("POI", "query database for poi failed.");
			return -1;
		}
		int result = curCount.getInt(0);
		curCount.close();

		return result;
	}

	/**
	 * search pois in given city by keyword
	 * 
	 * @param keyword
	 * @return
	 */
	public static Cursor Search(Context context, String keyword, String citycode) {
		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_App_Database_Name), context
				.getString(R.string.SQLite_App_Database_Path), TableName.POI);

		String sqlCommand = "select " + POIColumn.ID.getName() + " "
				+ CursorColumn.ID.get() + "," + POIColumn.NAME.getName() + ","
				+ POIColumn.ADDRESS.getName() + ","
				+ POIColumn.TOWN_CODE.getName() + " , " 
				+ POIColumn.SUB_BRANCH.getName() 
				+ " from "
				+ TableName.POI.getName() + " where "
				+ POIColumn.TOWN_CODE.getName() + " like '" + citycode
				+ "%' and " + POIColumn.NAME.getName() + " like '%" + keyword
				+ "%'" + " order by " + POIColumn.TOWN_CODE.getName() + ", "
				+ POIColumn.NAME.getName();

		Log.i("Favorite", "sqlCommand : " + sqlCommand);
		sqliteDatabase.setSQLCommand(sqlCommand);
		return sqliteDatabase.QueryWithCommand();
	}

	/**
	 * search pois around given point by category
	 * 
	 * @param gpPoi
	 * @param category
	 * @return
	 */
	public static Cursor Search(Context context, GeoPoint point, String category) {
		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_App_Database_Name), context
				.getString(R.string.SQLite_App_Database_Path), TableName.POI);

		// TODO radius should get from sharedpreference Geopoint shoudl get from
		// GPS and converted to tm97
		Log.i("POI.java", "PreferenceActivity.getSurroundRange(context)="
				+ PreferenceActivity.getSurroundRange(context));
		double radius = Double.parseDouble(PreferenceActivity
				.getSurroundRange(context));// 500;
		double eNorth = point.getTmY() + radius;
		double eEast = point.getTmX() + radius;
		double eSouth = point.getTmY() - radius;
		double eWest = point.getTmX() - radius;
		
		
		
	    String sqlCommand="";
        if(PreferenceActivity.isPOIEnabled(context).equalsIgnoreCase("true")){
		// TODO ????秋播??堊垓?????輯撒????
		 sqlCommand = "select " + POIColumn.ID.getName() + " "
		   + CursorColumn.ID.get() + "," + POIColumn.NAME.getName() + ","
		   + POIColumn.ADDRESS.getName() + "," + POIColumn.TMX.getName()
		   + "," + POIColumn.TMY.getName() + "," + "(abs("
		   + point.getTmX() + "-" + POIColumn.TMX.getName() + ")*abs("
	       + point.getTmX() + "-" + POIColumn.TMX.getName() + ")) + "
	       + "(abs(" + point.getTmY() + "-" + POIColumn.TMY.getName()
		   + ")*abs(" + point.getTmY() + "-" + POIColumn.TMY.getName()
		   + ")) destsqr" +" , " 
		   + POIColumn.SUB_BRANCH.getName()
		   + " from " + TableName.POI.getName() + " where "
		   + "((" + POIColumn.TMX.getName() + ">=" + eWest + " and "
		   + POIColumn.TMX.getName() + "<=" + eEast + " and "
		   + POIColumn.TMY.getName() + ">=" + eSouth + " and "
		   + POIColumn.TMY.getName() + "<=" + eNorth + ") and "
		   + POIColumn.CATEGORY.getName() + " like" + " 'poi_" + category
		   + "%') and destsqr<=" + radius * radius + " order by destsqr Limit 10000";
        }else{
		// ????堊垓?????謚????鈭?瘝I
		 sqlCommand = "select " + POIColumn.ID.getName() + " "
				+ CursorColumn.ID.get() + "," + POIColumn.NAME.getName() + ","
				+ POIColumn.ADDRESS.getName() + "," + POIColumn.TMX.getName()
				+ "," + POIColumn.TMY.getName() + "," + "(abs("
				+ point.getTmX() + "-" + POIColumn.TMX.getName() + ")*abs("
				+ point.getTmX() + "-" + POIColumn.TMX.getName() + ")) + "
				+ "(abs(" + point.getTmY() + "-" + POIColumn.TMY.getName()
				+ ")*abs(" + point.getTmY() + "-" + POIColumn.TMY.getName()
				+ ")) destsqr" +" , " 
				+ POIColumn.SUB_BRANCH.getName()
				+ " from " + TableName.POI.getName() + " where "
				+ POIColumn.CATEGORY.getName() + " like" + " 'poi_" + category
				+ "%'order by destsqr Limit 1000";
       }
		Log.i("Favorite", "sqlCommand : " + sqlCommand);
		sqliteDatabase.setSQLCommand(sqlCommand);
		return sqliteDatabase.QueryWithCommand();
	}
	
	

	// private static String getColumnStatement() {
	// return "select " + POIColumn.ID.getName() + " " + CursorColumn.ID.get()
	// + "," + POIColumn.NAME.getName() + ","
	// + POIColumn.SHORT_NAME.getName() + ","
	// + POIColumn.ENGLISH_NAME.getName() + ","
	// + POIColumn.SUB_BRANCH.getName() + ","
	// + POIColumn.ADDRESS.getName() + ","
	// + POIColumn.TELPHONE.getName() + ","
	// + POIColumn.TOWN_CODE.getName() + ","
	// + POIColumn.CATEGORY.getName() + "," + POIColumn.FAX.getName()
	// + "," + POIColumn.ZIP_CODE.getName() + ","
	// + POIColumn.WEB_SITE.getName() + ","
	// + POIColumn.BUSINESE_HOUR.getName() + ","
	// + POIColumn.CREDIT_CARD.getName() + ","
	// + POIColumn.CONTACT.getName() + ","
	// + POIColumn.BUSINESE_MODEL.getName() + ","
	// + POIColumn.ANNOTATION.getName() + ","
	// + POIColumn.LONGITUDE.getName() + ","
	// + POIColumn.LATITUDE.getName() + " from "
	// + TableName.POI.getName();
	// }

	// ?鈭亙眺poi_id??秋????祗table poi??
	public static boolean isItemInList(Context context, int poi_id)
			throws Exception {
		if (poi_id <= 0)
			throw new Exception("No item to varify.");
		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_App_Database_Name), context
				.getString(R.string.SQLite_App_Database_Path), TableName.POI);

		String sqlCommand = "select * from " + TableName.POI.getName()
				+ " where " + POIColumn.ID.getName() + "=" + poi_id;

		sqliteDatabase.setSQLCommand(sqlCommand);
		Cursor curResult = sqliteDatabase.QueryWithCommand();
		int itemCount = curResult.getCount();

		curResult.close();
		return itemCount > 0;
	}
}
