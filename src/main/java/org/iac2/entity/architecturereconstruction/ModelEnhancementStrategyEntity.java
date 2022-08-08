package org.iac2.entity.architecturereconstruction;

import java.util.List;

import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.utll.StringListConverter;

@Entity
@Data
@NoArgsConstructor
public class ModelEnhancementStrategyEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Convert(converter = StringListConverter.class)
    private List<String> pluginIdList;

    public ModelEnhancementStrategyEntity(List<String> pluginIdList) {
        this.pluginIdList = pluginIdList;
    }
}
