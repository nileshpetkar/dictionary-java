
-- Author: Nilesh K. Petkar
--
-- File generated with SQLiteStudio v3.1.1 on Mon Apr 23 15:36:31 2018
--
-- Text encoding used: System
--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Table: dicttable
CREATE TABLE dicttable (
    wordid     INTEGER  PRIMARY KEY AUTOINCREMENT,
    word       TEXT,
    meaning    TEXT,
    example    TEXT,
    synonym    TEXT,
    antonym    TEXT,
    category   TEXT,
    recent     TEXT,
    fav        TEXT,
    searchdate DATETIME,
    isdayword  TEXT
);


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
