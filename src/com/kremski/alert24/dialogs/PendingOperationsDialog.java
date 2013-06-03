package com.kremski.alert24.dialogs;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.kremski.alert24.R;
import com.kremski.alert24.adapters.EventCategoriesListAdapter;
import com.kremski.alert24.domain.EventCategory;

public class PendingOperationsDialog extends SherlockDialogFragment {

	private static final int LAYOUT_ID = R.layout.event_categories_dialog;
	public static final String TAG = PendingOperationsDialog.class.toString();
	private EventCategoriesListAdapter eventCategoriesAdapter;
	private ExpandableListView listWithEventCategories;
	private TextView acceptButton;
	private OnTypeAcceptListener onTypeAcceptListener;

	public static class PendingOperationsDialogBuilder {
		private EventCategoriesListAdapter eventCategoriesAdapter;
		private OnTypeAcceptListener onTypeAcceptListener;

		public PendingOperationsDialogBuilder eventCategoriesListAdapter(EventCategoriesListAdapter eventCategoriesListAdapter) {
			this.eventCategoriesAdapter = eventCategoriesListAdapter;
			return this;
		}

		public PendingOperationsDialogBuilder onTypeAcceptListener(OnTypeAcceptListener onTypeAcceptListener) {
			this.onTypeAcceptListener = onTypeAcceptListener;
			return this;
		}

		public PendingOperationsDialog build() {
			return newInstance(eventCategoriesAdapter, onTypeAcceptListener);
		}
	}

	public static PendingOperationsDialog newInstance(EventCategoriesListAdapter eventCategoriesListAdapter, OnTypeAcceptListener onTypeAcceptListener) 	{

		PendingOperationsDialog dialog = new PendingOperationsDialog();

		dialog.eventCategoriesAdapter = eventCategoriesListAdapter;
		dialog.onTypeAcceptListener = onTypeAcceptListener;

		return dialog;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup layout = (ViewGroup) inflater.inflate(LAYOUT_ID, container);

		listWithEventCategories = (ExpandableListView) layout.findViewById(R.id.event_categories_list);
		listWithEventCategories.setAdapter(eventCategoriesAdapter);

		acceptButton = (TextView)layout.findViewById(R.id.accept);
		acceptButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				PendingOperationsDialog.this.dismiss();
				onTypeAcceptListener.onTypeAccept(eventCategoriesAdapter.getChosenEventCategory());
			}
		});

		return layout;
	}

	public interface OnTypeAcceptListener {
		public void onTypeAccept(EventCategory eventCategory);
	}
}
