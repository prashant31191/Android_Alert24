package com.kremski.alert24;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.kremski.alert24.screens.Alert24Screen.SystemMessageReceiver;
import com.kremski.alert24.serwer.ServerUtilities;

public class GCMIntentService extends GCMBaseIntentService {

	private static final String TAG = GCMIntentService.class.toString();
	 
    public GCMIntentService() {
        super(ServerUtilities.SENDER_ID);
        
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {
        Log.i(TAG, "Device registered on gcm server: regId = " + registrationId);
    }
  
    @Override
    protected void onUnregistered(Context context, String registrationId) {
        Log.i(TAG, "Device unregistered");
    }
    
    @Override
    protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "Received message from serwer");
    	Bundle serverBundle = intent.getExtras();
        intent = new Intent(SystemMessageReceiver.SYSTEM_INTENT_ACTION);
        intent.putExtras(serverBundle);
        sendBroadcast(intent);
    }
    
    @Override
    protected void onDeletedMessages(Context context, int total) {
        Log.i(TAG, "Received deleted messages notification");

    }

    @Override
    public void onError(Context context, String errorId) {
        Log.i(TAG, "Received error: " + errorId);
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        Log.i(TAG, "Received recoverable error: " + errorId);
        return super.onRecoverableError(context, errorId);
    }

    
}

