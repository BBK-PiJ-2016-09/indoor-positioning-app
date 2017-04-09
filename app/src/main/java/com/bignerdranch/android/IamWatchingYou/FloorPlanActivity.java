package com.bignerdranch.android.IamWatchingYou;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.indooratlas.android.sdk.IALocation;
import com.indooratlas.android.sdk.IALocationListener;
import com.indooratlas.android.sdk.IALocationManager;
import com.indooratlas.android.sdk.IALocationRequest;
import com.indooratlas.android.sdk.IARegion;
import com.indooratlas.android.sdk.resources.IAFloorPlan;
import com.indooratlas.android.sdk.resources.IALatLng;
import com.indooratlas.android.sdk.resources.IAResourceManager;
import com.indooratlas.android.sdk.resources.IAResult;
import com.indooratlas.android.sdk.resources.IAResultCallback;
import com.indooratlas.android.sdk.resources.IATask;

import java.io.File;
import java.util.List;

public class FloorPlanActivity extends AppCompatActivity {

    private static final float dotRadius = 1.0f;
    private IATask<IAFloorPlan> mPendingAsyncResult;
    private IAFloorPlan mFloorPlan;
    private IAResourceManager mFloorPlanManager;
    private DownloadManager mDownloadManager;
    private static final String TAG = "IamWatchingYouApp ";
    //Creating a reference to the Firebase database we are going to use with this application.

    private final int CODE = 12312;
    private Records mRecord;
    private PositioningMethods pointsOfInterest = PositioningMethods.getInstance();

    private IALocationManager mLocationManager;

    private BlueDotView mFloorPlanImage;

