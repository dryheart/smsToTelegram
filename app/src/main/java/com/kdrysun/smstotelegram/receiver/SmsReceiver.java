package com.kdrysun.smstotelegram.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;
import android.util.Log;
import com.kdrysun.smstotelegram.domain.Sms;
import com.kdrysun.smstotelegram.parser.SmsParser;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.util.*;

public class SmsReceiver extends BroadcastReceiver {

    private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";

    private final String TAG = "SMS_Receiver";

    public static Timer timer;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(SMS_RECEIVED)) {
            Log.d(TAG, "onReceiver() 호출");

            if (timer != null) {
                timer.cancel();
                timer = null;
            }
            timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    SmsMessage[] messagesFromIntent = Telephony.Sms.Intents.getMessagesFromIntent(intent);

                    String phoneNumber = "";
                    String msg = "";
                    List<Sms> smsList = new ArrayList<>();

                    for (SmsMessage message : messagesFromIntent) {
                        if (StringUtils.isNotBlank(phoneNumber) &&
                                !StringUtils.equals(phoneNumber, message.getOriginatingAddress())) {

                            smsList.add(new Sms(phoneNumber, msg,
                                    DateFormatUtils.format(new Date(), "yyyyMMddhhmmss")));
                            msg = "";
                        }

                        phoneNumber = message.getOriginatingAddress();
                        msg += message.getMessageBody();
                    }

                    smsList.add(new Sms(phoneNumber, StringUtils.trimToEmpty(msg),
                            DateFormatUtils.format(new Date(), "yyyyMMddhhmmss")));

                    new SmsParser().parse(context, smsList);
                    //new Thread(() -> ).start();
                }
            }, 3000L);
        }
    }
}