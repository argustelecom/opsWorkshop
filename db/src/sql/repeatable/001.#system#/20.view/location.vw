CREATE OR REPLACE VIEW system.address_view AS
  WITH RECURSIVE parents(id, dtype, name, full_name, parent_id, level) AS (
    SELECT
      l.id           AS id,
      l.dtype        AS dtype,
      l.name         AS name,
      concat(l.name) AS full_name,
      l.parent_id    AS parent_id,
      1              AS level
    FROM system.location l
    WHERE l.parent_id IS NULL
    UNION ALL
    SELECT
      l.id                              AS id,
      l.dtype                           AS dtype,
      l.name                            AS name,
      concat(p.full_name, ', ', l.name) AS full_name,
      l.parent_id                       AS parent_id,
      p.level + 1                       AS level
    FROM system.location l, parents p
    WHERE p.id = l.parent_id
  )
  SELECT *
  FROM parents;