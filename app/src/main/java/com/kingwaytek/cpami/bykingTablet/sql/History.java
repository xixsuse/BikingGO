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
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.HistoryColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TableName;

public class History extends SQLiteBot {

	/** history ID */
	private int hID; // h_id:integer primary key autoincrement

	/** history Name */
	private String hName; // h_name:text not null

	/** history Type */
	private int hType; // h_type:inteter not null 0=undefined, 1=POI, 2=route

	/** history Item ID */
	private int hItemID; // h_item:integer not null, forign key to item table

	/** datetime of creation */
	private Date hDate; // h_datetime:timestamp not null

	// default (datetime('now','localtime'))

	// private SQLiteBot historyBot;
	//
	// /** application SQLite database name */
	// protected static String dataBaseName; // database name
	//
	// /** application database path */
	// protected static String dataBasePath; // database path

	/**
	 * Create an instance of History
	 * 
	 * @param context
	 *            current Context
	 */
	public History(Context context) {
		super(context.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path), TableName.History);
		// historyBot = new SQLiteBot(context, context
		// .getString(R.string.SQLite_Database_Name), TableName.History);
		// historyBot.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		//
		// dataBasePath = context.getString(R.string.SQLite_Database_Path);
		// dataBaseName = context.getString(R.string.SQLite_Database_Name);

