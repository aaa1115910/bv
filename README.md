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
- 增加点赞、投币功能
- 通过 精简动画、增加数据缓存、减少非必要的请求，减少页面重组
- 按自己的喜好调整页面的布局、元素大小、交互方式、原有功能
- 解决一些bug等等

  [修改内容](./CHANGELOG.md)

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

### Alpha

- [Github Release](https://github.com/fantasytyx/bv/releases?q=prerelease%3Atrue)

## License

[MIT](LICENSE) © aaa1115910