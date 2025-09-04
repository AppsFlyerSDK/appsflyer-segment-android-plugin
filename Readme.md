<img src="https://massets.appsflyer.com/wp-content/uploads/2018/06/20092440/static-ziv_1TP.png"  width="400" > 


# AppsFlyer - Segment Integration
[![CI - Tests](https://github.com/AppsFlyerSDK/appsflyer-segment-android-plugin/actions/workflows/unit-tests-workflow.yml/badge.svg)](https://github.com/AppsFlyerSDK/appsflyer-segment-android-plugin/actions/workflows/unit-tests-workflow.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.appsflyer/segment-android-integration/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.appsflyer/segment-android-integration)

----------
In order for us to provide optimal support, we would kindly ask you to submit any issues to support@appsflyer.com

*When submitting an issue please specify your AppsFlyer sign-up (account) email , your app ID , production steps, logs, code snippets and any additional relevant information.*


# Overview

AppsFlyer SDK provides app installation and event tracking functionality. We have developed an SDK that is highly robust (7+ billion SDK installations to date), secure, lightweight and very simple to embed.



You can track installs, updates and sessions and also track additional in-app events beyond app installs (including in-app purchases, game levels, etc.) to evaluate ROI and user engagement levels.

---

Built with AppsFlyer Android SDK `v6.17.3`

## Table of content

- [Introduction](#whatIsSegment)
- [Getting Started](#quickStart)
- [Manual mode](#manual)
-  [SDK Initialization](#sdk_init)
-  [Register In-App Events](#adding_events)
-  [Get Conversion Data](#conversion_data)
- [Unified Deep Linking](#deep_linking)
- [Send consent for DMA compliance](#dma_support) 
- [Sample App](#sample_app)


### <a id="whatIsSegment">
# Introduction

Segment makes it easy to send your data to AppsFlyer. Once you have tracked your data through Segment's open source libraries, the data is translated and routed to AppsFlyer in the appropriate format. AppsFlyer helps marketers to pinpoint targeting, optimize ad spend and boost ROI.


The AppsFlyer integration code is open-source on GitHub if you want to [check it out](https://github.com/segment-integrations/integration-appsflyer).

Check out the Segment AppsFlyer docs [here](https://segment.com/docs/destinations/appsflyer/).






### <a id="gettingStarted">
# Getting Started


#### DashBoard Setup

To enable AppsFlyer in the Segment dashboard, follow these steps:

1.  Enter your unique AppsFlyer Dev Key, which is accessible from your AppsFlyer account, in Segment’s destination settings.
2.  After you build and release to the app store, your data is translated and sent to AppsFlyer automatically.



The Segment AppsFlyer integration is entirely handled through Segment's servers, so you don’t need to bundle AppsFlyer's iOS or Android SDKs. Your Segment SDK will be enough.

AppsFlyer supports the `identify` and `track` methods.

### <a id="manual">
# Manual mode
Starting version 6.8.0, we support a manual mode to seperate the initialization of the AppsFlyer SDK and the start of the SDK. In this case, the AppsFlyer SDK won't start automatically, giving the developper more freedom when to start the AppsFlyer SDK. Please note that in manual mode, the developper is required to implement the API startAppsFlyer(Context context) in order to start the SDK.
<br>If you are using CMP to collect consent data this feature is needed. See explanation [here](#dma_support).
### Example:

```java
AppsflyerIntegration.setManualMode(true);
```

And to start the AppsFlyer SDK, use `void startAppsFlyer(Context context)` API.

### Example:

```java
    protected void onCreate(Bundle savedInstanceState) {
         AppsflyerIntegration.startAppsFlyer(this);
    }
 ```


### <a id="quickStart">

# Setting up the SDK

#### 2.1) Adding the Plugin to your Project

Add the AppsFlyer Segment Integration dependency to your app `build.gradle` file.
```java
implementation 'com.appsflyer:segment-android-integration:6.17.3'
implementation 'com.android.installreferrer:installreferrer:2.1'
```

#### 2.2)  Setting the Required Permissions

The AndroidManifest.xml should include the following permissions:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
```

In v6.8.0 of the AppsFlyer SDK, we added the normal permission com.google.android.gms.permission.AD_ID to the SDK's AndroidManifest, 
to allow the SDK to collect the Android Advertising ID on apps targeting API 33.
If your app is targeting children, you may need to revoke this permission to comply with Google's Data policy.
You can read more about it [here](https://support.appsflyer.com/hc/en-us/articles/7569900844689).

Starting from **6.14.0** Huawei Referrer integration was updated. [Learn more](https://dev.appsflyer.com/hc/docs/install-android-sdk#huawei-install-referrer).

### <a id="sdk_init"> 2.2)  Init AppsFlyer

```java

static final String SEGMENT_WRITE_KEY = "<YOUR_KEY>";

@Override
protected void onCreate(Bundle savedInstanceState) {
super.onCreate(savedInstanceState);
setContentView(R.layout.activity_main);

Analytics.Builder builder = new Analytics.Builder(this , SEGMENT_WRITE_KEY)
.use(AppsflyerIntegration.FACTORY)

...
(optional)

.logLevel(Analytics.LogLevel.VERBOSE)
.recordScreenViews()
.trackApplicationLifecycleEvents() // Application Opened , Application Updated , Application Installed events
.build();

Analytics.setSingletonInstance(builder.build());

}
```

Adding `.trackApplicationLifecycleEvents()` will send   `Application Opened`  , `Application Updated`  and `Application Installed` events to AppsFlyer.




## <a id="adding_events"> In-app events

When you call `track`, Segment translates it automatically and sends the event to AppsFlyer.

Segment includes all the event properties as callback parameters on the AppsFlyer event, and automatically translates `properties.revenue` to the appropriate AppsFlyer purchase event properties based on Segment's spec’d properties.

Finally, Segment automatically uses AppsFlyer’s transactionId-based de-duplication when sending an an `orderId`.


Purchase Event Example:
```java
Map<String, Object> eventValue = new HashMap<String, Object>();
eventValue.put("productId","com.test.id");
eventValue.put("revenue","1.00");
eventValue.put("currency","USD");

Analytics analytics = Analytics.with(this);
Properties properties = new Properties();
properties.putAll(eventValue);

analytics.track("purchase", properties);
```

Note: AppsFlyer will map `revenue -> af_revenue` and `currency -> af_currency`.

Check out the Segment docs on track [here](https://segment.com/docs/spec/track/).


### <a id="identify">

## Identify


When you `identify` a user, that user’s information is passed to AppsFlyer with `customer user Id` as AppsFlyer’s External User ID. Segment’s special traits recognized as AppsFlyer’s standard user profile fields (in parentheses) are:

`customerUserId` (`Customer User Id`) <br>
`currencyCode` (`Currency Code`)

All other traits will be sent to AppsFlyer as custom attributes.

```java
Analytics analytics = Analytics.with(this);

analytics.identify("a user's id", new Traits()
.putName("a user's name")
.putEmail("maxim@appsflyer.com"),
null);
```

Check out the Segment docs on indentify [here](https://segment.com/docs/spec/identify/).

### <a id="conversion_data">

##  Get Conversion Data

For Conversion data your should call the method below.

```java
         AppsflyerIntegration.conversionListener  = new AppsflyerIntegration.ExternalAppsFlyerConversionListener() {
                    @Override
                    public void onConversionDataSuccess(Map<String, Object> map) {
                        // Process Deferred Deep Linking here
                        for (String attrName : map.keySet()) {
                            Log.d(TAG, "attribute: " + attrName + " = " + map.get(attrName));
                        }
                    }

                    @Override
                    public void onConversionDataFail(String s) {

                    }

                    @Override
                    public void onAppOpenAttribution(Map<String, String> map) {
                     // Process Direct Deep Linking here
                        for (String attrName : map.keySet()) {
                            Log.d(TAG, "attribute: " + attrName + " = " + map.get(attrName));
                        }
                    }

                    @Override
                    public void onAttributionFailure(String s) {

                    }
                };
```

In order for Conversion Data to be sent to Segment, make sure you have enabled "Track Attribution Data" in AppsFlyer destination settings:

<img width="741" alt="Xnip2019-05-11_19-19-31" src="https://user-images.githubusercontent.com/18286267/57572409-8fb19200-7422-11e9-832f-fdd343af3137.png">

### <a id="deep_linking">

##  Unified deep linking
In order to implement unified deep linking, call the method below :

```java
        AppsflyerIntegration.deepLinkListener = new AppsflyerIntegration.ExternalDeepLinkListener() {
            @Override
            public void onDeepLinking(@NonNull DeepLinkResult deepLinkResult) {
                //TODO handle deep link logic
            }
        };
```
For more information about unified deep linking, check [here](https://dev.appsflyer.com/docs/android-unified-deep-linking)

## <a id="dma_support"> Send consent for DMA compliance 
For a general introduction to DMA consent data, see [here](https://dev.appsflyer.com/hc/docs/send-consent-for-dma-compliance).<be> 
The SDK offers two alternative methods for gathering consent data:<br> 
- **Through a Consent Management Platform (CMP)**: If the app uses a CMP that complies with the [Transparency and Consent Framework (TCF) v2.2 protocol](https://iabeurope.eu/tcf-supporting-resources/), the SDK can automatically retrieve the consent details.<br> 
<br>OR<br><br> 
- **Through a dedicated SDK API**: Developers can pass Google's required consent data directly to the SDK using a specific API designed for this purpose. 
### Use CMP to collect consent data 
A CMP compatible with TCF v2.2 collects DMA consent data and stores it in <code>SharedPreferences</code>. To enable the SDK to access this data and include it with every event, follow these steps:<br> 
<ol> 
  <li> Call <code>AppsFlyerLib.getInstance().enableTCFDataCollection(true)</code> to instruct the SDK to collect the TCF data from the device. 
  <li> Set the the adapter to be manual : <code>AppsflyerIntegration.setManualMode(true)</code>. <br> This will allow us to delay the Conversion call in order to provide the SDK with the user consent. 
  <li> Initialize Segment using <code>AppsflyerIntegration.FACTORY</code>. 
  <li> In the <code>Activity</code> class, use the CMP to decide if you need the consent dialog in the current session.
  <li> If needed, show the consent dialog, using the CMP, to capture the user consent decision. Otherwise, go to step 6. 
  <li> Get confirmation from the CMP that the user has made their consent decision, and the data is available in <code>SharedPreferences</code>.
  <li> Call <code>AppsflyerIntegration.startAppsFlyer(this)</code>
</ol> 
 
 #### Application class
``` kotlin
@Override public void onCreate() {
    super.onCreate();
    AppsFlyerLib.getInstance().enableTCFDataCollection(true);
    AppsflyerIntegration.setManualMode(true);
    initSegmentAnalytics();
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
``` 
#### Activity class
```kotlin
public class MainActivity extends AppCompatActivity {

  private boolean consentRequired = true;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_main);
      if (consentRequired)
          initConsentCollection();
      else
          AppsflyerIntegration.startAppsFlyer(this);
  }
  
  private void initConsentCollection() {
    // Implement here the you CMP flow
    // When the flow is completed and consent was collected 
    // call onConsentCollectionFinished()
  }

  private void onConsentCollectionFinished() {
    AppsflyerIntegration.startAppsFlyer(this);
  }
}
```

 
### Manually collect consent data 
If your app does not use a CMP compatible with TCF v2.2, use the SDK API detailed below to provide the consent data directly to the SDK. 
<ol> 
  <li> Initialize <code>AppsFlyerIntegration</code> using manual mode and also <code>Analytics</code>. This will allow us to delay the Conversion call in order to provide the SDK with the user consent. 
  <li> In the <code>Activity</code> class, determine whether the GDPR applies or not to the user.<br> 
  - If GDPR applies to the user, perform the following:  
      <ol> 
        <li> Given that GDPR is applicable to the user, determine whether the consent data is already stored for this session. 
            <ol> 
              <li> If there is no consent data stored, show the consent dialog to capture the user consent decision. 
              <li> If there is consent data stored continue to the next step. 
            </ol> 
        <li> To transfer the consent data to the SDK, create an object called AppsFlyerConsent with the following optional parameters:<br> 
          - <code>isUserSubjectToGDPR</code> - Indicates whether GDPR applies to the user.<br>
          - <code>hasConsentForDataUsage</code> - Indicates whether the user has consented to use their data for advertising purposes.<br>
          - <code>hasConsentForAdsPersonalization</code> - Indicates whether the user has consented to use their data for personalized advertising purposes.<br>
          - <code>hasConsentForAdStorage</code> - Indicates whether the user has consented to store or access information on a device.<br>
        <li> Call <code>AppsFlyerLib.getInstance().setConsentData()</code> with the <code>AppsFlyerConsent</code> object.    
        <li> Call <code>AppsflyerIntegration.startAppsFlyer(this)</code>. 
      </ol><br> 
    - If the GDPR does not apply to the user isUserSubjectToGDPR is false and the rest of the parameters must be null. See example below. 
        <li> Create an <code>AppsFlyerConsent</code> object: <code>AppsFlyerConsent nonGdprUser = new AppsFlyerConsent(false, null, null, null);</code>
        <li> Call <code>AppsFlyerLib.getInstance().setConsentData(nonGdprUser);</code>  
        <li> Call <code>AppsflyerIntegration.startAppsFlyer(this)</code>. 

For more details, [see] (https://dev.appsflyer.com/hc/docs/android-send-consent-for-dma-compliance)
</ol> 

### <a id="sample_app">

##  Sample App
<p>AppsFlyer has created a sample Android application that integrates AppsFlyer via Segment. Check it out at the <a href="https://github.com/AppsFlyerSDK/appsflyer-segment-android-plugin/tree/master/segmenttestapp" target="_blank">Github repo</a>.</p>
