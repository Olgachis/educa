package educa.evaluation.data;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
public class SubDimensionData {

    private final DimensionData.DimensionDataId id;
    private final List<Question> questions;
    private final String comment;
    private final Integer sortOrder;

    @java.lang.Override
    public java.lang.String toString() {
        return "SubDimensionData{" +
          "id=" + id +
          ", questions=" + questions +
          ", comment='" + comment + '\'' +
          ", sortOrder=" + sortOrder +
          '}';
    }
}
