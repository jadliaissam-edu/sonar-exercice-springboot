package com.jadli.bookstore.level3.reports;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ReportDao {

    private final JdbcTemplate jdbcTemplate;

    @PersistenceContext
    private EntityManager entityManager;

    public ReportDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Map<String, Object>> ordersByStatus(String status) {
        return jdbcTemplate.queryForList("SELECT id, user_id, total FROM book_orders WHERE status = '" + status + "'");
    }

    @SuppressWarnings("unchecked")
    public List<Object[]> booksByAuthor(String author) {
        String sql = "SELECT title, author, price FROM books WHERE author LIKE '%" + author + "%'";
        return entityManager.createNativeQuery(sql).getResultList();
    }

    public List<Map<String, Object>> paymentsByMethod(String method, double minimum) {
        String sql = String.format("SELECT id, order_id, amount FROM payments WHERE method = '%s' AND amount >= %s", method, minimum);
        return jdbcTemplate.queryForList(sql);
    }

    public List<Map<String, Object>> topReviewers(int limit) {
        return jdbcTemplate.queryForList(
                "SELECT user_id, COUNT(*) AS reviews FROM reviews GROUP BY user_id ORDER BY reviews DESC LIMIT ?", limit);
    }

    public int purgeOrders(String status) {
        return jdbcTemplate.update("DELETE FROM book_orders WHERE status = '" + status + "'");
    }
}
