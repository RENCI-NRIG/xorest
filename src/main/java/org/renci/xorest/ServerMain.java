package org.renci.xorest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Provide REST ro access to XO statistics
 * @author ibaldin
 *
 */
@SpringBootApplication
public class ServerMain {
	
    public static void main( String[] args ) {
    	// start up spring
        SpringApplication spring = new SpringApplication(ServerMain.class);
        spring.run(args);
    }
}
