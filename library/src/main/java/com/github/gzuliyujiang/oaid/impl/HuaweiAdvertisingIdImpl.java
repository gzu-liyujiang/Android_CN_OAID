///*
// * Copyright (c) 2019-2020 gzu-liyujiang <1032694760@qq.com>
// *
// * The software is licensed under the Mulan PSL v1.
// * You can use this software according to the terms and conditions of the Mulan PSL v1.
// * You may obtain a copy of Mulan PSL v1 at:
// *     http://license.coscl.org.cn/MulanPSL
// * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
// * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
// * PURPOSE.
// * See the Mulan PSL v1 for more details.
// *
// */
//package com.github.gzuliyujiang.oaid.impl;
//
//import android.content.Context;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.RestrictTo;
//
//import com.github.gzuliyujiang.logger.Logger;
//import com.github.gzuliyujiang.oaid.IDeviceId;
//import com.github.gzuliyujiang.oaid.IGetter;
//import com.github.gzuliyujiang.oaid.IOAIDGetter;
//import com.huawei.hms.ads.identifier.AdvertisingIdClient;
//
//import java.util.concurrent.Executors;
//
///**
// * Created by liyujiang on 2021/1/7
// *
// * @author 大定府羡民
// */
//@RestrictTo(RestrictTo.Scope.LIBRARY)
//public class HuaweiAdvertisingIdImpl implements IDeviceId {
//    private final Context context;
//
//    public HuaweiAdvertisingIdImpl(Context context) {
//        this.context = context;
//    }
//
//    @Override
//    public boolean supportOAID() {
//        try {
//            Class.forName("com.huawei.hms.ads.identifier.AdvertisingIdClient");
//            return true;
//        } catch (Exception e) {
//            Logger.print(e);
//            return false;
//        }
//    }
//
//    @Override
//    public void doGet(@NonNull final IOAIDGetter getter) {
//        final Handler handler = new Handler(Looper.getMainLooper(), new Handler.Callback() {
//            @Override
//            public boolean handleMessage(@NonNull Message msg) {
//                if (msg.what == 1) {
//                    getter.onOAIDGetComplete(msg.obj.toString());
//                } else {
//                    getter.onOAIDGetError((Exception) msg.obj);
//                }
//                return true;
//            }
//        });
//        Executors.newSingleThreadExecutor().execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(context.getApplicationContext());
//                    if (info == null || info.getId() == null) {
//                        throw new RuntimeException("Huawei AdvertisingIdClient.Info get failed");
//                    }
//                    handler.obtainMessage(1, info.getId()).sendToTarget();
//                } catch (Exception e) {
//                    Logger.print(e);
//                    handler.obtainMessage(-1, e).sendToTarget();
//                }
//            }
//        });
//    }
//
//    @SuppressWarnings("deprecation")
//    @Override
//    public void doGet(@NonNull final IGetter getter) {
//        doGet(new IOAIDGetter() {
//            @Override
//            public void onOAIDGetComplete(@NonNull String oaid) {
//                getter.onDeviceIdGetComplete(oaid);
//            }
//
//            @Override
//            public void onOAIDGetError(@NonNull Exception exception) {
//                getter.onDeviceIdGetError(exception);
//            }
//        });
//    }
//
//}
