package com.rayan.saasapp.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DashboardStatsResponse {
    private long totalProducts;
    private long totalCategories;
    private long totalStockMovements;
    private long lowStockAlerts;
}
