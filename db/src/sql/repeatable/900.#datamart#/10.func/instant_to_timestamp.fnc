CREATE OR REPLACE FUNCTION datamart.instant_to_timestamp(
  a_instant   IN BIGINT,
  a_time_zone IN VARCHAR DEFAULT 'Europe/Moscow'
) RETURNS TIMESTAMP AS $$

SELECT (to_timestamp(a_instant / 1000.0) AT TIME ZONE a_time_zone) :: TIMESTAMP;

$$
RETURNS NULL ON NULL INPUT
SECURITY DEFINER
LANGUAGE SQL
IMMUTABLE
COST 1;
/