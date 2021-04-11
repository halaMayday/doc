前提

今天公司推行了一套统一的代码风格规范。

java开发建议用的规范是：https://google.github.io/styleguide/javaguide.html

建议使用的lint是：checkstyle。

1. checkstyle初体验

   使用插件：CheckStyle-IDEA。

   内置两种配置google和sun的，sun的更为严格，公司建议使用google。 

   不扫不知道，一扫吓一跳。一个文件就123个不合规。

   <img src="/Users/nuc/Library/Application Support/typora-user-images/image-20210409213826333.png" alt="image-20210409213826333" style="zoom:50%;" />

2. 配置idea为google风格

   - 下载xml文件：https://github.com/google/styleguide/blob/gh-pages/intellij-java-google-style.xml

   - 设置：editor->code style-> import scheme 然后导入上一个步骤中我们下载保存的xml文件

     ![image-20210409214137842](/Users/nuc/Library/Application Support/typora-user-images/image-20210409214137842.png)

   - 调整代码格式：code-> Reformat code。快捷键Ctrl-Alt-L（默认情况下）

   备注：

   ​	备选方案：

   ​	使用插件google-java-format。下载安装后，Reformat code即可。

