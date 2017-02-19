package com.appsflyer.segmenttestapp;

import android.app.Application;
import android.util.Log;

import com.segment.analytics.Analytics;
import com.segment.analytics.android.integrations.appsflyer.AppsflyerIntegration;


public class SampleApplication extends Application {


    static final String SEGMENT_WRITE_KEY = "Enter-Your-Segment-Write-Key-Here";

    @Override public void onCreate() {
        super.onCreate();

        initSegmentAnalytics();

        Analytics analytics = Analytics.with(this);

        analytics.onIntegrationReady("Segment.io", new Analytics.Callback() {
            @Override public void onReady(Object instance) {
                Log.d("Segment AppsFlyer Sample", "Segment integration ready.");
            }
        });
    }

    private void initSegmentAnalytics() {
        Analytics.Builder builder = new Analytics.Builder(this, SEGMENT_WRITE_KEY)
                .use(AppsflyerIntegration.FACTORY)
                .logLevel(Analytics.LogLevel.VERBOSE)
                .trackApplicationLifecycleEvents() // Enable this to record certain application events automatically!
                .recordScreenViews(); // Enable this to record screen views automatically!


        // Set the initialized instance as a globally accessible instance.
        Analytics.setSingletonInstance(builder.build());
    }
}
