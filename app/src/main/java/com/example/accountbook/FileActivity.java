package com.example.accountbook;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.accountbook.db.DBHelper;
import com.example.accountbook.entity.Account;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class FileActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE"};
    private Button butBack;
    private Button butImport;
    private Button butExport;
    private EditText ediPath;
    private DBHelper dbHelper = new DBHelper(this);
    private String[] types = {"未知", "支出", "收入"};

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //检测是否有写的权限
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // 没有写的权限，去申请写的权限，会弹出对话框
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file);
        verifyStoragePermissions(this);
        butBack = (Button) findViewById(R.id.but_file_back);
        butImport = (Button) findViewById(R.id.but_file_import);
        butExport = (Button) findViewById(R.id.but_file_export);
        ediPath = (EditText) findViewById(R.id.edi_file_path);
        butBack.setOnClickListener(new BackListener());
        butImport.setOnClickListener(new ImportListener());
        butExport.setOnClickListener(new ExportListener());
    }

    private File openOrCreateFile(String path) {
        File file = new File(path);
        return file;
    }

    private List<Account> loadAccountsFromDB() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        List<Account> list = new LinkedList<Account>();
        Account account;
        Cursor cursor = db.query("account", null, null, null, null, null, null);
        if (cursor.isBeforeFirst()) {
            cursor.moveToFirst();
        }
        while (!cursor.isAfterLast()) {
            account = new Account();
            account.setId(cursor.getInt(cursor.getColumnIndex("id")));
            account.setType(cursor.getInt(cursor.getColumnIndex("type")));
            account.setTag(cursor.getString(cursor.getColumnIndex("tag")));
            account.setAmount(cursor.getDouble(cursor.getColumnIndex("amount")));
            account.setDate(cursor.getString(cursor.getColumnIndex("date")));
            account.setDetail(cursor.getString(cursor.getColumnIndex("detail")));
            list.add(account);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        return list;
    }

    private boolean writeToFile(List<Account> list, File file) {
        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(file);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        String line;
        for (Account account : list) {
            line = String.format("ID:%d\ntype:%s\ntag:%s\ndate:%s\namount:%f\ndetail:%s\n\n",
                    account.getId(),
                    types[account.getType()],
                    account.getTag(),
                    account.getDate(),
                    account.getAmount(),
                    account.getDetail()
            );
            try {
                bufferedWriter.append(line);
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        try {
            bufferedWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean export(String path) {
        File file = openOrCreateFile(path);
        List<Account> list = loadAccountsFromDB();
        boolean success = writeToFile(list, file);
        if (success) {
            Toast.makeText(this, "导出成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "导出失败", Toast.LENGTH_SHORT).show();
        }
        return success;
    }

    private List<Account> loadAccountsFromFile(File file) {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("loadAccountsFromFile", "FileReader创建失败");
        }
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line;
        Account account;
        List<Account> list = new LinkedList<Account>();
        while (true) {
            account = new Account();
            try {
                //顺序：id type tag date amount detail
                line = bufferedReader.readLine();
                account.setId(Integer.valueOf(line.substring(line.indexOf(":") + 1)));
                line = bufferedReader.readLine();
                account.setType(Integer.valueOf(line.substring(line.indexOf(":") + 1)));
                line = bufferedReader.readLine();
                account.setTag(line.substring(line.indexOf(":") + 1));
                line = bufferedReader.readLine();
                account.setDate(line.substring(line.indexOf(":") + 1));
                line = bufferedReader.readLine();
                account.setAmount(Double.valueOf(line.substring(line.indexOf(":") + 1)));
                line = bufferedReader.readLine();
                account.setDetail(line.substring(line.indexOf(":") + 1));
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("load accounts from file", "error occurred when reading line from file");
                break;
            }
            list.add(account);
        }
        return list;
    }

    private boolean writeToDB(List<Account> list) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values;
        for (Account account : list) {
            values = new ContentValues();
            values.put("id", account.getId() + "");
            values.put("type", account.getType() + "");
            values.put("tag", account.getTag() + "");
            values.put("date", account.getDate() + "");
            values.put("amount", account.getAmount() + "");
            values.put("detail", account.getDetail() + "");
            db.insert("account", null, values);
        }
        db.close();
        return true;
    }

    private boolean import_(String path) {
        File file = openOrCreateFile(path);
        List<Account> list = loadAccountsFromFile(file);
        boolean success = writeToDB(list);
        if (success) {
            Toast.makeText(this, "导入成功", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "导入失败", Toast.LENGTH_SHORT).show();
        }
        return success;
    }

    class BackListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(FileActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    class ImportListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String path = ediPath.getText() + "";
            import_("data\\data\\accountbook\\" + path);
        }
    }

    class ExportListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            String path = ediPath.getText() + "";
            export("data\\data\\accountbook\\" + path);
        }
    }
}
