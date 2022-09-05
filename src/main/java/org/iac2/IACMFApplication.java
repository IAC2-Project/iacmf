package org.iac2;

import org.iac2.service.architecturereconstruction.plugin.manager.ArchitectureReconstructionPluginManager;
import org.iac2.service.architecturereconstruction.plugin.manager.implementation.SimpleARPluginManager;
import org.iac2.service.checking.plugin.manager.ComplianceRuleCheckingPluginManager;
import org.iac2.service.checking.plugin.manager.implementation.SimpleCRCheckingManager;
import org.iac2.service.fixing.plugin.manager.IssueFixingPluginManager;
import org.iac2.service.fixing.plugin.manager.implementation.SimpleIssueFixingPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
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

    /*
    @Bean
    public CommandLineRunner demo(
            ParameterRepository paramRepository,
            ParameterAssignmentRepository assignmentRepo
            ) {
        return (args) -> {
            // save a few customers
            ParameterEntity par1 = new ParameterEntity("num-cpus", ParameterType.INT);
            ParameterEntity par2 = new ParameterEntity("server", ParameterType.STRING);
            ParameterEntity par3 = new ParameterEntity("admin", ParameterType.STRING);

            paramRepository.save(par1);
            par2 = paramRepository.save(par2);
            par3 = paramRepository.save(par3);

            // fetch all parameters
            log.info("Parameters found with findAll():");
            log.info("-------------------------------");
            for (ParameterEntity param : paramRepository.findAll()) {
                log.info(param.toString());
            }
            log.info("");

            Optional<ParameterEntity> parameter = paramRepository.findById(1L);
            log.info("Parameter found with findById(1L):");
            log.info("--------------------------------");
            log.info(parameter.toString());
            log.info("");

            log.info("Parameter found with findByName('server'):");
            log.info("--------------------------------------------");
            paramRepository.findByName("server").forEach(server -> {
                log.info(server.toString());
            });

            log.info("");

            assignmentRepo.save(new IntegerParameterAssignmentEntity(par1, 3));
            assignmentRepo.save(new StringParameterAssignmentEntity(par2, "localhost"));
            assignmentRepo.save(new StringParameterAssignmentEntity(par3, "falazigb"));

            // fetch all integer parameter assignment
            log.info("all assignments found with findAll():");
            log.info("-------------------------------");
            for (ParameterAssignmentEntity assignment : assignmentRepo.findAll()) {
                log.info(assignment.toString());
            }
            log.info("");


            log.info("assignments found with findByParameter(par2):");
            log.info("-------------------------------");
            for (ParameterAssignmentEntity assignment : assignmentRepo.findByParameter(par2)) {
                log.info(assignment.toString());
            }

            log.info("");

        };
    }
    */
}
