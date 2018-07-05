CREATE OR REPLACE FUNCTION datamart_sys.format_party_name(
  a_party_rec IN system.PARTY
) RETURNS VARCHAR AS $$
BEGIN
  CASE
    WHEN a_party_rec.dtype = 'Company'
    THEN RETURN coalesce(a_party_rec.brand_name, a_party_rec.legal_name);

    WHEN a_party_rec.dtype = 'Person'
    THEN
      RETURN concat_ws(
          ' ', trim(a_party_rec.last_name), trim(a_party_rec.first_name), trim(a_party_rec.second_name)
      );
  ELSE
    RETURN a_party_rec.dtype;
  END CASE;
END;
$$
LANGUAGE plpgsql STABLE COST 1
RETURNS NULL ON NULL INPUT
SECURITY DEFINER;
/