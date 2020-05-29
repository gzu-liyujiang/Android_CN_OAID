/*
 * MIT License
 *
 * Copyright (c) 2020 贵州穿青人@李裕江 <1032694760@qq.com>
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
public class DeviceID {

    private DeviceID() {
        super();
    }

    public static IDeviceId with(Context context) {
        Logger.print("manufacturer====>" + Build.MANUFACTURER.toUpperCase());
        IDeviceId deviceId;
        if (Utils.isLenovo() || Utils.isMotolora()) {
            deviceId = new LenovoDeviceIdImpl(context);
        } else if (Utils.isMeizu()) {
            deviceId = new MeizuDeviceIdImpl(context);
        } else if (Utils.isNubia()) {
            deviceId = new NubiaDeviceIdImpl(context);
        } else if (Utils.isXiaomi() || Utils.isBlackShark()) {
            deviceId = new XiaomiDeviceIdImpl(context);
        } else if (Utils.isSamsung()) {
            deviceId = new SamsungDeviceIdImpl(context);
        } else if (Utils.isVivo()) {
            deviceId = new VivoDeviceIdImpl(context);
        } else if (Utils.isASUS()) {
            deviceId = new AsusDeviceIdImpl(context);
        } else if (Utils.isHuawei()) {
            deviceId = new HuaweiDeviceIdImpl(context);
        } else if (Utils.isOppo() || Utils.isOnePlus()) {
            deviceId = new OppoDeviceIdImpl(context);
        } else if (Utils.isZTE() || Utils.isFreeme() || Utils.isSSUI()) {
            deviceId = new MsaDeviceIdImpl(context);
        } else {
            deviceId = new DefaultDeviceIdImpl(context);
        }
        return deviceId;
    }

}
