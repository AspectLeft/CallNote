package com.example.administrator.callnote;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.app.NotificationCompat;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.example.administrator.callnote.SelectKeys.MyHandler;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/29.
 */
public class MSCService extends Service {
    SpeechRecognizer sr;
    private int mNotificationId = 0;
    TelephonyManager telephonyManager;
    PhoneStateListener listener;
    private final static String TAG = "Secretary";

    private ArrayList<String> result_cache = new ArrayList<String>();

    public IBinder onBind(Intent intent) {return null;}

    MyApp mApp = null;

     MyHandler mHandler = null;

    private static final int CHANGED = 0x0010;
    Intent resultIntent;
    PendingIntent resultPendingIntent;

    public void onCreate()
    {
        super.onCreate();
        MainActivity.service_state = true;



        telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        listener = new PhoneStateListener(){
            @Override
            public void onCallStateChanged(int state, String incomingnumber)
            {

                switch (state)
                {
                    case TelephonyManager.CALL_STATE_IDLE:

                        if (sr != null)
                        {
                            sr.destroy();
                            Toast.makeText(MSCService.this, "CallNote: stop listening", Toast.LENGTH_SHORT).show();
                        }

                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:

                        result_cache.clear();
                        NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(MSCService.this)
                                        .setSmallIcon(R.mipmap.pen2)
                                        .setContentTitle("CallNote")
                                        .setContentText("Click to select keys.")
                                        .setOngoing(true);
                        Intent resultIntent = new Intent(MSCService.this, SelectKeys.class);
                        resultIntent.putStringArrayListExtra("cache", result_cache);

                        PendingIntent resultPendingIntent = PendingIntent.getActivity(MSCService.this,
                                0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);
                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(0, mBuilder.build());
                        Toast.makeText(MSCService.this, "CallNote: start listening", Toast.LENGTH_SHORT).show();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                sr = SpeechRecognizer.createRecognizer(MSCService.this, null);
                                sr.setParameter(SpeechConstant.DOMAIN, "iat");
                                sr.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                                sr.setParameter(SpeechConstant.ACCENT, "mandarin");
                                //Log.d(TAG, sr.getParameter(SpeechConstant.KEY_SPEECH_TIMEOUT));
                                sr.startListening(mRecoListener);
                            }
                        }).start();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:


                        break;
                }
            }
        };
        telephonyManager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(MSCService.this)
                        .setSmallIcon(R.mipmap.pen2)
                        .setContentTitle("CallNote")
                        .setContentText("Click to select keys.")
                        .setOngoing(true);


        //Uri uri = Uri.parse("https://www.bing.com/search?q=android+develop");
        //Intent resultIntent = new Intent(Intent.ACTION_VIEW, uri);
        resultIntent = new Intent(MSCService.this, SelectKeys.class);
        //resultIntent.putExtra("result","getinfo?");


        resultPendingIntent = PendingIntent.getActivity(MSCService.this,
                0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, mBuilder.build());




    }



    public void onDestroy()
    {
        super.onDestroy();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        notificationManager.cancel(0);
        if (sr != null)
            sr.destroy();
    }

    private RecognizerListener mRecoListener = new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            //Toast.makeText(MSCService.this, "onResult", Toast.LENGTH_SHORT).show();
            //textView.setText(recognizerResult.getResultString());
            String result = recognizerResult.getResultString();




            //Uri uri = Uri.parse("https://www.bing.com/search?q=android+develop");
            //Intent resultIntent = new Intent(Intent.ACTION_VIEW, uri);


          // Intent resultIntent = new Intent(MSCService.this, SelectKeys.class);
          //  resultIntent.putExtra("result", "can I get info?");

            mApp = (MyApp) getApplication();
            //  获得该共享变量实例
            mHandler = mApp.getHandler();

            if(mHandler!=null) {
                Message tempMsg = new Message();
                tempMsg.obj = result;
                mHandler.sendMessage(tempMsg);
            }
                result_cache.add(result);

                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(MSCService.this)
                                .setSmallIcon(R.mipmap.pen2)
                                .setContentTitle("CallNote")
                                .setContentText("Click to select keys.")
                                .setOngoing(true);
                Intent resultIntent = new Intent(MSCService.this, SelectKeys.class);
                resultIntent.putStringArrayListExtra("cache", result_cache);

                PendingIntent resultPendingIntent = PendingIntent.getActivity(MSCService.this,
                        0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, mBuilder.build());




            try {
                JSONObject jsonObject = new JSONObject(result);
                int sn = jsonObject.getInt("sn");
                boolean ls = jsonObject.getBoolean("ls");
/*                 JSONArray ws = jsonObject.getJSONArray("ws");
                for (int i = 0; i < ws.length(); ++i)
                {
                    JSONObject word = ws.getJSONObject(i);
                    JSONArray cw = word.getJSONArray("cw");
                    for (int j = 0; j < cw.length(); ++j)
                    {
                        //textView.setText(textView.getText() + "|" + cw.getJSONObject(j).getString("w"));
                       NotificationCompat.Builder mBuilder =
                                new NotificationCompat.Builder(MSCService.this)
                                        .setSmallIcon(R.mipmap.ic_launcher)
                                        .setContentTitle("MSCService")
                                        .setContentText(cw.getJSONObject(j).getString("w"));


                        Uri uri = Uri.parse("https://www.bing.com/search?q=" + cw.getJSONObject(j).getString("w"));
                        Intent resultIntent = new Intent(Intent.ACTION_VIEW, uri);
                        //Intent resultIntent = new Intent(MSCService.this, NotificationActivity.class);

                        PendingIntent resultPendingIntent = PendingIntent.getActivity(MSCService.this,
                                0, resultIntent, PendingIntent.FLAG_CANCEL_CURRENT);
                        mBuilder.setContentIntent(resultPendingIntent);

                        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                        notificationManager.notify(++mNotificationId, mBuilder.build());

                    }
                }*/
                if (ls)
                {
                    sr.destroy();
                    sr = SpeechRecognizer.createRecognizer(MSCService.this, null);
                    sr.setParameter(SpeechConstant.DOMAIN, "iat");
                    sr.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                    sr.setParameter(SpeechConstant.ACCENT, "mandarin");
                    sr.startListening(mRecoListener);

                  //  Toast.makeText(MSCService.this, "start listening", Toast.LENGTH_SHORT).show();

                }

            }catch (Exception e)
            {
                e.printStackTrace();
            }

        }

        @Override
        public void onError(SpeechError speechError) {
            Log.d("Listener", "error " + speechError.getPlainDescription(true));
            if (speechError.getErrorCode() == 10118)
            {
                sr.destroy();
                sr = SpeechRecognizer.createRecognizer(MSCService.this, null);
                sr.setParameter(SpeechConstant.DOMAIN, "iat");
                sr.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
                sr.setParameter(SpeechConstant.ACCENT, "mandarin");
                sr.startListening(mRecoListener);

           //     Toast.makeText(MSCService.this, "start listening", Toast.LENGTH_SHORT).show();
            }


        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };


}
