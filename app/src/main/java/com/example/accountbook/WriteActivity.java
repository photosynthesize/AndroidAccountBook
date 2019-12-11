package com.example.accountbook;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.accountbook.db.DBHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WriteActivity extends AppCompatActivity {
    private Button butCancel;
    private Button butSave;
    private Button butSelectTag;
    private TextView texCurrentTag;
    private RadioButton radInc;
    private RadioButton radExp;
    private EditText ediAmount;
    private EditText ediDetail;
    private LinearLayout laySelectTag;
    private String currentTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        butCancel = (Button) findViewById(R.id.but__write_cancel);
        butCancel.setOnClickListener(new CancelListener());
        butSave = (Button) findViewById(R.id.but__write_save);
        butSave.setOnClickListener(new SaveListener());
        butSelectTag = (Button) findViewById(R.id.but_write_selectTag);
        butSelectTag.setOnClickListener(new SelectTagListener());
        radInc = (RadioButton) findViewById(R.id.rad_write_income);
        radExp = (RadioButton) findViewById(R.id.rad_write_expenditure);
        ediAmount = (EditText) findViewById(R.id.edi_write_money);
        ediDetail = (EditText) findViewById(R.id.edi_write_detail);
        texCurrentTag = (TextView) findViewById(R.id.tex_write_currentTag);
    }

    class CancelListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(WriteActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    class SaveListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            //Intent intent = new Intent(WriteActivity.this, MainActivity.class);
            //startActivity(intent);
            SimpleDateFormat ft = new SimpleDateFormat("YYYY-MM-dd HH:mm:mm");
            String date = ft.format(new Date());
            String type = "unknown type";
            String amount = "114514";
            String tag = "unknown tag";
            String detail = "unknown detail";

            if (radExp.isChecked()) {
                type = "1";
            }
            if (radInc.isChecked()) {
                type = "2";
            }
            amount = ediAmount.getText() + "";
            tag = currentTag;
            detail = ediDetail.getText() + "";
            Log.d("date", date);
            Log.d("type", type);
            Log.d("amount", amount);
            Log.d("tag", tag);
            Log.d("detail", detail);
            save(type, tag, amount, detail, date);
            Toast.makeText(WriteActivity.this, "账目已保存", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(WriteActivity.this, MainActivity.class);
            startActivity(intent);
        }

        private void save(String type, String tag, String amount, String detail, String date) {
            String sql = String.format("INSERT INTO account(amount, date, detail, tag, type) values(%s, '%s', '%s', '%s', %s);", amount, date, detail, tag, type);
            /*int mode = Context.MODE_APPEND;
            SQLiteDatabase db = WriteActivity.this.openOrCreateDatabase(getString(R.string.db_name), mode, null);*/
            DBHelper dbHelper = new DBHelper(WriteActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            db.execSQL(sql);
            db.close();
        }

    }

    class SelectTagListener implements View.OnClickListener {
        private AlertDialog ad;

        @Override
        public void onClick(View v) {
            laySelectTag = (LinearLayout) getLayoutInflater().inflate(R.layout.dialog_tags_exp, null);
            AlertDialog.Builder dialog = new AlertDialog.Builder(WriteActivity.this);
            dialog.setTitle("选择标签");
            dialog.setView(laySelectTag);
            dialog.create();
            ad = dialog.show();
            initializeSelections();
        }

        private void initializeSelections() {
            //LinearLayout lay = (LinearLayout)getLayoutInflater().inflate(R.id.lay_tag_selection, null);
            LinearLayout laySelection = (LinearLayout) laySelectTag.findViewById(R.id.lay_tag_selection);
            if (laySelection == null) {
                Log.d("null", "-----------------laySelection=null------------------------------");
            }
            int childCount = laySelection.getChildCount();
            Button butSelection;
            for (int i = 0; i < childCount; i++) {
                butSelection = (Button) laySelection.getChildAt(i);
                butSelection.setOnClickListener(new DoSelectListener(ad));
            }

        }

        class DoSelectListener implements View.OnClickListener {
            private AlertDialog ad;

            public DoSelectListener(AlertDialog a) {
                ad = a;
            }

            @Override
            public void onClick(View v) {
                String tag = ((Button) v).getText() + "";
                currentTag = tag;
                texCurrentTag.setText("标签：" + currentTag);
                ad.dismiss();
            }
        }
    }
}
