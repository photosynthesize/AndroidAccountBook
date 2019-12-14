package com.example.accountbook;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
    private int accountType;

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
        radInc.setOnCheckedChangeListener(new TypeCheckListener());
        radExp = (RadioButton) findViewById(R.id.rad_write_expenditure);
        radExp.setOnCheckedChangeListener(new TypeCheckListener());
        ediAmount = (EditText) findViewById(R.id.edi_write_money);
        ediDetail = (EditText) findViewById(R.id.edi_write_detail);
        texCurrentTag = (TextView) findViewById(R.id.tex_write_currentTag);
        accountType = (radExp.isChecked()) ? DBHelper.TYPE_EXP : DBHelper.TYPE_INCOME;
    }

    class TypeCheckListener implements RadioButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked == false) return;
            if (buttonView == radExp) {
                Log.d("type", "expenditure");
                accountType = DBHelper.TYPE_EXP;
                return;
            }
            if (buttonView == radInc) {
                Log.d("type", "income");
                accountType = DBHelper.TYPE_INCOME;
                return;
            }
        }
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
                type = DBHelper.TYPE_EXP + "";
            }
            if (radInc.isChecked()) {
                type = DBHelper.TYPE_INCOME + "";
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
            EditText ediTagName = (EditText) laySelectTag.findViewById(R.id.edi_tag_tagName);
            Button butCancel = (Button) laySelectTag.findViewById(R.id.but_tag_cancel);
            butCancel.setOnClickListener(new DismissListener(ad));
            Button butAddTag = (Button) laySelectTag.findViewById(R.id.but_tag_addTag);
            butAddTag.setOnClickListener(new AddTagListener(ediTagName));

            String tagName;
            Button butTag;
            DBHelper dbHelper = new DBHelper(WriteActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query("tag", new String[]{"name"}, "type=?", new String[]{accountType + ""}, null, null, null);
            if (cursor.isBeforeFirst()) {
                cursor.moveToNext();
            }
            while (cursor.isAfterLast() == false) {
                tagName = cursor.getString(0);
                butTag = new Button(WriteActivity.this);
                butTag.setText(tagName);
                butTag.setOnClickListener(new DoSelectListener(ad));
                laySelection.addView(butTag);
                cursor.moveToNext();
            }
            cursor.close();
            db.close();
        }

        public void displayNewTag(String tagName) {
            LinearLayout laySelection = (LinearLayout) laySelectTag.findViewById(R.id.lay_tag_selection);
            DBHelper dbHelper = new DBHelper(WriteActivity.this);
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            Cursor cursor = db.query("tag", new String[]{"name"}, "name=?", new String[]{tagName}, null, null, null);
            //the tag does not exist
            if (cursor.getCount() == 0) {
                Log.e("displayNewTag()", "curosr.getCount() == 0");
                return;
            }
            Button butNewTag = new Button(WriteActivity.this);
            butNewTag.setText(tagName);
            butNewTag.setOnClickListener(new DoSelectListener(ad));
            laySelection.addView(butNewTag);
            cursor.close();
            db.close();
        }

        class AddTagListener implements View.OnClickListener {
            EditText ediTagName;

            public AddTagListener(EditText ediTagName) {
                this.ediTagName = ediTagName;
            }

            @Override
            public void onClick(View v) {
                String tagName = ediTagName.getText() + "";
                if (tagName == null || tagName.equals("")) {
                    Toast toast = Toast.makeText(WriteActivity.this, "The tag name cannot be empty.", Toast.LENGTH_SHORT);
                    toast.show();
                    return;
                }
                addTag(tagName);
            }

            private void addTag(String tagName) {
                DBHelper dbHelper = new DBHelper(WriteActivity.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String sql = String.format("INSERT INTO tag(type, name) VALUES(%d, '%s');", accountType, tagName);
                db.execSQL(sql);
                displayNewTag(tagName);
                db.close();
            }
        }

        class DismissListener implements View.OnClickListener {
            private AlertDialog ad;

            public DismissListener(AlertDialog alertDialog) {
                ad = alertDialog;
            }

            @Override
            public void onClick(View v) {
                ad.dismiss();
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
