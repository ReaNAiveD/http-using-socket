## 关于get图片的方法

设计上假设resources目录是http服务器的资源目录，clientResources是客户端的资源目录。

在get请求输入url时，如/hello.txt表示resources目录下的hello.txt文件。

get请求成功后，如果是非文本类型的文件，会存储在clientResources目录下，如果在idea的project面板可能无法立即看到，请在explore中打开目录查看。

例：

```
get
127.0.0.1/hello.txt


send
```
    
## 如何post文件

设计上假设resources目录是http服务器的资源目录，clientResources是客户端的资源目录。

post请求核心在于设置content内容。输入b选择传输文件，然后输入文件相对于clientResources的路径。

如果请求未设置Content-Type，那么post请求会根据资源默认设置Content-Type。
"keep-alive".equals(requestMessage.getHeaderLine("Connection"));
post单行文本时默认设置Content-Type为text/plain。

例：

```
post
127.0.0.1/hello


b
/DeepColor.png
send
```


## 一些特性

- get请求body为空，header设置结束后自动跳过
- version默认值为1.1，回车即可
- url输入是会将“/”前的放在Header里的Host， “/”后的会放在第一行的相对地址上
- 长连接第一次之后的请求不修改Method、Host、Version，修改相对地址、header、body

## 已知的bug和未完成的部分

- 服务器响应只有1.1
- 一定情形侠退出客户端，服务器端会炸
- 状态码不完全