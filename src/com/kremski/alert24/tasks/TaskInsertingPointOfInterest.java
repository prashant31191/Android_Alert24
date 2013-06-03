package com.kremski.alert24.tasks;

import android.util.Log;

import com.kremski.alert24.database.PointsOfInterestTable;
import com.kremski.alert24.domain.PointOfInterest;
import com.kremski.alert24.screens.Alert24Screen;

public class TaskInsertingPointOfInterest extends DefaultTask<Void, Void, PointOfInterest>{

	private PointsOfInterestTable pointsOfInterestTable;
	private OnPointInsertedListener pointsLoadedListener;
	private PointOfInterest pointToInsert;

	public TaskInsertingPointOfInterest(Alert24Screen activity, PointOfInterest pointToInsert, PointsOfInterestTable pointsOfInterestTable, OnPointInsertedListener eventsLoadedListener) {
		super(activity);
		this.pointsOfInterestTable = pointsOfInterestTable;
		this.pointsLoadedListener = eventsLoadedListener;
		this.pointToInsert = pointToInsert;
	}

	@Override
	protected PointOfInterest execute() {
		Log.i(LOG_TAG, "Inserting point of interest to database");
		pointToInsert.setDatabaseId(pointsOfInterestTable.insert(pointToInsert));
		Log.i(LOG_TAG, "Point of interest have been inserted into database : " + pointToInsert.toString());
		return pointToInsert;
	}

	@Override
	protected void handleResult(PointOfInterest insertedPoint) {
		pointsLoadedListener.onPointInserted(insertedPoint);
	}

	public interface OnPointInsertedListener {
		public void onPointInserted(PointOfInterest insertedPoint);
	}
}
