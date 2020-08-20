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
package com.github.gzuliyujiang.oaid.impl;

import androidx.annotation.NonNull;
import androidx.annotation.RestrictTo;

import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.IOAIDGetter;

/**
 * 随机生成一个全局唯一标识，通过`SharedPreferences`及`ExternalStorage`进行永久化存储。
 * 注：非系统及预装APP无法获得`WRITE_SETTINGS`权限，故放弃使用`Settings`进行永久化存储。
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public class DefaultDeviceIdImpl implements IDeviceId {

    @Override
    public boolean supportOAID() {
        return false;
    }

    @Override
    public void doGet(@NonNull final IOAIDGetter getter) {
        getter.onOAIDGetError(new RuntimeException("OAID unsupported"));
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
