package com.thefloow.driver.repository;

import com.thefloow.driver.model.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Repository
public class FileDriverRepository implements DriverRepository {


    private final Path repositoryPath;

    @Autowired
    public FileDriverRepository(@Autowired Path repositoryPath) {
        this.repositoryPath = repositoryPath;

    }

    @Override
    public CompletableFuture<List<Driver>> findAll() {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    @Override
    public CompletableFuture<List<Driver>> findCreatedAfter(LocalDate creationDate) {
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    @Override
    public CompletableFuture<Driver> save(Driver driver) {
        return CompletableFuture.completedFuture(new Driver());
    }
}