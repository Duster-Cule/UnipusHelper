## U校园AI版自动答题工具

---

答题接入deepseek，经过测试选择题正确率100%，填空题正确率70%左右，综合成绩在70-90分不等。

使用方法：

1. 在
[Releases](https://github.com/Duster-Cule/UnipusHelper/releases)
下载`unipusHelper.jar`和`unipushelperconfig.properties`。 
2. 配置`unipushelperconfig.properties`。具体配置请参考[properties](src/doc/properties.md)。
3. 启动时可以选择直接在命令行中输入
    ```bash
    java -jar unipusHelper.jar
    ```
    启动，也可以将其写入`.bat`文件方便双击运行。
---
软件为自用版，仅编写了一些自己需要使用的题目，如果需要适配更多题目，请提交issue并附上完整的HTML网页。