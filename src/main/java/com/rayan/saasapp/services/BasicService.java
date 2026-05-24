package com.rayan.saasapp.services;

import com.rayan.saasapp.common.PageResponse;

public interface BasicService<I, O> {

    void create(final I request);

    void update(final String id, final I request);

    void delete(final String id);

    O findById(final String id);

    PageResponse<O> findAll(int page, int size);
}
