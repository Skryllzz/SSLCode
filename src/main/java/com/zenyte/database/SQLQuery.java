package com.zenyte.database;

public class SQLQuery {
    private SQLRunnable query;
    private final DatabaseCredential database;

    public SQLQuery(final DatabaseCredential database, final SQLRunnable query) {
        this.query = query;
        this.database = database;
    }

    public SQLRunnable getQuery() {
        return this.query;
    }

    public void setQuery(final SQLRunnable query) {
        this.query = query;
    }

    public DatabaseCredential getDatabase() {
        return this.database;
    }
}
