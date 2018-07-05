/*
 * Вспомогательное представление для построения индекса
 */
CREATE OR REPLACE VIEW address_search.location_vw AS
  SELECT

    /*
     * идентификатор базового адресного объекта
     */
    l.id,

    /*
     * идентификатор родителя. Ссылка на базовый адресный объект
     */
    l.parent_id,

    /*
     * нормализованное представление класса адресного объекта
     */
    (CASE l.dtype
     WHEN 'Country'
       THEN 'C'
     WHEN 'Region'
       THEN 'R'
     WHEN 'Street'
       THEN 'S'
     WHEN 'Building'
       THEN 'B'
     WHEN 'Lodging'
       THEN 'L'
     ELSE
       'U'
     END) :: address_search.T_LOCATION_CLASS AS class,

    /*
     * идентификатор типа адресного объекта (имеет смысл только для регионов и улиц)
     */
    (CASE l.dtype
     WHEN 'Region'
       THEN rt.id
     WHEN 'Street'
       THEN st.id
     ELSE
       NULL
     END) :: BIGINT                          AS type_id,

    /*
     * нормализованное для поиска собственное имя адресного объекта
     * - удалены лишние пробелы
     * - удалены для регионов и улиц вхождения наименований типов (как стоп слова)
     * - удалены не alpha numeric символы
     * - приведено к нижнему регистру для использования простого like вместо ilike
     */
    (CASE l.dtype
     WHEN 'Region'
       THEN address_search.normalize(l.name, FALSE)
     WHEN 'Street'
       THEN address_search.normalize(l.name, FALSE)
     ELSE
       address_search.normalize(l.name, FALSE)
     END) :: VARCHAR                         AS search_name,

    /*
     * типизированное имя, состоит из исходного и добавленного в конце короткого наименования типа
     * Используется для построения квалифицированного имени адресного объекта для показа пользователю
     */
    (CASE l.dtype
     WHEN 'Region'
       THEN trim(concat(trim(coalesce(l.name, '')), ' ', trim(coalesce(rt.short_name, ''))))
     WHEN 'Street'
       THEN trim(concat(trim(coalesce(l.name, '')), ' ', trim(coalesce(st.short_name, ''))))
     ELSE
       l.name
     END) :: VARCHAR                         AS typed_name

  FROM system.location l
    LEFT JOIN system.region r ON l.dtype = 'Region' AND l.id = r.id
    LEFT JOIN system.street s ON l.dtype = 'Street' AND l.id = s.id
    LEFT JOIN system.location_type rt ON r.type_id = rt.id
    LEFT JOIN system.location_type st ON s.type_id = st.id
  /*
  * Индекс содержит только регионы, улицы, здания. Поиск по странам не нужен, т.к. их, с одной стороны, мало
  * с другой стороны они встречаются в каждом квалифицированном пути локиции. Т.е. страна становится стоп-словом
  * поиска из-за слишком большой популярности.
  * Также индекс не содержит квартиры / помещения, потому что в этом случае выбирать бы пришлось не по миллионам,
  * а по десяткам миллионов объектов
  */
  WHERE l.dtype = ANY (ARRAY ['Region', 'Street', 'Building']);