package de.devland.masterpassword.service;

import android.app.Activity;
import android.app.Service;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.enums.SnackbarType;

import de.devland.esperandro.Esperandro;
import de.devland.masterpassword.App;
import de.devland.masterpassword.R;
import de.devland.masterpassword.prefs.DefaultPrefs;

public class ClearClipboardService extends Service {


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        DefaultPrefs defaultPrefs = Esperandro.getPreferences(DefaultPrefs.class,
                getApplicationContext());
        Handler handler = new Handler();
        final ClipboardManager clipboard = (ClipboardManager) getApplicationContext().getSystemService(
                Context.CLIPBOARD_SERVICE);

        // TODO handler injection
        int clipboardDuration = Integer.parseInt(defaultPrefs.clipboardDuration());
        Activity activity = App.get().getCurrentForegroundActivity();
        if (clipboardDuration > 0) {
            if (activity != null) {
                Snackbar.with(activity)
                        .text(String.format(getApplicationContext().getString(
                                        R.string.copiedToClipboardWithDuration),
                                clipboardDuration))
                        .type(SnackbarType.MULTI_LINE)
                        .show(activity);
            }

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ClipData clip = ClipData.newPlainText("", "");
                    clipboard.setPrimaryClip(clip);
                    stopSelf();
                }
            }, clipboardDuration * 1000);
        } else {
            if (activity != null) {
                Snackbar.with(activity)
                        .text(activity.getString(R.string.copiedToClipboard))
                        .show(activity);
            }
        }
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
