package com.example.wei.dial;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;

public class MyService extends Service {
	
	public static final String ACTION_HAND_OFF = "com.example.dial.action_hand_off";

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if (intent != null) {
			String action = intent.getAction();
			if (action.equals(ACTION_HAND_OFF)) {
				new AsyncTask<Void, Void, Void>() {

					@Override
					protected Void doInBackground(Void... params) {

						try {
							Thread.sleep(3000); // Delay 0,5 seconds to handle
												// better turning on loudspeaker
						} catch (InterruptedException e) {
						}

						Log.d("dyw", "activating loudspeaker");
						// Activate loudspeaker
						AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
						// audioManager.setMode(AudioManager.MODE_IN_CALL);
						audioManager.setSpeakerphoneOn(true);

						return null;
					}
				}.execute();
			}
		}
		
		return super.onStartCommand(intent, flags, startId);
	}

}
