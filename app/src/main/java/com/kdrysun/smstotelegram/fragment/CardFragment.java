package com.kdrysun.smstotelegram.fragment;

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
import com.kdrysun.smstotelegram.domain.Card;

import java.util.List;

public class CardFragment extends Fragment {

    private ListView cardListView;
    private ArrayAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.card_frame, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        cardListView = getActivity().findViewById(R.id.cardList);

        adapter = new ArrayAdapter(getContext(), android.R.layout.simple_list_item_1) ;
        adapter.notifyDataSetChanged();
        cardListView.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        new Thread(() -> {
            SmsDatabase db = SmsDatabase.getInstance(getContext());
            List<Card> cards = db.cardDao().getAll();
            getActivity().runOnUiThread(() -> {
                adapter.clear();
                adapter.addAll(cards);
            });
        }).start();
    }
}
