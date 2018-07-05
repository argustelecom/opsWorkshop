CREATE OR REPLACE FUNCTION system.party_has_contact(BIGINT, BIGINT, VARCHAR)
  RETURNS BOOLEAN
AS $$
SELECT count(*) > 0
FROM system.party_contacts pc
  JOIN system.contact c ON pc.contact_id = c.id
WHERE pc.party_id = $1
      AND c.type_id = $2
      AND c.contact_data = $3;
$$ LANGUAGE SQL IMMUTABLE;
/