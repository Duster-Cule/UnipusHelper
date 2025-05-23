Issue 规范
---
在提出Issue前，我希望您先阅读以下文字：

在提出Issue前，请先翻阅有没有人已经提出过相似问题。

如果提Issue的目的是请求实现新的feature:

 - 请在标题前带上[Feature]字样
 - 请在正文出写出你想要的要求
 - 如果可以，欢迎提交PR

如果提Issue的目的是反馈Bug:
 - 请<font color="red">不要</font>提出无法复现的bug
 - 请在标题前带上[BUG]字样
 - 请以<font color="red">清晰易懂的方式</font>描述bug情况
 - 请在Issue的正文部分附上终端输出
 - 对于有能力的人，可以通过下面的方法给出更多信息，方便错误排查:
   - 使用`--debug`参数运行程序(`java -jar unipushelper.jar --debug`)
   - `--debug`参数会显示浏览器界面且在程序抛出异常时将页面保存至同目录下`page.mhtml`文件。如有需要请将此文件发送至[duster_cule@qq.com](mailto:duster_cule@qq.com)