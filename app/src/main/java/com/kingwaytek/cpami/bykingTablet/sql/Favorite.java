package com.kingwaytek.cpami.bykingTablet.sql;

import java.text.ParseException;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.ContentType;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.CursorColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.FavoriteColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.HistoryColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TableName;

public class Favorite extends SQLiteBot {

	/** favorite ID */
	private int fID; // f_id:integer primary key autoincrement

	/** favorite Name */
	private String fName; // f_name:text not null

	/** favorite Type */
	private int fType; // f_type:inteter not null 0=undefined, 1=POI, 2=route

	/** favorite Item ID */
	private int fItemID; // f_item:integer not null, forign key to item table

	/** datetime of creation */
	private Date fDate; // f_datetime:timestamp not null

	// default (datetime('now','localtime'))
	// private SQLiteBot favoriteBot;

	// /** application SQLite database name */
	// protected static String dataBaseName; // database name
	//
	// /** application database path */
	// protected static String dataBasePath; // database path

	/**
	 * Create an instance of Favorite
	 * 
	 * @param context
	 *            current Context
	 */
	public Favorite(Context context) {
		super(context.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path), TableName.Favorite);
		// favoriteBot = new SQLiteBot(context, context
		// .getString(R.string.SQLite_Database_Name), TableName.Favorite);
		// favoriteBot.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		//
		// dataBasePath = context.getString(R.string.SQLite_Database_Path);
		// dataBaseName = context.getString(R.string.SQLite_Database_Name);

