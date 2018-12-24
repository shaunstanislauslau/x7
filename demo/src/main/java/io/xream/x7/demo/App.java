package io.xream.x7.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import x7.EnableTransactionManagementReadable;

/**
 *
 * Demo
 *
 */
@SpringBootApplication
@EnableTransactionManagement
@EnableTransactionManagementReadable
public class App {
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class);
    }
}
