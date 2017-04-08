package com.bignerdranch.android.IamWatchingYou;



import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


public class IamWatchingYouActivity extends AppCompatActivity {
    private static final String TAG = "IamWatchingYouActivity";
    private static final int CODE = 1 ;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "going for permissions request");
        setContentView(R.layout.activity_iam_watching_you);
        if(!doWeHaveAccessToUsersLocation()) {
            accessFineLocation();
        }
        mButton = (Button)findViewById(R.id.Button);

        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                floorPlanActivity(v);
            }
        });




    }


    private boolean doWeHaveAccessToUsersLocation() {
        return (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED);
    }

    private void accessFineLocation() {

        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                            @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_DENIED) {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)){
                Toast.makeText(this, R.string.Fine_Location_access, Toast.LENGTH_LONG)
                        .show();
            } else {
                settingsButton();
            }
        }

    }

    private void goToAppSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    private void settingsButton() {
        mButton.setBackgroundColor(Color.RED);
        mButton.setText(R.string.settingsButton);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAppSettings();
            }
        });
    }

    private void floorPlanActivity(View v) {

        Intent intent = new Intent(this, FloorPlanActivity.class);
        startActivity(intent);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if(doWeHaveAccessToUsersLocation()) {
            setButton();
        }

    }

    private void setButton() {
        mButton.setBackgroundColor(Color.BLUE);
        mButton.setText(R.string.FetchFloorPlan);

        mButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {

                floorPlanActivity(v);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }






}
