/* Replace the location to where you saved the data files*/
COPY Users
FROM '/Users/samuribe/School/cs166/cs166_project_phase3-1/data/users.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Items
FROM '/Users/samuribe/School/cs166/cs166_project_phase3-1/data/items.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Store
FROM '/Users/samuribe/School/cs166/cs166_project_phase3-1/data/store.csv'
WITH DELIMITER ',' CSV HEADER;

COPY FoodOrder
FROM '/Users/samuribe/School/cs166/cs166_project_phase3-1/data/foodorder.csv'
WITH DELIMITER ',' CSV HEADER;

COPY ItemsInOrder
FROM '/Users/samuribe/School/cs166/cs166_project_phase3-1/data/itemsinorder.csv'
WITH DELIMITER ',' CSV HEADER;
