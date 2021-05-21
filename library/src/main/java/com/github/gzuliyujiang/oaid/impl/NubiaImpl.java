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

import android.content.ContentProviderClient;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDLog;

/**
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
class NubiaImpl implements IOAID {
    private final Context context;

    public NubiaImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q;
    }

    @Override
    public void doGet(@NonNull final IGetter getter) {
        if (!supported()) {
            String message = "Only supports Android 10.0 and above for Nubia";
            OAIDLog.print(message);
            getter.onOAIDGetError(new RuntimeException(message));
            return;
        }
        String oaid = null;
        Bundle bundle = null;
        try {
            Uri uri = Uri.parse("content://cn.nubia.identity/identity");
            ContentProviderClient client = context.getContentResolver().acquireContentProviderClient(uri);
            if (client != null) {
                bundle = client.call("getOAID", null, null);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    client.close();
                } else {
                    client.release();
                }
            }
            if (bundle == null) {
                throw new RuntimeException("OAID query failed: bundle is null");
            }
            if (bundle.getInt("code", -1) == 0) {
                oaid = bundle.getString("id");
            }
            if (oaid != null && oaid.length() > 0) {
                OAIDLog.print("OAID query success: " + oaid);
                getter.onOAIDGetComplete(oaid);
            } else {
                throw new RuntimeException("OAID query failed: " + bundle.getString("message"));
            }
        } catch (Exception e) {
            OAIDLog.print(e);
            getter.onOAIDGetError(e);
        }
    }

}
