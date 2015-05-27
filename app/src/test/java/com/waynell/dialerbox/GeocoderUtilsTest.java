package com.waynell.dialerbox;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.google.i18n.phonenumbers.geocoding.PhoneNumberOfflineGeocoder;

import org.junit.Test;

import java.util.Locale;

public class GeocoderUtilsTest {

	@Test
	public void testGetGeocodedLocationFor() throws Exception {
		long time = System.currentTimeMillis();
		String swissNumberStr = "15573275419";
		PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
		try {
			Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(swissNumberStr, "CN");
			PhoneNumberOfflineGeocoder geocoder = PhoneNumberOfflineGeocoder.getInstance();
			System.out.println(geocoder.getDescriptionForNumber(swissNumberProto, Locale.CHINA));
		} catch (NumberParseException e) {
			System.err.println("NumberParseException was thrown: " + e.toString());
		}
		System.out.println(System.currentTimeMillis() - time);
	}
}