# Android_CN_OAID

![Release APK](https://github.com/gzu-liyujiang/Android_CN_OAID/workflows/Release%20APK/badge.svg)
![Gradle Package](https://github.com/gzu-liyujiang/Android_CN_OAID/workflows/Gradle%20Package/badge.svg)

本项目抹平了各大 Android 手机厂商获取 OAID（开放匿名设备标识）的差异性，轻松通过几句代码即可获取不同手机的 OAID，可作为移动安全联盟官网提供的 SDK 闭源方案（miit_mdid_xxx.aar）的替代方案。另外也提供了 IMEI/MEID、AndroidID、WidevineID、PseudoID、GUID 等常见的设备标识的获取方法。

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
    implementation 'com.github.gzu-liyujiang:Android_CN_OAID:4.0.0'
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
        // 是否支持OAID
        builder.append("supportedOAID:").append(DeviceID.supportedOAID(this));
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
                builder.append("OAID: 失败，").append(error);
                tvDeviceIdResult.setText(builder);
            }
        });
```

- 用法二：预先获取设备标识符（**建议不要和用法一同时存在**）

```text
    // 在 Application#onCreate 里调用预取
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

## 厂商支持

| 厂商                     | 版本                            |
| ------------------------ | ------------------------------- |
| 小米（Xiaomi，Redmi）    | MIUI 10.2 及以上                |
| 黑鲨（BlackShark）       | MIUI 10.2 及以上                |
| 维沃（VIVO，IQOO）       | Funtouch OS 9 及以上，Origin OS |
| 华为（Huawei，Honor）    | HMS 2.6.2 及以上                |
| 欧珀（OPPO，Realme）     | Color OS 7.0 及以上             |
| 联想（Lenovo）           | ZUI 11.4 及以上                 |
| 摩托罗拉（Motorola）     | ZUI 11.4 及以上                 |
| 华硕（ASUS）             | Android 10 及以上               |
| 魅族（Meizu）            | Android 10 及以上               |
| 三星（Samsung）          | Android 10 及以上               |
| 努比亚（Nubia）          | Android 10 及以上               |
| 一加/万普拉斯（OnePlus） | Android 10 及以上               |
| 中兴（ZTE）              | Android 10 及以上               |
| 卓易（FreemeOS）         | Android 10 及以上               |

> 注：本项目的 OAID 获取接口主要参考北京数字联盟公开的代码并逆向分析参考移动安全联盟的 SDK，酷派、乐视、锤子、360 等厂商截止目前（2021.04.14）并未见到移动安全联盟有支持，也未查阅到厂商相关公开资料，只能生成 GUID 代替。

## 效果预览

- ![支持OAID的情况](/screenshot/oaid_vivo.png)
- ![支持OAID的情况](/screenshot/oaid_oppo.png)
- ![支持OAID的情况](/screenshot/oaid_huawei.png)
- ![支持OAID的情况](/screenshot/oaid_xiaomi.png)
- ![支持OAID的情况](/screenshot/oaid_meizu.png)
- ![支持OAID的情况](/screenshot/oaid_realme.png)
- ![支持OAID的情况](/screenshot/oaid_lenovo.png)
- ![不支持OAID的情况](/screenshot/oaid_360.png)
- ![不支持OAID的情况](/screenshot/oaid_samsung.png)
- ![不支持OAID的情况](/screenshot/oaid_coolpad.png)
- ![不支持OAID的情况](/screenshot/oaid_simulator.png)

## 参考资料

OAID 即 Open Anonymous Device Identifier，开放匿名设备标识符，是移动智能终端补充设备标识体系中的一员。

- [移动安全联盟统一 SDK 下载](https://github.com/2tu/msa) （from http://www.msa-alliance.cn）。
- 谷歌官方文档 [使用标识符的最佳做法](https://developer.android.google.cn/training/articles/user-data-ids) 。
- [团体标准-移动智能终端补充设备标识规范-v20190516.pdf](https://swsdl.vivo.com.cn/appstore/developer/uploadfile/20191109/uohz59/%E5%9B%A2%E4%BD%93%E6%A0%87%E5%87%86-%E7%A7%BB%E5%8A%A8%E6%99%BA%E8%83%BD%E7%BB%88%E7%AB%AF%E8%A1%A5%E5%85%85%E8%AE%BE%E5%A4%87%E6%A0%87%E8%AF%86%E8%A7%84%E8%8C%83-v20190516.pdf) 。
- 华为 [《获取 OAID 信息（SDK 方式）》](https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides-V5/identifier-service-obtaining-oaid-sdk-0000001050064988-V5) 。
- 小米 [设备标识体系说明](https://dev.mi.com/console/doc/detail?pId=1821) 。
- 魅族 [移动智能终端补充设备标识](http://open-wiki.flyme.cn/doc-wiki/index#id?133) 。
- 维沃 [移动智能终端补充设备标识服务](https://dev.vivo.com.cn/documentCenter/doc/253) 。
- 欧珀 [移动智能终端补充设备标识体系](https://open.oppomobile.com/wiki/doc#id=10608) 。
- 三星 [适配指导 | Android Q Device ID 权限变更](https://support-cn.samsung.com/App/DeveloperChina/notice/detail?noticeid=115) 。
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
