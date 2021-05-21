# 更新日志

## latest

- 代码质量提升（Powered by Gitee Scan）：
```text
Catch Exception instead of Throwable.
Either log or rethrow this exception.
Define and throw a dedicated exception instead of using a generic one.
Change this "try" to a try-with-resources.
Use the constant instead the magic number.
Convert this usage of the ternary operator to an "if"/"else" structure.
"if ... else if" constructs should end with "else" clauses.
Refactor this code to not nest more than 3 if/for/while/switch/try statements.
Refactor this method to reduce its Cognitive Complexity from xxx to the 15 allowed.
Reduce this anonymous class number of lines from xxx to at most 20, or make it a named class.
This block of commented-out lines of code should be removed.
Split this xxx characters long line (which is greater than 120 authorized).
```

## 4.1.2 - 2021/05/20

- 精简代码，优化打印调试日志。
- 修复 Android 6.0 以下系统版本获取 GUID 闪退（NoSuchMethodError: No static method canWrite...）。
- 改进 Android 6.0 以下系统版本 GUID 持久化、稳定性。

## 4.1.1 - 2021/05/15

- 加入 `READ_PHONE_STATE`及`WRITE_EXTERNAL_STORAGE` 权限以便适配低版本安卓系统。

## 4.1.0 - 2021/05/14

- 支持中兴及 Motorola、HTC、LG、Sony 等海外平台手机获取 AAID（安卓广告标识符）。
- 华为 OAID 获取优化，支持大屏电视。
- 修复努比亚支持情况识别。
- 处理用户关闭了广告标识符的情况。
- 通过云真机在线调试，更新更多真机实测效果图。

## 4.0.1 - 2021/05/13

- 修复联想、中兴、华硕等 OAID 获取。
- 增加更多品牌真机实测效果图。

## 4.0.0 - 2021/04/29

- **重新打造与移动安全联盟 SDK 的共存方案**，避免因此造成`Issues#22`之类的问题。
- 增强 GUID 在 Android 10 以下版本系统的上的稳定性。
- 避免可能会意外发生的未知异常（`Issues#21`、`PR#23`）。

## 3.0.3 - 2021/04/14

- 更细致的识别手机厂商及其品牌，增加荣耀、红米、爱酷、真我等品牌的支持。

## 3.0.2 - 2021/04/08

- 小米手机获取 OAID 问题（`PR#18`）。
- 增加数字版权管理设备 ID （注：根据`semver.org`语义化版本规范，此处做了向下兼容的功能性新增，版本号其实应该定为`3.1.0`）。
- 增加各大手机厂商关于 OAID 的说明文档。
- 允许判断设备是否支持 OAID（`PR#19`）。

## 3.0.1 - 2021/03/29

- 增加设备标识符统一格式为 MD5 或 SHA1 的方法

## 3.0.0 - 2021/03/26

- 移除之前版本已废弃的方法，重构部分 API。
- 支持在应用启动时预先获取设备的客户端标识。
- 增加 JavaDoc ，更新说明文档。
- 移除 Github Actions 的 CodeQL。

## 2.1.1 - 2021/03/03

- 修复魅族手机获取 OAID 失败问题，增加魅族效果图。

## 2.1.0 - 2021/01/19

- 增加 IMEI/MEID、AndroidID、GUID 等获取方法。
- 优化文档及调整其他一些细节。

## 2.0.0 - 2021/01/13

- 修复 Demo 编译错误（`Isuues#8`）。
- 分包，解决和移动安全联盟 SDK 冲突问题。
- 引入华为云真机调试方案。
- 修正文档错误。
- 修复 Github Actions 错误。

## 1.0.2 - 2020/08/20

- 规避可能的闪退。
- 使用 Github Actions 代替 Travis-CI。
- 更新说明文档。

## 1.0.1 - 2020/07/15

- 取消不支持 OAID 时默认生成的 GUID。

## 1.0.0 - 2020/05/30

- 初始版本。
