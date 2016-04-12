package com.kingwaytek.cpami.bykingTablet.sql;

import java.util.Calendar;
import java.util.Date;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.CursorColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TableName;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TrackPointsColumn;

public class TrackPoint extends SQLiteBot {

	private int pointID; // tpt_track_id
	private double pointLon; // tpt_longitude
	private double pointLat; // tpt_latitude
	private double pointAlt; // tpt_altitude
	private Date pointDate; // tpt_date
	private int pointType; // tpt_type

	// private SQLiteBot pointBot;
	//
	// protected static String dataBaseName; // database name
	// protected static String dataBasePath; // database path

	/** Constructors */
	public TrackPoint(Context context) {
		super(context.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path),
				TableName.TrackPoints);

		pointID = -1;
		pointLon = 0;
		pointLat = 0;
		pointAlt = 0;
		pointDate = null;
		pointType = -1;
	}

	public TrackPoint(Context context, Cursor point) throws Exception {
		super(context.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path),
				TableName.TrackPoints);

		Log.i("TrackPoint", "result count = " + point.getCount()
				+ ", position=" + point.getPosition());

		pointID = point.getInt(0);
		pointLon = point.getDouble(1);
		pointLat = point.getDouble(2);
		pointAlt = point.getDouble(3);
		pointDate = SqliteConstant.ISO8601_DATE_FORMAT
				.parse(point.getString(4));
		pointType = point.getInt(5);
		// do not close cursor here, caller will do the job.
	}

	/**
	 * Accessor & Modifier Area
	 */
	public int getID() {
		return pointID;
	}

	public double getLongitude() {
		return pointLon;
	}

	public double getLatitude() {
		return pointLat;
	}

	public double getAltitude() {
		return pointAlt;
	}

	public Date getDate() {
		return pointDate;
	}

	public int getType() {
		return pointType;
	}

	public void setID(int id) {
		pointID = id;
	}

	public void setLongitude(double lon) {
		pointLon = lon;
	}

	public void setLatitude(double lat) {
		pointLat = lat;
	}

	public void setAltitude(double alt) {
		pointAlt = alt;
	}

	public void setDate(Date datetime) {
		if (datetime == null) {
			pointDate = getCurrentDateTime();
			return;
		}
		pointDate = datetime;
	}

	public void setType(int type) {
		pointType = type;
	}

	/**
	 * Local Methods
	 */
	private Date getCurrentDateTime() {
		Calendar univeralCalendar = Calendar.getInstance();
		return univeralCalendar.getTime();
	}

	private ContentValues getValueMap() {
		ContentValues valueMap = new ContentValues(5);

		valueMap.put(TrackPointsColumn.ID.getName(), pointID);
		valueMap.put(TrackPointsColumn.LONGITUDE.getName(), pointLon);
		valueMap.put(TrackPointsColumn.LATITUDE.getName(), pointLat);
		valueMap.put(TrackPointsColumn.ALTITUDE.getName(), pointAlt);
		valueMap.put(TrackPointsColumn.TYPE.getName(), pointType);
		if (pointDate != null) {
			valueMap.put(TrackPointsColumn.DATE.getName(),
					SqliteConstant.ISO8601_DATE_FORMAT.format(pointDate));
		}

		return valueMap;
	}

	public static Cursor getTrackPoints(Context context, int trackID) {
		if (trackID <= 0)
			throw new IllegalArgumentException("trackID is invalid.");

		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path),
				TableName.TrackPoints);

		// sqliteDatabase.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));

		String sqlCommand = "select " + TrackPointsColumn.ID.getName() + " "
				+ CursorColumn.ID.get() + ","
				+ TrackPointsColumn.LONGITUDE.getName() + ","
				+ TrackPointsColumn.LATITUDE.getName() + ","
				+ TrackPointsColumn.ALTITUDE.getName() + ","
				+ TrackPointsColumn.DATE.getName() + ","
				+ TrackPointsColumn.TYPE.getName() + " from "
				+ TableName.TrackPoints.getName() + " where "
				+ TrackPointsColumn.ID.getName() + "=" + trackID + " order by "
				+ TrackPointsColumn.DATE.getName();

		Log.i("TrackPoint", "sql command : " + sqlCommand);
		sqliteDatabase.setSQLCommand(sqlCommand);
		return sqliteDatabase.QueryWithCommand();
	}

	public long Pin() throws SQLException {
		long addResult;
		addResult = super.Insert(getValueMap());

		return addResult;
	}

	public static int Pull(Context context, int trackID) throws SQLException {
		if (trackID <= 0)
			throw new IllegalArgumentException("trackID is invalid.");

		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path),
				TableName.TrackPoints);

		// sqliteDatabase.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		int delResult = sqliteDatabase.Delete(TrackPointsColumn.ID.getName()
				+ "=?", new String[] { String.valueOf(trackID) });

		return delResult;
	}

	public static void Pull(Context context, String params) throws SQLException {
		if (params == null)
			throw new IllegalArgumentException("argument is invalid.");

		SQLiteBot sqliteDatabase = new SQLiteBot(context
				.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path),
				TableName.TrackPoints);

		// sqliteDatabase.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		sqliteDatabase.setSQLCommand("delete from "
				+ TableName.TrackPoints.getName() + " where "
				+ TrackPointsColumn.ID.getName() + " in("
				+ (params.length() == 0 ? "" : params) + ")");

		Log.i("TrackPoints", "command = " + sqliteDatabase.getSQLCommand());
		sqliteDatabase.AlterWithCommand();

		return;
	}

	// do not use this
	public int Update() throws SQLException {
		if (pointID <= 0 || pointDate == null)
			throw new SQLException("update indexes is invalid.");

		int updResult;
		updResult = super.Update(getValueMap(), TrackPointsColumn.ID.getName()
				+ "=? and " + TrackPointsColumn.DATE.getName() + "=?",
				new String[] { String.valueOf(pointID),
						SqliteConstant.ISO8601_DATE_FORMAT.format(pointDate) });

		return updResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return pointDate.hashCode() + pointID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof TrackPoint))
			return false;

		TrackPoint other = (TrackPoint) obj;

		if (pointID == other.pointID && pointLon == other.pointLon
				&& pointLat == other.pointLat && pointAlt == other.pointAlt
				&& pointDate.equals(other.pointDate)
				&& pointType == other.pointType)
			return true;

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Track [" + "ID=" + pointID + ", Longitude=" + pointLon
				+ "Latitude=" + pointLat + ", Altitude=" + pointAlt
				+ "DateTime=" + pointDate.toString() + ", Type=" + pointType
				+ "]";
	}
}
