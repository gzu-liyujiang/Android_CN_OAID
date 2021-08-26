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

import android.app.KeyguardManager;
import android.content.Context;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDException;
import com.github.gzuliyujiang.oaid.OAIDLog;

import java.util.Objects;

/**
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2021/8/26 16:22
 */
public class CooseaImpl implements IOAID {
    private final Context context;
    private final KeyguardManager keyguardManager;

    public CooseaImpl(Context context) {
        this.context = context;
        this.keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        if (keyguardManager == null) {
            return false;
        }
        try {
            Object obj = keyguardManager.getClass().getDeclaredMethod("isSupported").invoke(keyguardManager);
            return (Boolean) Objects.requireNonNull(obj);
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
        if (keyguardManager == null) {
            getter.onOAIDGetError(new OAIDException("KeyguardManager not found"));
            return;
        }
        try {
            Object obj = keyguardManager.getClass().getDeclaredMethod("obtainOaid").invoke(keyguardManager);
            if (obj == null) {
                throw new OAIDException("OAID obtain failed");
            }
            String oaid = obj.toString();
            OAIDLog.print("OAID obtain success: " + oaid);
            getter.onOAIDGetComplete(oaid);
        } catch (Exception e) {
            OAIDLog.print(e);
        }
    }

}
