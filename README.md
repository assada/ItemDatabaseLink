# ItemDatabaseLink

[![Packaing Jar And Upload](https://github.com/assada/ItemDatabaseLink/actions/workflows/maven.yml/badge.svg)](https://github.com/assada/ItemDatabaseLink/actions/workflows/maven.yml)

## Description
With IDL you can reward player with some materials/items/effects/money to players via database.

Examples:
```sql
INSERT INTO minecraft.idl_items (uuid, type, value, qty, status) VALUES ('3630a9c7-1b18-3c4d-9cc9-462674d3795e', 'ITEM', 'DIAMOND', 64, 0)
```
Gives 64 diamonds for player with uuid 3630a9c7-1b18-3c4d-9cc9-462674d3795e
```sql
INSERT INTO minecraft.idl_items (uuid, type, value, qty, status) VALUES ('3630a9c7-1b18-3c4d-9cc9-462674d3795e', 'EFFECT', 'SPEED', 999, 0)
```
Apply speed potion(for 99.9 seconds) for player with uuid 3630a9c7-1b18-3c4d-9cc9-462674d3795e

```sql
INSERT INTO minecraft.idl_items (uuid, type, value, qty, status) VALUES ('3630a9c7-1b18-3c4d-9cc9-462674d3795e', 'EXPERIENCE', 'Experience', 10000, 0)
```
Add 10000 experience for player with uuid 3630a9c7-1b18-3c4d-9cc9-462674d3795e
```sql
INSERT INTO minecraft.idl_items (uuid, type, value, qty, status) VALUES ('3630a9c7-1b18-3c4d-9cc9-462674d3795e', 'HEAL', 'Heal', 0, 0)
```
Full heal and remove all bad potion effects player with uuid 3630a9c7-1b18-3c4d-9cc9-462674d3795e

## Features
* Databases: MySQL (more in future if requested)
* `/claim` command for getting all new items to inventory (or dropping on ground if inventory is full) or apply other rewards
* flexible configuration
* AuthMe integration (DB requests after successful login)
* Vault integration (for money rewards)
