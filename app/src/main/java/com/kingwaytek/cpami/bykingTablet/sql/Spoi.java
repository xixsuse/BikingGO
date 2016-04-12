package com.kingwaytek.cpami.bykingTablet.sql;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.CursorColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.POIColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.SpoiColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TableName;

public class Spoi extends POI {

	private int sId;
	private String sDescription;
	private String sTheme;
	private Map<String, String> sPhoto;
	private Context context;

	public Spoi(Context context, int spoiId) {
		super(context, TableName.Spoi);
		this.context = context;
		super.setSQLCommand("select * from " + TableName.Spoi.getName()
				+ " where " + SpoiColumn.ID.getName() + "="
				+ String.valueOf(spoiId));
		Log.i("POI", "sql command = " + super.getSQLCommand());
		Cursor curSpoi = super.QueryWithCommand();

		if (curSpoi.getCount() < 1) {
			Log.e("POI", "loading Spoi from database failed.");
			sId = -1;
			sDescription = sTheme = "";
			sPhoto = null;
			return;
		}

		sId = spoiId;
		sDescription = curSpoi.getString(SpoiColumn.DESCRIPTION.getIndex());
		sTheme = curSpoi.getString(SpoiColumn.THEME.getIndex());

		sPhoto = new HashMap<String, String>(3);
		// sPhoto.put(SpoiColumn.PHOTO_ONE.getName(), curSpoi
		// .getBlob(SpoiColumn.PHOTO_ONE.getIndex()));
		// sPhoto.put(SpoiColumn.PHOTO_TWO.getName(), curSpoi
		// .getBlob(SpoiColumn.PHOTO_TWO.getIndex()));
		// sPhoto.put(SpoiColumn.PHOTO_THREE.getName(), curSpoi
		// .getBlob(SpoiColumn.PHOTO_THREE.getIndex()));

		// need to append path and directory to front
		// ex: 3256478_

		/* 將相片改成路徑存取 */
		;

		String imgDbname = context.getString(R.string.SQLite_App_Database_Path)
				+ "/photo/" + curSpoi.getString(SpoiColumn.ID.getIndex()) + "-";

		for (int i = 1; i < 4; i++) {
			String imgFiletemp = imgDbname + i + ".jpg"; // make ex:
															// 3256478-1.jpg

			if (new File(imgFiletemp).exists()) {

				sPhoto.put(SpoiColumn.ID.getName() + i, imgFiletemp);

			}
		}

		Log.d("sPhoto.size()", "total photos = " + sPhoto.size());

		super.SetupPOI(curSpoi.getInt(SpoiColumn.POI_ID.getIndex()));
		curSpoi.close();
	}

	@Override
	public int getID() {
		return sId;
	}

	public int getPoiId() {
		return super.getID();
	}

	@Override
	public String getDescription() {
		return sDescription;
	}

	public String getPoiDescription() {
		return super.getDescription();
	}

	public String getTheme() {
		return sTheme;
	}

	public String getPhoto1Blob() {
		return sPhoto.get(SpoiColumn.ID.getName() + "1");
		// return sPhoto.get(SpoiColumn.PHOTO_ONE.getName());
	}

	public String getPhoto2Blob() {
		return sPhoto.get(SpoiColumn.ID.getName() + "2");
		// return sPhoto.get(SpoiColumn.PHOTO_TWO.getName());
	}

	public String getPhoto3Blob() {
		return sPhoto.get(SpoiColumn.ID.getName() + "3");
		// return sPhoto.get(SpoiColumn.PHOTO_THREE.getName());
	}

	public void setDescription(String desp) {
		sDescription = desp;
	}

	public void setTheme(String theme) {
		sTheme = theme;
	}

	public void setPhoto1(byte[] blob) {
		// sPhoto.put(SpoiColumn.PHOTO_ONE.getName(), blob);
	}

	public void setPhoto2(byte[] blob) {
		// sPhoto.put(SpoiColumn.PHOTO_TWO.getName(), blob);
	}

	public void setPhoto3(byte[] blob) {
		// sPhoto.put(SpoiColumn.PHOTO_THREE.getName(), blob);
	}

