package com.kremski.alert24.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.base.Preconditions;
import com.kremski.alert24.R;

public class DialogPopup extends DialogFragment {

	private static final int LAYOUT_ID = R.layout.dialog_popup;

	private int[] idsOfTextToDisplay;
	private int idOfImageToDisplay;
		private int typeLabelTextId;
	private String errorMessage;
	private Context context;

	public static class DialogPopupBuilder {
		private int idOfImageToDisplay;
		private int[] idsOfTextToDisplay;
		private int typeLabelTextId;
		private String message;
		private Context context;
		
		public DialogPopupBuilder typeLabelTextId(int typeLabelTextId) {
			this.typeLabelTextId = typeLabelTextId;
			return this;
		}

		public DialogPopupBuilder textIds(int...idOfTextToDisplay ) {
			this.idsOfTextToDisplay = idOfTextToDisplay;
			return this;
		}

		public DialogPopupBuilder typeLabelImageId(int idOfImageToDisplay) {
			this.idOfImageToDisplay = idOfImageToDisplay;
			return this;
		}

		public DialogPopupBuilder context(Context context) {
			this.context = context;
			return this;
		}

		public DialogPopup build() {
			return newInstance(context, message, idsOfTextToDisplay, idOfImageToDisplay, typeLabelTextId);
		}

		public DialogPopupBuilder errorMessage(String errorMessage) {
			this.message = errorMessage;
			return this;
		}
	}

	private static DialogPopup newInstance(Context context, String errorMessage, int[] idsOfTextToDisplay, int idOfImageToDisplay, int typeLabelTextId) {
		Preconditions.checkNotNull(context);

		DialogPopup dialog = new DialogPopup();
		dialog.errorMessage = errorMessage;
		dialog.idsOfTextToDisplay = idsOfTextToDisplay;
		dialog.idOfImageToDisplay = idOfImageToDisplay;
		dialog.typeLabelTextId = typeLabelTextId;
		dialog.context = context;

		return dialog;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ViewGroup layout = (ViewGroup)inflater.inflate(LAYOUT_ID, container);

		setUpLabelWithDisplayedText(layout);

		ImageView labelWithDisplayedImage = (ImageView)layout.findViewById(R.id.image);
		labelWithDisplayedImage.setBackgroundResource(idOfImageToDisplay);
		
		TextView typeLabel = (TextView)layout.findViewById(R.id.type);
		typeLabel.setText(typeLabelTextId);

		setUpOkButtonListener(layout);

		return layout;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setStyle(STYLE_NO_TITLE, 0); // remove title from dialogfragment
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		Dialog createdDialog = super.onCreateDialog(savedInstanceState);
		createdDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
		return createdDialog;
	}

	private void setUpLabelWithDisplayedText(ViewGroup layout) {
		TextView labelWithDisplayedText = (TextView)layout.findViewById(R.id.text);

		if (errorMessage == null || errorMessage.equals("")) {
			errorMessage = getTextToDisplayFromStringResources();
		}
		
		labelWithDisplayedText.setText(errorMessage);
	}
	
	private String getTextToDisplayFromStringResources() {
		Resources res = context.getResources();
		String textToDisplay = "";
		for (int idOfText : idsOfTextToDisplay) {
			textToDisplay += res.getString(idOfText) + " ";
		}
		return textToDisplay;
	}

	private void setUpOkButtonListener(ViewGroup layout) {
		View okButton = layout.findViewById(R.id.ok);
		okButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogPopup.this.dismiss();
			}
		});
	}
}
