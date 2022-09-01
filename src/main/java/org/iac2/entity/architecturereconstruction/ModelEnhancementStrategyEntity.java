package org.iac2.entity.architecturereconstruction;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.iac2.entity.util.StringListConverter;

import javax.persistence.*;
import java.util.List;

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
