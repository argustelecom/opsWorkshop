CREATE OR REPLACE VIEW datamart.product_offering_vw AS
  SELECT
    po.id                           AS product_offering_id,
    pl.name                         AS pricelist_name,
    po.product_type_id              AS product_id,
    pt.name                         AS product_name,
    (CASE
     WHEN po.dtype = 'PeriodProductOffering'
       THEN po.period_amount
     WHEN po.dtype = 'MeasuredProductOffering'
       THEN po.measure_value
     END)                           AS product_amount,
    (CASE
     WHEN po.dtype = 'PeriodProductOffering'
       THEN po.period_unit
     WHEN po.dtype = 'MeasuredProductOffering'
       THEN mu.symbol
     END)                           AS amount_measure_unit,
    po.price                        AS price_with_tax,
    po.price / (1 + tax_rate / 100) AS price_without_tax,
    po.currency,
    ow.tax_rate,
    po.provision_terms_id,
    ptms.name                       AS provision_terms_name
  FROM system.product_offering po
    JOIN system.pricelist pl ON po.pricelist_id = pl.id
    JOIN system.owner ow ON pl.owner_id = ow.id
    LEFT JOIN system.product_type pt ON po.product_type_id = pt.id
    LEFT JOIN system.provision_terms ptms ON po.provision_terms_id = ptms.id
    LEFT JOIN system.measure_unit mu ON po.measure_unit_id = mu.id;