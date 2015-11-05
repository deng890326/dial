package com.example.wei.dial;

import android.app.Activity;
import android.os.Bundle;

/**
 * Created by wei on 15-11-2.
 */
public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new NumberFragment())
                .commit();
    }
}
