package com.kremski.alert24.serwer.service;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;

import com.google.android.gms.maps.model.LatLng;
import com.kremski.alert24.database.EventCategoriesTable;
import com.kremski.alert24.database.EventsTable;
import com.kremski.alert24.database.SystemNotificationsTable;
import com.kremski.alert24.database.PointsOfInterestTable;
import com.kremski.alert24.serwer.ServerUtilities;

public class Alert24Service extends Service{

	private final Alert24ServiceBinder binder = new Alert24ServiceBinder();
	public final static int INTENT_WITH_SENDING_EVENT_RESULT = 0;
	public static final String BROADCAST_FROM_WORKER = "BROADCAST_FROM_WORKER";
	public static final String BROADCASTING_WORKER_NAME = "BROADCASTING_WORKER_NAME";
	public static final String WORKER_EXECUTION_RESULT = "WORKER_EXECUTION_RESULT";

	private Timer downloadingEventsTimer;
	private TimerTask taskDownloadingEventsFromServer;
	private WorkerDownloadingEvents workerDownloadingEventsFromServer;
	private int refreshInterval;
	boolean isDownloadingEvents = false;
	
	private EventsTable eventTableHelper;
	private EventCategoriesTable eventCategoriesTableHelper;
	private PointsOfInterestTable pointsOfInterestTable;
	private SystemNotificationsTable systemNotificationsTable;
	
	@Override
	public void onCreate() {
		super.onCreate();
		eventTableHelper = new EventsTable(getApplicationContext());
		eventTableHelper.openDatabase();
		eventCategoriesTableHelper = new EventCategoriesTable(getApplicationContext());
		eventCategoriesTableHelper.openDatabase();
		pointsOfInterestTable = new PointsOfInterestTable(getApplicationContext());
		pointsOfInterestTable.openDatabase();
		systemNotificationsTable = new SystemNotificationsTable(getApplicationContext());
		systemNotificationsTable.openDatabase();
	}
	
	public Timer getDownloadingEventsTimer() {
		return downloadingEventsTimer;
	}

	public EventsTable getEventTableHelper() {
		return eventTableHelper;
	}

	public EventCategoriesTable getEventCategoriesTableHelper() {
		return eventCategoriesTableHelper;
	}
	
	public SystemNotificationsTable getSystemNotificationsTable() {
		return systemNotificationsTable;
	}

	public PointsOfInterestTable getPointsOfInterestTable() {
		return pointsOfInterestTable;
	}
	
	public void startDownloadingEvents(Context context, List<LatLng> pointsOfInterestCoords, ServerUtilities serverUtilities,
			int radiusInMeters,  int refreshIntervalInMinutes) {
		refreshInterval = refreshIntervalInMinutes;
		workerDownloadingEventsFromServer = new WorkerDownloadingEvents(context, pointsOfInterestCoords, radiusInMeters, serverUtilities);

		taskDownloadingEventsFromServer = new TaskDownloadingEventsFromServer(workerDownloadingEventsFromServer);

		downloadingEventsTimer = new Timer();
		//TODO po wyborze zdjecia nie pobieraja sie juz eventy
		downloadingEventsTimer.schedule(taskDownloadingEventsFromServer, 0, 10000);
		isDownloadingEvents = true;
	}

	public void stopDownloadingEvents() {
		if (downloadingEventsTimer != null) {
			downloadingEventsTimer.cancel();
			isDownloadingEvents = false;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return binder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	@Override
	public void onDestroy() {
		eventTableHelper.closeDatabase();
		eventCategoriesTableHelper.closeDatabase();
		pointsOfInterestTable.closeDatabase();
		systemNotificationsTable.closeDatabase();
		stopDownloadingEvents();
		super.onDestroy();
	}

	public class Alert24ServiceBinder extends Binder {
		public Alert24Service getService() {
			return Alert24Service.this;
		}
	}

	public void executeWorker(final Worker workerToExecute) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				workerToExecute.execute();
			}
		}).start();
	}

	public static interface Worker {
		public void execute();
	}

	private class TaskDownloadingEventsFromServer extends TimerTask {

		public TaskDownloadingEventsFromServer(WorkerDownloadingEvents workerDownloadingEventsFromServer) {
			Alert24Service.this.workerDownloadingEventsFromServer = workerDownloadingEventsFromServer;
		}

		@Override
		public void run() {
			new Thread(new Runnable() {
				@Override
				public void run() {
					workerDownloadingEventsFromServer.execute();
				}
			}).start();
		}
	}

	public void setRefreshRadius(int refreshRadius) {
		stopDownloadingEvents();
		if (workerDownloadingEventsFromServer != null) {
			startDownloadingEvents(workerDownloadingEventsFromServer.getContext(), 
					workerDownloadingEventsFromServer.getPointsOfInterestCoords(), 
					workerDownloadingEventsFromServer.getServerUtilities(), 
					refreshRadius, refreshInterval);
		}
	}

	public void setRefreshInterval(int refreshIntervalInMinutes) {
		refreshInterval = refreshIntervalInMinutes;
		stopDownloadingEvents();
		if (workerDownloadingEventsFromServer != null) {
			startDownloadingEvents(workerDownloadingEventsFromServer.getContext(), 
					workerDownloadingEventsFromServer.getPointsOfInterestCoords(), 
					workerDownloadingEventsFromServer.getServerUtilities(), 
					workerDownloadingEventsFromServer.getRefreshRadius(), refreshInterval);
		}
	}

	public boolean isDownloadingEvents() {
		return isDownloadingEvents;
	}
}
