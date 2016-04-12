package com.kingwaytek.cpami.bykingTablet.sql;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.R;
import com.kingwaytek.cpami.bykingTablet.app.AlertDialogUtil;
import com.kingwaytek.cpami.bykingTablet.app.UtilDialog;
import com.kingwaytek.cpami.bykingTablet.app.track.TrackEngine.TrackRecordingStatus;
import com.kingwaytek.cpami.bykingTablet.data.GeoPoint;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.CursorColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.FavoriteColumn;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TableName;
import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TrackColumn;

public class Track extends SQLiteBot {

	private int trackID; // track_id
	private String trackName; // track_name
	private int trackDifficulty; // track difficulty
	private String trackDesc; // track_desc
	private Date trackStart; // track_start
	private Date trackEnd; // track_end
	private Date trackCreate; // track_create
	// private boolean trackRecording; // track_recording
	private TrackRecordingStatus trackRecordingStatus; // track_status

	private Map<Integer, TrackPoint> trackPoints;
	private double distance;// 軌跡距離By Cuber

	// private SQLiteBot trackBot;
	//
	// protected static String dataBaseName; // database name
	// protected static String dataBasePath; // database path

	/** Constructors */
	public Track(Context context) {
		super(context.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path), TableName.Track);
		// trackBot = new SQLiteBot(context, context
		// .getString(R.string.SQLite_Database_Name), TableName.Track);
		// trackBot.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		//
		// dataBasePath = context.getString(R.string.SQLite_Database_Path);
		// dataBaseName = context.getString(R.string.SQLite_Database_Name);

		trackID = -1;
		trackName = null;
		trackDifficulty = 1;
		trackDesc = null;
		trackStart = null;
		trackEnd = null;
		trackCreate = null;
		// trackRecording = true;
		trackRecordingStatus = null;

