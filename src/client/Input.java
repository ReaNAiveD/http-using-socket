package client;

/**
 * 输入枚举
 * 这个类代表了指令输入的状态
 * 每一次正确指令输入都可能改变这个状态
 * 用来辅助指令执行和判断下一次指令输入是否合法
 *
 * @author xiepeidong
 */
enum Input {

    //初始输入状态，接下来可以输入“q”结束客户端，输入“s”显示当前状态,输入“TYPE”或“POST”，输入“send”发送，输入数字选择长连接
    INIT,
    //输入GET、POST后的状态，接下来可以输入“q”返回INIT状态，输入path，默认值为127.0.0.1
    TYPE,
    //输入path后的状态，接下来可以输入“q”返回INIT状态，输入version，默认值为1.1
    PATH,
    //输入version后的状态，接下来可以输入“q”返回INIT状态，输入header键值对，默认值为空行
    VERSION,

    //选择长连接后的状态，接下来可以输入“q”返回INIT状态，输入相对地址，默认值为相对地址为“”
    PERSISTENT,
    //输入相对地址后的状态，接下来可以输入“q”返回INIT状态，输入header键值对，默认值为空行
    RELATIVE,

    //输入header键值对后的状态，接下来可以输入“q”返回INIT状态，输入header键值对，默认值为空行
    KEY_VALUE,
    //输入header空行后的状态，接下来可以输入“q”返回INIT状态，输入数字选择body类型，默认值为empty类型
    HEADER,
    //选择文本类型后的状态，接下来可以输入“q”返回INIT状态，输入文本内容，默认值为“”
    BODY_TEXT,
    //选择其他类型后的状态，接下来可以输入“q”返回INIT状态，输入文件路径，默认值为“/hello.txt”
    BODY_PATH,

}
