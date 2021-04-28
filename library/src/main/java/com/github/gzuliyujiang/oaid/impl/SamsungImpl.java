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

import java.lang.reflect.Method;

import repeackage.com.samsung.android.deviceidservice.IDeviceIdService;

/**
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
class SamsungImpl implements IOAID {
    private final Context context;

    public SamsungImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.samsung.android.deviceidservice", 0);
            return pi != null;
        } catch (Throwable e) {
            OAIDLog.print(e);
            return false;
        }
    }

    @Override
    public void doGet(@NonNull final IGetter getter) {
        Intent intent = new Intent();
        intent.setClassName("com.samsung.android.deviceidservice", "com.samsung.android.deviceidservice.DeviceIdService");
        try {
            boolean isBinded = context.bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    OAIDLog.print("Samsung DeviceIdService connected");
                    try {
                        //IDeviceIdService anInterface = new IDeviceIdService.Stub.asInterface(service);
                        Method asInterface = IDeviceIdService.Stub.class.getDeclaredMethod("asInterface", IBinder.class);
                        IDeviceIdService anInterface = (IDeviceIdService) asInterface.invoke(null, service);
                        if (anInterface == null) {
                            throw new RuntimeException("IDeviceIdService is null");
                        }
                        String deviceId = anInterface.getID();
                        if (deviceId == null || deviceId.length() == 0) {
                            throw new RuntimeException("Samsung DeviceId get failed");
                        }
                        getter.onOAIDGetComplete(deviceId);
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
                    OAIDLog.print("Samsung DeviceIdService disconnected");
                }
            }, Context.BIND_AUTO_CREATE);
            if (!isBinded) {
                throw new RuntimeException("Samsung DeviceIdService bind failed");
            }
        } catch (Throwable e) {
            getter.onOAIDGetError(e);
        }
    }

}
