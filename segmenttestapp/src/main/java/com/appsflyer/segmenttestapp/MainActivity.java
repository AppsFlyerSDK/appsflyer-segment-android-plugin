package com.appsflyer.segmenttestapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.segment.analytics.Analytics;
import com.segment.analytics.ValueMap;
import com.segment.analytics.android.integrations.appsflyer.AppsflyerIntegration;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AppsFlyer-Segment";
    private Analytics analytics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "AppsFlyer's Segment Integration TestApp is now initializing..");
        analytics = new Analytics.Builder(this, "LTKg97K4uHOXI1udmMG9eGHsubnCCASQ")
                .use(AppsflyerIntegration.FACTORY).build();
//        Analytics.setSingletonInstance(analytics);

//        ValueMap settings = new ValueMap().putValue("appsFlyerDevKey", "JkmJarFMos7svquk9gxQfC").putValue("trackAttributionData", true);
//        AppsflyerIntegration.FACTORY.create(settings, analytics).onActivityCreated(this, savedInstanceState);

        findViewById(R.id.track_button).setOnClickListener(this);

        Log.d(TAG, "Done!");

    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.track_button) {
            analytics.track("Clicking button attribution!");
        }
    }
}
