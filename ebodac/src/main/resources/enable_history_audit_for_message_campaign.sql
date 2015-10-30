-- enables history audit for Message Campaign ---

UPDATE Tracking
INNER JOIN Entity
ON Tracking.entity_id_OID = Entity.id
SET Tracking.recordHistory = 1, Tracking.modifiedByUser = 1
WHERE Entity.name="CampaignRecord" or Entity.name="CampaignMessageRecord";
