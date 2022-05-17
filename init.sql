drop table if exists xuser;
drop table if exists xhollow;
drop table if exists xhattitude;
drop table if exists xreplyhollow;

create table xuser
(
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    wechat_id varchar (20),
    username varchar (20),
    phone varchar (11),
    school INT,
    hollow_name varchar(20),
    mbti MEDIUMINT,
    image varchar(100),
    PRIMARY KEY (user_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table xhollow
(
    time DATETIME,
    content TEXT,
    under_post_id BIGINT,
    reply_post_id BIGINT,
    hollow_id BIGINT NOT NULL AUTO_INCREMENT,
    belong_to BIGINT,
    support_num INT,
    comfort_num INT,
    against_num INT,
    PRIMARY KEY (hollow_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table xhattitude
(
    user_id BIGINT,
    hollow_id BIGINT,
    support_attitude MEDIUMINT,
    comfort_attitude MEDIUMINT,
    against_attitude MEDIUMINT
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table xreplyhollow
(
    user_id BIGINT,
    hollow_id BIGINT,
    PRIMARY KEY (hollow_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;