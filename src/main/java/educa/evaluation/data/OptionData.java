package educa.evaluation.data;

import lombok.Data;

@Data
public class OptionData {

    private String name;
    private boolean valuable;
    private double value;

    public OptionData() {
    }

    public OptionData(String name, double value) {
        this.name = name;
        this.value = value;
        this.valuable = (name.equals("na")) ? false : true;
    }

    public OptionData(String name, boolean valuable, double value) {
        this.name = name;
        this.valuable = valuable;
        this.value = value;
    }

}
