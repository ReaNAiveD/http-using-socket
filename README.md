## 关于get图片的方法

设计上假设resources目录是http服务器的资源目录，clientResources是客户端的资源目录。

在get请求输入url时，如/hello.txt表示resources目录下的hello.txt文件。

get请求成功后，如果是非文本类型的文件，会存储在clientResources目录下，如果在idea的project面板可能无法立即看到，请在explore中打开目录查看。

例：

    get
    /DeepColor.png
    1.1
    host localhost
    
    a
    send