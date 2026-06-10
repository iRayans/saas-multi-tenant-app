package com.rayan.saasapp.services;

import com.rayan.saasapp.common.PageResponse;
import com.rayan.saasapp.requests.StockMvtRequest;
import com.rayan.saasapp.response.StockMvtResponse;

public interface StockMvtService extends BasicService<StockMvtRequest, StockMvtResponse> {
    PageResponse<StockMvtResponse> findAllByProductId(final String productId, final int page, final int size);

}

