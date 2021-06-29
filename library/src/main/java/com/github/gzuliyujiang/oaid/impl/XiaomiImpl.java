/*
 * Copyright (c) 2016-present 贵州纳雍穿青人李裕江<1032694760@qq.com>
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

import android.annotation.SuppressLint;
import android.content.Context;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDException;
import com.github.gzuliyujiang.oaid.OAIDLog;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 参阅 http://f4.market.xiaomi.com/download/MiPass/058fc4374ac89aea6dedd9dc03c60a5498241e0dd/DeviceId.jar
 * 即 com.miui.deviceid.IdentifierManager
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
class XiaomiImpl implements IOAID {
    private final Context context;
    private Class<?> idProviderClass;
    private Object idProviderImpl;

    @SuppressLint("PrivateApi")
    public XiaomiImpl(Context context) {
        this.context = context;
        try {
            idProviderClass = Class.forName("com.android.id.impl.IdProviderImpl");
            idProviderImpl = idProviderClass.newInstance();
        } catch (Exception e) {
            OAIDLog.print(e);
        }
    }

    @Override
    public boolean supported() {
        return idProviderImpl != null;
    }

    @Override
    public void doGet(final IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        if (idProviderClass == null || idProviderImpl == null) {
            getter.onOAIDGetError(new OAIDException("Xiaomi IdProvider not exists"));
            return;
        }
        try {
            String oaid = getOAID();
            if (oaid == null || oaid.length() == 0) {
                throw new OAIDException("OAID query failed");
            }
            OAIDLog.print("OAID query success: " + oaid);
            getter.onOAIDGetComplete(oaid);
        } catch (Exception e) {
            OAIDLog.print(e);
            getter.onOAIDGetError(e);
        }
    }

    private String getOAID() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = idProviderClass.getMethod("getOAID", Context.class);
        return (String) method.invoke(idProviderImpl, context);
    }

}
