package projectServer.server;

import java.sql.*;

public class Database {

    private static Connection connection;
    private static Statement statement;
    private static PreparedStatement ps;
    private static String insertStatement = "insert into users (login, password, nick, secret) values (?, ?, ?, ?);";
    private static final String DB_CONNECTION_STRING = "jdbc:sqlite:db/users.db";
    private static final String CREATE_REQUEST = "create table if not exists users " +
            "(id integer primary key autoincrement, login varchar(20), password varchar(20), nick varchar(20), secret varchar(20));";

    public Database() {
        try {
            connect();
         //   createTable();
          //  addUsers();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
          //  disconnect();
        }
    }

//    public static void main(String[] args) throws SQLException {
//        try {
//            connect();
//            createTable();
//            //  addUsers();
//            var nick = searchUser("log1", "pass1");
//            System.out.println(nick);
//        } catch (SQLException e) {
//            e.printStackTrace();
//
//        }
//    }

    public void addUsers() throws SQLException {
        connection.setAutoCommit(false);
        addOneUser("log1", "pass1", "nick1", "secret1");
        addOneUser("log2", "pass2", "nick2", "secret2");
        addOneUser("log3", "pass3", "nick3", "secret3");
        addOneUser("log4", "pass4", "nick4", "secret4");
        addOneUser("log5", "pass5", "nick5", "secret5");
        connection.setAutoCommit(true);
    }

    public void addOneUser(String login, String password, String nick, String secret) throws SQLException {
        ps.setString(1, login);
        ps.setString(2, password);
        ps.setString(3, nick);
        ps.setString(4, secret);
        ps.executeUpdate();
    }

    public String searchUser(String login, String password)  throws SQLException  {
        String nick;
        try (var resultSet = statement.executeQuery("select * from users where login = \'" + login + "\' and password = \'" + password + "\';")) {
                   nick = resultSet.getString("nick");
        }
        return nick;
    }

    public boolean registrationSuccess(String login, String nick) throws SQLException {
        try (var searchLogin = statement.executeQuery("select id from users where login = \'" + login + "\';")) {
            if (searchLogin.next()) {
                return false;
            }
            else {
                var searchNick = statement.executeQuery("select id from users where nick = \'" + nick + "\';");
                if (searchNick.next()) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean freeNickSuccess(String oldNick, String newNick) throws SQLException {
        try (var searchNick = statement.executeQuery("select id from users where nick = \'" + newNick + "\';")) {
            if (searchNick.next()) {
                return false;
            }
            changeNick(oldNick, newNick);
            return true;
        }
    }

    private void changeNick(String oldNick, String newNick) throws SQLException {
        statement.executeUpdate("update users set nick = \'" + newNick + "\' where nick = \'" + oldNick + "\';");
    }



    private static void createTable() throws SQLException {
        statement.execute(CREATE_REQUEST);
    }

    private static void connect() throws SQLException   {
        connection = DriverManager.getConnection(DB_CONNECTION_STRING);
        statement = connection.createStatement();
        ps = connection.prepareStatement(insertStatement);
    }

    private static void disconnect() {
        try {
            if (statement != null) statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (ps != null) ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (connection != null) connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
