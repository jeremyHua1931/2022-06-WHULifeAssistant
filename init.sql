drop table if exists xuser;
drop table if exists xhollow;
drop table if exists xhattitude;
drop table if exists xreplyhollow;
drop table if exists xmovie;
drop table if exists xtv;
drop table if exists xnovel;


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

create table xmovie(
    name varchar(255),
    ranks DOUBLE,
    info TEXT,
    description TEXT,
    detailpage TEXT,
    image TINYTEXT,
    type TINYTEXT,
    PRIMARY KEY (name)
);

create table xnovel
(
    type varchar(255),
    choice TINYTEXT,
    ranks TINYINT,
    name TINYTEXT,
    author TINYTEXT,
    novelurl TINYTEXT,
    image TINYTEXT,
    category TINYTEXT,
    subcategory TINYTEXT,
    completionstatus TINYTEXT,
    updatedchapter TINYTEXT,
    introduction TEXT,
    kind TINYTEXT,
    PRIMARY KEY (type)
);


create table xreplyhollow
(
    user_id BIGINT,
    hollow_id BIGINT,
    PRIMARY KEY (hollow_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;


create table xtv
(
    name varchar(255),
    type TINYTEXT,
    ranks DOUBLE,
    detailpage TINYTEXT,
    image TINYTEXT,
    info TEXT,
    description TEXT,
    PRIMARY KEY (name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;
