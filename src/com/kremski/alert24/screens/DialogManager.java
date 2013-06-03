package com.kremski.alert24.screens;

import android.content.Context;
import android.support.v4.app.FragmentManager;

import com.kremski.alert24.R;
import com.kremski.alert24.dialogs.DialogPopup;

public class DialogManager {

	private FragmentManager fragmentManager;
	private Context context;

	DialogManager(Context context, FragmentManager fragmentManager) {
		this.fragmentManager = fragmentManager;
		this.context = context;
	}

	public void showErrorDialog(int...idsOfTextToDisplay) {
		DialogPopup infoDialog = new DialogPopup.DialogPopupBuilder()
		.context(context)
		.typeLabelImageId(R.drawable.error_icon)
		.typeLabelTextId(R.string.error)
		.textIds(idsOfTextToDisplay)
		.build();
		infoDialog.show(fragmentManager, DialogPopup.class.toString());
	}

	public void showErrorDialog(String errorMessage) {
		DialogPopup infoDialog = new DialogPopup.DialogPopupBuilder()
		.context(context)
		.typeLabelImageId(R.drawable.error_icon)
		.typeLabelTextId(R.string.error)
		.errorMessage(errorMessage)
		.build();
		infoDialog.show(fragmentManager, DialogPopup.class.toString());
	}

	public void showInfoDialog(int...idsOfTextToDisplay) {
		DialogPopup infoDialog = new DialogPopup.DialogPopupBuilder()
		.context(context)
		.typeLabelImageId(R.drawable.info_icon)
		.textIds(idsOfTextToDisplay)
		.typeLabelTextId(R.string.info)
		.build();
		infoDialog.show(fragmentManager, DialogPopup.class.toString());
	}

	public void showInfoDialog(String infoMessage) {
		DialogPopup infoDialog = new DialogPopup.DialogPopupBuilder()
		.context(context)
		.typeLabelImageId(R.drawable.info_icon)
		.typeLabelTextId(R.string.info)
		.errorMessage(infoMessage)
		.build();
		infoDialog.show(fragmentManager, DialogPopup.class.toString());
	}
}