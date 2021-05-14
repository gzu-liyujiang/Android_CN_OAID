/*
 * Copyright (c) 2019-2021 gzu-liyujiang <1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 *
 */

package com.github.gzuliyujiang.oaid.impl;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDLog;

import repeackage.com.google.android.gms.ads.identifier.internal.IAdvertisingIdService;

/**
 * 参阅谷歌官方 Google Play Services SDK。
 * <prev>
 * implementation `com.google.android.gms:play-services-ads:19.4.0`
 * AdvertisingIdClient.getAdvertisingIdInfo(context).getId()
 * </pre>
 *
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2021/5/14 2:37
 */
class GmsImpl implements IOAID {
    private final Context context;

    public GmsImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.android.vending", 0);
            return pi != null;
        } catch (Throwable e) {
            OAIDLog.print(e);
            return false;
        }
    }

    @Override
    public void doGet(@NonNull final IGetter getter) {
        if (!supported()) {
            getter.onOAIDGetError(new RuntimeException("Google Play services not available"));
            return;
        }
        Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
        intent.setPackage("com.google.android.gms");
        try {
            boolean isBinded = context.bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    OAIDLog.print("Google Play Service connected");
                    try {
                        IAdvertisingIdService anInterface = IAdvertisingIdService.Stub.asInterface(service);
                        if (anInterface.isLimitAdTrackingEnabled(true)) {
                            // 实测在系统设置中停用了广告化功能也是能获取到广告标识符的
                            OAIDLog.print("User has disabled advertising identifier");
                        }
                        String id = anInterface.getId();
                        if (id == null || id.length() == 0) {
                            throw new RuntimeException("Android Advertising ID get failed");
                        }
                        getter.onOAIDGetComplete(id);
                    } catch (Throwable e) {
                        OAIDLog.print(e);
                        getter.onOAIDGetError(e);
                    } finally {
                        try {
                            context.unbindService(this);
                        } catch (Throwable e) {
                            OAIDLog.print(e);
                        }
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    OAIDLog.print("Google Play Service disconnected");
                }
            }, Context.BIND_AUTO_CREATE);
            if (!isBinded) {
                throw new RuntimeException("Google Play Service bind failed");
            }
        } catch (Throwable e) {
            getter.onOAIDGetError(e);
        }
    }

}
