package com.kdrysun.smstotelegram.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.kdrysun.smstotelegram.domain.PaymentType;
import com.kdrysun.smstotelegram.domain.Settlement;

import java.util.List;

@Dao
public interface SettlementDao {

    @Query("SELECT * FROM settlement")
    List<Settlement> getAll();

    @Query("SELECT * FROM settlement WHERE type = :type AND date = :date")
    Settlement findSettlementByDateAndCardType(PaymentType type, String date);

    @Query("SELECT * FROM settlement WHERE date = :date ORDER BY type")
    List<Settlement> findSettlementByDate(String date);

    @Insert
    void insertAll(Settlement... settlements);

    @Delete
    void delete(Settlement settlement);

    @Query("UPDATE settlement SET price = :price WHERE type = :type AND date = :date")
    void updateByDateAndCardType(long price, PaymentType type, String date);
}
