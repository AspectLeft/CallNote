package com.example.administrator.callnote;

import android.app.Activity;
import android.app.Fragment;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.util.EncodingUtils;

import java.io.InputStream;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by wb199 on 2016/9/27.
 */

public class SetWord extends Fragment {

    CheckBox programmer=null;
    CheckBox finance=null;
    CheckBox math=null;
    EditText myEdit;
    private DbHelper dbHelper ;
    private SharedPreferences pref;
    SharedPreferences.Editor editor;
    boolean isMath;
    boolean isProgrammer;
    boolean isFinance;
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        isMath = pref.getBoolean("math", false);
        isProgrammer = pref.getBoolean("programmer", false);
        isFinance = pref.getBoolean("finance", false);
    }

    public void insertOnClick()
    {
        String tempS;
        tempS=myEdit.getText().toString();
        dbHelper=new DbHelper(getActivity(),"SETWORD",null,1);

        ContentValues values = new ContentValues();

        values.put("title","selfset");
        values.put("word",tempS);

        SQLiteDatabase dbWrite=dbHelper.getWritableDatabase();
        dbWrite.insert("SETWORD",null,values);
        Toast.makeText(getActivity(),"Set",Toast.LENGTH_SHORT).show();

    }
    public void deleteOnClick()
    {
        dbHelper=new DbHelper(getActivity(),"SETWORD",null,1);

        SQLiteDatabase dbWrite=dbHelper.getWritableDatabase();
        dbWrite.delete("SETWORD", "title = ?", new String[] { "selfset" });
        Toast.makeText(getActivity(),"Clear",Toast.LENGTH_SHORT).show();
        myEdit.setText("");
    }
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_setword, container, false);
        myEdit = (EditText)view.findViewById(R.id.edit_set);
        myEdit.setText("");
        programmer=(CheckBox)view.findViewById(R.id.programmer);
        finance=(CheckBox)view.findViewById(R.id.finance);
        math=(CheckBox)view.findViewById(R.id.math);

        programmer.setChecked(isProgrammer);
        finance.setChecked(isFinance);
        math.setChecked(isMath);

        dbHelper=new DbHelper(getActivity(),"SETWORD",null,1);

        programmer.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                editor=pref.edit();
                if(isChecked){
                    Toast.makeText(getActivity(),buttonView.getText()+" selected",Toast.LENGTH_SHORT).show();

                    editor.putBoolean("programmer",true);
                    String Setword="";
                    try {
                        InputStream in2 = getResources().getAssets().open("programmer1.txt");
                        int length = in2.available();
                        byte[] buffer = new byte[length];
                        in2.read(buffer);
                        Setword = EncodingUtils.getString(buffer, "GBK");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ContentValues values = new ContentValues();

                    values.put("title","programmer");
                    values.put("word",Setword);

                    SQLiteDatabase dbWrite=dbHelper.getWritableDatabase();
                    dbWrite.insert("SETWORD",null,values);

                }else{
                    editor.putBoolean("programmer",false);
                    SQLiteDatabase dbWrite=dbHelper.getWritableDatabase();
                    dbWrite.delete("SETWORD", "title = ?", new String[] { "programmer" });
                    Toast.makeText(getActivity(),buttonView.getText()+" not selected",Toast.LENGTH_SHORT).show();
                }
                editor.commit();
            }
        });
        finance.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                editor=pref.edit();
                if(isChecked){
                    editor.putBoolean("finance",true);
                    Toast.makeText(getActivity(),buttonView.getText()+" selected",Toast.LENGTH_SHORT).show();
                    String Setword="";
                    try {
                        InputStream in2 = getResources().getAssets().open("finance1.txt");
                        int length = in2.available();
                        byte[] buffer = new byte[length];
                        in2.read(buffer);
                        Setword = EncodingUtils.getString(buffer, "GBK");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ContentValues values = new ContentValues();

                    values.put("title","finance");
                    values.put("word",Setword);

                    SQLiteDatabase dbWrite=dbHelper.getWritableDatabase();
                    dbWrite.insert("SETWORD",null,values);

                }else{
                    editor.putBoolean("finance",false);
                    SQLiteDatabase dbWrite=dbHelper.getWritableDatabase();
                    dbWrite.delete("SETWORD", "title = ?", new String[] { "finance" });
                    Toast.makeText(getActivity(),buttonView.getText()+" not selected",Toast.LENGTH_SHORT).show();
                }
                editor.commit();
            }
        });
        math.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                editor=pref.edit();
                if(isChecked){
                    editor.putBoolean("math",true);
                    Toast.makeText(getActivity(),buttonView.getText()+" selected",Toast.LENGTH_SHORT).show();
                    String Setword="";
                    try {
                        InputStream in2 = getResources().getAssets().open("math1.txt");
                        int length = in2.available();
                        byte[] buffer = new byte[length];
                        in2.read(buffer);
                        Setword = EncodingUtils.getString(buffer, "GBK");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    ContentValues values = new ContentValues();

                    values.put("title","math");
                    values.put("word",Setword);

                    SQLiteDatabase dbWrite=dbHelper.getWritableDatabase();
                    dbWrite.insert("SETWORD",null,values);
                }else{
                    editor.putBoolean("math",false);
                    SQLiteDatabase dbWrite=dbHelper.getWritableDatabase();
                    dbWrite.delete("SETWORD", "title = ?", new String[] { "math" });
                    Toast.makeText(getActivity(),buttonView.getText()+" not selected",Toast.LENGTH_SHORT).show();
                }
                editor.commit();
            }
        });

        return view;
    }


}
