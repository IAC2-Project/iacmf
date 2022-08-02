package org.iac2.entity.compliancerule;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class ComplianceIssueEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;


}
