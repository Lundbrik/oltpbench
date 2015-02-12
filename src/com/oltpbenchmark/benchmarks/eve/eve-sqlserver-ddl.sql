-- Drop all tables
IF OBJECT_ID('SHIPS') IS NOT NULL DROP table SHIPS;
IF OBJECT_ID('SOLARSYSTEMS') IS NOT NULL DROP table SOLARSYSTEMS;
IF OBJECT_ID('CLASSES') IS NOT NULL DROP table CLASSES;

-- Class table holds the different ship classes
CREATE TABLE CLASSES
(
  cid   integer     NOT NULL
, class varchar(30) NOT NULL
, reach integer     NOT NULL
, PRIMARY KEY
  (
    cid
  )
);

-- Solarsystem table holds the solar systems in the game
CREATE TABLE SOLARSYSTEMS
(
  ssid   integer   NOT NULL
, x_max  integer   NOT NULL
, y_max  integer   NOT NULL
, PRIMARY KEY
  (
    ssid
  )
);

-- Ship table holds the ships (players) in the game instance
CREATE TABLE SHIPS
(
  id      integer     NOT NULL
, x_coord integer     NOT NULL
, y_coord integer     NOT NULL
, cid     integer     NOT NULL REFERENCES CLASSES (cid)
, ssid    integer     NOT NULL REFERENCES SOLARSYSTEMS (ssid)
, PRIMARY KEY
  (
    id
  )
);

