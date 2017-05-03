# 咕咚翻译
![icon](/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png "")

[![Build Status](https://travis-ci.org/maoruibin/TranslateApp.svg?branch=master)](https://travis-ci.org/maoruibin/TranslateApp)

## 介绍
一个实现『划词翻译』功能的 Android 应用 ，可能是目前 Android 市场上翻译效率最高的一款应用。

![shot](http://7xr9gx.com1.z0.glb.clouddn.com/gd.gif)

## 待完成功能
- [ ] 单词显示框支持沉浸式，覆盖状态栏显示。
- [ ] 使用 Github 做自动更新
- [x] 不支持谷歌翻译，如果有人有兴趣添加谷歌，必应翻译，欢迎PR。
- [x] 单词发音
- [x] 显示单词信息时增加音标
- [x] 生词本为空时的提示信息

`Note:`想要参与贡献代码的同学，请在 develop 分支上操作，欢迎参与，可以提前在 issue 中交流自己要修改的功能模块，同时也欢迎来[Google+社区](https://plus.google.com/u/1/communities/111919086388322816251)一起讨论 咕咚翻译。

## 下载

<a href="https://play.google.com/store/apps/details?id=name.gudong.translate" target="_blank" alt="Google Paly"><img src="http://7xr9gx.com1.z0.glb.clouddn.com/icon_google_play_brand.png"/></a>

[Fir](http://fir.im/gdTranslater)

[酷安市场](http://www.coolapk.com/apk/name.gudong.translate)

如果想要帮助做内部测试，[欢迎来Google+社区](https://plus.google.com/u/1/communities/111919086388322816251)我会把最新的版本放在 Google+社区。

## 缘起
自己经常在手机上会阅读一些英文的技术 blog 或者文档，经常会遇到陌生的单词，想必大家都有类似的经历，一般的，如果是上班期间，我可能会打开谷歌翻译，然后查词，但是当你不在电脑旁，就只能通过手机上的翻译软件来翻译生词，所以这时我们通常遇到生词会这样操作，长按生词、选择复制、点击 Home 键、找到翻译 App,有道翻译或者谷歌翻译之类的，然后长按输入框，选择粘贴，此时你才可以看到那个生词的意思。其实这是一个非常考验用户耐性的事，连续操作几次，不烦才怪呢。

我也是，自己以前在 Medium 上阅读文章时经常做这样的事，后来我想能不能简单点呢，所以就有了咕咚翻译这个 App,咕咚是我的网名呢~

## 讨论/发声

个人博客上[关于咕咚翻译的介绍](http://gudong.name/product/2016/02/26/gudong_translate.html)

V2EX 上对 App 的讨论 [咕咚翻译](https://www.v2ex.com/t/259288#reply69)

微博上[关于咕咚翻译的讨论](http://weibo.com/1874136301/Dkrpm8sWn?type=comment#_rnd1456976705834)

Google+ [咕咚翻译社群](https://plus.google.com/u/1/communities/111919086388322816251)


## 技术点

* 1、全程使用 [Dagger2](https://github.com/google/dagger) 对项目进行类依赖管理
* 2、项目使用标准的 MVP 架构，[关于 MVP 的一篇博客](http://gudong.name/advanced/2015/11/23/gank_mvp_introduce.html)
* 3、实现对粘贴板的监听以及访问
* 4、无需权限显示悬浮窗（6.0悬浮窗权限适配）
* 5、使用 [RxJava](https://github.com/ReactiveX/RxJava) 探索函数式编程
* 6、使用 [Retrofit2](https://github.com/square/retrofit) 进行网络请求
* 7、开机自启动
* 8、Android 6.0 [自定义文本操作栏](http://www.jianshu.com/p/40e84359d683)


`Note` 3、4都是借鉴自[廖祜秋](https://github.com/liaohuqiu/)的开源项目[android-UCToast](https://github.com/liaohuqiu/android-UCToast)，感谢~

## 更新日志
[日志列表](./doc/Changelog.md)

## 捐赠
[咕咚翻译捐赠](http://gudong.name/1990/03/01/list_pay.html)

## 关于作者

咕咚，爱折腾、爱新鲜，爱篮球。

[个人站点](http://gudong.name/)

[github](https://github.com/maoruibin)

[微博](http://weibo.com/u/1874136301)

## License
 
     Copyright (C) 2015 GuDong <gudong.name@gmail.com>
   
     This file is part of GdTranslate
   
     GdTranslate is free software: you can redistribute it and/or modify
     it under the terms of the GNU General Public License as published by
     the Free Software Foundation, either version 3 of the License, or
     (at your option) any later version.
   
     GdTranslate is distributed in the hope that it will be useful,
     but WITHOUT ANY WARRANTY; without even the implied warranty of
     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
     GNU General Public License for more details.
   
     You should have received a copy of the GNU General Public License
     along with GdTranslate.  If not, see <http://www.gnu.org/licenses/>.

