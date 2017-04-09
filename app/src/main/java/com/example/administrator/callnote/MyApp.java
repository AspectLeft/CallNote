package com.example.administrator.callnote;
import android.app.Application;

import com.example.administrator.callnote.SelectKeys.MyHandler;

/**
 * Created by wb199 on 2016/9/15.
 */
public class MyApp extends Application {
    // 共享变量
    private MyHandler handler = null;

    // set方法
    public void setHandler(MyHandler handler) {
        this.handler = handler;
    }

    // get方法
    public MyHandler getHandler() {
        return handler;
    }
}
