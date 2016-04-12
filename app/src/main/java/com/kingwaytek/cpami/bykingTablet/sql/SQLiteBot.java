package com.kingwaytek.cpami.bykingTablet.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TableName;


public class SQLiteBot implements IDbBot {

	/** Database Path */
	private String sqliteDbPath; // database path

	/** SQLite database name */
	private String sqliteDbName; // database name

	/** Database table name */
	private TableName sqliteTblName; // database table name

	/** SQLite Database */
	private SQLiteDatabase sqliteDb; // SQLiteDatabase

	/** T-SQL Command Statement */
	private String tsqlCommand; // T-SQL statement

	/** SQL Parameters */
	private String[] sqlParams; // Parameters for SQLite access

	/**
	 * Create an instance of SQLiteBot
	 * 
	 * @param dbName
	 *            database name
	 */
	protected SQLiteBot(String dbName, String dbPath) {
		// super(context, dbName, null, 1);

		sqliteDbName = dbName;
		sqliteDbPath = dbPath;
		sqliteTblName = null;
		SQLiteInit();
	}

	/**
	 * Create an instance of SQLiteBot
	 * 
	 * @param dbName
	 *            database name
	 * @param dbPath
	 *            database path
	 * @param tblName
	 *            database table name
	 */
	public SQLiteBot(String dbName, String dbPath, TableName tblName) {
		// super(context, dbName, null, 1);

		sqliteDbPath = dbPath;
		sqliteDbName = dbName;
		sqliteTblName = tblName;
		SQLiteInit();
	}

