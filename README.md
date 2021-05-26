# Android_CN_OAID

![Release APK](https://github.com/gzu-liyujiang/Android_CN_OAID/workflows/Release%20APK/badge.svg)
![Gradle Package](https://github.com/gzu-liyujiang/Android_CN_OAID/workflows/Gradle%20Package/badge.svg)

安卓设备唯一标识解决方案，可作为移动安全联盟统一 SDK （miit_mdid_xxx.aar）的替代方案。本项目提供了国内各大手机厂商获取 OAID（开放匿名设备标识）及海外手机平台获取 AAID （安卓广告标识）的便携接口，另外也提供了 IMEI/MEID、AndroidID、WidevineID、PseudoID、GUID 等常见的设备标识的获取方法。

- GitHub：https://github.com/gzu-liyujiang/Android_CN_OAID
- 码云(GitEE)：https://gitee.com/li_yu_jiang/Android_CN_OAID

## 接入指引

最新版本：[![jitpack](https://jitpack.io/v/gzu-liyujiang/Android_CN_OAID.svg)](https://jitpack.io/#gzu-liyujiang/Android_CN_OAID) （[更新日志](/CHANGELOG.md) | [JavaDoc](https://gzu-liyujiang.github.io/Android_CN_OAID/)）

### 依赖配置

```groovy
allprojects {
    repositories {
        maven { url 'https://www.jitpack.io' }
    }
}
```

**4.0.0 版本以后重新调整了与移动安全联盟 SDK 共存的方案** ，直接使用如下依赖即可：

```groovy
dependencies {
    implementation 'com.github.gzu-liyujiang:Android_CN_OAID:<version>'
}
```

**对于 4.0.0 以前的版本** ，若项目中直接或间接地使用了移动安全联盟的 SDK，则可能需要取消相关有冲突的依赖项：

```groovy
dependencies {
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_IMPL:3.0.3'  //具体实现，必须
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_ASUS:3.0.3'  //华硕，有冲突时请注释掉
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_BUN:3.0.3'  //中兴、卓易，有冲突时请注释掉
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_HEYTAP:3.0.3'  //欧珀、一加，有冲突时请注释掉
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_SAMSUNG:3.0.3'  //三星，有冲突时请注释掉
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_ZUI:3.0.3'  //联想、摩托罗拉，有冲突时请注释掉
}
```

自 **4.1.1** 开始默认加入了 `READ_PHONE_STATE`、`WRITE_SETTINGS`及`WRITE_EXTERNAL_STORAGE` 权限以便适配低版本安卓系统。
为**遵循最小必要原则**保护用户隐私，若项目中没用到 IMEI 及 GUID，那么可酌情在 `AndroidManifest.xml` 中加入如下代码移除相关权限：

```xml
<manifest>
    <uses-permission
        android:name="android.permission.READ_PHONE_STATE"
        tools:node="remove" />
    <uses-permission
        android:name="android.permission.WRITE_SETTINGS"
        tools:node="remove" />
</manifest>
```
若 Android 10+ 也页需要 `READ_PHONE_STATE` 及 `WRITE_EXTERNAL_STORAGE` 权限的话，可通过下面这个覆盖本库声明的：
```xml
    <uses-permission
        android:name="android.permission.READ_PHONE_STATE"
        tools:node="replace" />
    <uses-permission
        android:name="android.permission.READ_EXTERNAL_STORAGE"
        tools:node="replace" />
    <uses-permission
        android:name="android.permission.WRITE_EXTERNAL_STORAGE"
        tools:node="replace" />
```

### 代码示例

获取多个可能的设备标识，结合服务端引入[拜占庭容错方案](https://juejin.cn/post/6844903952148856839#heading-11)得到可靠的稳定的设备唯一标识：

- 用法一：实时获取设备标识符

```text
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
        String widevineID = DeviceID.getWidevineID(this);
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
        builder.append("supported:").append(DeviceID.supportedOAID(this));
        builder.append("\n");
        // 获取OAID/AAID，异步回调
        DeviceID.getOAID(this, new IGetter() {
            @Override
            public void onOAIDGetComplete(@NonNull String result) {
                // 不同厂商的OAID/AAID格式是不一样的，可进行MD5、SHA1之类的哈希运算统一
                builder.append("OAID/AAID: ").append(result);
                tvDeviceIdResult.setText(builder);
            }

            @Override
            public void onOAIDGetError(@NonNull Exception error) {
                // 获取OAID/AAID失败
                builder.append("OAID/AAID: 失败，").append(error);
                tvDeviceIdResult.setText(builder);
            }
        });
```

- 用法二：预先获取设备标识符（**建议不要和用法一同时存在**）

```text
    // 在 Application#onCreate 里调用预取。注意：如果不需要调用`getClientId()`及`getOAID()`，请不要调用这个方法
    DeviceID.register(this);
    // 在需要用到设备标识的地方获取
    // 客户端标识原始值：DeviceID.getClientId()
    // 客户端标识统一格式为MD5：DeviceID.getClientIdMD5()
    // 客户端标识统一格式为SHA1：DeviceID.getClientIdSHA1()
    // 开放匿名设备标识原始值：DeviceID.getOAID()
  tvDeviceIdResult.setText(String.format("ClientID: %s", DeviceID.getClientIdMD5()));
```

## 混淆规则

本库自带`consumer-rules.pro`如下混淆规则，不混淆厂商的相关接口及类。若通过远程依赖的方式引用，则无需进行额外配置：

```proguard
-keep class repeackage.com.uodis.opendevice.aidl.** { *; }
-keep interface repeackage.com.uodis.opendevice.aidl.** { *; }
-keep class repeackage.com.asus.msa.SupplementaryDID.** { *; }
-keep interface repeackage.com.asus.msa.SupplementaryDID.** { *; }
-keep class repeackage.com.bun.lib.** { *; }
-keep interface repeackage.com.bun.lib.** { *; }
-keep class repeackage.com.heytap.openid.** { *; }
-keep interface repeackage.com.heytap.openid.** { *; }
-keep class repeackage.com.samsung.android.deviceidservice.** { *; }
-keep interface repeackage.com.samsung.android.deviceidservice.** { *; }
-keep class repeackage.com.zui.deviceidservice.** { *; }
-keep interface repeackage.com.zui.deviceidservice.** { *; }
```

## 支持情况

| 厂商或品牌                        | 系统或框架                                               |
| --------------------------------- | -------------------------------------------------------- |
| 华为（HuaWei、Honor）             | HMS Core 2.6.2+ 、Google Play Service 4.0+               |
| 小米（XiaoMi、RedMi、BlackShark） | MIUI 10.2+、Google Play Service 4.0+                     |
| 维沃（VIVO、IQOO）                | Funtouch OS 9+、Origin OS 1.0+、Google Play Service 4.0+ |
| 欧珀（OPPO、RealMe）              | Color OS 7.0+、Google Play Service 4.0+                  |
| 三星（Samsung）                   | Android 10+、Google Play Service 4.0+                    |
| 联想（Lenovo）                    | ZUI 11.4+、Google Play Service 4.0+                      |
| 华硕（ASUS）                      | Android 10+、Google Play Service 4.0+                    |
| 魅族（Meizu）                     | Android 10+、Google Play Service 4.0+                    |
| 一加（OnePlus）                   | Android 10+、Google Play Service 4.0+                    |
| 努比亚（Nubia）                   | Android 10+、Google Play Service 4.0+                    |
| 其他（ZTE、HTC、Motorola、……）    | Freeme OS、SSUI、Google Play Service 4.0+                |

> 注：本项目的 OAID 获取接口主要参考北京数字联盟公开的代码以及逆向分析参考移动安全联盟的 SDK、HUAWEI Ads SDK、小米 DeviceId.jar、Google Play Services SDK 等。

## 效果预览

### OAID（开放匿名设备标识符）

- 华为手机 ![](/screenshot/oaid_huawei.png)
- 荣耀手机 ![](/screenshot/oaid_honor.png)
- 小米手机 ![](/screenshot/oaid_xiaomi.png)
- 红米手机 ![](/screenshot/oaid_redmi.png)
- 黑鲨手机 ![](/screenshot/oaid_blackshark.png)
- 维沃手机 ![](/screenshot/oaid_vivo_iqoo.png)
- 欧珀手机 ![](/screenshot/oaid_oppo.png)
- 真我手机 ![](/screenshot/oaid_realme.png)
- 三星手机 ![](/screenshot/oaid_samsung.png)
- 魅族手机 ![](/screenshot/oaid_meizu.png)
- 联想手机 ![](/screenshot/oaid_lenovo.png)
- 一加手机 ![](/screenshot/oaid_oneplus.png)
- 华硕手机 ![](/screenshot/oaid_asus.png)
- 努比亚机 ![](/screenshot/oaid_nubia.png)

### AAID（安卓广告标识符）

- 谷歌手机 ![](/screenshot/aaid_google.png)
- 中兴手机 ![](/screenshot/aaid_zte.png)
- 摩托罗拉 ![](/screenshot/aaid_motorola.png)
- 索尼手机 ![](/screenshot/aaid_sony.png)
- 诺基亚机 ![](/screenshot/aaid_nokia.png)
- HTC 手机 ![](/screenshot/aaid_htc.png)
- LG 手机 ![](/screenshot/aaid_lge.png)

### 不支持 OAID 或 AAID

- 用户关闭广告标识符 ![](/screenshot/oaid_disabled.png)

```text
锤子（Smartisan）、酷派（Yulong,Coolpad）、360（360）、奇酷（QiKu）、海信（Hisense）、金立（Gionee）、
美图（Meitu）、糖果（SOAP）、格力（Gree）、朵唯（Doov）、优思（Uniscope）、夏普（SHARP）、乐视（LeTV）、
维图（VOTO）、宏碁（Acer）、TCL（TCL）、……
```

## 参考资料

OAID 是移动智能终端补充设备标识体系中的一员，官方定义为 Open Anonymous Device Identifier（开放匿名设备标识符），
华为称之为 Open Advertising ID （开放广告标识符），谷歌称之为 Android Advertising ID （安卓广告标识符）。

- [移动安全联盟统一 SDK 下载](https://github.com/2tu/msa) （from http://www.msa-alliance.cn ）。
- 谷歌官方文档 [使用标识符的最佳做法](https://developer.android.google.cn/training/articles/user-data-ids) 。
- [团体标准-移动智能终端补充设备标识规范-v20190516.pdf](https://swsdl.vivo.com.cn/appstore/developer/uploadfile/20191109/uohz59/%E5%9B%A2%E4%BD%93%E6%A0%87%E5%87%86-%E7%A7%BB%E5%8A%A8%E6%99%BA%E8%83%BD%E7%BB%88%E7%AB%AF%E8%A1%A5%E5%85%85%E8%AE%BE%E5%A4%87%E6%A0%87%E8%AF%86%E8%A7%84%E8%8C%83-v20190516.pdf) 。
- 华为 [开放广告标识符（OAID）](https://developer.huawei.com/consumer/cn/codelab/HMSAdsOAID/index.html#0) 。
- 小米 [设备标识体系说明](https://dev.mi.com/console/doc/detail?pId=1821) 。
- 魅族 [移动智能终端补充设备标识](http://open-wiki.flyme.cn/doc-wiki/index#id?133) 。
- 维沃 [移动智能终端补充设备标识服务](https://dev.vivo.com.cn/documentCenter/doc/253) 。
- 欧珀 [移动智能终端补充设备标识体系](https://open.oppomobile.com/wiki/doc#id=10608) 。
- 三星 [适配指导 | Android Q Device ID 权限变更](https://support-cn.samsung.com/App/DeveloperChina/notice/detail?noticeid=115) 。
- 北京数字联盟公开的获取各厂商 OAID 的简易代码：[Get_Oaid_CNAdid](https://github.com/shuzilm-open-source/Get_Oaid_CNAdid)。
- 获取或生成设备唯一标识后，推荐参考“[一种 Android 移动设备构造 UDID 的方案](https://github.com/No89757/Udid) ”。
- StackOverFlow [Is there a unique Android device ID ?](https://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id) 。

## 远程真机

- 免费 [华为远程真机云调试](https://developer.huawei.com/consumer/cn/agconnect/cloud-adjust) 。
- 免费 [小米云测平台远程真机租用](https://testit.miui.com/remote) 。
- 免费 [VIVO 云测平台远程真机](https://vcl.vivo.com.cn/#/machine/picking) 。
- 免费 [OPPO 云测平台远程真机](https://open.oppomobile.com/cloudmachine/device/list-plus) 。
- 免费 [三星远程开发测试平台真机调试](http://samsung.smarterapps.cn/index.php?app=home&mod=Index&act=samsung) 。
- 新人试用 ~~腾讯 WeTest 云真机调试、阿里 EMAS 移动测试远程真机、百度 MTC 远程真机调试、Testin 远程真机测试、AllTesting 真机测试~~ 。

## 许可协议

```text
Copyright (c) 2019-2021 gzu-liyujiang <1032694760@qq.com>

The software is licensed under the Mulan PSL v2.
You can use this software according to the terms and conditions of the Mulan PSL v2.
You may obtain a copy of Mulan PSL v2 at:
    http://license.coscl.org.cn/MulanPSL2
THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
PURPOSE.
See the Mulan PSL v2 for more details.
```
