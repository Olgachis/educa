package educa.evaluation.service;

import educa.evaluation.data.CampusData;
import educa.evaluation.domain.Campus;
import educa.evaluation.repository.CampusRepository;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.transaction.Transactional;

@Service
public class CampusService {

    @Autowired
    private CampusRepository campusRepository;

    //Servicio para lista de campus
    public List<CampusData> listPrimary() {
        return campusRepository.findAllByPrimaryCampus(true)
                .stream()
                .map(i -> {
                    return CampusData.builder()
                            .id(i.getId())
                            .name(i.getName())
                            .campusName(i.getCampusName())
                            .primary(i.getPrimaryCampus())
                            .campusType(buildType(i))
                            .innerCampus(buildInnerCampus(i))
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

    private List<CampusData> buildInnerCampus(Campus campus) {
        List<Campus> innerCampusList = campusRepository.findAllByInstitutionAndPrimaryCampus(campus.getInstitution(), false);
        if(innerCampusList.size() > 0 ){
            return
                    innerCampusList.stream()
                            .map(i -> {
                                return CampusData.builder()
                                       .id(i.getId())
                                        .name(i.getName())
                                        .campusName(i.getCampusName())
                                        .primary(i.getPrimaryCampus())
                                        .campusType(buildType(i))
                                        .build();
                            })
                            .collect(Collectors.toList());
        }
        return null;
    }

    public CampusData saveCampus(CampusData campusData){
        return null;
    }
}

