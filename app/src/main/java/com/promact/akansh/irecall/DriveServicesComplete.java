package com.promact.akansh.irecall;

import android.util.Log;

import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.events.CompletionEvent;
import com.google.android.gms.drive.events.DriveEventService;

/**
 * Created by Akansh on 13-07-2017.
 */

public class DriveServicesComplete extends DriveEventService {
    public static final String TAG = "DriveService";

    @Override
    public void onCompletion(CompletionEvent completionEvent) {
        super.onCompletion(completionEvent);

        DriveId driveId = completionEvent.getDriveId();
        String resourceId = driveId.getResourceId();

        Log.d(TAG, "resource id: " + resourceId);
    }
}
