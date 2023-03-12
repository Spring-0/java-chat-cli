package chatapplication.server.database;

import chatapplication.common.models.User;

import java.sql.*;
import java.util.Arrays;

public class DatabaseManager {

    private Connection dbConnection;
    private Statement stmt;

    private final String QUERY = "SELECT * FROM Users WHERE user_id == '%s'";
    private final String INSERT = "INSERT INTO Users(username, password) VALUES('%s', '%s')";

    public DatabaseManager(){
        dbConnection = DatabaseConnection.getInstance().getConnection();
        try {
            this.stmt = dbConnection.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Check if user is already registered
    public boolean isReturningUser(String username) {
        try {
            PreparedStatement ps = dbConnection.prepareStatement("SELECT * FROM Users WHERE username = ?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    // Add user to database
    public void createUserEntry(User user){
        try{
            // TODO: Encrypt passwords
            stmt.execute(String.format("INSERT INTO Users(username, password) VALUES('%s', '%s')", user.getUsername(), user.getPasswd()));
        } catch (SQLException e){
            System.out.println("There was an error adding user: " + user.getUserID() + " to the database.");
            e.printStackTrace();
        }

    }


    // Authenticate user credentials in database
    public boolean authenticate(String username, String passwd){
        try {
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?;";
            PreparedStatement preparedStatement = dbConnection.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwd);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}
