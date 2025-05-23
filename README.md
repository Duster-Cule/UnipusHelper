## U校园AI版自动答题工具

---

答题接入deepseek，经过测试选择题正确率100%，填空题正确率70%左右，综合成绩在70-90分不等。

目前答题可以接入ollama，不过经过测试⚠️⚠️<font color=red>️极其不建议</font>⚠️⚠️在家庭电脑上使用。具体内容请查看[OllamaWarning](/src/doc/ollamaWarning.md)

---

使用方法：

1. 在
[Releases](https://github.com/Duster-Cule/UnipusHelper/releases)
下载`unipusHelper.jar`和`unipushelperconfig.properties`并放入同一个文件夹。 
2. 配置`unipushelperconfig.properties`。具体配置请参考[properties](src/doc/properties.md)。
3. 启动时可以选择直接在命令行中输入
    ```bash
    java -jar unipusHelper.jar
    ```
    启动，也可以将其写入`.bat`文件方便双击运行。
---
软件为自用版，仅编写了一些自己需要使用的题目，如果需要适配更多题目，请提交issue并附上完整的HTML网页。

目前支持题目类型：

| 题目类型           | 是否支持 |
|----------------|:----:|
| 选择题            |  ✅   |
| 选词填空           |  ✅   |
| 填空             |  ✅   |
| 阅读（挂机5分钟）      |  ✅   |
| 简答题            |  ✅   |
| 翻译题            |  ✅   |
| 学习生词           |  ✅   |
| review & check |  ✅   |
| 观看视频           | TODO |
| 词语匹配[^1]       |  ❌   |
| 音频题            |  ❌   |
| 评论[^2]         |  ❌   |

[^1]: 目前AI作答成功率低，故后续再支持

[^2]: 为防止AI乱答，交给用户自己作答，可以直接抄现有评论

有问题欢迎大家提出Issue，请注意在提出Issue前阅读[Issue规范](src/doc/issue.md)