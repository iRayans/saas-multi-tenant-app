package com.rayan.saasapp.controllers;

import com.rayan.saasapp.response.DashboardStatsResponse;
import com.rayan.saasapp.services.DashboardStats;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
public class DashboardStatsController {

    final private DashboardStats dashboardStats;

    @GetMapping
    public ResponseEntity<DashboardStatsResponse> getDashboardStats() {
        DashboardStatsResponse response = dashboardStats.getStats();
        return ResponseEntity.ok(response);
    }
}
