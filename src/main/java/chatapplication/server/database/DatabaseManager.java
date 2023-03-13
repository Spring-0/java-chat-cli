package chatapplication.server.database;

import chatapplication.common.models.User;
import java.sql.*;

public class DatabaseManager {

    private final Connection DB_CONNECTION;
    private final Statement STMT;

    public DatabaseManager(){
        DB_CONNECTION = DatabaseConnection.getInstance().getConnection();
        try {
            this.STMT = DB_CONNECTION.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Check if user is already registered
    public boolean isReturningUser(String username) {
        try {
            PreparedStatement ps = DB_CONNECTION.prepareStatement("SELECT * FROM Users WHERE username = ?;");
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
            STMT.execute(String.format("INSERT INTO Users(username, password) VALUES('%s', '%s')", user.getUsername(), user.getPasswd()));
        } catch (SQLException e){
            System.out.println("There was an error adding user: " + user.getUserID() + " to the database.");
            e.printStackTrace();
        }

    }


    // Authenticate user credentials in database
    public boolean authenticate(String username, String passwd){
        try {
            String sql = "SELECT * FROM Users WHERE username = ? AND password = ?;";
            PreparedStatement preparedStatement = DB_CONNECTION.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, passwd);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    // Update user username
    public void updateUsername(String username, String newUsername){
        try{
            final String UPDATE = "Update users SET username = '%s' WHERE username = '%s'";
            STMT.execute(String.format(UPDATE, newUsername, username));
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    // Update user passwd
    public void updatePassword(String username, String passwd){
        try {
            final String UPDATE = "UPDATE users SET password = '%s' WHERE username = '%s'";
            STMT.execute(String.format(UPDATE, passwd, username));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}
