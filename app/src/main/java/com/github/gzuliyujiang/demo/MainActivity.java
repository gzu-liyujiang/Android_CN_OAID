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

package com.github.gzuliyujiang.demo;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.gzuliyujiang.oaid.DeviceID;
import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IOAIDGetter;
import com.hjq.permissions.OnPermission;
import com.hjq.permissions.Permission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

/**
 * Created by liyujiang on 2020/5/20.
 *
 * @author 大定府羡民（1032694760@qq.com）
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tvDeviceIdResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvDeviceInfo = findViewById(R.id.tv_device_info);
        tvDeviceInfo.setText(DeviceID.deviceInfo());
        findViewById(R.id.btn_get_device_id).setOnClickListener(this);
        tvDeviceIdResult = findViewById(R.id.tv_device_id_result);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_get_device_id) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                getDeviceId();
                return;
            }
            XXPermissions.with(this).permission(Permission.READ_PHONE_STATE).request(new OnPermission() {
                @Override
                public void hasPermission(List<String> granted, boolean all) {
                    getDeviceId();
                }

                @Override
                public void noPermission(List<String> denied, boolean never) {
                    getDeviceId();
                }
            });
        }
    }

    private void getDeviceId() {
        final StringBuilder builder = new StringBuilder();
        builder.append("UniqueID: ");
        // 获取设备唯一标识，只支持Android 10之前的系统，需要READ_PHONE_STATE权限，可能为空
        String uniqueID = DeviceID.getUniqueID(this);
        if (TextUtils.isEmpty(uniqueID)) {
            builder.append("DID/IMEI/MEID获取失败");
        } else {
            builder.append(uniqueID);
        }
        builder.append("\n");
        builder.append("AndroidID: ");
        // 获取安卓ID，可能为空
        String androidID = DeviceID.getAndroidID(this);
        if (TextUtils.isEmpty(androidID)) {
            builder.append("AndroidID获取失败");
        } else {
            builder.append(androidID);
        }
        builder.append("\n");
        builder.append("PseudoID: ");
        // 获取伪造ID，根据硬件信息生成，不会为空，有大概率会重复
        builder.append(DeviceID.getPseudoID());
        builder.append("\n");
        builder.append("GUID: ");
        // 获取GUID，随机生成，不会为空
        builder.append(DeviceID.getGUID(this));
        builder.append("\n");
        IDeviceId deviceId = DeviceID.with(this);
        if (!deviceId.supportOAID()) {
            // 不支持OAID，须自行生成GUID，然后存到`SharedPreferences`及`ExternalStorage`甚至`CloudStorage`。
            builder.append("OAID: 不支持");
            tvDeviceIdResult.setText(builder);
            return;
        }
        deviceId.doGet(new IOAIDGetter() {
            @Override
            public void onOAIDGetComplete(@NonNull String oaid) {
                // 不同厂商的OAID格式是不一样的，可进行MD5、SHA1之类的哈希运算统一
                builder.append("OAID: ").append(oaid);
                tvDeviceIdResult.setText(builder);
            }

            @Override
            public void onOAIDGetError(@NonNull Exception exception) {
                // 获取OAID失败
                builder.append("OAID: exception=").append(exception);
                tvDeviceIdResult.setText(builder);
            }
        });
    }

}
