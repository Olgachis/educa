ALTER TABLE campus ADD COLUMN open_questionnaire BOOLEAN NOT NULL DEFAULT false;

UPDATE campus SET open_questionnaire = true WHERE id = '48';