package com.kdrysun.smstotelegram.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.kdrysun.smstotelegram.domain.Settlement;

import java.util.List;

@Dao
public interface SettlementDao {

    @Query("SELECT * FROM settlement ORDER BY seq DESC")
    List<Settlement> getAll();

    @Query("SELECT * FROM settlement WHERE type = :type AND date = :date")
    Settlement findSettlementByDate(String type, String date);

    @Insert
    void insertAll(Settlement... settlements);

    @Delete
    void delete(Settlement settlement);
}
