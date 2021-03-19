-- Простые запросы
EXPLAIN
SELECT * FROM actor;

EXPLAIN ANALYZE
SELECT * FROM actor;

EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM actor;

EXPLAIN (ANALYZE, BUFFERS, FORMAT TEXT)
SELECT * FROM actor;

EXPLAIN (ANALYZE, BUFFERS, FORMAT XML)
SELECT * FROM actor;

EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON)
SELECT * FROM actor;

EXPLAIN (ANALYZE, BUFFERS, FORMAT YAML)
SELECT * FROM actor;

-- https://blog.bullgare.com/wp-content/uploads/2015/04/understanding_explain.html

-- Создаем тестовую таблицу
DROP TABLE IF EXISTS foo;
DROP TABLE IF EXISTS bar;

CREATE TABLE foo (c1 integer, c2 text);

INSERT INTO foo
SELECT i, md5(random()::text)
FROM generate_series(1, 1000000) AS i;

-- Посмотрим, что вставили
SELECT * FROM foo;

-- Читаем данные
EXPLAIN
SELECT * FROM foo;
-- Seq Scan

-- Попробуем добавить 10 строк.
INSERT INTO foo
SELECT i, md5(random()::text)
FROM generate_series(1, 10) AS i;

-- Опять выполним запрос
SELECT count(*) FROM foo;

EXPLAIN
SELECT * FROM foo;
-- Видим старое значение estimated rows

-- Обновим статистику через ANALYZE
ANALYZE foo;
EXPLAIN SELECT * FROM foo;
-- Теперь в rows актуальное значение.

-- EXPLAIN выводит гипотетический план, запрос не выполняется
-- EXPLAIN ANALYZE - реальный, запрос выполняется,
-- добавляется раздел "actual"
EXPLAIN ANALYZE
SELECT * FROM foo;

-- Использование памяти
EXPLAIN (ANALYZE,BUFFERS)
SELECT * FROM foo;
-- Buffers: shared read — количество блоков, считанных с диска.
-- Buffers: shared hit — количество блоков, считанных из кэша.

-- ------------
-- WHERE
-- ------------
EXPLAIN ANALYZE
SELECT * FROM foo WHERE c1 > 500;
-- Seq Scan, т.к. индексов нет.
-- Filter: (c1 > 500)
-- Rows Removed by Filter: 510

-- Seq Scan on foo  (cost=0.00..20834.12 rows=999541 width=37) (actual time=1.312..2344.030 rows=999500 loops=1)
--   Filter: (c1 > 500)
--   Rows Removed by Filter: 510
-- Planning time: 0.104 ms
-- Execution time: 3170.731 ms


-- ------------
-- Индексы
-- ------------
-- Создадим индекс
CREATE INDEX ON foo(c1);
-- Выполним тот же запрос
EXPLAIN ANALYZE
SELECT * FROM foo WHERE c1 > 500;
-- Seq Scan - хотя индекс есть

-- Отфильтровано 510 строк из более чем миллиона (Rows Removed by Filter: 510)

-- Seq Scan on foo  (cost=0.00..20834.12 rows=999533 width=37) (actual time=0.510..2658.878 rows=999500 loops=1)
--   Filter: (c1 > 500)
--   Rows Removed by Filter: 510
-- Planning time: 0.677 ms
-- Execution time: 3520.871 ms

-- Запретим Seq Scan и попробуем использовать индекс принудительно:
SET enable_seqscan TO off;

EXPLAIN ANALYZE
SELECT * FROM foo WHERE c1 > 500;

SET enable_seqscan TO on;
-- Index Scan, Index Cond
-- А что со стоимостью?

-- Изменим запрос - выбираем мало записей
EXPLAIN ANALYZE
SELECT * FROM foo WHERE c1 < 500;
-- Index Scan

-- Усложним условие. Используем текстовое поле.
EXPLAIN ANALYZE
SELECT * FROM foo
WHERE c1 < 500 AND c2 LIKE 'abcd%';

