ALTER TABLE campus ADD COLUMN campus_name VARCHAR(255);

UPDATE campus SET campus_name = 'Dr. Fernando Zárraga', internship = false, initial_education = false, preschool = true, basic = true, secondary = false, high_school = false, primary_campus = false WHERE id = '1';
UPDATE campus SET campus_name = 'Coyoacán', internship = false, initial_education = false, preschool = false, basic = true, secondary = false, high_school = false, primary_campus = false WHERE id = '2';
UPDATE campus SET campus_name = 'Gabriela Mistral', internship = false, initial_education = false, preschool = true, basic = true, secondary = false, high_school = false, primary_campus = false WHERE id = '7';
UPDATE campus SET campus_name = 'Varones', internship = false, initial_education = false, preschool = false, basic = true, secondary = true, high_school = true, primary_campus = false WHERE id = '12';
UPDATE campus SET campus_name = 'Isabel de Talamas',internship = false, initial_education = false, preschool = true, basic = true, secondary = false, high_school = false, primary_campus = false WHERE id = '22';
UPDATE campus SET campus_name = 'Cuernavaca',internship = false, initial_education = false, preschool = false, basic = true, secondary = false, high_school = false, primary_campus = false WHERE id = '28';
UPDATE campus SET campus_name = 'Mujeres',internship = true, initial_education = false, preschool = false, basic = false, secondary = false, high_school = false, primary_campus = false WHERE id = '30';
UPDATE campus SET campus_name = 'El Sol',internship = false, initial_education = false, preschool = false, basic = false, secondary = false, high_school = false, primary_campus = false WHERE id = '32';
UPDATE campus SET campus_name = 'Jurica',internship = false, initial_education = false, preschool = true, basic = true, secondary = false, high_school = false, primary_campus = false WHERE id = '36';
UPDATE campus SET campus_name = 'Liebres',internship = false, initial_education = false, preschool = false, basic = false, secondary = true, high_school = false, primary_campus = false WHERE id = '39';
UPDATE campus SET campus_name = 'Fraternidad',internship = false, initial_education = false, preschool = true, basic = false, secondary = false, high_school = false, primary_campus = false WHERE id = '53';

