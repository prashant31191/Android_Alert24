package com.kremski.alert24.screens;

import java.util.LinkedList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.kremski.alert24.R;
import com.kremski.alert24.adapters.SystemNotificationsListAdapter;
import com.kremski.alert24.domain.SystemNotification;
import com.kremski.alert24.domain.PointOfInterest;
import com.kremski.alert24.tasks.TaskDeletingAllMessages;
import com.kremski.alert24.tasks.TaskUpdatingMessage;
import com.kremski.alert24.tasks.TaskDeletingAllMessages.OnAllMessagesRemovedListener;
import com.kremski.alert24.tasks.TaskDeletingMessage;
import com.kremski.alert24.tasks.TaskDeletingMessage.OnMessageRemovedListener;
import com.kremski.alert24.tasks.TaskLoadingMessages;
import com.kremski.alert24.tasks.TaskLoadingMessages.OnMessagesLoadedListener;
import com.kremski.alert24.tasks.TaskUpdatingMessage.OnMessageUpdatedListener;
import com.kremski.alert24.views.ActionBarOption;
import com.kremski.alert24.views.SystemNotificationRowView;
import com.kremski.alert24.views.SystemNotificationsListFragment;

public class SystemNotificationsScreen extends Alert24Screen implements OnMessagesLoadedListener, OnAllMessagesRemovedListener, OnMessageRemovedListener, OnMessageUpdatedListener{

	private SystemNotificationsListAdapter messagesAdapter;
	private SystemNotificationsListFragment messagesListFragment;

	@Override
	protected void prepareActionBarOptions(LinkedList<ActionBarOption> actionbarChoosableOptions) {
		super.prepareActionBarOptions(actionbarChoosableOptions);
		ActionBarOption showAllMessages =   new ActionBarOption(R.drawable.messages, R.string.show_all_notifications, createOptionShowingAllMessages());
		actionbarChoosableOptions.add(showAllMessages);
		ActionBarOption showReadMessages =   new ActionBarOption(R.drawable.is_read_status, R.string.show_read_notifications, createOptionShowingReadMessages());
		actionbarChoosableOptions.add(showReadMessages);
		ActionBarOption showNotReadMessages =   new ActionBarOption(R.drawable.is_not_read_status, R.string.show_not_read_notifications, createOptionShowingNotReadMessages());
		actionbarChoosableOptions.add(showNotReadMessages);

		ActionBarOption removeAllMessages =   new ActionBarOption(R.drawable.delete, R.string.remove_notifications, createOptionRemovingAllPoints());
		actionbarChoosableOptions.add(removeAllMessages);
	}

	private OnClickListener createOptionShowingAllMessages() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				messagesAdapter.showAllMessages();
			}
		};
	}

	private OnClickListener createOptionShowingReadMessages() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				messagesAdapter.showReadMessages();
			}
		};
	}

	private OnClickListener createOptionShowingNotReadMessages() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				messagesAdapter.showNotReadMessages();
			}
		};
	}

	private OnClickListener createOptionRemovingAllPoints() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new TaskDeletingAllMessages(SystemNotificationsScreen.this, messagesTable, SystemNotificationsScreen.this).execute(new Void[]{});
			}
		};
	}

	@Override
	protected int getContentViewId() {
		return R.layout.system_notifications_screen;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		messagesListFragment = (SystemNotificationsListFragment)getSupportFragmentManager().findFragmentById(R.id.system_notifications);
		messagesListFragment.setListAdapter(messagesAdapter = new SystemNotificationsListAdapter(this));
		registerForContextMenu(messagesListFragment.getListView());
		setActionBarTitle(R.string.notifications_screen_title);
	}

	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();

		new TaskLoadingMessages(this, messagesTable, this).execute(new Void[]{});
	}

	@Override
	public void onMessagesLoaded(List<SystemNotification> messages) {
		if (messages.size() > 0) {
			messagesAdapter.setAllMessages(messages);
		} else {
			messagesListFragment.setEmptyText(getResources().getString(R.string.zero_messages));
		}
	}

	@Override
	public void onAllMessagesRemoved() {
		messagesAdapter.setAllMessages(new LinkedList<SystemNotification>());
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.messages_list_fragment_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		SystemNotification clickedMessage = getMessageFromView(info.targetView);


		if (item.getItemId() == R.id.read) {
			showMessage(clickedMessage);
		} else if (item.getItemId() == R.id.set_as_read) {
			clickedMessage.setRead(true);
			new TaskUpdatingMessage(this, clickedMessage, messagesTable, this).execute(new Void[]{});
		} else if (item.getItemId() == R.id.set_as_not_read) {
			clickedMessage.setRead(false);
			new TaskUpdatingMessage(this, clickedMessage, messagesTable, this).execute(new Void[]{});
		} else if (item.getItemId() == R.id.delete) {
			new TaskDeletingMessage(this, clickedMessage, messagesTable, this).execute(new Void[]{});
		}

		return super.onContextItemSelected(item);
	}	

	private SystemNotification getMessageFromView(View view) {
		Preconditions.checkArgument(view instanceof SystemNotificationRowView , "Not SystemNotificationRowView");
		SystemNotification clickedMessage = ((SystemNotificationRowView)view).getMessage();
		return clickedMessage;
	}


	public void showMessage(SystemNotification message) {
		Intent intent = new Intent(this, SystemNotificationPreviewScreen.class);
		intent.putExtra(message.getClass().toString(), message);
		intent.putExtra(List.class.toString(), Lists.newArrayList(messagesAdapter.getDisplayedMessages()));
		int i = 0;
		for (SystemNotification m : messagesAdapter.getDisplayedMessages()) {
			if (m.getDatabaseId() == message.getDatabaseId()) {
				intent.putExtra(SystemNotificationPreviewScreen.CHOSEN_NOTIFICATION_INDEX, i);
			}
			++i;
		}
		startActivity(intent);
	}

	@Override
	public void onMessageRemoved(SystemNotification removedMessage) {
		messagesAdapter.removeMessage(removedMessage);
	}

	@Override
	public void onMessageUpdated(SystemNotification updatedMessage) {
		messagesAdapter.updateMessage(updatedMessage);
	}

	@Override
	public void addMessage(final SystemNotification message) {
		super.addMessage(message);
		runOnUiThread(new Runnable(	) {

			@Override
			public void run() {
				messagesAdapter.addMessage(message);
			}
		});
	}

}
