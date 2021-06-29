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
package com.github.gzuliyujiang.oaid;

/**
 * OAID获取回调
 *
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/8/20
 */
public interface IGetter {

    /**
     * 成功获取到OAID
     *
     * @param result OAID
     */
    void onOAIDGetComplete(String result);

    /**
     * OAID获取失败（不正常或获取不到）
     *
     * @param error 异常信息
     */
    void onOAIDGetError(Exception error);

}
