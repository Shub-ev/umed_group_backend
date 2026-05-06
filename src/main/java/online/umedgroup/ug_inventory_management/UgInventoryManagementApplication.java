package online.umedgroup.ug_inventory_management;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class UgInventoryManagementApplication {

	public static void main(String[] args) {
		SpringApplication.run(UgInventoryManagementApplication.class, args);
	}


}