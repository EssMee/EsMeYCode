-- table products
CREATE TABLE products (
    id INT,
    name STRING,
    description STRING,
    PRIMARY KEY (id) NOT ENFORCED
) WITH (
    'connector' = 'mysql-cdc',
    'hostname' = 'localhost',
    'port' = '3306',
    'username' = 'root',
    'password' = '123456',
    'database-name' = 'mydb',
    'table-name' = 'products'
);

-- table orders
create table orders (
    order_id int,
    order_date TIMESTAMP(0),
    customer_name string,
    price decimal(10, 5),
    product_id int,
    order_status boolean,
    PRIMARY KEY (order_id) not enforced
) with (
    'connector' = 'mysql-cdc',
    'hostname' = 'localhost',
    'port' = '3306',
    'username' = 'root',
    'password' = '123456',
    'database-name' = 'mydb',
    'table-name' = 'orders'
);

-- table shipments
CREATE TABLE shipments (
    shipment_id INT,
    order_id INT,
    origin STRING,
    destination STRING,
    is_arrived BOOLEAN,
    PRIMARY KEY (shipment_id) NOT ENFORCED
) WITH (
    'connector' = 'postgres-cdc',
    'hostname' = 'localhost',
    'port' = '5432',
    'username' = 'postgres',
    'password' = 'postgres',
    'database-name' = 'postgres',
    'schema-name' = 'public',
    'table-name' = 'shipments'
);


-- table userd to load data to es
create table enriched_orders(
    order_id int,
    order_date TIMESTAMP(0),
    customer_name string,
    price decimal(10, 5),
    product_id int,
    order_status boolean,
    product_name string,
    product_description string,
    shipment_id int,
    origin string,
    destination string,
    is_arrived boolean,
    primary key(order_id) not enforced
) with (
    'connector' = 'elasticsearch-7',
    'hosts' = 'http://localhost:9200',
    'index' = 'enriched_orders'
);


-- Use Flink SQL to join the order table with the products and shipments table to enrich orders and write to the Elasticsearch.
insert into enriched_orders
select o.*,
    p.name,
    p.description,
    s.shipment_id,
    s.origin,
    s.destination,
    s.is_arrived
from orders as o
    left join products as p on o.product_id = p.id
    left join shipments as s on o.order_id = s.order_id;