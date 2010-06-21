package com.nanosheep.bikeroute;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;

import com.google.android.maps.GeoPoint;

/**
 * Display an alert on return to the location the bike was parked at,
 * 
 * @author jono@nanosheep.net
 * 
 */

public final class BikeAlert extends BroadcastReceiver {
	/** Name for bike alert intents. **/
	private static final String INTENT_ID = "com.nanosheep.bikeroute.BIKE_ALERT";
	/** Intent. **/
	private final Intent intent;
	/** Pending Intent. **/
	private final PendingIntent pi;
	/** Owning activity. **/
	Activity act;

	public BikeAlert(final Activity activity) {
		super();
		act = activity;
		intent = new Intent(INTENT_ID);
		pi = PendingIntent.getBroadcast(activity, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
	}

	/**
	 * Display a dialog offering to unpark the bike.
	 */

	@Override
	public void onReceive(final Context context, final Intent intent) {
		final boolean enter = intent.getBooleanExtra(
				LocationManager.KEY_PROXIMITY_ENTERING, false);
		if (enter) {
			act.showDialog(BikeNav.UNPARK_DIALOG);
		}
	}

	/**
	 * Set a proximity alert at the given point for tracking bike position.
	 * 
	 * @param bikeLoc
	 *            point to alert at.
	 */

	public void setBikeAlert(final GeoPoint bikeLoc) {
		final LocationManager lm = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
		lm.addProximityAlert(Degrees.asDegrees(bikeLoc.getLatitudeE6()),
				Degrees.asDegrees(bikeLoc.getLongitudeE6()), 5f, -1, pi);
		final IntentFilter filter = new IntentFilter(INTENT_ID);
		act.registerReceiver(this, filter);
	}

	/**
	 * Remove the alert.
	 */

	public void unsetAlert() {
		final LocationManager lm = (LocationManager) act.getSystemService(Context.LOCATION_SERVICE);
		lm.removeProximityAlert(pi);
	}
}