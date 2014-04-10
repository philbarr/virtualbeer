package com.simplyapped.libgdx.ext.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;
import com.badlogic.gdx.backends.android.AndroidApplication;

public class AndroidOSDialog implements OSDialog {
	
    Handler uiThread;
    Activity appContext;
    View testView;
 
    public AndroidOSDialog(Activity appContext) {
        uiThread = new Handler();
        this.appContext = appContext;
    }
    
    @Override
    public void showShortToast(final CharSequence toastMessage) {
            uiThread.post(new Runnable() {
                    public void run() {
                            Toast toast = Toast.makeText(appContext, toastMessage, Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0,0);
                            toast.show();
                    }
            });
    }
 
   @Override
    public void showLongToast(final CharSequence toastMessage) {
            uiThread.post(new Runnable() {
                    public void run() {
                            Toast.makeText(appContext, toastMessage, Toast.LENGTH_LONG)
                                            .show();
                    }
            });
    }
 
   @Override
    public void showAlertBox(final String alertBoxTitle,
                    final String alertBoxMessage, final String alertBoxButtonText) {
            uiThread.post(new Runnable() {
                    public void run() {
                            new AlertDialog.Builder(appContext)
                                            .setTitle(alertBoxTitle)
                                            .setMessage(alertBoxMessage)
                                            .setNeutralButton(alertBoxButtonText,
                                                            new DialogInterface.OnClickListener() {
                                                                    public void onClick(DialogInterface dialog,
                                                                                    int whichButton) {
                                                                    }
                                                            }).create().show();
                    }
            });
    }
}
