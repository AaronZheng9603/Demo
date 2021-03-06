package com.aaron.demo.content_provider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.aaron.demo.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 学习 ContentProvider
 */
public class ProviderActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "ProviderActivity";
    private static final int REQUEST_PERMISSION = 1;
    private BaseAdapter mAdapter;
    private List<String> mStringList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_provider);
        initView();
        checkPermission();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_read_contacts:
                readContacts();
                break;
            case R.id.btn_read_messages:
                readMessages();
                break;
            case R.id.btn_clear_list:
                mStringList.clear();
                mAdapter.notifyDataSetChanged();
                break;
        }
    }

    private void initView() {
        Button readContacts = findViewById(R.id.btn_read_contacts);
        Button readMessages = findViewById(R.id.btn_read_messages);
        Button clearList = findViewById(R.id.btn_clear_list);
        readContacts.setOnClickListener(this);
        readMessages.setOnClickListener(this);
        clearList.setOnClickListener(this);
        ListView listView = findViewById(R.id.list_view);
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, mStringList);
        listView.setAdapter(mAdapter);
    }

    private void checkPermission() {
        // 检查权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            // 申请权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS,
                            Manifest.permission.READ_SMS}, REQUEST_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION:
                if (grantResults.length > 0 && grantResults[0]
                        == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(ProviderActivity.this, "准备就绪",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProviderActivity.this, "请先打开权限",
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void readContacts() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract
                            .CommonDataKinds.Phone.DISPLAY_NAME));
                    String number = cursor.getString(cursor.getColumnIndex(ContactsContract
                            .CommonDataKinds.Phone.NUMBER));
                    mStringList.add(displayName + "\n" + number);
                }
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    private void readMessages() {
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(Uri.parse("content://sms/"),
                    new String[]{"_id", "address", "date", "body", "type"}, null, null, null);
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String address = cursor.getString(1);
                    String body = cursor.getString(3);
                    mStringList.add(address + "\n" + body);
                }
                mAdapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}
