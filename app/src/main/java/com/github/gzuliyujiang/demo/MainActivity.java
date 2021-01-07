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

package com.github.gzuliyujiang.demo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.DeviceID;
import com.github.gzuliyujiang.oaid.IDeviceId;
import com.github.gzuliyujiang.oaid.IOAIDGetter;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, IOAIDGetter {
    private TextView tvOAID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TextView tvDeviceInfo = findViewById(R.id.tv_device_info);
        tvDeviceInfo.setText(DeviceID.deviceInfo());
        findViewById(R.id.btn_oaid_get).setOnClickListener(this);
        tvOAID = findViewById(R.id.tv_oaid_result);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_oaid_get) {
            Logger.print("DeviceID doGet");
            IDeviceId deviceId = DeviceID.with(this);
            if (!deviceId.supportOAID()) {
                tvOAID.setText("不支持OAID，须自行生成GUID");
                return;
            }
            deviceId.doGet(this);
        }
    }

    @Override
    public void onOAIDGetComplete(@NonNull String oaid) {
        Logger.print("onOAIDGetComplete====>" + oaid);
        tvOAID.setText(oaid);
    }

    @Override
    public void onOAIDGetError(@NonNull Exception exception) {
        Logger.print("onOAIDGetError====>" + exception);
        tvOAID.setText(exception.toString());
    }

}
