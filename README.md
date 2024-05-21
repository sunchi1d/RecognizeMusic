# 一个听歌识曲的demo

### 注意事项

因为html里调用的js方法用到了WEB AUDIO API的AudioWorkletProcessor，这个需要chrome66版本以上以及webview版本不能过低。

由于安全问题，安卓在新版本中禁用了html里的file组件，在webview中点击file会读取不到sdcard，本人在项目中重写了onActivityResult和onShowFileChooser实现了可以从file里读取aduio到html的audio组件中，让音频可以正常播放。

因为需要录音和读卡，注意配置相关权限，项目中重写了onPermissionRequest，点开项目会弹出是否打开权限的消息框。

需要在xml里配置network-security-config的cleartextTrafficPermitted=true否则GET请求会失败。

如果使用android studio AVD请自行更新chrome版本以及webview版本，在google play商店中搜索然后update就行，没有就自行下载（登录谷歌需要VPN，需要的朋友可以联系我）

通过音频文件或者录音获取音纹audioFP,再通过使用 https://cors-anywhere.herokuapp.com/ 绕过cors限制对网易云官方pi发出请求（访问可用度受限）

如果想了解webview和js交互以达到在安卓中调用js方法或者在js中调用安卓方法的话（如在html5里点击按钮在安卓中实现页面跳转，数据传输等）可以看我另一个项目，已经把该功能集成在了里面。[https://github.com/sunchi1d/MusicPro]()

### 网易云api

该api的nodejs版本可以通过时长和音纹请求到歌曲信息

可以使用网易云api,由于作者已经不再更新维护，见[https://github.com/Binaryify/NeteaseCloudMusicApi]()

Gitee上可以下载旧版本

https://gitee.com/alamhubb/NeteaseCloudMusicApi

安装见安装文档

在assets文件夹下html文件中修改fetch请求

```javascript
fetch('https://cors-anywhere.herokuapp.com/' +
            'https://interface.music.163.com/api/music/audio/match?' +
                new URLSearchParams({
                    sessionId: '0123456789abcdef', algorithmCode: "shazam_v2", duration: duration, rawdata: FP, times: 1, decrypt: 1
                }), {
                    method: 'POST'
                }
```

修改为

```javascript
fetch(
            'http://localhost:3000/audio/match?' +
            new URLSearchParams(Object.assign({
                audioFP: FP,
                duration: duration
            }))
        )
```

### 使用效果

![Screenshot_20240520_142513](Screenshot_20240520_142513.png)

如图所示则是应用成功，否则请按上面所说检查配置

结果显示

![Screenshot_20240520_142514](Screenshot_20240520_142514.png)

如图所示，搜索成功！！

### 项目说明

本项目通过css水波纹模拟了网易云音乐听歌识曲的功能

由于只找到基于js的生成音频指纹方法所以想在安卓上利用webview实现会更便捷，但是在生活实际上这样的方法并不科学，会浪费系统资源，我还在寻炸新的方法。

本项目固定了录音时间为3s,然后通过时间和音纹来请求到音乐信息，所以弊端也很明显，如果识别失败，需要再次点击识别按钮重新识别，这样可能会错过最佳录入时间，现实中更具友好性的应该是点击识曲功能后，线程开启，如果没有识别到会继续识别，直到音乐停止或者识别完成为止，在实际情况下迫切的想知道歌曲的名字，或者音乐即将停止很难有多次识别的机会时候，本项目便很难达成需求，本人也在极力探索新的方法，请等待我的更新。

webview作为在安卓开发中常见的开发工具，可以减少开发成本，本项目是一个学习安卓和html5跨平台开发的一个demo，已经应用到本人的音乐软件MusicPro中，使用javascript和安卓进行交互，实现了点击搜索结果在安卓软件应用中播放识别到的歌曲，感兴趣的话可以看一下我的另一个项目 [https://github.com/sunchi1d/MusicPro]()

### 鸣谢

https://github.com/Binaryify/NeteaseCloudMusicApi

https://github.com/mos9527/ncm-afp

https://github.com/Rob--W/cors-anywhere
