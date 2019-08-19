package com.kdrysun.smstotelegram.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import com.kdrysun.smstotelegram.R;
import com.kdrysun.smstotelegram.database.SmsDatabase;
import com.kdrysun.smstotelegram.domain.PaymentType;
import com.kdrysun.smstotelegram.domain.Settlement;
import com.kdrysun.smstotelegram.domain.typeconverter.PaymentTypeConverter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.Arrays;

public class SettlementDialog extends Dialog {
    private Settlement settlement;

    public SettlementDialog(Context context) {
        super(context);
    }

    public SettlementDialog(Context context, Settlement settlement) {
        super(context);
        this.settlement = settlement;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settlement_dialog);

        TextView paymentTypeEdit = findViewById(R.id.settlementPopupPaymentTypeInput);
        paymentTypeEdit.setText(settlement.getType().getName());
        paymentTypeEdit.setOnClickListener(v -> {
            String[] paymentTypes = Arrays.stream(PaymentType.values()).map(PaymentType::getName).toArray(String[]::new);
            new AlertDialog.Builder(getContext()).
                    setTitle("결제수단").
                    setSingleChoiceItems(paymentTypes, -1, (dialog, which) -> {
                        String paymentTypeName = paymentTypes[which];
                        PaymentType paymentType = new PaymentTypeConverter().toCardType(paymentTypeName);
                        paymentTypeEdit.setText(paymentType.getName());
                        settlement.setType(paymentType);
                        dialog.dismiss();
                    }).show();
        });

        EditText dateEdit = findViewById(R.id.settlementPopupDateInput);
        dateEdit.setText(settlement.getDate());
        EditText priceEdit = findViewById(R.id.settlementPopupPriceInput);
        priceEdit.setText(settlement.getPrice() + "");
        TextView paidEdit = findViewById(R.id.settlementPopupPaidInput);
        paidEdit.setText(settlement.isPaid() + "");
        paidEdit.setOnClickListener(v -> {
            String[] paids = {"true", "false"};
            new AlertDialog.Builder(getContext()).
                    setTitle("결제여부").
                    setSingleChoiceItems(paids, -1, (dialog, which) -> {
                        String paid = paids[which];
                        boolean isPaid = Boolean.valueOf(paid);
                        paidEdit.setText(paid);
                        settlement.setPaid(isPaid);
                        dialog.dismiss();
                    }).show();
        });

        Button cancel = findViewById(R.id.settlementPopupCancelButton);
        cancel.setOnClickListener(v -> SettlementDialog.this.dismiss());

        Button delete = findViewById(R.id.settlementPopupDeleteButton);
        delete.setOnClickListener(v -> {
            new AlertDialog.Builder(getContext()).setTitle("삭제").
                    setMessage("삭제하시겠습니까?").setCancelable(false).
                    setPositiveButton("확인", (dialog, which) -> {

                        Toast.makeText(getContext(), settlement.getSeq() + ":" + settlement.toString(), Toast.LENGTH_SHORT).show();

                        SmsDatabase db = SmsDatabase.getInstance(getContext());
                        new Thread(() -> {
                            db.settlementDao().delete(settlement);
                        }).start();

                        this.dismiss();
                    }).
                    setNegativeButton("취소", (dialog, which) -> {
                        dialog.dismiss();
                    }).show();
        });

        Button update = findViewById(R.id.settlementPopupUpdateButton);
        update.setOnClickListener(v -> {
            String type = paymentTypeEdit.getText().toString();
            PaymentType paymentType = new PaymentTypeConverter().toCardType(type);
            String date = dateEdit.getText().toString();
            String price = priceEdit.getText().toString();
            boolean isPaid = Boolean.valueOf(paidEdit.getText().toString());

            if (paymentType == null) {
                Toast.makeText(getContext(), "결제수단 입력필요", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!NumberUtils.isDigits(date) || StringUtils.length(date) != 6) {
                Toast.makeText(getContext(), "날짜는 yyyyMM 형식으로 입력가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!NumberUtils.isDigits(price)) {
                Toast.makeText(getContext(), "금액은 숫자만 입력가능합니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            settlement.setType(paymentType);
            settlement.setDate(date);
            settlement.setPrice(NumberUtils.toLong(price));
            settlement.setPaid(isPaid);

            Toast.makeText(getContext(), settlement.getSeq() + ":" + settlement.toString(), Toast.LENGTH_SHORT).show();

            SmsDatabase db = SmsDatabase.getInstance(getContext());
            new Thread(() -> {
                db.settlementDao().update(settlement);
            }).start();

            this.dismiss();
        });
    }
}
