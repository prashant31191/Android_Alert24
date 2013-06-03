package com.kremski.alert24.views;

import hirondelle.date4j.DateTime;

import java.util.TimeZone;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kremski.alert24.R;
import com.kremski.alert24.domain.SystemNotification;

public class SystemNotificationChildFragment extends Fragment{

	private TextView notificationContentLabel;
	private TextView notificationAuthorLabel;
	private TextView notificationReadStatus;
	private TextView notificationTitleLabel;
	private TextView notificationDateLabel;
	private SystemNotification systemNotification;

	public static SystemNotificationChildFragment newInstance(SystemNotification notification) {
		SystemNotificationChildFragment fragment = new SystemNotificationChildFragment();
		fragment.systemNotification = notification;
		return fragment;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View layout =  inflater.inflate(R.layout.system_notification_child_fragment, null);
		notificationContentLabel = (TextView)layout.findViewById(R.id.content);
		notificationAuthorLabel = (TextView)layout.findViewById(R.id.author);
		notificationDateLabel = (TextView)layout.findViewById(R.id.date);
		notificationReadStatus = (TextView)layout.findViewById(R.id.is_read_status);
		notificationTitleLabel = (TextView)layout.findViewById(R.id.title);

		notificationContentLabel.setText(systemNotification.getContent());
		notificationAuthorLabel.setText(systemNotification.getAuthor());
		int readStatusId = systemNotification.isRead() ? R.string.is_read : R.string.is_not_read;
		notificationReadStatus.setText(readStatusId);
		notificationDateLabel.setText(DateTime.forInstant(systemNotification.getSentDate(), TimeZone.getDefault()).format("DD-MM-YY hh:mm"));
		notificationTitleLabel.setText(systemNotification.getTitle());
		return layout;
	}
}
