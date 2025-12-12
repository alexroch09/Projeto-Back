INSERT INTO call_status (status_name)
SELECT * FROM (
    SELECT 'Pendente' AS status_name UNION ALL
    SELECT 'Em andamento' AS status_name UNION ALL
    SELECT 'Concluido' AS status_name
) AS tmp
WHERE NOT EXISTS (
    SELECT 1 FROM call_status
);


