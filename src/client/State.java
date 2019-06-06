package client;

import java.util.*;

/**
 * 状态类
 *
 * @author xiepeidong
 */
class State {

    static final String QUIT_COMMAND = "q";
    static final String SHOW_COMMAND = "s";
    static final String GET_COMMAND = "get";
    static final String POST_COMMAND = "post";
    static final String SEND_COMMAND = "send";
    static final String PUT_COMMAND = "put";
    static final String PATH_REGEX = ".*/?.*$";
    static final String PERSISTENT_REGEX = "^[0-9]+$";
    static final String GET_METHOD = "GET";
    static final String POST_METHOD = "POST";
    static final String PUT_METHOD = "PUT";
    static final String DEFAULT_PATH = "127.0.0.1";
    static final String HOST_HEADER = "Host";
    static final String TYPE_HEADER = "Content-Type";
    static final String CONNECTION_HEADER = "Connection";
    static final String PERSISTENT_HEADER = "keep-alive";
    static final String TEXT_TYPE = "text";
    static final String EMPTY_BODY = "e";
    static final String TEXT_BODY = "a";
    static final String FILE_BODY = "b";
    static final String SPLIT_FLAG = "/";
    static final String SPACE_FLAG = " ";
    static final List<String> VALID_VERSION = Arrays.asList("1.0", "1.1");
    static final int HEADER_LENGTH = 2;

    static boolean quit = false;
    static Input input = Input.INIT;
    static String host = "127.0.0.1";
    static int port = 8080;
    static Connection tempConnection;
    static HashMap<Integer, Connection> persistentConnections = new HashMap<>();

    /**
     * todo
     * 输入前给出建议
     */
    static void suggest() {
        System.out.println();
        System.out.println("+ + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + + ");
        switch (input) {
            case INIT:
                System.out.println("client <<TIP>> : \"q\" to quit.");
                System.out.println("client <<TIP>> : \"s\" to show information.");
                System.out.println("client <<TIP>> : \"get\" to create a GET connection.");
                System.out.println("client <<TIP>> : \"post\" to create a POST connection.");
                System.out.println("client <<TIP>> : default value  \"\" to refresh.");
                if (tempConnection != null) {
                    System.out.println("client <<TIP>> : \"send\" to send the request.");
                }
                TreeSet<Integer> set = new TreeSet<>(Comparator.reverseOrder());
                set.addAll(State.persistentConnections.keySet());
                for (Integer i : set) {
                    System.out.println("client <<TIP>> : \"" + i + "\" to choose the persistent connection " + i + ".");
                }
                break;
            case METHOD:
                System.out.println("client <<TIP>> : \"q\" to abandon editing the request.");
                System.out.println("client <<TIP>> : path to set path.");
                System.out.println("client <<TIP>> : default value  \"127.0.0.1\" to set path.");
                break;
            case PATH:
                System.out.println("client <<TIP>> : \"q\" to abandon editing the request.");
                System.out.println("client <<TIP>> : \"1.0\" or \"1.1\" to set http version.");
                System.out.println("client <<TIP>> : default value  \"1.1\" to set version.");
                break;
            case PERSISTENT:
                System.out.println("client <<TIP>> : \"q\" to abandon editing the request.");
                System.out.println("client <<TIP>> : relative path to set relative path.");
                break;
            case VERSION:
            case RELATIVE:
            case KEY_VALUE:
                System.out.println("client <<TIP>> : \"q\" to abandon editing the request.");
                System.out.println("client <<TIP>> : key and value to set header.");
                System.out.println("client <<TIP>> : blank to stop setting header.");
                break;
            case HEADER:
                System.out.println("client <<TIP>> : \"q\" to abandon editing the request.");
                System.out.println("client <<TIP>> : \"e\" to set body empty.");
                System.out.println("client <<TIP>> : \"a\" to set body to one-line text.");
                System.out.println("client <<TIP>> : \"b\" to set body to file.");
                System.out.println("client <<TIP>> : default value  \"e\" to set empty body.");
                break;
            case BODY_TEXT:
                System.out.println("client <<TIP>> : \"q\" to abandon editing the request.");
                System.out.println("client <<TIP>> : text to set one-line text.");
                break;
            case BODY_PATH:
                System.out.println("client <<TIP>> : \"q\" to abandon editing the request.");
                System.out.println("client <<TIP>> : file path to set file.");
                System.out.println("client <<TIP>> : default value  \"/hello.txt\" to set file.");
                break;
            default:
                break;
        }
        System.out.print("User > ");
    }

    /**
     * 退出客户端
     * 需要关闭所有连接
     */
    static void quit() {
        if (tempConnection != null) {
            tempConnection.close();
        }
        for (Integer key : persistentConnections.keySet()) {
            persistentConnections.get(key).close();
        }
        System.out.println("client <<INFO>> : QUIT.");
        quit = true;
    }

    /**
     * 显示当前客户端状态
     */
    static void show() {
        if (tempConnection == null) {
            System.out.println("client <<INFO>> : There is no connection prepared.");
        } else {
            System.out.println("client <<INFO>> : There is a connection prepared.");
            System.out.println("client <<INFO>> : Its request message is");
            System.out.println("==============================");
            System.out.println(tempConnection.getRequestMessage().getRequest());
            System.out.println("==============================");
        }
        int size = persistentConnections.size();
        if (size == 0) {
            System.out.println("client <<INFO>> : No persistent connection keeps alive.");
        } else if (size == 1) {
            System.out.println("client <<INFO>> : 1 persistent connection keeps alive.");
        } else {
            System.out.println("client <<INFO>> : " + size + " persistent connections keep alive.");
        }
    }

    /**
     * 放弃编辑当前请求
     */
    static void abandonEditing() {
        State.tempConnection = null;
        System.out.println("client <<INFO>> : Abandon editing the request.");
        State.input = Input.INIT;
    }

}
