package com.waynell.dialerbox;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * @author Wayne
 */
public class XposedMod implements IXposedHookZygoteInit, IXposedHookLoadPackage {

	private static Context mContext;

	// Dialer App
	private static final String CLASS_PHONE_DIALER = "com.android.dialer.DialerApplication";

	// CallLog
	private static final String CLASS_PHONE_CALL_DETAILS = "com.android.dialer.PhoneCallDetails";

	private static final String CLASS_PHONE_CALL_DETAILS_HELPER = "com.android.dialer.PhoneCallDetailsHelper";

	// InCall
	private static final String CLASS_CALL_CARD_FRAGMENT = "com.android.incallui.CallCardFragment";

	private static final String CLASS_CALL_CALLER_INFO = "com.android.incallui.CallerInfo";

	private static final String CLASS_CALL_CALL = "com.android.incallui.Call";

	private static final String CLASS_CALL_CONTACT_CACHE_ENTRY = "com.android.incallui.ContactInfoCache.ContactCacheEntry";

	private static final String CLASS_CALL_STATUS_BAR_NOTIFIER = "com.android.incallui.StatusBarNotifier";

	// android.telecom.Call.Details.CAPABILITY_GENERIC_CONFERENCE
	private static final int CAPABILITY_GENERIC_CONFERENCE = 0x00004000;

	@Override
	public void initZygote(StartupParam startupParam) throws Throwable {
	}

	@Override
	public void handleLoadPackage(XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
		if (loadPackageParam.packageName.equals("com.google.android.dialer")) {
			getDialerContext(loadPackageParam);
			addCallLogGeocode(loadPackageParam);
			addInCallGeocode(loadPackageParam);
		}
	}

	private void getDialerContext(XC_LoadPackage.LoadPackageParam param) {
		XposedHelpers.findAndHookMethod(CLASS_PHONE_DIALER, param.classLoader, "onCreate", new XC_MethodHook() {
			@Override
			protected void afterHookedMethod(MethodHookParam param) throws Throwable {
				long time = computeTime(0);
				mContext = (Context) XposedHelpers.callMethod(param.thisObject, "getApplicationContext");
				log("DialerApplication.getDialerContext: " + mContext + "-" + computeTime(time));
			}
		});
	}

	private void addCallLogGeocode(XC_LoadPackage.LoadPackageParam param) {
		try {
			final ClassLoader loader = param.classLoader;
			final Class<?> classPhoneCallDetails = XposedHelpers.findClass(CLASS_PHONE_CALL_DETAILS, loader);
			final Class<?> classPhoneCallDetailsHelper = XposedHelpers.findClass(CLASS_PHONE_CALL_DETAILS_HELPER, loader);

			if (classPhoneCallDetails == null || classPhoneCallDetailsHelper == null) {
				return;
			}

			//com.android.dialer.PhoneCallDetailsHelper#getCallTypeOrLocation(PhoneCallDetails details)
			XposedHelpers.findAndHookMethod(classPhoneCallDetailsHelper, "getCallTypeOrLocation", classPhoneCallDetails, new XC_MethodHook() {
				@Override
				protected void afterHookedMethod(MethodHookParam param) throws Throwable {
					String name = (String) XposedHelpers.getObjectField(param.args[0], "name");
					String number = (String) XposedHelpers.getObjectField(param.args[0], "number");

					if (!TextUtils.isEmpty(number)) {
						String geocode = GeocoderUtils.getGeocodedLocationFor(mContext, loader, number);
						if (!TextUtils.isEmpty(geocode)) {
							if (TextUtils.isEmpty(name)) {
								param.setResult(geocode);
							} else {
								param.setResult(param.getResult() + " " + geocode);
							}
						}
					}
				}
			});
		} catch (Throwable throwable) {
			log(throwable);
		}
	}

	private void addInCallGeocode(XC_LoadPackage.LoadPackageParam param) {
		try {
			final ClassLoader loader = param.classLoader;
			final Class<?> classCallCardFragment = XposedHelpers.findClass(CLASS_CALL_CARD_FRAGMENT, loader);

			if (classCallCardFragment == null) {
				return;
			}

			// com.android.incallui.CardFragment#setPrimary(String number, String name,
			// boolean nameIsNumber, String label, Drawable photo, boolean isSipCall)
			XposedHelpers.findAndHookMethod(classCallCardFragment, "setPrimary", String.class, String.class, boolean.class,
					String.class, Drawable.class, boolean.class, new XC_MethodHook() {
						@Override
						protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
							long time = computeTime(0);
							// String number, String name, boolean nameIsNumber, String label
							// Contact people --- xxxxx, xx, false, mobile etc.
							// Not contact people --- null, xxxxx, true, null.

							String label = param.args[3] == null ? "" : (String) param.args[3];
							if (!(boolean) param.args[2] && !TextUtils.isEmpty((String) param.args[0])) {
								param.args[3] = label + " " + GeocoderUtils.getGeocodedLocationFor(mContext, loader, (String) param.args[0]);
							} else if ((boolean) param.args[2] && !TextUtils.isEmpty((String) param.args[1])) {
								param.args[3] = GeocoderUtils.getGeocodedLocationFor(mContext, loader, (String) param.args[1]);
								if (param.args[0] != null) {
									param.args[0] = null;
								}
							}
							log("CallCardFragment.setPrimary: " + param.args[3] + "-" + computeTime(time));
						}
					});

			//com.android.incallui.StatusBarNotifier#getContentTitle(ContactCacheEntry contactInfo, Call call)
			Class<?> classContactCacheEntry = XposedHelpers.findClass(CLASS_CALL_CONTACT_CACHE_ENTRY, param.classLoader);
			Class<?> classCall = XposedHelpers.findClass(CLASS_CALL_CALL, param.classLoader);
			XposedHelpers.findAndHookMethod(CLASS_CALL_STATUS_BAR_NOTIFIER, param.classLoader, "getContentTitle", classContactCacheEntry, classCall,
					new XC_MethodHook() {
						@Override
						protected void afterHookedMethod(MethodHookParam param) throws Throwable {
							long time = computeTime(0);
							boolean isConferenceCall = (boolean) XposedHelpers.callMethod(param.args[1], "isConferenceCall");
							boolean isCan = (boolean) XposedHelpers
									.callMethod(param.args[1], "can", new Class[]{int.class}, CAPABILITY_GENERIC_CONFERENCE);
							if (!isConferenceCall || isCan) {
								String number = (String) XposedHelpers.getObjectField(param.args[0], "number");
								if (!TextUtils.isEmpty(number)) {
									String geocode = GeocoderUtils.getGeocodedLocationFor(mContext, loader, number);
									if (!TextUtils.isEmpty(geocode)) {
										param.setResult(param.getResult() + " " + geocode);
									}
								}
							}
							log("StatusBarNotifier.getContentTitle: " + param.getResult().toString() + "-" + computeTime(time));
						}
					});

		} catch (Throwable throwable) {
			log(throwable);
		}
	}

	private void log(Throwable throwable) {
		if (BuildConfig.DEBUG) {
			XposedBridge.log(throwable);
		}
	}

	private void log(String log) {
		if (BuildConfig.DEBUG) {
			XposedBridge.log(log);
		}
	}

	private long computeTime(long time) {
		return System.currentTimeMillis() - time;
	}
}
