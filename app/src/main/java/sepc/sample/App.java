package sepc.sample;

public class App {
    public static void main(String[] args) {

        // Hardocded
        // String hostname = "";
        // int port = ;
        // int port2 = ;
        Long timeout = 3000000L;
        String subscription = "Monkey_Tilt";
        System.out.println("Openening new connection");
        // Uncomment which connector type you want to use
        new PushConnector(hostname, port, subscription);
        // new PullConnector(hostname, port2, subscription, timeout);

    }

}
