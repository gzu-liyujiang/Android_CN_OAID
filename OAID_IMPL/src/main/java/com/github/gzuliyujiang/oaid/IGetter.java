/*
 * Copyright (c) 2019-2021 gzu-liyujiang <1032694760@qq.com>
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

import androidx.annotation.NonNull;

/**
 * Created by liyujiang on 2020/5/30
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @deprecated Use {@link IOAIDGetter} instead
 */
@Deprecated
public interface IGetter {

    void onDeviceIdGetComplete(@NonNull String deviceId);

    void onDeviceIdGetError(@NonNull Exception exception);

}
