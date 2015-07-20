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

	private static String getCurrentCountryIso(Context context, ClassLoader loader) {
		Class<?> classGeo = XposedHelpers.findClass(CLASS_GEO_UTIL, loader);
		return (String) XposedHelpers.callStaticMethod(classGeo, "getCurrentCountryIso", new Class[]{Context.class}, context);
	}

    public static String getGeocodedLocationFor(Context context, String phoneNumber, ClassLoader classLoader, boolean isForChina) {
        try {
			final String currentIso = isForChina ? Locale.CHINA.getCountry() : getCurrentCountryIso(context, classLoader);
			final Locale locale = isForChina ? Locale.CHINA : context.getResources().getConfiguration().locale;
            return getDescriptionForNumber(parsePhoneNumber(phoneNumber, currentIso), locale);
        } catch (NumberParseException e) {
            return null;
        }
    }

	static Phonenumber.PhoneNumber parsePhoneNumber(String number, String currentIso) throws NumberParseException {
		final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
		return phoneNumberUtil.parse(number, currentIso);
	}

	static String getDescriptionForNumber(Phonenumber.PhoneNumber number, Locale languageCode) {
		final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();
		final PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();
		PhoneNumberUtil.PhoneNumberType numberType = phoneNumberUtil.getNumberType(number);
		if (numberType == PhoneNumberUtil.PhoneNumberType.UNKNOWN) {
			return "";
		} else {
			return geocoder.getDescriptionForValidNumber(number, languageCode);
		}
	}
}
