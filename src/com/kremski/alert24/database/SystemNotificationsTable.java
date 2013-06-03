package com.kremski.alert24.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.google.android.gms.maps.model.LatLng;
import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.domain.SystemNotification;

public class SystemNotificationsTable extends DefaultTableHelper<SystemNotification> {

	public static final String TABLE_NAME = "messages";

	public static final String KEY_AUTHOR = "message_author";
	public static final String KEY_CONTENT = "message_content";
	public static final String KEY_TITLE = "message_title";
	public static final String KEY_IS_READ = "message_is_read";
	public static final String KEY_SENT_DATE = "message_send_date";
	public static final String KEY_DATABASE_ID = "message_id";
	
	public static final String CREATE_TABLE_QUERY = "create table if not exists " + TABLE_NAME + " "
			+ "( " + KEY_AUTHOR + " text not null, "
			+ KEY_CONTENT + " text not null, "
			+ KEY_TITLE + " text not null, "
			+ KEY_IS_READ + " integer, " 
			+ KEY_SENT_DATE + " text, " 
			+ KEY_DATABASE_ID + " integer primary key autoincrement); " ;
	
	public SystemNotificationsTable(Context context) {
		super(context);
	}

	@Override
	public SystemNotification getHelperObject() {
		return new SystemNotification("", "", "", false, 0);
	}

	@Override
	protected Cursor getCursorWithRecord(SystemNotification record) {
		return query(getTableName(), null, KEY_DATABASE_ID + "= ?", new String[]{Long.toString(record.getDatabaseId())}, null, null, null);
	}

	@Override
	protected String getTableName() {
		return TABLE_NAME;
	}

	@Override
	protected SystemNotification getRecordFromCursor(Cursor cursor) {
		String title = cursor.getString(cursor.getColumnIndex(KEY_TITLE));
		String author = cursor.getString(cursor.getColumnIndex(KEY_AUTHOR));
		String content = cursor.getString(cursor.getColumnIndex(KEY_CONTENT));
		long databaseId = cursor.getLong(cursor.getColumnIndex(KEY_DATABASE_ID));
		boolean isRead = (cursor.getInt(cursor.getColumnIndex(KEY_IS_READ)) == 1);
		long sentDate = cursor.getLong(cursor.getColumnIndex(KEY_SENT_DATE));
		
		return new SystemNotification(databaseId, title, content, author, isRead, sentDate);
	}

	@Override
	protected ContentValues createContentValuesFromRecord(SystemNotification record) {
		ContentValues contentValues = new ContentValues();
		contentValues.put(KEY_AUTHOR, record.getAuthor());
		contentValues.put(KEY_CONTENT, record.getContent());
		contentValues.put(KEY_IS_READ, (record.isRead() ? 1 : 0));
		contentValues.put(KEY_SENT_DATE, record.getSentDate());
		contentValues.put(KEY_TITLE, record.getTitle());
		return contentValues;
	}

	@Override
	protected String getIdKey() {
		return KEY_DATABASE_ID;
	}
}
