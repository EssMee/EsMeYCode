#!/usr/bin/env python
# -*- encoding: utf-8 -*-
import logging
import sys
from pyflink.common import Types
from pyflink.datastream import StreamExecutionEnvironment
from pyflink.table import DataTypes, TableDescriptor, Schema, StreamTableEnvironment
from pyflink.table.udf import udf
from pyflink.table import Table

def mix_ds_tbl():
    env = StreamExecutionEnvironment.get_execution_environment()
    t_env = StreamTableEnvironment.create(env)

    # source
    t_env.create_temporary_table(
        "source",
        TableDescriptor.for_connector("datagen").schema(
            Schema.new_builder()
            .column("id", data_type=DataTypes.BIGINT())
            .column("data", data_type=DataTypes.STRING())
            .build())
        .option("number-of-rows", "10")
        .build())

    # sink
    t_env.create_temporary_table("print", TableDescriptor.for_connector("print").schema(
        Schema.new_builder().column("a", data_type=DataTypes.BIGINT()).build()).build())

    @udf(result_type=DataTypes.BIGINT())w
    def length(data):
        return len(data)

    # preform table api operation
    table = t_env.from_path("source")
    table = table.select(table.id, length(table.data))
    # convert table to datastream and preform datastream api
    """here i recommend read the code"""
    ds = t_env.to_data_stream(table)
    ds = ds.map(lambda i: i[0] + i[1], output_type=Types.LONG())
    ds.print("transfered_ds")

    # convert datastream to table and preform table api
    table = t_env.from_data_stream(ds, fields_or_schema=Schema.new_builder().column("f0", DataTypes.BIGINT()).build())
    
    # execute 
    table.execute_insert("sink").wait()


'''
@file   :   mixing_ds_table.py
@desc   :   None
'''

if __name__ == '__main__':
    logging.basicConfig(
        stream=sys.stdout,
        level=logging.INFO,
        format="%(asctime)s %(filename)s : %(levelname)s %(message)s",
        datefmt="%Y-%m-%d %A %H:%M:%S"
    )

    mix_ds_tbl()