    private long mDownloadId;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floor_plan);
        if(savedInstanceState !=null) {

        }
        //Creating a new instance of location manager class to access indoorAtlas services.
        mLocationManager = IALocationManager.create(this);
        mFloorPlanImage = (BlueDotView)findViewById(R.id.floorPlanView);
        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        mFloorPlanManager = IAResourceManager.create(this);

        if(!doWeHavePermission()) {
            requestWritePermission();
        }
        locationUpdates();
        /* optional setup of floor plan id
           if setLocation is not called, then location manager tries to find
           location automatically */
       final String floorPlanId = getString(R.string.indooratlas_floor_plan_id);
       /*  if (!TextUtils.isEmpty(floorPlanId)) {
            final IALocation location = IALocation.from(IARegion.floorPlan(floorPlanId));
            mLocationManager.setLocation(location);
        }*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " onResume");
        locationUpdates();
        mLocationManager.registerRegionListener(mRegionListener);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLocationManager.removeLocationUpdates(mLocationListener);
        mLocationManager.unregisterRegionListener(mRegionListener);
        unregisterReceiver(onComplete);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.destroy();
    }

    private boolean doWeHavePermission() {
        return (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED);
    }



    //We are making a request, if we don't already have, to access users fine location
    private void requestWritePermission() {
        Log.d(TAG, "Requesting Fine Location Permission ");
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,}, CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults ) {

        if(grantResults.length == 0 || grantResults[0] == PackageManager.PERMISSION_DENIED) {

            Toast.makeText(this, R.string.write_access_denied, Toast.LENGTH_LONG)
                    .show();
            finish();

        }


    }

    private void locationUpdates() {
        mLocationManager.requestLocationUpdates(IALocationRequest.create()
                .setFastestInterval(1000), mLocationListener);
    }


    private IALocationListener mLocationListener = new IALocationListener() {
        @Override
        public void onLocationChanged(IALocation location) {

            Log.d(TAG, "Latitude " + location.getLatitude());
            Log.d(TAG, "Longitude "+ location.getLongitude());

            // We delegate to Records what to do with a record object.
            mRecord = new Records(location);

            List <Position> nearbyPointsOfInterest =
                    pointsOfInterest.isNearPointsOfInterest(location.getLatitude(),
                            location.getLongitude(), 6);

            if (nearbyPointsOfInterest.size() == 0) {
                //We don't want to spam the user with toasts, so we wait until
                // he is far from enough (more than the error) of a POI to
                // create a toast again
                pointsOfInterest.setToastFlag(true);
            } else {
                nearbyPointsOfInterest =
                        pointsOfInterest.isNearPointsOfInterest(
                                location.getLatitude(),
                                location.getLongitude(),
                                3);

                if (nearbyPointsOfInterest.size() > 0 &&
                        pointsOfInterest.getToastFlag()) {
                    for (Position position : nearbyPointsOfInterest) {
                        Toast.makeText(
                                FloorPlanActivity.this,
                                position.getDescription(),
                                Toast.LENGTH_LONG)
                                .show();
                    }
                    pointsOfInterest.setToastFlag(false);
                }
            }

            if (mFloorPlanImage != null && mFloorPlanImage.isReady()) {
                IALatLng latLng = new IALatLng(
                        location.getLatitude(),
                        location.getLongitude());

                PointF point = mFloorPlan.coordinateToPoint(latLng);
                mFloorPlanImage.setDotCenter(point);
                mFloorPlanImage.postInvalidate();
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {
            //N/A
        }
    };


    /*  Broadcast receiver for floor plan image download */
    private BroadcastReceiver onComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0L);
            if (id != mDownloadId) {
                Log.w(TAG, "Ignore unrelated download");
                return;
            }
            Log.w(TAG, "Image download completed");
            Bundle extras = intent.getExtras();
            DownloadManager.Query q = new DownloadManager.Query();
            q.setFilterById(extras.getLong(DownloadManager.EXTRA_DOWNLOAD_ID));
            Cursor c = mDownloadManager.query(q);

            if (c.moveToFirst()) {
                int status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
                if (status == DownloadManager.STATUS_SUCCESSFUL) {
                    // process download
                    Log.d(TAG, "download?");
                    String filePath = c.getString(c.getColumnIndex(
                            DownloadManager.COLUMN_LOCAL_URI));
                    showFloorPlanImage(filePath);
                }
            }
            c.close();
        }
    };

    /**
     * Fetches floor plan data from IndoorAtlas server. Some room for cleaning up!!
     */
    private void fetchFloorPlan(String id) {
        cancelPendingNetworkCalls();
        final IATask<IAFloorPlan> asyncResult = mFloorPlanManager.fetchFloorPlanWithId(id);
        mPendingAsyncResult = asyncResult;
        if (mPendingAsyncResult != null) {
            mPendingAsyncResult.setCallback(new IAResultCallback<IAFloorPlan>() {
                @Override
                public void onResult(IAResult<IAFloorPlan> result) {
                    Log.d(TAG, "fetch floor plan result:" + result);
                    if (result.isSuccess() && result.getResult() != null) {
                        mFloorPlan = result.getResult();
                        String fileName = mFloorPlan.getId() + ".img";

                        String filePath = Environment.getExternalStorageDirectory() + "/"
                                + Environment.DIRECTORY_DOWNLOADS + "/" + fileName;
                        File file = new File(filePath);
                        if (!file.exists()) {
                            DownloadManager.Request request =
                                    new DownloadManager.Request(Uri.parse(mFloorPlan.getUrl()));
                            request.setDescription("IndoorAtlas floor plan");
                            request.setTitle("Floor plan");
                            request.allowScanningByMediaScanner();
                            request.setNotificationVisibility(DownloadManager
                                    .Request.VISIBILITY_HIDDEN);
                            request.setDestinationInExternalPublicDir(Environment
                                    .DIRECTORY_DOWNLOADS, fileName);

                            mDownloadId = mDownloadManager.enqueue(request);
                        } else {

                            showFloorPlanImage(filePath);
                        }
                    } else {
                        // do something with error
                        if (!asyncResult.isCancelled()) {
                            Toast.makeText(FloorPlanActivity.this,
                                    (result.getError() != null
                                            ? "error loading floor plan: " + result.getError()
                                            : "access to floor plan denied"), Toast.LENGTH_LONG)
                                    .show();
                        }
                    }
                }
            }, Looper.getMainLooper()); // deliver callbacks in main thread
        }
    }

    private void showFloorPlanImage(String filePath) {
        Log.w(TAG, "showFloorPlanImage: " + filePath);
        mFloorPlanImage.setRadius(mFloorPlan.getMetersToPixels() * dotRadius);
        mFloorPlanImage.setImage(ImageSource.uri(filePath));
    }

    private void cancelPendingNetworkCalls() {
        if (mPendingAsyncResult != null && !mPendingAsyncResult.isCancelled()) {
            mPendingAsyncResult.cancel();
        }
    }
    private IARegion.Listener mRegionListener = new IARegion.Listener() {

        @Override
        public void onEnterRegion(IARegion region) {
            if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                String id = region.getId();
                Log.d(TAG, "floorPlan changed to " + id);
                fetchFloorPlan(id);
            }
        }

        @Override
        public void onExitRegion(IARegion region) {
            // leaving a previously entered region
        }

    };
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }
}
