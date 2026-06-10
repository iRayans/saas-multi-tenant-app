package com.rayan.saasapp.services.impl;

import com.rayan.saasapp.common.PageResponse;
import com.rayan.saasapp.entites.Product;
import com.rayan.saasapp.entites.StockMvt;
import com.rayan.saasapp.mappers.StockMvtMapper;
import com.rayan.saasapp.repositories.ProductRepository;
import com.rayan.saasapp.repositories.StockMvtRepository;
import com.rayan.saasapp.requests.StockMvtRequest;
import com.rayan.saasapp.response.StockMvtResponse;
import com.rayan.saasapp.services.StockMvtService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class StockMvtServiceImpl implements StockMvtService {
    private final StockMvtRepository stockMvtRepository;
    private final ProductRepository productRepository;
    private final StockMvtMapper mapper;

    @Override
    public void create(StockMvtRequest request) {
        checkIfProductExistsById(request.getProductId());
        StockMvt stockMvt = mapper.toEntity(request);
        this.stockMvtRepository.save(stockMvt);
        log.info("StockMvt created.");
    }

    @Override
    public void update(String id, StockMvtRequest request) {
        Optional<StockMvt> stockMvt = this.stockMvtRepository.findById(id);
        if (stockMvt.isEmpty()) {
            log.debug("StockMvt not found with id {}", id);
            throw new RuntimeException("StockMvt not found with id " + id);
        }
        checkIfProductExistsById(request.getProductId());

        StockMvt upateStockMvt = mapper.toEntity(request);
        upateStockMvt.setId(id);
        this.stockMvtRepository.save(upateStockMvt);
        log.info("StockMvt updated with id {}", upateStockMvt.getId());
    }

    @Override
    public void delete(final String id) {
        final StockMvt stockMvt = this.stockMvtRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("StockMvt does not exist"));
        this.stockMvtRepository.delete(stockMvt);

    }

    @Override
    public StockMvtResponse findById(String id) {

        return this.stockMvtRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("StockMvt not found."));
    }

    @Override
    public PageResponse<StockMvtResponse> findAll(final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<StockMvt> stockMvts = this.stockMvtRepository.findAll(pageRequest);
        final Page<StockMvtResponse> stockMvtResponses = stockMvts.map(this.mapper::toResponse);
        return PageResponse.of(stockMvtResponses);
    }


    private void checkIfProductExistsById(String productId) {
        Optional<Product> product = this.productRepository.findById(productId);
        if (product.isEmpty()) {
            log.debug("Product does not exist");
            throw new EntityNotFoundException("Product does not exist");
        }
    }


    @Override
    public PageResponse<StockMvtResponse> findAllByProductId(final String productId, final int page, final int size) {
        final PageRequest pageRequest = PageRequest.of(page, size);
        final Page<StockMvt> stockMvts = this.stockMvtRepository.findAllByProductId(productId, pageRequest);
        final Page<StockMvtResponse> stockMvtResponses = stockMvts.map(this.mapper::toResponse);
        return PageResponse.of(stockMvtResponses);
    }
}
