package com.kdrysun.smstotelegram.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.kdrysun.smstotelegram.R;
import com.kdrysun.smstotelegram.database.SmsDatabase;
import com.kdrysun.smstotelegram.domain.Settlement;

public class SettlementFragment extends Fragment {

    private ListView settlementListView;
    private Button addButton;
    private ArrayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settlement_frame, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Context context = getActivity().getApplicationContext();

        settlementListView = getActivity().findViewById(R.id.cardList);
        adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1);

        new Thread(() -> {
            SmsDatabase db = SmsDatabase.getInstance(context);
            adapter.addAll(db.settlementDao().getAll());

            settlementListView.setAdapter(adapter);
        }).start();

        settlementListView.setOnItemLongClickListener((parent, view1, position, id) -> {

            Settlement settlement = (Settlement) adapter.getItem(position);

            Toast.makeText(context, settlement.toString(), Toast.LENGTH_SHORT).show();


            //adapter.notifyDataSetChanged();
            return true;
        });


        addButton = getActivity().findViewById(R.id.settlementAddButton);
        addButton.setOnClickListener(v -> {

            // TODO 입력 팝업 호출


            // TODO 입력


            // TODO 갱신
            adapter.notifyDataSetChanged();


        });
    }
}
