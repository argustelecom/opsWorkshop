CREATE TABLE system.customer (
  id     BIGINT,
  vip    BOOLEAN     NOT NULL DEFAULT FALSE,
  CONSTRAINT pk_customer PRIMARY KEY (id),
  CONSTRAINT fk_customer_ancestor FOREIGN KEY (id) REFERENCES system.party_role (id)
);

COMMENT ON TABLE system.customer IS 'Роль описывающая участника как клиента';
COMMENT ON COLUMN system.customer.id IS 'PK';

ALTER TABLE system.customer
ADD CONSTRAINT fk_customer_party_role FOREIGN KEY (id) REFERENCES system.party_role (id);