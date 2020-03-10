package com.thefloow.driver.repository;

import com.thefloow.driver.model.Driver;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface DriverRepository {

    CompletableFuture<List<Driver>> findAll();

    CompletableFuture<List<Driver>> findCreatedAfter(LocalDate creationDate);

    CompletableFuture<Driver> save(Driver driver);
}
