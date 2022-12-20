package org.iac2;

import java.util.concurrent.Executor;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
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
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
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
    public OpenAPI iacmfOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("IACMF")
                        .description("IaC Compliance Management Framework")
                        .version("1.0.0")
                        .license(new License().name("Apache 2.0").url("https://www.apache.org/licenses/LICENSE-2.0")));
    }

    @Bean
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(10);
        executor.setThreadNamePrefix("IACMF-");
        executor.initialize();
        return executor;
    }

    @Bean
    public CommandLineRunner initialization() {
        return (args) -> {
            log.info("Intializing the IACMF core...");
            log.info("*****************************");
            log.info("");
            log.info("Initializing EDMM type mappings...");
            EdmmTypeResolver.initDefaultMappings();
            log.info("Finished initializing EDMM type mappings.");
            log.info("""
                                              
                                              #############################################
                                              #                                           #
                                              #   Finished initalizing the IACMF core.    #
                                              #                                           #
                                              #############################################""");
        };
    }

}
