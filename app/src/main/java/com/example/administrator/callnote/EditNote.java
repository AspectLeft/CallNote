package com.example.administrator.callnote;

import android.app.ListActivity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by yuan on 2016/9/26.
 */

public class EditNote extends ListActivity {
    public static final String EXTRA_MEMO_ID = "memoId";
    public static final String EXTRA_MEMO_NAME = "memoName";
    public static final String EXTRA_MEMO_CONTENT = "memoContent";
    TextWatcher isNull = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if (i2 > 0) {
                findViewById(R.id.add).setClickable(true);
            }
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

        }


    };
    private int memoId = -1;
    private EditText mName;
    private EditText mContent;
    //    private MediaAdapter adapter;
    private DbHelper db;
    private SQLiteDatabase dbRead, dbWrite;
    private String currentPath = null;
    private View.OnClickListener btnClick = new View.OnClickListener() {

        Intent i;
        File f;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.back:// 保存按钮
                    // 保存多媒体信息和笔记信息到数据库，然后关闭当前页面，返回到笔记列表页面/主页面
                    saveMemo();
                    setResult(RESULT_OK);
                    finish();
                    break;
                case R.id.add://新建按钮
                    //保存多媒体信息和笔记信息到数据库，新建一个mian页面，并跳转到mian页面（若edittexet无信息则不可点击）
                    saveMemo();
                    i = new Intent(EditNote.this, EditNote.class);
                    setResult(RESULT_OK);
                    startActivity(i);
                    finish();
                    break;
                default:
                    break;
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        db = new DbHelper(this,"memos",null,1);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();

        mName = (EditText) findViewById(R.id.edittext);
        mContent = (EditText) findViewById(R.id.edit_content);

        // 获取Activity传递过来的noteId
        memoId = getIntent().getIntExtra(EXTRA_MEMO_ID, -1);

        if (memoId > -1) {
            mName.setText(getIntent().getStringExtra(EXTRA_MEMO_NAME));
            mContent.setText(getIntent().getStringExtra(EXTRA_MEMO_CONTENT));

        }
        findViewById(R.id.back).setOnClickListener(btnClick);
        findViewById(R.id.add).setOnClickListener(btnClick);


        mName.addTextChangedListener(isNull);
        findViewById(R.id.add).setClickable(false);
    }

    public int saveMemo() {

        ContentValues cv = new ContentValues();
        if (!mName.getText().toString().equals("")) {
            cv.put(DbHelper.COLUMN_NAME_MEMO_NAME, mName.getText().toString());
            cv.put(DbHelper.COLUMN_NAME_MEMO_CONTENT, mContent.getText().toString());
            cv.put(DbHelper.COLUMN_NAME_MEMO_DATE, new SimpleDateFormat(
                    "yyyy-MM-dd hh:mm:ss").format(new Date()));
        }
        if (memoId > -1) {
            dbWrite.update(DbHelper.TABLE_NAME_MEMOS, cv, DbHelper.COLUMN_NAME_ID
                    + "=?", new String[]{memoId + ""});
            return memoId;
        } else {
            return (int) dbWrite.insert(DbHelper.TABLE_NAME_MEMOS, null, cv);
        }
    }

    protected void onDestroy() {
        dbRead.close();
        dbWrite.close();
        super.onDestroy();
    }

    public void showString(String sth) {
        Toast.makeText(getBaseContext(), sth, Toast.LENGTH_SHORT).show();
    }
}