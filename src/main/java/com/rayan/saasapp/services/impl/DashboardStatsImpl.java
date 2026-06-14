package com.rayan.saasapp.services.impl;

import com.rayan.saasapp.repositories.CategoryRepository;
import com.rayan.saasapp.repositories.ProductRepository;
import com.rayan.saasapp.repositories.StockMvtRepository;
import com.rayan.saasapp.response.DashboardStatsResponse;
import com.rayan.saasapp.services.DashboardStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardStatsImpl implements DashboardStats {

    private final ProductRepository productRepository;
    private final StockMvtRepository stockMvtRepository;
    private final CategoryRepository categoryRepository;


    @Override
    public DashboardStatsResponse getStats() {
        final long productCount = productRepository.countByDeletedFalse();
        final long stockMvtCount = stockMvtRepository.countByDeletedFalse();
        final long categoryCount = categoryRepository.countByDeletedFalse();
        final long lowStockProductCount = productRepository.countLowStockProducts();

        DashboardStatsResponse response = DashboardStatsResponse.builder()
                .totalStockMovements(productCount)
                .lowStockAlerts(stockMvtCount)
                .totalCategories(categoryCount)
                .totalProducts(lowStockProductCount)
                .build();
        return response;
    }
}
