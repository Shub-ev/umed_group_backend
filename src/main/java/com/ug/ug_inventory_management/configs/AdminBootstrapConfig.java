/*
 * Create Bootstrap admin if no admin is present
 * % Study %
 */

package com.ug.ug_inventory_management.configs;

import com.ug.ug_inventory_management.models.Admin;
import com.ug.ug_inventory_management.repositories.AdminRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrapConfig {

    private final static Logger logger = LoggerFactory.getLogger(AdminBootstrapConfig.class);

    @Bean
    CommandLineRunner bootstrapAdmin (
            AdminRepository adminRepository,
            PasswordEncoder passwordEncoder,
            BootstrapAdminProperties props
    ) {
        return args -> {
            if(adminRepository.count() == 0) {
                Admin admin = new Admin(
                        props.getUsername(),
                        passwordEncoder.encode(props.getPassword())
                );

                adminRepository.save(admin);
                logger.info("Bootstrap admin created successfully.");
            }
        };
    }
}
