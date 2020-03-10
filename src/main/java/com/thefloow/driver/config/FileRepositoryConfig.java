package com.thefloow.driver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class FileRepositoryConfig {

    @Bean
    Path getRepositoryPath(@Value("#{systemProperties['user.dir']}") String repositoryFolderPath,
                           @Value("${repository.filename}") String repositoryFilename) {
        return Paths.get(repositoryFolderPath, repositoryFilename);
    }
}
