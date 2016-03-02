#咕咚翻译
![icon](/app/src/main/res/mipmap-xxxhdpi/ic_launcher.png "")

##介绍
一个实现『划词翻译』功能的 Android 应用 ，可能是目前 Android 市场上翻译效率最高的一款应用。

![shot](http://7xr9gx.com1.z0.glb.clouddn.com/gd.gif)

##下载
[链接地址]( http://fir.im/gdTranslater)

##缘起
自己经常在手机上会阅读一些英文的技术 blog 或者文档，经常会遇到陌生的单词，想必大家都有类似的经历，一般的，如果是上班期间，我可能会打开谷歌翻译，然后查词，但是当你不在电脑旁，就只能通过手机上的翻译软件来翻译生词，所以这时我们通常遇到生词会这样操作，长按生词、选择复制、点击 Home 键、找到翻译 App,有道翻译或者谷歌翻译之类的，然后长按输入框，选择粘贴，此时你才可以看到那个生词的意思。其实这是一个非常考验用户耐性的事，连续操作几次，不烦才怪呢。

我也是，自己以前在 Medium 上阅读文章时经常做这样的事，后来我想能不能简单点呢，所以就有了咕咚翻译这个 App,咕咚是我的网名呢~

更多[关于咕咚翻译](http://gudong.name/product/2016/02/26/gudong_translate.html)

V2EX 上对 App 的讨论 [咕咚翻译](https://www.v2ex.com/t/259288#reply69)


## 已知 Bug 
* 查词时，输入框下面应该会显示"查词中...",目前不能显示出来
* 在本应用外收藏成功单词后，没有成功提示

## 待优化
* 生词本为空时的提示信息
* 不支持谷歌翻译，如果有人有兴趣添加谷歌，必应翻译，欢迎PR。
* 没有使用测试，所以项目中可能还潜在的存在一些 bug,关于测试，也是自己今年需要补齐的一块知识。

## 技术点

* 1、全程使用 [Dagger2](https://github.com/google/dagger) 对项目进行类依赖管理
* 2、项目使用标准的 MVP 架构
* 3、实现对粘贴板的监听以及访问
* 4、无需权限显示悬浮窗
* 5、RxJava 的使用
* 6、使用 Retrofit2 进行网络请求
* 7、开机自启动
* 8、泛型

`Note` 3、4都是借鉴自[廖祜秋](https://github.com/liaohuqiu/)的开源项目[android-UCToast](https://github.com/liaohuqiu/android-UCToast)，感谢~

## 更新日志

### V0.6
* 大幅提高定时设置的稳定性，可以在首页灵活设置显示开关,以及显示间隔时间等。
* 加入 [Once](https://github.com/jonfinerty/Once) 库用于简化一些一次性操作
* 加入了 BugHd 作为 bug 统计工具
* 使用 MVP 对项目进行重构
* 使用 Dagger2 进行依赖管理

## 关于作者

咕咚，爱折腾、爱新鲜，爱篮球。

[个人站点](http://gudong.name/)

[github](https://github.com/maoruibin)

[微博](http://weibo.com/u/1874136301)

##License
 
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

