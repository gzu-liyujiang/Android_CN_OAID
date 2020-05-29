package com.github.gzuliyujiang.demo;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.github.gzuliyujiang.logger.Logger;
import com.github.gzuliyujiang.oaid.DeviceID;
import com.github.gzuliyujiang.oaid.IGetter;
import com.yanzhenjie.permission.AndPermission;

public class MainActivity extends AppCompatActivity implements IGetter {
    private static final String[] PERMISSIONS_All_NEED = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
    };
    private TextView tvOAID;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    tvOAID.setText(msg.obj.toString());
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkAllPermissions(this);
        setContentView(R.layout.activity_main);
        TextView tvDeviceInfo = findViewById(R.id.tv_device_info);
        tvDeviceInfo.setText("品牌型号：");
        tvDeviceInfo.append(Build.BRAND);
        tvDeviceInfo.append(" ");
        tvDeviceInfo.append(Build.MODEL);
        tvDeviceInfo.append("\n");
        tvDeviceInfo.append("生产厂商：");
        tvDeviceInfo.append(Build.MANUFACTURER);
        tvDeviceInfo.append("\n");
        tvDeviceInfo.append("系统版本：");
        tvDeviceInfo.append(Build.VERSION.RELEASE);
        tvDeviceInfo.append(" (API ");
        tvDeviceInfo.append(String.valueOf(Build.VERSION.SDK_INT));
        tvDeviceInfo.append(")");
        tvOAID = findViewById(R.id.tv_oaid);
    }

    public void checkAllPermissions(final Context context) {
        AndPermission.with(context)
                .permission(PERMISSIONS_All_NEED)
                .onDenied(list -> {
                    if (AndPermission.hasAlwaysDeniedPermission(context, PERMISSIONS_All_NEED)) {
                        Toast.makeText(context, "部分功能被禁止，被禁止的功能将无法使用", Toast.LENGTH_SHORT).show();
                        Logger.print("部分功能被禁止");
                        showNormalDialog(MainActivity.this);
                    }
                }).start();
    }


    /**
     * 获取设备当前 OAID
     *
     * @param view
     */
    public void getOAID(View view) {
        Logger.print("DeviceID doGet");
        DeviceID.with(this).doGet(this);
    }

    @Override
    public void onDeviceIdGetComplete(@NonNull String deviceId) {
        Message msg = Message.obtain();
        msg.what = 1;
        msg.obj = deviceId;
        handler.sendMessage(msg);
        Logger.print("onDeviceIdGetComplete====>" + deviceId);
    }

    @Override
    public void onDeviceIdGetError(@NonNull Exception exception) {
        Logger.print("onDeviceIdGetError====>" + exception);
    }

    private void showNormalDialog(final Context context) {
        final AlertDialog.Builder normalDialog =
                new AlertDialog.Builder(context);
        normalDialog.setTitle("去申请权限");
        normalDialog.setMessage("部分权限被你禁止了，可能误操作，可能会影响部分功能，是否去要去重新设置？");
        normalDialog.setPositiveButton("是",
                (dialog, which) -> getAppDetailSettingIntent(context));
        normalDialog.setNegativeButton("否",
                (dialog, which) -> dialog.dismiss());
        normalDialog.show();
    }

    static private void getAppDetailSettingIntent(Context context) {
        Intent localIntent = new Intent();
        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
        localIntent.setData(Uri.fromParts("package", context.getPackageName(), null));
        context.startActivity(localIntent);
    }

}
