#! -*- coding:utf-8 -*-

"""4 methods to create a StreamTableEnvironment
(1)
# create with StreamExecutionEnvironment.
>>> env = StreamExecutionEnvironment.get_execution_environment()
>>> table_env = StreamTableEnvironment.create(env)
(2)
# create with StreamExecutionEnvironment and TableConfig.
>>> table_config = TableConfig()
>>> table_config.set_null_check(False)
>>> table_env = StreamTableEnvironment.create(env, table_config)
(3)
# create with StreamExecutionEnvironment and EnvironmentSettings.
>>> environment_settings = EnvironmentSettings.in_streaming_mode()
>>> table_env = StreamTableEnvironment.create(env, environment_settings=environment_settings)
(4)
# create with EnvironmentSettings.
>>> table_env = StreamTableEnvironment.create(environment_settings=environment_settings)
"""

from pyflink.table import EnvironmentSettings, StreamTableEnvironment,DataTypes
from pyflink.table.catalog import HiveCatalog
"""create env"""
env_settings = EnvironmentSettings.new_instance().in_streaming_mode().use_blink_planner().build()
t_env = StreamTableEnvironment.create(environment_settings=env_settings)

"""confg env"""
t_env.get_config().get_configuration().set_string("parallelism.default","4")

"""create source这里建议看源码
"""

"""method I"""
tab = t_env.from_elements(
    [("hello",1),("world",2),("flink",3)],
    DataTypes.ROW([DataTypes.FIELD("name",DataTypes.STRING()), 
    DataTypes.FIELD("value",DataTypes.INT())])
)

"""method II"""
"""需要用t_env注册一个path"""
t_env.execute_sql(
    """create table my_source (
        name varchar,
        value  int
    ) with (
        "connector" = "datagen",
        "number-of-rows" = "10"
    )
    """
)
tab =  t_env.from_path("my_source")


"""method III
catalog 不常用 事先将表的meta注册到了catalog中，不需要在作业里重复一次
"""
hi_catalog = HiveCatalog()

