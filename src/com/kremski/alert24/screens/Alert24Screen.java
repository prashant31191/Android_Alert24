package com.kremski.alert24.screens;


import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ConcurrentMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.darvds.ribbonmenu.RibbonMenuView;
import com.darvds.ribbonmenu.iRibbonMenuCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.kremski.alert24.R;
import com.kremski.alert24.adapters.ActionBarOptionsAdapter;
import com.kremski.alert24.database.EventCategoriesTable;
import com.kremski.alert24.database.EventsTable;
import com.kremski.alert24.database.PointsOfInterestTable;
import com.kremski.alert24.database.SystemNotificationsTable;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.domain.EventCategory;
import com.kremski.alert24.domain.SystemNotification;
import com.kremski.alert24.notifications.NotificationFactory;
import com.kremski.alert24.serwer.ServerUtilities;
import com.kremski.alert24.serwer.command.ServerCommand;
import com.kremski.alert24.serwer.command.ServerCommandFactory;
import com.kremski.alert24.serwer.command.ServerCommandHandler;
import com.kremski.alert24.serwer.service.Alert24Service;
import com.kremski.alert24.serwer.service.Alert24Service.Alert24ServiceBinder;
import com.kremski.alert24.serwer.service.Alert24Service.Worker;
import com.kremski.alert24.serwer.service.WorkerDownloadingEvents;
import com.kremski.alert24.serwer.service.WorkerResult;
import com.kremski.alert24.serwer.service.WorkerResultHandler;
import com.kremski.alert24.serwer.service.WorkerResultHandlerFactory;
import com.kremski.alert24.tasks.TaskExecutingServerCommand;
import com.kremski.alert24.views.ActionBarOption;
import com.kremski.alert24.views.image.DefaultImageManager;
import com.kremski.alert24.views.image.ImageManager;
import com.kremski.alert24.views.image.ImageUrlResolver;
import com.kremski.alert34.resolvers.EventCategoryToImageUrlResolver;
import com.kremski.alert34.resolvers.EventToImageUrlResolver;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

public abstract class Alert24Screen extends SherlockFragmentActivity implements iRibbonMenuCallback {

	private WorkerResultHandlerFactory handlerFactory = new WorkerResultHandlerFactory();
	protected DefaultImageManager<Event> eventImageManager;
	protected DefaultImageManager<EventCategory> eventCategoryImageManager; 
	protected DefaultImageManager<String> eventPhotoImageManager; 

	protected EventsTable eventTableHelper;
	protected EventCategoriesTable eventCategoriesTableHelper;
	protected PointsOfInterestTable pointsOfInterestTable;
	protected SystemNotificationsTable messagesTable;

	protected ServerUtilities serverUtilities; 

	private Alert24Service alert24Service = null;
	protected boolean isAlert24ServiceBounded = false;
	private WorkersBroadcastReceiver workersBroadcastReceiver = new WorkersBroadcastReceiver();

	protected NotificationFactory notificationFactory;
	protected ServerCommandFactory serverCommandFactory;

	protected String LOG_TAG = getClass().toString();

	private RibbonMenuView ribbonMenuView;	

	private ConcurrentMap<String, Boolean> operationsInProgress = Maps.newConcurrentMap();

	private ConcurrentLinkedQueue<Worker> workersToExecute = new ConcurrentLinkedQueue<Alert24Service.Worker>();
	protected OptionsManager optionsManager;
	protected DialogManager dialogManager;
	private RibbonMenuItemClickHandler ribbonMenuItemClickHandler;

	private SystemMessageReceiver systemMessageReceiver;
	protected ActionBarOptionsAdapter actionBarOptionsAdapter;
	private static List<Event> lastDownloadedEvents = Lists.newLinkedList();

	private ServiceConnection alert24ServiceConnection = new ServiceConnection() {

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			isAlert24ServiceBounded = false;
		}

