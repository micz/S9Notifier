package it.micz.s9notifier.provider;

import java.util.ArrayList;
import java.util.List;

import it.micz.samsungbadger.Badge;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class SamsungBadgeProvider {
	private static final int MAX_COUNT = 99;
	
    protected static final Uri K9_ACCOUNTS_URI =
            Uri.parse("content://com.fsck.k9.messageprovider/accounts/");

    protected static final Uri K9_ACCOUNT_UNREAD_URI =
            Uri.parse("content://com.fsck.k9.messageprovider/account_unread/");

    protected static final String ACCOUNT_NUMBER = "accountNumber";
    protected static final String ACCOUNT_NAME = "accountName";
    protected static final String ACCOUNT_UUID = "accountUuid";
    protected static final String ACCOUNT_COLOR = "accountColor";
    protected static final String UNREAD = "unread";

    protected final List<Integer> mAccountNumbers = new ArrayList<Integer>();
    protected final List<String> mAccountNames = new ArrayList<String>();
    protected final List<String> mAccountUuids = new ArrayList<String>();
    protected final List<Integer> mAccountColors = new ArrayList<Integer>();
    protected final List<Integer> mUnreads = new ArrayList<Integer>();
    protected int mTotalUnread = 0;

	public Boolean updateBadge(Context mContext) {

		//int unreadCount = 2;
		try {
			Badge badge = Badge.getBadge(mContext);

			// This indicates there is no badge record for your app yet
			if (badge == null) {
				badge = new Badge();
				badge.mPackage = mContext.getPackageName();
				badge.mClass = Badge.getLauncherClassName(mContext);
				badge.mBadgeCount = "0";
				badge.save(mContext);
			}
			
			setK9Data(mContext);

			if (mTotalUnread <= 0) {
				// Hide TextView for unread count if there are no unread
				// messages.
				badge.mBadgeCount = "0";
			} else {
				String displayCount = (mTotalUnread <= MAX_COUNT) ? String
						.valueOf(mTotalUnread) : String.valueOf(MAX_COUNT) + "+";
				badge.mBadgeCount = displayCount;
			}

			badge.update(mContext);
			return true;
		} catch (Exception e) {
			// problems here...
			e.getMessage();
			return false;
		}
	}

    protected void setK9Data(Context mContext)
    {
        final String[] projection =
                new String[] {ACCOUNT_NUMBER, ACCOUNT_NAME, ACCOUNT_UUID, ACCOUNT_COLOR};
        final Cursor accountCursor =
                mContext.getContentResolver().query(K9_ACCOUNTS_URI, projection, null, null, null);

        if (accountCursor.moveToFirst()) {
            do {
                mAccountNumbers.add(
                        accountCursor.getInt(accountCursor.getColumnIndex(ACCOUNT_NUMBER)));
                mAccountNames.add(
                        accountCursor.getString(accountCursor.getColumnIndex(ACCOUNT_NAME)));
                mAccountUuids.add(
                        accountCursor.getString(accountCursor.getColumnIndex(ACCOUNT_UUID)));

                mAccountColors.add(
                        accountCursor.getInt(accountCursor.getColumnIndex(ACCOUNT_COLOR)));

            } while (accountCursor.moveToNext());
        }

        accountCursor.close();

        for (int accountNumber : mAccountNumbers) {
            final Uri uri = ContentUris.withAppendedId(K9_ACCOUNT_UNREAD_URI, accountNumber);
            final Cursor unreadCursor =
                    mContext.getContentResolver().query(uri, null, null, null, null);

            unreadCursor.moveToFirst();

            final int unread = unreadCursor.getInt(unreadCursor.getColumnIndex(UNREAD));
            mUnreads.add(unread);
            mTotalUnread += unread;

            unreadCursor.close();
        }
    }
}
