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

package com.github.gzuliyujiang.fallback;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.github.gzuliyujiang.oaid.DeviceID;
import com.github.gzuliyujiang.oaid.IGetter;
import com.github.gzuliyujiang.oaid.OAIDLog;

/**
 * @author 大定府羡民（1032694760@qq.com）
 * @since 2020/5/20
 */
public class MainActivity extends AppCompatActivity implements ActivityResultCallback<Boolean>, View.OnClickListener {
    private ActivityResultLauncher<String> resultLauncher;
    private TextView tvDeviceIdResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), this);
        setContentView(R.layout.activity_main);
        TextView tvDeviceInfo = findViewById(R.id.tv_device_info);
        tvDeviceInfo.setText(obtainDeviceInfo());
        findViewById(R.id.btn_get_device_id_1).setOnClickListener(this);
        findViewById(R.id.btn_get_device_id_2).setOnClickListener(this);
        tvDeviceIdResult = findViewById(R.id.tv_device_id_result);
    }

    @Override
    public void onActivityResult(Boolean result) {
        if (result != null && result) {
            obtainDeviceId();
            return;
        }
        Toast.makeText(this, "请授予电话状态权限以便获取IMEI！", Toast.LENGTH_LONG).show();
        obtainDeviceId();
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_get_device_id_1) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                obtainDeviceId();
                return;
            }
            resultLauncher.launch(Manifest.permission.READ_PHONE_STATE);
        } else if (id == R.id.btn_get_device_id_2) {
            tvDeviceIdResult.setText(String.format("DeviceID: %s", DeviceID.getClientIdMD5()));
        } else {
            OAIDLog.print("\"if ... else if\" constructs should end with \"else\" clauses.");
        }
    }

    private void obtainDeviceId() {
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
        builder.append("WidevineID: ");
        // 获取数字版权管理ID，可能为空
        String widevineID = DeviceID.getWidevineID();
        if (TextUtils.isEmpty(widevineID)) {
            builder.append("WidevineID获取失败");
        } else {
            builder.append(widevineID);
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
        // 是否支持OAID/AAID
        builder.append("supported: ").append(DeviceID.supportedOAID(this));
        builder.append("\n");
        // 获取OAID/AAID，异步回调
        DeviceID.getOAID(this, new IGetter() {
            @Override
            public void onOAIDGetComplete(String result) {
                // 不同厂商的OAID/AAID格式是不一样的，可进行MD5、SHA1之类的哈希运算统一
                builder.append("OAID/AAID: ").append(result);
                tvDeviceIdResult.setText(builder);
            }

            @Override
            public void onOAIDGetError(Exception error) {
                // 获取OAID/AAID失败
                builder.append("OAID/AAID: ").append(error);
                tvDeviceIdResult.setText(builder);
            }
        });
    }

    private String obtainDeviceInfo() {
        //noinspection StringBufferReplaceableByString
        StringBuilder sb = new StringBuilder();
        sb.append("BrandModel：");
        sb.append(Build.BRAND);
        sb.append(" ");
        sb.append(Build.MODEL);
        sb.append("\n");
        sb.append("Manufacturer：");
        sb.append(Build.MANUFACTURER);
        sb.append("\n");
        sb.append("SystemVersion：");
        sb.append(Build.VERSION.RELEASE);
        sb.append(" (Level ");
        sb.append(Build.VERSION.SDK_INT);
        sb.append(")");
        return sb.toString();
    }

}
