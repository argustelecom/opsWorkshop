CREATE OR REPLACE FUNCTION datamart.get_customer_contact(
  a_customer_id             IN BIGINT,
  a_contact_type_short_name IN VARCHAR
) RETURNS VARCHAR
AS $$

WITH parties AS
(
  (
    SELECT pr.party_id
    FROM
      system.customer c
      JOIN system.party_role pr ON pr.id = c.id
    WHERE
      c.id = a_customer_id
  )
  UNION ALL (
    SELECT pr.party_id
    FROM
      system.contact_person cp
      JOIN system.party_role pr ON pr.id = cp.id
    WHERE
      cp.company_id = a_customer_id
  )
)
SELECT c.contact_data
FROM
  parties p
  JOIN system.party_contacts pc ON pc.party_id = p.party_id
  JOIN system.contact c ON pc.contact_id = c.id
  JOIN system.contact_type ct ON c.type_id = ct.id
WHERE
  ct.short_name = a_contact_type_short_name
LIMIT 1;

$$
SECURITY DEFINER
LANGUAGE SQL
STABLE;
/