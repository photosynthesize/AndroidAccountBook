package com.example.accountbook;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class ListActivity extends AppCompatActivity {
    private Button butBack;
    private Button butCalender;
    private CalendarView calendar;
    private ScrollView scrContent;
    private TextView textTest;
    private SQLiteDatabase db;
    private boolean folded = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        butBack = (Button) findViewById(R.id.but_list_back);
        butBack.setOnClickListener(new BackListener());
        butCalender = (Button) findViewById(R.id.but_list_calender);
        butCalender.setOnClickListener(new FoldCalenderListener());
        calendar = (CalendarView) findViewById(R.id.cal_list_calendar);
        calendar.setOnDateChangeListener(new SelectDateListener());
        textTest = (TextView) findViewById(R.id.tex_list_test);
        displayAccount();
    }

    //显示指定日期的账目
    private void displayAccountByDate(String day) {
        int mode = Context.MODE_PRIVATE;
        int type;
        String tag;
        String date;
        String detail;
        double amount;
        String line = "";
        String whereClause = "date like ?";
        String[] args = {day + "%"};
        db = openOrCreateDatabase(getString(R.string.db_name), mode, null);
        Cursor cursor = db.query("account", null, whereClause, args, null, null, null);

        if (cursor.isBeforeFirst()) {
            cursor.moveToFirst();
        }
        while (!cursor.isAfterLast()) {
            type = cursor.getInt(cursor.getColumnIndex("type"));
            tag = cursor.getString(cursor.getColumnIndex("tag"));
            date = cursor.getString(cursor.getColumnIndex("date"));
            detail = cursor.getString(cursor.getColumnIndex("detail"));
            amount = cursor.getDouble(cursor.getColumnIndex("amount"));
            line = line + String.format("账目类别：%d\n标签：%s\n日期：%s\n金额：%.2f\n详情：%s\n\n", type, tag, date, amount, detail);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        textTest.setText(line);
    }

    //显示全部账目
    private void displayAccount() {
        int mode = Context.MODE_PRIVATE;
        int type;
        String tag;
        String date;
        String detail;
        double amount;
        String line = "";
        db = openOrCreateDatabase(getString(R.string.db_name), mode, null);
        Cursor cursor = db.query("account", null, null, null, null, null, null);
        if (cursor.isBeforeFirst()) {
            cursor.moveToFirst();
        }
        while (!cursor.isAfterLast()) {
            type = cursor.getInt(cursor.getColumnIndex("type"));
            tag = cursor.getString(cursor.getColumnIndex("tag"));
            date = cursor.getString(cursor.getColumnIndex("date"));
            detail = cursor.getString(cursor.getColumnIndex("detail"));
            amount = cursor.getDouble(cursor.getColumnIndex("amount"));
            line = line + String.format("账目类别：%d\n标签：%s\n日期：%s\n金额：%.2f\n详情：%s\n\n", type, tag, date, amount, detail);
            cursor.moveToNext();
        }
        cursor.close();
        db.close();
        textTest.setText(line);
    }

    class SelectDateListener implements CalendarView.OnDateChangeListener {

        @Override
        public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
            String day = year + "-" + addZero(month + 1) + "-" + addZero(dayOfMonth);
            Log.d("day", day);
            displayAccountByDate(day);
        }

        //在只有一位的数字前面补一个0
        private String addZero(int number) {
            String res = number + "";
            if (res.length() == 1) {
                res = "0" + res;
            }
            return res;
        }
    }

    class FoldCalenderListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (folded == false) {
                calendar.setVisibility(View.GONE);
                butCalender.setText(getString(R.string.unfold_calender));
                folded = true;
                return;
            }
            if (folded == true) {
                calendar.setVisibility(View.VISIBLE);
                butCalender.setText(getString(R.string.fold_calender));
                folded = false;
                return;
            }
        }
    }

    class BackListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(ListActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
