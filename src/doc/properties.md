`showbrowser`：浏览器窗口是否可见（启动参数-debug和-showbroser会覆盖此值）

`browser.width` `browser.height`：浏览器窗口的大小

`username`：用户名/邮箱/手机号

`password`：密码

`LLMPlatform`：需要连接的LLM平台。可选值：["deepseek","ollama"]

`APIAddress`：ollama专用，ollama api地址（默认localhost）

`APIPort`：ollama专用，ollama api端口（默认11434）

`model`：ollama专用，模型名称（请注意这里和运行ollama run指令后的名称一致，格式为`modelname:version`。如`deepseek-r1:14b`）

`APIKey`：LLM平台的APIKey

`exceptURLs`：排除列表（必修但是不需要程序作答的URL，多个URL以`|`隔开。仅在`learnURLs`参数为空时使用）

`learnURLs`：指定学习URL（若此项指定了URL，程序将只完成这些URL对应题目。多个URL以`|`隔开）