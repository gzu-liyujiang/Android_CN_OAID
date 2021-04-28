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

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.oaid.IOAID;
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
        if (ioaid == null) {
            if (OAIDRom.isLenovo() || OAIDRom.isMotolora()) {
                ioaid = new LenovoImpl(context);
            } else if (OAIDRom.isMeizu()) {
                ioaid = new MeizuImpl(context);
            } else if (OAIDRom.isNubia()) {
                ioaid = new NubiaImpl(context);
            } else if (OAIDRom.isXiaomi() || OAIDRom.isBlackShark()) {
                ioaid = new XiaomiImpl(context);
            } else if (OAIDRom.isSamsung()) {
                ioaid = new SamsungImpl(context);
            } else if (OAIDRom.isVivo()) {
                ioaid = new VivoImpl(context);
            } else if (OAIDRom.isASUS()) {
                ioaid = new AsusImpl(context);
            } else if (OAIDRom.isHuawei()) {
                ioaid = new HuaweiImpl(context);
            } else if (OAIDRom.isOppo() || OAIDRom.isOnePlus()) {
                ioaid = new OppoImpl(context);
            } else if (OAIDRom.isZTE() || OAIDRom.isFreeme() || OAIDRom.isSSUI()) {
                ioaid = new MsaImpl(context);
            } else {
                ioaid = new DefaultImpl();
            }
        }
        return ioaid;
    }

}
