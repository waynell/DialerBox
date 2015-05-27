package com.waynell.dialerbox;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import android.content.Context;

import java.util.Locale;

import de.robv.android.xposed.XposedHelpers;

/**
 * Static methods related to Geo.
 */
public class GeocoderUtils {

	// Contact Common
	private static final String CLASS_GEO_UTIL = "com.android.contacts.common.GeoUtil";

    public static String getCurrentCountryIso(Context context, ClassLoader loader) {
		Class<?> classGeo = XposedHelpers.findClass(CLASS_GEO_UTIL, loader);
		return (String) XposedHelpers.callStaticMethod(classGeo, "getCurrentCountryIso", new Class[]{Context.class}, context);
    }

    public static String getGeocodedLocationFor(Context context, ClassLoader loader, String phoneNumber) {
        final PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();
        final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
        try {
            final Phonenumber.PhoneNumber structuredPhoneNumber =
                    phoneNumberUtil.parse(phoneNumber, getCurrentCountryIso(context, loader));
            final Locale locale = context.getResources().getConfiguration().locale;
            return geocoder.getDescriptionForNumber(structuredPhoneNumber, locale);
        } catch (NumberParseException e) {
            return null;
        }
    }
}
