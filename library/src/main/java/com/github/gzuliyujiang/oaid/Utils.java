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

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.logger.Logger;

import java.lang.reflect.Method;

/**
 * Created by liyujiang on 2020/5/29
 *
 * @author 大定府羡民
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class Utils {

    @SuppressWarnings("unchecked")
    public static String sysProperty(String key, String defValue) {
        String res = null;
        try {
            @SuppressLint("PrivateApi") Class clazz = Class.forName("android.os.SystemProperties");
            Method method = clazz.getMethod("get", String.class, String.class);
            res = (String) method.invoke(clazz, new Object[]{key, defValue});
        } catch (Exception e) {
            Logger.print(e);
        }
        if (res == null) {
            res = "";
        }
        return res;
    }

    public static boolean isASUS() {
        // 华硕手机
        return Build.MANUFACTURER.toUpperCase().equals("ASUS");
    }

    public static boolean isHuawei() {
        // 华为手机
        return Build.MANUFACTURER.toUpperCase().equals("HUAWEI");
    }

    public static boolean isZTE() {
        // 中兴手机
        return Build.MANUFACTURER.toUpperCase().equals("ZTE");
    }

    public static boolean isXiaomi() {
        // 小米手机
        return Build.MANUFACTURER.toUpperCase().equals("XIAOMI");
    }

    public static boolean isOppo() {
        // 欧珀手机
        return Build.MANUFACTURER.toUpperCase().equals("OPPO");
    }

    public static boolean isVivo() {
        // 维沃手机
        return Build.MANUFACTURER.toUpperCase().equals("VIVO");
    }

    public static boolean isOnePlus() {
        // 一加手机
        return Build.MANUFACTURER.toUpperCase().equals("ONEPLUS");
    }

    public static boolean isBlackShark() {
        // 黑鲨手机
        return Build.MANUFACTURER.toUpperCase().equals("BLACKSHARK");
    }

    public static boolean isSamsung() {
        // 三星手机
        return Build.MANUFACTURER.toUpperCase().equals("SAMSUNG");
    }

    public static boolean isMotolora() {
        // 摩托罗拉手机
        return Build.MANUFACTURER.toUpperCase().equals("MOTOLORA");
    }

    public static boolean isNubia() {
        // 努比亚手机
        return Build.MANUFACTURER.toUpperCase().equals("NUBIA");
    }

    public static boolean isMeizu() {
        // 魅族手机
        return Build.MANUFACTURER.toUpperCase().equals("MEIZU");
    }

    public static boolean isLenovo() {
        // 联想手机
        return Build.MANUFACTURER.toUpperCase().equals("LENOVO");
    }

    public static boolean isFreeme() {
        // 卓易手机
        if (Build.MANUFACTURER.toUpperCase().equals("FREEMEOS")) {
            return true;
        }
        String pro = sysProperty("ro.build.freeme.label", "");
        return !TextUtils.isEmpty(pro) && pro.toUpperCase().equals("FREEMEOS");
    }

    public static boolean isSSUI() {
        // 这是啥玩意的手机？
        if (Build.MANUFACTURER.toUpperCase().equals("SSUI")) {
            return true;
        }
        String pro = sysProperty("ro.ssui.product", "unknown");
        return !TextUtils.isEmpty(pro) && !pro.toUpperCase().equals("UNKNOWN");
    }

}
