package com.thefloow.driver.repository;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import com.thefloow.driver.controller.error.ServiceException;
import com.thefloow.driver.model.Driver;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class FileDriverRepositoryTest {

    @Test
    @DisplayName("Should read the content of the file and return all driver records")
    public void findAll_happyPath_returnsAllDriverRecords() throws Exception {

        try (FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix())) {
            Path driversTestRepoFile = getPathFromJimfsFilesystem(fileSystem, "drivers_repo.txt");

            FileDriverRepository test = new FileDriverRepository(driversTestRepoFile);

            List<Driver> drivers = getOrThrow(test.findAll());

            assertThat(drivers).extracting("id", "firstName", "lastName", "dateOfBirth", "creationDate")
                    .containsOnly(
                            tuple(1, "Jaime", "Bergas", LocalDate.parse("1984-05-15"), LocalDate.parse("2020-02-08")),
                            tuple(2, "Jane", "Doe", LocalDate.parse("1980-05-15"), LocalDate.parse("2018-02-08")),
                            tuple(3, "John", "Doe", LocalDate.parse("1990-05-15"), LocalDate.parse("2019-02-08"))
                    );

        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        }
    }


    @Test
    @DisplayName("Should read the content of the file and return only driver records created after 2019-01-01")
    public void findCreatedAfter_happyPath_returnsFilteredRecords() throws Exception {

        try (FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix())) {
            Path driversTestRepoFile = getPathFromJimfsFilesystem(fileSystem, "drivers_repo.txt");

            FileDriverRepository test = new FileDriverRepository(driversTestRepoFile);

            List<Driver> drivers = getOrThrow(test.findCreatedAfter(LocalDate.parse("2019-01-01")));

            assertThat(drivers).extracting("id", "firstName", "lastName", "dateOfBirth", "creationDate")
                    .containsOnly(
                            tuple(1, "Jaime", "Bergas", LocalDate.parse("1984-05-15"), LocalDate.parse("2020-02-08")),
                            tuple(3, "John", "Doe", LocalDate.parse("1990-05-15"), LocalDate.parse("2019-02-08"))
                    );

        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        }
    }


    @Test
    @DisplayName("Should add a new driver record to the repository with correct next driver id")
    public void addNewDriver_happyPath_driverRecordAddedToRepository() throws Exception {

        try (FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix())) {
            Path driversTestRepoFile = getPathFromJimfsFilesystem(fileSystem, "drivers_repo.txt");

            FileDriverRepository test = new FileDriverRepository(driversTestRepoFile);

            Driver newDriver = getOrThrow(test.save(buildNewDriver()));

            assertThat(newDriver.getId()).isEqualTo(4);

            List<Driver> drivers = getOrThrow(test.findAll());

            assertThat(drivers).extracting("id", "firstName", "lastName", "dateOfBirth", "creationDate")
                    .containsOnly(
                            tuple(1, "Jaime", "Bergas", LocalDate.parse("1984-05-15"), LocalDate.parse("2020-02-08")),
                            tuple(2, "Jane", "Doe", LocalDate.parse("1980-05-15"), LocalDate.parse("2018-02-08")),
                            tuple(3, "John", "Doe", LocalDate.parse("1990-05-15"), LocalDate.parse("2019-02-08")),
                            tuple(4, "New", "Driver", LocalDate.parse("1990-12-12"), LocalDate.now())
                    );
        } catch (FileNotFoundException fnfe) {
            throw fnfe;
        }
    }

    @Test
    @DisplayName("Should create repository file if it does not exist")
    public void newRepository_fileDoesNotExists_repositoryFileIsCreated() throws IOException {

        try (FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix())) {
            Path driversTestRepoFile = getPathFromJimfsFilesystem(fileSystem, "drivers.txt");

            new FileDriverRepository(driversTestRepoFile);

            assertThat(Files.exists(driversTestRepoFile)).isTrue();

        } catch (IOException e) {
            throw e;
        }
    }


    @Test
    @DisplayName("Should throw service exception when repository contains invalid records")
    public void findAll_fileContainsInvalidRecords_serviceExceptionIsThrown() throws Exception {

        try (FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix())) {
            Path driversTestRepoFile = getPathFromJimfsFilesystem(fileSystem, "drivers_invalid_repo.txt");

            FileDriverRepository test = new FileDriverRepository(driversTestRepoFile);

            assertThatThrownBy(() -> getOrThrow(test.findAll()))
                    .isInstanceOf(ServiceException.class)
                    .hasFieldOrPropertyWithValue("code", "technical.failure")
                    .hasMessage("Error reading repository file record");

        } catch (IOException e) {
            throw e;
        }
    }

    private static Driver buildNewDriver() {
        return new Driver(null, "New", "Driver", LocalDate.parse("1990-12-12"), LocalDate.now());
    }


    private static Path getTestFileFromJimfsFilesystem(String testFile) throws IOException {
        FileSystem fileSystem = Jimfs.newFileSystem(Configuration.unix());
        Path resourceFilePath = fileSystem.getPath("drivers.txt");
        Files.copy(Paths.get("src", "test", "resources", "repository", testFile), resourceFilePath);

        return resourceFilePath;
    }


    private static Path getPathFromJimfsFilesystem(FileSystem fileSystem, String testFile) throws IOException {
        Path resourceFilePath = fileSystem.getPath("drivers.txt");
        Files.copy(Paths.get("src", "test", "resources", "repository", testFile), resourceFilePath);

        return resourceFilePath;
    }


    private static <T> T getOrThrow(Future<T> future) throws Exception {
        try {
            return future.get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            throw cause instanceof Exception ? (Exception) cause : new RuntimeException(cause);
        }
    }
}