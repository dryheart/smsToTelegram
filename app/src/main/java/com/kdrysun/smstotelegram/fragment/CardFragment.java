package com.kdrysun.smstotelegram.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.kdrysun.smstotelegram.R;
import com.kdrysun.smstotelegram.database.SmsDatabase;

public class CardFragment extends Fragment {

    private ListView cardListView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.card_frame, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        cardListView = getActivity().findViewById(R.id.cardList);

        new Thread(() -> {
            Context context = getActivity().getApplicationContext();
            SmsDatabase db = SmsDatabase.getInstance(context);
            ArrayAdapter adapter = new ArrayAdapter(context, android.R.layout.simple_list_item_1) ;
            adapter.addAll(db.cardDao().getAll());

            cardListView.setAdapter(adapter);
        }).start();
    }
}
