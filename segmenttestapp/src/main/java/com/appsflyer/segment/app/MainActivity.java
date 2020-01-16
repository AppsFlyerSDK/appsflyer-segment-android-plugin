package com.appsflyer.segment.app;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.segment.analytics.Analytics;
import com.segment.analytics.Properties;
import com.segment.analytics.android.integrations.appsflyer.AppsflyerIntegration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AppsFlyer-Segment";
    private KeyValueAdapter adapter;
    private EditText eventNameET;
    private EditText keyET;
    private EditText valueET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initListView();
        findViewById(R.id.button_add).setOnClickListener(this);
        findViewById(R.id.track_button).setOnClickListener(this);
        eventNameET = (EditText) findViewById(R.id.event_name_editText);
        keyET = (EditText) findViewById(R.id.key_text_editText);
        valueET = (EditText) findViewById(R.id.value_text_editText);
        valueET.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    adapter.addItem(keyET.getText().toString(), valueET.getText().toString());
                    keyET.requestFocus();
                }
                return false;
            }
        });

        Log.d(TAG, "AppsFlyer's Segment Integration TestApp is now initializing..");

        initConversionListener();
        Log.d(TAG, "Done!");
    }



    private void initConversionListener() {
        AppsflyerIntegration.cld = new AppsflyerIntegration.ConversionListenerDisplay() {
            @Override
            public void display(Map<String, ?> attributionData) {
                for (String attrName : attributionData.keySet()) {
                    Log.d(TAG, "attribute: " + attrName + " = " +
                            attributionData.get(attrName));
                }

                //SCREEN VALUES//
                //noinspection StringBufferReplaceableByString
                StringBuilder sb = new StringBuilder();
                sb.append("callbackType: ").append(attributionData.get("type")).append("\n");
                sb.append("Install Type: ").append(attributionData.get("af_status")).append("\n");
                sb.append("Media Source: ").append(attributionData.get("media_source")).append("\n");
                sb.append("Click Time(GMT): ").append(attributionData.get("click_time")).append("\n");
                sb.append("Install Time(GMT): ").append(attributionData.get("install_time")).append("\n");
                final String conversionDataString = sb.toString();
                runOnUiThread(new Runnable() {
                    public void run() {
                        TextView conversionTextView = (TextView) findViewById(R.id.conversionDataTextView);
                        if (conversionTextView != null) {
                            conversionTextView.setGravity(Gravity.CENTER_VERTICAL);
                            conversionTextView.setText(conversionDataString);
                        } else {
                            Log.d(TAG,"Could not load conversion data");
                        }
                    }
                });

            }
        };
    }


    private void initListView() {
        adapter = new KeyValueAdapter();
        ((ListView) findViewById(R.id.listView_items)).setAdapter(adapter);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.track_button) {
            String eventName = eventNameET.getText().toString();

            Analytics analytics = Analytics.with(this);

            if (!eventName.equals("")) {
                Properties properties = new Properties();
                properties.putAll(adapter.mData);
                analytics.track(eventName, properties);
            } else {
                analytics.track("Testing attribution!");
            }
        } else if (view.getId() == R.id.button_add) {
            adapter.addItem(keyET.getText().toString(), valueET.getText().toString());
        } else {
            Log.w(TAG,"Unknown button ID. Check you're implementation.");

        }
    }

    public void deleteClickHandler(View v) {
        RelativeLayout vwParentRow = (RelativeLayout) v.getParent();
        LinearLayout keyValContainer = (LinearLayout) vwParentRow.getChildAt(0);
        String key = ((TextView) keyValContainer.getChildAt(0)).getText().toString();
        adapter.mData.remove(key);
        adapter.mKeys.remove(key);
        adapter.notifyDataSetChanged();
    }

    public static class ViewHolder {
        TextView keyTextView;
        TextView valueTextView;
        Button deleteButton;
    }

    private class KeyValueAdapter extends BaseAdapter {

        Map<String, String> mData = new LinkedHashMap<>();
        private List<String> mKeys;
        private LayoutInflater mInflater;

        @SuppressWarnings("unused")
        public KeyValueAdapter(LinkedHashMap<String, String> data) {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mData = data;
            mKeys = new ArrayList<>(mData.keySet());
        }

        KeyValueAdapter() {
            mInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mKeys = new ArrayList<>(mData.keySet());
        }

        void addItem(String key, String value) {
            mData.put(key, value);
            if (!mKeys.contains(key)) {
                mKeys.add(key);
            }
            notifyDataSetChanged();
            keyET.setText("");
            valueET.setText("");
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mKeys.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("getView " + position + " " + convertView);
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.item1, parent, false); //http://stackoverflow.com/questions/14978296/unable-to-start-activityunsupportedoperationexception-addviewview-layoutpara
                holder = new ViewHolder();
                holder.keyTextView = (TextView) convertView.findViewById(R.id.key_text_item);
                holder.valueTextView = (TextView) convertView.findViewById(R.id.value_text_item);
                holder.deleteButton = (Button) convertView.findViewById(R.id.remove_button);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            holder.keyTextView.setText(mKeys.get(position));
            holder.valueTextView.setText(mData.get(mKeys.get(position)));
            return convertView;
        }

    }

}
