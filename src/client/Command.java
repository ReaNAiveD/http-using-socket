package client;

import client.utils.InputStreamTool;

import java.io.*;
import java.lang.reflect.Array;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Base64;

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
    boolean run() {

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
                } else if (command.matches(State.PERSISTENT_REGEX) && State.persistentConnections.keySet().contains(Integer.valueOf(command))) {
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
                    State.input = Input.BODYTYPE;
                } else {
                    return false;
                }
                break;

            //输入空行后的状态
            case BODYTYPE:
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if(State.EMPTY_BODY.equals(command)){
                    State.tempConnection.getRequestMessage().setEntityBody("");
                    State.input = Input.INIT;
                } else if (State.TEXT_BODY.equals(command)) {
                    //State.tempConnection.getRequestMessage().setEntityBody("BODYTYPE-TODO-A");
                    State.input = Input.BODY;
                } else if (State.FILE_BODY.equals(command)) {
                    //State.tempConnection.getRequestMessage().setEntityBody("BODYTYPE-TODO-B");
                    State.input = Input.BODYPATH;
                } else {
                    return false;
                }
                break;

            case BODY:
                State.tempConnection.getRequestMessage().setEntityBody(command);
                State.input = Input.INIT;
                break;

            case BODYPATH:
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                    break;
                }
                File uploadFile = new File(processFilePath(command));
                try {
                    FileInputStream inputStream = new FileInputStream(uploadFile);
                    byte[] data = InputStreamTool.readAllBytes(inputStream);
                    if(!State.tempConnection.getRequestMessage().getHeaderLines().containsKey("Content-Type")){
                        State.tempConnection.getRequestMessage().addHeaderLine("Content-Type", Files.probeContentType(uploadFile.toPath()));
                    }
                    if (State.tempConnection.getRequestMessage().getHeaderLines().get("Content-Type").split("/")[0].equals("text")){
                        State.tempConnection.getRequestMessage().setEntityBody(new String(data, StandardCharsets.UTF_8));
                    }
                    else {
                        Base64.Encoder encoder = Base64.getEncoder();
                        State.tempConnection.getRequestMessage().setEntityBody(encoder.encodeToString(data));
                    }
                    State.input = Input.INIT;
                    break;
                } catch (FileNotFoundException e){
                    System.out.println("client <<ERROR>> : File Not Found.");
                    return false;
                } catch (IOException e){
                    System.out.println("client <<ERROR>> : IO EXCEPTION.");
                    e.printStackTrace();
                }

            default:
                return false;
        }
        return true;
    }

    private String processFilePath(String originFilePath){
        if (originFilePath.equals("")){
            return "resources" + getDefaultFilePath();
        }
        return "resources" + originFilePath;
    }

    private String getDefaultFilePath(){
        return "/hello.txt";
    }

}
