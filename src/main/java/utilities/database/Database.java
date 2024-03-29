package utilities.database;

import utilities.FileOptions;

import java.io.*;
import java.sql.*;
import java.text.ParseException;
import java.util.*;
import java.util.Date;

/**
 * Created by jarndt on 5/8/17.
 */
public class Database {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IOException {
//        stopDatabase();
        DATABASE_TYPE = HSQL;
//        Database d = Database.newDatabaseConnection();
//        d.databaseType = HSQL;
//        d.executeQuery("DROP TABLE CATEGORIES CASCADE;");
//        try {
////            d.executeQuery("insert into users(USER_ID,password,first_name,email_address)values('a','a','a','a');");
//            d.executeQuery(
//                    "insert into USERS(user_id,first_name,last_name,password,email_address,user_group,date_created,date_last_login,is_verified)" +
//                            " values(?,?,?,?,?,?,?,?,?);",
//                    Arrays.asList("ald;fsa","fn","ln","pass","email","admin",new Date(),new Date(),true)
//            );
//        }catch (Exception e){e.printStackTrace();}
//        d.executeQuery("select * from users")
//                .forEach(System.out::println);

//        for(Category cust : repository.findAll()){
//        Database.getExistingDatabaseConnection().executeQuery("DROP SCHEMA PUBLIC CASCADE");
        System.out.println(Database.getExistingDatabaseConnection().executeQuery("select * from problems"));

        System.exit(0);
    }

    private static Database databaseConnection;

    public static synchronized Database getExistingDatabaseConnection() throws SQLException, IOException, ClassNotFoundException {
        if (databaseConnection == null)
            databaseConnection = newDatabaseConnection();
        return databaseConnection;
    }

    public static Database newDatabaseConnection() throws SQLException, IOException, ClassNotFoundException {
        return new Database(DATABASE, USERNAME, PASSWORD);
    }


//    private static final Logger LOGGER = LogManager.getLogger(Database.class.getName());
    public static final String DATABASE = "localhost:5432/app", USERNAME = "app_user", PASSWORD = "app";

    public static final String POSTGRES = "POSTGRES", HSQL = "HSQL";

    public static String DATABASE_TYPE = HSQL;

    public static boolean checkForPostgres(String server, String username, String password) { /*127.0.0.1:5432/testdb*/
        try {
//            DriverManager.getConnection()
            Class.forName("org.postgresql.Driver");
            Connection connection = DriverManager.getConnection(
                    "jdbc:postgresql://" + server, username, password);
            if (connection != null)
                return true;
        } catch (Exception e) {
//            LOGGER.log(Level.DEBUG, "", e); /* */
        }
        return false;
    }

    public static String SQL_SCRIPT_DIR = FileOptions.cleanFilePath(FileOptions.getDefaultDir() + "/database_config/");
    private Connection connection;
    private String server, username, password;

    public Database(String server, String username, String password) throws SQLException, ClassNotFoundException, IOException {
        this.server = server;
        this.username = username;
        this.password = password;
        getConnection();
        String file = null;
        try {
            try {
                executeQuery(file = FileOptions.readResourceAsStreamIntoString("../database_config/create_tables.sql"));
            } catch (NullPointerException npe) {
                //File not found...
//                LOGGER.log(Level.ERROR, "using string:" + npe.getMessage());
//                executeQuery(sqlFile);
            }
        } catch (Exception e) {
            if (!e.getMessage().equals("type not found or user lacks privilege: SERIAL"))
                System.out.println(file+"\n\t"+e);
//                LOGGER.error(file, e);
            else
                e.printStackTrace();
        }
    }

    int attempt = 0;

    private Connection getConnection() throws SQLException {
        if (connection == null) {
            if (DATABASE_TYPE.equals(POSTGRES)) {
                try {
                    Class.forName("org.postgresql.Driver");
                    try {
                        connection = DriverManager.getConnection(
                                "jdbc:postgresql://" + server, username, password);
                        connection.setAutoCommit(true);
                    } catch (Exception e) {
//                        if (e.getMessage().contains("Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections."))
//                            startDatabase();
                        if (e.getMessage().equals("FATAL: database \"app\" does not exist"))
                            init();
                        if (e.getMessage().equals("FATAL: role \"app_user\" does not exist"))
                            init();
                        else
                            System.out.println(e);
//                            LOGGER.log(Level.ERROR, e);
                    }
                } catch (ClassNotFoundException | IOException e) {
                    e.printStackTrace();
                }
            }
            if (DATABASE_TYPE.equals(HSQL) || attempt++ > 5) {
                System.out.println("Attempted to connect and start postgres 5 times, temporarily going to use hsql now....  " +
                        "\n\tFix Postgres.  All future database transactions will be to a HSQL database." +
                        "\nNext database connection will attempt to connect to postgres again.  On next successful" +
                        "\npostgres connection will dump HSQL data to postgres.");
                try {
                    newHSQLConnection();
                    DATABASE_TYPE = HSQL;
                } catch (IOException e) {
                    System.out.println("ERROR Starting HSQL database");
                }
                return null;
            }
            return getConnection();
        }
        return connection;
    }

