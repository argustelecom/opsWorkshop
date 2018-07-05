CREATE OR REPLACE FUNCTION datamart.round_half_even(
  in_value IN NUMERIC,
  in_scale IN INT DEFAULT 0
) RETURNS NUMERIC AS
$$
SELECT CASE
       WHEN
         (trunc(abs(in_value) % 0.1 ^ in_scale, (in_scale + 1))) = 0.5 * 0.1 ^ in_scale AND
         (trunc(abs(in_value) % 0.1 ^ (in_scale - 1), in_scale) * 10 ^ in_scale) :: INT % 2 = 0
         THEN trunc(in_value, in_scale) ELSE round(in_value, in_scale)
       END;
$$
LANGUAGE SQL
IMMUTABLE COST 1
SECURITY DEFINER;
/