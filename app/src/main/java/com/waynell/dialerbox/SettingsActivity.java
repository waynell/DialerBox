package com.waynell.dialerbox;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class SettingsActivity extends PreferenceActivity {

	public static final String KEY_CALL_LOG_ATTR = "call_log_attr";

	public static final String KEY_CALL_INCOMING_ATTR = "call_incoming_attr";

	public static final String KEY_CALL_VIBRATION_CONNECTED = "call_vibration_connected";

	public static final String KEY_CALL_VIBRATION_DISCONNECTED = "call_vibration_disconnected";

	public static final String KEY_CALL_VIBRATION_WAITING = "call_vibration_waiting";

	public static final String KEY_SMART_DIAL = "smart_dial";

	public static final String KEY_DIAL_PAD_SHOW = "dial_pad_show";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
		addPreferencesFromResource(R.xml.pref_general);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void about(MenuItem item) {
		Intent intent = new Intent(this, AboutActivity.class);
		startActivity(intent);
	}
}
