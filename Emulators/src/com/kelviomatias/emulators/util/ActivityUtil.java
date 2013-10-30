package com.kelviomatias.emulators.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;

public class ActivityUtil {

	public static Intent getLaunchIntentForPackage(Context context, String packageName) {
		return context.getPackageManager().getLaunchIntentForPackage(packageName);
	}

	public static boolean appInstalledOrNot(Context context, String packageName) {
		try {
			context.getPackageManager().getPackageInfo(packageName,
					PackageManager.GET_ACTIVITIES);
			return true;
		} catch (PackageManager.NameNotFoundException e) {
			return false;
		}
	}

}
