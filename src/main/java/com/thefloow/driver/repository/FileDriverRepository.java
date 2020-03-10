package com.thefloow.driver.repository;

import com.thefloow.driver.controller.error.ServiceException;
import com.thefloow.driver.model.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Repository
public class FileDriverRepository implements DriverRepository {

    private static final Integer DRIVER_RECORD_SIZE = 5;

    private final Path repositoryPath;

    @Autowired
    public FileDriverRepository(@Autowired Path repositoryPath) {
        this.repositoryPath = repositoryPath;
        try {
            initializeRepositoryFile(repositoryPath);
        } catch (IOException e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "technical.failure",
                    "Error initializing repository file");
        }
    }

    @Override
    public CompletableFuture<List<Driver>> findAll() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Files.lines(repositoryPath)
                        .map(this::mapToDriver)
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "technical.failure",
                        "Error reading repository file");
            }
        });
    }

    @Override
    public CompletableFuture<List<Driver>> findCreatedAfter(LocalDate creationDate) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return Files.lines(repositoryPath)
                        .map(this::mapToDriver)
                        .filter(driver -> driver.getCreationDate().isAfter(creationDate))
                        .collect(Collectors.toList());
            } catch (IOException e) {
                throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "technical.failure",
                        "Error reading repository file ");
            }
        });
    }

    @Override
    public CompletableFuture<Driver> save(Driver driver) {
        return CompletableFuture.supplyAsync(() -> {
            try (BufferedWriter writer = Files.newBufferedWriter(
                    repositoryPath, StandardCharsets.UTF_8, StandardOpenOption.APPEND)
            ) {
                Integer nextDriverId = getNextDriverId();
                writer.write(mapToString(nextDriverId, driver));
                writer.newLine();
                driver.setId(nextDriverId);
            } catch (IOException e) {
                throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                        "technical.failure",
                        "Error writing repository file");
            }

            return driver;
        });
    }

    private void initializeRepositoryFile(Path repositoryPath) throws IOException {
        if (Files.notExists(repositoryPath)) {
            Files.createFile(repositoryPath);
        }
    }

    private Driver mapToDriver(String driverRecord) {
        String[] splitted = driverRecord.split(";");
        if (splitted.length != DRIVER_RECORD_SIZE) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "technical.failure",
                    "Error reading repository file record");
        }
        return new Driver(
                Integer.parseInt(splitted[0]),
                splitted[1],
                splitted[2],
                LocalDate.parse(splitted[3]),
                LocalDate.parse(splitted[4])
        );
    }

    private static String mapToString(Integer driverId, Driver driver) {
        return new StringBuilder()
                .append(driverId).append(";")
                .append(driver.getFirstName()).append(";")
                .append(driver.getLastName()).append(";")
                .append(driver.getDateOfBirth()).append(";")
                .append(driver.getCreationDate()).toString();
    }

    /**
     * Reads repository file, gets last driverId stored and increments it by one.
     *
     * @return last driverId stored in repository file plus one
     */
    private Integer getNextDriverId() {
        try {
            List<String> allLines = Files.readAllLines(repositoryPath);

            if (allLines.isEmpty()) {
                return 1;
            }

            String lastLine = allLines.get(allLines.size() - 1);
            String[] splitted = lastLine.split(";");

            return Integer.parseInt(splitted[0]) + 1;
        } catch (IOException e) {
            throw new ServiceException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "technical.failure",
                    "Error reading repository file");
        }
    }
}
