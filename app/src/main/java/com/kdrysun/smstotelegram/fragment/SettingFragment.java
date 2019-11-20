package com.kdrysun.smstotelegram.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import com.kdrysun.smstotelegram.R;
import com.kdrysun.smstotelegram.database.SmsDatabase;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

public class SettingFragment extends PreferenceFragmentCompat {

    private int documentTreeRequestCode = 9999;
    private int documentTreeFileRequestCode = 9998;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting, rootKey);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            try {
                SmsDatabase db = SmsDatabase.getInstance(getContext());
                db.close();

                String dbPath = db.getOpenHelper().getWritableDatabase().getPath();
                File dbFile = new File(dbPath);

                if (requestCode == documentTreeRequestCode) {
                    DocumentFile documentFile = DocumentFile.fromTreeUri(getContext(), data.getData());
                    DocumentFile backupFile = documentFile.createFile("", "sms_" + DateFormatUtils.format(new Date(), "yyyyMMdd") + ".db");
                    OutputStream outputStream = getContext().getContentResolver().openOutputStream(backupFile.getUri());
                    FileUtils.copyFile(dbFile, outputStream);

                    outputStream.close();

                    Toast.makeText(getContext(), "DB 백업완료", Toast.LENGTH_LONG).show();
                } else if (requestCode == documentTreeFileRequestCode) {
                    InputStream inputStream = getContext().getContentResolver().openInputStream(data.getData());

                    FileUtils.copyInputStreamToFile(inputStream, dbFile);

                    inputStream.close();

                    Toast.makeText(getContext(), "DB 복원완료", Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onPreferenceTreeClick(Preference preference) {

        if (StringUtils.equals(preference.getKey(), "databaseBackup")) {

            // 백업
            if (checkWriteStoragePermission()) {
                // 디렉토리 선택 팝업 호출
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                intent.addCategory(Intent.CATEGORY_DEFAULT);
                startActivityForResult(Intent.createChooser(intent, "디렉토리 선택"), documentTreeRequestCode);
            }
        } else if (StringUtils.equals(preference.getKey(), "databaseRestore")) {

            // 복원
            if (checkReadStoragePermission()) {

                // 파일 선택 팝업 호출
                Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                i.addCategory(Intent.CATEGORY_OPENABLE);
                i.setType("*/*");
                startActivityForResult(Intent.createChooser(i, "파일 선택"), documentTreeFileRequestCode);
            }
        }

        return super.onPreferenceTreeClick(preference);
    }

    private boolean checkReadStoragePermission() {
        int permissonCheck= ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE);

        if (permissonCheck != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.READ_EXTERNAL_STORAGE}, 1);

        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED;
    }

    private boolean checkWriteStoragePermission() {
        int permissonCheck= ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permissonCheck != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(getActivity(), new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);

        return ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    public static void autoDatabaseBackup(Context context) {
        try {
            SmsDatabase db = SmsDatabase.getInstance(context);
            db.close();

            String dbPath = db.getOpenHelper().getWritableDatabase().getPath();
            File dbFile = new File(dbPath);

            String backupFilePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath() + File.separator + "sms.db";
            File backupFile = new File(backupFilePath);
            if (!backupFile.exists()) backupFile.createNewFile();

            FileUtils.copyFile(dbFile, backupFile);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
