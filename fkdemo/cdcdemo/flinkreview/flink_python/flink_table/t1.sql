create table my_window_table(
    item STRING,
    price DOUBLE,
    proctime as PROCTIME()
) with (
    'connector' = 'socket',
    'hostname' = '127.0.0.1',
    'port' = '9999',
    'format' = 'csv'
);

create view my_window_view as
select tumble_start(proctime, interval '10' minutes) as window_start,
    tumble_end(proctime, interval '10' minutes) as window_end,
    tumble_proctime(proctime, interval '10' minutes) as window_proctime,
    item,
    max(price) as max_price
from my_window_table
group by tumble(proctime, interval '10' minutes),item;