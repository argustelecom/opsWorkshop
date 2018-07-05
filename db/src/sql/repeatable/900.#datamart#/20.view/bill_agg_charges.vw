CREATE OR REPLACE VIEW datamart.bill_agg_charges_vw AS
  SELECT
    b.id                                          AS bill_id,
    charges_agg_list."analyticTypeId"             AS analytic_type_id,
    charges_agg_list."keyword"                    AS analytic_keyword,
    charges_agg_list."productId"                  AS product_id,
    coalesce(charges_agg_list."sum", 0)           AS sum_total,
    coalesce(charges_agg_list."sumWithoutTax", 0) AS sum_without_tax,
    coalesce(charges_agg_list."tax", 0)           AS sum_tax,
    coalesce(charges_agg_list."discountSum", 0)   AS sum_discount,
    charges_agg_list."row"                        AS bill_row
  FROM
    system.bill b,
    LATERAL ((
      SELECT *
      FROM jsonb_to_recordset((b.agg_data :: JSONB) -> 'chargesAggList') AS c (
           "row" BOOLEAN,
           "sum" NUMERIC,
           "tax" NUMERIC,
           "error" VARCHAR,
           "keyword" VARCHAR,
           "periodic" BOOLEAN,
           "productId" BIGINT,
           "discountSum" NUMERIC,
           "sumWithoutTax" NUMERIC,
           "analyticTypeId" INT
           )
    )) charges_agg_list;