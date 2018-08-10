create table ops.products_customers (
		product_id bigint NOT NULL,
		customer_id bigint NOT NULL,
	CONSTRAINT pk_products_customers PRIMARY KEY (product_id, customer_id),
	CONSTRAINT fk_customer FOREIGN KEY (customer_id) REFERENCES ops.customer (id),
	CONSTRAINT fk_product FOREIGN KEY (product_id) REFERENCES ops.product (id)
    );