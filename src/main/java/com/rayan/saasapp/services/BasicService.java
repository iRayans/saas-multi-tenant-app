package com.rayan.saasapp.services;

import java.util.List;

public interface BasicService<I, O> {

    void create(final I request);

    void update(final String id, final I request);

    void delete(final String id);

    O findById(final String id);

    List<O> findAll();
}
