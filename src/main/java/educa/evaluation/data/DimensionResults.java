package educa.evaluation.data;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DimensionResults {

   private DimensionData.DimensionDataId id;
   private Map<String, SubdimensionResults> subdimensionResults;

   private float maxQuestions;
   private float maxPoints;
   private float maxCountingQuestions;
   private float minimumRequiredQuestions;
   private float questions;
   private float points;

   private int sortOrder;

}
