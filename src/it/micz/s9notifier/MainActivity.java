package it.micz.s9notifier;

import it.micz.s9notifier.provider.SamsungBadgeProvider;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {
	
	private SamsungBadgeProvider mBadge;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Context mContext = this.getApplicationContext();
		mBadge=new SamsungBadgeProvider();
		mBadge.updateBadge(mContext);
	}

	@Override
	public void onStart() {
		super.onStart();
		PackageManager pm = getPackageManager();
		Intent appStartIntent = pm.getLaunchIntentForPackage("com.fsck.k9");
		if (null != appStartIntent) {
			appStartIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			startActivity(appStartIntent);
		} else {
			openAlert(getCurrentFocus(), "K-9 mail not found",
					"You need to install K-9 mail to use this app!");
		}
		finish();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		System.runFinalization();
		System.exit(0);
	}

	private void openAlert(View view, String title, String message) {
		AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				MainActivity.this);

		alertDialogBuilder.setTitle(title);
		alertDialogBuilder.setMessage(message);
		// set neutral button: Exit the app message
		alertDialogBuilder.setNeutralButton((CharSequence) "Close",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						// exit the app and go to the HOME
						MainActivity.this.finish();
					}
				});

		AlertDialog alertDialog = alertDialogBuilder.create();
		// show alert
		alertDialog.show();
	}

}
