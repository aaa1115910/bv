<div align="center">

<img src="app/shared/src/main/res/drawable/ic_banner_md.webp" style="border-radius: 24px; margin-top: 32px;"/>

# BV

~~Bug Video~~

[![Android Sdk Require](https://img.shields.io/badge/Android-6.0%2B-informational?logo=android)](https://apilevels.com/#:~:text=Jetpack%20Compose%20requires%20a%20minSdk%20of%2021%20or%20higher)
[![GitHub](https://img.shields.io/github/license/aaa1115910/bv)](https://github.com/aaa1115910/bv)
[![Downloads](https://img.shields.io/github/downloads/fantasytyx/bv/total?logo=github&cacheSeconds=3600)](https://github.com/fantasytyx/bv/releases)

**BV 无法在中国大陆地区内的智能电视上使用，如有相关使用需求请使用 [云视听小电视](https://app.bilibili.com)**

**禁止在中国境内传播、宣传、分发 BV**

</div>

---
BV ~~(Bug Video)~~ 是一款 [哔哩哔哩](https://www.bilibili.com) 的第三方应用，适配 `Android 移动端`
和 `Android TV`，使用 `Jetpack Compose` 开发

**都是随心乱写的代码，能跑就行。**

---

<div align="center">

# 学废了

</div>

## 声明

**此项目是个人为了学习安卓开发而fork, 仅用于学习和测试，禁止在中国境内传播、宣传、分发，如有相关使用需求请使用 [哔哩哔哩官方APP](https://app.bilibili.com)，否则后果自负**

## 修改
在原bv的基础上做了一些修改，包括：
- 把“浏览历史、我的收藏、我的追番、稍后再看”整合到“首页”下面
- 增加“首页默认标签”设置 （设置-界面设置，默认“推荐”）
    - 可以修改打开应用时首页默认选中的标签
- 首页的推荐、热门、动态、历史、收藏、稍后看列表、UGC列表以及UGC视频推荐列表，UGC视频卡片增加长按确认键进入up主空间页面
- 动态，聚焦在视频卡片上时，按菜单键打开UP关注列表页
- 动态、up空间、视频推荐，在充电视频的UGC视频卡片右上角增加闪电图标（web接口）

  ![首页](https://github.com/user-attachments/assets/687891ea-b6f0-43c4-b4d6-cdd24b716f6f)
- UGC视频详情页增加点赞、投币功能
- 增加是否“显示UGC视频详情页”设置 （默认显示）
    - 关闭后，点击非PGC视频卡片不显示详情页直接开始播放

  ![UGC详情](https://github.com/user-attachments/assets/09f76c5c-f494-44a7-9aa9-672da489a156)
- 播放器页面增加“推荐视频”
    - 操作方式： 1）双击下键; 2）按下键显示视频信息，移动焦点在底部那排按钮后再按下键

  ![视频播放-推荐视频](https://github.com/user-attachments/assets/2ea057f2-7e78-4b68-9bd4-5d4b2bb31d9e)
- 新增视频画面旋转功能
- 播放器控制条，增加点赞、收藏、投币
    - 仅UGC视频且要登录才会显示
- 播放器控制条，默认聚焦在进度条
  - 此时，按确认键会触发“播放/暂停”、按左右键回触发“快进/快退”
- 播放器控制条，增加功能按钮（播放速度、up空间、旋转画面、字幕开关、刷新当前视频、弹幕开关、播放清单、推荐视频、播放器设置、循环播放）

  ![视频播放](https://github.com/user-attachments/assets/e341b54e-3af9-4c9f-ab68-791335864267)
- 调整设置，增加分类“播放设置”
  - 把 分辨率、视频编码、音频编码、启用音频软件 4个设置移入这个分类
  - 增加是否“显示UGC视频详情页”设置 （默认显示）
  - 增加是否显示“播放器底部常驻进度条”设置（默认不显示）
  - 增加“显示视频加载过程信息”设置（默认不显示）
  - 增加是否“下一个播放”设置（默认不播放），可设置为：
    - 不播
    - 播推荐视频
    - 播剧集和分P的下一个
    - 播播剧集和分P的下一个或推荐视频
  - 增加是否“都播完后退出播放器”设置（默认开启）
  - 增加默认播放速度设置（默认1倍）
  - 增加快进时间间隔设置（默认10秒）
  - 增加快退时间间隔设置（默认5秒）

  ![设置](https://github.com/user-attachments/assets/5e721ec3-e584-4233-a112-e7a3ee5f1afd)
- 优化up空间丰富内容并增加本地搜索
- 优化搜索页面、账号管理页面
- 优化列表、优化视频卡片、精简动画、增加数据缓存、减少非必要的请求
- 按自己的喜好调整页面的布局、元素大小、交互方式、原有功能
- 解决一些bug等等

[修改明细](./CHANGELOG.md)

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

- [Github Release](https://github.com/fantasytyx/bv/releases)

## License

[MIT](LICENSE) © aaa1115910