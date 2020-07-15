/*
 * Copyright (c) 2019-2020 gzu-liyujiang <1032694760@qq.com>
 *
 * The software is licensed under the Mulan PSL v1.
 * You can use this software according to the terms and conditions of the Mulan PSL v1.
 * You may obtain a copy of Mulan PSL v1 at:
 *     http://license.coscl.org.cn/MulanPSL
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
 * PURPOSE.
 * See the Mulan PSL v1 for more details.
 *
 */
package com.github.gzuliyujiang.oaid;

import android.content.Context;
import android.os.Build;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.impl.AsusDeviceIdImpl;
import com.github.gzuliyujiang.oaid.impl.DefaultDeviceIdImpl;
import com.github.gzuliyujiang.oaid.impl.HuaweiDeviceIdImpl;
import com.github.gzuliyujiang.oaid.impl.LenovoDeviceIdImpl;
import com.github.gzuliyujiang.oaid.impl.MeizuDeviceIdImpl;
import com.github.gzuliyujiang.oaid.impl.MsaDeviceIdImpl;
import com.github.gzuliyujiang.oaid.impl.NubiaDeviceIdImpl;
import com.github.gzuliyujiang.oaid.impl.OppoDeviceIdImpl;
import com.github.gzuliyujiang.oaid.impl.SamsungDeviceIdImpl;
import com.github.gzuliyujiang.oaid.impl.VivoDeviceIdImpl;
import com.github.gzuliyujiang.oaid.impl.XiaomiDeviceIdImpl;

/**
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民
 */
public final class DeviceID {

    private DeviceID() {
        super();
    }

    public static IDeviceId with(Context context) {
        IDeviceId deviceId;
        if (SystemUtils.isLenovo() || SystemUtils.isMotolora()) {
            deviceId = new LenovoDeviceIdImpl(context);
        } else if (SystemUtils.isMeizu()) {
            deviceId = new MeizuDeviceIdImpl(context);
        } else if (SystemUtils.isNubia()) {
            deviceId = new NubiaDeviceIdImpl(context);
        } else if (SystemUtils.isXiaomi() || SystemUtils.isBlackShark()) {
            deviceId = new XiaomiDeviceIdImpl(context);
        } else if (SystemUtils.isSamsung()) {
            deviceId = new SamsungDeviceIdImpl(context);
        } else if (SystemUtils.isVivo()) {
            deviceId = new VivoDeviceIdImpl(context);
        } else if (SystemUtils.isASUS()) {
            deviceId = new AsusDeviceIdImpl(context);
        } else if (SystemUtils.isHuawei()) {
            deviceId = new HuaweiDeviceIdImpl(context);
        } else if (SystemUtils.isOppo() || SystemUtils.isOnePlus()) {
            deviceId = new OppoDeviceIdImpl(context);
        } else if (SystemUtils.isZTE() || SystemUtils.isFreeme() || SystemUtils.isSSUI()) {
            deviceId = new MsaDeviceIdImpl(context);
        } else {
            deviceId = new DefaultDeviceIdImpl();
        }
        Logger.print(deviceInfo() + "\nsupportOAID: " + deviceId.supportOAID());
        return deviceId;
    }

    public static String deviceInfo() {
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append("BrandModel：");
        sb.append(Build.BRAND);
        sb.append(" ");
        sb.append(Build.MODEL);
        sb.append("\n");
        sb.append("Manufacturer：");
        sb.append(Build.MANUFACTURER);
        sb.append("\n");
        sb.append("SystemVersion：");
        sb.append(Build.VERSION.RELEASE);
        sb.append(" (API ");
        sb.append(Build.VERSION.SDK_INT);
        sb.append(")");
        return sb.toString();
    }

}
