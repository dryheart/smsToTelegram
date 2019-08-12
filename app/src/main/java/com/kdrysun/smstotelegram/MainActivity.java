package com.kdrysun.smstotelegram;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.kdrysun.smstotelegram.database.SmsDatabase;
import com.kdrysun.smstotelegram.fragment.CardFragment;
import com.kdrysun.smstotelegram.fragment.SettlementFragment;
import com.kdrysun.smstotelegram.fragment.SmsFragment;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private SmsFragment smsFragment = new SmsFragment();
    private SettlementFragment settlementFragment = new SettlementFragment();
    private CardFragment cardFragment = new CardFragment();

    public static SmsDatabase smsDatabase;

    private String TAG = "SmsToTelegram_MainActivity";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_sms:
                    transaction.replace(R.id.smsFrame, smsFragment).commitAllowingStateLoss();
                    break;
                case R.id.navigation_settlement:
                    transaction.replace(R.id.smsFrame, settlementFragment).commitAllowingStateLoss();
                    break;
                case R.id.navigation_card:
                    transaction.replace(R.id.smsFrame, cardFragment).commitAllowingStateLoss();
                    break;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.smsFrame, smsFragment).commitAllowingStateLoss();

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        this.smsPermissionCheck();
    }

    private void smsPermissionCheck() {
        int permissonCheck= ContextCompat.checkSelfPermission(this, Manifest.permission.RECEIVE_SMS);

        if (permissonCheck != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{ Manifest.permission.RECEIVE_SMS}, 1);
    }
}
