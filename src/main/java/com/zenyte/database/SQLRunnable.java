package com.zenyte.database;

public abstract class SQLRunnable implements Runnable {

    public SQLRunnable() {
    }

    public abstract void execute(final DatabaseCredential auth);

    public void prepare() {
        DatabasePool.submit(this);
    }

    @Override
    public void run() {
        prepare();
    }
}

