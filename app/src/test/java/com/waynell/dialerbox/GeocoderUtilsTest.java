package com.waynell.dialerbox;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class GeocoderUtilsTest {

	@Test
	public void testGetGeocodedLocationFor() throws Exception {
		final List<String> numberList = Arrays.asList("10086",
				"15573275419", "057157892157");
		try {
			Locale locale = Locale.CHINA;
			final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

			for (String s : numberList) {
				Phonenumber.PhoneNumber phoneNumber = GeocoderUtils.parsePhoneNumber(s, locale.getCountry());
				System.out.println("geocoder: " + GeocoderUtils.getDescriptionForNumber(phoneNumber, locale));
			}
		} catch (NumberParseException e) {
			System.err.println("NumberParseException was thrown: " + e.toString());
		}
	}
}