create table catalog
(
    id           bigint auto_increment comment '主键'
        primary key,
    catalog_item varchar(256)                       not null comment '类别名',
    user_id      bigint                             null comment '上传标签的用户id',
    create_time  datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time  datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete    tinyint  default 0                 not null comment '逻辑是否删除'
)
    comment '类别表';

create table tag
(
    id              bigint auto_increment comment '主键'
        primary key,
    tag_name        varchar(256)                       not null comment '标签名',
    user_id         bigint                             null comment '上传标签的用户id',
    catalog_item_id bigint                             null comment '分类id',
    create_time     datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time     datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete       tinyint  default 0                 not null comment '逻辑是否删除'
)
    comment '标签表';

create table team
(
    id            bigint auto_increment comment '主键'
        primary key,
    name          varchar(256)                       not null comment '队伍名称',
    description   varchar(256)                       null comment '描述',
    max_num       int                                not null comment '最大人数',
    expire_time   datetime                           null comment '过期时间',
    user_id       bigint                             not null comment '创建人id',
    team_status   int      default 0                 not null comment '队伍状态：0公开；1私密；2加密，默认0',
    team_password varchar(256)                       null comment '队伍密码',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint  default 0                 not null comment '是否删除，逻辑删除； 0 表示存在'
)
    comment '队伍表';

create table user
(
    id            bigint auto_increment comment '主键'
        primary key,
    user_account  varchar(256)                       null comment '登录账号',
    user_password varchar(256)                       not null comment '密码',
    tags          json                               null comment '用户具有的标签',
    profile       varchar(512)                       null comment '用户简介',
    username      varchar(256)                       null comment '用户昵称',
    avatar_url    varchar(256)                       null comment '头像',
    gender        tinyint                            null comment '性别，0 表示男性；1 表示女性',
    phone         varchar(30)                        null comment '电话',
    email         varchar(60)                        null comment '邮箱',
    user_status   int                                null comment '账号状态，0 表示正常',
    user_role     int      default 0                 not null comment '0 默认用户 1 管理员',
    create_time   datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time   datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete     tinyint  default 0                 not null comment '逻辑是否删除'
)
    comment '用户表';

create table user_team
(
    id          bigint auto_increment comment '主键'
        primary key,
    user_id     bigint                             not null comment '用户id',
    team_id     bigint                             not null comment '队伍id',
    join_time   datetime                           null comment '加入时间',
    create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    is_delete   tinyint  default 0                 not null comment '逻辑删除； 0 表示存在'
);