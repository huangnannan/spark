-- 创建 clickhouse 分布式表, _local 是各个节点的本地表， _all 类似于视图表

CREATE TABLE test_cluster.person_local on cluster perftest_3shards_1replicas
(
    `name` String COMMENT '姓名',
    `gen` String COMMENT '性别',
    `age` Int16 COMMENT '年龄'
)
ENGINE = ReplicatedMergeTree('/clickhouse/tables/{replica}/person_local', '{replica}')
ORDER BY (gen,name)
SETTINGS index_granularity = 8192;

CREATE TABLE IF NOT EXISTS test_cluster.person_all ON CLUSTER perftest_3shards_1replicas AS test_cluster.person_local
ENGINE = Distributed(perftest_3shards_1replicas, test_cluster, person_local, rand());


drop table test_cluster.person_local ON CLUSTER perftest_3shards_1replicas;
drop table test_cluster.person_all ON CLUSTER perftest_3shards_1replicas;
