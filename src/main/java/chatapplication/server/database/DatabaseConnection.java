package chatapplication.server.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {

    private static DatabaseConnection instance;
    private Connection conn;

    private final String DB_NAME = "";
    private final String DB_USERNAME = "";
    private final String DB_PASSWORD = "";
    private final int DB_PORT = 3306;


    private DatabaseConnection(){
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.conn = DriverManager.getConnection(String.format("jdbc:mysql://localhost:%d/%s?user=%s&password=%s", DB_PORT, DB_NAME, DB_USERNAME, DB_PASSWORD));
            System.out.println("Created conn");
        } catch(ClassNotFoundException | SQLException e){
            e.printStackTrace();
        }
    }


    public static DatabaseConnection getInstance(){
        if(instance == null){
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection(){
        return conn;
    }

}
