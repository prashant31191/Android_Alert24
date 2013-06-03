package com.kremski.alert24.screens;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.widget.AdapterView.AdapterContextMenuInfo;

import com.google.android.gms.maps.model.LatLng;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.kremski.alert24.R;
import com.kremski.alert24.adapters.PointOfInterestAdapter;
import com.kremski.alert24.dialogs.AddPointOfInterestDialog;
import com.kremski.alert24.dialogs.AddPointOfInterestDialog.OnPointAcceptListener;
import com.kremski.alert24.domain.Event;
import com.kremski.alert24.domain.PointOfInterest;
import com.kremski.alert24.screens.event_screens.AddressRetrivedHandler;
import com.kremski.alert24.serwer.service.WorkerRetrivingAddress;
import com.kremski.alert24.serwer.service.WorkerSendingEvent;
import com.kremski.alert24.tasks.TaskInsertingPointOfInterest;
import com.kremski.alert24.tasks.TaskDeletingAllPointsOfInterest;
import com.kremski.alert24.tasks.TaskInsertingPointOfInterest.OnPointInsertedListener;
import com.kremski.alert24.tasks.TaskLoadingPointsOfInterest;
import com.kremski.alert24.tasks.TaskLoadingPointsOfInterest.OnPointsLoadedListener;
import com.kremski.alert24.tasks.TaskDeletingAllPointsOfInterest.OnAllPointsRemovedListener;
import com.kremski.alert24.tasks.TaskDeletingPointOfInterest.OnPointRemovedListener;
import com.kremski.alert24.tasks.TaskDeletingPointOfInterest;
import com.kremski.alert24.views.ActionBarOption;
import com.kremski.alert24.views.EventRowView;
import com.kremski.alert24.views.PointOfInterestRowView;
import com.kremski.alert24.views.PointsOfInterestListFragment;

public class PointsOfInterestScreen extends Alert24Screen implements OnPointsLoadedListener, OnPointAcceptListener, OnPointInsertedListener, OnAllPointsRemovedListener, OnPointRemovedListener{

	private PointOfInterestAdapter pointsOfInterestAdapter;
	private PointsOfInterestListFragment pointsListFragment;
	private int SELECT_LOCATION_REQUEST_CODE = 0;

	@Override
	protected void prepareActionBarOptions(LinkedList<ActionBarOption> actionbarChoosableOptions) {
		super.prepareActionBarOptions(actionbarChoosableOptions);
		
		ActionBarOption addPointOfInterest = new ActionBarOption(R.drawable.add_point_of_interest, R.string.add_point_of_interest_action, createOptionAddingNewPointOfInterest());
		actionbarChoosableOptions.add(addPointOfInterest);
		
		ActionBarOption removeAllPoints =   new ActionBarOption(R.drawable.delete, R.string.remove_points_of_interest, createOptionRemovingAllPoints());
		actionbarChoosableOptions.add(removeAllPoints);
	}

