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

    //初始输入状态，接下来可以输入“q”结束客户端，输入“GET”或“POST”，输入“send”发送，输入数字选择长连接
    INIT,
    //输入GET后的状态，接下来可以输入“q”返回INIT状态，输入URL
    GET,
    //输入POST后的状态，接下来可以输入“q”返回INIT状态，输入URL
    POST,
    //输入URL后的状态，接下来可以输入“q”返回INIT状态，输入version
    URL,
    //输入version后的状态，接下来可以输入“q”返回INIT状态，输入header，输入空行
    VERSION,
    //选择长连接后的状态，接下来可以输入“q”返回INIT状态，输入header，输入空行
    PERSISTENT,
    //输入header后的状态，接下来可以输入“q”返回INIT状态，输入header，输入空行
    HEADER,
    //输入空行后的状态，接下来可以输入“q”返回INIT状态，输入数字选择body类型
    BODYTYPE,
    //输入body类型后的输入文本内容
    BODY,
    //输入body类型后的输入文件路径
    BODYPATH

}
