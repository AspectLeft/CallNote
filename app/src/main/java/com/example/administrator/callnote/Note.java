package com.example.administrator.callnote;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

/**
 * Created by Administrator on 2016/11/17.
 */

public class Note extends ListFragment {
    private SimpleCursorAdapter adapter = null;
    private DbHelper db;
    private SQLiteDatabase dbRead,dbWrite;
    public static final int REQUEST_CODE_ADD_NOTE = 1;
    public static final int REQUEST_CODE_EDIT_NOTE = 2;



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // 操作数据库
        db = new DbHelper(getActivity(),"memos",null,1);
        dbRead = db.getReadableDatabase();
        dbWrite=db.getWritableDatabase();


        // 查询数据库并将数据显示在ListView上。
        // 建议使用CursorLoader，这个操作因为在UI线程，容易引起无响应错误
        adapter = new SimpleCursorAdapter(getActivity(), R.layout.note_item, null,
                new String[] { DbHelper.COLUMN_NAME_MEMO_NAME,
                        DbHelper.COLUMN_NAME_MEMO_CONTENT }, new int[] {
                R.id.mMemo, R.id.mContent});
        setListAdapter(adapter);

        refreshMemosListView();

    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_note, container, false);


        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick( final AdapterView<?> adapterView, View view,   int i, long l) {
                Cursor c=adapter.getCursor();
                c.moveToPosition(i);
                final String delename= c.getString(c.getColumnIndex(DbHelper.COLUMN_NAME_MEMO_NAME));

                AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
                builder.setMessage("确定删除此备忘录"+delename+"?");
                builder.setTitle("提示");


                //添加AlertDialog.Builder对象的setPositiveButton()方法
                builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dbWrite.delete(DbHelper.TABLE_NAME_MEMOS,
                                DbHelper.COLUMN_NAME_MEMO_NAME+"=?",new String[]{delename});
                        refreshMemosListView();

                    }
                });

                //添加AlertDialog.Builder对象的setNegativeButton()方法
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

                builder.create().show();
                return true;
            }
        });
    }

    /**
     * 复写方法，笔记列表中的笔记条目被点击时被调用，打开编辑笔记页面，同事传入当前笔记的信息
     */
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        // 获取当前备忘录条目的Cursor对象
        Cursor c = adapter.getCursor();
        c.moveToPosition(position);

        // 显式Intent开启编辑备忘录页面
        Intent i = new Intent(getActivity(), EditNote.class);

        // 传入备忘录id，name，content
        i.putExtra(EditNote.EXTRA_MEMO_ID,
                c.getInt(c.getColumnIndex(DbHelper.COLUMN_NAME_ID)));
        i.putExtra(EditNote.EXTRA_MEMO_NAME,
                c.getString(c.getColumnIndex(DbHelper.COLUMN_NAME_MEMO_NAME)));
        i.putExtra(EditNote.EXTRA_MEMO_CONTENT,
                c.getString(c.getColumnIndex(DbHelper.COLUMN_NAME_MEMO_CONTENT)));

        // 有返回的开启Activity
        startActivityForResult(i, REQUEST_CODE_EDIT_NOTE);

        super.onListItemClick(l, v, position, id);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case REQUEST_CODE_ADD_NOTE:
            case REQUEST_CODE_EDIT_NOTE:
                if (resultCode == Activity.RESULT_OK) {
                    refreshMemosListView();
                }
                break;

            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    public void refreshMemosListView() {
        /**
         * Change the underlying cursor to a new cursor. If there is an existing
         * cursor it will be closed.
         *
         * Parameters: cursor The new cursor to be used
         */
        adapter.changeCursor(dbRead.query(DbHelper.TABLE_NAME_MEMOS, null, null,
                null, null, null, null));

    }
}
