package it.micz.s9notifier;

import it.micz.s9notifier.provider.SamsungBadgeProvider;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class K9Receiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
    	SamsungBadgeProvider mBadge=new SamsungBadgeProvider();
		mBadge.updateBadge(context);
    }
}
