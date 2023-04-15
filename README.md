<div align="center">

<img src="app/src/main/res/drawable/ic_banner.webp" style="border-radius: 24px; margin-top: 32px;"/>

# BV

~~Bug Video~~

[![App Center Release](https://img.shields.io/endpoint?url=https%3A%2F%2Funtitled-ecso9wcazr6c.runkit.sh)](https://install.appcenter.ms/users/aaa1115910-gmail.com/apps/bv/distribution_groups/public)
[![Android Sdk Require](https://img.shields.io/badge/android-5.0%2B-informational)](https://developer.android.com/jetpack/compose/interop/adding#:~:text=minimum%20API%20level%20to%2021%20or%20higher%2C)
[![GitHub](https://img.shields.io/github/license/aaa1115910/bv)](https://github.com/aaa1115910/bv)

[![Release workflow](https://github.com/aaa1115910/bv/actions/workflows/release.yml/badge.svg)](https://github.com/aaa1115910/bv/actions/workflows/release.yml)
[![Release workflow](https://github.com/aaa1115910/bv/actions/workflows/alpha.yml/badge.svg)](https://github.com/aaa1115910/bv/actions/workflows/alpha.yml)

**BV
不支持在中国大陆地区内的智慧型电视上使用，如有相关使用需求请使用 [云视听小电视](https://app.bilibili.com)
**

</div>

---
BV ~~(Bug Video)~~ 是一款 [哔哩哔哩](https://www.bilibili.com) 的第三方应用，适配 `Android 移动端`
和 `Android TV`，使用 `Jetpack Compose` 开发，支持 `Android 5.0+`

**都是随心乱写的代码，能跑就行。**

## 特色

- :bug: 丰富多样的 Bug
- :children_crossing: 反人类设计
- :zap: 卡卡卡卡卡
- :art: 异样审美

## 安装

你可以在 [App Center](https://install.appcenter.ms/users/aaa1115910-gmail.com/apps/bv/distribution_groups/public)
或 [Actions](https://github.com/aaa1115910/bv/actions) 获取持续集成版本。

## 自行编译注意事项

### JDK 版本

`jdk 11`

### google-services.json

该项目使用 Firebase Crashlytics 进行异常日志统计，因此需要用到 `google-services.json` 文件。

你应在 [Firebase](https://console.firebase.google.com/)
上创建一个新应用，并下载获取你自己的 `google-services.json` 文件，并将其放置于 `app` 目录下。
