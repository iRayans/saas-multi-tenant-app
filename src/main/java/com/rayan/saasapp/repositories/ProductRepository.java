package com.rayan.saasapp.repositories;

import com.rayan.saasapp.entites.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, String> {

    Optional<Product> findByReferenceIgnoreCase(String reference);

    long countByDeletedFalse();

    @Query("""
            SELECT COUNT(p) FROM Product p
            WHERE (
                SELECT COALESCE(SUM(CASE WHEN s.typeMvt = 'IN' THEN s.quantity ELSE -s.quantity END), 0)
                FROM StockMvt s
                WHERE s.product = p
            ) <= p.alertThreshold
            AND p.deleted = false
            """)
    long countLowStockProducts();
}
