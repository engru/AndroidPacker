# AndroidPacker
一个简便易用的打包工具，支持按照`git commit code` 和`tag` 生成apk文件
# Usage
* `project`下的`build.gradle`
```
repositories {
        //mavenCentral()
        ...
        maven { url 'https://maven.google.com' }
        ...
    }
dependencies {
        ...
        classpath 'com.android.tools.build:gradle:2.3.3'
        classpath 'com.wx.build.gradle.packer:packer:1.0.5'
        ...
    }
```
* `app` 下的`build.gradle`
1. 引入`pakcer`插件
```
apply plugin: 'packer'

```
2. 配置`productFlavor`
 ```
 productFlavors {
        wandoujia {
            ...
            applicationId "com.jiangxq.packertest"
            signingConfig signingConfigs.packertest
            ...
        }
    }
 ```
3. 配置`packer`配置项
```
packer{
    product "product_Name" //产品名称
    version{
        type "git" // 版本号来源方式
    }
    output{
        renameOutput true
    }
}
```
生成的文件格式为
`product_Name-[productFlavors]-[Release/Debug]-gitTag.gitCommitCode.apk`

比如：

`Packer-wandoujia-Release-1.0.32.apk`
