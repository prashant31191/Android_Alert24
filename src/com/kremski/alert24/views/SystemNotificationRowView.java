package com.kremski.alert24.views;

import hirondelle.date4j.DateTime;

import java.text.SimpleDateFormat;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kremski.alert24.R;
import com.kremski.alert24.domain.SystemNotification;

public class SystemNotificationRowView extends LinearLayout{

	private static final int LAYOUT_ID = R.layout.system_notification_row_view;

	private TextView messageContentLabel;
	private TextView messageAuthorLabel;
	private TextView messageTitleLabel;
	private TextView messageDateLabel;
	private ImageView messageReadStatus;
	private SystemNotification message;
	private static final int DISPLAYED_CONTENT_LENGTH = 20;

	public SystemNotificationRowView(Context context) {
		super(context);
		initialize();
	}

	private void initialize() {
		inflateLayout();
		messageTitleLabel = (TextView)findViewById(R.id.title);
		messageContentLabel = (TextView)findViewById(R.id.content);
		messageAuthorLabel = (TextView)findViewById(R.id.author);
		messageReadStatus = (ImageView)findViewById(R.id.is_read_status);
		messageDateLabel = (TextView)findViewById(R.id.date);
	}

	private void inflateLayout() {
		String service = Context.LAYOUT_INFLATER_SERVICE;
		LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(service);	
		inflater.inflate(LAYOUT_ID, this, true);
	}

	public void setMessage(SystemNotification message) {
		this.message = message;
		String content = message.getContent();
		if (content.length() > DISPLAYED_CONTENT_LENGTH) {
			content = content.substring(0, DISPLAYED_CONTENT_LENGTH);
			content += "...";
		}
		messageTitleLabel.setText(message.getTitle());
		messageContentLabel.setText(content);
		messageAuthorLabel.setText(message.getAuthor());
		Drawable readStatusIcon = message.isRead() ? 
				getResources().getDrawable(R.drawable.is_read_status) : 
				getResources().getDrawable(R.drawable.is_not_read_status);
		messageReadStatus.setImageDrawable(readStatusIcon);
		messageDateLabel.setText(DateTime.forInstant(message.getSentDate(), TimeZone.getDefault()).format("DD-MM-YY hh:mm"));
		
	}

	public SystemNotification getMessage() {
		return message;
	}
}
