package com.github.gzuliyujiang.demo;

import android.app.Application;

import com.github.gzuliyujiang.logger.Logger;

/**
 * Created by liyujiang on 2020/5/20.
 */
public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Logger.ENABLE = BuildConfig.DEBUG;
    }

}
