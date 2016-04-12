package com.kingwaytek.cpami.bykingTablet.sql;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;

import com.kingwaytek.cpami.bykingTablet.sql.SqliteConstant.TableName;

public interface IDbBot {

	/**
	 * get SQLite database path, default android path is
	 * \/data\/data\/package_name\/databases\/
	 * 
	 * @return database path
	 */
	public String getDatabasePath();

	/**
	 * set SQLite database path, default android path is
	 * \/data\/data\/package_name\/databases\/
	 * 
	 * @param dbPath
	 *            the path to set
	 */
	public void setDatabasePath(String dbPath);

	/**
	 * get SQLite database file name.
	 * 
	 * @return SQLite database file name
	 */
	public String getDatabaseName();

	/**
	 * set SQLite database file name
	 * 
	 * @param dbName
	 *            the file name to set
	 */
	public void setDatabaseName(String dbName);

	/**
	 * get current working SQLite database table name.
	 * 
	 * @return current working SQLite database table name
	 */
	public TableName getDbTableName();

	/**
	 * set current working SQLite database table name.
	 * 
	 * @param tblName
	 *            the table name to set
	 */
	public void setDbTableName(TableName tblName);

	// public void OpenDatabase() throws SQLException;
	//
	// public void CloseDatabase() throws SQLException;

	/**
	 * check if this SQLite database is valid or not.
	 * 
	 * @return true if this SQLite database is valid, false otherwise.
	 * @throws SQLException
	 */
	public abstract boolean IsDatabaseValid() throws SQLException;

	/**
	 * query SQLite database with provided t-sql command.
	 * 
	 * @return Cursor over the result set.
	 * @throws SQLException
	 */
	public abstract Cursor QueryWithCommand() throws SQLException;

	/**
	 * alter SQLite database with provided t-sql command.
	 * 
	 * @throws SQLException
	 */
	public abstract void AlterWithCommand() throws SQLException;

	/**
	 * query SQLite database with provided parameters.
	 * 
	 * @return Cursor over the result set.
	 * @throws SQLException
	 */
	public abstract Cursor Query() throws SQLException;

	/**
	 * insert record into SQLite database.
	 * 
	 * @param cvMap
	 *            ContentValues to insert.
	 * @return long indicate insert result.
	 * @throws SQLException
	 */
	public abstract long Insert(ContentValues cvMap) throws SQLException;

	/**
	 * delete record(s) with provided parameters.
	 * 
	 * @param condition
	 *            conditions to do delete.
	 * @param params
	 *            deletion parameters.
	 * @return int indicate delete result.
	 * @throws SQLException
	 */
	public abstract int Delete(String condition, String[] params)
			throws SQLException;

	/**
	 * update record(s) with provided parameters.
	 * 
	 * @param cvMap
	 *            ContentValues to update.
	 * @param condition
	 *            conditions to do update.
	 * @param params
	 *            update parameters.
	 * @return int indicate update result.
	 * @throws SQLException
	 */
	public abstract int Update(ContentValues cvMap, String condition,
			String[] params) throws SQLException;
}
