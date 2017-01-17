ALTER TABLE questionnaire_section ADD COLUMN campus_relevant BOOLEAN NOT NULL DEFAULT false;

UPDATE questionnaire_section SET campus_relevant = true
WHERE subdimension
IN ('Director General', 'Casas Hogar e Internado', 'Educación Inicial', 'Educación Preescolar', 'Primaria', 'Secundaria', 'Medio superior', 'Protección Civil', 'Instalaciones', 'Equipo y material didáctico', ' Docentes', 'Proceso de enseñanza-aprendizaje', 'Reprobación y deserción', 'Autogestión del aprendizaje', 'Evaluación', 'Perfil del alumnado y cuotas de recuperación', 'Trascendencia de la Institución', 'Ruta de Mejora', 'INDESOL');