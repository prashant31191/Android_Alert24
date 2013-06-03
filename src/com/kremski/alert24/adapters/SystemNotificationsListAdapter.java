package com.kremski.alert24.adapters;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.common.collect.Lists;
import com.kremski.alert24.domain.SystemNotification;
import com.kremski.alert24.views.SystemNotificationRowView;

public class SystemNotificationsListAdapter extends BaseAdapter{

	private Context context;
	private List<SystemNotification> displayedMessages = Lists.newLinkedList();
	private List<SystemNotification> allMessages = Lists.newLinkedList();
	private AdapterState currentAdapterState = AdapterState.SHOW_ALL_MESSAGES;
	
	public SystemNotificationsListAdapter(Context context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		return displayedMessages.size();
	}

	@Override
	public Object getItem(int position) {
		return displayedMessages.get(position);
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SystemNotification message = displayedMessages.get(position);

		if (convertView == null) {
			convertView = new SystemNotificationRowView(context);
		} 
		SystemNotificationRowView messageRowView = (SystemNotificationRowView)convertView;
		messageRowView.setMessage(message);
		return messageRowView;
	}

	public void setAllMessages(List<SystemNotification> allMessages) {
		this.allMessages = Lists.newLinkedList(allMessages);
		this.displayedMessages = Lists.newLinkedList(allMessages);
		notifyDataSetChanged();
	}

	public synchronized void removeMessage(SystemNotification messageToRemove) {
		for (int position = 0; position < allMessages.size(); ++position) {
			if (allMessages.get(position).getDatabaseId() == messageToRemove.getDatabaseId()) {
				allMessages.remove(position);
				break;
			}
		}
		currentAdapterState.showMessages(this);
	}

	public synchronized void updateMessage(SystemNotification messageToUpdate) {
		for (int position = 0; position < allMessages.size(); ++position) {
			if (allMessages.get(position).getDatabaseId() == messageToUpdate.getDatabaseId()) {
				allMessages.set(position, messageToUpdate);
				break;
			}
		}
		
		currentAdapterState.showMessages(this);
	}

	public List<SystemNotification> getAllMessages() {
		return allMessages;
	}
	
	public void showAllMessages() {
		currentAdapterState = AdapterState.SHOW_ALL_MESSAGES;
		displayedMessages = Lists.newLinkedList(allMessages);
		notifyDataSetChanged();
	}

	public void showNotReadMessages() {
		currentAdapterState = AdapterState.SHOW_NOT_READ_MESSAGES;
		displayedMessages.clear();
		for (SystemNotification m : allMessages) {
			if (!m.isRead()) {
				displayedMessages.add(m);
			}
		}
		notifyDataSetChanged();
	}

	public void showReadMessages() {
		currentAdapterState = AdapterState.SHOW_READ_MESSAGES;
		displayedMessages.clear();
		for (SystemNotification m : allMessages) {
			if (m.isRead()) {
				displayedMessages.add(m);
			}
		}
		notifyDataSetChanged();
	}
	
	private enum AdapterState {
		SHOW_ALL_MESSAGES {
			@Override
			public void showMessages(SystemNotificationsListAdapter adapter) {
				adapter.showAllMessages();
			}
		},
		
		SHOW_NOT_READ_MESSAGES {

			@Override
			public void showMessages(SystemNotificationsListAdapter adapter) {
				adapter.showNotReadMessages();
			}
			
		},
		
		SHOW_READ_MESSAGES {

			@Override
			public void showMessages(SystemNotificationsListAdapter adapter) {
				adapter.showReadMessages();
			}
			
		};
		
		public abstract void showMessages(SystemNotificationsListAdapter adapter);
	}

	public List<SystemNotification> getDisplayedMessages() {
		return Lists.newLinkedList(displayedMessages);
	}

	public void addMessage(SystemNotification message) {
		allMessages.add(message);
		currentAdapterState.showMessages(this);
	}
}
