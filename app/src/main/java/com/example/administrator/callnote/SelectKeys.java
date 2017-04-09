package com.example.administrator.callnote;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import net.sf.classifier4J.summariser.ISummariser;
import net.sf.classifier4J.summariser.SimpleSummariser;

import org.apache.http.util.EncodingUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2016/9/8.
 */




public class SelectKeys extends Activity {

    int CNT=0;
    private MyHandler handler = null;

    private MyApp mApp = null;

    private List<Keyword> keywordList = new ArrayList<Keyword>();
    private KeywordAdapter adapter;

    private TextView tv=null;
    private TextView tv2=null;
    private String TotSentence;

    private String KeySentence;
    private String TotWord;
    private String KeyWord;
    private Context mContext = null;

    private DbHelper db,db2;
    private SQLiteDatabase dbRead, dbWrite,dbsetRead;

    private String DefaultWord;
    private String FrequentWord;
    ISummariser summariser = new SimpleSummariser();
    private String SetWord;
   private String tempSet;
    private ArrayList<String> result_cache = new ArrayList<String>();
    private String SenseWord;

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_selectkeys);
        tempSet="";
        db = new DbHelper(this,"memos",null,1);
        db2= new DbHelper(this,"SETWORD",null,1);
        Log.d("SelectKeys", "now begin dbread");
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();
        Log.d("SelectKeys", "now begin dbsetRead");
        dbsetRead = db2.getReadableDatabase();
        SetWord="";

       // StuDBHelper dbHelper_set = new StuDBHelper(SelectKeys.this,"stu_db",null,1);
        //得到一个可读的SQLiteDatabase对象

       // SQLiteDatabase dbsetWrite =dbHelper_set.getWritableDatabase();
       // SQLiteDatabase dbsetRead =dbHelper_set.getReadableDatabase();
        Log.d("SelectKeys", "now begin cursor");
        final Cursor cursor = dbsetRead.query("SETWORD", null, null, null, null, null, null);
        Log.d("SelectKeys", "now begin while");
        while(cursor.moveToNext()){
            Log.d("SelectKeys", "now begin set_word");
            String set_word = cursor.getString(cursor.getColumnIndex("word"));
            Log.d("SelectKeys", "now begin name");
            String name = cursor.getString(cursor.getColumnIndex("title"));
            Log.d("SelectKeys", "now begin SetWord");
         //   Toast.makeText(SelectKeys.this, name, Toast.LENGTH_SHORT).show();
            SetWord=SetWord+set_word;
            Log.d("SelectKeys", "new tiltle is " + name);
            Log.d("SelectKeys", "new word is " + set_word);
        }

        // Toast.makeText(SelectKeys.this, SetWord, Toast.LENGTH_SHORT).show();
        //关闭数据库
        dbsetRead.close();

        SetWord=SetWord.replace(" ","\n");
     //   SetWord=SetWord.replace("\n"," ");

        mContext = this;
        TotSentence="";
        TotWord="";

        //tv.setText(SetWord+"..");

        try {
            InputStream in = getResources().getAssets().open("stopword2.txt");
            int length = in.available();
            byte[] buffer = new byte[length];
            in.read(buffer);
            DefaultWord = EncodingUtils.getString(buffer, "GBK");
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            InputStream in2 = getResources().getAssets().open("senseword.txt");
            int length = in2.available();
            byte[] buffer = new byte[length];
            in2.read(buffer);
            SenseWord = EncodingUtils.getString(buffer, "GBK");
        } catch (Exception e) {
            e.printStackTrace();
        }
        SenseWord=SenseWord.replace("\r\n"," ");


        Log.d("SelectKeys", "now begin result_cache");


        Intent intent = getIntent();
        result_cache = intent.getStringArrayListExtra("cache");
        if (result_cache != null && !result_cache.isEmpty()) {
            for (String result : result_cache) {
                initKeywords(result);
            }
        }
        Log.d("SelectKeys", "now begin adapter");
        adapter = new KeywordAdapter(SelectKeys.this, R.layout.keyword_item, keywordList);

        mApp = (MyApp) getApplication();
        handler = new MyHandler();
        mApp.setHandler(handler);
        //initKeywords();

        Log.d("SelectKeys", "now begin listView");

        ListView listView = (ListView) findViewById(R.id.list_view);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                final Keyword keyword = keywordList.get(i);
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SelectKeys.this);
                alertDialog.setTitle(keyword.getName());
                if (keyword.getName().equals("微信"))
                {
                    alertDialog.setItems(new String[]{"Search web", "Add to note", "Open note", "Launch WeChat"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0://search
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("https://www.bing.com/search?q=" + keyword.getName())));
                                    break;
                                case 1://add to note

                                    ContentValues cv = new ContentValues();
                                    if (!keyword.getName().equals("")) {
                                        cv.put(DbHelper.COLUMN_NAME_MEMO_NAME, keyword.getName());
                                        cv.put(DbHelper.COLUMN_NAME_MEMO_CONTENT, keyword.getName());
                                        cv.put(DbHelper.COLUMN_NAME_MEMO_DATE, new SimpleDateFormat(
                                                "yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    }
                                    dbWrite.insert(DbHelper.TABLE_NAME_MEMOS, null, cv);
                                    break;
                                case 2:
                                    Cursor cursor1 = dbRead.rawQuery("select * from " + DbHelper.TABLE_NAME_MEMOS + " where " + DbHelper.COLUMN_NAME_MEMO_NAME + "=?", new String[]{keyword.getName()});
                                    if (cursor1.moveToFirst())
                                    {
                                        AlertDialog.Builder quick_view = new AlertDialog.Builder(SelectKeys.this);
                                        quick_view.setTitle(cursor1.getString(cursor1.getColumnIndex(DbHelper.COLUMN_NAME_MEMO_NAME)))
                                                .setMessage(cursor1.getString(cursor1.getColumnIndex(DbHelper.COLUMN_NAME_MEMO_CONTENT)))
                                                .show();

                                    }
                                    else
                                    {
                                        Toast.makeText(SelectKeys.this, "Note not found", Toast.LENGTH_SHORT).show();
                                    }
                                    cursor1.close();
                                    break;
                                case 3://launch wechat
                                    Intent launchWeChat = getPackageManager().getLaunchIntentForPackage("com.tencent.mm");
                                    startActivity(launchWeChat);
                                    break;

                            }
                        }
                    }).show();
                }
                else {
                    alertDialog.setItems(new String[]{"Search web", "Add to note", "Open note"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            switch (i) {
                                case 0://search
                                    startActivity(new Intent(Intent.ACTION_VIEW,
                                            Uri.parse("https://www.bing.com/search?q=" + keyword.getName())));
                                    break;
                                case 1://add to note

                                    ContentValues cv = new ContentValues();
                                    if (!keyword.getName().equals("")) {
                                        cv.put(DbHelper.COLUMN_NAME_MEMO_NAME, keyword.getName());
                                        cv.put(DbHelper.COLUMN_NAME_MEMO_CONTENT, keyword.getName());
                                        cv.put(DbHelper.COLUMN_NAME_MEMO_DATE, new SimpleDateFormat(
                                                "yyyy-MM-dd hh:mm:ss").format(new Date()));
                                    }
                                    dbWrite.insert(DbHelper.TABLE_NAME_MEMOS, null, cv);
                                    break;
                                case 2:
                                    Cursor cursor1 = dbRead.rawQuery("select * from " + DbHelper.TABLE_NAME_MEMOS + " where " + DbHelper.COLUMN_NAME_MEMO_NAME + "=?", new String[]{keyword.getName()});
                                    if (cursor1.moveToFirst())
                                    {
                                        AlertDialog.Builder quick_view = new AlertDialog.Builder(SelectKeys.this);
                                        quick_view.setTitle(cursor1.getString(cursor1.getColumnIndex(DbHelper.COLUMN_NAME_MEMO_NAME)))
                                                .setMessage(cursor1.getString(cursor1.getColumnIndex(DbHelper.COLUMN_NAME_MEMO_CONTENT)))
                                                .show();

                                    }
                                    else
                                    {
                                        Toast.makeText(SelectKeys.this, "Note not found", Toast.LENGTH_SHORT).show();
                                    }
                                    cursor1.close();
                                    break;


                            }
                        }
                    }).show();
                }
            }
        });
      //  Toast.makeText(SelectKeys.this,"oncreate", Toast.LENGTH_SHORT).show();
    }


    private  void initKeywords(String result)//每次收到service传递过来的参数后用initKeywords函数进行提取关键词和显示
    {
        boolean WordFlag;
        CharSequence temp="";
        String tempp="";
      //  Intent intent = getIntent();
      //  String result = intent.getStringExtra("result");
        KeyWord="";
        try {
            JSONObject jsonObject = new JSONObject(result);
            int sn = jsonObject.getInt("sn");
            boolean ls = jsonObject.getBoolean("ls");
            JSONArray ws = jsonObject.getJSONArray("ws");
            for (int i = 0; i < ws.length(); ++i)
            {
                JSONObject word = ws.getJSONObject(i);
                JSONArray cw = word.getJSONArray("cw");
                for (int j = 0; j < cw.length(); ++j)
                {
                    temp=cw.getJSONObject(j).getString("w");
                    tempp=temp.toString();

                    tempp=tempp.replace("。","");
                    tempp=tempp.replace("？","");
                    tempp=tempp.replace("！","");
                    tempp=tempp.replace("，","");
                    tempp=tempp.replace(" ","");


                    if(DefaultWord.indexOf(tempp,0)>=0)
                    WordFlag=false;
                    else {
                        WordFlag=true;
                        TotWord=TotWord+tempp+". ";
                        CNT++;
                    }

                    int NUMCNT=0;
                    for(int k=1;k<tempp.length();k++)
                        if(JudgeNum(tempp.charAt(k)))
                            NUMCNT++;


                    if(tempp!=""&&tempp!=" ")
                    if((NUMCNT>=2)&&tempSet.indexOf(tempp,0)<0)
                    {
                        tempSet = tempSet + tempp+ ". " ;
                    }

                    TotSentence=TotSentence+tempp;
                    TotSentence=TotSentence.replace('。','.');
                    TotSentence=TotSentence.replace('？','.');
                    TotSentence=TotSentence.replace('！','.');
                    TotSentence=TotSentence.replace('，','.');




                }
            }


        }catch (Exception e)
        {
            e.printStackTrace();
        }

        if(TotWord.length()>=1)
            for(int k=1;k<TotWord.length();k++)
                if(TotWord.charAt(k)=='.'&&TotWord.charAt((k-1))=='.')
                    TotWord=TotWord.substring(0, k)+TotWord.substring(k+1);

        if(CNT>=5)
       if(summariser.summarise(TotWord,5)!=null)
        KeyWord=KeyWord+summariser.summarise(TotWord,5);

        int start=0;
        int count=0;
        int realstart=0;

        while (SetWord.indexOf("\n", start) >= 0 && start < SetWord.length()) {
            start = SetWord.indexOf("\n", start) ;
            if(start>realstart&&SetWord.substring(realstart,start)!=" "&&SetWord.substring(realstart,start)!="") {
                String tempString=SetWord.substring(realstart,start);
                if(TotSentence.indexOf(tempString)>=0&&tempSet.indexOf(tempString)<0)
                    tempSet=tempSet+tempString+". ";
                count++;
            }
            realstart =start+1;
            start=start+1;

        }

        start=0;
        realstart=0;
       while (SenseWord.indexOf(" ", start) >= 0 && start < SenseWord.length()) {
            start = SenseWord.indexOf(" ", start) ;
            if(start>realstart&&SenseWord.substring(realstart,start)!=" "&&SenseWord.substring(realstart,start)!="") {
                String tempString=SenseWord.substring(realstart,start);
                if(TotSentence.indexOf(tempString)>=0&&tempSet.indexOf(tempString)<0)
                    tempSet=tempSet+tempString+". ";
                count++;
            }
            realstart =start+1;
            start=start+1;

        }



        for(int i=0;i<TotSentence.length();i++)
        {
            if(TotSentence.charAt(i)=='月'){
                int j=i;
                while(j-1>=0&&JudgeNum(TotSentence.charAt(j-1)))j--;
                int k=i;
                while(k+1<TotSentence.length()&&(JudgeNum(TotSentence.charAt(k+1))||TotSentence.charAt(k+1)=='日'||TotSentence.charAt(k+1)=='号'))k++;
                if(k-j>=1&&tempSet.indexOf(TotSentence.substring(j,k+1))<0)
                {
                    tempSet=tempSet+TotSentence.substring(j,k+1)+". ";
                    Toast.makeText(SelectKeys.this, TotSentence.substring(j,k+1), Toast.LENGTH_SHORT).show();
                }

            }

            if(TotSentence.charAt(i)=='点'){
                int j=i;
                while(j-1>=0&&JudgeNum(TotSentence.charAt(j-1)))j--;
                int k=i;
                while(k+1<TotSentence.length()&&(JudgeNum(TotSentence.charAt(k+1))||TotSentence.charAt(k+1)=='分'||TotSentence.charAt(k+1)=='半'))k++;

                if(k-j>=1&&tempSet.indexOf(TotSentence.substring(j,k+1))<0&&j!=i)
                {
                    tempSet=tempSet+TotSentence.substring(j,k+1)+". ";
                }

            }
        }



        if(tempSet!=""&&tempSet!=" ") {
                   KeyWord = KeyWord + tempSet;
        }

    if(KeyWord.length()>=1)
        if(KeyWord.charAt(0)=='.')
            KeyWord=KeyWord.substring(1);//减去多余的.

        for(int k=1;k<KeyWord.length();k++)
            if(KeyWord.charAt(k)=='.'&&KeyWord.charAt((k-1))=='.')
                KeyWord=KeyWord.substring(0, k)+KeyWord.substring(k+1);//减去多余的.


       // KeyWord=TotWord;
        String[] strArray = new String[50];
         start=0;
         count=0;
         realstart=0;

        while (KeyWord.indexOf(".", start) >= 0 && start < KeyWord.length()) {
            start = KeyWord.indexOf(".", start) ;
            if(KeyWord.substring(realstart,start)!=" "&&KeyWord.substring(realstart,start)!="") {
                strArray[count] = KeyWord.substring(realstart, start);
                strArray[count] = strArray[count].replace(" ","");
                count++;
            }
            realstart =start+1;
            start=start+1;

        }

        keywordList.clear();
        for(int i=0;i<count;i++)
            keywordList.add(new Keyword(strArray[i], R.mipmap.label));


//TotSentence是所有通话的语音识别内容，KeySentence是提取关键词后的语音识别结果,count是识别了多少个关键词
       //tv.setText(TotSentence);
       // tv2.setText(KeyWord);
    }

//不同activity之间的参数传递
    final class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

          //  Toast.makeText(SelectKeys.this, "Myhandler", Toast.LENGTH_SHORT).show();

           // tv.setText(Integer.toString(CNT));
            String result=(String)msg.obj;
            initKeywords(result);
            adapter.notifyDataSetChanged();

            }
        }
    boolean JudgeNum(char S){
        if(S>='0'&&S<='9')
            return true;
        if(S=='一'||S=='二'||S=='三'||S=='四'||S=='五'||S=='六'||S=='七'||S=='八'||S=='九'||S=='十')
            return true;
        return false;
    }
    public void onDestroy()
    {
        super.onDestroy();
        dbRead.close();
        dbWrite.close();

    }

}
