import com.sforce.soap.apex.CompileClassResult;
import com.sforce.soap.apex.Connector;
import com.sforce.soap.apex.SoapConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;

public class Runner {
    private SoapConnection connection;

    public static void main(String[] args) throws ConnectionException {
        new Runner().compileClassesSample();
    }
    public void compileClassesSample() throws ConnectionException {
        SoapConnection apexBinding = getConnectionToSalesForce();

        String p1 = "public class p1 {\n"
                + "public static Integer var1 = 0;\n"
                + "public static void methodA() {\n"
                + " var1 = 1;\n" + "}\n"
                + "public static void methodB() {\n"
                + " p2.MethodA();\n" + "}\n"
                + "}";
        String p2 = "public class p2 {\n"
                + "public static Integer var1 = 0;\n"
                + "public static void methodA() {\n"
                + " var1 = 1;\n" + "}\n"
                + "public static void methodB() {\n"
                + " p1.MethodA();\n" + "}\n"
                + "}";
        CompileClassResult[] r;
        r = apexBinding.compileClasses(new String[]{p1, p2});
        if (!r[0].isSuccess()) {
            System.out.println("Couldn't compile class p1 because: "
                    + r[0].getProblem());
        }
        if (!r[1].isSuccess()) {
            System.out.println("Couldn't compile class p2 because: "
                    + r[1].getProblem());
        }
    }

    public SoapConnection getConnectionToSalesForce(){
        if(connection != null){
            return connection;
        }else{
// Connect to SalesForce
            ConnectorConfig config = new ConnectorConfig();
            config.setUsername("username");
            config.setPassword("password");
//            config.setAuthEndpoint("");
            config.setTraceMessage(true);
            try {
                connection = Connector.newConnection(config);
                String sessionId = config.getSessionId();
                String authEndPoint = config.getAuthEndpoint();
                String serviceEndPoint = config.getServiceEndpoint();
                connection.getSessionHeader().setSessionId(sessionId);

// display current settings
                System.out.println("SessionId: " + sessionId);
                System.out.println("Auth EndPoint: "+ authEndPoint );
                System.out.println("Service EndPoint: "+ serviceEndPoint);
                System.out.println("Username: "+config.getUsername());
            }catch (ConnectionException e1) {
                e1.printStackTrace();
            }
        }
        return connection;
    }

}
