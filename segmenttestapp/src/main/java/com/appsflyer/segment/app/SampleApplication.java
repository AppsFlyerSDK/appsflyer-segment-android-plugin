package com.appsflyer.segment.app;

import android.app.Application;
import android.util.Log;

import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import com.segment.analytics.android.integrations.appsflyer.AppsflyerIntegration;


import java.util.Map;

//https://segment.com/docs/spec/identify/
//https://segment.com/docs/sources/mobile/android/
public class SampleApplication extends Application {


    static final String SEGMENT_WRITE_KEY = "p3uCyX72FjaikfQVyxvUGSzBpRst2flg";
    static final String TAG = "SEG_AF";

    @Override public void onCreate() {
        super.onCreate();

        initSegmentAnalytics();

        Analytics analytics = Analytics.with(this);

        analytics.onIntegrationReady("Segment.io", new Analytics.Callback() {
            @Override public void onReady(Object instance) {
                Log.d(TAG, "Segment integration ready.");
            }
        });

        analytics.identify("a user's id", new Traits()
                        .putName("a user's name")
                        .putEmail("maxim@appsflyer.com"),
                null);


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
