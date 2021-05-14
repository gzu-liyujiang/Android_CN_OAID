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

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.oaid.IOAID;
import com.github.gzuliyujiang.oaid.OAIDLog;
import com.github.gzuliyujiang.oaid.OAIDRom;

/**
 * @author 贵州山野羡民（1032694760@qq.com）
 * @since 2021/4/28 20:32
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class OAIDFactory {
    private static IOAID ioaid;

    private OAIDFactory() {
        super();
    }

    public static IOAID create(@NonNull Context context) {
        if (!(context instanceof Application)) {
            // See https://github.com/gzu-liyujiang/Android_CN_OAID/pull/23
            context = context.getApplicationContext();
        }
        if (ioaid == null) {
            if (OAIDRom.isLenovo() || OAIDRom.isMotolora()) {
                ioaid = new LenovoImpl(context);
            } else if (OAIDRom.isMeizu()) {
                ioaid = new MeizuImpl(context);
            } else if (OAIDRom.isNubia()) {
                ioaid = new NubiaImpl(context);
            } else if (OAIDRom.isXiaomi() || OAIDRom.isMiui() || OAIDRom.isBlackShark()) {
                ioaid = new XiaomiImpl(context);
            } else if (OAIDRom.isSamsung()) {
                ioaid = new SamsungImpl(context);
            } else if (OAIDRom.isVivo()) {
                ioaid = new VivoImpl(context);
            } else if (OAIDRom.isASUS()) {
                ioaid = new AsusImpl(context);
            } else if (OAIDRom.isHuawei() || OAIDRom.isEmui()) {
                ioaid = new HuaweiImpl(context);
            } else if (OAIDRom.isOppo() || OAIDRom.isOnePlus()) {
                ioaid = new OppoImpl(context);
            }
        }
        if (ioaid == null || !ioaid.supported()) {
            ioaid = new GmsImpl(context);
            if (ioaid.supported()) {
                OAIDLog.print("Google Mobile Service has been found");
            } else {
                ioaid = new MsaImpl(context);
                if (ioaid.supported()) {
                    OAIDLog.print("Mobile Security Alliance has been found");
                } else {
                    ioaid = new DefaultImpl();
                }
            }
        }
        return ioaid;
    }

}
