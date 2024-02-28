package sepc.sample;

import sepc.sample.DB.DbClient;

public class App {
    public static void main(String[] args) {
        // Credentials are hardcoded physically each time env will be added soon

        // try {
        // make sure to provide sql server credentials
        // DbClient.runSqlFileToCreateTables();
        // System.out.println("Database setup successfully.");
        // } catch (Exception e) {
        // e.printStackTrace();
        // }

        // Hardocded
        String hostname = "sept.oddsmatrix.com";
        int port = 7000;
        int port2 = 8081;
        Long timeout = 3000000L;
        String subscription = "Monkey_Tilt";
        System.out.println("Openening new connection");
        // Uncomment which connector type you want to use
        new PushConnector(hostname, port, subscription);
        // new PullConnector(hostname, port2, subscription, timeout);

    }

}