	/**
	 * Initialize SQLiteBot local variables
	 */
	private void SQLiteInit() {
		sqliteDb = null;
		tsqlCommand = null;
		sqlParams = null;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDatabasePath() {
		return sqliteDbPath;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDatabasePath(String dbPath) {
		sqliteDbPath = dbPath;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getDatabaseName() {
		return sqliteDbName;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDatabaseName(String dbName) {
		sqliteDbName = dbName;
	}

	/**
	 * {@inheritDoc}
	 */
	public TableName getDbTableName() {
		return sqliteTblName;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setDbTableName(TableName tblName) {
		sqliteTblName = tblName;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean IsDatabaseValid() throws SQLException {
		if (sqliteDbName == null || sqliteDbName.trim().length() == 0) {
			return false;
		}

		SQLiteDatabase sqlDbTemp = null;

		try {
			// sqlDbTemp = super.getReadableDatabase();
			sqlDbTemp = SQLiteDatabase.openDatabase(
					sqliteDbPath + sqliteDbName, null,
					SQLiteDatabase.OPEN_READONLY);
		} catch (Exception e) {
			Log.e("SQLiteBot", "IsDatabaseValid Exception: " + e);
		}

		if (sqlDbTemp != null) {
			sqlDbTemp.close();
		}

		return sqlDbTemp != null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cursor QueryWithCommand() throws SQLException {
		if (tsqlCommand == null || tsqlCommand.length() == 0)
			throw new SQLException("T-SQL Command is null or has empty value.");

		OpenConnection(SQLiteDatabase.OPEN_READONLY);
		Cursor curResult = sqliteDb.rawQuery(tsqlCommand, sqlParams);
		curResult.moveToFirst();
		this.Dispose();

		return curResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void AlterWithCommand() throws SQLException {
		if (tsqlCommand == null || tsqlCommand.length() == 0)
			throw new SQLException("T-SQL Command is null or has empty value.");

		OpenConnection(SQLiteDatabase.OPEN_READWRITE);
		if (sqlParams == null || sqlParams.length == 0) {
			sqliteDb.execSQL(tsqlCommand);
		} else {
			sqliteDb.execSQL(tsqlCommand, sqlParams);
		}
		this.Dispose();

		return;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Cursor Query() throws SQLException {
		// TODO Auto-generated method stub
		// OpenConnection(SQLiteDatabase.OPEN_READONLY);
		// sqliteDb.query(table, columns, selection, selectionArgs, groupBy,
		// having, orderBy);
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public long Insert(ContentValues cvMap) throws SQLException {
		if (sqliteTblName == null || sqliteTblName.getName().length() == 0)
			throw new SQLException(
					"SQLite TableName is null or has empty value.");
		if (cvMap == null || cvMap.size() == 0)
			throw new IllegalArgumentException(
					"Argument(s) is null or has empty value.");

		OpenConnection(SQLiteDatabase.OPEN_READWRITE);
		long longResult = sqliteDb.insert(sqliteTblName.getName(), null, cvMap);
		this.Dispose();

		return longResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int Delete(String condition, String[] params) throws SQLException {
		if (sqliteTblName == null || sqliteTblName.getName().length() == 0)
			throw new SQLException(
					"SQLite TableName is null or has empty value.");

		OpenConnection(SQLiteDatabase.OPEN_READWRITE);
		int intResult = sqliteDb.delete(sqliteTblName.getName(), condition,
				params);
		this.Dispose();

		return intResult;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int Update(ContentValues cvMap, String condition, String[] params)
			throws SQLException {
		if (sqliteTblName == null || sqliteTblName.getName().length() == 0)
			throw new SQLException(
					"SQLite TableName is null or has empty value.");
		if (cvMap == null || cvMap.size() == 0)
			throw new IllegalArgumentException(
					"Argument cvMap is null or has empty value.");

		OpenConnection(SQLiteDatabase.OPEN_READWRITE);
		int intResult = sqliteDb.update(sqliteTblName.getName(), cvMap,
				condition, params);
		this.Dispose();

		return intResult;
	}

	/**
	 * get T-SQL Command Statement
	 * 
	 * @return T-SQL command
	 */
	public String getSQLCommand() {
		return tsqlCommand;
	}

	/**
	 * set T-SQL Command Statement
	 * 
	 * @param sqlCommnad
	 *            T-SQL command
	 */
	public void setSQLCommand(String sqlCommnad) {
		tsqlCommand = sqlCommnad;
	}

	/**
	 * get parameters for QueryWithCommand or AlterWithCommand
	 * 
	 * @return parameters for query or alter
	 */
	public String[] getQueryParams() {
		return sqlParams.clone();
	}

	/**
	 * set Parameters used for QueryWithCommand or AlterWithCommand
	 * 
	 * @param params
	 *            parameters for query or alter
	 */
	public void setQueryParams(String[] params) {
		if (params == null || params.length == 0)
			return;

		sqlParams = new String[params.length];
		for (int i = 0; i < params.length; i++) {
			sqlParams[i] = params[i];
		}
	}

	/**
	 * Open this SQLite connection
	 * 
	 * @param openMode
	 *            mode to open connection
	 * @throws SQLException
	 */
	private void OpenConnection(int openMode) throws SQLException {
		// try {
		// switch (openMode) {
		// case SQLiteDatabase.OPEN_READONLY:
		// sqliteDb = super.getReadableDatabase();
		// break;
		// case SQLiteDatabase.OPEN_READWRITE:
		// sqliteDb = super.getWritableDatabase();
		// break;
		// default:
		// sqliteDb = super.getReadableDatabase();
		// break;
		// }
		// } catch (Exception e) {
		// Log.e("SQLiteBot", "OpenConnection Failed: " + e);
		// sqliteDb = null;
		// }
		Log.i("SQLiteBot", "dbFile : " + sqliteDbPath + sqliteDbName);
		try {
			sqliteDb = SQLiteDatabase.openDatabase(sqliteDbPath + sqliteDbName,
					null, openMode);
		} catch (Exception e) {
			Log.e("SQLiteBot", "OpenConnection Failed: " + e);
			sqliteDb = null;
		}
		Log.i("SQLiteBot", "db valid : " + (sqliteDb != null));
	}

	/**
	 * close this SQLite database connection and clean up.
	 * 
	 * @throws SQLException
	 */
	private void Dispose() throws SQLException {
		try {
			sqliteDb.close();
			tsqlCommand = null;
			sqlParams = null;
			SQLiteDatabase.releaseMemory();
		} catch (Exception e) {
			Log.e("SQLiteBot", "Dispose Failed: " + e);
		}
	}

	// // Extension from SQLiteOpenHelper
	//
	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public synchronized void close() {
	// if (sqliteDb != null)
	// sqliteDb.close();
	//
	// super.close();
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public void onCreate(SQLiteDatabase db) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// /**
	// * {@inheritDoc}
	// */
	// @Override
	// public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	// {
	// // TODO Auto-generated method stub
	//
	// }
}
