package com.waynell.dialerbox;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class SettingsActivity extends PreferenceActivity {

	public static final String KEY_CALL_LOG_ATTR = "call_log_attr";

	public static final String KEY_CALL_INCOMING_ATTR = "call_incoming_attr";

	public static final String KEY_CALL_OUTGOING_ATTR = "call_outgoing_attr";

	public static final String KEY_CALL_VIBRATION_CONNECTED = "call_vibration_connected";

	public static final String KEY_CALL_VIBRATION_DISCONNECTED = "call_vibration_disconnected";

	public static final String KEY_CALL_VIBRATION_WAITING = "call_vibration_waiting";

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
		addPreferencesFromResource(R.xml.pref_general);
	}
}
