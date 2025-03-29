package com.kryeit.rottenstuff.lives;

import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.statement.StatementContext;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class LivesMapper implements RowMapper<Lives> {
    @Override
    public Lives map(ResultSet rs, StatementContext ctx) throws SQLException {
        return new Lives(
                UUID.fromString(rs.getString("uuid")),
                rs.getInt("lives"),
                Lives.Rank.valueOf(rs.getString("rank")),
                rs.getTimestamp("week")
        );
    }
}