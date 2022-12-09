package org.iac2;

import org.iac2.common.utility.EdmmTypeResolver;
import org.iac2.service.architecturereconstruction.plugin.manager.ArchitectureReconstructionPluginManager;
import org.iac2.service.architecturereconstruction.plugin.manager.implementation.SimpleARPluginManager;
import org.iac2.service.checking.plugin.manager.ComplianceRuleCheckingPluginManager;
import org.iac2.service.checking.plugin.manager.implementation.SimpleCRCheckingManager;
import org.iac2.service.fixing.plugin.manager.IssueFixingPluginManager;
import org.iac2.service.fixing.plugin.manager.implementation.SimpleIssueFixingPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Scope;

@SpringBootApplication
public class IACMFApplication {

    private static final Logger log = LoggerFactory.getLogger(IACMFApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(IACMFApplication.class, args);
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ArchitectureReconstructionPluginManager theARPluginManager() {
        return SimpleARPluginManager.getInstance();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public ComplianceRuleCheckingPluginManager theCheckingManager() {
        return SimpleCRCheckingManager.getInstance();
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
    public IssueFixingPluginManager theIssueFixingPluginManager() {
        return SimpleIssueFixingPluginManager.getInstance();
    }

    @Bean
    public CommandLineRunner initialization() {
        return (args) -> {
            log.info("Intializing the IACMF core...");
            log.info("**********************");
            log.info("");
            log.info("Initializing EDMM type mappings...");
            EdmmTypeResolver.initDefaultMappings();
            log.info("Finished initializing EDMM type mappings.");
            log.info("Finished initalizing the IACMF core.");
        };
    }

}
