CREATE TABLE simple_questionnaire (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title_func TEXT NOT NULL,
    title VARCHAR(512) NOT NULL,
    questions_json TEXT NOT NULL,
    date_created TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT
);

CREATE TABLE simple_response (
    id VARCHAR(255) NOT NULL PRIMARY KEY,
    title VARCHAR(1024) NOT NULL,
    questionnaire_id VARCHAR(255) NOT NULL REFERENCES simple_questionnaire(id),
    simple_response TEXT NOT NULL,
    date_created TIMESTAMP WITH TIME ZONE NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE NOT NULL,
    version BIGINT
);

INSERT INTO simple_questionnaire VALUES('teachers', 'response.name', 'Cuestionario para maestros/as', '{ "text": "En este año escolar usted va a impartir el nuevo Programa Educación Social y Financiera de Fundación EDUCA. Por eso nos interesa saber sus expectativas del programa. Le pedimos conteste con franqueza y confianza.", "questions": [ { "id": "name", "displayName": "Nombre del maestro/de la maestra", "type": "text" }, { "id": "grades", "displayName": "Grados escolares en donde aplicara el programa Educación Social y Financiera", "type": "text" }, { "id": "knowMaterial", "displayName": "¿Conoce el nuevo material y los nuevos cuadernos de trabajo del Programa Educación Social y Financiera?", "type": "options", "options": [ "Sí", "No" ] }, { "id": "objective", "displayName": "¿Cuál es el objetivo del Programa Educación Social y Financiera desde su  perspectiva?", "type": "text" }, { "id": "receivedInformation", "displayName": "¿Piensa que recibió la formación adecuada para impartir el programa?", "type": "options", "options": [ "Sí", "No", "No lo sé" ] }, { "id": "easilyUnderstood", "displayName": "¿Le ha sido fácil entender el material y la forma de facilitarlo?", "type": "options", "options": [ "Sí", "No", "No lo sé" ] }, { "id": "suitableQuestionnaire", "displayName": "¿Opina que los cuadernos de trabajo del Programa son adecuados para el grupo de edad de los niños?", "type": "options", "options": [ "Sí", "No", "No lo sé" ] }, { "id": "childResults", "displayName": "Desde su punto de vista, ¿qué resultados espera que genere el nuevo programa en los niños?", "type": "text" }, { "id": "collectiveSaving", "displayName": "¿Cuántos niños de su salón están realizando ahorro colectivo?", "type": "number" }, { "id": "individualSaving", "displayName": "¿Cuántos niños de su salón están realizando ahorro individual?", "type": "number" }, { "id": "familyResults", "displayName": "Desde su punto de vista, ¿qué resultados espera que genere el nuevo programa en las familias de los alumnos?", "type": "text" }, { "id": "myResults", "displayName": "Desde su punto de vista, ¿qué resultados espera que genere el nuevo programa en usted mismo?", "type": "text" }, { "id": "schoolResultsa", "displayName": "Desde su punto de vista, ¿qué resultados espera que genere el nuevo programa en su escuela?", "type": "text" } ] }', current_timestamp, current_timestamp, 0);
INSERT INTO simple_questionnaire VALUES('secondary-students', 'response.name');
INSERT INTO simple_questionnaire VALUES();
INSERT INTO simple_questionnaire VALUES();
INSERT INTO simple_questionnaire VALUES();
INSERT INTO simple_questionnaire VALUES();




