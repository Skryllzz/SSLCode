package com.zenyte.database;

import com.zenyte.Constants;

import java.util.HashMap;

public class Database {
    public static HashMap<String, Database> databases = new HashMap<>();
    public static DatabasePool pool;
    private final DatabaseDetails details;

    public Database(final DatabaseDetails details) {
        this.details = details;
    }


    public enum DatabaseDetails {
        MAIN_LOCAL(DatabaseCredential.LOCAL, "zenyte_main"),
        FORUM_LOCAL(DatabaseCredential.LOCAL, "zenyte_forum"),
        MAIN_BETA(DatabaseCredential.BETA, "zenyte_main"),
        FORUM_BETA(DatabaseCredential.BETA, "zenyte_forum");
        private final DatabaseCredential auth;
        private final String database;
        public static final DatabaseDetails[] VALUES = values();

        DatabaseDetails(final DatabaseCredential auth, final String database) {
            this.auth = auth;
            this.database = database;
        }

        public DatabaseCredential getAuth() {
            return this.auth;
        }

        public String getDatabase() {
            return this.database;
        }
    }

    public static void preload() {
        external:
        for (final DatabaseDetails entry : DatabaseDetails.VALUES) {
            for (DatabaseCredential auth : Constants.FAILOVER.getNodes())
                if (auth != entry.getAuth()) continue external;
            if (entry.getDatabase() != null) databases.put(entry.getDatabase(), new Database(entry));
        }
    }

    public DatabaseDetails getDetails() {
        return this.details;
    }
}
