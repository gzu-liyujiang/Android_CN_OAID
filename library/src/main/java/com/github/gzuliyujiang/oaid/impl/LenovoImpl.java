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

import repeackage.com.zui.deviceidservice.IDeviceidInterface;

/**
 * 参阅 com.umeng.umsdk:oaid_lenovo:1.0.0
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
class LenovoImpl implements IOAID {
    private final Context context;

    public LenovoImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.zui.deviceidservice", 0);
            return pi != null;
        } catch (Throwable e) {
            OAIDLog.print(e);
            return false;
        }
    }

    @Override
    public void doGet(@NonNull final IGetter getter) {
        Intent intent = new Intent();
        intent.setClassName("com.zui.deviceidservice", "com.zui.deviceidservice.DeviceidService");
        try {
            boolean isBinded = context.bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    OAIDLog.print("Lenovo DeviceidService connected");
                    try {
                        IDeviceidInterface anInterface = IDeviceidInterface.Stub.asInterface(service);
                        if (anInterface == null) {
                            throw new RuntimeException("IDeviceidInterface is null");
                        }
                        String oaid = anInterface.getOAID();
                        if (oaid == null || oaid.length() == 0) {
                            throw new RuntimeException("Lenovo OAID get failed");
                        }
                        getter.onOAIDGetComplete(oaid);
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
                    OAIDLog.print("Lenovo DeviceidService disconnected");
                }
            }, Context.BIND_AUTO_CREATE);
            if (!isBinded) {
                getter.onOAIDGetError(new RuntimeException("Lenovo DeviceidService bind failed"));
            }
        } catch (Throwable e) {
            getter.onOAIDGetError(e);
        }
    }

}
