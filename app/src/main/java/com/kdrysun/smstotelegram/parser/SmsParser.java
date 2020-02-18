package com.kdrysun.smstotelegram.parser;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import androidx.preference.PreferenceManager;
import com.kdrysun.smstotelegram.database.AccumulatePrice;
import com.kdrysun.smstotelegram.database.SmsDatabase;
import com.kdrysun.smstotelegram.domain.PaymentType;
import com.kdrysun.smstotelegram.domain.Settlement;
import com.kdrysun.smstotelegram.domain.Sms;
import com.kdrysun.smstotelegram.fragment.SettingFragment;
import com.kdrysun.smstotelegram.log.FileLogger;
import com.kdrysun.smstotelegram.receiver.Telegram;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SmsParser {
    Logger fileLogger = FileLogger.getLogger(SmsParser.class);

    public void parse(Context context, List<Sms> smsList) {
        SmsDatabase db = SmsDatabase.getInstance(context);

        try {
            for (Sms sms : smsList) {
                fileLogger.log(Level.INFO, sms.toString());

                PaymentType paymentType = db.cardDao().findCardType(sms.getNumber());
                if (paymentType != null) {
                    Log.d("SmsParser", "detect phone number" + sms.getNumber());

                    TextParser textParser = TextParserBuilder.build(paymentType);
                    TextDto dto = textParser.parse(sms.getMessage());

                    Log.d("SmsParser", dto.toString());

                    String telegramMsg = textParser.format(dto);

                    Log.d("SmsParser", telegramMsg);

                    if (dto.isPaid()) {
                        String prevMonth = DateFormatUtils.format(DateUtils.addMonths(new Date(), -1), "yyyyMM");
                        Settlement prevSettle = db.settlementDao().findSettlementByDateAndCardType(dto.getPaymentType(), prevMonth);

                        if (prevSettle == null)
                            db.settlementDao().insertAll(new Settlement(
                                    prevMonth,
                                    dto.getPrice(),
                                    dto.getPaymentType()));
                        else
                            db.settlementDao().updatePriceByDateAndCardType(dto.getPrice(), dto.getPaymentType(), prevMonth);
                    }

                    if (dto.isAccumCalc()) {
                        AccumulatePrice.calculate(db, dto);

                        String currentMonth = DateFormatUtils.format(new Date(), "yyyyMM");
                        List<Settlement> settlements = db.settlementDao().findSettlementByDate(currentMonth);
                        telegramMsg += textParser.formatAccum(currentMonth, settlements);
                    }

                    // db 입력
                    sms.setMessage(telegramMsg);
                    db.smsDao().insertAll(smsList.stream().toArray(Sms[]::new));

                    // 설정에서 자동 발송 on 일 경우만 발송
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                    boolean isSendTelegram = preferences.getBoolean("isSendTelegram", true);

                    // Telegram 전송
                    if (isSendTelegram)
                        new Telegram().send(telegramMsg);

                    fileLogger.log(Level.INFO, dto.toString());

                    // 자동 백업
                    SettingFragment.autoDatabaseBackup(context);
                } else {
                    Log.d("SmsParser", "not detect phone number" + sms.getNumber());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sms sms = new Sms("0000", e.getMessage(), DateFormatUtils.format(new Date(), "yyyyMMddhhmmss"));
            db.smsDao().insertAll(sms);
        }
    }
}
