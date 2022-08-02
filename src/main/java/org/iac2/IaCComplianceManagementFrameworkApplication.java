package org.iac2;

import java.util.List;
import java.util.Optional;

import org.iac2.entity.compliancerule.parameter.ParameterEntity;
import org.iac2.entity.compliancerule.parameter.ParameterType;
import org.iac2.entity.compliancerule.parameter.assignment.IntegerParameterAssignmentEntity;
import org.iac2.entity.compliancerule.parameter.assignment.StringParameterAssignmentEntity;
import org.iac2.repositories.parameter.ParameterRepository;
import org.iac2.repositories.parameter.assignment.IntegerParameterAssignmentRepository;
import org.iac2.repositories.parameter.assignment.StringParameterAssignmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class IaCComplianceManagementFrameworkApplication {

    private static final Logger log = LoggerFactory.getLogger(IaCComplianceManagementFrameworkApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(IaCComplianceManagementFrameworkApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(
            ParameterRepository paramRepository,
            StringParameterAssignmentRepository stringAssignmentRepo,
            IntegerParameterAssignmentRepository intAssignmentRepo) {
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

            intAssignmentRepo.save(new IntegerParameterAssignmentEntity(par1, 3));
            stringAssignmentRepo.save(new StringParameterAssignmentEntity(par2, "localhost"));
            stringAssignmentRepo.save(new StringParameterAssignmentEntity(par3, "falazigb"));

            // fetch all integer parameter assignment
            log.info("int assignments found with findAll():");
            log.info("-------------------------------");
            for (IntegerParameterAssignmentEntity assignment : intAssignmentRepo.findAll()) {
                log.info(assignment.toString());
            }
            log.info("");


            // fetch all string parameter assignment
            log.info("string assignments found with findAll():");
            log.info("-------------------------------");

            for (StringParameterAssignmentEntity assignment : stringAssignmentRepo.findAll()) {
                log.info(assignment.toString());
            }

            log.info("");


            log.info("string assignments found with findByParameter(par2):");
            log.info("-------------------------------");
            for (StringParameterAssignmentEntity assignment : stringAssignmentRepo.findByParameter(par2)) {
                log.info(assignment.toString());
            }

            log.info("");

        };
    }
}
