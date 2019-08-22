package com.kdrysun.smstotelegram.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.kdrysun.smstotelegram.R;
import com.kdrysun.smstotelegram.database.SmsDatabase;
import com.kdrysun.smstotelegram.domain.Sms;
import com.kdrysun.smstotelegram.receiver.Telegram;

public class SmsFragment extends Fragment {

    private ListView smsListView;
    private ArrayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sms_frame, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        smsListView = getActivity().findViewById(R.id.smsList);

        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1) ;
        adapter.notifyDataSetChanged();
        smsListView.setAdapter(adapter);

        smsListView.setOnItemLongClickListener((parent, view1, position, id) -> {

            Sms sms = (Sms) adapter.getItem(position);

            new AlertDialog.Builder(getContext()).setTitle("재발송").
                    setMessage("재발송 하시겠습니까?").setCancelable(false).
                    setPositiveButton("확인", (dialog, which) -> {
                        new Telegram().send(sms.getMessage());
                    }).
                    setNegativeButton("취소", (dialog, which) -> {
                        dialog.dismiss();
                    }).show();

            return true;
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        SmsDatabase db = SmsDatabase.getInstance(getContext());
        new Thread(() -> {
            adapter.addAll(db.smsDao().getAll());
        }).start();
    }
}
