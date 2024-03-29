package io.veasna.ccaptain.query;

/**
 * @author Veasna
 * @version 1.0
 * @license Veasna , LLC
 * @since 26/3/24 10:44
 */
public class CustomerQuery {

    public static final String STATS_QUERY = "select c.total_customers, i.total_invoices, inv.total_billed FROM (SELECT COUNT(*) total_customers FROM customer) c,(SELECT COUNT(*) total_invoices FROM invoice) i, (SELECT ROUND(SUM(total)) total_billed FROM invoice) inv";
}
