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

import repeackage.com.qiku.id.IOAIDInterface;
import repeackage.com.qiku.id.QikuIdmanager;

/**
 * @author 10cl
 * @since 2024/03/06
 */
public class QikuImpl implements IOAID {
    private final Context context;
    private boolean mUseQikuId = true;

    public QikuImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo("com.qiku.id", 0);
            if (pi != null) {
                return true;
            } else {
                mUseQikuId = false;
                return new QikuIdmanager().isSupported();
            }
        } catch (Exception e) {
            OAIDLog.print(e);
            return false;
        }
    }

    @Override
    public void doGet(IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        if (mUseQikuId) {
            Intent intent = new Intent("qiku.service.action.id");
            intent.setPackage("com.qiku.id");
            OAIDService.bind(context, intent, getter, new OAIDService.RemoteCaller() {
                @Override
                public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                    IOAIDInterface anInterface = IOAIDInterface.Stub.asInterface(service);
                    if (anInterface == null) {
                        throw new OAIDException("IOAIDInterface is null");
                    }
                    return anInterface.getOAID();
                }
            });
        } else {
            try {
                String oaid = new QikuIdmanager().getOAID();
                if (oaid == null || oaid.length() == 0) {
                    throw new OAIDException("OAID/AAID acquire failed");
                }
                getter.onOAIDGetComplete(oaid);
            } catch (Exception e) {
                getter.onOAIDGetError(e);
            }
        }
    }

}
