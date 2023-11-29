create table area
(
    id         int          not null comment 'ID'
        primary key,
    name       varchar(50)  not null comment '栏目名',
    parent_id  int          null comment '父栏目',
    short_name varchar(50)  null,
    level      int          not null comment '1.省 2.市 3.区 4.镇',
    sort       int unsigned null comment '排序'
)
    comment '省市区表' charset = utf8mb4;

create index parent_id
    on area (parent_id);

create table banner
(
    id          bigint auto_increment
        primary key,
    company_id  bigint                               null comment '公司id',
    img_url     varchar(255)                         not null comment '图片地址',
    jump_url    varchar(255)                         null comment '跳转地址',
    position    int                                  not null comment '位置：1 首页',
    sort        int      default 1                   not null comment '排序：越小越靠前',
    status      int      default 1                   not null comment '0 隐藏，1 显示',
    del         char     default '0'                 not null comment '0 未删除，1 已删除',
    remark      varchar(500)                         null comment '备注',
    create_by   bigint                               null comment '创建人id',
    update_by   bigint                               null comment '更新人id',
    create_time datetime default current_timestamp() not null comment '创建时间',
    update_time datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '广告位' charset = utf8mb4;

create index company_id
    on banner (company_id);

create table base_order
(
    id             bigint unsigned auto_increment comment '主键'
        primary key,
    order_code     varchar(200)                                 not null comment '流水号',
    type           int(11) unsigned                             not null comment '订单类型:1余额充值,2套餐购买(手动买月租/手动买加量包),3月租自动续费',
    company_id     bigint unsigned                              null comment '公司id',
    pig_farm_id    bigint unsigned                              null comment '猪场id',
    user_id        bigint unsigned                              null comment '用户id',
    total          int(11) unsigned                             not null comment '合计金额单位:分',
    amount         int(11) unsigned                             not null comment '充值金额单位:分',
    payment_type   varchar(100)                                 not null comment '支付方式:1对公转账,2支付宝,3微信',
    pay_code       varchar(200)                                 null comment '订单号(支付宝,微信的单号)',
    order_status   int(11) unsigned                             not null comment '用户支付状态:1未付款,2已付款',
    we_status      int(11) unsigned                             not null comment '我方系统处理状态:1待处理,2已完成',
    other_percent  int(11) unsigned                             null comment '手续费比例,单位千分之',
    other          int(11) unsigned default 0                   not null comment '手续费单位:分',
    balance_before int(11) unsigned                             not null comment '操作前账户余额 单位:分',
    balance_after  int(11) unsigned                             not null comment '操作后账户余额 单位:分',
    create_time    datetime         default current_timestamp() not null comment '创建时间',
    update_time    datetime                                     null on update current_timestamp() comment '修改时间',
    constraint unq_code
        unique (order_code),
    constraint unq_pay_code
        unique (pay_code)
)
    comment '订单表';

create index idx_create_time
    on base_order (create_time);

create index idx_order_code
    on base_order (order_code);

create index idx_order_status
    on base_order (order_status);

create index idx_pay_code
    on base_order (pay_code);

create index idx_pig_farm_id
    on base_order (pig_farm_id);

create index idx_type
    on base_order (type);

create index idx_user_id
    on base_order (user_id);

create table blend_feed
(
    id                 bigint auto_increment
        primary key,
    farm_id            bigint null,
    first_amount       int    null comment '首次饲喂量',
    feed_again         int    null comment '是否二次饲喂',
    feed_again_time    time   null comment '二次饲喂时间',
    feed_again_amount  int    null comment '二次饲喂量',
    feed_again_task_id bigint null comment '二次下料任务',
    material_line_id   bigint null comment '约定，混养栏在同一条料线下'
)
    comment '混养配置';

create table blend_feed_column
(
    id            bigint auto_increment
        primary key,
    blend_feed_id bigint      null,
    farm_id       bigint      null,
    house_id      bigint      null,
    col_id        bigint      null,
    position      varchar(20) null
)
    comment '混养栏位';

create table commonly_used_menu
(
    id          bigint auto_increment
        primary key,
    name        varchar(20)                          null comment '菜单名称',
    icon        varchar(20)                          null comment '菜单图标',
    type        varchar(20)                          null comment '菜单类型',
    action      varchar(20)                          null comment '菜单动作',
    del         char     default '0'                 not null comment '0未删除，1已删除',
    remark      varchar(500)                         null comment '备注',
    create_by   bigint                               null comment '创建人',
    update_by   bigint                               null comment '修改人',
    create_time datetime default current_timestamp() not null comment '创建时间',
    update_time datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '常用菜单' charset = utf8mb4;

create table commonly_used_menu_config
(
    id                    bigint auto_increment
        primary key,
    user_id               bigint                               not null comment '用户ID',
    commonly_used_menu_id bigint                               not null comment '菜单ID',
    sort                  int                                  null comment '排序',
    del                   char     default '0'                 not null comment '0未删除，1已删除',
    remark                varchar(500)                         null comment '备注',
    create_by             bigint                               null comment '创建人',
    update_by             bigint                               null comment '修改人',
    create_time           datetime default current_timestamp() not null comment '创建时间',
    update_time           datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '常用菜单配置' charset = utf8mb4;

create table company
(
    id           bigint auto_increment comment '主键id'
        primary key,
    name         varchar(255) collate utf8mb4_unicode_ci                     not null comment '公司名',
    contact_name varchar(255) collate utf8mb4_unicode_ci                     null comment '联系人',
    phone        varchar(11) collate utf8mb4_unicode_ci                      not null comment '手机号',
    email        varchar(255) collate utf8mb4_unicode_ci                     null comment '邮箱',
    province_id  int                                                         null comment '省id',
    city_id      int                                                         null comment '市id',
    area_id      int                                                         null comment '区id',
    address      varchar(255) collate utf8mb4_unicode_ci                     null comment '详细地址',
    enabled      char collate utf8mb4_unicode_ci default '1'                 not null comment '是否可用：0-不可用，1-可用',
    del          char collate utf8mb4_unicode_ci default '0'                 not null comment '0-未删除，1-已删除',
    remark       varchar(500) collate utf8mb4_unicode_ci                     null comment '备注',
    create_by    bigint                                                      not null comment '创建人',
    update_by    bigint                                                      null comment '更新人',
    create_time  datetime                        default current_timestamp() null comment '创建时间',
    update_time  datetime                                                    null on update current_timestamp() comment '更新时间'
)
    comment '公司表' charset = utf8mb4;

create table device_quality_check
(
    id              bigint unsigned auto_increment comment '主键'
        primary key,
    tower_id        bigint                               not null comment '料塔id',
    device_num      varchar(255)                         not null comment '设备编号',
    check_count     int                                  not null comment '检测次数(第几次)',
    start_time      datetime                             null comment '检测开始时间',
    end_time        datetime                             null comment '检测结束时间',
    volume          bigint                               null comment '体积(单位 立方厘米)',
    standard_volume bigint                               not null comment '参考标准(单位 立方厘米)',
    right_percent   int                                  null comment '准确率% 单位(保留两位小数后*100的整数)',
    pass            int      default -1                  not null comment '是否通过 -1未开始 0不通过  1通过 2无效测量',
    log_id          bigint                               null comment '测量日志的id',
    sn              varchar(255)                         null comment '序列号',
    batch_num       varchar(255)                         null comment '批次号',
    company_id      bigint                               not null comment '公司',
    pig_farm_id     bigint                               not null comment '猪场',
    create_time     datetime default current_timestamp() not null comment '创建时间',
    update_time     datetime                             null on update current_timestamp() comment '修改时间',
    handle          int      default 0                   not null comment '结果处理(0自动,1手动)',
    remark          varchar(255)                         null comment '备注(通过不通过的原因)',
    checker_id      bigint                               null comment '质检员id',
    checker_name    varchar(255)                         null comment '质检员名字'
)
    comment '质检记录表';

create table farm_feeding_strategy
(
    id             bigint auto_increment
        primary key,
    stage_begin    int    null comment '阶段开始',
    stage_end      int    null comment '阶段结束',
    back_fat       int    null comment '偏瘦',
    firstborn      int    null comment '头胎',
    feeding_amount int    null comment '饲喂量（克）',
    record_id      bigint not null
)
    comment '猪场下料任务' charset = utf8mb4;

create table farm_feeding_strategy_allow
(
    id          bigint auto_increment
        primary key,
    employ_id   bigint      null comment '员工ID',
    employ_name varchar(10) null comment '员工名称'
)
    comment '饲喂策略可操作人员' charset = utf8mb4;

create table farm_feeding_strategy_record
(
    id           bigint auto_increment
        primary key,
    company_id   bigint                               not null comment '公司id',
    pig_farm_id  bigint                               not null comment '猪场id',
    pig_type_id  bigint                               null comment '猪只类型',
    pig_type     varchar(20)                          null comment '猪只类型名称',
    name         varchar(50)                          null comment '策略名称',
    operator_id  bigint                               not null comment '操作人id',
    file_name    varchar(50)                          null comment '文件名',
    storage_path varchar(50)                          null comment '文件存储路径',
    del          char     default '0'                 not null comment '0 未删除，1 删除',
    remark       varchar(50)                          null comment '备注',
    create_by    bigint                               null comment '创建人id',
    update_by    bigint                               null comment '修改人id',
    create_time  datetime default current_timestamp() not null comment '创建时间',
    update_time  datetime                             null on update current_timestamp() comment '修改时间'
)
    comment '饲喂策略记录' charset = utf8mb4;

create index company_id
    on farm_feeding_strategy_record (company_id, pig_farm_id);

create table farm_feeding_strategy_record_detail
(
    id        bigint auto_increment
        primary key,
    stage     varchar(30) null comment '阶段',
    feed      varchar(30) null comment '饲料',
    one       varchar(10) null,
    thin      varchar(20) null comment '偏瘦',
    suitable  varchar(20) null comment '适宜',
    fat       varchar(20) null comment '偏胖',
    five      varchar(10) null,
    record_id bigint      not null comment '策略主记录'
)
    comment '饲喂策略记录明细' charset = utf8mb4;

create table feed_tower
(
    id              bigint auto_increment
        primary key,
    company_id      bigint            null comment '公司',
    pig_farm_id     bigint            null comment '猪场ID',
    name            varchar(50)       null comment '料塔名称',
    device_no       varchar(50)       null comment '设备编号',
    enable          int     default 0 not null comment '是否启用',
    capacity        bigint  default 0 null comment '料塔容量（g）',
    warning         int               null comment '警戒百分比',
    feed_type       varchar(20)       null comment '饲料品类',
    density         bigint  default 0 null comment '密度（g/m³）',
    init            tinyint default 0 not null comment '是否完成空腔容积校正，默认0：未校正',
    init_volume     bigint            null comment '初始体积：容量/密度计算，单位cm³',
    residual_volume bigint  default 0 null comment '余料体积（cm³）',
    residual_weight bigint  default 0 null comment '余料重量(g)',
    residual_date   datetime          null comment '剩余重量时间',
    houses          varchar(50)       null comment '料塔对应的栋舍',
    data_status     int     default 0 not null comment '数据是否配置完成，0：未完成、1：已完成',
    iccid           varchar(30)       null,
    init_time       datetime          null comment '最近一次的校准时间',
    del             int     default 0 null,
    neck_height     int     default 0 not null comment '料塔颈高 cm',
    install_height  int     default 0 not null comment '安装高度 cm',
    up_radius       int     default 0 not null comment '进料口半径 cm',
    up_slope_len    int     default 0 not null comment '上锥斜面长度 cm',
    in_radius       int     default 0 not null comment '中半径 cm',
    in_height       int     default 0 not null comment '中高度 cm',
    in_floor        int     default 0 not null comment '中部到地面的距离',
    down_floor      int     default 0 not null comment '底部到地面的距离',
    down_girth      int     default 0 not null comment '出料口周长 cm'
)
    comment '料塔' charset = utf8mb4;

create table feed_tower_add
(
    id                bigint unsigned auto_increment comment '主键'
        primary key,
    car_id            bigint                               null comment '车辆id',
    tower_id          bigint                               not null comment '料塔id',
    current_state     int                                  not null comment '当前状态：0未开始 1加料前测量中 2加料前测量结束 3等待开盖 4已开盖 5我已准备好加料 6加料中 7加料结束 8等待关盖 9已关盖 10关盖测量中 11关盖测量结束 12加料完成  13打料前测量失败 14 打料后测量失败 15中止',
    add_before        bigint                               null comment '加料前余料(单位:g)',
    add_before_volume bigint                               null comment '加料前余料体积(单位:立方厘米)',
    add_before_log_id bigint                               null comment '加料前余料测量日志log_id',
    add_start_time    datetime                             null comment '加料开始时间',
    add_end_time      datetime                             null comment '加料结束时间',
    may_add_end_time  datetime                             null comment '预计加料结束时间',
    may_left_time     bigint                               null comment '预计加料结束倒计时(单位:秒)',
    add_after         bigint                               null comment '加料后余料(单位:g)',
    add_after_volume  bigint                               null comment '加料后余料体积(单位:立方厘米)',
    add_after_log_id  bigint                               null comment '加料后余料测量日志log_id',
    use_time          bigint                               null comment '加料耗时(单位秒)',
    add_total         bigint                               null comment '打料量(单位g)',
    open_time         datetime                             null comment '开盖时间',
    close_time        datetime                             null comment '关盖时间',
    remark            varchar(200)                         null comment '备注(中止流程的原因)',
    stop_status       int                                  null comment '备注(中止流程前的状态)',
    create_time       datetime default current_timestamp() not null comment '创建时间',
    update_time       datetime                             null on update current_timestamp() comment '修改时间'
)
    comment '打料表';

create table feed_tower_apply
(
    id           bigint unsigned auto_increment comment '主键'
        primary key,
    apply_code   varchar(200)                         not null comment '报料单号',
    company_id   bigint unsigned                      null comment '公司id',
    pig_farm_id  bigint unsigned                      null comment '猪场id',
    tower_id     bigint unsigned                      null comment '料塔id',
    user_id      bigint unsigned                      null comment '用户id',
    feed_type    varchar(20)                          null comment '饲料品类',
    total        bigint unsigned                      not null comment '报料量,单位:g',
    apply_status int(11) unsigned                     not null comment '状态',
    create_time  datetime default current_timestamp() not null comment '创建时间',
    update_time  datetime                             null on update current_timestamp() comment '修改时间',
    constraint unq_code
        unique (apply_code)
)
    comment '报料表';

create index idx_pig_farm_id
    on feed_tower_apply (pig_farm_id);

create index idx_tower_id
    on feed_tower_apply (tower_id);

create table feed_tower_car
(
    id          bigint unsigned auto_increment comment '主键'
        primary key,
    car_code    varchar(200)                         not null comment '车牌号',
    driver_name varchar(200)                         not null comment '司机名',
    id_card     varchar(200)                         not null comment '司机身份证',
    mobile      varchar(200)                         not null comment '手机',
    start_time  datetime                             null comment '测速开始时间',
    end_time    datetime                             null comment '测速结束时间',
    speed       bigint                               null comment '流速(单位克每秒)',
    create_time datetime default current_timestamp() not null comment '创建时间',
    update_time datetime                             null on update current_timestamp() comment '修改时间',
    constraint unq_car_code
        unique (car_code)
)
    comment '车辆表';

create table feed_tower_device
(
    id              bigint auto_increment
        primary key,
    company_id      bigint           null comment '公司',
    pig_farm_id     bigint default 0 not null comment '猪场ID',
    tower_id        bigint default 0 not null comment '绑定的料塔',
    name            varchar(50)      null comment '设备名称',
    sn              varchar(256)     null comment '序列号，入库的时候自动生成',
    sn_time         datetime         null comment '序列号生成时间',
    device_no       varchar(30)      null comment '设备编号',
    standard_angle  int(3) default 0 not null comment '标准角度',
    version         varchar(10)      null comment '产品版本',
    version_code    varchar(50)      null comment '固件版本号',
    modbus_id       int              null comment '设备ModbusID号',
    wifi_account    varchar(50)      null comment '设备使用wifi时的账号',
    wifi_pwd        varchar(50)      null comment '设备使用wifi时的密码',
    warranty        int              null comment '质保月数',
    warranty_begin  date             null comment '设备激活日期，质保开始日期',
    warranty_period date             null comment '质保到期日期',
    del             int    default 0 null,
    create_time     datetime         null comment '入库时间',
    constraint feed_tower_device_device_no_uindex
        unique (device_no),
    constraint feed_tower_device_sn_uindex
        unique (sn)
);

create table feed_tower_employ
(
    id        bigint auto_increment
        primary key,
    tower_id  bigint null,
    employ_id bigint null
)
    comment '人员访问权限';

create table feed_tower_feed_type
(
    id          bigint auto_increment
        primary key,
    company_id  bigint      null comment '公司',
    pig_farm_id bigint      null comment '猪场ID',
    name        varchar(50) null comment '品类名称',
    density     bigint      null comment '密度（g/m³）',
    constraint sph_feed_tower_feed_type_farm_id_name_density_uindex
        unique (pig_farm_id, name, density)
);

create table feed_tower_log
(
    id             bigint auto_increment
        primary key,
    company_id     bigint                       null comment '公司',
    pig_farm_id    bigint                       null comment '猪场ID',
    tower_id       bigint                       null comment '设备ID',
    tower_capacity bigint                       null comment '料塔容量（g）',
    tower_density  bigint                       null comment '密度（g/m³）',
    tower_volume   bigint                       null comment '料塔容积',
    device_no      varchar(50)                  null comment '设备编号',
    task_no        varchar(20)                  null comment '测量任务标识',
    start_mode     varchar(20)                  null comment '启动方式，0：自动，1：手动',
    lid_status     int    default 0             null comment '料盖是否异常，开启或关闭',
    temperature    varchar(10)                  null comment '温度',
    humidity       varchar(10)                  null comment '湿度',
    network        varchar(5)                   null comment '网络状态：离线、弱、中、强',
    status         varchar(20)                  null comment 'begin、running、completed',
    volume_yang    bigint                       null comment '杨总算法计算的体积',
    volume_matlab  bigint                       null comment 'Matlab算法',
    volume_third   bigint                       null comment '淘宝算法',
    volume_base    bigint                       null comment '自研算法空腔体积',
    volume         bigint                       null comment '余料体积(cm³)',
    weight         bigint                       null comment '余料重量(g)',
    variation      bigint default 0             not null comment '变化量(g)',
    modified       int(1) default 0             not null comment '加料：1，用料：-1',
    data           longtext collate utf8mb4_bin null comment '监测一次的扫描数据（角度、距离）',
    create_time    datetime                     null comment '开始时间',
    completed_time datetime                     null comment '结束时间',
    remark         varchar(50)                  null comment '备注'
)
    comment '料塔日志' charset = utf8mb4;

create table feed_tower_log_slave
(
    id          bigint auto_increment
        primary key,
    log_id      bigint                       null,
    device_no   varchar(50)                  null,
    task_no     varchar(20)                  null,
    data        longtext collate utf8mb4_bin null comment '测量数据',
    create_time datetime                     null,
    constraint data
        check (json_valid(`data`))
)
    comment '料塔日志从表';

create table feed_tower_msg
(
    id             bigint auto_increment
        primary key,
    company_id     bigint                           null comment '公司',
    pig_farm_id    bigint                           null comment '猪场ID',
    tower_id       bigint                           null comment '设备ID',
    device_no      varchar(50)                      null comment '设备编号',
    topic          varchar(50)                      null comment '接口',
    msg            varchar(2000)                    null comment '完整的消息',
    content_len    smallint                         null comment '内容长度',
    param          varchar(500) collate utf8mb4_bin null comment '参数',
    type           varchar(500) collate utf8mb4_bin null comment 'Send、Received',
    version        varchar(4)                       null comment '协议版本',
    correct        varchar(10)                      null comment '是否正确有效 CRC 验证',
    task_id        varchar(20)                      null comment '检测任务标识',
    data_time      varchar(20)                      null comment '数据时间',
    steering_angle int                              null comment '舵机角度',
    amount         int                              null comment '采集点数',
    content        varchar(2000)                    null comment '完整的消息',
    crc            varchar(4)                       null comment '验证码',
    end_flag       tinyint(1)                       null comment '接收数据是否结束',
    create_time    datetime                         null
)
    comment '料塔消息' charset = utf8mb4;

create table feed_tower_qrtz
(
    id           bigint auto_increment
        primary key,
    tower_id     bigint        null,
    device_no    varchar(50)   null,
    trigger_time varchar(5)    null comment '触发时间',
    job_name     varchar(50)   null,
    job_group    varchar(50)   null,
    job_enable   int default 0 null
);

create table feedback
(
    id          bigint auto_increment
        primary key,
    company_id  bigint                               null,
    pig_farm_id bigint                               null,
    content     varchar(1024)                        not null,
    img         varchar(255)                         null,
    del         char     default '0'                 null,
    remark      varchar(255)                         null,
    create_by   bigint                               null,
    update_by   bigint                               null,
    create_time datetime default current_timestamp() null,
    update_time datetime                             null on update current_timestamp()
)
    charset = utf8mb4;

create table feeder
(
    id               bigint auto_increment
        primary key,
    company_id       bigint                  null comment '公司id',
    pig_farm_id      bigint                  null comment '猪场id',
    pig_house_id     bigint                  null comment '栋舍id',
    client_id        bigint                  not null comment '主机编号',
    material_line_id bigint                  null comment '料线ID',
    feeder_code      int(3)                  null comment '饲喂器编号',
    type             varchar(20)             null comment '饲喂器类型',
    status           varchar(20) default '0' null comment '饲喂器状态',
    error            varchar(20) default '0' null comment '饲喂器信息',
    sensor_weight    int                     null comment '传感器重量',
    date             datetime                null comment '最新记录时间'
)
    comment '饲喂器' charset = utf8mb4;

create index company_id
    on feeder (company_id, pig_farm_id);

create table feeder_qrtz
(
    id                 bigint auto_increment
        primary key,
    trigger_time       varchar(5)    null comment '触发时间',
    farm_id            bigint        null,
    house_id           bigint        null,
    house_name         varchar(20)   null comment '栋舍名称',
    house_type_id      int           null,
    house_type         varchar(20)   null comment '栋舍类型',
    material_line_id   bigint        null,
    material_line_name varchar(20)   null comment '料线名称',
    blend_feed_id      bigint        null comment '二次饲喂',
    job_name           varchar(20)   null comment '任务名称',
    job_group          varchar(20)   null comment '任务组',
    job_enable         int default 0 null comment '任务是否启用',
    constraint feeder_qrtz_pk
        unique (material_line_id, house_id, trigger_time, job_group)
);

create table financial_data
(
    id           bigint auto_increment
        primary key,
    company_id   bigint                               not null comment '公司id',
    pig_farm_id  bigint                               not null comment '猪场id',
    number       int                                  not null comment '数量',
    unit_price   decimal(10, 2)                       not null comment '单价',
    total_price  decimal(10, 2)                       not null comment '总价',
    data_type_id bigint                               not null comment '关联财务数据类型id',
    income       int                                  not null comment '收支类型1,收入，2支出',
    status       int      default 0                   not null comment '状态，1已导出，0未导出',
    del          char     default '0'                 not null comment '0未删除，1已删除',
    remark       varchar(500)                         null comment '备注',
    create_by    bigint                               null comment '创建人',
    update_by    bigint                               null comment '修改人',
    create_time  datetime default current_timestamp() not null comment '创建时间',
    update_time  datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '财务数据' charset = utf8mb4;

create index company_id
    on financial_data (company_id, pig_farm_id);

create table financial_data_type
(
    id          bigint auto_increment
        primary key,
    company_id  bigint                               not null comment '公司id',
    name        varchar(50)                          not null comment '名称',
    suject_code varchar(50)                          not null comment '科目代码',
    suject_name varchar(50)                          not null comment '科目名称',
    data_type   int                                  not null comment '数据类型(1猪只销售,2猪只购买,3其它)',
    loan_type   varchar(50)                          not null comment '借贷类型1，借，2货',
    del         char     default '0'                 not null comment '0未删除，1已删除',
    remark      varchar(500)                         null comment '备注',
    create_by   bigint                               null comment '创建人',
    update_by   bigint                               null comment '修改人',
    create_time datetime default current_timestamp() not null comment '创建时间',
    update_time datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '财务数据类型' charset = utf8mb4;

create index company_id
    on financial_data_type (company_id);

create table firmware_upgrade_config
(
    id            bigint auto_increment
        primary key,
    category      varchar(20)   null comment '固件类别：料塔设备、饲喂器主机、饲喂器从机',
    upgrade_time  datetime      not null comment '定时升级时间',
    upgrade_limit int           not null comment '同时升级设备的上限',
    frame_length  int           null comment '发送数据包每帧的长度',
    version_id    bigint        null comment '固件',
    version       varchar(50)   null comment '固件版本号',
    version_file  varchar(50)   null comment '版本文件存储路径',
    enable        int default 0 not null comment '配置是否生效'
);

create table firmware_upgrade_detail
(
    id               bigint auto_increment
        primary key,
    device_no        varchar(30)                          null,
    upgrade_time     datetime default current_timestamp() not null comment '升级时间',
    complete_time    datetime                             null comment '完成时间',
    upgrade_schedule varchar(20)                          null comment '升级进度',
    remark           varchar(30)                          null comment '备注',
    report_id        bigint                               null,
    seq              int      default 0                   not null comment '报告中的显示排序，异常（2）、完成（1）、进行中（0）'
)
    comment '升级报告明细';

create table firmware_upgrade_report
(
    id                bigint auto_increment
        primary key,
    firmware_category varchar(20)   null comment '升级固件类别',
    firmware_version  varchar(50)   null comment '升级固件版本',
    upgrade_time      datetime      null comment '升级时间',
    upgrade_count     int           null comment '升级设备数量',
    upgrade_success   int           null comment '成功数量',
    upgrade_fail      int           null comment '失败数量',
    upgrade_status    int default 0 null comment '升级任务是否完成，0：未完成，1：完成'
)
    comment '升级报告';

create table firmware_version
(
    id          bigint auto_increment
        primary key,
    category    varchar(20)                          null comment '固件类型',
    version     varchar(50)                          null comment '固件版本号',
    file_name   varchar(50)                          null comment '文件名称',
    save_path   varchar(50)                          null comment '存储目录',
    del         char     default '0'                 not null comment '0-未删除，1-已删除',
    remark      varchar(500)                         null comment '备注',
    create_by   bigint                               not null comment '创建人',
    update_by   bigint                               null comment '更新人',
    create_time datetime default current_timestamp() null comment '创建时间',
    update_time datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '固件版本管理';

create table gateway
(
    id               bigint auto_increment
        primary key,
    company_id       bigint                               not null comment '公司id',
    pig_farm_id      bigint                               not null comment '猪场id',
    pig_house_id     bigint                               not null comment '栋舍id',
    client_id        bigint                               not null comment '主机编号',
    material_line_id bigint                               not null comment '料线ID',
    online_time      datetime                             null comment '最近的在线时间',
    del              char     default '0'                 not null comment '0未删除，1已删除',
    remark           varchar(500)                         null comment '备注',
    create_by        bigint                               null comment '创建人',
    update_by        bigint                               null comment '修改人',
    create_time      datetime default current_timestamp() not null comment '创建时间',
    update_time      datetime                             null on update current_timestamp() comment '更新时间',
    constraint gateway_client_id_uindex
        unique (client_id)
)
    comment '主机' charset = utf8mb4;

create index company_id
    on gateway (company_id, pig_farm_id);

create table haiwei_config
(
    id          bigint auto_increment
        primary key,
    farm_id     bigint       null comment '所属猪场',
    house_id    bigint       null,
    url         varchar(100) null comment '集成链接',
    device      varchar(100) null comment '集成链接',
    account     varchar(20)  null comment '账号',
    privateKey  varchar(100) null comment '项目私钥',
    machineCode varchar(100) null comment '机器码',
    platform    int          null comment '平台代码'
)
    comment '海为云集成配置';

create table index_menu_type
(
    id             bigint auto_increment comment '首页菜单类型ID'
        primary key,
    menu_type_name varchar(30)                          not null comment '首页菜单类型名称',
    menu_type_key  varchar(100)                         null comment '首页菜单类型字符串',
    menu_type_sort int      default 1                   not null comment '显示顺序',
    status         int      default 1                   not null comment '首页菜单类型状态（1 正常  0 停用）',
    del            char     default '0'                 null comment '删除标志（0 正常 1 删除）',
    remark         varchar(500)                         null comment '备注',
    create_by      bigint                               null comment '创建者',
    update_by      bigint                               null comment '更新者',
    create_time    datetime default current_timestamp() not null comment '创建时间',
    update_time    datetime                             null on update current_timestamp() comment '更新时间',
    app            varchar(20)                          not null comment 'app'
)
    comment '角色信息表' charset = utf8mb4;

create index company_id
    on index_menu_type (menu_type_key);

create table index_menu_user_type
(
    user_id    bigint            not null comment '用户ID',
    type_id    bigint            not null comment '首页菜单类型ID',
    is_default tinyint default 0 not null comment '默认模块',
    primary key (user_id, type_id)
)
    comment '用户和角色关联表' charset = utf8mb4;

create table jpush_message
(
    id             bigint auto_increment
        primary key,
    company_id     bigint                               not null comment '公司id',
    pig_farm_id    bigint                               null comment '猪场id',
    user_id        bigint                               null comment '用户id',
    title          varchar(255)                         not null comment '标题',
    body           varchar(512)                         not null comment '内容',
    ext_parameters varchar(512)                         null comment '附加内容',
    status         int                                  not null comment '状态：0 未读，1 已读',
    type           varchar(20)                          not null comment '业务类型：',
    sub_type       varchar(128)                         null comment '子类型',
    push_type      varchar(20)                          not null comment '推送类型：MESSAGE 消息，NOTICE 通知',
    del            char                                 not null comment '0 未删除，1 已删除',
    remark         varchar(500)                         null comment '备注',
    create_by      bigint                               null comment '创建人id',
    update_by      bigint                               null comment '更新人id',
    create_time    datetime default current_timestamp() not null comment '创建时间',
    update_time    datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '消息推送表' charset = utf8mb4;

create index user_id
    on jpush_message (company_id, pig_farm_id, user_id);

create table manual_feeding_record
(
    id              bigint auto_increment
        primary key,
    company_id      bigint      not null comment '公司id',
    pig_farm_id     bigint      not null comment '猪场id',
    house_id        bigint      null comment '栋舍ID',
    house_name      varchar(20) null comment '栋舍名称',
    house_column_id bigint      null comment '栏位ID',
    position        varchar(20) null comment '栏位位置',
    pig_id          bigint      null comment '栏位猪只',
    amount          int         null comment '下料重量（g）',
    feed_time       datetime    null comment '下料时间',
    operator        bigint      null comment '操作人',
    batch           varchar(50) null comment '批次',
    client_id       bigint      null,
    feeder_code     int         null,
    feed_status     varchar(4)  null comment '饲喂状态'
)
    comment '手动下料明细' charset = utf8mb4;

create index company_id
    on manual_feeding_record (company_id, pig_farm_id);

create table material_line
(
    id             bigint auto_increment
        primary key,
    company_id     bigint                               not null comment '公司id',
    pig_farm_id    bigint                               not null comment '猪场id',
    pig_house_id   bigint                               not null comment '栋舍id',
    pig_house_name varchar(20)                          null comment '栋舍名称',
    name           varchar(50)                          not null comment '料线名称',
    position       varchar(20)                          not null comment '料线位置',
    del            char     default '0'                 not null comment '0未删除，1已删除',
    remark         varchar(500)                         null comment '备注',
    create_by      bigint                               null comment '创建人',
    update_by      bigint                               null comment '修改人',
    create_time    datetime default current_timestamp() not null comment '创建时间',
    update_time    datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '料线' charset = utf8mb4;

create index company_id
    on material_line (company_id, pig_farm_id);

create table mqtt_log
(
    id             bigint auto_increment
        primary key,
    topic          varchar(50)   null comment '主题',
    message        varchar(2000) null comment '消息',
    clientId       bigint        null comment '客户端ID',
    type           varchar(10)   null comment '消息类型，发送：Send、接收：Receive',
    correct        varchar(5)    null comment '命令是否正确',
    create_time    datetime      null,
    head           varchar(8)    null,
    version        varchar(4)    null,
    total_length   varchar(4)    null,
    operation_type varchar(4)    null,
    value_length   varchar(4)    null,
    value          varchar(2000) null,
    crc            varchar(4)    null,
    feedback       int default 0 null comment '是否反馈'
)
    comment 'MQTT日志' charset = utf8mb4;

create table order_detail
(
    id           bigint unsigned auto_increment comment '主键'
        primary key,
    order_code   varchar(200)                         not null comment '订单id',
    iccid        varchar(200)                         not null comment '充值卡的iccid',
    tower_id     bigint unsigned                      not null comment '充值卡的料塔id',
    orderNo      varchar(200)                         null comment '物联平台充值流水号',
    sku_id       bigint unsigned                      not null comment '套餐id',
    actual_price int(11) unsigned                     not null comment '实际购买价格单位:分',
    num          int unsigned                         not null comment '购买数量',
    we_status    int(11) unsigned                     not null comment '我方系统处理状态:1待处理,2已完成',
    create_time  datetime default current_timestamp() not null comment '创建时间',
    update_time  datetime                             null on update current_timestamp() comment '修改时间'
)
    comment '订单详情表';

create table pig_back_fat
(
    id          bigint auto_increment
        primary key,
    company_id  bigint                               not null comment '公司id',
    pig_farm_id bigint                               not null comment '猪场id',
    pig_id      bigint                               not null comment '种猪id',
    stage       int                                  null comment '阶段',
    back_fat    int                                  not null comment '背膘',
    operator    bigint                               null comment '测量员',
    del         char     default '0'                 not null comment '0未删除，1已删除',
    remark      varchar(500)                         null comment '备注',
    create_by   bigint                               null comment '创建人',
    update_by   bigint                               null comment '修改人',
    create_time datetime default current_timestamp() not null comment '创建时间',
    update_time datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '背膘记录' charset = utf8mb4;

create index company_id
    on pig_back_fat (company_id, pig_farm_id);

create table pig_back_fat_task
(
    id              bigint auto_increment
        primary key,
    company_id      bigint                               not null comment '公司id',
    pig_farm_id     bigint                               not null comment '猪场id',
    pig_house_id    bigint                               not null comment '猪舍id',
    name            varchar(50)                          null comment '任务名称',
    begin_time      datetime                             null comment '开始时间',
    end_time        datetime                             null comment '结束时间',
    check_mode      varchar(20)                          null comment '测膘模式，批量(BATCH)、单个(SINGLE)',
    status          varchar(20)                          null comment '任务状态（NotStarted：未开始、Running：进行中、Completed：已完成）',
    begin_column_id bigint                               not null comment '开始栏位id',
    end_column_id   bigint                               not null comment '结束栏位id',
    begin_position  varchar(50)                          null comment '开始栏位位置',
    end_position    varchar(50)                          null comment '结束栏位位置',
    del             char     default '0'                 not null comment '0未删除，1已删除',
    remark          varchar(500)                         null comment '备注',
    create_by       bigint                               null comment '创建人',
    update_by       bigint                               null comment '修改人',
    create_time     datetime default current_timestamp() not null comment '创建时间',
    update_time     datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '测膘任务' charset = utf8mb4;

create index company_id
    on pig_back_fat_task (company_id, pig_farm_id);

create table pig_back_fat_task_detail
(
    id                  bigint auto_increment
        primary key,
    company_id          bigint                               not null comment '公司id',
    pig_farm_id         bigint                               not null comment '猪场id',
    pig_house_id        bigint                               not null comment '猪舍id',
    pig_house_column_id bigint                               not null comment '栏位id',
    column_code         varchar(20)                          null comment '栏位编号',
    column_position     varchar(20)                          null comment '栏位名称',
    pig_id              bigint                               null comment '种猪ID',
    ear_number          varchar(30)                          null comment '种猪耳号',
    back_fat            int                                  null comment '背膘',
    operator            bigint                               null comment '测量员',
    task_id             bigint                               null comment '测量任务ID',
    status              varchar(20)                          null comment '处理状态：Undetected：未处理；Detected：已记录；Skip：跳过',
    del                 char     default '0'                 not null comment '0未删除，1已删除',
    remark              varchar(500)                         null comment '备注',
    create_by           bigint                               null comment '创建人',
    update_by           bigint                               null comment '修改人',
    create_time         datetime default current_timestamp() not null comment '创建时间',
    update_time         datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '测膘任务明细' charset = utf8mb4;

create index company_id
    on pig_back_fat_task_detail (company_id, pig_farm_id);

create table pig_breeding
(
    id                   bigint auto_increment
        primary key,
    company_id           bigint                               not null comment '公司id',
    pig_farm_id          bigint                               not null comment '猪场id',
    operator_id          bigint                               not null comment '操作人id',
    ear_number           varchar(50)                          not null comment '耳号',
    approach_time        date                                 not null comment '进场日期',
    type                 int                                  not null comment '猪只类型1公猪，2母猪',
    approach_fetal       int      default 0                   null comment '入场胎次',
    approach_type        int                                  not null comment '进场类型1.自繁，2购买，3转入',
    pig_house_columns_id bigint                               null comment '所属位置(具体栏id)',
    birth_date           date                                 null comment '出生日期',
    presence_status      int      default 1                   null comment '在场状态，1在场，2离场',
    pig_status           int      default 1                   null comment '种猪状态默认：1.后备，2配种，3，空怀，4，返情，5，流产，6，妊娠，7，哺乳 8.断奶',
    status_time          date                                 null comment '状态日期',
    variety              int                                  not null comment '品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜',
    price                decimal(10, 2)                       null comment '价格',
    weight               decimal(10, 2)                       null comment '重量(kg)',
    parity               int      default 1                   null comment '胎次',
    back_fat_record_id   bigint                               null comment '背膘记录ID',
    back_fat             int                                  null comment '背膘',
    back_fat_check_time  datetime                             null comment '背膘检测时间',
    del                  char     default '0'                 not null comment '0 未删除，1 删除',
    remark               varchar(50)                          null comment '备注',
    create_by            bigint                               null comment '创建人id',
    update_by            bigint                               null comment '修改人id',
    create_time          datetime default current_timestamp() not null comment '创建时间',
    update_time          datetime                             null on update current_timestamp() comment '修改时间'
)
    comment '种猪' charset = utf8mb4;

create index company_id
    on pig_breeding (company_id, pig_farm_id);

create table pig_breeding_change_house
(
    id                   bigint auto_increment
        primary key,
    company_id           bigint                               not null comment '公司id',
    pig_farm_id          bigint                               not null comment '猪场id',
    pig_breeding_id      bigint                               not null comment '种猪Id',
    change_house_time    date                                 not null comment '转舍日期',
    house_columns_out_id bigint                               null comment '转出猪栏id',
    house_columns_in_id  bigint                               null comment '转入猪栏id',
    operator_id          bigint                               not null comment '操作人员',
    del                  char     default '0'                 not null comment '0未删除，1已删除',
    remark               varchar(500)                         null comment '备注',
    create_by            bigint                               null comment '创建人',
    update_by            bigint                               null comment '修改人',
    create_time          datetime default current_timestamp() not null comment '创建时间',
    update_time          datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '种猪转舍' charset = utf8mb4;

create index company_id
    on pig_breeding_change_house (company_id, pig_farm_id);

create table pig_breeding_leave
(
    id              bigint auto_increment
        primary key,
    company_id      bigint                               not null comment '公司id',
    pig_farm_id     bigint                               not null comment '猪场id',
    pig_breeding_id bigint                               not null comment '种猪Id',
    leave_time      date                                 not null comment '离场时间',
    type            int                                  not null comment '离场类型1.死淘，2转出,3育成',
    leaving_reason  int                                  null comment '离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它',
    weight          decimal(10, 2)                       null comment '重量kg',
    price           decimal(10, 2)                       null comment '金额',
    unit_price      decimal(10, 2)                       null comment '头单价',
    operator_id     bigint                               not null comment '操作员',
    del             char     default '0'                 not null comment '0未删除，1已删除',
    remark          varchar(500)                         null comment '备注',
    create_by       bigint                               null comment '创建人',
    update_by       bigint                               null comment '修改人',
    create_time     datetime default current_timestamp() not null comment '创建时间',
    update_time     datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '种猪离场' charset = utf8mb4;

create index company_id
    on pig_breeding_leave (company_id, pig_farm_id);

create table pig_farm
(
    id                     bigint auto_increment
        primary key,
    company_id             bigint                               not null comment '公司id',
    name                   varchar(255)                         not null comment '名称',
    type                   int                                  not null comment '猪场类型1.种猪场，2育种场，3自繁自养，4，商品猪场，5，家庭农场，6，集团猪场',
    pig_type_id            bigint                               null comment '猪场猪只类型id',
    level                  int                                  not null comment '规模，1.100头以下，2，100-500头，3，500-1000头，4，1000-3000头，5，3000头以上',
    principal_id           bigint                               null comment '负责人，关联用户id',
    principal_tel          varchar(11)                          null comment '负责人联系电话',
    province_id            int                                  null comment '省id',
    city_id                int                                  null comment '市id',
    area_id                int                                  null comment '行政区划代码',
    address                varchar(255)                         null comment '详细地址',
    default_feeding_amount int      default 2300                not null comment '默认饲喂量',
    del                    char     default '0'                 not null comment '0未删除，1已删除',
    remark                 varchar(500)                         null comment '备注',
    create_by              bigint                               null comment '创建人',
    update_by              bigint                               null comment '修改人',
    create_time            datetime default current_timestamp() not null comment '创建时间',
    update_time            datetime                             null on update current_timestamp() comment '更新时间',
    balance                int      default 0                   not null comment '账户余额单位:分',
    jx                     int      default 0                   not null comment '是否巨星猪场：0：否，1：是'
)
    comment '猪场' charset = utf8mb4;

create index company_id
    on pig_farm (company_id);

create table pig_farm_task
(
    id               bigint auto_increment
        primary key,
    task_id          int               not null comment '任务ID',
    task_key         varchar(50)       not null comment '任务KEY',
    task_desc        varchar(100)      not null comment '任务描述',
    task_time        time              not null comment '任务时间',
    task_cron        varchar(100)      not null comment '任务时间表达式',
    task_enable      int     default 0 not null comment '任务是否启用，1：启用、0：未启用',
    type             varchar(40)       not null comment '任务类型',
    feed_again       tinyint default 0 not null comment '是否多次饲喂',
    company_id       bigint            not null comment '公司id',
    pig_farm_id      bigint            not null comment '猪场id',
    pig_house_id     bigint            not null comment '栋舍id',
    pig_house_type   int               null comment '栋舍类别',
    material_line_id bigint            not null comment '料线ID',
    switcher_id      bigint            null comment '电闸ID'
)
    comment '猪场下料任务' charset = utf8mb4;

create index company_id
    on pig_farm_task (company_id, pig_farm_id);

create table pig_feeding_record
(
    id               bigint auto_increment
        primary key,
    client_id        bigint      not null comment '主机编号',
    feeder_code      int(3)      not null comment '饲喂器编号',
    pig_id           bigint      null comment '种猪ID',
    ear_number       varchar(20) null comment '种猪耳号',
    back_fat         int         null comment '种猪背膘',
    amount           int         null comment '饲喂量（克/g）',
    parities         int         null comment '胎次',
    stage            int         null comment '阶段',
    is_auto          varchar(5)  null comment '是否自动饲喂',
    company_id       bigint      not null comment '公司id',
    pig_farm_id      bigint      not null comment '猪场id',
    pig_house_id     bigint      not null comment '栋舍id',
    pig_house_name   varchar(20) null,
    pig_house_type   int         null comment '栋舍类型',
    pig_house_row_id bigint      null comment '栋舍排id',
    pig_house_col_id bigint      not null comment '栋舍栏位',
    create_time      datetime    null
)
    comment '猪场饲喂记录' charset = utf8mb4;

create index pig_feeding_record_company_id_pig_farm_id_index
    on pig_feeding_record (company_id, pig_farm_id);

create index pig_feeding_record_pig_farm_id_index
    on pig_feeding_record (pig_farm_id);

create table pig_group
(
    id                   bigint auto_increment
        primary key,
    company_id           bigint                               not null comment '公司id',
    pig_farm_id          bigint                               not null comment '猪场id',
    pig_house_columns_id bigint                               not null comment '猪栏Id',
    name                 varchar(255)                         not null comment '群名称',
    del                  char     default '0'                 not null comment '0未删除，1已删除',
    remark               varchar(500)                         null comment '备注',
    create_by            bigint                               null comment '创建人',
    update_by            bigint                               null comment '修改人',
    create_time          datetime default current_timestamp() not null comment '创建时间',
    update_time          datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '猪群' charset = utf8mb4;

create index company_id
    on pig_group (company_id, pig_farm_id);

create table pig_house
(
    id              bigint auto_increment
        primary key,
    company_id      bigint                               not null comment '公司id',
    pig_farm_id     bigint                               not null comment '猪场id',
    name            varchar(255)                         not null comment '名称',
    code            varchar(255)                         null comment '编号：比如配怀一：PH1',
    type            int                                  not null comment '1.分娩舍,2配种舍,3保育舍,4育肥舍,5,公猪舍,6,妊娠舍,7,混合舍,8其它',
    pig_type_id     int                                  null comment '猪种ID,继承猪场pig_type_id',
    rows            int                                  not null comment '总排数',
    columns         int                                  not null comment '总栏数',
    max_per_columns int                                  not null comment '最大存栏数',
    del             char     default '0'                 not null comment '0未删除，1已删除',
    remark          varchar(500)                         null comment '备注',
    create_by       bigint                               null comment '创建人',
    update_by       bigint                               null comment '修改人',
    create_time     datetime default current_timestamp() not null comment '创建时间',
    update_time     datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '猪舍' charset = utf8mb4;

create index company_id
    on pig_house (company_id, pig_farm_id);

create table pig_house_columns
(
    id                bigint auto_increment
        primary key,
    company_id        bigint                               not null comment '公司id',
    pig_farm_id       bigint                               not null comment '猪场id',
    pig_house_id      bigint                               null comment '栋舍ID',
    pig_house_rows_id bigint                               not null comment '猪舍排id',
    name              varchar(255)                         not null comment '名称',
    code              varchar(255)                         null comment '编号：比如第5栏：05',
    no                int                                  null comment '栏位编号',
    position          varchar(255)                         null comment '栏位位置',
    client_id         bigint                               null comment '主机ID',
    feeder_code       int                                  null comment '饲喂器编号',
    feeder_enable     int      default 0                   not null comment '肥猪饲喂器是否启用，默认关',
    feeding_amount    int                                  null comment '栏位饲喂量（克）',
    pig_id            bigint                               null comment '猪只与栏位关联，主要用于巨星猪只，云慧养端关系维护在表pig_breeding中',
    curr_quantity     int                                  null comment 'AI计数：当前猪只数量',
    curr_date         datetime                             null comment 'AI计数：栏位当前猪只数量时间',
    del               char     default '0'                 not null comment '0未删除，1已删除',
    remark            varchar(500)                         null comment '备注',
    create_by         bigint                               null comment '创建人',
    update_by         bigint                               null comment '修改人',
    create_time       datetime default current_timestamp() not null comment '创建时间',
    update_time       datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '猪舍栏' charset = utf8mb4;

create index company_id
    on pig_house_columns (company_id, pig_farm_id, pig_house_rows_id);

create index pig_house_columns_pig_farm_id_index
    on pig_house_columns (pig_farm_id);

create table pig_house_rows
(
    id           bigint auto_increment
        primary key,
    company_id   bigint                               not null comment '公司id',
    pig_farm_id  bigint                               not null comment '猪场id',
    pig_house_id bigint                               not null comment '猪舍id',
    name         varchar(255)                         null comment '名称',
    code         varchar(255)                         null comment '编号：比如第一排：01',
    position     varchar(255)                         null comment '排位置：比如第一排：PH1-01',
    del          char     default '0'                 not null comment '0未删除，1已删除',
    remark       varchar(500)                         null comment '备注',
    create_by    bigint                               null comment '创建人',
    update_by    bigint                               null comment '修改人',
    create_time  datetime default current_timestamp() not null comment '创建时间',
    update_time  datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '猪舍排' charset = utf8mb4;

create index company_id
    on pig_house_rows (company_id, pig_farm_id, pig_house_id);

create table pig_labor
(
    id               bigint auto_increment
        primary key,
    company_id       bigint                               not null comment '公司id',
    pig_farm_id      bigint                               not null comment '猪场id',
    pig_breeding_id  bigint                               not null comment '种猪Id',
    labor_date       date                                 not null comment '分娩时间',
    labor_result     int                                  not null comment '分娩结果1，顺产，2，难产，3助产',
    labor_minute     int                                  not null comment '产程(分钟)',
    healthy_number   int      default 0                   not null comment '健仔',
    weak_number      int      default 0                   null comment '弱仔',
    deformity_number int      default 0                   null comment '畸形',
    dead_number      int      default 0                   null comment '死胎',
    mummy_number     int      default 0                   null comment '木乃伊',
    live_number      int      default 0                   null comment '活仔母',
    killed_number    int      default 0                   null comment '处死活仔数',
    feeding_number   int      default 0                   null comment '可饲养仔猪数：健仔+弱仔+畸形-处死活仔',
    parity           int                                  not null comment '胎次',
    operator_id      bigint                               null comment '操作人id',
    del              char     default '0'                 not null comment '0未删除，1已删除',
    remark           varchar(500)                         null comment '备注',
    create_by        bigint                               null comment '创建人',
    update_by        bigint                               null comment '修改人',
    create_time      datetime default current_timestamp() not null comment '创建时间',
    update_time      datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '母猪分娩' charset = utf8mb4;

create index company_id
    on pig_labor (company_id, pig_farm_id);

create table pig_labor_task
(
    id              bigint auto_increment
        primary key,
    company_id      bigint                               not null comment '公司id',
    pig_farm_id     bigint                               not null comment '猪场id',
    pig_breeding_id bigint                               not null comment '种猪id',
    mating_date     date                                 not null comment '配种日期',
    pregnancy_date  date                                 null comment '妊娠时间',
    days            int                                  not null comment '超期的天数',
    status          int      default 1                   not null comment '1待处理，2已处理',
    del             char     default '0'                 not null comment '0未删除，1已删除',
    remark          varchar(500)                         null comment '备注',
    create_by       bigint                               null comment '创建人',
    update_by       bigint                               null comment '修改人',
    create_time     datetime default current_timestamp() not null comment '创建时间',
    update_time     datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '分娩任务' charset = utf8mb4;

create index company_id
    on pig_labor_task (company_id, pig_farm_id);

create table pig_mating
(
    id                bigint auto_increment
        primary key,
    company_id        bigint                               not null comment '公司id',
    pig_farm_id       bigint                               not null comment '猪场id',
    pig_breeding_id   bigint                               not null comment '种猪id',
    before_pig_status int                                  null comment '种猪状态默认：1.后备，2配种，3，空怀，4，返情，5，流产，6，妊娠，7，哺乳 8.断奶',
    mating_date       date                                 not null comment '配种时间',
    type              int                                  not null comment '配种方式1,人工受精，2自然交配',
    boar_id           bigint                               null comment '配种公猪',
    operator_id       bigint                               null comment '操作员',
    parity            int      default 1                   null comment '胎次',
    del               char     default '0'                 not null comment '0未删除，1已删除',
    remark            varchar(500)                         null comment '备注',
    create_by         bigint                               null comment '创建人',
    update_by         bigint                               null comment '修改人',
    create_time       datetime default current_timestamp() not null comment '创建时间',
    update_time       datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '母猪配种' charset = utf8mb4;

create index company_id
    on pig_mating (company_id, pig_farm_id);

create table pig_mating_task
(
    id              bigint auto_increment
        primary key,
    company_id      bigint                               not null comment '公司id',
    pig_farm_id     bigint                               not null comment '猪场id',
    pig_breeding_id bigint                               not null comment '种猪id',
    status          int      default 1                   not null comment '1待处理，2处理',
    days            int      default 1                   not null comment '超期的天数',
    del             char     default '0'                 not null comment '0未删除，1已删除',
    remark          varchar(500)                         null comment '备注',
    create_by       bigint                               null comment '创建人',
    update_by       bigint                               null comment '修改人',
    create_time     datetime default current_timestamp() not null comment '创建时间',
    update_time     datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '配种任务' charset = utf8mb4;

create index company_id
    on pig_mating_task (company_id, pig_farm_id);

create table pig_piggy
(
    id              bigint auto_increment
        primary key,
    company_id      bigint                               not null comment '公司id',
    pig_farm_id     bigint                               not null comment '猪场id',
    pig_breeding_id bigint                               not null comment '种猪id',
    number          int                                  not null comment '数量',
    del             char     default '0'                 not null comment '0未删除，1已删除',
    remark          varchar(500)                         null comment '备注',
    create_by       bigint                               null comment '创建人',
    update_by       bigint                               null comment '修改人',
    create_time     datetime default current_timestamp() not null comment '创建时间',
    update_time     datetime                             null on update current_timestamp() comment '更新时间',
    constraint pig_breeding_id
        unique (pig_breeding_id)
)
    comment '仔猪入场' charset = utf8mb4;

create index company_id
    on pig_piggy (company_id, pig_farm_id);

create table pig_piggy_leave
(
    id             bigint auto_increment
        primary key,
    company_id     bigint                               not null comment '公司id',
    pig_farm_id    bigint                               not null comment '猪场id',
    pig_piggy_id   bigint                               not null comment '仔猪id',
    leave_time     date                                 not null comment '离场时间',
    number         int                                  not null comment '离场数量',
    type           int                                  not null comment '离场类型1.死淘，2转出',
    leaving_reason int                                  null comment '离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它',
    weight         decimal(10, 2)                       null comment '重量',
    price          decimal(10, 2)                       null comment '价格',
    operator_id    bigint                               null comment '操作人',
    del            char     default '0'                 not null comment '0未删除，1已删除',
    remark         varchar(500)                         null comment '备注',
    create_by      bigint                               null comment '创建人',
    update_by      bigint                               null comment '修改人',
    create_time    datetime default current_timestamp() not null comment '创建时间',
    update_time    datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '仔猪离场' charset = utf8mb4;

create index company_id
    on pig_piggy_leave (company_id, pig_farm_id, pig_piggy_id);

create table pig_pork
(
    id                   bigint auto_increment
        primary key,
    company_id           bigint                               not null comment '公司id',
    pig_farm_id          bigint                               not null comment '猪场id',
    approach_time        date                                 not null comment '进场时间',
    pig_house_columns_id bigint                               not null comment '所属位置',
    pig_group_id         bigint                               not null comment '猪群id',
    approach_type        int                                  not null comment '进场类型，1购买，2转入，3自繁',
    birth_date           date                                 not null comment '出生日期',
    number               int                                  not null comment '数量',
    variety              int                                  null comment '品种1,长白，2大白，3二元，4，三元，5土猪，6大长，7，杜洛克，8皮杜',
    weight               decimal(10, 2)                       null comment '重量(kg)',
    price                decimal(10, 2)                       null comment '价格',
    operator_id          bigint                               null comment '操作员',
    del                  char     default '0'                 not null comment '0未删除，1已删除',
    remark               varchar(500)                         null comment '备注',
    create_by            bigint                               null comment '创建人',
    update_by            bigint                               null comment '修改人',
    create_time          datetime default current_timestamp() not null comment '创建时间',
    update_time          datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '肉猪入场' charset = utf8mb4;

create index company_id
    on pig_pork (company_id, pig_farm_id, pig_house_columns_id);

create table pig_pork_change_house
(
    id                   bigint auto_increment
        primary key,
    company_id           bigint                               not null comment '公司id',
    pig_farm_id          bigint                               not null comment '猪场id',
    change_house_time    date                                 not null comment '转舍日期',
    number               int                                  not null comment '数量',
    house_columns_out_id bigint                               not null comment '转出猪栏id',
    pig_group_out_id     bigint                               not null comment '转出群id',
    house_columns_in_id  bigint                               not null comment '转入猪栏id',
    pig_group_in_id      bigint                               not null comment '转入群id',
    operator_id          bigint                               not null comment '操作人员',
    weight               decimal(10, 2)                       null comment '重量(kg)',
    del                  char     default '0'                 not null comment '0未删除，1已删除',
    remark               varchar(500)                         null comment '备注',
    create_by            bigint                               null comment '创建人',
    update_by            bigint                               null comment '修改人',
    create_time          datetime default current_timestamp() not null comment '创建时间',
    update_time          datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '肉猪转舍' charset = utf8mb4;

create index company_id
    on pig_pork_change_house (company_id, pig_farm_id);

create table pig_pork_leave
(
    id                   bigint auto_increment
        primary key,
    company_id           bigint                               not null comment '公司id',
    pig_farm_id          bigint                               not null comment '猪场id',
    pig_house_columns_id bigint                               not null comment '位置',
    pig_group_id         bigint                               not null comment '猪群id',
    leave_time           date                                 not null comment '离场时间',
    number               int                                  not null comment '离场数量',
    type                 int                                  not null comment '离场类型1.死淘，2转出',
    leaving_reason       int                                  null comment '离场原因1,长期不发育，2，生产能力太差，3，胎龄过大，4，体况太差，5，精液质量过差，6发烧，7，难产，8，肢蹄病，9压死，10，弱仔，11，病死，12，打架，13，其它',
    weight               decimal(10, 2)                       null comment '重量',
    price                decimal(10, 2)                       null comment '价格',
    unit_price           decimal(10, 2)                       null comment '单价',
    operator_id          bigint                               null comment '操作人',
    del                  char     default '0'                 not null comment '0未删除，1已删除',
    remark               varchar(500)                         null comment '备注',
    create_by            bigint                               null comment '创建人',
    update_by            bigint                               null comment '修改人',
    create_time          datetime default current_timestamp() not null comment '创建时间',
    update_time          datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '肉猪离场' charset = utf8mb4;

create index company_id
    on pig_pork_leave (company_id, pig_farm_id, pig_house_columns_id);

create table pig_pork_stock
(
    id                   bigint auto_increment
        primary key,
    company_id           bigint                               not null comment '公司id',
    pig_farm_id          bigint                               not null comment '猪场id',
    pig_house_columns_id bigint                               not null comment '猪栏id',
    pig_group_id         bigint                               not null comment '猪群id',
    pork_number          int      default 0                   not null comment '肉猪库存',
    type                 int      default 1                   not null comment '1在场，2离场',
    del                  char     default '0'                 not null comment '0未删除，1已删除',
    remark               varchar(500)                         null comment '备注',
    create_by            bigint                               null comment '创建人',
    update_by            bigint                               null comment '修改人',
    create_time          datetime default current_timestamp() not null comment '创建时间',
    update_time          datetime                             null on update current_timestamp() comment '更新时间',
    constraint pig_house_columns_id_pig_group_id_type
        unique (pig_house_columns_id, pig_group_id, type)
)
    comment '肉猪库存' charset = utf8mb4;

create index company_id
    on pig_pork_stock (company_id, pig_farm_id, pig_house_columns_id);

create table pig_pregnancy
(
    id               bigint auto_increment
        primary key,
    company_id       bigint                               not null comment '公司id',
    pig_farm_id      bigint                               not null comment '猪场id',
    pig_breeding_id  bigint                               not null comment '猪id',
    pregnancy_date   date                                 not null comment '妊娠时间',
    pregnancy_result int                                  not null comment '妊娠结果1.妊娠，2流产，3返情，4阴性',
    operator_id      bigint                               null comment '操作员',
    parity           int                                  not null comment '胎次',
    boar_id          bigint                               null comment '公猪Id',
    mating_date      date                                 null comment '配种日期',
    del              char     default '0'                 not null comment '0未删除，1已删除',
    remark           varchar(500)                         null comment '备注',
    create_by        bigint                               null comment '创建人',
    update_by        bigint                               null comment '修改人',
    create_time      datetime default current_timestamp() not null comment '创建时间',
    update_time      datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '母猪妊娠' charset = utf8mb4;

create index company_id
    on pig_pregnancy (company_id, pig_farm_id);

create table pig_pregnancy_task
(
    id              bigint auto_increment
        primary key,
    company_id      bigint                               not null comment '公司id',
    pig_farm_id     bigint                               not null comment '猪场id',
    pig_breeding_id bigint                               not null comment '种猪id',
    mating_date     date                                 not null comment '配种日期',
    days            int                                  not null comment '超期的天数',
    status          int      default 1                   not null comment '1待处理，2已处理',
    del             char     default '0'                 not null comment '0未删除，1已删除',
    remark          varchar(500)                         null comment '备注',
    create_by       bigint                               null comment '创建人',
    update_by       bigint                               null comment '修改人',
    create_time     datetime default current_timestamp() not null comment '创建时间',
    update_time     datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '妊娠任务' charset = utf8mb4;

create index company_id
    on pig_pregnancy_task (company_id, pig_farm_id);

create table pig_semen_collection
(
    id                bigint auto_increment
        primary key,
    company_id        bigint                               not null comment '公司id',
    pig_farm_id       bigint                               not null comment '猪场id',
    pig_breeding_id   bigint                               not null comment '种猪id',
    collection_date   date                                 not null comment '采精日期',
    volume            decimal(10, 2)                       not null comment '采精量',
    color             int                                  not null comment '色泽，1乳白色，2，灰白色，3，偏黄色，4，红色，5绿色',
    smell             int                                  not null comment '气味，1正常，2异常',
    vitality          decimal(10, 2)                       not null comment '活力',
    density           decimal(10, 2)                       not null comment '密度',
    deformity         decimal(10, 2)                       not null comment '畸形率',
    dilution_fraction decimal(10, 2)                       not null comment '稀释分数',
    ph                decimal(10, 2)                       not null comment 'PH值',
    operator_id       bigint                               null comment '操作员',
    del               char     default '0'                 not null comment '0未删除，1已删除',
    remark            varchar(500)                         null comment '备注',
    create_by         bigint                               null comment '创建人',
    update_by         bigint                               null comment '修改人',
    create_time       datetime default current_timestamp() not null comment '创建时间',
    update_time       datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '公猪采精' charset = utf8mb4;

create index company_id
    on pig_semen_collection (company_id, pig_farm_id);

create table pig_type
(
    id          bigint auto_increment
        primary key,
    company_id  bigint                               not null comment '公司id',
    name        varchar(255)                         null comment '名称',
    del         char     default '0'                 not null comment '删除：0-否，1-是',
    remark      varchar(500)                         null comment '备注',
    create_by   bigint                               null comment '创建人id',
    update_by   bigint                               null comment '更新人id',
    create_time datetime default current_timestamp() null comment '创建时间',
    update_time datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '猪场猪种' collate = utf8mb4_unicode_ci;

create index company_id
    on pig_type (company_id);

create table pig_weaned
(
    id                   bigint auto_increment
        primary key,
    company_id           bigint                               not null comment '公司id',
    pig_farm_id          bigint                               not null comment '猪场id',
    pig_breeding_id      bigint                               not null comment '种猪id',
    weaned_date          date                                 not null comment '断奶日期',
    weaned_number        int                                  not null comment '断奶数量',
    pig_house_columns_id bigint                               not null comment '转入栏号id',
    pig_group_id         bigint                               not null comment '群号id',
    parity               int                                  not null comment '胎次',
    weaned_weight        decimal(10, 2)                       null comment '断奶窝重',
    operator_id          bigint                               not null comment '操作人id',
    del                  char     default '0'                 not null comment '0未删除，1已删除',
    remark               varchar(500)                         null comment '备注',
    create_by            bigint                               null comment '创建人',
    update_by            bigint                               null comment '修改人',
    create_time          datetime default current_timestamp() not null comment '创建时间',
    update_time          datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '母猪断奶' charset = utf8mb4;

create index company_id
    on pig_weaned (company_id, pig_farm_id);

create table pig_weaned_task
(
    id              bigint auto_increment
        primary key,
    company_id      bigint                               not null comment '公司id',
    pig_farm_id     bigint                               not null comment '猪场id',
    pig_breeding_id bigint                               not null comment '种猪id',
    labor_date      date                                 not null comment '分娩日期',
    days            int                                  not null comment '超期的天数',
    status          int      default 1                   not null comment '1待处理，2已处理',
    del             char     default '0'                 not null comment '0未删除，1已删除',
    remark          varchar(500)                         null comment '备注',
    create_by       bigint                               null comment '创建人',
    update_by       bigint                               null comment '修改人',
    create_time     datetime default current_timestamp() not null comment '创建时间',
    update_time     datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '断奶任务' charset = utf8mb4;

create index company_id
    on pig_weaned_task (company_id, pig_farm_id);

create table printer
(
    id           bigint unsigned auto_increment comment '主键'
        primary key,
    printer_name varchar(200)                         not null comment '打印机名',
    printer_ip   varchar(200)                         not null comment '打印机ip',
    printer_port int                                  not null comment '打印机端口',
    create_time  datetime default current_timestamp() not null comment '创建时间',
    update_time  datetime                             null on update current_timestamp() comment '修改时间',
    constraint unq_printer_ip
        unique (printer_ip),
    constraint unq_printer_name
        unique (printer_name)
)
    comment '打印机表';

create table printer_user
(
    user_id    bigint default 0 not null comment '用户ID',
    printer_id bigint           not null comment '打印机ID',
    is_default int              not null,
    primary key (user_id, printer_id)
)
    comment '用户和打印机关联表' charset = utf8mb4;

create table product
(
    id          bigint auto_increment
        primary key,
    category    varchar(20)       null comment '产品类别',
    name        varchar(30)       null comment '名称',
    version     varchar(20)       null comment '产品版本',
    warranty    int               null comment '质保月数',
    del         tinyint default 0 not null,
    create_time datetime          null,
    update_time datetime          null
)
    comment '产品SKU';

create table production_day
(
    id              bigint auto_increment
        primary key,
    company_id      bigint                               not null comment '公司id',
    pig_farm_id     bigint                               not null comment '猪场id',
    pig_breeding_id bigint                               not null comment '猪的id',
    year            int                                  not null comment '年份',
    month           int                                  not null comment '月份',
    days            int                                  not null comment '当月天数',
    del             char     default '0'                 not null comment '0-未删除，1-已删除',
    remark          varchar(500)                         null comment '备注',
    create_by       bigint                               null comment '创建人id',
    update_by       bigint                               null comment '更新人id',
    create_time     datetime default current_timestamp() null comment '创建时间',
    update_time     datetime                             null on update current_timestamp() comment '更新时间'
)
    charset = utf8mb4;

create table push_message
(
    id             bigint auto_increment
        primary key,
    company_id     bigint                               not null comment '公司id',
    pig_farm_id    bigint                               null comment '猪场id',
    user_id        bigint                               null comment '用户id',
    title          varchar(255)                         not null comment '标题',
    body           varchar(512)                         not null comment '内容',
    ext_parameters varchar(512)                         null comment '附加内容',
    status         int                                  not null comment '状态：0 未读，1 已读',
    type           varchar(20)                          not null comment '业务类型：',
    sub_type       varchar(128)                         null comment '子类型',
    push_type      varchar(20)                          not null comment '推送类型：MESSAGE 消息，NOTICE 通知',
    del            char                                 not null comment '0 未删除，1 已删除',
    remark         varchar(500)                         null comment '备注',
    create_by      bigint                               null comment '创建人id',
    update_by      bigint                               null comment '更新人id',
    create_time    datetime default current_timestamp() not null comment '创建时间',
    update_time    datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '消息推送表' charset = utf8mb4;

create index user_id
    on push_message (company_id, pig_farm_id, user_id);

create table push_message_type
(
    id                bigint auto_increment comment '推送消息类型ID'
        primary key,
    message_type_name varchar(30)                          not null comment '推送消息类型名称',
    message_type_key  varchar(100)                         null comment '推送消息类型字符串',
    message_type_sort int      default 1                   not null comment '显示顺序',
    status            int      default 1                   not null comment '推送消息类型状态（1 正常  0 停用）',
    del               char     default '0'                 null comment '删除标志（0 正常 1 删除）',
    remark            varchar(500)                         null comment '备注',
    create_by         bigint                               null comment '创建者',
    update_by         bigint                               null comment '更新者',
    create_time       datetime default current_timestamp() not null comment '创建时间',
    update_time       datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '角色信息表' charset = utf8mb4;

create index company_id
    on push_message_type (message_type_key);

create table push_user_type
(
    user_id bigint not null comment '用户ID',
    type_id bigint not null comment '推送消息类型ID',
    primary key (user_id, type_id)
)
    comment '用户和角色关联表' charset = utf8mb4;

create table qrcode
(
    id            bigint auto_increment
        primary key,
    company_id    bigint                               null comment '公司id',
    pig_farm_id   bigint                               null comment '猪场id',
    pig_house_id  bigint                               null comment '栋舍id',
    pig_column_id bigint                               null comment '栏位id',
    no            int                                  null comment '栏位号',
    code          varchar(50)                          not null comment '编码',
    feeder_code   int                                  not null comment '饲喂器号',
    batch         int                                  null comment '批次',
    del           char     default '0'                 not null,
    create_time   datetime default current_timestamp() not null,
    constraint qrcode_code_uindex
        unique (code)
)
    comment '二维码' charset = utf8mb4;

create table qrtz_calendars
(
    sched_name    varchar(120) not null,
    calendar_name varchar(190) not null,
    calendar      blob         not null,
    primary key (sched_name, calendar_name)
);

create table qrtz_fired_triggers
(
    sched_name        varchar(120) not null,
    entry_id          varchar(95)  not null,
    trigger_name      varchar(190) not null,
    trigger_group     varchar(190) not null,
    instance_name     varchar(190) not null,
    fired_time        bigint(13)   not null,
    sched_time        bigint(13)   not null,
    priority          int          not null,
    state             varchar(16)  not null,
    job_name          varchar(190) null,
    job_group         varchar(190) null,
    is_nonconcurrent  varchar(1)   null,
    requests_recovery varchar(1)   null,
    primary key (sched_name, entry_id)
);

create index idx_qrtz_ft_inst_job_req_rcvry
    on qrtz_fired_triggers (sched_name, instance_name, requests_recovery);

create index idx_qrtz_ft_j_g
    on qrtz_fired_triggers (sched_name, job_name, job_group);

create index idx_qrtz_ft_jg
    on qrtz_fired_triggers (sched_name, job_group);

create index idx_qrtz_ft_t_g
    on qrtz_fired_triggers (sched_name, trigger_name, trigger_group);

create index idx_qrtz_ft_tg
    on qrtz_fired_triggers (sched_name, trigger_group);

create index idx_qrtz_ft_trig_inst_name
    on qrtz_fired_triggers (sched_name, instance_name);

create table qrtz_job_details
(
    sched_name        varchar(120) not null,
    job_name          varchar(190) not null,
    job_group         varchar(190) not null,
    description       varchar(250) null,
    job_class_name    varchar(250) not null,
    is_durable        varchar(1)   not null,
    is_nonconcurrent  varchar(1)   not null,
    is_update_data    varchar(1)   not null,
    requests_recovery varchar(1)   not null,
    job_data          blob         null,
    primary key (sched_name, job_name, job_group)
);

create index idx_qrtz_j_grp
    on qrtz_job_details (sched_name, job_group);

create index idx_qrtz_j_req_recovery
    on qrtz_job_details (sched_name, requests_recovery);

create table qrtz_locks
(
    sched_name varchar(120) not null,
    lock_name  varchar(40)  not null,
    primary key (sched_name, lock_name)
);

create table qrtz_paused_trigger_grps
(
    sched_name    varchar(120) not null,
    trigger_group varchar(190) not null,
    primary key (sched_name, trigger_group)
);

create table qrtz_scheduler_state
(
    sched_name        varchar(120) not null,
    instance_name     varchar(190) not null,
    last_checkin_time bigint(13)   not null,
    checkin_interval  bigint(13)   not null,
    primary key (sched_name, instance_name)
);

create table qrtz_triggers
(
    sched_name     varchar(120) not null,
    trigger_name   varchar(190) not null,
    trigger_group  varchar(190) not null,
    job_name       varchar(190) not null,
    job_group      varchar(190) not null,
    description    varchar(250) null,
    next_fire_time bigint(13)   null,
    prev_fire_time bigint(13)   null,
    priority       int          null,
    trigger_state  varchar(16)  not null,
    trigger_type   varchar(8)   not null,
    start_time     bigint(13)   not null,
    end_time       bigint(13)   null,
    calendar_name  varchar(190) null,
    misfire_instr  smallint(2)  null,
    job_data       blob         null,
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_triggers_ibfk_1
        foreign key (sched_name, job_name, job_group) references qrtz_job_details (sched_name, job_name, job_group)
);

create table qrtz_blob_triggers
(
    sched_name    varchar(120) not null,
    trigger_name  varchar(190) not null,
    trigger_group varchar(190) not null,
    blob_data     blob         null,
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_blob_triggers_ibfk_1
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers (sched_name, trigger_name, trigger_group)
);

create index sched_name
    on qrtz_blob_triggers (sched_name, trigger_name, trigger_group);

create table qrtz_cron_triggers
(
    sched_name      varchar(120) not null,
    trigger_name    varchar(190) not null,
    trigger_group   varchar(190) not null,
    cron_expression varchar(120) not null,
    time_zone_id    varchar(80)  null,
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_cron_triggers_ibfk_1
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers (sched_name, trigger_name, trigger_group)
);

create table qrtz_simple_triggers
(
    sched_name      varchar(120) not null,
    trigger_name    varchar(190) not null,
    trigger_group   varchar(190) not null,
    repeat_count    bigint(7)    not null,
    repeat_interval bigint(12)   not null,
    times_triggered bigint(10)   not null,
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_simple_triggers_ibfk_1
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers (sched_name, trigger_name, trigger_group)
);

create table qrtz_simprop_triggers
(
    sched_name    varchar(120)   not null,
    trigger_name  varchar(190)   not null,
    trigger_group varchar(190)   not null,
    str_prop_1    varchar(512)   null,
    str_prop_2    varchar(512)   null,
    str_prop_3    varchar(512)   null,
    int_prop_1    int            null,
    int_prop_2    int            null,
    long_prop_1   bigint         null,
    long_prop_2   bigint         null,
    dec_prop_1    decimal(13, 4) null,
    dec_prop_2    decimal(13, 4) null,
    bool_prop_1   varchar(1)     null,
    bool_prop_2   varchar(1)     null,
    primary key (sched_name, trigger_name, trigger_group),
    constraint qrtz_simprop_triggers_ibfk_1
        foreign key (sched_name, trigger_name, trigger_group) references qrtz_triggers (sched_name, trigger_name, trigger_group)
);

create index idx_qrtz_t_c
    on qrtz_triggers (sched_name, calendar_name);

create index idx_qrtz_t_g
    on qrtz_triggers (sched_name, trigger_group);

create index idx_qrtz_t_j
    on qrtz_triggers (sched_name, job_name, job_group);

create index idx_qrtz_t_jg
    on qrtz_triggers (sched_name, job_group);

create index idx_qrtz_t_n_g_state
    on qrtz_triggers (sched_name, trigger_group, trigger_state);

create index idx_qrtz_t_n_state
    on qrtz_triggers (sched_name, trigger_name, trigger_group, trigger_state);

create index idx_qrtz_t_next_fire_time
    on qrtz_triggers (sched_name, next_fire_time);

create index idx_qrtz_t_nft_misfire
    on qrtz_triggers (sched_name, misfire_instr, next_fire_time);

create index idx_qrtz_t_nft_st
    on qrtz_triggers (sched_name, trigger_state, next_fire_time);

create index idx_qrtz_t_nft_st_misfire
    on qrtz_triggers (sched_name, misfire_instr, next_fire_time, trigger_state);

create index idx_qrtz_t_nft_st_misfire_grp
    on qrtz_triggers (sched_name, misfire_instr, next_fire_time, trigger_group, trigger_state);

create index idx_qrtz_t_state
    on qrtz_triggers (sched_name, trigger_state);

create table sku
(
    id            bigint unsigned auto_increment comment '主键'
        primary key,
    name          varchar(200)     not null comment '套餐名称',
    package_code  varchar(200)     not null comment '套餐编码',
    package_type  varchar(200)     not null comment '套餐类型',
    recharge_type varchar(200)     not null comment '生效类型',
    price         int(11) unsigned not null comment '套餐价格单位:分'
)
    comment '套餐类型表';

create table sph_employ
(
    id          bigint auto_increment
        primary key,
    employ_code varchar(64)      null comment '工号',
    name        varchar(64)      null comment '姓名',
    password    varchar(64)      null comment '密码',
    icon        varchar(100)     null comment '头像',
    email       varchar(100)     null comment '邮箱',
    position    varchar(20)      null comment '职位',
    nick_name   varchar(200)     null comment '昵称',
    phone       varchar(64)      null comment '手机号',
    farm_id     bigint           null comment '默认养殖场',
    farm_name   varchar(30)      null comment '默认养殖场名称',
    note        varchar(500)     null comment '备注信息',
    create_time datetime         null comment '创建时间',
    login_time  datetime         null comment '最后登录时间',
    status      int(1) default 1 null comment '帐号启用状态：0->禁用；1->启用',
    org_id      bigint           null comment '所属公司',
    org_name    varchar(30)      null comment '所属公司名称',
    login_id    varchar(30)      null comment '登录名',
    rid         varchar(64)      null comment '设备RID',
    register    int              null comment '是否是注册用户，0：否，1：是'
)
    comment '员工表' charset = utf8mb4;

create table summary_report
(
    id                         bigint auto_increment
        primary key,
    company_id                 bigint     null,
    farm_id                    bigint     null,
    tower_count                int        null comment '料塔数',
    tower_device_count         int        null comment '料塔设备数',
    feeder_count               int        null comment '饲喂器总数',
    feeder_ph_count            int        null comment '配怀舍饲喂器数',
    feeder_hb_count            int        null comment '后备舍饲喂器数',
    feeder_gz_count            int        null comment '公猪站饲喂器数',
    feeder_fm_count            int        null comment '分娩舍饲喂器数',
    feeder_by_count            int        null comment '保育舍饲喂器数',
    today_feeding_amount_total bigint(15) null comment '今日总饲喂量（g）',
    today_feeding_amount_ph    bigint(15) null comment '今日配怀饲喂量（g）',
    today_feeding_amount_hb    bigint(15) null comment '今日后备饲喂量（g）',
    today_feeding_amount_gz    bigint(15) null comment '今日公猪站饲喂量（g）',
    today_feeding_amount_fm    bigint(15) null comment '今日分娩饲喂量（g）',
    today_feeding_amount_by    bigint(15) null comment '今日保育饲喂量（g）',
    year_feeding_amount_total  bigint(15) null comment '年总饲喂量（g）',
    year_feeding_amount_ph     bigint(15) null comment '年配怀舍饲喂量（g）',
    year_feeding_amount_hb     bigint(15) null comment '年后备饲喂量（g）',
    year_feeding_amount_gz     bigint(15) null comment '年公猪站饲喂量（g）',
    year_feeding_amount_fm     bigint(15) null comment '年分娩饲喂量（g）',
    year_feeding_amount_by     bigint(15) null comment '年保育饲喂量（g）'
);

create table switcher
(
    id           bigint auto_increment
        primary key,
    company_id   bigint                               not null comment '公司id',
    pig_farm_id  bigint                               not null comment '猪场id',
    pig_house_id bigint                               not null comment '栋舍id',
    name         varchar(50)                          null comment '电闸名称',
    client_id    bigint                               not null comment '主机编号',
    del          char     default '0'                 not null comment '0未删除，1已删除',
    remark       varchar(500)                         null comment '备注',
    create_by    bigint                               null comment '创建人',
    update_by    bigint                               null comment '修改人',
    create_time  datetime default current_timestamp() not null comment '创建时间',
    update_time  datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '电闸' charset = utf8mb4;

create index company_id
    on switcher (company_id, pig_farm_id);

create table switcher_route
(
    id               bigint auto_increment
        primary key,
    code             int           null comment '路由编号',
    client_id        bigint        not null comment '主机编号',
    status           int default 0 null comment '通电状态：0断电，1通电',
    status_time      datetime      null comment '最近的状态时间',
    switch_id        bigint        not null comment '电闸ID',
    material_line_id bigint        null comment '所属料线'
)
    comment '电闸路由' charset = utf8mb4;

create table sys_login_info
(
    id             bigint auto_increment comment '访问ID'
        primary key,
    company_id     bigint                                   null comment '公司id',
    pig_farm_id    bigint                                   null comment '猪场id',
    user_id        bigint                                   null comment '用户id',
    login_name     varchar(50)  default ''                  null comment '登录账号',
    client_type    varchar(10)                              null comment '客户端类型',
    ip             varchar(50)  default ''                  null comment '登录IP地址',
    login_location varchar(255) default ''                  null comment '登录地点',
    browser        varchar(50)  default ''                  null comment '浏览器类型',
    os             varchar(50)  default ''                  null comment '操作系统',
    status         int                                      null comment '登录状态（1 成功 0 失败）',
    msg            varchar(255) default ''                  null comment '提示消息',
    login_time     datetime                                 null comment '访问时间',
    del            char         default '0'                 not null comment '删除：0-未删除，1-已删除',
    remark         varchar(500)                             null comment '备注',
    create_by      bigint                                   null comment '创建人id',
    update_by      bigint                                   null comment '更新人id',
    create_time    datetime     default current_timestamp() null comment '创建时间',
    update_time    datetime                                 null on update current_timestamp() comment '更新时间'
)
    comment '系统访问记录' charset = utf8mb4;

create index company_id
    on sys_login_info (company_id, pig_farm_id);

create index login_name
    on sys_login_info (login_name);

create table sys_menu
(
    id           bigint auto_increment comment '菜单ID'
        primary key,
    module_id    int                                      null comment '菜单所属模块，必填，没有模块设0',
    menu_name    varchar(255)                             not null comment '菜单名称',
    parent_id    bigint       default 0                   null comment '父菜单ID',
    order_num    int          default 0                   null comment '显示顺序',
    url          varchar(255) default '#'                 null comment '请求地址',
    target       varchar(255) default ''                  null comment '打开方式（menuItem页签 menuBlank新窗口）',
    hide_in_menu int(1)       default 0                   null comment '前端菜单隐藏',
    menu_type    char         default ''                  null comment '菜单类型（C目录 M菜单 B按钮）',
    visible      int                                      null comment '菜单状态（1 显示 0 隐藏）',
    perms        varchar(100)                             null comment '权限标识',
    icon         varchar(255) default '#'                 null comment '菜单图标',
    del          char         default '0'                 null comment '删除：0-未删除，1-已删除',
    remark       varchar(500) default ''                  null comment '备注',
    create_by    bigint                                   null comment '创建者',
    update_by    bigint                                   null comment '更新者',
    create_time  datetime     default current_timestamp() null comment '创建时间',
    update_time  datetime                                 null on update current_timestamp() comment '更新时间'
)
    comment '菜单权限表' charset = utf8mb4;

create table sys_operation_log
(
    id             bigint auto_increment comment '日志主键'
        primary key,
    company_id     bigint                                    null comment '公司id',
    pig_farm_id    bigint                                    null comment '猪场id',
    title          varchar(64)   default ''                  null comment '模块标题',
    business_type  varchar(64)   default '0'                 null comment '业务类型',
    method         varchar(100)  default ''                  null comment '方法名称',
    request_method varchar(10)   default ''                  null comment '请求方式',
    oper_name      varchar(64)   default ''                  null comment '操作人员',
    dept_name      varchar(64)   default ''                  null comment '部门名称',
    oper_url       varchar(255)  default ''                  null comment '请求URL',
    oper_ip        varchar(50)   default ''                  null comment '主机地址',
    oper_location  varchar(255)  default ''                  null comment '操作地点',
    oper_param     varchar(2000) default ''                  null comment '请求参数',
    json_result    text                                      null comment '返回参数',
    client_type    varchar(10)                               null comment '客户端类型：Web/Android/iOS',
    status         int           default 0                   null comment '操作状态（1 正常 0 异常）',
    error_msg      varchar(2000) default ''                  null comment '错误消息',
    oper_time      datetime                                  null comment '操作时间',
    del            char          default '0'                 null comment '删除：0-未删除，1-已删除',
    remark         varchar(500)                              null comment '备注',
    create_by      bigint                                    null comment '创建人id',
    update_by      bigint                                    null comment '更新人id',
    create_time    datetime      default current_timestamp() null comment '创建时间',
    update_time    datetime                                  null on update current_timestamp() comment '更新时间'
)
    comment '操作日志记录' charset = utf8mb4;

create index company_id
    on sys_operation_log (company_id, pig_farm_id);

create table sys_production_tips
(
    id          bigint auto_increment
        primary key,
    company_id  bigint                               not null comment '公司id',
    type        int                                  not null comment '1断奶待配提示,2返情待配提示,3流产待配提示,4阴性待配提示,5首次妊检提示,6,二次妊检提示	,7分娩提示，8断奶提示，9后备初配提示',
    merit       varchar(50)                          not null comment '判断指标',
    days        int                                  not null comment '天数',
    status      int      default 1                   not null comment '1打开，2关闭',
    del         char     default '0'                 not null comment '0未删除，1已删除',
    remark      varchar(500)                         null comment '备注',
    create_by   bigint                               null comment '创建人',
    update_by   bigint                               null comment '修改人',
    create_time datetime default current_timestamp() not null comment '创建时间',
    update_time datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '生产提示' charset = utf8mb4;

create index company_id
    on sys_production_tips (company_id);

create table sys_role
(
    id          bigint auto_increment comment '角色ID'
        primary key,
    company_id  bigint                               null comment '公司id',
    role_name   varchar(30)                          not null comment '角色名称',
    role_key    varchar(100)                         null comment '角色权限字符串',
    role_sort   int      default 1                   not null comment '显示顺序',
    data_scope  int      default 1                   null comment '数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）',
    status      int      default 1                   not null comment '角色状态（1 正常  0 停用）',
    del         char     default '0'                 null comment '删除标志（0 正常 1 删除）',
    remark      varchar(500)                         null comment '备注',
    create_by   bigint                               null comment '创建者',
    update_by   bigint                               null comment '更新者',
    create_time datetime default current_timestamp() not null comment '创建时间',
    update_time datetime                             null on update current_timestamp() comment '更新时间'
)
    comment '角色信息表' charset = utf8mb4;

create index company_id
    on sys_role (company_id, role_key);

create table sys_role_menu
(
    role_id bigint not null comment '角色ID',
    menu_id bigint not null comment '菜单ID',
    primary key (role_id, menu_id)
)
    comment '角色和菜单关联表' charset = utf8mb4;

create table sys_user
(
    id             bigint auto_increment comment '用户ID'
        primary key,
    company_id     bigint                                   not null comment '公司id',
    login_name     varchar(64)                              not null comment '登录账号',
    nick_name      varchar(64)  default ''                  null comment '用户昵称',
    real_name      varchar(64)                              null comment '真实姓名',
    email          varchar(64)  default ''                  null comment '用户邮箱',
    phone          varchar(11)  default ''                  null comment '手机号码',
    sex            int(1)       default 2                   null comment '用户性别（0男 1女 2未知）',
    avatar         varchar(255) default ''                  null comment '头像路径',
    password       varchar(255) default ''                  not null comment '密码',
    status         int(1)       default 1                   null comment '帐号状态（1 正常 0 停用）',
    user_role_type varchar(20)                              null comment '用户角色类型：SUPER_ADMIN、COMPANY_ADMIN、COMMON_USER',
    del            char         default '0'                 null comment '删除标志（0代表存在 1代表删除）',
    remark         varchar(500)                             null comment '备注',
    create_by      bigint                                   null comment '创建者',
    update_by      bigint                                   null comment '更新者',
    create_time    datetime     default current_timestamp() null comment '创建时间',
    update_time    datetime                                 null on update current_timestamp() comment '更新时间',
    rid            varchar(64)                              null comment '用户设备id(极光推送)'
)
    comment '用户信息表' charset = utf8mb4;

create index login_name
    on sys_user (company_id, login_name);

create table sys_user_farm
(
    user_id    bigint            not null comment '角色id',
    farm_id    bigint            not null comment '猪场id',
    is_default tinyint default 0 not null comment '是否为默认',
    primary key (user_id, farm_id)
)
    comment '用户猪场' collate = utf8mb4_unicode_ci;

create table sys_user_role
(
    user_id bigint not null comment '用户ID',
    role_id bigint not null comment '角色ID',
    primary key (user_id, role_id)
)
    comment '用户和角色关联表' charset = utf8mb4;

create table version
(
    id          bigint auto_increment
        primary key,
    version     varchar(50)                          not null comment '版本号',
    code        int                                  null comment '审核版本号',
    tips        varchar(1024)                        not null comment '更新提示语',
    download    varchar(255)                         not null comment '下载地址',
    os          int                                  not null comment '系统：1 安卓，2 iOS',
    update_type int                                  not null comment '更新类型：0 提示更新，1 强制更新',
    status      int                                  not null comment '状态：0 下架，1 上架',
    audit       int                                  null comment '当前版本是非在审核：0 否，1 是',
    del         char                                 not null comment '0 未删除，1 已删除',
    remark      varchar(500)                         null comment '备注',
    create_by   bigint                               null comment '创建人id',
    update_by   bigint                               null comment '更新人id',
    create_time datetime default current_timestamp() not null comment '创建时间',
    update_time datetime                             null on update current_timestamp() comment '更新时间'
)
    comment 'app版本管理表' charset = utf8mb4;

