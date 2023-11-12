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

package com.github.gzuliyujiang.oaid;

/**
 * @author Mankin
 * @noinspection unused
 * @since 2023/10/30 00:20
 */
public interface IRegisterCallback {
    /**
     * @deprecated 使用{@link #onComplete(String)}代替
     */
    @Deprecated
    default void onComplete() {
        onComplete("", null);
    }

    /**
     * 启动时注册完成回调，
     *
     * @param clientId 客户端标识按优先级尝试获取IMEI/MEID、OAID、AndroidID、GUID。
     * @param error    OAID获取失败时的异常信息
     */
    default void onComplete(String clientId, Exception error) {
    }
}
