package com.zenyte.database;

public enum DatabaseCredential {
    LOCAL("localhost", "zenyte", "xx01$xz$Zenyte!Discord"),
    BETA("172.50.1.4", "zenyte", "cde#Zenyte!xsw@Database!zaq!Password");
    private final String host;
    private final String user;
    private final String pass;

    DatabaseCredential(String host, String user, String pass) {
        this.host = host;
        this.user = user;
        this.pass = pass;
    }

    public String getHost() {
        return this.host;
    }

    public String getUser() {
        return this.user;
    }

    public String getPass() {
        return this.pass;
    }
}
