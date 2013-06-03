package com.kremski.alert24.tasks;

import android.util.Log;

import com.kremski.alert24.database.PointsOfInterestTable;
import com.kremski.alert24.screens.Alert24Screen;

public class TaskDeletingAllPointsOfInterest extends DefaultTask<Void, Void, Void>{

	private PointsOfInterestTable pointsOfInterestTable;
	private OnAllPointsRemovedListener onPointsRemovedListener;

	public TaskDeletingAllPointsOfInterest(Alert24Screen activity, PointsOfInterestTable pointsOfInterestTable, OnAllPointsRemovedListener onPointsRemovedListener) {
		super(activity);
		this.pointsOfInterestTable = pointsOfInterestTable;
		this.onPointsRemovedListener = onPointsRemovedListener;
	}

	@Override
	protected Void execute() {
		Log.i(LOG_TAG, "Removing points of interest from database");
		pointsOfInterestTable.deleteAll();
		Log.i(LOG_TAG, "Points of interest have been delete");
		return null; 
	}

	@Override
	protected void handleResult(Void result) {
		onPointsRemovedListener.onAllPointsRemoved();
	}

	public interface OnAllPointsRemovedListener {
		public void onAllPointsRemoved();
	}
}
