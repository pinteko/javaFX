package commands;

public enum Command {
    BROADCAST_MESSAGE("/broadcast"),
    LIST_USERS("/list"),
    PRIVATE_MESSAGE("/private"),
    LOGIN("/login"),
    ERROR("/error"),
    AUTH_OK("/auth_ok"),
    CHANGE_PASS_OK("/change_pass_ok"),
    CHANGE_NICK_OK("/change_nick_ok");

    private String command;

    Command(String command) {
        this.command = command;
    }

    public String getCommand() {
        return command;
    }
}