### XMultiThreadDownloader
------
a android multi thread download library support remote service
###Note
this project is fork from <https://github.com/Aspsine/MultiThreadDownload> and add some features,The lib hasn't been fully tested and released yet. If you find some bugs, welcome to post [issues](https://github.com/onlysoft/XMultiThreadDownloader/issues) to me.
###ScreenShot
![demo tsak list](https://github.com/onlysoft/XMultiThreadDownloader/raw/master/art/screenshot02.png)
#####Gradle
----------
```
dependencies {
    ...
    compile 'cn.onlysoft:xmultithreddownload:1.0.2'
}
```
###how to use
read demo first,I will and it as soon as quickly
</br>
- Ponit 1: Add permission in 'AndroidManifest.xml'
```Xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```
- Ponit 2: Add "compile project(':library')" in your 'build.gradle' file.
- Ponit 3: Config it in your Application class
```Java
private void initDownloader() {
    DownloadConfiguration configuration = new DownloadConfiguration();
    configuration.setMaxThreadNum(10);
    configuration.setThreadNum(3);
    DownloadManager.getInstance().init(getApplicationContext(), configuration);
}
```
--Ponit 4:add tak 
//first covert your data model to downloadInfo 
//then add it
```Java
private void download(int position, String tag, DownloadInfo info) {
        DownloadService.intentDownload(getActivity(), position, tag, info);
    }
```
--Ponit 4:get all Task Info
```Java
DownloadManager.getInstance().getDownloadInfos();
```
//please run it async
####Changelog
---------
> **1.0.0**
>>fixed some bugs
>>save download task to db
>>add the remote service
