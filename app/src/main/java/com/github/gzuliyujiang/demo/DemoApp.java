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

package com.github.gzuliyujiang.demo;

import android.app.Application;

import com.github.gzuliyujiang.oaid.DeviceID;
import com.github.gzuliyujiang.oaid.OAIDLog;

/**
 * Created by liyujiang on 2020/5/20.
 *
 * @author 大定府羡民（1032694760@qq.com）
 */
public class DemoApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        OAIDLog.enable();
        // 注意：如果不需要调用`getClientId()`及`getOAID()`，请不要调用这个方法
        DeviceID.register(this);
    }

}
