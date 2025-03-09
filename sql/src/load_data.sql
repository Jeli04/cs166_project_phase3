/* Replace the location to where you saved the data files*/
COPY Users
FROM '/Users/jerryli/Desktop/CS166/cs166_project_phase3/data/users.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Items
FROM '/Users/jerryli/Desktop/CS166/cs166_project_phase3/data/items.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Store
FROM '/Users/jerryli/Desktop/CS166/cs166_project_phase3/data/store.csv'
WITH DELIMITER ',' CSV HEADER;

COPY FoodOrder
FROM '/Users/jerryli/Desktop/CS166/cs166_project_phase3/data/foodorder.csv'
WITH DELIMITER ',' CSV HEADER;

COPY ItemsInOrder
FROM '/Users/jerryli/Desktop/CS166/cs166_project_phase3/data/itemsinorder.csv'
WITH DELIMITER ',' CSV HEADER;
