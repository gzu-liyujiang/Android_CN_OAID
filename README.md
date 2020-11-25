# Android_CN_OAID

![Release APK](https://github.com/gzu-liyujiang/Android_CN_OAID/workflows/Release%20APK/badge.svg)
![Gradle Package](https://github.com/gzu-liyujiang/Android_CN_OAID/workflows/Gradle%20Package/badge.svg)
[![jitpack](https://jitpack.io/v/gzu-liyujiang/Android_CN_OAID.svg)](https://jitpack.io/#gzu-liyujiang/Android_CN_OAID)

本项目用于获取国内各大Android手机厂商的开放匿名设备标识（OAID），类似于移动安全联盟官网提供的统一SDK闭源方案（miit_mdid_xxx.aar），参考[Get_Oaid_CNAdid](https://github.com/shuzilm-open-source/Get_Oaid_CNAdid)源码进行重写，还原AIDL，增强易用性及健壮性。获取或生成设备唯一标识后，推荐参考“[一种Android移动设备构造UDID的方案](https://github.com/No89757/Udid) ”，客户端结合服务端进行设备唯一标识处理以提升唯一性和稳定性。

```gradle
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}

dependencies {
    implementation 'com.github.gzu-liyujiang:Android_CN_OAID:版本号'
}
```
```groovy
        IDeviceId deviceId = DeviceID.with(this);
        if (!deviceId.supportOAID()) {
            // 不支持OAID，须自行生成全局唯一标识（GUID）。
            // 本库不提供GUID的生成方式，可以使用`UUID.randomUUID().toString()`生成，
            // 然后存到`SharedPreferences`及`ExternalStorage`甚至`CloudStorage`。
            return;
        }
        deviceId.doGet(new IOAIDGetter() {
            @Override
            public void onOAIDGetComplete(@NonNull String oaid) {
                
            }

            @Override
            public void onOAIDGetError(@NonNull Exception exception) {

            }
        });
```
本库自带`consumer-rules.pro`混淆规则，若通过远程依赖的方式应用，则无需进行额外配置：
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

![支持OAID的情况](/screenshot/oaid.jpg)
![不支持OAID的情况](/screenshot/guid.jpg)

## 背景
   
### 国家政策
   
 随着人工智能的发展，数据的价值逐渐增加，国家对用户隐私安全保护的要求越来越高。为解决移动互联网环境下涉及个人隐私的设备标识滥用问题，并在商业价值和隐私保护合规中找到平衡，2019年7月，中国信息通信研究院泰尔终端实验室携手行业终端厂商共同建立[《移动智能终端补充设备标识规范》](http://msa-alliance.cn/col.jsp?id=120)，进一步完善智能终端领域用户隐私安全保护体系。
   
### Android Q( Android 10)
   
Android Q（Android 10），禁止第三方应用读取设备唯一标识(IMEI,MAC等)。
    
### 欧洲GDPR
    
2018年5月25日，欧洲联盟出台《通用数据保护条例》（General Data Protection Regulation，简称GDPR），在相关权威的释法解读中，设备ID（IMEI等)被定义为个人数据。
   
## OAID

OAID 即 Open Anonymous Device Identifier，开放匿名设备标识符，根据移动安全联盟公布在网上的《移动智能终端补充设备标识规范》“旨在规范移动智能终端补充设备标识体系的体系架构、功能要求、接口要求以及安全要求。 **规范设备生产企业遵循标准要求开发统一接口调用方式，方便移动应用接入、减小维护成本”**。因此该联盟及联盟单位必须将统一的 OAID 调用方式公布出来，这也是“中华人民共和国标准化法”的法律要求。事实上，**除非是企业内部标准，其他标准都必须公开**。

### **根据“标准法”的第二十二条：**

制定标准应当有利于科学合理利用资源，推广科学技术成果，增强产品的安全性、通用性、可替换性，提高经济效益、社会效益、生态效益，做到技术上先进、经济上合理。 **禁止利用标准实施妨碍商品、服务自由流通等排除、限制市场竞争的行为。**

### 支持OAID的厂商及其系统版本

| 厂商   | 版本                                    |
| ------ |  ------------------------------------- |
| 小米   | MIUI10.2 及以上                         |
| vivo   | FuntouchOS 9 及以上                     |
| 华为   | 全版本                                  |
| OPPO   | Color OS 7.0 及以上                     |
| Lenovo | ZUI 11.4 及以上                         |
| 华硕   | Android 10 版本                         |
| 魅族   | Android 10 版本                         |
| 三星   | Android 10 版本                         |
| 努比亚 | Android 10 版本                         |
| 中兴   | Android 10 版本                         |
| 一加   | Android 10 版本                         |
| Freeme OS   | Android 10 版本                   |
| SSUI OS | Android 10 版本                       |

## 使用标识符的最佳做法

参阅谷歌官方文档：https://developer.android.google.cn/training/articles/user-data-ids 。在使用 Android 标识符时，请遵循以下最佳做法：

- **避免使用硬件标识符**。 在大多数用例中，您可以避免使用硬件标识符，例如 SSAID (Android ID) 和 IMEI，而不会限制所需的功能。
- 自 Android 10（API 级别 29）起，您的应用必须是设备或个人资料所有者应用，具有特殊运营商许可，或具有 READ_PRIVILEGED_PHONE_STATE 特权，才能访问不可重置的设备标识符。
- **只针对用户分析或广告用例使用广告 ID**。 在使用广告 ID 时，请始终遵循用户关于广告跟踪的选择。此外，请确保标识符无法关联到个人身份信息 (PII)，并避免桥接广告 ID 重置。
- 尽一切可能针对防欺诈支付和电话以外的所有其他用例**使用实例 ID 或私密存储的 GUID**。 对于绝大多数非广告用例，使用实例 ID 或 GUID 应该足矣。
- 使用适合您的用例的 API 以**尽量降低隐私权风险**。 使用 DRM API 保护重要内容，并使用 SafetyNet API 防止滥用行为。SafetyNet API 是能够确定设备真伪而不会招致隐私权风险的最简单方法。

## License

```text
Copyright (c) 2019-2020 gzu-liyujiang <1032694760@qq.com>

The software is licensed under the Mulan PSL v1.
You can use this software according to the terms and conditions of the Mulan PSL v1.
You may obtain a copy of Mulan PSL v1 at:
    http://license.coscl.org.cn/MulanPSL
THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR
PURPOSE.
See the Mulan PSL v1 for more details.
```
