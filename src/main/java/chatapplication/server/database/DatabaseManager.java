package chatapplication.server.database;

import chatapplication.common.models.GroupChat;
import chatapplication.common.models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;

public class DatabaseManager {

    private ArrayList<GroupChat> groupChats;


    private final Connection DB_CONNECTION;
    private final Statement STMT;

    public DatabaseManager(){
        DB_CONNECTION = DatabaseConnection.getInstance().getConnection();
        groupChats = new ArrayList<>();
        try {
            this.STMT = DB_CONNECTION.createStatement();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    // Check if user is already registered
    public boolean usernameExists(String username) {
        try {
            PreparedStatement ps = DB_CONNECTION.prepareStatement("SELECT * FROM Users WHERE username = ?;");
            ps.setString(1, username.toLowerCase());
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
            STMT.execute(String.format("INSERT INTO Users(username, password) VALUES('%s', '%s')", user.getUsername().toLowerCase(), user.getPasswd()));
        } catch (SQLException e){
            System.out.println("There was an error adding user: " + user.getUserID() + " to the database.");
            e.printStackTrace();
        }

    }

    public void addGroupChat(GroupChat groupChat){
        if(!groupChats.contains(groupChat)){
            groupChats.add(groupChat);
            String groupName = groupChat.getGroupName();
            ArrayList<User> groupChatMembers = groupChat.getReceivers();
            String sql = "INSERT INTO group_chats (id, name) VALUES (?, ?)";
            try{
                PreparedStatement preparedStatement = DB_CONNECTION.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                preparedStatement.setInt(1, groupChat.getGroupID());
                preparedStatement.setString(2, groupName);
                preparedStatement.executeUpdate();
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                int groupChatID = -1;
                if (resultSet.next()) {
                    groupChatID = resultSet.getInt(1);
                }
                if (groupChatID != -1) {
                    String sql2 = "INSERT INTO user_group_chats (user_id, group_chat_id) VALUES (?, ?)";
                    preparedStatement = DB_CONNECTION.prepareStatement(sql2);
                    for(User receiver : groupChatMembers){
                        int userID = receiver.getUserID();
                        preparedStatement.setInt(1, userID);
                        preparedStatement.setInt(2, groupChatID);
                        preparedStatement.executeUpdate();
                    }
                }
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    public void addUserToGroup(User user, GroupChat groupChat){
        String sql = "INSERT INTO user_group_chats (user_id, group_chat_id) VALUES(?, ?)";
        try{

            PreparedStatement preparedStatement = DB_CONNECTION.prepareStatement(sql);
            preparedStatement.setInt(1, user.getUserID());
            preparedStatement.setInt(2, groupChat.getGroupID());
            System.out.println(groupChat.getGroupID());
            preparedStatement.executeUpdate();

        } catch (SQLException e){
            e.printStackTrace();
        }
    }

    public ArrayList<GroupChat> getGroupChatsByUser(User user) {
        ArrayList<GroupChat> userGroupChats = new ArrayList<>();
        HashSet<Integer> groupChatIDs = new HashSet<>();
        String sql = "SELECT gc.id, gc.name FROM group_chats gc JOIN user_group_chats gcm ON gc.id = gcm.group_chat_id WHERE gcm.user_id = ?";
        String sql1 = "SELECT u.username, u.password, u.user_id FROM users u JOIN user_group_chats gcm ON u.user_id = gcm.user_id WHERE gcm.group_chat_id = ?";
        try {
            PreparedStatement preparedStatement = DB_CONNECTION.prepareStatement(sql);
            preparedStatement.setInt(1, user.getUserID());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                int groupID = resultSet.getInt("id");
                String groupName = resultSet.getString("name");
                ArrayList<User> receivers = new ArrayList<>();

                PreparedStatement preparedStatement1 = DB_CONNECTION.prepareStatement(sql1);
                preparedStatement1.setInt(1, groupID);
                ResultSet resultSet1 = preparedStatement1.executeQuery();

                while (resultSet1.next()) {
                    String username = resultSet1.getString("username");
                    System.out.println("From database: " + username);
                    receivers.add(User.getUserByUsername(username));
                }

                resultSet1.close();

                if(!groupChatIDs.contains(groupID)){
                    GroupChat groupChat = new GroupChat(receivers);
                    groupChat.setGroupName(groupName);
                    groupChat.setGroupID(groupID);

                    userGroupChats.add(groupChat);
                    groupChatIDs.add(groupID);
                }


            }
            resultSet.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return userGroupChats;
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

    public int getUserID(String username){
        try{

            String sql = "SELECT * FROM Users WHERE username = ?";
            PreparedStatement preparedStatement = DB_CONNECTION.prepareStatement(sql);
            preparedStatement.setString(1, username.toLowerCase());
            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                return resultSet.getInt("user_id");
            }


        } catch (SQLException e){
            e.printStackTrace();
        }
        return 0;
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