	public static Cursor GetCatalogList(Context context) {
		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_App_Database_Name), context
				.getString(R.string.SQLite_App_Database_Path), TableName.Spoi);

		sqliteDatabase.setSQLCommand("select distinct "// (substr("
				+ SpoiColumn.THEME.getName() // + ",1,6)) "
				// + SpoiColumn.THEME.getName()
				+ ",'' " + CursorColumn.ID.get()
				+ " from "
				+ TableName.Spoi.getName());

		Log.i("POI", "sql command = " + sqliteDatabase.getSQLCommand());

		return sqliteDatabase.QueryWithCommand();
	}
	
	public static Cursor GetThemeList(Context context , String catalog) {
		// TODO: rewrite theme query
		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_App_Database_Name), context
				.getString(R.string.SQLite_App_Database_Path), TableName.Spoi);

		sqliteDatabase.setSQLCommand("select distinct "// (substr("
				+ SpoiColumn.THEME.getName() // + ",1,6)) "
				// + SpoiColumn.THEME.getName()
				+ ",'' " + CursorColumn.ID.get()
				+ " from "
				+ TableName.Spoi.getName()
				+ " where "
				+ SpoiColumn.THEME.getName()
				+ " like " 
				+ "'%"+catalog+"%'");

		// sqliteDatabase.setSQLCommand("select distinct("
		// + SpoiColumn.THEME.getName() + ") "
		// + SpoiColumn.THEME.getName() + ", 0 " + CursorColumn.ID.get()
		// + " from " + TableName.Spoi.getName() + " order by "
		// + SpoiColumn.THEME.getName());
		Log.i("POI", "sql command = " + sqliteDatabase.getSQLCommand());

		return sqliteDatabase.QueryWithCommand();
	}

	public static Cursor Search(Context context, GeoPoint point, String theme) {
		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_App_Database_Name), context
				.getString(R.string.SQLite_App_Database_Path), TableName.Spoi);

		// TODO radius should get from sharedpreference Geopoint shoudl get from
		// GPS and converted to tm97
		double radius = 1000;
		double eNorth = point.getTmY() + radius;
		double eEast = point.getTmX() + radius;
		double eSouth = point.getTmY() - radius;
		double eWest = point.getTmX() - radius;

		// 此為界定範圍搜尋
		// String sqlCommand = "select " + SpoiColumn.ID.getName() + " "
		// + CursorColumn.ID.get() + "," + POIColumn.NAME.getName() + ","
		// + POIColumn.ADDRESS.getName() + "," + POIColumn.TMX.getName()
		// + "," + POIColumn.TMY.getName() + "," + "(abs("
		// + point.getTmX() + "-" + POIColumn.TMX.getName() + ")*abs("
		// + point.getTmX() + "-" + POIColumn.TMX.getName() + ")) + "
		// + "(abs(" + point.getTmY() + "-" + POIColumn.TMY.getName()
		// + ")*abs(" + point.getTmY() + "-" + POIColumn.TMY.getName()
		// + ")) destsqr" + " from " + TableName.Spoi.getName()
		// + " inner join " + TableName.POI.getName() + " on " + ""
		// + SpoiColumn.POI_ID.getName() + "=" + POIColumn.ID.getName()
		// + " and ((" + POIColumn.TMX.getName() + ">=" + eWest + " and "
		// + POIColumn.TMX.getName() + "<=" + eEast + " and "
		// + POIColumn.TMY.getName() + ">=" + eSouth + " and "
		// + POIColumn.TMY.getName() + "<=" + eNorth + ") and "
		// + SpoiColumn.THEME.getName() + " like" + " '" + theme
		// + "%') and destsqr<=" + (radius * radius) + " order by destsqr";

		// 更改為不界定範圍
		String sqlCommand = "select " + SpoiColumn.ID.getName() + " "
				+ CursorColumn.ID.get() + "," + POIColumn.NAME.getName() + ","
				+ POIColumn.ADDRESS.getName() + "," + POIColumn.TMX.getName()
				+ "," + POIColumn.TMY.getName() + "," + "(abs("
				+ point.getTmX() + "-" + POIColumn.TMX.getName() + ")*abs("
				+ point.getTmX() + "-" + POIColumn.TMX.getName() + ")) + "
				+ "(abs(" + point.getTmY() + "-" + POIColumn.TMY.getName()
				+ ")*abs(" + point.getTmY() + "-" + POIColumn.TMY.getName()
				+ ")) destsqr" +" , "
				+ POIColumn.SUB_BRANCH.getName()
				+ " from " + TableName.Spoi.getName()
				+ " inner join " + TableName.POI.getName() + " on " + ""
				+ SpoiColumn.POI_ID.getName() + "=" + POIColumn.ID.getName()
				+ " and " + SpoiColumn.THEME.getName() + " like" + " '" + theme
				+ "%' order by destsqr";

		Log.i("Favorite", "sqlCommand : " + sqlCommand);
		sqliteDatabase.setSQLCommand(sqlCommand);
		return sqliteDatabase.QueryWithCommand();
	}

	// private static String getColumnStatement() {
	// return "select " + SpoiColumn.ID.getName() + " "
	// + CursorColumn.ID.get() + ","
	// + SpoiColumn.DESCRIPTION.getName() + ","
	// + SpoiColumn.PHOTO_ONE.getName() + ","
	// + SpoiColumn.PHOTO_TWO.getName() + ","
	// + SpoiColumn.PHOTO_THREE.getName() + ","
	// + SpoiColumn.POI_ID.getName() + ","
	// + SpoiColumn.THEME.getName() + " from "
	// + TableName.Spoi.getName();
	// }
}