-- Если в условии только текстовое поле:
EXPLAIN ANALYZE
SELECT * FROM foo WHERE c2 LIKE 'abcd%';
-- Seq Scan, индексов на c2 нет

-- Создадим индекс по c2
CREATE INDEX ON foo(c2 text_pattern_ops);
-- Выполняем запрос
EXPLAIN ANALYZE
SELECT * FROM foo
WHERE c2 LIKE 'abcd%';
-- Bitmap Index Scan, индекс foo_c2_idx1 для определения нужных нам записей,

-- Покрывающий индекс - Index Only Scan
EXPLAIN ANALYZE
SELECT c1 FROM foo WHERE c1 < 500;

-- ------------
-- Сортировка
-- ------------
-- Попробуем сначала с индексом
EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM foo ORDER BY c1;

-- А потом удалим индекс
DROP INDEX foo_c1_idx;
-- CREATE INDEX ON foo(c1);

EXPLAIN (ANALYZE, BUFFERS)
SELECT * FROM foo ORDER BY c1;
-- есть:
-- Sort Method: external sort
-- temp read=5751 written=57451 (это страницы)

-- DROP INDEX foo_c1_idx;

-- Попробуем увеличить work_mem:
SET work_mem TO '200MB';
EXPLAIN (ANALYZE)
SELECT * FROM foo ORDER BY c1;
-- Sort Method: quicksort - все в памяти


-- ----------
-- LIKE
-- ----------

-- Будет ли использоваться индекс?
EXPLAIN (ANALYZE,BUFFERS)
SELECT * FROM foo WHERE c2 LIKE 'abc%';

-- А здесь?
EXPLAIN (ANALYZE,BUFFERS)
SELECT * FROM foo WHERE c2 LIKE '%abc';

-- Что со стоимостью и временем?

-- -----------
-- JOIN
-- -----------

-- Создадим новую таблицу, вставим данные.

CREATE TABLE bar (c1 integer, c2 boolean);

INSERT INTO bar
SELECT i, i%2 = 1
FROM generate_series(1, 500000) AS i;

ANALYZE bar;

-- Посмотрим, что там
SELECT * FROM bar;

-- Запрос по двум таблицам
EXPLAIN ANALYZE
SELECT *
FROM foo
JOIN bar ON foo.c1 = bar.c1;
-- Hash Join
-- Запомним стоимость и время:
-- ->  Seq Scan on foo  (cost=0.00..18334.10 rows=1000010 width=37) (actual time=0.049..1572.154 rows=1000010 loops=1)
--   ->  Hash  (cost=7213.00..7213.00 rows=500000 width=5) (actual time=2111.893..2111.893 rows=500000 loops=1)
--         Buckets: 524288  Batches: 1  Memory Usage: 22163kB
--         ->  Seq Scan on bar  (cost=0.00..7213.00 rows=500000 width=5) (actual time=0.081..844.095 rows=500000 loops=1)
-- Planning time: 1.289 ms
-- Execution time: 8370.692 ms

-- Добавим индексы
CREATE INDEX ON foo(c1); -- этот мы удаляли
CREATE INDEX ON bar(c1);

-- Тот же запрос - какой будет тип JOIN?
EXPLAIN ANALYZE
SELECT *
FROM foo
JOIN bar ON foo.c1 = bar.c1;

DROP INDEX foo_c1_idx; -- этот мы удаляли
DROP INDEX bar_c1_idx;

-- Merge Join  (cost=2.03..39787.09 rows=500000 width=42) (actual time=0.335..6107.047 rows=500010 loops=1)
--   Merge Cond: (foo.c1 = bar.c1)
--   ->  Index Scan using foo_c1_idx on foo  (cost=0.42..34317.58 rows=1000010 width=37) (actual time=0.283..1469.190 rows=500011 loops=1)
--   ->  Index Scan using bar_c1_idx on bar  (cost=0.42..15212.42 rows=500000 width=5) (actual time=0.042..1431.064 rows=500010 loops=1)
-- Planning time: 2.692 ms
-- Execution time: 6533.678 ms
