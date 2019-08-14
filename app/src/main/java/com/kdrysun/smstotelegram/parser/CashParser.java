package com.kdrysun.smstotelegram.parser;

import com.kdrysun.smstotelegram.domain.PaymentType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CashParser extends TextParser {

    private String[] cashPattern = {
            ".*KDB[\\S]*[\\s\\r\\n]*(.[^\\r\\n]*)[\\s\\r\\n]*([0-9,]+)원[\\s\\r\\n]*(.[^\\r\\n]+)[\\s\\r\\n]*잔액([0-9,]+)원[\\s\\r\\n]*(.*)",
            ".*전북[\\S]*[\\s\\r\\n]*(.[^\\r\\n\\d,]*)([0-9,]+)원.*잔액([0-9,]+)원[\\s\\r\\n]*([\\d\\s/:]+)[\\s\\r\\n](.*)"
    };

    @Override
    public TextDto parse(String originalMessage) {
        dto = new TextDto();
        dto.setPaymentType(PaymentType.CASH);

        try {
            for (int i = 0; i <= cashPattern.length; i++) {
                Pattern p = Pattern.compile(cashPattern[i], Pattern.DOTALL);
                Matcher matcher = p.matcher(originalMessage);

                if (matcher.matches()) {
                    dto.setParsed(true);
                    dto.setType(matcher.group(1));
                    dto.setPrice(matcher.group(2));
                    switch (i) {
                        case 0:
                            // KDB 산업은행
                            dto.setPlace(matcher.group(3));
                            dto.setBalance(matcher.group(4));
                            dto.setTime(matcher.group(5));
                            break;

                        case 1:
                            // 전북은행
                            dto.setBalance(matcher.group(3));
                            dto.setTime(matcher.group(4));
                            dto.setPlace(matcher.group(5));
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
