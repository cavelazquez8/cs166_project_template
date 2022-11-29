COPY Users
FROM '/home/user/cs166_project_template/data/users.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE users_userID_seq RESTART 101;

COPY Store
FROM '/home/user/cs166_project_template/data/stores.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Product
FROM '/home/user/cs166_project_template/data/products.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Warehouse
FROM '/home/user/cs166_project_template/data/warehouse.csv'
WITH DELIMITER ',' CSV HEADER;

COPY Orders
FROM '/home/user/cs166_project_template/data/orders.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE orders_orderNumber_seq RESTART 501;


COPY ProductSupplyRequests
FROM '/home/user/cs166_project_template/data/productSupplyRequests.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE productsupplyrequests_requestNumber_seq RESTART 11;

COPY ProductUpdates
FROM '/home/user/cs166_project_template/data/productUpdates.csv'
WITH DELIMITER ',' CSV HEADER;
ALTER SEQUENCE productupdates_updateNumber_seq RESTART 51;
