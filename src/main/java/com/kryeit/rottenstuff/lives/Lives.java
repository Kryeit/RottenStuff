package com.kryeit.rottenstuff.lives;

import com.kryeit.rottenstuff.storage.Database;
import org.jdbi.v3.core.Jdbi;

import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

public record Lives(UUID player, int lives, Rank rank, Timestamp week) {

    public static Lives add(UUID player, int amount) {
        Jdbi jdbi = Database.getJdbi();
        return jdbi.withHandle(handle -> {
            Lives currentLives = handle.createQuery("SELECT * FROM lives WHERE uuid = :uuid")
                    .bind("uuid", player)
                    .mapTo(Lives.class)
                    .findOne()
                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));

            int maxLives = currentLives.rank().getMaxLives();
            int newLivesCount = Math.min(currentLives.lives() + amount, maxLives);

            handle.createUpdate("UPDATE lives SET lives = :lives WHERE uuid = :uuid")
                    .bind("lives", newLivesCount)
                    .bind("uuid", player)
                    .execute();

            return new Lives(player, newLivesCount, currentLives.rank(), currentLives.week());
        });
    }

    public static Lives remove(UUID player, int amount) {
        Jdbi jdbi = Database.getJdbi();
        return jdbi.withHandle(handle -> {
            Lives currentLives = handle.createQuery("SELECT * FROM lives WHERE uuid = :uuid")
                    .bind("uuid", player)
                    .mapTo(Lives.class)
                    .findOne()
                    .orElseThrow(() -> new IllegalArgumentException("Player not found"));

            int maxLives = currentLives.rank().getMaxLives();
            int newLivesCount = Math.min(currentLives.lives() - amount, maxLives);
            if (newLivesCount < 0) {
                newLivesCount = 0;
            }

            handle.createUpdate("UPDATE lives SET lives = :lives WHERE uuid = :uuid")
                    .bind("lives", newLivesCount)
                    .bind("uuid", player)
                    .execute();

            return new Lives(player, newLivesCount, currentLives.rank(), currentLives.week());
        });
    }

    public static Lives get(UUID player) {
        Jdbi jdbi = Database.getJdbi();
        return jdbi.withHandle(handle -> {
            try {
                // First check if player exists
                boolean exists = handle.createQuery("SELECT COUNT(*) FROM lives WHERE uuid = :uuid")
                        .bind("uuid", player)
                        .mapTo(Integer.class)
                        .one() > 0;

                if (exists) {
                    // Get data with individual column queries to avoid mapper issues
                    int lives = handle.createQuery("SELECT lives FROM lives WHERE uuid = :uuid")
                            .bind("uuid", player)
                            .mapTo(Integer.class)
                            .one();

                    String rankStr = handle.createQuery("SELECT rank FROM lives WHERE uuid = :uuid")
                            .bind("uuid", player)
                            .mapTo(String.class)
                            .one();

                    Timestamp week = handle.createQuery("SELECT week FROM lives WHERE uuid = :uuid")
                            .bind("uuid", player)
                            .mapTo(Timestamp.class)
                            .one();

                    return new Lives(player, lives, Rank.valueOf(rankStr), week);
                } else {
                    return create(player);
                }
            } catch (Exception e) {
                return create(player);
            }
        });
    }

    public static Lives create(UUID player) {
        Lives newLives = new Lives(player, Rank.BAND_AID.getMaxLives(), Rank.BAND_AID, Timestamp.from(Instant.now())); // Default values with current timestamp
        Jdbi jdbi = Database.getJdbi();
        jdbi.useHandle(handle ->
                handle.createUpdate("INSERT INTO lives (uuid, lives, rank, week) VALUES (:uuid, :lives, :rank, :week)")
                        .bind("uuid", newLives.player())
                        .bind("lives", newLives.lives())
                        .bind("rank", newLives.rank().name())
                        .bind("week", newLives.week())
                        .execute()
        );
        return newLives;
    }

    public static Lives updateWeekTimestamp(UUID player) {
        Jdbi jdbi = Database.getJdbi();
        return jdbi.withHandle(handle -> {
            Lives currentLives = get(player);
            Timestamp newTimestamp = new Timestamp(System.currentTimeMillis());

            handle.createUpdate("UPDATE lives SET week = :week WHERE uuid = :uuid")
                    .bind("week", newTimestamp)
                    .bind("uuid", player)
                    .execute();

            return new Lives(player, currentLives.lives(), currentLives.rank(), newTimestamp);
        });
    }

    public enum Rank {
        BAND_AID(3),
        REFLEX(10),
        ;

        private int maxLives;

        Rank(int maxLives) {
            this.maxLives = maxLives;
        }

        public int getMaxLives() {
            return maxLives;
        }
    }
}