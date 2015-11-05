package com.example.wei.dial;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button mDialButton;

    private String[] mNumbers = new String[NumberFragment.NUM_COUNT];
    private int mDialingIndex = -1;
    private  MyContentObserver myContentObserver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PreferenceManager.setDefaultValues(getApplicationContext(), R.xml.preference, false);


        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        for (int i = 0; i < NumberFragment.NUM_COUNT; i++) {
            mNumbers[i] = sp.getString(NumberFragment.KEY_NUMBERS[i], "");
        }

        mDialButton = (Button) findViewById(R.id.dial);
        mDialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialingIndex = -1;

                myContentObserver = new MyContentObserver(new Handler());
                getApplicationContext()
                        .getContentResolver()
                        .registerContentObserver(
                                android.provider.CallLog.Calls.CONTENT_URI, true,
                                myContentObserver);
                dialNextNumber();
            }
        });
    }

    /**
     * 拨打下一个号码
     */
    private void dialNextNumber() {
        String number = getNextNumber();
        if (!TextUtils.isEmpty(number)) {
            Intent intent = new Intent(Intent.ACTION_CALL);
            intent.setData(Uri.parse("tel:" + number));
//			callFromApp = true;
            Log.d("dyw", "dialing number: " + number);

            if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                    || checkSelfPermission(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED) {
                this.startActivity(intent);
            }

            Intent service = new Intent(MyService.ACTION_HAND_OFF);
            service.setClass(getApplicationContext(), MyService.class);
            this.startService(service);
        }
    }

    /**
     * 取得下一个号码
     * @return 号码
     */
    private String getNextNumber() {
        for (mDialingIndex += 1; mDialingIndex < NumberFragment.NUM_COUNT; mDialingIndex++) {
            String number = mNumbers[mDialingIndex];
            if (!TextUtils.isEmpty(number)) {
                return number;
            }
        }
        mDialingIndex = -1;
        return null;
    }

    /**
     * 查看拨号是否接通
     * @return true如果接通
     */
    private boolean checkCallOK() {

        String[] projection = {
                android.provider.CallLog.Calls.NUMBER,
                android.provider.CallLog.Calls.DURATION
        };
        String selection = android.provider.CallLog.Calls.TYPE + "=" + android.provider.CallLog.Calls.OUTGOING_TYPE;
        String sortOrder = android.provider.CallLog.Calls.DATE + " DESC ";
        boolean shouldCall = android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.M
                || checkSelfPermission(Manifest.permission.READ_CALL_LOG) == PackageManager.PERMISSION_GRANTED;

        Cursor cursor = null;
        if (shouldCall) {
            cursor = getContentResolver().query(android.provider.CallLog.Calls.CONTENT_URI,
                    projection, selection, null, sortOrder);
        }

        if (cursor != null && cursor.moveToFirst()) {
            int numberIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.NUMBER);
            int durationIndex = cursor.getColumnIndex(android.provider.CallLog.Calls.DURATION);
            String number = cursor.getString(numberIndex);
            long duration = cursor.getLong(durationIndex);
            cursor.close();
            if (number.equals(mNumbers[mDialingIndex]) && duration > 0) {
                return true;
            }
        }
        return false;
    }

    class MyContentObserver extends ContentObserver {
        public MyContentObserver(Handler h) {
            super(h);
        }

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            if (checkCallOK() || mDialingIndex == NumberFragment.NUM_COUNT - 1) {
                getApplicationContext()
                        .getContentResolver()
                        .unregisterContentObserver(myContentObserver);
            } else {
                dialNextNumber();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        for (int i = 0; i < NumberFragment.NUM_COUNT; i++) {
            mNumbers[i] = sp.getString(NumberFragment.KEY_NUMBERS[i], "");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                View dialogView = getLayoutInflater().inflate(R.layout.pw_dialog, null);
                final EditText passwordEdit = (EditText) dialogView.findViewById(R.id.password_text);
                new AlertDialog.Builder(this)
                        .setTitle(R.string.password)
                        .setView(dialogView)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (passwordEdit.getText().toString().equals("666666")) {
                                    startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                                } else {
                                    Toast.makeText(MainActivity.this, R.string.password_wrong, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).show();
                passwordEdit.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        InputMethodManager keyboard = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        keyboard.showSoftInput(passwordEdit, 0);
                    }
                }, 50);
                break;
            default:
                break;

        }
        return super.onOptionsItemSelected(item);
    }
}
