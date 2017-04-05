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
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.text.TextUtils;
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

public class IamWatchingYouActivity extends FragmentActivity {
    private static final String Fb_URL = "https://iamwatchingyou-aa347.firebaseio.com/";
    private static final String TAG = "IamWatchingYouApp ";
    //Creating a reference to the Firebase database we are going to use with this application.
    FirebaseDatabase db = FirebaseDatabase.getInstance();
    DatabaseReference myRef = db.getReference("User_Records");
    private final int CODE = 12312;
    private Position mUsersPosition;
    private Records mRecord;
    private IATask<IAFloorPlan> mPendingAsyncResult;
    private IALocationManager mLocationManager;
    private IAFloorPlan mFloorPlan;
    private IAResourceManager mFloorPlanManager;
    private BlueDotView mFloorPlanImage;
    private DownloadManager mDownloadManager;
    private long mDownloadId;
    private static final float dotRadius = 1.0f;

    private IALocationListener mLocationListener = new IALocationListener() {
        @Override
        public void onLocationChanged(IALocation location) {

        //    Log.d(TAG, "Latitude " + location.getLatitude());
       //     Log.d(TAG, "Longitude "+ location.getLongitude());


            //We create a position object with the latitude and the longitude of our user
            mUsersPosition = new Position(location.getLatitude(), location.getLongitude());
            //We create an object with the location passed as an object and the time of
            //we recorded this location
            mRecord = new Records(mUsersPosition, location.getTime());
            //we save that object to our database with a unique id
            myRef.push().setValue(mRecord);
            if (mFloorPlanImage != null && mFloorPlanImage.isReady()) {
                IALatLng latLng = new IALatLng(location.getLatitude(), location.getLongitude());
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

    private IARegion.Listener mRegionListener = new IARegion.Listener() {

        @Override
        public void onEnterRegion(IARegion region) {
            if (region.getType() == IARegion.TYPE_FLOOR_PLAN) {
                String id = region.getId();
                Log.d(TAG, "floorPlan changed to " + id);
                Toast.makeText(IamWatchingYouActivity.this, id, Toast.LENGTH_SHORT).show();
                fetchFloorPlan(id);
            }
        }

        @Override
        public void onExitRegion(IARegion region) {
            // leaving a previously entered region
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iam_watching_you);

        //Creating a new instance of location manager class to access indoorAtlas services.
        mLocationManager = IALocationManager.create(this);
        mFloorPlanImage = (BlueDotView) findViewById(R.id.floorPlanView);

        mDownloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);

        mFloorPlanManager = IAResourceManager.create(this);
        /* optional setup of floor plan id
           if setLocation is not called, then location manager tries to find
           location automatically */
       /* final String floorPlanId = getString(R.string.indooratlas_floor_plan_id);
        if (!TextUtils.isEmpty(floorPlanId)) {
            final IALocation location = IALocation.from(IARegion.floorPlan(floorPlanId));
            mLocationManager.setLocation(location);
        }*/
    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, " onResume");
        requestFineLocationPermission();
        Log.d(TAG, " insidePermissions " );
        //location updates every 1sec
        Log.d(TAG, " going for location updates with manager "+mLocationManager.toString() );
        Log.d(TAG, " mLocationListener "+ mLocationListener.toString());
        mLocationManager.requestLocationUpdates(IALocationRequest.create()
                .setFastestInterval(1000), mLocationListener);
        Log.d(TAG, " oeo");
        mLocationManager.registerRegionListener(mRegionListener);
        registerReceiver(onComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));



    }

    @Override
    protected void onPause() {
        super.onPause();

        mLocationManager.removeLocationUpdates(mLocationListener);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mLocationManager.destroy();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        Log.d(TAG, "onSaveInstanceState");
    }


    //We are making a request, if we don't already have, to access users fine location
    private void requestFineLocationPermission() {
        Log.d(TAG, "Requesting Fine Location Permission ");

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION )
                != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "You already have permission what am I doing here? ");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CODE);
        }
    }


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

    private void showFloorPlanImage(String filePath) {
        Log.w(TAG, "showFloorPlanImage: " + filePath);
        mFloorPlanImage.setRadius(mFloorPlan.getMetersToPixels() * dotRadius);
        mFloorPlanImage.setImage(ImageSource.uri(filePath));
    }

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
                        //we get this toast
                        Toast.makeText(IamWatchingYouActivity.this,filePath,Toast.LENGTH_LONG)
                                .show();
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
                            Toast.makeText(IamWatchingYouActivity.this, "kai edw",
                                    Toast.LENGTH_LONG)
                                    .show();
                            showFloorPlanImage(filePath);
                        }
                    } else {
                        // do something with error
                        if (!asyncResult.isCancelled()) {
                            Toast.makeText(IamWatchingYouActivity.this,
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

    private void cancelPendingNetworkCalls() {
        if (mPendingAsyncResult != null && !mPendingAsyncResult.isCancelled()) {
            mPendingAsyncResult.cancel();
        }
    }


    //Callback for the result from requesting permissions. This method is invoked for every call
    //on requestPermissions.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults ) {

        switch (requestCode) {
            case CODE :
                if (grantResults.length == 0 || grantResults[0]
                        == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) {

                    Toast.makeText(this,"please give us access to your location through settings",Toast.LENGTH_LONG)
                            .show();
                    finish();

                } else {
                    mLocationManager.requestLocationUpdates(IALocationRequest.create().
                            setFastestInterval(1000), mLocationListener);
                }
                Log.d(TAG,"grant permission");
                break;
        }

    }

}
