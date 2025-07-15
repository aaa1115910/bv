<div align="center">

<img src="app/shared/src/main/res/drawable/ic_banner.webp" style="border-radius: 24px; margin-top: 32px;"/>

# BV

~~Bug Video~~

[![Android Sdk Require](https://img.shields.io/badge/Android-5.0%2B-informational?logo=android)](https://apilevels.com/#:~:text=Jetpack%20Compose%20requires%20a%20minSdk%20of%2021%20or%20higher)
[![GitHub](https://img.shields.io/github/license/aaa1115910/bv)](https://github.com/aaa1115910/bv)

**BV 无法在中国大陆地区内的智能电视上使用，如有相关使用需求请使用 [云视听小电视](https://app.bilibili.com)**

**禁止在中国境内传播、宣传、分发 BV**

</div>

---
BV ~~(Bug Video)~~ 是一款 [哔哩哔哩](https://www.bilibili.com) 的第三方应用，适配 `Android 移动端`
和 `Android TV`，使用 `Jetpack Compose` 开发

**都是随心乱写的代码，能跑就行。**

## 特色

- :bug: 丰富多样的 Bug
- :children_crossing: 反人类设计
- :zap: 卡卡卡卡卡
- :art: 异样审美
- :disappointed: 巨难用

---

<div align="center">

# 学废了

</div>

## 声明

**此项目是个人为了学习安卓开发而fork, 仅用于学习和测试，禁止在中国境内传播、宣传、分发，如有相关使用需求请使用 [哔哩哔哩官方APP](https://app.bilibili.com)，否则后果自负**

## 修改
在原bv的基础上做了一些修改，包括：
- UGC视频详情页增加点赞、投币功能
- 把“浏览历史、我的收藏、我的追番、稍后再看”整合到“首页”下面
- 增加“首页默认标签”设置 （设置-界面设置，默认“推荐”）
  - 可以修改打开应用时首页默认选中的标签
- 增加是否“显示UGC视频详情页” （设置-更多设置，默认开启）
  - 关闭后会跳过UGC视频详情页，点击视频卡片直接开始播放
- 增加设置“播放器显示视频调试信息”（设置-更多设置，默认不显示）
- 增加设置“设置竖屏视频播放时的最大清晰度为1080P”（设置-更多设置，默认禁用）
  - 开启可解决部分设备竖屏视频变形/花屏的问题
- 增加是否自动“播放下一个视频”（设置-更多设置，默认开启）
- 增加是否“都播完后退出播放器”（设置-更多设置，默认开启）
- 增加默认播放速度配置（设置-更多设置，默认1倍）
- 增加快进/快退时间间隔配置（设置-更多设置，默认10秒）
- 增加是否显示“播放器底部常驻进度条”配置（设置-更多设置，默认不显示）
- 优化列表、优化视频卡片、精简动画、增加数据缓存、减少非必要的请求
- 按自己的喜好调整页面的布局、元素大小、交互方式、原有功能
- 解决一些bug等等

  [修改内容](./CHANGELOG.md)

![首页](https://github.com/user-attachments/assets/ad7ca9a5-fec5-4e60-9c2c-cc6c102be09d)
![PGC](https://github.com/user-attachments/assets/59600816-85f7-4f8a-83cc-3b8c27870027)
![UGC详情](https://github.com/user-attachments/assets/bef98470-1005-44ed-b823-58daf3392c3c)
![视频播放](https://github.com/user-attachments/assets/1e8939ba-f973-4847-9ae5-7cb9ae64f98e)
![设置](https://github.com/user-attachments/assets/9171ab80-cc19-4e06-898c-66b6bc309e1f)


## 构建
自己动手丰衣足食
- 安装开发环境
  - Android studio、Android SDK、JAVA等等

- 补全构建需要的文件
    - 在项目根目录用使用 Android SDK 中的 keytool 工具创建签名文件 keystore.jks。
    ```sh
    keytool -genkey -v -keystore keystore.jks -alias 别名 -keyalg RSA -keysize 2048 -validity 10000
    ```
  命令说明：
  - genkey: 生成密钥对
  - -v: 详细输出
  - -keystore keystore.jks: 指定生成的密钥库文件名
  - -alias 别名: 指定密钥的别名（可以根据需要修改）
  - -keyalg RSA: 使用 RSA 算法
  - -keysize 2048: 密钥长度为 2048 位
  - -validity 10000: 密钥的有效期为 10000 天（约 27 年）
    执行此命令后，会提示你输入：
    - 密钥库密码（keystore.pwd）
    - 密钥密码（keystore.alias_pwd），可以与密钥库密码相同
    - 姓名、组织单位、城市等信息，可空

  - 在项目根目录增加 signing.properties 文件。文件内容如下
    ```properties
    keystore.path=./keystore.jks
    keystore.pwd=创建签名文件时设置的密码
    keystore.alias=创建签名文件时设置的别名
    keystore.alias_pwd=创建签名文件时设置的别名密码
    ```
2. 执行构建命令来生成 apk 文件
    ```sh
    # release
    ./gradlew clean assembleRelease
    ```
    - 在根目录增加 signing.properties 文件。文件内容如下
    ```properties
    keystore.path=./keystore.jks
    keystore.pwd=创建签名文件时设置的密码
    keystore.alias=创建签名文件时设置的别名
    keystore.alias_pwd=创建签名文件时设置的别名密码
    ```
- 执行构建命令来生成 apk 文件
```sh
# release
./gradlew clean assembleRelease
```


## 安装

### Release

- [Github Release](https://github.com/fantasytyx/bv/releases?q=prerelease%3Afalse)

## License

[MIT](LICENSE) © aaa1115910