		ResetFavorite();
	}

	/**
	 * Create an instance of Favorite which load from database according to
	 * favoriteID provided
	 * 
	 * @param context
	 *            current Context
	 * @param favoriteID
	 *            favorite ID to load Favorite
	 * @throws Exception
	 */
	public Favorite(Context context, Integer favoriteID) {
		super(context.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path), TableName.Favorite);
		// super.setDatabasePath(context.getString(R.string.SQLite_Database_Path));
		// favoriteBot = new SQLiteBot(context, context
		// .getString(R.string.SQLite_Database_Name), TableName.Favorite);
		// favoriteBot.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		// // TODO Auto-generated constructor stub
		//
		// dataBasePath = context.getString(R.string.SQLite_Database_Path);
		// dataBaseName = context.getString(R.string.SQLite_Database_Name);

		super.setSQLCommand("select * from " + TableName.Favorite.getName()
				+ " where " + FavoriteColumn.ID.getName() + "=" + favoriteID);

		Log.i("Favorite", "sql command = " + super.getSQLCommand());
		Cursor curFavorite = super.QueryWithCommand();
		Log.i("Favorite", "result count = " + curFavorite.getCount());
		if (curFavorite.getCount() != 1) {
			Log.w("Favorite", "favorite id is not found. load empty instance.");
			ResetFavorite();
			curFavorite.close();
			return;
		}

		fID = curFavorite.getInt(0);
		fName = curFavorite.getString(1);
		fType = curFavorite.getInt(2);
		fItemID = curFavorite.getInt(3);

		try {
			fDate = SqliteConstant.ISO8601_DATE_FORMAT.parse(curFavorite
					.getString(4));
		} catch (ParseException e) {
			fDate = null;
			e.printStackTrace();
		}
		curFavorite.close();
	}

	private void ResetFavorite() {
		fID = -1;
		fName = null;
		fType = -1;
		fItemID = -1;
		fDate = null;
	}

	/**
	 * get favorite ID
	 * 
	 * @return favorite ID
	 */
	public int getID() {
		return fID;
	}

	/**
	 * get favorite Name
	 * 
	 * @return favorite Name
	 */
	public String getName() {
		return fName;
	}

	/**
	 * set favorite Name
	 * 
	 * @param name
	 *            name to set
	 */
	public void setName(String name) {
		fName = name;
	}

	/**
	 * get favorite Type
	 * 
	 * @return favorite Type 0=undefined, 1=POI, 2=route
	 */
	public int getType() {
		return fType;
	}

	/**
	 * set favorite Type
	 * 
	 * @param type
	 *            type to set 0=undefined, 1=POI, 2=route
	 */
	public void setType(int type) {
		fType = type;
	}

	/**
	 * get favorite Item ID
	 * 
	 * @return favorite Item ID
	 */
	public int getItemID() {
		return fItemID;
	}

	/**
	 * set favorite Item ID
	 * 
	 * @param itemID
	 *            ID to set
	 */
	public void setItemID(int itemID) {
		fItemID = itemID;
	}

	/**
	 * get favorite creation datetime
	 * 
	 * @return favorite creation datetime
	 */
	public Date getDate() {
		return fDate;
	}

	// main Methods

	/**
	 * prepare ContenValues Map for query, insert, delete, or update.
	 * 
	 * @return ContenValues Map
	 */
	private ContentValues getValueMap() {
		ContentValues valueMap = new ContentValues(3);

		valueMap.put(FavoriteColumn.NAME.getName(), fName);
		valueMap.put(FavoriteColumn.TYPE.getName(), fType);
		valueMap.put(FavoriteColumn.ITEM.getName(), fItemID);

		return valueMap;
	}

	public Cursor getFavoriteList(ContentType type, String search) {
		String sqlCommand = "select "
				+ FavoriteColumn.ID.getName()
				+ " "
				+ CursorColumn.ID.get()
				+ ","
				+ FavoriteColumn.NAME.getName()
				+ ","
				+ FavoriteColumn.TYPE.getName()
				+ ","
				+ FavoriteColumn.ITEM.getName()
				+ " from "
				+ TableName.Favorite.getName()
				+ " where "
				+ FavoriteColumn.TYPE.getName()
				+ " = "
				+ type.getValue()
				+ ((search == null || search.length() == 0) ? "" : " and "
						+ FavoriteColumn.NAME.getName() + " like '%" + search
						+ "%'") + " order by " + FavoriteColumn.DATE.getName()
				+ " desc";

		Log.i("Favorite", "sqlCommand : " + sqlCommand);
		super.setSQLCommand(sqlCommand);
		return super.QueryWithCommand();
	}

	public boolean isItemInList() throws Exception {
		if (fItemID <= 0)
			throw new Exception("No item to varify.");
		String sqlCommand = "select " + FavoriteColumn.ID.getName() + " from "
				+ TableName.Favorite.getName() + " where "
				+ FavoriteColumn.ITEM.getName() + "=" + fItemID + " and "
				+ FavoriteColumn.TYPE.getName() + "=" + fType;

		super.setSQLCommand(sqlCommand);

		Cursor curResult = super.QueryWithCommand();
		int itemCount = curResult.getCount();

		curResult.close();
		return itemCount > 0;
	}

	/**
	 * Add favorite to database
	 * 
	 * @return long indicate add result
	 * @throws SQLException
	 */
	public long Add() throws SQLException {
		long addResult;
		addResult = super.Insert(getValueMap());
		fID = (int) addResult;

		return addResult;
	}

	/**
	 * Delete favorite from database according to favorite ID provided
	 * 
	 * @param favoriteID
	 *            favorite ID to delete
	 * @return int indicate delete result
	 * @throws SQLException
	 */
	public static int Remove(Context context, int favoriteID)
			throws SQLException {
		if (favoriteID <= 0)
			throw new IllegalArgumentException("favoriteID is invalid.");

		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path), TableName.Favorite);

		// sqliteDatabase.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		int delResult = sqliteDatabase.Delete(FavoriteColumn.ID.getName()
				+ "=?", new String[] { String.valueOf(favoriteID) });

		return delResult;
	}

	/**
	 * Delete favorite from database according to favorite IDs or name provided
	 * 
	 * @param params
	 *            favorite IDs or name to delete
	 * @return int indicate delete result
	 * @throws SQLException
	 */
	public static void Remove(Context context, String params)
			throws SQLException {
		if (params == null)
			throw new IllegalArgumentException("argument is invalid.");

		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path), TableName.Favorite);

		// sqliteDatabase.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		sqliteDatabase.setSQLCommand("delete from "
				+ TableName.Favorite.getName() + " where "
				+ FavoriteColumn.ID.getName() + " in("
				+ (params.length() == 0 ? "" : params) + ")");

		Log.i("Favorite", "command = " + sqliteDatabase.getSQLCommand());
		sqliteDatabase.AlterWithCommand();

		return;
	}
	
	//?航???璆??ack_id?殉????撖??殉????賂貉?擏?
	public static void RemoveTracks(Context context, String params)
	throws SQLException {
		if (params == null)
			throw new IllegalArgumentException("argument is invalid.");
		
		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path), TableName.Favorite);
	
		sqliteDatabase.setSQLCommand("delete from "
				+ TableName.Favorite.getName() + " where "
				+ FavoriteColumn.ITEM.getName() + " in("
				+ (params.length() == 0 ? "" : params) + ")");
		
		Log.i("Favorite", "command = " + sqliteDatabase.getSQLCommand());
		sqliteDatabase.AlterWithCommand();
		
		return;
	}

	/**
	 * Update favorite from database
	 * 
	 * @return int indicate update result
	 * @throws SQLException
	 */
	public int Update() throws SQLException {
		if (fID <= 0){
			return 0;
		}
//			throw new SQLException("update index is invalid.");

		int updResult;
		updResult = super.Update(getValueMap(), FavoriteColumn.ID.getName()
				+ "=?", new String[] { String.valueOf(fID) });

		return updResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return fID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Favorite))
			return false;

		Favorite other = (Favorite) obj;

		if (fID == other.fID && fName.equals(other.fName)
				&& fType == other.fType && fItemID == other.fItemID
				&& fDate.equals(other.fDate))
			return true;

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Favorite [" + "ID=" + fID + ", Name=" + fName + ", Type="
				+ ContentType.get(fType) + ", ItemID=" + fItemID + ", Date="
				+ fDate.toString() + "]";
	}
}
