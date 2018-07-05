CREATE OR REPLACE FUNCTION system.tie_invoices_with_calls(v_invoice_ids BIGINT [], v_charge_job_id VARCHAR DEFAULT NULL)
  RETURNS VOID AS
$$
DECLARE
  inv          RECORD;
  l_service_id BIGINT;
BEGIN
  -- связываем инвойсы по договорам и факты использования
  FOR inv IN SELECT *
             FROM system.get_invoices_for_call_processing(v_invoice_ids :: BIGINT [])
             WHERE NOT without_contract
  LOOP
    -- связываем инвойсы, имедющие договор с поставщиком и вызовы
    INSERT INTO system.usage_invoice_rated_outgoing_call (usage_invoice_id, call_id)
      SELECT
        inv.id,
        roc.call_id
      FROM
        system.rated_outgoing_calls roc
        LEFT JOIN system.usage_invoice_rated_outgoing_call ic ON roc.call_id = ic.call_id
        LEFT JOIN system.unsuitable_rated_outgoing_call uc ON roc.call_id = uc.call_id
      WHERE
        ic.call_id IS NULL
        AND uc.call_id IS NULL
        AND roc.service_id = inv.service_id
        AND roc.supplier_id = inv.provider_id
        AND roc.telephony_zone_id = ANY (inv.zones_ids)
        AND inv.start_date <= roc.call_date AND roc.call_date <= inv.end_date;
  END LOOP;

  -- формируем отсев для случаев, когда не подходит зона, но равен поставщик. Чтобы такие вызовы не попали в особый инвой
  FOR inv IN SELECT *
             FROM system.get_invoices_for_call_processing(v_invoice_ids :: BIGINT [])
  LOOP
    INSERT INTO system.unsuitable_rated_outgoing_call (call_id, job_id)
      SELECT
        roc.call_id,
        roc.charge_job_id
      FROM
        system.rated_outgoing_calls roc
        LEFT JOIN system.usage_invoice_rated_outgoing_call ic ON roc.call_id = ic.call_id
        LEFT JOIN system.unsuitable_rated_outgoing_call uc ON roc.call_id = uc.call_id
      WHERE
        ic.call_id IS NULL
        AND uc.call_id IS NULL
        AND roc.service_id = inv.service_id
        AND roc.supplier_id = inv.provider_id
        AND inv.start_date <= roc.call_date AND roc.call_date <= inv.end_date
        AND (v_charge_job_id IS NULL OR roc.charge_job_id = coalesce(v_charge_job_id, ''));
  END LOOP;

  -- связываем осбые инвойсы, без договора, и факты использования
  FOR inv IN SELECT *
             FROM system.get_invoices_for_call_processing(v_invoice_ids :: BIGINT [])
             WHERE without_contract
  LOOP
    -- связываем инвойсы, не имедющие договор с поставщиком и вызовы
    INSERT INTO system.usage_invoice_rated_outgoing_call (usage_invoice_id, call_id)
      SELECT
        inv.id,
        roc.call_id
      FROM
        system.rated_outgoing_calls roc
        LEFT JOIN system.usage_invoice_rated_outgoing_call ic ON roc.call_id = ic.call_id
        LEFT JOIN system.unsuitable_rated_outgoing_call uc ON roc.call_id = uc.call_id
      WHERE
        ic.call_id IS NULL
        AND uc.call_id IS NULL
        AND roc.service_id = inv.service_id
        AND inv.start_date <= roc.call_date AND roc.call_date <= inv.end_date;
  END LOOP;

  -- выбрать остаток за период, услуги (если передан job_id включить в фильтрацию), по закрытым инвойсам
  -- и поместить в отсев
  FOR inv IN SELECT
               i.service_id,
               min(i.start_date) AS start_date,
               max(i.end_date)   AS end_date
             FROM system.invoice i
             WHERE id = ANY (v_invoice_ids)
             GROUP BY i.service_id
  LOOP
    INSERT INTO system.unsuitable_rated_outgoing_call (call_id, job_id)
      SELECT
        roc.call_id,
        roc.charge_job_id
      FROM
        system.rated_outgoing_calls roc
        LEFT JOIN system.usage_invoice_rated_outgoing_call ic ON roc.call_id = ic.call_id
        LEFT JOIN system.unsuitable_rated_outgoing_call uc ON roc.call_id = uc.call_id
      WHERE
        ic.call_id IS NULL
        AND uc.call_id IS NULL
        AND roc.service_id = inv.service_id
        AND inv.start_date <= roc.call_date AND roc.call_date <= inv.end_date
        AND (v_charge_job_id IS NULL OR roc.charge_job_id = coalesce(v_charge_job_id, ''));
  END LOOP;

  -- Положить в отсев все не обработанные факты использования. Выполнять только при тарификации.
  -- Добавлено, т.к. в отсев не попадали факты подходящие к уже закрытым инвойсам
  IF (v_charge_job_id IS NULL OR v_charge_job_id = '')
  THEN
    SELECT i.service_id
    INTO l_service_id
    FROM system.invoice i
    WHERE i.id = v_invoice_ids [1];

    INSERT INTO SYSTEM.unsuitable_rated_outgoing_call (call_id, job_id)
      SELECT
        roc.call_id,
        roc.charge_job_id
      FROM
        SYSTEM.rated_outgoing_calls roc
        LEFT JOIN SYSTEM.usage_invoice_rated_outgoing_call ic ON roc.call_id = ic.call_id
        LEFT JOIN SYSTEM.unsuitable_rated_outgoing_call uc ON roc.call_id = uc.call_id
      WHERE
        ic.call_id IS NULL
        AND uc.call_id IS NULL
        AND roc.service_id = l_service_id
        AND upper(roc.charge_job_id) LIKE 'C%';
  END IF;

END;
$$
LANGUAGE plpgsql;
/