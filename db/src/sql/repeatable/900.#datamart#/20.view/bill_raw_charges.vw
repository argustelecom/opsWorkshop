CREATE OR REPLACE VIEW datamart.bill_raw_charges_vw
  AS
    SELECT
      b.id                                        AS bill_id,
      charges_raw_list."analyticTypeId"           AS analytic_type_id,
      charges_raw_list."productId"                AS product_id,
      coalesce(charges_raw_list."sum", 0)         AS sum_total,
      coalesce(charges_raw_list."discountSum", 0) AS discount_sum,
      coalesce(charges_raw_list."taxRate", 0)     AS tax_rate,
      charges_raw_list."startDate"                AS start_date,
      charges_raw_list."endDate"                  AS end_date,
      charges_raw_list."subscriptionId"           AS subscription_id,
      charges_raw_list."row"                      AS bill_row
    FROM
      system.bill b
      JOIN system.bill_raw_data br ON b.bill_raw_data_id = br.id
      ,
      LATERAL ((
        SELECT *
        FROM jsonb_to_recordset((br.raw_data :: JSONB) -> 'chargesRawList') AS c (
             "analyticTypeId" INT,
             "error" VARCHAR,
             "productId" BIGINT,
             "subscriptionId" BIGINT,
             "taxRate" NUMERIC,
             "startDate" NUMERIC,
             "endDate" NUMERIC,
             "sum" NUMERIC,
             "discountSum" NUMERIC,
             "row" BOOLEAN
             )
      )) charges_raw_list;