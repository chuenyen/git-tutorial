package com.example.cfra;

import java.io.File;
import java.util.HashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

import com.example.model.Security;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class })
public class CApplication implements CommandLineRunner {

	@Autowired
	private XMLService xmlService;
	
	public static void main(String[] args) {
		SpringApplication.run(CApplication.class, args);
	}

	public void run(String... args) throws Exception {

		var folderSource = new File(args[0]);
		var folderDestination = new File(args[1]);
		
		var folderSourceFiles = folderSource.listFiles();
		var folderDestinationFiles = folderDestination.listFiles();
		
		var sourceSecurities = new HashMap<String, Security>();
		var destinationSecurities = new HashMap<String, Security>();
		
		xmlService.parseSecurity(folderSourceFiles, sourceSecurities);
		xmlService.parseSecurity(folderDestinationFiles, destinationSecurities);
		
		xmlService.compare(sourceSecurities, destinationSecurities);
    }
}
