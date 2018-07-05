CREATE OR REPLACE VIEW datamart.bill_incomes_vw AS
  SELECT
    b.id                                         AS bill_id,
    incomesagglist."analyticTypeId"              AS analytic_type_id,
    incomesagglist.keyword                       AS analytic_keyword,
    COALESCE(incomesagglist.sum, (0) :: NUMERIC) AS sum
  FROM system.bill b,
    LATERAL ( SELECT
                c.sum,
                c.error,
                c.keyword,
                c."analyticTypeId"
              FROM jsonb_to_recordset(((b.agg_data) :: JSONB -> 'incomesAggList')) AS c (
                   "sum" NUMERIC,
                   "error" VARCHAR,
                   "keyword" VARCHAR,
                   "analyticTypeId" INTEGER
                   )) incomesagglist;