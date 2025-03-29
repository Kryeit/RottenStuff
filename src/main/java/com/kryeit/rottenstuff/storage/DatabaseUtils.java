package com.kryeit.rottenstuff.storage;

public class DatabaseUtils {
    public static void createTables() {
        Database.getJdbi().withHandle(
                handle -> handle.execute(
                        """
                            CREATE TABLE IF NOT EXISTS lives (
                                uuid UUID PRIMARY KEY,
                                lives INTEGER NOT NULL,
                                rank VARCHAR(255) NOT NULL,
                                week TIMESTAMP NOT NULL
                            )
                        """
                )
        );
    }
}
