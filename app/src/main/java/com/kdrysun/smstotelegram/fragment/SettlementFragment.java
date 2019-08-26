package com.kdrysun.smstotelegram.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kdrysun.smstotelegram.R;
import com.kdrysun.smstotelegram.database.SmsDatabase;
import com.kdrysun.smstotelegram.dialog.SettlementDialog;
import com.kdrysun.smstotelegram.domain.Settlement;

import java.util.List;

public class SettlementFragment extends Fragment {

    private ListView settlementListView;
    private FloatingActionButton addButton;
    private ArrayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.settlement_frame, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        settlementListView = getActivity().findViewById(R.id.settlementList);

        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1);
        adapter.notifyDataSetChanged();
        settlementListView.setAdapter(adapter);

        settlementListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            Settlement settlement = (Settlement) adapter.getItem(position);
            SettlementDialog settlementDialog = new SettlementDialog(getContext(), settlement);
            settlementDialog.show();

            //adapter.notifyDataSetChanged();
            return true;
        });

/*
        addButton = getActivity().findViewById(R.id.settlementAddButton);
        addButton.setOnClickListener(v -> {

            // TODO 입력 팝업 호출


            // TODO 입력


            // TODO 갱신
            adapter.notifyDataSetChanged();
        });
*/
    }

    @Override
    public void onResume() {
        super.onResume();

        new Thread(() -> {
            SmsDatabase db = SmsDatabase.getInstance(getContext());
            List<Settlement> settlements = db.settlementDao().getAll();
            getActivity().runOnUiThread(() -> adapter.addAll(settlements));
        }).start();
    }
}
