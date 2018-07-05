-- отключить все индексы поиска
CREATE OR REPLACE FUNCTION address_search.disable_all_idx()
  RETURNS VOID
AS $$
BEGIN
  -- индексы таблицы common_idx
  DROP INDEX IF EXISTS address_search.ind_common_idx_parent;
  DROP INDEX IF EXISTS address_search.ind_common_idx_tree_id;
  DROP INDEX IF EXISTS address_search.ind_common_idx_match;

  -- индексы таблицы region_idx
  DROP INDEX IF EXISTS address_search.ind_region_idx_parent;
  DROP INDEX IF EXISTS address_search.ind_region_idx_tree_id;
  DROP INDEX IF EXISTS address_search.ind_region_idx_trg_search_name;
  DROP INDEX IF EXISTS address_search.ind_region_idx_trg_search_tree_name;

  -- индексы таблицы street_idx
  DROP INDEX IF EXISTS address_search.ind_street_idx_parent;
  DROP INDEX IF EXISTS address_search.ind_street_idx_tree_id;
  DROP INDEX IF EXISTS address_search.ind_street_idx_match;
  DROP INDEX IF EXISTS address_search.ind_street_idx_trg_search_name;
END;
$$ LANGUAGE plpgsql;
/