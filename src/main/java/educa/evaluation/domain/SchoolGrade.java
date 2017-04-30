package educa.evaluation.domain;

/**
 * Created by drh on 29/04/17.
 */
public enum SchoolGrade {
    INITIALEDUCATION("KINDER"),
    PRESCHOOL("KINDER"),
    BASIC("KINDER"),
    SECONDARY("KINDER"),
    HIGHSCHOOL("KINDER"),
    PRIMARYCAMPUS("KINDER");

    private String grade;

    SchoolGrade(String grade) {
        this.grade = grade;
    }


    public String getGrade() {
        return grade;
    }

    @Override
    public String toString() {
        return "SchoolGrade{" +
                "grade='" + grade + '\'' +
                '}';
    }
}
