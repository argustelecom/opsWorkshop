CREATE TABLE ops.team_product
(
    team_id bigint NOT NULL,
    product_id bigint NOT NULL,
    CONSTRAINT fk_team_product_1 FOREIGN KEY (team_id) REFERENCES ops.team (id) ON DELETE CASCADE,
    CONSTRAINT fk_team_product_2 FOREIGN KEY (product_id) REFERENCES ops.product (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX team_product_1 ON ops.team_product (team_id, product_id);
CREATE UNIQUE INDEX team_product_2 ON ops.team_product (product_id, team_id);