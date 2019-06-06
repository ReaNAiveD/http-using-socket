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
                if ("".equals(command)) {
                    break;
                }
                if (State.QUIT_COMMAND.equals(command)) {
                    State.quit();
                } else if (State.SHOW_COMMAND.equals(command)) {
                    State.show();
                } else if (State.GET_COMMAND.equals(command)) {
                    State.tempConnection = new Connection();
                    State.tempConnection.getRequestMessage().setMethod(State.GET_METHOD);
                    State.input = Input.METHOD;
                } else if (State.POST_COMMAND.equals(command)) {
                    State.tempConnection = new Connection();
                    State.tempConnection.getRequestMessage().setMethod(State.POST_METHOD);
                    State.input = Input.METHOD;
                } else if (State.SEND_COMMAND.equals(command) && State.tempConnection != null) {
                    State.tempConnection.sendAndReceive();
                    State.input = Input.INIT;
                } else if (command.matches(State.PERSISTENT_REGEX) && State.persistentConnections.keySet().contains(Integer.valueOf(command))) {
                    //使用长连接，仅保留报文的请求行，而头部保留host
                    State.tempConnection = State.persistentConnections.get(Integer.valueOf(command));
                    String host = State.tempConnection.getRequestMessage().getHeaderLines().get(State.HOST_HEADER);
                    State.tempConnection.getRequestMessage().clearHeaderLines();
                    State.tempConnection.getRequestMessage().addHeaderLine(State.HOST_HEADER, host);
                    System.out.println("client <<INFO>> : Choose the persistent connection " + Integer.valueOf(command) + ".");
                    State.input = Input.PERSISTENT;
                } else {
                    return false;
                }
                break;

            //输入GET、POST后的状态
            case METHOD:
                if ("".equals(command)) {
                    command = State.DEFAULT_PATH;
                }
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if (command.matches(State.PATH_REGEX) && !command.contains(State.SPACE_FLAG)) {
                    int index = command.indexOf(State.SPLIT_FLAG);
                    if (index == -1) {
                        State.tempConnection.getRequestMessage().initHeaderLine(State.HOST_HEADER, command);
                        State.tempConnection.getRequestMessage().setPath("");
                    } else {
                        if(command.substring(0, index).equals("")){
                            State.tempConnection.getRequestMessage().initHeaderLine(State.HOST_HEADER, State.DEFAULT_PATH);
                        }
                        else {
                            State.tempConnection.getRequestMessage().initHeaderLine(State.HOST_HEADER, command.substring(0, index));
                        }
                        State.tempConnection.getRequestMessage().setPath(command.substring(index));
                    }
                    State.input = Input.PATH;
                } else {
                    return false;
                }
                break;

            //输入path后的状态
            case PATH:
                if ("".equals(command)) {
                    command = State.VALID_VERSION.get(1);
                }
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if (State.VALID_VERSION.contains(command)) {
                    State.tempConnection.getRequestMessage().setVersion(command);
                    State.input = Input.VERSION;
                } else {
                    return false;
                }
                break;

            //选择长连接后的状态
            case PERSISTENT:
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if (!command.contains(State.SPACE_FLAG)) {
                    State.tempConnection.getRequestMessage().setPath(command);
                    State.input = Input.RELATIVE;
                } else {
                    return false;
                }
                break;

            //输入version、相对地址、header键值对后的状态
            case VERSION:
            case RELATIVE:
            case KEY_VALUE:
                array = command.split(State.SPACE_FLAG);
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if (Array.getLength(array) == State.HEADER_LENGTH) {
                    State.tempConnection.getRequestMessage().addHeaderLine(array[0], array[1]);
                    State.input = Input.KEY_VALUE;
                } else if ("".equals(command)) {
                    System.out.println("client <<INFO>> : Stop editing the header.");
                    if (State.GET_METHOD.equals(State.tempConnection.getRequestMessage().getMethod())) {
                        State.tempConnection.getRequestMessage().setEntityBody("");
                        System.out.println("client <<INFO>> : Stop editing the body.");
                        State.input = Input.INIT;
                    } else {
                        State.input = Input.HEADER;
                    }
                } else {
                    return false;
                }
                break;

            //输入header空行后的状态
            case HEADER:
                if ("".equals(command)) {
                    command = State.EMPTY_BODY;
                }
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if (State.EMPTY_BODY.equals(command)) {
                    State.tempConnection.getRequestMessage().setEntityBody("");
                    System.out.println("client <<INFO>> : Stop editing the body.");
                    State.input = Input.INIT;
                } else if (State.TEXT_BODY.equals(command)) {
                    State.input = Input.BODY_TEXT;
                } else if (State.FILE_BODY.equals(command)) {
                    State.input = Input.BODY_PATH;
                } else {
                    return false;
                }
                break;

            //选择文本类型后的状态
            case BODY_TEXT:
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if (!"".equals(command)) {
                    State.tempConnection.getRequestMessage().setEntityBody(command);
                    State.input = Input.INIT;
                } else {
                    return false;
                }
                break;

            //选择其他类型后的状态
            case BODY_PATH:
                if ("".equals(command)) {
                    command = "/hello.txt";
                }
                if (State.QUIT_COMMAND.equals(command)) {
                    State.abandonEditing();
                } else if (command.startsWith(State.SPLIT_FLAG)) {
                    try {
                        File uploadFile = new File("resources" + command);
                        FileInputStream inputStream = new FileInputStream(uploadFile);
                        byte[] data = InputStreamTool.readAllBytes(inputStream);
                        State.tempConnection.getRequestMessage().addHeaderLine(State.TYPE_HEADER, Files.probeContentType(uploadFile.toPath()));
                        if (State.tempConnection.getRequestMessage().getHeaderLines().get(State.TYPE_HEADER).split(State.SPLIT_FLAG)[0].equals(State.TEXT_TYPE)) {
                            State.tempConnection.getRequestMessage().setEntityBody(new String(data, StandardCharsets.UTF_8));
                        } else {
                            Base64.Encoder encoder = Base64.getEncoder();
                            State.tempConnection.getRequestMessage().setEntityBody(encoder.encodeToString(data));
                        }
                        State.input = Input.INIT;
                        break;
                    } catch (FileNotFoundException e) {
                        System.out.println("client <<ERROR>> : File Not Found.");
                        e.printStackTrace();
                        return false;
                    } catch (IOException e) {
                        System.out.println("client <<ERROR>> : IO Exception.");
                        e.printStackTrace();
                        return false;
                    }
                } else {
                    return false;
                }
                break;

            default:
                return false;
        }
        return true;
    }

}
