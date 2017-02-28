package educa.evaluation.service;

import educa.evaluation.data.CampusData;
import educa.evaluation.domain.Campus;
import educa.evaluation.domain.User;
import educa.evaluation.repository.CampusRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InstitutionService {

    @Autowired
    private SecurityService securityService;

    @Autowired
    private CampusRepository campusRepository;

    public List<CampusData> listCampuses() {
        User user = securityService.getCurrentUser();
        return listCampuses(user);
    }

    private List<CampusData> listCampuses(User user) {
        return campusRepository.findAllByInstitution(user.getCampus().getInstitution())
                .stream()
                .map(i -> {
                    return CampusData.builder()
                            .id(i.getId())
                            .name(i.getName())
                            .primary(i.getPrimaryCampus())
                            .campusType(buildType(i))
                            .build();
                })
                .collect(Collectors.toList());
    }

    private String buildType(Campus campus) {
        StringBuilder builder = new StringBuilder();
        if(campus.getInitialEducation()) {
            builder.append("(Educación inicial) ");
        }
        if(campus.getInternship()) {
            builder.append("(Internado) ");
        }
        if(campus.getPreschool()) {
            builder.append("(Preescolar) ");
        }
        if(campus.getBasic()) {
            builder.append("(Primaria) ");
        }
        if(campus.getSecondary()) {
            builder.append("(Secundaria) ");
        }
        if(campus.getHighSchool()) {
            builder.append("(Preparatoria) ");
        }
        return builder.toString().trim();
    }

}
