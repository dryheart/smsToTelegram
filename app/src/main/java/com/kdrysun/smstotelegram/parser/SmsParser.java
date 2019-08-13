package com.kdrysun.smstotelegram.parser;

import android.content.Context;
import androidx.room.Room;
import com.kdrysun.smstotelegram.database.SmsDatabase;
import com.kdrysun.smstotelegram.domain.PaymentType;
import com.kdrysun.smstotelegram.domain.Settlement;
import com.kdrysun.smstotelegram.domain.Sms;
import com.kdrysun.smstotelegram.receiver.Telegram;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.util.Date;
import java.util.List;

public class SmsParser {

    public void parse(Context context, List<Sms> smsList) {
        SmsDatabase db = Room.databaseBuilder(context, SmsDatabase.class, "sms.db").build();

        Date date = new Date();
        String currentMonth = DateFormatUtils.format(date, "yyyyMM");
        String prevMonth = DateFormatUtils.format(DateUtils.addMonths(date, -1), "yyyyMM");

        try {
            for (Sms sms : smsList) {
                PaymentType paymentType = db.cardDao().findCardType(sms.getNumber());
                if (paymentType != null) {

                    TextParser textParser = TextParserBuilder.build(paymentType);
                    TextDto dto = textParser.parse(sms.getMessage());

                    if (dto.isAccumCalc()) {
                        /*
                        누적금액 있을경우 누적금액에서 이전달 결제 금액 차감
                        누적금액이 없을 경우 현재결제 금액에 사용금액 증감
                         */
                        String dateCondition = dto.getAccum() > 0 ? prevMonth : currentMonth;
                        Settlement monthSettle = db.settlementDao().findSettlementByDateAndCardType(paymentType, dateCondition);
                        long monthPrice = monthSettle != null ? monthSettle.getPrice() : 0;

                        Settlement settle = new Settlement(currentMonth,
                                dto.getAccum() > 0 ? dto.getAccum() - monthPrice : monthPrice + dto.getPrice(),
                                paymentType);

                        Settlement currentSettle = db.settlementDao().findSettlementByDateAndCardType(paymentType, currentMonth);
                        if (currentSettle == null)
                            db.settlementDao().insertAll(settle);
                        else
                            db.settlementDao().updateByDateAndCardType(settle.getPrice(), settle.getType(), settle.getDate());
                    }

                    List<Settlement> settlements = db.settlementDao().findSettlementByDate(currentMonth);
                    String telegramMsg = textParser.formatAccum(currentMonth, settlements);

                    // db 입력
                    sms.setMessage(telegramMsg);
                    db.smsDao().insertAll(smsList.stream().toArray(Sms[]::new));

                    // Telegram 전송
                    new Telegram().send(telegramMsg);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Sms sms = new Sms("0000", e.getMessage(), DateFormatUtils.format(new Date(), "yyyyMMddhhmmss"));
            db.smsDao().insertAll(sms);
        }
    }
}
