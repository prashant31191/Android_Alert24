package com.kremski.alert24.database;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.common.collect.Lists;

public abstract class DefaultTableHelper<T extends TableRecord> implements TableHelper<T>{
	
	protected SQLiteDatabase sqliteDatabase = null;
	private final DatabaseHelper databaseHelper;
	
	private static final String DATABASE_NAME = "Alert24Database";
	private static final int DATABASE_VERSION = 1;
	
	protected abstract Cursor getCursorWithRecord(T record);
	protected abstract String getTableName();
	protected abstract T getRecordFromCursor(Cursor cursor);
	protected abstract ContentValues createContentValuesFromRecord(T record); 
	protected abstract String getIdKey();
	
	public DefaultTableHelper(Context context) {
		databaseHelper = new DatabaseHelper(context);
	}
	
	public void openDatabase() {
		sqliteDatabase = databaseHelper.getWritableDatabase();
	}
	
	public void closeDatabase() {
		databaseHelper.close();
	}
	
	public List<T> getAll() {
		return queryForRecords(null, null);
	}
	
	public final long insert(T record) {
		String tableName = getTableName();
		ContentValues contentValues = createContentValuesFromRecord(record);
		return sqliteDatabase.insert(tableName, null, contentValues);
	}
	
	public final void deleteAll() {
		sqliteDatabase.delete(getTableName(), null, null);
	}
	
	public void delete(T recordToDelete) {
		sqliteDatabase.delete(getTableName(), getIdKey() + "= ?", new String[]{Long.toString(recordToDelete.getDatabaseId())});
	}
	
	public void update(T recordToUpdate) {
		sqliteDatabase.update(getTableName(), createContentValuesFromRecord(recordToUpdate), getIdKey() + " = ?", new String[]{Long.toString(recordToUpdate.getDatabaseId())});
	}
		
	protected Cursor query(String tableName, String columns[], String selection, String selectionArgs[], String groupBy, String having, String orderBy) {
		return sqliteDatabase.query(tableName, columns, selection, selectionArgs, groupBy, having, orderBy);
	}
	
	protected List<T> queryForRecords(String selection, String[] selectionArgs) {
		Cursor cursor = sqliteDatabase.query(getTableName(), null, selection, selectionArgs, null, null, null);
		
		List<T> records = getRecordsFromCursor(cursor);
		
		if (cursor != null) {
			cursor.close();
		}
		return records;
	}
	
	protected String prepareINOperatorSelection(int size) {
		StringBuilder builder = new StringBuilder();
		builder.append("(");
		for(int i = 0; i < size; ++i) {
			builder.append("?,");
		}
		builder.replace(builder.length()-1, builder.length(), ")");
		return builder.toString();
	}
	
	protected List<T> getRecordsFromCursor(Cursor cursor) {
		List<T> records = new LinkedList<T>();
		
		if (cursor != null && cursor.moveToFirst()) {
			do {
				records.add(getRecordFromCursor(cursor));
			}
			while(cursor.moveToNext());
		}
		
		return records;
	}
	
	protected class DatabaseHelper extends SQLiteOpenHelper {

		public DatabaseHelper(Context context) {
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
		}
		
		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL("PRAGMA foreign_keys = ON");
			db.execSQL(EventsTable.CREATE_TABLE_QUERY);
			db.execSQL(EventCategoriesTable.CREATE_TABLE_QUERY);
			db.execSQL(PointsOfInterestTable.CREATE_TABLE_QUERY);
			db.execSQL(SystemNotificationsTable.CREATE_TABLE_QUERY);
		}
		
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS " + EventsTable.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + EventCategoriesTable.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + PointsOfInterestTable.TABLE_NAME);
			db.execSQL("DROP TABLE IF EXISTS " + SystemNotificationsTable.TABLE_NAME);
			
			onCreate(db);
		}
	}
}
