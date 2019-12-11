package com.example.accountbook;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accountbook.db.DBHelper;

import java.util.Calendar;

public class StatActivity extends AppCompatActivity {
    private Button back;
    private Button butDay;
    private Button butWeek;
    private Button butMonth;
    private Button butYear;
    private TextView texTotalIncome;
    private TextView texTotalExpenditure;
    private TextView texSurplus;
    private double totalIncome;
    private double totalExpenditure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stat);

        StatListener statListener = new StatListener();
        back = (Button) findViewById(R.id.but_stat_back);
        back.setOnClickListener(new BackListener());
        butWeek = (Button) findViewById(R.id.but_stat_weekly);
        butWeek.setOnClickListener(statListener);
        butDay = (Button) findViewById(R.id.but_stat_daily);
        butDay.setOnClickListener(statListener);
        butMonth = (Button) findViewById(R.id.but_stat_monthly);
        butMonth.setOnClickListener(statListener);
        butYear = (Button) findViewById(R.id.but_stat_annual);
        butYear.setOnClickListener(statListener);
        texSurplus = (TextView) findViewById(R.id.tex_stat_surplus);
        texTotalExpenditure = (TextView) findViewById(R.id.tex_stat_spending);
        texTotalIncome = (TextView) findViewById(R.id.tex_stat_income);
    }

    class BackListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(StatActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    class StatListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d("StatListener", "stat on click");
            String text = ((Button) v).getText() + "";
            String likeExpr = "likeExpr00";
            //this week
            if (text.equals(getString(R.string.this_week))) {
                statWeek();
                display();
                return;
            }
            //today
            if (text.equals(getString(R.string.today))) {
                likeExpr = statDay();
                Log.d("stat", "today");
            }
            //this month
            if (text.equals(getString(R.string.this_month))) {
                likeExpr = statMonth();
                Log.d("stat", "this month");
            }
            //this year
            if (text.equals(getString(R.string.this_year))) {
                likeExpr = statYear();
                Log.d("stat", "this year");
            }
            doStat(likeExpr);
            display();
        }

        private void display() {
            texTotalExpenditure.setText(getString(R.string.total_expenditure) + totalExpenditure + "元");
            texTotalIncome.setText(getString(R.string.total_income) + totalIncome + "元");
            texSurplus.setText(getString(R.string.surplus) + (totalIncome - totalExpenditure) + "元");
        }

        private String[] dateInAWeek() {
            int[] daysOfWeek = {7, 1, 2, 3, 4, 5, 6};
            Calendar calendar = Calendar.getInstance();
            int dayEurope = calendar.get(Calendar.DAY_OF_WEEK);
            int dayChinese = daysOfWeek[dayEurope - 1];
            String[] res = new String[dayChinese];
            for (int i = dayChinese; i >= 1; i--) {
                res[i - 1] = String.format("%04d-%02d-%02d", calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DATE));
                calendar.add(Calendar.DATE, -1);
            }
            return res;
        }

        private void doStat(String likeExpr) {
            DBHelper dbHelper = new DBHelper(StatActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String sqlIncome = String.format("select sum(amount) from account where type=2 and date like '%s';", likeExpr);
            Cursor cursor = db.rawQuery(sqlIncome, null);
            if (cursor.isBeforeFirst()) {
                cursor.moveToNext();
            }
            totalIncome = cursor.getDouble(0);
            cursor.close();

            String sqlExp = String.format("select sum(amount) from account where type=1 and date like '%s';", likeExpr);
            cursor = db.rawQuery(sqlExp, null);
            if (cursor.isBeforeFirst()) {
                cursor.moveToNext();
            }
            totalExpenditure = cursor.getDouble(0);
            cursor.close();
            db.close();
        }

        private void statWeek() {
            String[] dateInTheWeek = dateInAWeek();
            String whereClause = "";
            for (int i = 0; i < dateInTheWeek.length; i++) {
                whereClause = whereClause + "date like '" + dateInTheWeek[i] + "%' or ";
            }
            whereClause += "1=2";
            DBHelper dbHelper = new DBHelper(StatActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            String sqlIncome = String.format("select sum(amount) from account where type=2 and (%s);", whereClause);
            Log.d("sql", sqlIncome);
            Cursor cursor = db.rawQuery(sqlIncome, null);
            if (cursor.isBeforeFirst()) {
                cursor.moveToNext();
            }
            totalIncome = cursor.getDouble(0);
            cursor.close();

            String sqlExp = String.format("select sum(amount) from account where type=1 and (%s);", whereClause);
            cursor = db.rawQuery(sqlExp, null);
            if (cursor.isBeforeFirst()) {
                cursor.moveToNext();
            }
            totalExpenditure = cursor.getDouble(0);
            cursor.close();
            db.close();
        }

        private String statDay() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            String likeExpr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth) + "%";
            return likeExpr;
        }

        private String statMonth() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            String likeExpr = String.format("%04d-%02d", year, month + 1) + "%";
            return likeExpr;
        }

        private String statYear() {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            String likeExpr = String.format("%04d", year) + "%";
            return likeExpr;
        }
    }
}
