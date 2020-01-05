# UpdateDialog
## Latest Release Version： [![](https://jitpack.io/v/bobozhou007/UpdateDialog.svg)](https://jitpack.io/#bobozhou007/UpdateDialog)
## 项目说明：
  项目依托开发中的实际需求，致力于打造简便易用的更新弹窗，欢迎各位多提宝贵意见！<br/>
  项目截图：
    
## 注意事项：
   1.更新内容支持html格式，采用Html.from()方法，可实现自由换行和变换颜色等。（例如：1.下载链接兼容中文\<br/>2.下载链接可以是非以.apk结尾的文件的链接\<br/>3.兼容7.0系统及以上apk文件的访问权限）<br/>
   2.下载地址默认download文件夹
## 使用说明：
  ### 配置：
    1.在项目gradle中追加maven仓库
      allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
    2.添加依赖 
      dependencies {
	        implementation 'com.github.bobozhou007:UpdateDialog:LatestVersion'
	}
  ### 代码实现：
      new UpdateDialog.Builder()
          .setVersion("v1.8.8")//版本名称
          .setContent("1.下载链接兼容中文<br/>2.下载链接可以是非以.apk结尾的文件的链接<br/>3.兼容7.0系统及以上apk文件的访问权限")//更新内容
          .setCancelable(true)//是否可取消
          .setDebug(true)//是否查看日志
          .setDownloadUrl("http(s)://xxxx.apk")//设置下载链接
          .build()
          .showUpdateDialog(this);
### 版本说明：v1.0.1
   1.下载链接兼容中文<br/>
   2.下载链接可以是非以.apk结尾的文件的链接<br/>
   3.兼容7.0系统及以上apk文件的访问权限<br/>
   4.修复bugs
### 版本说明：v1.0.0
   1.实现基础的更新功能<br/>
   2.可选择强制更新和推荐更新<br/>
   3.可以查看调试日志