	private OnClickListener createOptionAddingNewPointOfInterest() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				startActivityForResult(new Intent(PointsOfInterestScreen.this, PickLocationScreen.class), 
						SELECT_LOCATION_REQUEST_CODE);
			}
		};
	}
	
	private OnClickListener createOptionRemovingAllPoints() {
		return new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new TaskDeletingAllPointsOfInterest(PointsOfInterestScreen.this, 
						pointsOfInterestTable, PointsOfInterestScreen.this).execute(new Void[]{});
			}
		};
	}

	@Override
	protected int getContentViewId() {
		return R.layout.points_of_interest_screen;
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public void onCreate(Bundle savedInstance) {
		super.onCreate(savedInstance);
		pointsListFragment = (PointsOfInterestListFragment)
				getSupportFragmentManager().findFragmentById(R.id.points_of_interest);
		pointsListFragment.setListAdapter(pointsOfInterestAdapter = new PointOfInterestAdapter(this));
		registerForContextMenu(pointsListFragment.getListView());
		setActionBarTitle(R.string.point_of_interest_screen_short_title);
	}
	
	@Override
	protected void onServiceConnected() {
		super.onServiceConnected();
		
		new TaskLoadingPointsOfInterest(this, pointsOfInterestTable, this).execute(new Void[]{});
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if (resultCode == Activity.RESULT_OK) {
			if (requestCode == SELECT_LOCATION_REQUEST_CODE ){
				showAddPointDialog(intent);
			}
		}
	}
	
	@Override
	protected void handleReceivedNotification(String notificationName, Intent intent) {
	}

	@Override
	public void onPointsLoaded(List<PointOfInterest> points) {
		if (points.size() > 0 ) {
			pointsOfInterestAdapter.setDisplayedPointOfInterests(points);
		} else {
			pointsListFragment.setEmptyText(getResources().getString(R.string.zero_points_of_interest));
		}
	}
	
	private void showAddPointDialog(Intent intentWithLocation) {
		LatLng location = intentWithLocation.getParcelableExtra(LatLng.class.toString());
		
		AddPointOfInterestDialog addPointDialog = AddPointOfInterestDialog.newInstance(this, location, optionsManager);
		FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
		fragmentTransaction.add(addPointDialog, null);
		fragmentTransaction.commitAllowingStateLoss();	
		
		registerWorkerResultHandler(WorkerRetrivingAddress.class.toString(), new AddressRetrivedHandler(this, addPointDialog));
		WorkerRetrivingAddress worker = new WorkerRetrivingAddress(this, new LatLng(location.latitude, location.longitude));
		executeWorker(worker);
	}

	@Override
	public void onPointAccept(PointOfInterest acceptedPoint) {
		if (acceptedPoint.getName() == null || acceptedPoint.getName().equals("")){
			dialogManager.showErrorDialog(R.string.incorrect_name);
		} else {
			new TaskInsertingPointOfInterest(this, acceptedPoint, pointsOfInterestTable, this).execute(new Void[]{});
		}
	}

	@Override
	public void onPointInserted(PointOfInterest insertedPoint) {
		pointsOfInterestAdapter.addPoint(insertedPoint);
	}

	@Override
	public void onAllPointsRemoved() {
		pointsOfInterestAdapter.setDisplayedPointOfInterests(new LinkedList<PointOfInterest>());
	}
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		getMenuInflater().inflate(R.menu.points_of_interest_list_fragment_context_menu, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo)item.getMenuInfo();
		PointOfInterest clickedPoint = getPointFromView(info.targetView);

		if (item.getItemId() == R.id.show_point) {
			showPoint(clickedPoint);
		} else if (item.getItemId() == R.id.delete_point) {
			deletePoint(clickedPoint);
		} 
		
		return super.onContextItemSelected(item);
	}	
	
	private PointOfInterest getPointFromView(View view) {
		Preconditions.checkArgument(view instanceof PointOfInterestRowView , "Not PointOfInterestRowView");
		PointOfInterest clickedPoint = ((PointOfInterestRowView)view).getPoint();
		return clickedPoint;
	}
	
	private void showPoint(PointOfInterest point) {
		Intent intent = new Intent(this, PointOfInterestPreviewScreen.class);
		intent.putExtra(point.getClass().toString(), point);
		intent.putExtra(List.class.toString(), Lists.newArrayList(pointsOfInterestAdapter.getAllPoints()));
		int i = 0;
		for (PointOfInterest p : pointsOfInterestAdapter.getAllPoints()) {
			if (p.getDatabaseId() == point.getDatabaseId()) {
				intent.putExtra(PointOfInterestPreviewScreen.CHOSEN_POINT_INDEX, i);
			}
			++i;
		}
		startActivity(intent);
	}
	
	private void deletePoint(PointOfInterest point) {
		new TaskDeletingPointOfInterest(this, point, pointsOfInterestTable, this).execute(new Void[]{});
		
	}
	
	@Override
	public void onPointRemoved(PointOfInterest removedPoint) {
		pointsOfInterestAdapter.removePointOfInterest(removedPoint);
	}
}