    private void newHSQLConnection() throws IOException, SQLException {
        connection = HSQLDBCommons.getDatabase().getDBConn();
    }

    private void init() throws ClassNotFoundException, SQLException, IOException {
        Class.forName("org.postgresql.Driver");
        Connection conn = DriverManager.getConnection("jdbc:postgresql://localhost:5432/postgres", "postgres", "postgres");
        try {
            conn.prepareCall("CREATE USER APP_USER WITH PASSWORD 'app';")
                    .executeUpdate();
        } catch (Exception e) {
            if (!e.getMessage().equals("ERROR: role \"app_user\" already exists"))
                throw e;
        }
        try {
            conn.prepareCall("CREATE DATABASE app OWNER = APP_USER; ")
                    .executeUpdate();
        } catch (Exception e) {
            if (!e.getMessage().equals("ERROR: database \"app\" already exists"))
                throw e;
        }
    }

    private java.text.DateFormat format = new java.text.SimpleDateFormat("yyyyMMddHHmmss");

    public List<Map<String, Object>> executeQuery(String query, List<Object> values) throws SQLException, ParseException {
        PreparedStatement prep = getConnection().prepareStatement(query);
        for (int i = 1; i <= values.size(); i++)
            if (values.get(i - 1) instanceof Date) {
                java.util.Date date = format.parse(((Date) values.get(i - 1)).getTime() + "");
                java.sql.Timestamp timestamp = new java.sql.Timestamp(date.getTime());
                prep.setTimestamp(i, timestamp);
            } else if (values.get(i - 1) instanceof byte[]) {
                prep.setBytes(i, (byte[]) values.get(i - 1));
            } else
                prep.setObject(i, values.get(i - 1));
        return executeQuery(prep.toString());
    }

    public List<Map<String, Object>> executeQuery(String query) throws SQLException {
        Statement stmt = getConnection().createStatement();
        if (DATABASE_TYPE.equals(HSQL)) {
            query.replaceAll("SERIAL", "IDENTITY");
        }
        ResultSet rs = null;
        try {
            for (String s : query.split(";(?=(?:[^']*'[^']*')*[^']*$)"))
                try {
                    if (s.replaceAll("[\\s|\t|\n]", "").isEmpty())
                        continue;
                    if (Arrays.asList("DROP ", "INSERT ", "UPDATE ", "DELETE ", "CREATE ").stream().filter(a -> s.toLowerCase().contains(a.toLowerCase())).count() == 0)
                        rs = stmt.executeQuery(s);
                    else
                        stmt.executeUpdate(s);
                } catch (Exception e) {
                    if (Arrays.asList("\"USERS\"", "\"EVENTS\"", "\"CATEGORIES\"", "\"MESSAGES\"").stream().filter(a -> e.getMessage().contains(a)).count() != 0)
                        try {
                            init();
                        } catch (ClassNotFoundException | IOException e1) {
                            e1.printStackTrace();
                        }
                    else if (Arrays.asList("\"USERS\"", "\"EVENTS\"", "\"CATEGORIES\"", "\"MESSAGES\"").stream().filter(a -> e.getMessage().equals("ERROR: relation \"" + a.toLowerCase() + "\" does not exist")).count() != 0)
                        try {
                            //create scrips //init doesn't do that though
                            init();
                        } catch (ClassNotFoundException | IOException e1) {
                            e1.printStackTrace();
                        }
                    else {
//                        LOGGER.log(Level.ERROR, s, e);
                        throw new SQLException(s, e);
                    }
                }
        } catch (SQLException sql) {
            if (sql.getMessage().contains("Table already exists")
                    || sql.getMessage().contains("No results were returned by the query")) {
                return null;
            } else if (sql.getMessage().contains("Unexpected token: POSITION in statement")) {
                rs = stmt.executeQuery(query.toUpperCase().replace("POSITION", "\"POSITION\""));
            } else {
                throw sql;
            }
        }
        if (rs == null)
            return null;
        List<Map<String, Object>> results = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> subMap = new HashMap<String, Object>();
            ResultSetMetaData rsmd = rs.getMetaData();
            for (int i = 1; i <= rsmd.getColumnCount(); i++) {
                subMap.put(rsmd.getColumnLabel(i).toLowerCase(), rs.getObject(i));
            }
            results.add(subMap);
        }
        return results;
    }
}