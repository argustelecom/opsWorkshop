INSERT INTO system.contract_role_rules (id, contract_category, role_type, role) VALUES
  (1, 'BILATERAL', 'PROVIDER', 'OWNER'),
  (2, 'BILATERAL', 'CLIENT', 'CUSTOMER'),
  (3, 'AGENCY', 'PROVIDER', 'SUPPLIER'),
  (4, 'AGENCY', 'BROKER', 'OWNER'),
  (5, 'AGENCY', 'CLIENT', 'CUSTOMER')
ON CONFLICT (id)
  DO UPDATE SET
    contract_category = EXCLUDED.contract_category,
    role_type         = EXCLUDED.role_type,
    role              = EXCLUDED.role;