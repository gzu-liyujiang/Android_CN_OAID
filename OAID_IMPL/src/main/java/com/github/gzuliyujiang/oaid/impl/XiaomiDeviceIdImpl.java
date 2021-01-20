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

import android.annotation.SuppressLint;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAIDGetter;

import java.lang.reflect.Method;

/**
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民（1032694760@qq.com）
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class XiaomiDeviceIdImpl implements IDeviceId {
    private final Context context;
    private Class<?> idProvider;

    public XiaomiDeviceIdImpl(Context context) {
        this.context = context;
    }

    @SuppressLint("PrivateApi")
    @Override
    public boolean supportOAID() {
        try {
            idProvider = Class.forName("com.android.id.impl.IdProviderImpl");
            return true;
        } catch (Exception e) {
            Logger.print(e);
            return false;
        }
    }

    @SuppressLint("PrivateApi")
    @Override
    public void doGet(@NonNull final IOAIDGetter getter) {
        if (idProvider == null) {
            try {
                idProvider = Class.forName("com.android.id.impl.IdProviderImpl");
            } catch (Exception e) {
                Logger.print(e);
            }
        }
        String did = null;
        try {
            Method udidMethod = idProvider.getMethod("getDefaultUDID", Context.class);
            did = invokeMethod(udidMethod);
        } catch (Exception e) {
            Logger.print(e);
        }
        if (did != null && did.length() > 0) {
            getter.onOAIDGetComplete(did);
            return;
        }
        try {
            Method oaidMethod = idProvider.getMethod("getOAID", Context.class);
            did = invokeMethod(oaidMethod);
            if (did != null && did.length() > 0) {
                getter.onOAIDGetComplete(did);
            } else {
                throw new RuntimeException("Xiaomi OAID get failed");
            }
        } catch (Exception e) {
            Logger.print(e);
            getter.onOAIDGetError(e);
        }
    }

    private String invokeMethod(Method method) {
        String result = null;
        if (method != null) {
            try {
                result = (String) method.invoke(idProvider.newInstance(), context);
            } catch (Exception e) {
                Logger.print(e);
            }
        }
        return result;
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
