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

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAIDGetter;

import java.util.Objects;

/**
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民（1032694760@qq.com）
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class MeizuDeviceIdImpl implements IDeviceId {
    private final Context context;
    private BroadcastReceiver receiver;
    private boolean received;

    public MeizuDeviceIdImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supportOAID() {
        try {
            ProviderInfo pi = context.getPackageManager().resolveContentProvider("com.meizu.flyme.openidsdk", 0);
            if (pi != null) {
                return true;
            }
        } catch (Exception e) {
            Logger.print(e);
        }
        return false;
    }

    @Override
    public void doGet(@NonNull final IOAIDGetter getter) {
        received = false;
        if (receiver != null) {
            context.unregisterReceiver(receiver);
        }
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Logger.print("OPEN_ID_CHANGE: " + intent);
                received = true;
                // TODO: 2021/3/1 待实现待验证
                queryID(getter);
            }
        };
        IntentFilter filter = new IntentFilter("com.meizu.flyme.openid.ACTION_OPEN_ID_CHANGE");
        context.registerReceiver(receiver, filter, "com.meizu.flyme.openid.permission.OPEN_ID_CHANGE", null);
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                if (received) {
                    return;
                }
                queryID(getter);
            }
        }, 1000);
    }

    private void queryID(@NonNull IOAIDGetter getter) {
        Uri uri = Uri.parse("content://com.meizu.flyme.openidsdk/");
        try (Cursor cursor = context.getContentResolver().query(uri, null, null, new String[]{"oaid"}, null)) {
            Objects.requireNonNull(cursor).moveToFirst();
            String ret = null;
            int valueIdx = cursor.getColumnIndex("value");
            if (valueIdx > 0) {
                ret = cursor.getString(valueIdx);
            }
            if (ret == null || ret.length() == 0) {
                throw new RuntimeException("OAID query failed");
            }
            getter.onOAIDGetComplete(ret);
        } catch (Exception e) {
            Logger.print(e);
        } finally {
            if (receiver != null) {
                context.unregisterReceiver(receiver);
                receiver = null;
            }
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
