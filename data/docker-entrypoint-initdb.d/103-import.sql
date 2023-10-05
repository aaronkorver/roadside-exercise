copy temp1 from '/csv/assistant.csv' WITH CSV HEADER DELIMITER AS ',';

insert into assistant (name,location,lat, lon)
select esp_name, st_geographyfromtext('SRID=4326;POINT(' || longitude || ' ' || latitude || ')'), latitude, longitude from temp1