		trackPoints = null;

	}

	public Track(Context context, int trackID) throws Exception {
		super(context.getString(R.string.SQLite_Usr_Database_Name), context
				.getString(R.string.SQLite_Usr_Database_Path), TableName.Track);
		// trackBot = new SQLiteBot(context, context
		// .getString(R.string.SQLite_Database_Name), TableName.Track);
		// trackBot.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		//
		// dataBasePath = context.getString(R.string.SQLite_Database_Path);
		// dataBaseName = context.getString(R.string.SQLite_Database_Name);

		super.setSQLCommand("select * from " + TableName.Track.getName() + " where " + TrackColumn.ID.getName() + "="
				+ trackID);

		Log.i("Track", "sql command = " + super.getSQLCommand());
		Cursor curTrack = super.QueryWithCommand();
		Log.i("Track", "result count = " + curTrack.getCount());

		if (curTrack.getCount() != 1)
			throw new Exception("can not load track from trackID provided.");

		this.trackID = trackID;
		trackName = curTrack.getString(1);
		trackDifficulty = curTrack.getInt(2);
		trackDesc = curTrack.getString(3);
		trackStart = SqliteConstant.ISO8601_DATE_FORMAT.parse(curTrack.getString(4));
		trackEnd = curTrack.getString(5) == "" ? null : SqliteConstant.ISO8601_DATE_FORMAT.parse(curTrack.getString(5));
		trackCreate = curTrack.getString(6) == "" ? null : SqliteConstant.ISO8601_DATE_FORMAT.parse(curTrack
				.getString(6));
		// trackRecording = curTrack.getInt(6) == -1;
		trackRecordingStatus = TrackRecordingStatus.get(curTrack.getInt(7));

		curTrack.close();

		// fetch points
		FetchPoints(context);
		distance = CalculateDistance();
	}

	/**
	 * Accessor & Modifier Area
	 */
	public int getID() {
		return trackID;
	}

	public String getName() {
		return trackName;
	}

	public int getDifficulty() {
		return trackDifficulty;
	}

	public String getDescription() {
		return trackDesc;
	}

	public Date getStartTime() {
		return trackStart;
	}

	public Date getEndTime() {
		return trackEnd;
	}

	public Date getCreateTime() {
		return trackCreate;
	}

	// public boolean isRecording() {
	// return trackRecording;
	// }

	public TrackRecordingStatus getRecordingStatus() {
		return trackRecordingStatus;
	}

	public Map<Integer, TrackPoint> getTrackPoints() {
		return trackPoints;
	}

	public GeoPoint getStartPoint() {
		if (trackPoints == null || trackPoints.size() == 0) {
			return null;
		}
		GeoPoint start = new GeoPoint(trackPoints.get(0).getLongitude(), trackPoints.get(0).getLatitude());
		return start;
	}

	public GeoPoint getEndPoint() {
		if (trackPoints == null || trackPoints.size() == 0) {
			return null;
		}
		GeoPoint end = new GeoPoint(trackPoints.get(trackPoints.size() - 1).getLongitude(), trackPoints.get(
				trackPoints.size() - 1).getLatitude());
		return end;
	}

	public void setName(String name) {
		trackName = name;
	}

	// minimum 1, maximum 5
	public void setDifficulty(int difficulty) {
		if (difficulty <= 0) {
			trackDifficulty = 1;
			return;
		}
		if (difficulty > 5) {
			trackDifficulty = 5;
			return;
		}
		trackDifficulty = difficulty;
	}

	public void setDescription(String description) {
		trackDesc = description;
	}

	public void setEndTime() {
		trackEnd = getCurrentDateTime();
	}

	public void setEndTime(Date datetime) {
		if (datetime == null) {
			setEndTime();
			return;
		}

		trackEnd = datetime;
	}

	public void setStartTime(Date datetime) {
		if (datetime == null) {
			return;
		}

		trackStart = datetime;
	}

	public void setCreateTime() {
		trackCreate = getCurrentDateTime();
	}

	// public void isRecording(boolean isrecording) {
	// trackRecording = isrecording;
	// }

	public void setRecordingStatus(TrackRecordingStatus status) {
		trackRecordingStatus = status;
	}

	// ** Local Methods */
	private Date getCurrentDateTime() {
		Calendar univeralCalendar = Calendar.getInstance();
		return univeralCalendar.getTime();
	}

	private void FetchPoints(Context context) {
		Cursor curPoint = TrackPoint.getTrackPoints(context, trackID);

		// no points found in db
		if (curPoint.getCount() <= 0)
			return;

		trackPoints = new LinkedHashMap<Integer, TrackPoint>(curPoint.getCount());
		int pos = 0;

		try {
			while (!curPoint.isAfterLast()) {
				trackPoints.put(pos++, new TrackPoint(context, curPoint));
				curPoint.moveToNext();
			}
		} catch (Exception e) {
			Log.e("Track", "error fetch points at : " + pos + ", " + e);
		}

		curPoint.close();
	}

	private ContentValues getValueMap() {
		ContentValues valueMap = new ContentValues(6);

		valueMap.put(TrackColumn.NAME.getName(), trackName);
		valueMap.put(TrackColumn.DIFFICULTY.getName(), trackDifficulty);
		valueMap.put(TrackColumn.DESCRIPTION.getName(), trackDesc);
		valueMap.put(TrackColumn.END.getName(),
				trackEnd == null ? "" : SqliteConstant.ISO8601_DATE_FORMAT.format(trackEnd));
		valueMap.put(TrackColumn.CREATE.getName(),
				trackCreate == null ? "" : SqliteConstant.ISO8601_DATE_FORMAT.format(trackCreate));
		// valueMap.put(TrackColumn.RECORDING.getName(),
		// trackRecording ? TrackRecordingStatus.RECORDING.getValue()
		// : TrackRecordingStatus.STOPED.getValue());
		valueMap.put(
				TrackColumn.RECORDING.getName(),
				trackRecordingStatus == null ? TrackRecordingStatus.RECORDING.getValue() : trackRecordingStatus
						.getValue());
		if (trackStart != null) {
			valueMap.put(TrackColumn.START.getName(),
					trackStart == null ? "" : SqliteConstant.ISO8601_DATE_FORMAT.format(trackStart));
		}

		return valueMap;
	}

	public static Cursor getTrackList(Context context, String search) {
		SQLiteBot sqliteDatabase = new SQLiteBot(context.getString(R.string.SQLite_Usr_Database_Name),
				context.getString(R.string.SQLite_Usr_Database_Path), TableName.Track);

		String sqlCommand = "select " + TrackColumn.ID.getName() + " " + CursorColumn.ID.get() + ","
				+ TrackColumn.NAME.getName() + "," + TrackColumn.DIFFICULTY.getName() + ","
				+ TrackColumn.DESCRIPTION.getName() + "," + TrackColumn.START.getName() + ","
				+ TrackColumn.END.getName() + "," + TrackColumn.CREATE.getName() + ","
				+ TrackColumn.RECORDING.getName() + " from " + TableName.Track.getName() + " where "
				+ TrackColumn.NAME.getName() + " like '%" + search + "%' and " + TrackColumn.RECORDING.getName() + "="
				+ TrackRecordingStatus.STOPED.getValue() + " or " + TrackColumn.RECORDING.getName() + "="
				+ TrackRecordingStatus.IMPORTED.getValue() + " order by " + TrackColumn.CREATE.getName() + " desc";

		Log.i("Track", "sql command : " + sqlCommand);
		sqliteDatabase.setSQLCommand(sqlCommand);
		return sqliteDatabase.QueryWithCommand();
	}

	public long Record() throws SQLException {
		long addResult;
		addResult = super.Insert(getValueMap());
		trackID = (int) addResult;

		return addResult;
	}

	public static int Erase(Context context, int trackID) throws SQLException {
		if (trackID <= 0)
			throw new IllegalArgumentException("trackID is invalid.");

		SQLiteBot sqliteDatabase = new SQLiteBot(context.getString(R.string.SQLite_Usr_Database_Name),
				context.getString(R.string.SQLite_Usr_Database_Path), TableName.Track);

		// sqliteDatabase.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		int delResult = sqliteDatabase
				.Delete(TrackColumn.ID.getName() + "=?", new String[] { String.valueOf(trackID) });

		// need to delete track points
		TrackPoint.Pull(context, trackID);

		return delResult;
	}

	public static void Erase(Context context, String params) throws SQLException {
		if (params == null)
			throw new IllegalArgumentException("argument is invalid.");

		SQLiteBot sqliteDatabase = new SQLiteBot(context.getString(R.string.SQLite_Usr_Database_Name),
				context.getString(R.string.SQLite_Usr_Database_Path), TableName.Track);

		// sqliteDatabase.setDatabasePath(context
		// .getString(R.string.SQLite_Database_Path));
		sqliteDatabase.setSQLCommand("delete from " + TableName.Track.getName() + " where " + TrackColumn.ID.getName()
				+ " in(" + (params.length() == 0 ? "" : params) + ")");

		// need to delete track points
		TrackPoint.Pull(context, params);

		Log.i("Track", "command = " + sqliteDatabase.getSQLCommand());
		sqliteDatabase.AlterWithCommand();

		return;
	}

	// erase tracks that is not finished normally.
	public static void EraseBrokenTracks(Context context) {
		SQLiteBot sqliteDatabase = new SQLiteBot(context.getString(R.string.SQLite_Usr_Database_Name),
				context.getString(R.string.SQLite_Usr_Database_Path), TableName.Track);

		String sqlCommand = "select " + TrackColumn.ID.getName() + " from " + TableName.Track.getName() + " where "
				+ TrackColumn.RECORDING.getName() + "<" + TrackRecordingStatus.STOPED.getValue();

		Log.i("Track_EraseBrokenTracks", "sqlcommand:" + sqlCommand);
		sqliteDatabase.setSQLCommand(sqlCommand);
		Cursor cursorID = sqliteDatabase.QueryWithCommand();

		Log.i("Track_EraseBrokenTracks", "item count:" + cursorID.getCount());
		if (cursorID.getCount() <= 0) {
			cursorID.close();
			return;
		}

		cursorID.moveToPosition(-1);
		while (cursorID.moveToNext()) {
			Track.Erase(context, cursorID.getInt(cursorID.getColumnIndex(TrackColumn.ID.getName())));
		}
		cursorID.close();

		return;
	}

	// use this only for update name, description
	public int Update() throws SQLException {
		if (trackID <= 0)
			throw new SQLException("update index is invalid.");

		int updResult;
		updResult = super.Update(getValueMap(), TrackColumn.ID.getName() + "=?",
				new String[] { String.valueOf(trackID) });

		return updResult;
	}

	public Double CalculateDistance() {
		Log.i("Track", "trackpoints count=" + trackPoints.size());
		if (trackPoints == null || trackPoints.size() <= 0) {
			return 0.0;
		}

		// calculate distance
		double disResult = 0.0;
		double diffLon = 0.0;
		double diffLat = 0.0;
		GeoPoint lstPoint = new GeoPoint();
		GeoPoint curPoint = new GeoPoint();
		// fetch very first point information
		lstPoint.setLongitude(trackPoints.get(0).getLongitude());
		lstPoint.setLatitude(trackPoints.get(0).getLatitude());
		lstPoint.LonlatToTm97();
		for (TrackPoint curTp : trackPoints.values()) {
			curPoint.setLongitude(curTp.getLongitude());
			curPoint.setLatitude(curTp.getLatitude());
			curPoint.LonlatToTm97();

			// calculation
			diffLon = Math.abs(curPoint.getTmX() - lstPoint.getTmX());
			diffLat = Math.abs(curPoint.getTmY() - lstPoint.getTmY());
			disResult += Math.sqrt((diffLon * diffLon) + (diffLat * diffLat));

			// exchange
			lstPoint.setLongitude(curPoint.getLongitude());
			lstPoint.setLatitude(curPoint.getLatitude());
			lstPoint.LonlatToTm97();
		}
		return disResult;
	}

	// 查詢track_id是否有在table track裡
	public static boolean isItemInList(Context context, int track_id) throws Exception {
		if (track_id <= 0)
			throw new Exception("No item to varify.");
		SQLiteBot sqliteDatabase = new SQLiteBot(context.getString(R.string.SQLite_Usr_Database_Name),
				context.getString(R.string.SQLite_Usr_Database_Path), TableName.Track);

		String sqlCommand = "select * from " + TableName.Track.getName() + " where " + TrackColumn.ID.getName() + "="
				+ track_id;

		sqliteDatabase.setSQLCommand(sqlCommand);
		Cursor curResult = sqliteDatabase.QueryWithCommand();
		int itemCount = curResult.getCount();

		curResult.close();
		return itemCount > 0;
	}

	/**
	 * Method will not use here
	 * 
	 * @param context
	 *            context that this Object resides on.
	 */
	@Deprecated
	public void Play(Context context) {
		if (trackPoints == null || trackPoints.size() <= 0) {
			Log.w("Track", "nothing to Play.");

			UtilDialog uit = new UtilDialog(context);
			uit.showDialog_route_plan_choice("沒有軌跡資料", null, "確定", null);

			return;
		}

		Log.i("Track", "points count : " + trackPoints.size());
	}

	/**
	 * Method will not use here
	 * 
	 * @param format
	 *            export format
	 */
	@Deprecated
	public void Export(int format) {
		if (trackPoints == null || trackPoints.size() <= 0) {
			Log.w("Track", "nothing to Export.");
			return;
		}

		Log.i("Track", "points count : " + trackPoints.size());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		return trackID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof Track))
			return false;

		Track other = (Track) obj;

		if (trackID == other.trackID && trackName.equals(other.trackName) && trackDesc.equals(other.trackDesc)
				&& trackStart.equals(other.trackStart) && trackEnd.equals(other.trackEnd)
				&& trackCreate.equals(other.trackCreate))
			return true;

		return false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return "Track [" + "ID=" + trackID + ", Name=" + trackName + ", Description=" + trackDesc + ", StartTime="
				+ trackStart.toString() + ", EndTime=" + trackEnd.toString() + ", CreateTime=" + trackCreate.toString()
				+ ", isRecording=" + trackRecordingStatus.getValue() + "]";
	}
}
