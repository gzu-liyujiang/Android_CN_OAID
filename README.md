# Android_CN_OAID

[![travis-ci](https://travis-ci.org/gzu-liyujiang/Android_CN_OAID.svg?branch=master)](https://travis-ci.org/gzu-liyujiang/Android_CN_OAID)
[![jitpack](https://jitpack.io/v/gzu-liyujiang/Android_CN_OAID.svg)](https://jitpack.io/#gzu-liyujiang/Android_CN_OAID)

适用于国内各大Android手机厂商的开放匿名设备标识（OAID）、全局唯一标识（GUID）解决方案，本项目基于[Get_Oaid_CNAdid](https://github.com/shuzilm-open-source/Get_Oaid_CNAdid)作了重构，使用AIDL，增强易用性及健壮性。

![支持OAID的情况](/screenshot/oaid.jpg)
![不支持OAID的情况](/screenshot/guid.jpg)

## 背景
   
### 国家政策
   
 随着人工智能的发展，数据的价值逐渐增加，国家对用户隐私安全保护的要求越来越高。为解决移动互联网环境下涉及个人隐私的设备标识滥用问题，并在商业价值和隐私保护合规中找到平衡，2019年7月，中国信息通信研究院泰尔终端实验室携手行业终端厂商共同建立《移动智能终端补充设备标识规范》，进一步完善智能终端领域用户隐私安全保护体系。
   
### Android Q( Android 10)
   
Android Q（Android 10），禁止第三方应用读取设备唯一标识(IMEI,MAC等)。
    
### 欧洲GDPR
    
2018年5月25日，欧洲联盟出台《通用数据保护条例》（General Data Protection Regulation，简称GDPR），在相关权威的释法解读中，设备ID（IMEI等)被定义为个人数据。
   
## OAID

OAID 即 Open Anonymous Device Identifier，开放匿名设备标识符，根据该联盟公布在网上的《移动智能终端补充设备标识规范》“旨在规范移动智能终端补充设备标识体系的体系架构、功能要求、接口要求以及安全要求。 **规范设备生产企业遵循标准要求开发统一接口调用方式，方便移动应用接入、减小维护成本”**。因此该联盟及者联盟单位必须将统一的 OAID 调用方式公布出来，这也是“中华人民共和国标准化法”的法律要求。事实上，除非是**企业内部标准**，其他标准都**必须**公开。

### **根据标准法的第二十二条：**

制定标准应当有利于科学合理利用资源，推广科学技术成果，增强产品的安全性、通用性、可替换性，提高经济效益、社会效益、生态效益，做到技术上先进、经济上合理。 **禁止利用标准实施妨碍商品、服务自由流通等排除、限制市场竞争的行为。**

### 匿名设备标识符特性

发生下述事件时，OAID（匿名设备标识符）重置:

- (1) 用户在系统设置中手动重置，匿名设备标识符将重置;
- (2) 移动智能终端恢复出厂设置时，匿名设备标识符将重置;
- (3) 匿名设备标识符自身可定期重置。 重置后生成新的匿名设备标识符，且应用只能获取新的匿名设备标识符。

**匿名设备标识符的开启\关闭受控机制**
移动智能终端应提供匿名设备标识符的开启\关闭受控机制，用户可以选择在系统设置中关闭匿名设备标识符。关闭后，应用获取到的匿名设备标识符的返回值为 NO。

**请注意：**OAID 与IMEI 不同，IMEI 为设备标识，对于相同设备在不被篡改时，不发生变化。OAID 为广告标识，同一台设备在不同时间内，可以拥有不同的的 OAID。

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
| Ssui OS | Android 10 版本                       |
