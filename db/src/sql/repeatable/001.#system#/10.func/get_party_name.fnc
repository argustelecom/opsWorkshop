CREATE OR REPLACE FUNCTION system.get_party_name(
  a_party_id IN BIGINT
)
  RETURNS VARCHAR
AS $$

SELECT CASE
       WHEN dtype = 'Company'
         THEN
           CASE
           WHEN brand_name IS NULL
             THEN legal_name
           ELSE brand_name
           END
       WHEN dtype = 'Person'
         THEN concat_ws(' ', first_name, second_name, last_name)
       END
FROM system.party
WHERE id = a_party_id;

$$
LANGUAGE SQL
STABLE
COST 15
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/