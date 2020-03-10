package com.thefloow.driver.controller;

import com.thefloow.driver.model.Driver;
import com.thefloow.driver.repository.DriverRepository;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
@Api(tags = "drivers")
public class DriverController {

    private final DriverRepository driverRepository;

    @Autowired
    public DriverController(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    @GetMapping("/drivers")
    @ApiOperation("Provides a list of all existing drivers")
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = Driver.class, responseContainer = "List")
    })
    public CompletableFuture<List<Driver>> findAll() {
        return driverRepository.findAll();
    }

    @GetMapping("/drivers/byDate")
    @ApiOperation("Provides a list of all existing drivers created after certain date")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "date", value = "Creation date to find drivers from", paramType = "query"),
    })
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = Driver.class, responseContainer = "List")
    })
    public CompletableFuture<List<Driver>> findDriversCreatedAfterDate(@RequestParam("date")
                                                      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return driverRepository.findCreatedAfter(date);
    }

    @PostMapping("/driver/create")
    @ApiOperation("Creates and stores a new driver")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "driver", value = "Driver to be created", paramType = "body")})
    @ApiResponses({
            @ApiResponse(code = 200, message = "Success", response = Driver.class)
    })
    public CompletableFuture<Driver> createDriver(@RequestBody Driver driver) {

        return driverRepository.save(driver);
    }
}