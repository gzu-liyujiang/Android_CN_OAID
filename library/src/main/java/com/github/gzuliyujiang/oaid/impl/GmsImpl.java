/*
 * Copyright (c) 2016-present. 贵州纳雍穿青人李裕江 and All Contributors.
 *
 * The software is licensed under the Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *     http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.github.gzuliyujiang.oaid.impl;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.RemoteException;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDException;
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
        if (context == null) {
            return false;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.google.android.gms", 0);
            return pi != null;
        } catch (Exception e) {
            OAIDLog.print(e);
            return false;
        }
    }

    @Override
    public void doGet(final IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        Intent intent = new Intent("com.google.android.gms.ads.identifier.service.START");
        intent.setPackage("com.google.android.gms");
        OAIDService.bind(context, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                IAdvertisingIdService anInterface = IAdvertisingIdService.Stub.asInterface(service);
                if (anInterface.isLimitAdTrackingEnabled(true)) {
                    OAIDLog.print("User has disabled advertising identifier");
                    // 从2022年开始，当isLimitAdTrackingEnabled()为true时，无论应用的目标SDK级别如何，getId()的返回值都是00000000-0000-0000-0000-000000000000
                    throw new OAIDException("AAID acquire failed");
                }
                return anInterface.getId();
            }
        });
    }

}
