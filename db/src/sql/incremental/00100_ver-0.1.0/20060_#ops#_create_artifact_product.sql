CREATE TABLE ops.artifact_product
(
    artifact_id bigint NOT NULL,
    product_id bigint NOT NULL,
    CONSTRAINT FK_artifact_product_artifact_id FOREIGN KEY (artifact_id) REFERENCES ops.artifact (id) ON DELETE CASCADE,
    CONSTRAINT FK_artifact_product_product_id FOREIGN KEY (product_id) REFERENCES ops.product (id) ON DELETE CASCADE
);
CREATE INDEX ind_artifact_product_1 ON ops.artifact_product (artifact_id, product_id);
CREATE INDEX ind_artifact_product_2 ON ops.artifact_product (product_id, artifact_id);