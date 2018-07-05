CREATE OR REPLACE VIEW datamart.employee_vw AS
  SELECT
    e.id                              AS employee_id,
    e.personnel_number,
    datamart_sys.format_party_name(p) AS employee_name,
    a.name                            AS appointment
  FROM system.employee e
    JOIN system.appointment a ON a.id = e.appointment_id
    JOIN system.party_role pr ON pr.id = e.id
    JOIN system.party p ON p.id = pr.party_id;