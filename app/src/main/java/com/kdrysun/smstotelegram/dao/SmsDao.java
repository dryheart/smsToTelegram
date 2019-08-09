package com.kdrysun.smstotelegram.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import com.kdrysun.smstotelegram.domain.Sms;

import java.util.List;

@Dao
public interface SmsDao {

    @Query("SELECT * FROM sms ORDER BY seq DESC")
    List<Sms> getAll();

    @Insert
    void insertAll(Sms... sms);

    @Delete
    void delete(Sms sms);
}
