# Android_CN_OAID

![Release APK](https://github.com/gzu-liyujiang/Android_CN_OAID/workflows/Release%20APK/badge.svg)
![Gradle Package](https://github.com/gzu-liyujiang/Android_CN_OAID/workflows/Gradle%20Package/badge.svg)

本项目抹平了各大 Android 手机厂商获取 OAID（开放匿名标识）的差异性，轻松通过几句代码即可获取不同手机的 OAID，可作为移动安全联盟官网提供的 SDK 闭源方案（miit_mdid_xxx.aar）的替代方案。

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

在没有用到移动安全联盟 SDK 的情况下，依赖如下：

```groovy
dependencies {
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_ASUS:版本号' //华硕
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_BUN:版本号' //中兴、卓易
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_HEYTAP:版本号' //欧珀、一加
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_SAMSUNG:版本号' //三星
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_UODIS:版本号' //华为
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_ZUI:版本号' //联想、摩托罗拉
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_IMPL:版本号' //具体实现
}
```

若项目中直接或间接地使用了移动安全联盟的 SDK，则可能需要取消相关有冲突的依赖项，例如，取消和“msa_mdid_1.0.22.aar”有冲突的项依赖如下：

```groovy
dependencies {
    //implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_ASUS:版本号'  //华硕
    //implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_BUN:版本号'  //中兴、卓易
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_HEYTAP:版本号'  //欧珀、一加
    //implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_SAMSUNG:版本号'  //三星
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_UODIS:版本号'  //华为
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_ZUI:版本号'  //联想、摩托罗拉
    implementation 'com.github.gzu-liyujiang.Android_CN_OAID:OAID_IMPL:版本号'  //具体实现
}
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
        builder.append("PseudoID: ");
        // 获取伪造ID，根据硬件信息生成，不会为空，有大概率会重复
        builder.append(DeviceID.getPseudoID());
        builder.append("\n");
        builder.append("GUID: ");
        // 获取GUID，随机生成，不会为空
        builder.append(DeviceID.getGUID(this));
        builder.append("\n");
        // 获取OAID，异步回调
        DeviceID.getOAID(this, new IGetter() {
            @Override
            public void onOAIDGetComplete(@NonNull String result) {
                // 不同厂商的OAID格式是不一样的，可进行MD5、SHA1之类的哈希运算统一
                builder.append("OAID: ").append(result);
                tvDeviceIdResult.setText(builder);
            }

            @Override
            public void onOAIDGetError(@NonNull Throwable error) {
                // 获取OAID失败
                builder.append("OAID: ").append(error);
                tvDeviceIdResult.setText(builder);
            }
        });
```

- 用法二：预先获取设备标识符

```text 
    // 在 Application#onCreate 里调用预取
    DeviceID.register(this);
    // 在需要用到设备标识的地方获取
    // 设备标识统一格式为MD5：DeviceID.getClientIdMD5()
    // 设备标识统一格式为SHA1：DeviceID.getClientIdSHA1()
    tvDeviceIdResult.setText(String.format("DeviceID: %s", DeviceID.getClientId()));
```

## 混淆规则

本库自带`consumer-rules.pro`混淆规则，不混淆厂商的相关接口及类。若通过远程依赖的方式应用，则无需进行额外配置：

```proguard
-keep class com.asus.msa.SupplementaryDID.** { *; }
-keep interface com.asus.msa.SupplementaryDID.** { *; }
-keep class com.bun.lib.** { *; }
-keep interface com.bun.lib.** { *; }
-keep class com.heytap.openid.** { *; }
-keep interface com.heytap.openid.** { *; }
-keep class com.samsung.android.deviceidservice.** { *; }
-keep interface com.samsung.android.deviceidservice.** { *; }
-keep class com.uodis.opendevice.aidl.** { *; }
-keep interface com.uodis.opendevice.aidl.** { *; }
-keep class com.zui.deviceidservice.** { *; }
-keep interface com.zui.deviceidservice.** { *; }
```

## 厂商支持

| 厂商                 | 版本                 |
| -------------------- | -------------------- |
| 小米（Xiaomi）       | MIUI 10.2 及以上     |
| 黑鲨（BlackShark）   | MIUI 10.2 及以上     |
| 维沃（VIVO）         | Funtouch OS 9 及以上 |
| 华为（Huawei）       | HMS 2.6.2 及以上     |
| 欧珀（OPPO）         | Color OS 7.0 及以上  |
| 联想（Lenovo）       | ZUI 11.4 及以上      |
| 摩托罗拉（Motorola） | ZUI 11.4 及以上      |
| 华硕（ASUS）         | Android 10 及以上    |
| 魅族（Meizu）        | Android 10 及以上    |
| 三星（Samsung）      | Android 10 及以上    |
| 努比亚（Nubia）      | Android 10 及以上    |
| 一加（OnePlus）      | Android 10 及以上    |
| 中兴（ZTE）          | Android 10 及以上    |
| 卓易（Freeme OS）    | Android 10 及以上    |

> 注：本项目的 OAID 获取接口主要参考北京数字联盟公开的代码并逆向分析参考移动安全联盟的 SDK，酷派、乐视、真我、锤子等厂商截止目前（2021.01.19）并未见到移动安全联盟有支持，也未查阅到厂商相关公开资料，需自行生成 GUID。

## 效果预览

- ![支持OAID的情况](/screenshot/oaid_vivo.png)
- ![支持OAID的情况](/screenshot/oaid_huawei.png)
- ![支持OAID的情况](/screenshot/oaid_xiaomi.png)
- ![支持OAID的情况](/screenshot/oaid_meizu.png)
- ![不支持OAID的情况](/screenshot/oaid_360.png)
- ![不支持OAID的情况](/screenshot/oaid_samsung.png)
- ![不支持OAID的情况](/screenshot/oaid_simulator.png)

## 参考资料

OAID 即 Open Anonymous Identifier，开放匿名标识符，是移动智能终端补充设备标识体系中的一员。

- [移动安全联盟统一 SDK 下载](https://github.com/2tu/msa) （from http://www.msa-alliance.cn）。
- 谷歌官方文档 [使用标识符的最佳做法](https://developer.android.google.cn/training/articles/user-data-ids) 。
- [团体标准-移动智能终端补充设备标识规范-v20190516.pdf](http://www.msa-alliance.cn/login.jsp?url=%2Fcol.jsp%3Fid%3D120&errno=11&mid=634&fid=ABUIABA9GAAgpKaN6QUoq7em2QI) 。
- 华为官方文档 [《获取 OAID 信息（SDK 方式）》](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/identifier-service-obtaining-oaid-sdk-0000001050064988-V5) 。
- Flyme SDK [移动智能终端补充设备标识](http://open-wiki.flyme.cn/doc-wiki/index#id?133) 。
- 北京数字联盟公开的获取各厂商 OAID 的简易代码：[Get_Oaid_CNAdid](https://github.com/shuzilm-open-source/Get_Oaid_CNAdid)。
- 获取或生成设备唯一标识后，推荐参考“[一种 Android 移动设备构造 UDID 的方案](https://github.com/No89757/Udid) ”。
- StackOverFlow [Is there a unique Android device ID ?](https://stackoverflow.com/questions/2785485/is-there-a-unique-android-device-id) 。
- 「使用心得」[极光推送的设备唯一性标识 RegistrationID](https://community.jiguang.cn/article/38100) 。

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
