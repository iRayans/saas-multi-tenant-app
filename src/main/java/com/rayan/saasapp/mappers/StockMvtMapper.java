package com.rayan.saasapp.mappers;

import com.rayan.saasapp.entites.Product;
import com.rayan.saasapp.entites.StockMvt;
import com.rayan.saasapp.requests.StockMvtRequest;
import com.rayan.saasapp.response.StockMvtResponse;
import org.springframework.stereotype.Component;

@Component
public class StockMvtMapper {

    public StockMvt toEntity(StockMvtRequest request) {
        return StockMvt.builder()
                .dateMvt(request.getDateMvt())
                .comment(request.getComment())
                .typeMvt(request.getTypeMvt())
                .quantity(request.getQuantity())
                .product(Product.builder()
                        .id(request.getProductId())
                        .build())
                .deleted(false)
                .build();
    }

    public StockMvtResponse toResponse(StockMvt entity) {
        return StockMvtResponse.builder()
                .id(entity.getId())
                .dateMvt(entity.getDateMvt())
                .comment(entity.getComment())
                .typeMvt(entity.getTypeMvt())
                .quantity(entity.getQuantity())
                .build();
    }
}