-- Drop tables before creating them
DROP TABLE IF EXISTS fittings;
DROP TABLE IF EXISTS fitting;
DROP TABLE IF EXISTS ships;
DROP TABLE IF EXISTS classes;
DROP TABLE IF EXISTS solar_systems;

-- classes table holds all the ship classes and class related information
CREATE TABLE classes
(
  class_id            integer     NOT NULL,
  class_name          varchar(40) NOT NULL,
  reachability        bigint      NOT NULL,
  base_health_points  integer     NOT NULL,
  max_fittings_count  integer     NOT NULL,
  PRIMARY KEY
  (
    class_id
  )
);

-- solar_systems table holds all solar systems, their size and security level
CREATE TABLE solar_systems
(
  solar_system_id integer NOT NULL,
  max_position    geography(POINTZ, 4326),
  security_level  integer NOT NULL,
  PRIMARY KEY
  (
    solar_system_id
  )
);

-- ships table holds every ship, their position and current health points
CREATE TABLE ships
(
  ship_id         integer NOT NULL,
  position        geography(POINTZM, 4326),
  class_id        integer NOT NULL REFERENCES classes (class_id),
  health_points   integer NOT NULL,
  PRIMARY KEY
  (
    ship_id
  )
);
CREATE INDEX idx_ships ON ships USING GIST (position);

-- fitting table holds information about a fitting
CREATE TABLE fitting
(
  fitting_id    integer NOT NULL,
  fitting_type  integer NOT NULL,
  fitting_value integer NOT NULL,
  PRIMARY KEY
  (
    fitting_id
  )
);

-- fittings table hold the links between ships and fittings
CREATE TABLE fittings
(
  fittings_id integer NOT NULL,
  ship_id     integer NOT NULL REFERENCES ships (ship_id),
  fitting_id  integer NOT NULL REFERENCES fitting (fitting_id),
  PRIMARY KEY
  (
    fittings_id
  )
);
CREATE INDEX idx_fittings_shipid ON fittings(ship_id);
