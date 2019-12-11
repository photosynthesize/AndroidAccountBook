package com.example.accountbook;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private Button write;
    private Button list;
    private Button stat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        write = (Button) findViewById(R.id.but_main_write);
        list = (Button) findViewById(R.id.but_main_list);
        stat = (Button) findViewById(R.id.but_main_stat);

        write.setOnClickListener(new WriteListener());
        list.setOnClickListener(new ListListener());
        stat.setOnClickListener(new StatListener());

        createDB();
    }

    private void createDB() {
        String sql = "CREATE TABLE IF NOT EXISTS account (" +
                "id INTEGER primary key, " +
                "type INTEGER not null," +
                "tag text not null," +
                "detail text," +
                "date text not null," +
                "amount real not null" +
                ");";
        int mode = Context.MODE_PRIVATE;
        SQLiteDatabase db = this.openOrCreateDatabase(getString(R.string.db_name), mode, null);
        db.execSQL(sql);
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
}
