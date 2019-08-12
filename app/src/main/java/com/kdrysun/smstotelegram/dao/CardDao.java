package com.kdrysun.smstotelegram.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.kdrysun.smstotelegram.domain.Card;
import com.kdrysun.smstotelegram.domain.PaymentType;

import java.util.List;

@Dao
public interface CardDao {

    @Query("SELECT * FROM card")
    List<Card> getAll();

    @Query("SELECT type FROM card WHERE phoneNumber = :phoneNumber")
    PaymentType findCardType(String phoneNumber);

    @Insert
    void insertAll(Card... cards);

    @Delete
    void delete(Card card);
}
