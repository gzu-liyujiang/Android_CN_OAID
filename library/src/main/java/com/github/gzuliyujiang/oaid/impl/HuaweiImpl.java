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
import android.content.pm.PackageManager;

import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDException;
import com.github.gzuliyujiang.oaid.OAIDLog;
import com.huawei.hms.ads.identifier.AdvertisingIdClient;

import java.io.IOException;

/**
 * 参阅华为官方 <a href="https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/identifier-service-integrating-sdk-0000001056460552">HUAWEI Ads SDK</a>。
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/30
 */
class HuaweiImpl implements IOAID {
    private final Context context;

    public HuaweiImpl(Context context) {
        this.context = context;
    }

    @Override
    public boolean supported() {
        if (context == null) {
            return false;
        }
        boolean ret = false;
        try {
            PackageManager pm = context.getPackageManager();
            if (pm.getPackageInfo("com.huawei.hwid", 0) != null) {
                ret = true;
            } else if (pm.getPackageInfo("com.huawei.hwid.tv", 0) != null) {
                ret = true;
            } else {
                ret = pm.getPackageInfo("com.huawei.hms", 0) != null;
            }
        } catch (Exception e) {
            OAIDLog.print(e);
        }
        return ret;
    }

    @Override
    public void doGet(final IGetter getter) {
        if (context == null || getter == null) {
            return;
        }
        try {
            AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(context);
            if (info == null) {
                getter.onOAIDGetError(new OAIDException("Advertising identifier info is null"));
                return;
            }
            if (info.isLimitAdTrackingEnabled()) {
                // 实测在系统设置中关闭了广告标识符，将获取到固定的一大堆0
                getter.onOAIDGetError(new OAIDException("User has disabled advertising identifier"));
                return;
            }
            getter.onOAIDGetComplete(info.getId());
        } catch (IOException e) {
            OAIDLog.print(e);
            getter.onOAIDGetError(e);
        }
    }

}
