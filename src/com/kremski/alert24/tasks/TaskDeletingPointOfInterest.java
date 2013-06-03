package com.kremski.alert24.tasks;

import android.util.Log;

import com.kremski.alert24.database.PointsOfInterestTable;
import com.kremski.alert24.domain.PointOfInterest;
import com.kremski.alert24.screens.Alert24Screen;

public class TaskDeletingPointOfInterest extends DefaultTask<Void, Void, Void>{

	private PointsOfInterestTable pointsOfInterestTable;
	private OnPointRemovedListener onPointsRemovedListener;
	private PointOfInterest pointToRemove;

	public TaskDeletingPointOfInterest(Alert24Screen activity, PointOfInterest pointToRemove, PointsOfInterestTable pointsOfInterestTable, OnPointRemovedListener onPointsRemovedListener) {
		super(activity);
		this.pointsOfInterestTable = pointsOfInterestTable;
		this.onPointsRemovedListener = onPointsRemovedListener;
		this.pointToRemove = pointToRemove;
	}

	@Override
	protected Void execute() {
		Log.i(LOG_TAG, "Removing point of interest from database");
		pointsOfInterestTable.delete(pointToRemove);
		Log.i(LOG_TAG, "Point of interest have been delete");
		return null; 
	}

	@Override
	protected void handleResult(Void result) {
		onPointsRemovedListener.onPointRemoved(pointToRemove);

	}

	public interface OnPointRemovedListener {
		public void onPointRemoved(PointOfInterest removedPoint);
	}
}
