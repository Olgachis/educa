package educa.evaluation.domain;

public enum QuestionnaireStatus {
    HISTORIC("HISTORICO"),
    CURRENT("ACTUAL");

    private String name;


    QuestionnaireStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
