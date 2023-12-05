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
import android.os.Handler;
import android.os.Looper;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDException;
import com.github.gzuliyujiang.oaid.OAIDLog;
import com.hihonor.ads.identifier.AdvertisingIdClient;

import java.util.concurrent.Executors;

/**
 * 参阅荣耀官方 <a href="https://developer.hihonor.com/cn/kitdoc?kitId=11030&navigation=guides&docId=dev-overview.md&token=">HONOR Ads SDK</a>。
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2023/12/04
 */
class HonorImpl implements IOAID {
    private final Context context;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    public HonorImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        // 核心标识：com.hihonor.id 或 com.hihonor.id.HnOaIdService
        return AdvertisingIdClient.isAdvertisingIdAvailable(context);
    }

    @Override
    public void doGet(final IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        Executors.newSingleThreadExecutor().execute(new Runnable() {
            @Override
            public void run() {
                runOnSubThread(getter);
            }
        });
    }

    private void runOnSubThread(IGetter getter) {
        // 参阅 https://developer.hihonor.com/cn/kitdoc?kitId=11030&navigation=ref&docId=AdvertisingIdClient.md
        try {
            // 如果用户手机中，HMS Core（APK）版本在2.6.2以下，无法获取OAID，将抛出IOException。
            final AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(context);
            if (info == null) {
                postOnMainThread(getter, new OAIDException("Advertising identifier info is null"));
                return;
            }
            if (info.isLimit) {
                // 实测在系统设置中关闭了广告标识符，将获取到固定的一大堆0
                postOnMainThread(getter, new OAIDException("User has disabled advertising identifier"));
                return;
            }
            postOnMainThread(getter, info.id);
        } catch (Exception e) {
            OAIDLog.print(e);
            postOnMainThread(getter, new OAIDException(e));
        }
    }

    private void postOnMainThread(final IGetter getter, final String oaid) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                getter.onOAIDGetComplete(oaid);
            }
        });
    }

    private void postOnMainThread(final IGetter getter, final OAIDException e) {
        uiHandler.post(new Runnable() {
            @Override
            public void run() {
                getter.onOAIDGetError(e);
            }
        });
    }

}
