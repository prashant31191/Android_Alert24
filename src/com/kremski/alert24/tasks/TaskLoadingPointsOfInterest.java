package com.kremski.alert24.tasks;

import java.util.List;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.kremski.alert24.database.PointsOfInterestTable;
import com.kremski.alert24.domain.PointOfInterest;
import com.kremski.alert24.screens.Alert24Screen;

public class TaskLoadingPointsOfInterest extends DefaultTask<Void, Void, List<PointOfInterest>>{

	private PointsOfInterestTable pointsOfInterestTable;
	private final static String LOG_TAG = TaskLoadingPointsOfInterest.class.toString();
	private OnPointsLoadedListener pointsLoadedListener;

	public TaskLoadingPointsOfInterest(Alert24Screen activity, PointsOfInterestTable pointsOfInterestTable, OnPointsLoadedListener pointsLoadedListener) {
		super(activity);
		this.pointsOfInterestTable = pointsOfInterestTable;
		this.pointsLoadedListener = pointsLoadedListener;
	}

	@Override
	protected List<PointOfInterest> execute() {
		Log.i(LOG_TAG, "Selecting points of interest from database");
		List<PointOfInterest> points = pointsOfInterestTable.getAll();
		Log.i(LOG_TAG, "Points of interest have been selected : " + points.toString());
		return points; 
	}
	
	@Override
	protected void handleResult(List<PointOfInterest> result) {
		pointsLoadedListener.onPointsLoaded(result);
	}

	public interface OnPointsLoadedListener {
		public void onPointsLoaded(List<PointOfInterest> points);
	}
}
