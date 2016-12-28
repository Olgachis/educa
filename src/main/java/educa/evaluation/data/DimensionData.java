package educa.evaluation.data;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
public class DimensionData {

    @Data
    public static class DimensionDataId implements Comparable<DimensionDataId> {
        private final String number;
        private final String name;
        private final Integer sortOrder;

        @Override
        public int compareTo(DimensionDataId o) {
            return number.compareTo(o.number);
        }
    }

    private final DimensionDataId id;

    private final Map<String, SubDimensionData> subdimensions;

}
