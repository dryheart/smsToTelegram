package com.kdrysun.smstotelegram;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.collect.Lists;
import com.kdrysun.smstotelegram.database.SmsDatabase;
import com.kdrysun.smstotelegram.domain.Sms;
import com.kdrysun.smstotelegram.fragment.CardFragment;
import com.kdrysun.smstotelegram.fragment.SettingFragment;
import com.kdrysun.smstotelegram.fragment.SettlementFragment;
import com.kdrysun.smstotelegram.fragment.SmsFragment;
import com.kdrysun.smstotelegram.parser.SmsParser;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private SmsFragment smsFragment = new SmsFragment();
    private SettlementFragment settlementFragment = new SettlementFragment();
    private CardFragment cardFragment = new CardFragment();
    private SettingFragment settingFragment = new SettingFragment();

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
                    return true;
                case R.id.navigation_settlement:
                    transaction.replace(R.id.smsFrame, settlementFragment).commitAllowingStateLoss();
                    return true;
                case R.id.navigation_card:
                    transaction.replace(R.id.smsFrame, cardFragment).commitAllowingStateLoss();
                    return true;
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

//        this.testSms();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.appbar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.setting:
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.smsFrame, settingFragment)
                        .commit();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void testSms() {
        new Thread(() -> {
            List<Sms> smsList = Lists.newArrayList(

                new Sms("15888100", "[Web발신]\n롯데8*8* 승인\n김*선\n13,900원 일시불\n02/17 20:28\n(주)티몬\n누적13,900원", "20200217213000")

                /*
                // KB
                new Sms("15881688", "[Web발신]\nKB국민카드4*4*승인\n김*선\n5,000원 일시불\n08/13 10:03\n(주)티몬\n누적1,823,148원", "20190813133000"),
                new Sms("15881688", "[Web발신]\nKB국민카드4*4*승인\n김*선\n20,020원 08/12\nSK텔레콤-자동납부", "20190813133000"),
                new Sms("18990800", "[Web발신]\n[KB국민카드]김두선님 08/14 결제금액 1,510,490원. 잔여포인트리28,906(08/01기준)", "20190813133000"),

                // 신한
                new Sms("15447200", "[Web발신]\n신한카드(141) 승인 김*선 4,300원(일시불)08/10 20:52 GS25 강서안  누적805,315원", "20190813133000"),
                new Sms("15447200", "[Web발신]\n신한카드(141) 승인 김*선님 건강보험(지역)  191,308원 정상승인 되었습니다.", "20190813133000"),
                new Sms("15447200", "[Web발신] \n신한카드(141) 승인 김*선님 주택임대료  194,500원 정상승인 되었습니다.", "20190813133000"),
                new Sms("15447000", "[Web발신]\n[신한카드]        김*선님 08/15결제금액(08/16기준)    723,065원(결제:국민은행)", "20190813133000"),

                // KDB
                new Sms("15881500", "[Web발신]\n(KDB)020****3636709\n출금\n100,000원\n윤혜령\n잔액30,075,963원 07:32:29", "20190813133000"),
                new Sms("15881500", "[Web발신]\n(KDB)020****3636709\n입금\n4,835,000원\n(주)이노라인\n잔액33,347,963원 16:07:35", "20190813133000"),

                new Sms("15884477", "[Web발신]\n[전북]####946\n출금25,800원\n잔액1,115,316원\n12/24 13:39\n모> 신한신헌섭", "20190813133000"),
                new Sms("15884477", "[Web발신]\n[전북]####946\n입금200,000원\n잔액1,147,346원\n01/02 19:37\n산업김두선", "20190813133000")
                */
            );

            new SmsParser().parse(getApplicationContext(), smsList);
        }).start();
    }

    private void smsPermissionCheck() {
        String[] checkPermission = new String[]{
                Manifest.permission.RECEIVE_SMS,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };

        List<String> permission = new ArrayList<>();
        for (String p : checkPermission) {
            if (ContextCompat.checkSelfPermission(this, p) != PackageManager.PERMISSION_GRANTED)
                permission.add(p);
        }

        if (permission.size() > 0)
            ActivityCompat.requestPermissions(this, permission.toArray(new String[permission.size()]), 1);
    }
}
