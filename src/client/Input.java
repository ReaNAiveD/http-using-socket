package client;

/**
 * 输入枚举
 * 这个类代表了指令输入的状态
 * 每一次正确指令输入都可能改变这个状态
 * 用来辅助指令执行和判断下一次指令输入是否合法
 * 正常流程为INIT、METHOD、PATH、VERSION、KEY_VALUE、HEADER、BODY_TEXT/BODY_PATH、INIT
 * 长连接流程为INIT、PERSISTENT、RELATIVE、KEY_VALUE、HEADER、BODY_TEXT/BODY_PATH、INIT
 * VERSION,RELATIVE,KEY_VALUE其实可以合为一个，但为便于理解故分开
 *
 * @author xiepeidong
 */
enum Input {

    //初始输入状态，接下来可以输入“q”结束客户端，输入“s”显示当前状态,输入“METHOD”或“POST”，输入“send”发送，输入数字选择长连接，默认值为“”刷新
    INIT,
    //输入GET、POST后的状态，接下来可以输入“q”返回INIT状态，输入path，默认值为127.0.0.1
    METHOD,
    //输入path后的状态，接下来可以输入“q”返回INIT状态，输入version，默认值为1.1
    PATH,
    //选择长连接后的状态，接下来可以输入“q”返回INIT状态，输入相对地址，无默认值
    PERSISTENT,
    //输入version后的状态，接下来可以输入“q”返回INIT状态，输入header键值对，无默认值
    VERSION,
    //输入相对地址后的状态，接下来可以输入“q”返回INIT状态，输入header键值对，无默认值
    RELATIVE,
    //输入header键值对后的状态，接下来可以输入“q”返回INIT状态，输入header键值对，无默认值
    KEY_VALUE,
    //输入header空行后的状态，接下来可以输入“q”返回INIT状态，输入数字选择body类型，默认值为empty类型
    HEADER,
    //选择文本类型后的状态，接下来可以输入“q”返回INIT状态，输入文本内容，无默认值
    BODY_TEXT,
    //选择其他类型后的状态，接下来可以输入“q”返回INIT状态，输入文件路径，默认值为“/hello.txt”
    BODY_PATH,

}
