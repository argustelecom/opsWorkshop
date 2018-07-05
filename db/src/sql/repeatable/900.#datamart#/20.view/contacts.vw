CREATE OR REPLACE VIEW datamart.contacts_vw AS
  SELECT
    c.id     AS contact_id,
    pr.dtype AS contactor_entity,
    ct.category,
    ct.name,
    ct.short_name,
    c.contact_data,
    c.comment,
    (CASE -- Получение имени контактного лица
     WHEN pr.dtype = 'ContactPerson'
       THEN datamart_sys.format_party_name(p)
     ELSE NULL
     END)    AS contact_person_name,
    (CASE -- получение id customer через party_role для контактов клиентов
     WHEN pr.dtype = 'Individual' OR pr.dtype = 'Organization'
       THEN pr.id
     WHEN pr.dtype = 'ContactPerson'
       THEN cppr.id
     ELSE NULL
     END)    AS customer_id,
    (CASE -- получение идентификатора сотрудника в случае party_role = employee
     WHEN pr.dtype = 'Employee'
       THEN pr.id
     ELSE NULL
     END)    AS employee_id
  FROM system.contact c
    JOIN system.contact_type ct ON ct.id = c.type_id
    JOIN system.party_contacts pc ON pc.contact_id = c.id
    JOIN system.party p ON p.id = pc.party_id
    JOIN system.party_role pr ON pr.party_id = p.id
    LEFT JOIN system.contact_person cp ON cp.id = pr.id
    LEFT JOIN system.party_role cppr ON cppr.party_id = cp.company_id;