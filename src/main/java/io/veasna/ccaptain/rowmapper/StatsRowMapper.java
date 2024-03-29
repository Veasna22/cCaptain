package io.veasna.ccaptain.rowmapper;

import io.veasna.ccaptain.domain.Role;
import io.veasna.ccaptain.domain.Stats;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 3/11/23 18:10
 */
public class StatsRowMapper implements RowMapper<Stats> {
    @Override
    public Stats mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        return Stats.builder()
                .totalCustomers(resultSet.getInt("total_customers"))
                .totalInvoices(resultSet.getInt("total_invoices"))
                .totalBilled( resultSet.getDouble("total_billed"))
                .build();
    }
}
