package com.waynell.dialerbox;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class AboutActivity extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.pref_about);
		findPreference("version").setSummary(BuildConfig.VERSION_NAME);

		Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
				"mailto", "ywj2167917@gmail.com", null));
		emailIntent.putExtra(Intent.EXTRA_SUBJECT,
				getString(R.string.app_name) + "-" + BuildConfig.VERSION_NAME);
		findPreference("author").setIntent(emailIntent);

		Intent sourceIntent = new Intent(Intent.ACTION_VIEW);
		sourceIntent.setData(Uri.parse(getString(R.string.source_code_url)));
		findPreference("source").setIntent(sourceIntent);
	}
}
