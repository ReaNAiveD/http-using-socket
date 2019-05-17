package client;

import java.lang.reflect.Array;

/**
 * 指令类
 *
 * @author liuxingchi, xiepeidong
 */
class Command {

    private String command;

    Command(String command) {
        this.command = command;
    }

    /**
     * 判断command是否符合规则，执行符合规则的指令
     *
     * @return command的合法性
     */
    boolean isValid() {

        String[] array;
        switch (State.input) {

            //初始输入状态
            case INIT:
                if (State.QUIT_COMMAND.equals(command)) {
                    State.quit();
                } else if (State.SHOW_COMMAND.equals(command)) {
                    State.show();
                } else if (State.GET_COMMAND.equals(command)) {
                    State.tempConnection = new Connection();
                    State.tempConnection.getRequestMessage().setMethod("GET");
                    State.input = Input.GET;
                } else if (State.POST_COMMAND.equals(command)) {
                    State.tempConnection = new Connection();
                    State.tempConnection.getRequestMessage().setMethod("POST");
                    State.input = Input.POST;
                } else if (State.SEND_COMMAND.equals(command) && State.tempConnection != null) {
                    State.tempConnection.sendAndReceive();
                    State.input = Input.INIT;
                } else if (command.matches(State.PERSISTENT_REGEX) && Integer.valueOf(command) < State.persistentConnections.size()) {
                    State.tempConnection = State.persistentConnections.get(Integer.valueOf(command));
                    System.out.println("client <<INFO>> : Choose the persistent connection " + Integer.valueOf(command) + ".");
                    State.input = Input.PERSISTENT;
                } else {
                    return false;
                }
                break;

            //输入GET、POST后的状态
            case GET:
            case POST:
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if (command.matches(State.URL_REGEX)) {
                    State.tempConnection.getRequestMessage().setUrl(command);
                    State.input = Input.URL;
                } else {
                    return false;
                }
                break;

            //输入URL后的状态
            case URL:
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if (State.VALID_VERSION.contains(command)) {
                    State.tempConnection.getRequestMessage().setVersion(command);
                    State.input = Input.VERSION;
                } else {
                    return false;
                }
                break;

            //输入version后、选择长连接后的状态
            case VERSION:
            case PERSISTENT:
                array = command.split(" ");
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if (Array.getLength(array) == State.HEADER_LENGTH) {
                    State.tempConnection.getRequestMessage().initHeaderLine(array[0], array[1]);
                } else {
                    return false;
                }
                break;

            //输入header后的状态
            case HEADER:
                array = command.split(" ");
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if (Array.getLength(array) == State.HEADER_LENGTH) {
                    State.tempConnection.getRequestMessage().addHeaderLine(array[0], array[1]);
                } else if ("".equals(command)) {
                    System.out.println("client <<INFO>> : Stop editing the header.");
                    State.input = Input.BODY;
                } else {
                    return false;
                }
                break;

            //输入空行后的状态
            //TODO
            case BODY:
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if (State.TEXT_BODY.equals(command)) {
                    State.tempConnection.getRequestMessage().setEntityBody("BODY-TODO-A");
                } else if (State.MUSIC_BODY.equals(command)) {
                    State.tempConnection.getRequestMessage().setEntityBody("BODY-TODO-B");
                } else if (State.PICTURE_BODY.equals(command)) {
                    State.tempConnection.getRequestMessage().setEntityBody("BODY-TODO-C");
                } else {
                    return false;
                }
                State.input = Input.INIT;
                break;

            default:
                return false;
        }
        return true;
    }

}
