package com.example.accountbook.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    private static final String databaseName = "testDB";
    private static final int TYPE_EXP = 1;
    private static final int TYPE_INCOME = 2;

    /**
     * Create a helper object to create, open, and/or manage a database.
     * This method always returns very quickly.  The database is not actually
     * created or opened until one of {@link #getWritableDatabase} or
     * {@link #getReadableDatabase} is called.
     *
     * @param context to use for locating paths to the the database
     * @param name    of the database file, or null for an in-memory database
     * @param factory to use for creating cursor objects, or null for the default
     * @param version number of the database (starting at 1); if the database is older,
     *                {@link #onUpgrade} will be used to upgrade the database; if the database is
     *                newer, {@link #onDowngrade} will be used to downgrade the database
     */
    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context c) {
        super(c, databaseName, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlCreateAccount = "CREATE TABLE IF NOT EXISTS account (" +
                "id INTEGER primary key, " +
                "type INTEGER not null," +
                "tag text not null," +
                "detail text," +
                "date text not null," +
                "amount real not null" +
                ");";
        db.execSQL(sqlCreateAccount);

        String sqlCreateTag = "CREATE TABLE IF NOT EXISTS tag (" +
                "id INTEGER primary key, " +
                "type INTEGER not null," +
                "name text not null" +
                ");";
        db.execSQL(sqlCreateTag);

        String[] expTags = {"交通", "食物", "学费", "书费"};
        String[] incomeTags = {"工资", "奖金", "红包", "二手市场"};
        String sql;
        for (String tagName : expTags) {
            sql = getSqlOfAddTag(TYPE_EXP, tagName);
            db.execSQL(sql);
        }
        for (String tagName : incomeTags) {
            sql = getSqlOfAddTag(TYPE_INCOME, tagName);
            db.execSQL(sql);
        }
    }

    private String getSqlOfAddTag(int type, String name) {
        String sql = String.format("INSERT INTO tag(type, name) VALUES(%d, '%s');", type, name);
        return sql;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
