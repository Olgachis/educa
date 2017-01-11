package educa.evaluation.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class ImprovementPlan {
    private List<ImprovementQuestion> questions;
}