		@Override
		public void onServiceConnected(ComponentName arg0, IBinder binder) {
			Alert24ServiceBinder alert24ServiceBinder = (Alert24ServiceBinder)binder;
			alert24Service = alert24ServiceBinder.getService();
			isAlert24ServiceBounded = true;
			eventTableHelper = alert24Service.getEventTableHelper();
			eventCategoriesTableHelper = alert24Service.getEventCategoriesTableHelper();
			pointsOfInterestTable = alert24Service.getPointsOfInterestTable();
			messagesTable = alert24Service.getSystemNotificationsTable();
			Alert24Screen.this.onServiceConnected();
		}
	};

	protected void onServiceConnected() {
		while(!workersToExecute.isEmpty()) {
			Worker worker = workersToExecute.poll();
			addOperationInProgress(worker.getClass().toString());
			alert24Service.executeWorker(worker);
		}
	}

	protected String getLogTag() {
		return LOG_TAG;
	}

	public synchronized boolean isOperationInProgress(String operationName) {
		Boolean progressStatus = operationsInProgress.get(operationName);
		if (progressStatus != null) {
			return progressStatus;
		} else {
			return false;
		}
	}

	public synchronized void addOperationInProgress(String operationName) {
		operationsInProgress.put(operationName, true);
		showProgressBar();
	}

	public synchronized void removeOperationInProgress(String operationName) {
		operationsInProgress.remove(operationName);

		if (operationsInProgress.isEmpty()) {
			hideProgressBar();
		}
	}

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		requestWindowFeature(com.actionbarsherlock.view.Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(getContentViewId());
		setUpSidebarNavigation();
		optionsManager = new OptionsManager(getSharedPreferences(OptionsManager.PREFERENCES_NAME, MODE_PRIVATE), getResources());
		dialogManager = new DialogManager(this, getSupportFragmentManager());
		ribbonMenuItemClickHandler = new RibbonMenuItemClickHandler(this, this);
		serverUtilities = new ServerUtilities(ServerUtilities.SERWER_ADDRESS);
		notificationFactory = new NotificationFactory(this);
		serverCommandFactory = new ServerCommandFactory();
		systemMessageReceiver = new SystemMessageReceiver();
		initializeImageManagers();
		initializeActionBar();
		
		registerWorkerResultHandler(WorkerDownloadingEvents.class.toString(), new EventsDownloadedHandler(this));
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(workersBroadcastReceiver, new IntentFilter(Alert24Service.BROADCAST_FROM_WORKER));
		registerReceiver(systemMessageReceiver, new IntentFilter(SystemMessageReceiver.SYSTEM_INTENT_ACTION));
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (workersBroadcastReceiver != null) {
			unregisterReceiver(workersBroadcastReceiver);
		}
		if (systemMessageReceiver != null) {
			unregisterReceiver(systemMessageReceiver);
		}
	}

	abstract protected int getContentViewId();

	private void setUpSidebarNavigation() {
		ribbonMenuView = (RibbonMenuView) findViewById(R.id.ribbon_menu);
		ribbonMenuView.setMenuClickCallback(this);
		ribbonMenuView.setMenuItems(R.menu.ribbon_menu);
	}

	private void initializeImageManagers() {
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(getBaseContext()));
		DisplayImageOptions imgOptions = initializeImageOptions();
		eventImageManager = new DefaultImageManager<Event>(this, new EventToImageUrlResolver(), imageLoader, imgOptions);
		eventCategoryImageManager = new DefaultImageManager<EventCategory>(this, new EventCategoryToImageUrlResolver(), imageLoader, imgOptions);
		eventPhotoImageManager = new DefaultImageManager<String>(this, new ImageUrlResolver<String>() {

			@Override
			public String resolve(String objectToResolve) {
				return objectToResolve;
			}
		}, imageLoader, imgOptions);
	}

	private DisplayImageOptions initializeImageOptions() {
		return new DisplayImageOptions.Builder()
		.cacheInMemory()
		.cacheOnDisc()
		.displayer(new RoundedBitmapDisplayer(20))
		.build();
	}

	@Override
	public boolean onOptionsItemSelected(com.actionbarsherlock.view.MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			ribbonMenuView.toggleMenu();
			return true;
		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void RibbonMenuItemClick(int itemId) {
		ribbonMenuItemClickHandler.onClick(itemId);
	}

	private void initializeActionBar() {
		LinkedList<ActionBarOption> actionbarChoosableOptions = Lists.newLinkedList();
		prepareActionBarOptions(actionbarChoosableOptions);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		getSupportActionBar().setListNavigationCallbacks(actionBarOptionsAdapter = 
				new ActionBarOptionsAdapter(this, actionbarChoosableOptions), null);
		getSupportActionBar().setCustomView(getActionBarView());
		getSupportActionBar().setDisplayShowHomeEnabled(false);
		getSupportActionBar().setDisplayShowCustomEnabled(true);
		getSupportActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.primary_1_background));

		hideProgressBar();
	}

	protected View getActionBarView() {
		return LayoutInflater.from(this).inflate(R.layout.actionbar, null);
	}

	private void showProgressBar() {
		setSupportProgressBarIndeterminateVisibility(true);
		getSherlock().setProgressBarIndeterminateVisibility(true);
		getSherlock().setProgressBarVisibility(true);
		getSherlock().setProgressBarIndeterminate(true);
	}

	private void hideProgressBar() {
		setSupportProgressBarIndeterminateVisibility(false);
		getSherlock().setProgressBarIndeterminateVisibility(false);
		getSherlock().setProgressBarVisibility(false);
		getSherlock().setProgressBarIndeterminate(false);
	}

	protected void setActionBarTitle(int resourceIdWithTitle) {
		getSupportActionBar().setTitle(resourceIdWithTitle);
	}

	protected void prepareActionBarOptions(LinkedList<ActionBarOption> actionbarChoosableOptions) {}

	public void registerWorkerResultHandler(String key, WorkerResultHandler handler) {
		handlerFactory.registerWorkerResultHandler(key, handler);
	}

	@Override
	protected void onStart() {
		super.onStart();
		Intent intent = new Intent(this, Alert24Service.class);
		bindService(intent, alert24ServiceConnection, Context.BIND_AUTO_CREATE);

		Intent intentWhichStartedActivity = getIntent();
		if (intentWhichStartedActivity != null) {
			String action = intentWhichStartedActivity.getAction();
			if (NotificationFactory.NOTIFICATION_BROADCAST.equals(action)) {
				String notificationName = intentWhichStartedActivity.getExtras().getString(NotificationFactory.NOTIFICATION_TYPE_KEY);
				handleReceivedNotification(notificationName, intentWhichStartedActivity);
			}
		}
	}

	protected void handleReceivedNotification(String notificationName, Intent intent) {

	}

	public void executeWorker(Worker workerToExecute) {
		if (isOperationInProgress(workerToExecute.getClass().toString())) {
			dialogManager.showInfoDialog(R.string.operation_in_progress);
		} else { 
			if (alert24Service != null) {
				addOperationInProgress(workerToExecute.getClass().toString());
				alert24Service.executeWorker(workerToExecute);
			} else {
				workersToExecute.add(workerToExecute);
			}

		}
	}

	public void executeWorker(Worker workerToExecute, int...infoDialogStringResource) {
		if (isOperationInProgress(workerToExecute.getClass().toString())) {
			dialogManager.showInfoDialog(R.string.operation_in_progress);
		} else { 
			
			StringBuilder messageInDialog = new StringBuilder();
			Resources r = getResources();
			for (int id : infoDialogStringResource) { 
				messageInDialog.append(r.getString(id));
			}
			
			dialogManager.showInfoDialog(messageInDialog.toString());
			
			if (alert24Service != null) {
				addOperationInProgress(workerToExecute.getClass().toString());
				alert24Service.executeWorker(workerToExecute);
			} else {
				workersToExecute.add(workerToExecute);
			}
		}
	}

	protected void fireNotification(final Notification notification) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
				notificationManager.notify(0, notification);
			}
		});
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (isAlert24ServiceBounded) {
			unbindService(alert24ServiceConnection);
			isAlert24ServiceBounded = false;
		}
	}

	public ImageManager<Event> getImageManager() {
		return eventImageManager;
	}

	public EventsTable getEventsTableHelper() {
		return eventTableHelper;
	}

	public boolean isDeviceOnline() {
		ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnected()) {
			return true;
		}
		return false;
	}

	public Alert24Service getAlert24Service() {
		return alert24Service;
	}

	public EventCategoriesTable getEventCategoriesTableHelper() {
		return eventCategoriesTableHelper;
	}

	public ServerUtilities getServerUtilities() {
		return serverUtilities;
	}

	public ImageManager<EventCategory> getEventCategoryImageManager() {
		return eventCategoryImageManager;
	}
	
	public ImageManager<String> getEventPhotoImageManager() {
		return eventPhotoImageManager;
	}

	public void showErrorDialog(String errorMessageToShowUponError) {
		dialogManager.showErrorDialog(errorMessageToShowUponError);
	}

	public boolean isAlert24ServiceBounded() {
		return isAlert24ServiceBounded;
	}
	
	public void onEventsDownloaded(List<Event> downloadedEvents) {
		setDownloadedEvents(downloadedEvents);
	}
	
	private synchronized void setDownloadedEvents(List<Event> downloadedEvents) {
		lastDownloadedEvents = Lists.newLinkedList(downloadedEvents);
	}
	
	protected synchronized List<Event> getDownloadedEvents() {
		return Lists.newLinkedList(lastDownloadedEvents);
	}

	protected LatLng getLastKnownLocation() {
		LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);  
		List<String> providers = lm.getProviders(true);

		/* Loop over the array backwards, and if you get an accurate location, then break out the loop*/
		Location l = null;

		for (int i=providers.size()-1; i>=0; i--) {
			l = lm.getLastKnownLocation(providers.get(i));
			if (l != null) break;
		}

		double[] gps = new double[2];
		if (l != null) {
			return new LatLng(l.getLatitude(), l.getLongitude());
		}
		return null;
	}	

	private class WorkersBroadcastReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(Alert24Service.BROADCAST_FROM_WORKER)) {

				String broadcastingWorkerName = intent.getExtras().getString(Alert24Service.BROADCASTING_WORKER_NAME);
				WorkerResult workerExecutionResult = intent.getExtras().getParcelable(Alert24Service.WORKER_EXECUTION_RESULT);

				WorkerResultHandler resultHandler = handlerFactory.getWorkerResultHandler(broadcastingWorkerName);
				if (resultHandler != null) {
					resultHandler.handleWorkerResult(workerExecutionResult, intent);
				}
			}
		}
	}

	public class SystemMessageReceiver extends BroadcastReceiver implements ServerCommandHandler {

		private final String LOG_TAG = SystemMessageReceiver.class.toString();
		public static final String SYSTEM_INTENT_ACTION = "com.kremski.alert24.intent.SYSTEM_MESSAGE";
		public static final String SYSTEM_MESSAGE_OBJECT_KEY = "SystemNotification";
		
		@Override
		public void onReceive(Context context, Intent intent) {
			if (intent.getAction().equals(SYSTEM_INTENT_ACTION)) {
				handleReceivedMessageFromServer(intent);
			}
		}

		private void handleReceivedMessageFromServer(Intent intentWithMessageFromServer) {
			String serverMessage = intentWithMessageFromServer.getExtras().getString(SYSTEM_MESSAGE_OBJECT_KEY);
			try {
				JSONObject serverJsonCommand = new JSONObject(serverMessage);
				if (serverCommandFactory.isServerCommand(serverJsonCommand)) {
					ServerCommand serverCommand = serverCommandFactory.createCommand(this, serverJsonCommand);
					new TaskExecutingServerCommand(Alert24Screen.this, serverCommand).execute(new Void[]{});
				}
			} catch (JSONException e) {
				Log.i(LOG_TAG, e.getLocalizedMessage());
			}
		}

		@Override
		public void addEvent(Event event) {
		}

		@Override
		public void addMessage(SystemNotification message) {
			Alert24Screen.this.addMessage(message);
		}
	}

	public void addEvent(Event event) {

	}

	public void addMessage(SystemNotification message) {
		messagesTable.insert(message);
	}

	public void showInfoDialog(int eventCategoriesDownloadedMessageId) {
		dialogManager.showInfoDialog(eventCategoriesDownloadedMessageId);
	}

}
