package com.rayan.saasapp.repositories;

import com.rayan.saasapp.entites.StockMvt;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockMvtRepository extends JpaRepository<StockMvt, String> {
    Page<StockMvt> findAllByProductId(String productId, Pageable pageable);

    long countByDeletedFalse();


}
