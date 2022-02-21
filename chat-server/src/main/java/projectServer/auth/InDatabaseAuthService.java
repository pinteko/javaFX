package projectServer.auth;

import projectServer.entity.User;
import projectServer.error.WrongCredentialsException;
import projectServer.server.Database;

import java.sql.SQLException;

public class InDatabaseAuthService implements AuthService {

    private Database database;

    public InDatabaseAuthService() {
        database = new Database();
    }

    @Override
    public void start() {
        System.out.println("Auth service started");
    }

    @Override
    public void stop() {
        System.out.println("Auth service stopped");
    }

    @Override
    public String authorizeUserByLoginAndPassword(String login, String password) {
        try {
            return database.searchUser(login, password);
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new WrongCredentialsException("Wrong username or password");
        }
    }

    @Override
    public String changeNick(String oldNick, String newNick) {
        String nick = oldNick;
        try {
            if (database.freeNickSuccess(oldNick, newNick)) {
            nick = newNick;
            }
        }
        catch (SQLException e) {
            e.printStackTrace();
            throw new WrongCredentialsException("This nick already used!");
        }
        return nick;
    }

    @Override
    public User createNewUser(String login, String password, String nick, String secret) {
        User newUser = null;
            try {
                if (database.registrationSuccess(login, nick)) {
                    database.addOneUser(login, password, nick, secret);
                    newUser = new User(login, password, nick, secret);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                throw new WrongCredentialsException("This login or nick already used!");
            }
            return newUser;
    }

    @Override
    public void deleteUser(String login, String pass) {

    }

    @Override
    public void changePassword(String login, String oldPass, String newPass) {

    }

    @Override
    public void resetPassword(String login, String newPass, String secret) {

    }
}
