CREATE OR REPLACE VIEW datamart.transactions_vw AS
  SELECT
    t.personal_account_id,
    t.transaction_date                           AS execution_date,
    t.business_date                              AS planned_date,
    regexp_replace(tr.dtype, '(.*)Reason', '\1') AS reason_type,

    (CASE tr.dtype
     WHEN 'UserReason'
       THEN tr.reason_number
     WHEN 'PaymentDocReason'
       THEN tr.payment_doc_source
     WHEN 'InvoiceReason'
       THEN tr.invoice_id :: VARCHAR
     END)                                        AS reason_name,

    urt.id                                       AS user_reason_type_id,
    urt.name                                     AS user_reason_type_name,
    t.amount
  FROM system.transactions t
    JOIN system.transaction_reason tr ON t.id = tr.id
    LEFT JOIN system.user_reason_type urt ON tr.user_reason_type_id = urt.id;