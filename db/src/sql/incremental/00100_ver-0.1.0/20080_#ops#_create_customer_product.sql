CREATE TABLE ops.customer_product
(
    customer_id bigint NOT NULL,
    product_id bigint NOT NULL,
    CONSTRAINT FK_customer_product_customer FOREIGN KEY (customer_id) REFERENCES ops.customer (id) ON DELETE CASCADE,
    CONSTRAINT FK_customer_product_product FOREIGN KEY (product_id) REFERENCES ops.product (id) ON DELETE CASCADE
);
CREATE UNIQUE INDEX IND_customer_product_customer ON ops.customer_product (customer_id, product_id);
CREATE UNIQUE INDEX IND_customer_product_product ON ops.customer_product (product_id, customer_id);