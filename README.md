# ItemDatabaseLink

## Description
With IDL you can send some materials/items to players via database.

Example:
```sql
INSERT INTO minecraft.idl_items (uuid, type, value, qty, status) VALUES ('3630a9c7-1b18-3c4d-9cc9-462674d3795e', 'Item', 'DIAMOND', 64, 0)
```

Gives 64 diamonds for player with uuid 3630a9c7-1b18-3c4d-9cc9-462674d3795e

## Features
* Databases: MySQL (more in future)
* `/get` command for getting all new items to inventory (or dropping on ground if inventory is full)
* flexible configuration
* AuthMe integration