		ResetHistory();
	}

	/**
	 * Create an instance of History which load from database according to
	 * historyID provided
	 * 
	 * @param context
	 *            current Context
	 * @param historyID
	 *            history ID to load History
	 * @throws Exception
	 */
	public History(Context context, Integer historyID) {
		super(context.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path), TableName.History);
		// historyBot = new SQLiteBot(context, context
		// .getString(R.string.SQLite_Database_Name), TableName.History);
		// historyBot.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		//
		// dataBasePath = context.getString(R.string.SQLite_Database_Path);
		// dataBaseName = context.getString(R.string.SQLite_Database_Name);

		super.setSQLCommand("select * from " + TableName.History.getName()
				+ " where " + HistoryColumn.ID.getName() + "=" + historyID);

		Log.i("History", "sql command = " + super.getSQLCommand());
		Cursor curHistory = super.QueryWithCommand();
		Log.i("History", "result count = " + curHistory.getCount());
		if (curHistory.getCount() != 1) {
			Log.w("History", "history id is not found. load empty instance.");
			ResetHistory();
			curHistory.close();
			return;
		}

		hID = curHistory.getInt(0);
		hName = curHistory.getString(1);
		hType = curHistory.getInt(2);
		hItemID = curHistory.getInt(3);

		try {
			hDate = SqliteConstant.ISO8601_DATE_FORMAT.parse(curHistory
					.getString(4));
		} catch (ParseException e) {
			hDate = null;
			e.printStackTrace();
		}
		curHistory.close();
	}

	private void ResetHistory() {
		hID = -1;
		hName = null;
		hType = -1;
		hItemID = -1;
		hDate = null;
	}

	/**
	 * get history ID
	 * 
	 * @return history ID
	 */
	public int getID() {
		return hID;
	}

	/**
	 * get history Name
	 * 
	 * @return history Name
	 */
	public String getName() {
		return hName;
	}

	/**
	 * set history Name
	 * 
	 * @param name
	 *            name to set
	 */
	public void setName(String name) {
		hName = name;
	}

	/**
	 * get history Type
	 * 
	 * @return history Type 0=undefined, 1=POI, 2=route
	 */
	public int getType() {
		return hType;
	}

	/**
	 * set history Type
	 * 
	 * @param type
	 *            type to set 0=undefined, 1=POI, 2=route
	 */
	public void setType(int type) {
		hType = type;
	}

	/**
	 * get history Item ID
	 * 
	 * @return history Item ID
	 */
	public int getItemID() {
		return hItemID;
	}

	/**
	 * set history Item ID
	 * 
	 * @param itemID
	 *            ID to set
	 */
	public void setItemID(int itemID) {
		hItemID = itemID;
	}

	/**
	 * get history creation datetime
	 * 
	 * @return history creation datetime
	 */
	public Date getDate() {
		return hDate;
	}

	// main Methods

	/**
	 * prepare ContenValues Map for query, insert, delete, or update.
	 * 
	 * @return ContenValues Map
	 */
	private ContentValues getValueMap() {
		ContentValues valueMap = new ContentValues(3);

		valueMap.put(HistoryColumn.NAME.getName(), hName);
		valueMap.put(HistoryColumn.TYPE.getName(), hType);
		valueMap.put(HistoryColumn.ITEM.getName(), hItemID);

		return valueMap;
	}

	public Cursor getHistoryList(ContentType type, String search) {
		String sqlCommand = "select "
				+ HistoryColumn.ID.getName()
				+ " "
				+ CursorColumn.ID.get()
				+ ","
				+ HistoryColumn.NAME.getName()
				+ ","
				+ HistoryColumn.TYPE.getName()
				+ ","
				+ HistoryColumn.ITEM.getName()
				+ " from "
				+ TableName.History.getName()
				+ " where "
				+ HistoryColumn.TYPE.getName()
				+ " = "
				+ type.getValue()
				+ ((search == null || search.length() == 0) ? "" : " and "
						+ HistoryColumn.NAME.getName() + " like '%" + search
						+ "%'") + " order by " + HistoryColumn.DATE.getName()
				+ " desc";

		super.setSQLCommand(sqlCommand);
		return super.QueryWithCommand();
	}

	public boolean isItemInList() throws Exception {
		if (hItemID <= 0)
			throw new Exception("No item to varify.");
		String sqlCommand = "select " + HistoryColumn.ID.getName() + " from "
				+ TableName.History.getName() + " where "
				+ HistoryColumn.ITEM.getName() + " = " + hItemID;

		super.setSQLCommand(sqlCommand);

		Cursor curResult = super.QueryWithCommand();
		int itemCount = curResult.getCount();

		curResult.close();
		return itemCount > 0;
	}

	/**
	 * Put history to database
	 * 
	 * @return long indicate add result
	 * @throws SQLException
	 */
	public long Put() throws SQLException {
		long addResult;
		addResult = super.Insert(getValueMap());
		hID = (int) addResult;

		return addResult;
	}

	/**
	 * Delete history from database according to history ID provided
	 * 
	 * @param historyID
	 *            history ID to delete
	 * @return int indicate delete result
	 * @throws SQLException
	 */
	public static int Remove(Context context, int historyID)
			throws SQLException {
		if (historyID <= 0)
			throw new IllegalArgumentException("historyID is invalid.");

		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path), TableName.History);

		sqliteDatabase.setDatabasePath(context
				.getString(R.string.SQLite_Usr_Database_Path));
		int delResult = sqliteDatabase.Delete(
				HistoryColumn.ID.getName() + "=?", new String[] { String
						.valueOf(historyID) });

		return delResult;
	}

	/**
	 * Delete history from database according to history IDs or name provided
	 * 
	 * @param params
	 *            history IDs or name to delete
	 * @return int indicate delete result
	 * @throws SQLException
	 */
	public static void Remove(Context context, String params)
			throws SQLException {
		if (params == null)
			throw new IllegalArgumentException("argument is invalid.");

		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path), TableName.History);

		// sqliteDatabase.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		sqliteDatabase.setSQLCommand("delete from "
				+ TableName.History.getName() + " where "
				+ HistoryColumn.ID.getName() + " in("
				+ (params.length() == 0 ? "" : params) + ")");

		Log.i("History", "command = " + sqliteDatabase.getSQLCommand());
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
				.getString(R.string.SQLite_Usr_Database_Path), TableName.History);
	
		sqliteDatabase.setSQLCommand("delete from "
				+ TableName.History.getName() + " where "
				+ HistoryColumn.ITEM.getName() + " in("
				+ (params.length() == 0 ? "" : params) + ")");
		
		Log.i("History", "command = " + sqliteDatabase.getSQLCommand());
		sqliteDatabase.AlterWithCommand();
		
		return;
	}

	/**
	 * Update history from database
	 * 
	 * @return int indicate update result
	 * @throws SQLException
	 */
	public int Update() throws SQLException {
		if (hID <= 0)
			throw new SQLException("update index is invalid.");

		int updResult;
		updResult = super.Update(getValueMap(), HistoryColumn.ID.getName()
				+ "=?", new String[] { String.valueOf(hID) });

		return updResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return hID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof History))
			return false;

		History other = (History) obj;

		if (hID == other.hID && hName.equals(other.hName)
				&& hType == other.hType && hItemID == other.hItemID
				&& hDate.equals(other.hDate))
			return true;

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "History [" + "ID=" + hID + ", Name=" + hName + ", Type="
				+ ContentType.get(hType) + ", ItemID=" + hItemID + ", Date="
				+ hDate.toString() + "]";
	}
}
