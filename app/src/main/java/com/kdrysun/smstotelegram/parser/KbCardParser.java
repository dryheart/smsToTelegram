package com.kdrysun.smstotelegram.parser;

import com.kdrysun.smstotelegram.domain.PaymentType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class KbCardParser extends TextParser {

    private String[] kbPattern = {
            ".*(승인|취소)[\\s\\r\\n]*\\S+[\\s\\r\\n]*([0-9,]+)원[\\s\\r\\n(]+(\\S[^\\s\\r\\n)]+).[^\\d]*(\\d+.*:\\d+)[\\s\\r\\n]*(.+)[\\s\\r\\n]+누적([0-9,]+)원",
            ".*(승인|취소)[\\s\\r\\n]*\\S+[\\s\\r\\n]*([0-9,]+)원[\\s\\r\\n(]+(\\S[^\\s\\r\\n)]*)[\\s\\r\\n]*(.+)",
            ".[^\\d]*([\\d/]+)[\\s\\r\\n]*결제금액[\\s\\r\\n]+([\\d,]+)원.*"
    };

    @Override
    public TextDto parse(String originalMessage) {
        dto = new TextDto();
        dto.setPaymentType(PaymentType.KBCARD);

        try {
            for (int i = 0; i <= kbPattern.length; i++) {
                Pattern p = Pattern.compile(kbPattern[i], Pattern.DOTALL);
                Matcher matcher = p.matcher(originalMessage);

                if (matcher.matches()) {
                    dto.setParsed(true);
                    switch (i) {
                        case 0:
                            dto.setType(matcher.group(1));
                            dto.setPrice(matcher.group(2));
                            dto.setMethod(matcher.group(3));
                            dto.setTime(matcher.group(4));
                            dto.setPlace(matcher.group(5));
                            dto.setAccum(matcher.group(6));
                            break;

                        case 1:
                            dto.setType(matcher.group(1));
                            dto.setPrice(matcher.group(2));
                            dto.setTime(matcher.group(3));
                            dto.setPlace(matcher.group(4));
                            break;

                        case 2:
                            dto.setTime(matcher.group(1));
                            dto.setPrice(matcher.group(2));
                            dto.setPlace("결제금액");
                            dto.setPaid(true);
                            break;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (!dto.isParsed())
            return super.parse(originalMessage);

        return dto;
    }
}
