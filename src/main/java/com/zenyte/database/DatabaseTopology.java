/**
 *
 */
package com.zenyte.database;

/**
 * @author Noele | Jun 19, 2018 : 12:14:15 AM
 * @see https://noeles.life || noele@zenyte.com
 */
public enum DatabaseTopology {
    LOCAL(DatabaseCredential.LOCAL),
    BETA(DatabaseCredential.BETA);
    private final DatabaseCredential[] nodes;

    DatabaseTopology(final DatabaseCredential... nodes) {
        this.nodes = nodes;
    }

    public DatabaseCredential[] getNodes() {
        return this.nodes;
    }
}
