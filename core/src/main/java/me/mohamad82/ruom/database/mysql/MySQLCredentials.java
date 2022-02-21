package me.mohamad82.ruom.database.mysql;

public class MySQLCredentials {

    private final String username;
    private final String password;
    private final String url;

    private MySQLCredentials(String url, String username, String password) {
        this.url = url;
        this.username = username;
        this.password = password;
    }

    public static MySQLCredentials mySQLCredentials(String address, int port, String database, boolean useSSL, String username, String password) {
        return new MySQLCredentials(String.format("jdbc:mysql://%s:%s/%s?useSSL=%s", address, port, database, useSSL), username, password);
    }

    public static MySQLCredentials mySQLCredentials(String url, String username, String password) {
        return new MySQLCredentials(url, username, password);
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

}
