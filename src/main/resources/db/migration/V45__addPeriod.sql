ALTER TABLE questionnaire ADD COLUMN period NUMERIC(4, 0);
UPDATE questionnaire SET period = 2017 WHERE ID IN ('ac', 'iap');