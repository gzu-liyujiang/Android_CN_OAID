/*
 * Copyright (c) 2019-2020 gzu-liyujiang <1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v1.
 * You can use this software according to the terms and conditions of the Mulan PSL v1.
 * You may obtain a copy of Mulan PSL v1 at:
 *     http://license.coscl.org.cn/MulanPSL
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v1 for more details.
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
import com.uodis.opendevice.aidl.OpenDeviceIdentifierService;

/**
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class HuaweiDeviceIdImpl implements IDeviceId {
    private Context context;

    public HuaweiDeviceIdImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supportOAID() {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.huawei.hwid", 0);
            return pi != null;
        } catch (Exception e) {
            Logger.print(e);
            return false;
        }
    }

    @Override
    public void doGet(@NonNull final IGetter getter) {
        Intent intent = new Intent("com.uodis.opendevice.OPENIDS_SERVICE");
        intent.setPackage("com.huawei.hwid");
        boolean isBinded = context.bindService(intent, new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                Logger.print("Huawei OPENIDS_SERVICE connected");
                try {
                    OpenDeviceIdentifierService anInterface = OpenDeviceIdentifierService.Stub.asInterface(service);
                    String IDs = anInterface.getIDs();
                    if (IDs == null || IDs.length() == 0) {
                        getter.onDeviceIdGetError(new RuntimeException("Huawei IDs get failed"));
                    } else {
                        getter.onDeviceIdGetComplete(IDs);
                    }
                } catch (Exception e) {
                    Logger.print(e);
                    getter.onDeviceIdGetError(e);
                } finally {
                    context.unbindService(this);
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                Logger.print("Huawei OPENIDS_SERVICE disconnected");
            }
        }, Context.BIND_AUTO_CREATE);
        if (!isBinded) {
            getter.onDeviceIdGetError(new RuntimeException("Huawei OPENIDS_SERVICE bind failed"));
        }
    }

}
