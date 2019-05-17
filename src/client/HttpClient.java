package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * 客户端的主类
 *
 * @author liuxingchi
 */
public class HttpClient {

    public static void main(String[] args) {
        try {
            System.out.println("client <<INFO>> : Client is ready.");
            do {
                State.suggest();
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                String s = br.readLine();
                if (!new Command(s).isValid()) {
                    System.out.println("client <<WARNING>> : Invalid command, please enter a new command.");
                }
            } while (!State.quit);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
