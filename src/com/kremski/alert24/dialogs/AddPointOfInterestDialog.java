package com.kremski.alert24.dialogs;

import yuku.ambilwarna.AmbilWarnaDialog;
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Address;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockDialogFragment;
import com.google.android.gms.maps.model.LatLng;
import com.kremski.alert24.R;
import com.kremski.alert24.domain.PointOfInterest;
import com.kremski.alert24.screens.OptionsManager;
import com.kremski.alert24.screens.event_screens.AddressContainer;

public class AddPointOfInterestDialog extends SherlockDialogFragment implements AddressContainer {

	private static final int LAYOUT_ID = R.layout.add_point_of_interest_dialog;
	public static final String TAG = AddPointOfInterestDialog.class.toString();
	private TextView acceptButton;
	private TextView pointNameLabel;
	private ProgressBar addressLoadingProgressBar;
	private TextView pointAddressLabel;
	private TextView pointColorLabel;
	private OnPointAcceptListener onPointAcceptListener;

	private LatLng pointLocation;
	private int pointCircleColor;
	private OptionsManager optionsManager;

	public static AddPointOfInterestDialog newInstance(OnPointAcceptListener onPointAcceptListener, LatLng pointLocation, OptionsManager optionsManager) 	{

		AddPointOfInterestDialog dialog = new AddPointOfInterestDialog();

		dialog.onPointAcceptListener = onPointAcceptListener;
		dialog.pointLocation = pointLocation;
		dialog.optionsManager = optionsManager;

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

		pointAddressLabel = (TextView)layout.findViewById(R.id.address);
		pointNameLabel = (TextView)layout.findViewById(R.id.name);
		addressLoadingProgressBar = (ProgressBar)layout.findViewById(R.id.address_loading_progress_bar);
		acceptButton = (TextView)layout.findViewById(R.id.accept);
		addressLoadingProgressBar.setVisibility(View.VISIBLE);
		pointAddressLabel.setVisibility(View.GONE);
		pointColorLabel = (TextView)layout.findViewById(R.id.color);
		pointColorLabel.setOnClickListener(createColorPickerListener());
		pointColorLabel.setBackgroundColor(optionsManager.getFavoritesLocationDefaultColor());
		pointCircleColor = optionsManager.getFavoritesLocationDefaultColor();
		acceptButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				AddPointOfInterestDialog.this.dismiss();
				String address = pointAddressLabel.getText().toString();
				if (address == null || address.equals("")){
					address = getParsedAddressText(null, pointLocation);
				}
				onPointAcceptListener.onPointAccept(new PointOfInterest(0, pointNameLabel.getText().toString(), pointLocation, address, pointCircleColor));

			}
		});

		return layout;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog createdDialog = super.onCreateDialog(savedInstanceState);
		createdDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		return createdDialog;
	}	

	private View.OnClickListener createColorPickerListener() {
		return new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showDialogToPickColor();
			}
		};
	}

	private void showDialogToPickColor() {
		AmbilWarnaDialog dialog = new AmbilWarnaDialog(getActivity(), pointCircleColor, new OnAmbilWarnaListener()
		{
			@Override
			public void onCancel(AmbilWarnaDialog dialog) {
			}
			@Override
			public void onOk(AmbilWarnaDialog dialog, int color) {
				String hexColor = Integer.toHexString(color);
				pointCircleColor = Color.parseColor("#66" + hexColor.substring(2, hexColor.length()));
				pointColorLabel.setBackgroundColor(pointCircleColor);
			}
		});
		dialog.show();
	}	

	public interface OnPointAcceptListener {
		public void onPointAccept(PointOfInterest acceptedPoint);
	}

	@Override
	public void setAddress(Address address, LatLng latLngSource) {
		addressLoadingProgressBar.setVisibility(View.GONE);
		pointAddressLabel.setVisibility(View.VISIBLE);
		pointAddressLabel.setText(getParsedAddressText(address, latLngSource));
		pointLocation = latLngSource;
	}

	private String getParsedAddressText(Address address, LatLng latLngSource) {
		String textInAddressInputLabel = null;

		if (address != null) {
			textInAddressInputLabel= String.format("%s, %s, %s",
					address.getMaxAddressLineIndex() > 0 ? address.getAddressLine(0) : "", address.getLocality(), address.getCountryName());
		} 
		else if (latLngSource != null) {
			textInAddressInputLabel= String.format("%s:%s\n%s:%s", getResources().getString(R.string.prefix_for_latitude_value), latLngSource.latitude,
					getResources().getString(R.string.prefix_for_longitude_value), latLngSource.longitude);
		} 

		return textInAddressInputLabel;
	}
}
