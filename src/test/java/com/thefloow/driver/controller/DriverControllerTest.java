package com.thefloow.driver.controller;

import com.thefloow.driver.model.Driver;
import com.thefloow.driver.repository.DriverRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.LocalDate;
import java.util.List;

import static java.util.Arrays.asList;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
@SpringBootTest
public class DriverControllerTest{

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DriverRepository mockDriverRepository;

    private List<Driver> driverList = asList(
            new Driver(1, "Jaime", "Bergas", LocalDate.parse("1984-05-15"), LocalDate.parse("2020-02-08")),
            new Driver(2, "Jane", "Doe", LocalDate.parse("1980-05-15"), LocalDate.parse("2018-02-08"))
    );

    private Driver driver = new Driver(3, "John", "Doe", LocalDate.parse("1990-05-15"), LocalDate.parse("2019-02-08"));

    @Test
    public void findAllDrivers_happyPath() throws Exception {
        when(mockDriverRepository.findAll()).thenReturn(completedFuture(driverList));

        performAsync(get("/drivers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].firstName").value("Jaime"))
                .andExpect(jsonPath("$.[0].lastName").value("Bergas"))
                .andExpect(jsonPath("$.[0].dateOfBirth").value("1984-05-15"))
                .andExpect(jsonPath("$.[0].creationDate").value("2020-02-08"))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[1].firstName").value("Jane"))
                .andExpect(jsonPath("$.[1].lastName").value("Doe"))
                .andExpect(jsonPath("$.[1].dateOfBirth").value("1980-05-15"))
                .andExpect(jsonPath("$.[1].creationDate").value("2018-02-08"));

        verify(mockDriverRepository, times(1)).findAll();
    }

    @Test
    public void findDriversCreatedAfterDate_happyPath() throws Exception {
        LocalDate fromDate = LocalDate.parse("2019-01-01");

        when(mockDriverRepository.findCreatedAfter(fromDate)).thenReturn(completedFuture(driverList));

        performAsync(get("/drivers/byDate?date=2019-01-01"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$.[0].id").value(1))
                .andExpect(jsonPath("$.[0].firstName").value("Jaime"))
                .andExpect(jsonPath("$.[0].lastName").value("Bergas"))
                .andExpect(jsonPath("$.[0].dateOfBirth").value("1984-05-15"))
                .andExpect(jsonPath("$.[0].creationDate").value("2020-02-08"))
                .andExpect(jsonPath("$.[1].id").value(2))
                .andExpect(jsonPath("$.[1].firstName").value("Jane"))
                .andExpect(jsonPath("$.[1].lastName").value("Doe"))
                .andExpect(jsonPath("$.[1].dateOfBirth").value("1980-05-15"))
                .andExpect(jsonPath("$.[1].creationDate").value("2018-02-08"));

        verify(mockDriverRepository, times(1)).findCreatedAfter(fromDate);
    }


    @Test
    public void createNewDriver_happyPath() throws Exception {

        String newDriverRequestBody = "{\"firstName\": \"John\",\"lastName\": \"Doe\",\"dateOfBirth\": \"1990-05-15\"}";

        when(mockDriverRepository.save(any(Driver.class))).thenReturn(completedFuture(driver));

        performAsync(
                post("/driver/create")
                        .contentType(APPLICATION_JSON)
                        .content(newDriverRequestBody)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.firstName").value("John"))
                .andExpect(jsonPath("$.lastName").value("Doe"))
                .andExpect(jsonPath("$.dateOfBirth").value("1990-05-15"));

        verify(mockDriverRepository, times(1)).save(any(Driver.class));
    }


    @Test
    public void findDriversCreatedAfterDate_invalidDateParameter_returnsBadRequest() throws Exception {
        mockMvc.perform(get("/drivers/byDate?date=2020-40-40"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad.request"))
                .andExpect(jsonPath("$.message").value("Invalid input data"));
    }

    @Test
    public void createNewDriver_invalidInputData_returnsBadRequest() throws Exception {

        String invalidNewDriverRequestBody = "{\"firstName\": \"123\",\"lastName\": \"456\",\"dateOfBirth\": \"1984-15-05\"}";

        mockMvc.perform(
                post("/driver/create")
                        .contentType(APPLICATION_JSON)
                        .content(invalidNewDriverRequestBody)
        )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("bad.request"))
                .andExpect(jsonPath("$.message").value("Invalid input data"));
    }

    public ResultActions performAsync(RequestBuilder requestBuilder) throws Exception {
        return mockMvc.perform(
                MockMvcRequestBuilders.asyncDispatch(
                        mockMvc.perform(requestBuilder)
                                .andExpect(MockMvcResultMatchers.request().asyncStarted()).andReturn())
        );
    }
}