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
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAIDGetter;
import com.samsung.android.deviceidservice.IDeviceIdService;

import java.lang.reflect.Method;

/**
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民（1032694760@qq.com）
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class SamsungDeviceIdImpl implements IDeviceId {
    private final Context context;

    public SamsungDeviceIdImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supportOAID() {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.samsung.android.deviceidservice", 0);
            return pi != null;
        } catch (Exception e) {
            Logger.print(e);
            return false;
        }
    }

    @Override
    public void doGet(@NonNull final IOAIDGetter getter) {
        Intent intent = new Intent();
        intent.setClassName("com.samsung.android.deviceidservice", "com.samsung.android.deviceidservice.DeviceIdService");
        try {
            boolean isBinded = context.bindService(intent, new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    Logger.print("Samsung DeviceIdService connected");
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
                    } catch (Exception e) {
                        Logger.print(e);
                        getter.onOAIDGetError(e);
                    } finally {
                        context.unbindService(this);
                    }
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                    Logger.print("Samsung DeviceIdService disconnected");
                }
            }, Context.BIND_AUTO_CREATE);
            if (!isBinded) {
                throw new RuntimeException("Samsung DeviceIdService bind failed");
            }
        } catch (Exception e) {
            getter.onOAIDGetError(e);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void doGet(@NonNull final IGetter getter) {
        doGet(new IOAIDGetter() {
            @Override
            public void onOAIDGetComplete(@NonNull String oaid) {
                getter.onDeviceIdGetComplete(oaid);
            }

            @Override
            public void onOAIDGetError(@NonNull Exception exception) {
                getter.onDeviceIdGetError(exception);
            }
        });
    }

}
