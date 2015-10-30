-- enables history audit for CSD ---

UPDATE Tracking
SET
    recordHistory = 1,
    modifiedByUser = 1
WHERE
    entity_id_OID IN (SELECT
            id
        FROM
            Entity
        WHERE
            className LIKE 'org.motechproject.csd.%');
