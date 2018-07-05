-- включить все индексы поиска
CREATE OR REPLACE FUNCTION address_search.enable_all_idx()
  RETURNS VOID
AS $$
BEGIN
  --
  -- индексы таблицы common_idx
  CREATE INDEX IF NOT EXISTS ind_common_idx_parent
    ON address_search.common_idx
    USING BTREE (parent_id);

  CREATE INDEX IF NOT EXISTS ind_common_idx_tree_id
    ON address_search.common_idx
    USING GIN (tree_id);

  CREATE INDEX IF NOT EXISTS ind_common_idx_match
    ON address_search.common_idx
    USING BTREE (class, parent_id, search_name text_pattern_ops);

  --
  -- индексы таблицы region_idx
  CREATE INDEX IF NOT EXISTS ind_region_idx_parent
    ON address_search.region_idx
    USING BTREE (parent_id);

  CREATE INDEX IF NOT EXISTS ind_region_idx_tree_id
    ON address_search.region_idx
    USING GIN (tree_id);

  CREATE INDEX IF NOT EXISTS ind_region_idx_trg_search_name
    ON address_search.region_idx
    USING GIN (search_name gin_trgm_ops);

  CREATE INDEX IF NOT EXISTS ind_region_idx_trg_search_tree_name
    ON address_search.region_idx
    USING GIN (tree_search_name gin_trgm_ops);

  --
  -- индексы таблицы street_idx
  CREATE INDEX IF NOT EXISTS ind_street_idx_parent
    ON address_search.street_idx
    USING BTREE (parent_id);

  CREATE INDEX IF NOT EXISTS ind_street_idx_tree_id
    ON address_search.street_idx
    USING GIN (tree_id);

  CREATE INDEX IF NOT EXISTS ind_street_idx_match
    ON address_search.street_idx
    USING GIN (parent_id, search_name gin_trgm_ops);

  CREATE INDEX IF NOT EXISTS ind_street_idx_trg_search_name
    ON address_search.street_idx
    USING GIN (search_name gin_trgm_ops);
END;
$$ LANGUAGE plpgsql;
/