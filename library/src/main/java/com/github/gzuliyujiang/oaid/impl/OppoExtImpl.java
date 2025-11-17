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

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.os.IBinder;
import android.os.RemoteException;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.OAIDException;
import com.github.gzuliyujiang.oaid.OAIDLog;

import repeackage.com.oplus.stdid.IStdID;

/**
 * @author luoyesiqiu
 * @since 2023/10/28 09:13
 */
public class OppoExtImpl extends OppoImpl {
    private final static String ACTION = "action.com.oplus.stdid.ID_SERVICE";
    private final static String PACKAGE_NAME = "com.coloros.mcs";
    private final static String CLASS_NAME = "com.oplus.stdid.IdentifyService";

    private final Context context;

    public OppoExtImpl(Context context) {
        super(context);
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(PACKAGE_NAME, 0);
            return pi != null;
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
        Intent intent = new Intent(ACTION);
        intent.setComponent(new ComponentName(PACKAGE_NAME, CLASS_NAME));
        OAIDService.bind(context, intent, getter, new OAIDService.RemoteCaller() {
            @Override
            public String callRemoteInterface(IBinder service) throws OAIDException, RemoteException {
                try {
                    return realGetOUID(service);
                } catch (OAIDException | RemoteException e) {
                    throw e;
                } catch (Exception e) {
                    throw new OAIDException(e);
                }
            }
        });
    }

    protected String getSerId(IBinder service, String pkgName, String sign) throws RemoteException, OAIDException {
        IStdID anInterface = IStdID.Stub.asInterface(service);
        if (anInterface == null) {
            throw new OAIDException("IStdID is null");
        }
        return anInterface.getSerID(pkgName, sign, "OUID");
    }

}
