package com.kdrysun.smstotelegram.receiver;

import android.icu.text.NumberFormat;
import com.kdrysun.smstotelegram.domain.PaymentType;
import com.kdrysun.smstotelegram.domain.Settlement;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextParse {

    private static String pattern = ".*(승인|취소)[\\s\\r\\n]*\\S+[\\s\\r\\n]*([0-9,]+)원[\\s\\r\\n(]+(\\S[^\\s\\r\\n)]+).[^\\d]*(\\d+.*:\\d+)[\\s\\r\\n]*(.+)[\\s\\r\\n]+누적([0-9,]+)원";
    private static String etcPattern = "\\[Web발신\\][\\s\\r\\n]*(.*)";

    public static TextDto parse(PaymentType paymentType, String originalMessage) {

        TextDto dto = new TextDto();
        dto.setPaymentType(paymentType);
        try {
            // 첫번째 승인/취소를 못찾았을 경우 Web발신만 제거
            Pattern p = Pattern.compile(pattern);
            Matcher matcher = p.matcher(originalMessage);

            if (matcher.matches()) {
                dto.setParsed(true);
                dto.setType(matcher.group(1));
                dto.setPrice(matcher.group(2));
                dto.setMethod(matcher.group(3));
                dto.setTime(matcher.group(4));
                dto.setPlace(matcher.group(5));
                dto.setAccum(matcher.group(6));
            } else {
                p = Pattern.compile(pattern);
                matcher = p.matcher(originalMessage);

                if (matcher.matches())
                    dto.setPlace(matcher.group(1));
            }
        } catch (Exception e) {
            // TODO 로그처리
        }

        return dto;
    }

    /**
     * 카드금액 아래내용처럼 포맷팅
     *
     * [국민카드] 일시불 승인 5000원
     * 08/09 12:10 완이네 작은
     *
     * 누적 금액
     * 국민 : 80000 원
     * 신한 : 00000 원
     * 현금 : 00000 원
     *
     * @param dto 문자정보
     * @return 포맷팅한 문자열
     */
    public static String format(TextDto dto) {
        return new StringBuilder().append("[" + dto.getPaymentType().getName() + "]").
                append(" ").append(dto.getMethod()).append(" ").append(dto.getType()).
                append(" ").append(NumberFormat.getInstance().format(dto.getPrice()) + "원\n").
                append(dto.getTime()).append(" ").append(dto.getPlace()).toString();
    }

    public static String formatAccum(String telegramMsg, List<Settlement> settlements) {
        StringBuilder str = new StringBuilder();

        if (settlements == null || settlements.size() == 0)
            return "";

        str.append("\n\n누적금액\n");
        for (Settlement stl : settlements)
            str.append(stl.getType().getName()).append(": ").append(stl.getPrice()).append("원\n");

        return telegramMsg += str.toString();
    }
}
