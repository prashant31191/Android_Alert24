package com.kremski.alert24.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.kremski.alert24.domain.EventCategory;

public class EventCategoriesTable extends DefaultTableHelper<EventCategory> {

	public static final String TABLE_NAME = "event_categories";

	public static final String KEY_NAME = "event_category_name";
	public static final String KEY_ICON_URL = "event_category_icon_url";
	public static final String KEY_DATABASE_ID = "event_category_database_id";
	public static final String KEY_DATABASE_PARENT_ID = "event_category_parent_id";
	public static final String KEY_SERVER_ID = "event_category_server_id";
	
	public static final String CREATE_TABLE_QUERY = "create table if not exists " + TABLE_NAME + " "
			+ "( " + KEY_NAME + " text not null, "
			+ KEY_ICON_URL + " text, " 
			+ KEY_DATABASE_PARENT_ID + " integer, " 
			+ KEY_DATABASE_ID + " integer primary key autoincrement, " 
			+ KEY_SERVER_ID + " text);";
	
	public EventCategoriesTable(Context context) {
		super(context);
	}

	@Override
	public EventCategory getHelperObject() {
		return new EventCategory("", "", 0, 0, "");
	}

	@Override
	protected Cursor getCursorWithRecord(EventCategory record) {
		return query(getTableName(), null, KEY_DATABASE_ID + "= ?", new String[]{Long.toString(record.getDatabaseId())}, null, null, null);
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected EventCategory getRecordFromCursor(Cursor cursor) {
		String name = cursor.getString(cursor.getColumnIndex(KEY_NAME));
		long databaseId = cursor.getLong(cursor.getColumnIndex(KEY_DATABASE_ID));
		long databaseParentId = cursor.getLong(cursor.getColumnIndex(KEY_DATABASE_PARENT_ID));
		String serverId = cursor.getString(cursor.getColumnIndex(KEY_SERVER_ID));
		String iconUrl = cursor.getString(cursor.getColumnIndex(KEY_ICON_URL));
		
		return new EventCategory(name, iconUrl, databaseId, databaseParentId, serverId);
	}

	@Override
	protected ContentValues createContentValuesFromRecord(EventCategory record) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_NAME, record.getName());
		contentValues.put(KEY_ICON_URL, record.getIconUrl());
		contentValues.put(KEY_DATABASE_PARENT_ID, record.getDatabaseParentId());
		contentValues.put(KEY_SERVER_ID, record.getServerId());
		return contentValues;
	}

	@Override
	protected String getIdKey() {
		return KEY_DATABASE_ID;
	}
}
