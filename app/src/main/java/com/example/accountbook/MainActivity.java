package com.example.accountbook;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accountbook.db.DBHelper;

public class MainActivity extends AppCompatActivity {
    private Button write;
    private Button list;
    private Button stat;
    private Button file;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        write = (Button) findViewById(R.id.but_main_write);
        list = (Button) findViewById(R.id.but_main_list);
        stat = (Button) findViewById(R.id.but_main_stat);
        file = (Button) findViewById(R.id.but_main_file);

        write.setOnClickListener(new WriteListener());
        list.setOnClickListener(new ListListener());
        stat.setOnClickListener(new StatListener());
        file.setOnClickListener(new FileListener());

        createDB();
    }

    private void createDB() {
        /*String sql = "CREATE TABLE IF NOT EXISTS account (" +
                "id INTEGER primary key, " +
                "type INTEGER not null," +
                "tag text not null," +
                "detail text," +
                "date text not null," +
                "amount real not null" +
                ");";
        int mode = Context.MODE_PRIVATE;
        SQLiteDatabase db = this.openOrCreateDatabase(getString(R.string.db_name), mode, null);
        db.execSQL(sql);*/
        DBHelper dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        db.close();
    }

    class FileListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intentToWrite = new Intent(MainActivity.this, FileActivity.class);
            startActivity(intentToWrite);
        }
    }

    class WriteListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intentToWrite = new Intent(MainActivity.this, WriteActivity.class);
            startActivity(intentToWrite);
        }
    }

    class StatListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intentToWrite = new Intent(MainActivity.this, StatActivity.class);
            startActivity(intentToWrite);
        }
    }

    class ListListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intentToWrite = new Intent(MainActivity.this, ListActivity.class);
            startActivity(intentToWrite);
        }
    }

    class HealthListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Toast.makeText(MainActivity.this, "此功能尚未实现", Toast.LENGTH_SHORT).show();
        }
    }
}
