drop table if exists xuser;
drop table if exists xhollow;
drop table if exists xhattitude;
drop table if exists xreplyhollow;
drop table if exists xtv;
drop table if exists xtvall;
drop table if exists xnovel;
drop table if exists xnovelall;
drop table if exists novelattitude;
drop table if exists movieattitude;
drop table if exists tvattitude;
drop table if exists xgroup;
drop table if exists xgattitude;
drop table if exists map;

create table xuser
(
    user_id     BIGINT NOT NULL AUTO_INCREMENT,
    wechat_id   varchar(40),
    username    varchar(20),
    phone       varchar(11),
    school      INT,
    hollow_name varchar(20),
    mbti        MEDIUMINT,
    image       varchar(100),
    competence  INT,
    PRIMARY KEY (user_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table xhollow
(
    time          DATETIME,
    content       TEXT,
    under_post_id BIGINT,
    reply_post_id BIGINT,
    hollow_id     BIGINT NOT NULL AUTO_INCREMENT,
    belong_to     BIGINT,
    support_num   INT,
    comfort_num   INT,
    against_num   INT,
    PRIMARY KEY (hollow_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

create table xhattitude
(
    user_id          BIGINT,
    hollow_id        BIGINT,
    support_attitude MEDIUMINT,
    comfort_attitude MEDIUMINT,
    against_attitude MEDIUMINT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

create table xreplyhollow
(
    user_id   BIGINT,
    hollow_id BIGINT,
    PRIMARY KEY (hollow_id)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

drop table if exists map;
create table map
(
    primarykey varchar(500),
    address    TEXT,
    city       varchar(500),
    dates      TEXT,
    longitude  DOUBLE,
    latitude   DOUBLE,
    wechatid   TEXT,
    kind       TEXT,
    PRIMARY KEY (primarykey)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

drop table if exists notice;
create table notice
(
    type      varchar(500),
    date      TINYTEXT,
    title     TINYTEXT,
    content   TEXT,
    wechatid  TEXT,
    publisher TINYTEXT,
    kind      TINYTEXT,
    PRIMARY KEY (type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


#xmovie table only records the current popular movies
#xmovie all table only records high score movies. If the popular movies are new high scores, update this table
drop table if exists xmovie;
drop table if exists xmovieall;
create table xmovie
(
    name             varchar(255),
    crawltime        TINYTEXT,
    ranks            DOUBLE,
    info             TEXT,
    description      TEXT,
    detailpage       TEXT,
    image            TEXT,
    type             varchar(255),
    myattitude       INT,
    recommendtotal   Int,
    unrecommendtotal Int,
    intj             Int,
    intp             Int,
    entj             Int,
    entp             Int,
    infj             Int,
    infp             Int,
    enfj             Int,
    enfp             Int,
    istj             Int,
    isfj             Int,
    estj             Int,
    esfj             Int,
    istp             Int,
    isfp             Int,
    estp             Int,
    esfp             Int,
    unintj           Int,
    unintp           Int,
    unentj           Int,
    unentp           Int,
    uninfj           Int,
    uninfp           Int,
    unenfj           Int,
    unenfp           Int,
    unistj           Int,
    unisfj           Int,
    unestj           Int,
    unesfj           Int,
    unistp           Int,
    unisfp           Int,
    unestp           Int,
    unesfp           Int,
    PRIMARY KEY (type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


create table xmovieall
(
    name             varchar(255),
    crawltime        TINYTEXT,
    ranks            DOUBLE,
    info             TEXT,
    description      TEXT,
    detailpage       TEXT,
    image            TEXT,
    type             varchar(255),
    recommendtotal   Int,
    unrecommendtotal Int,
    intj             Int,
    intp             Int,
    entj             Int,
    entp             Int,
    infj             Int,
    infp             Int,
    enfj             Int,
    enfp             Int,
    istj             Int,
    isfj             Int,
    estj             Int,
    esfj             Int,
    istp             Int,
    isfp             Int,
    estp             Int,
    esfp             Int,
    unintj           Int,
    unintp           Int,
    unentj           Int,
    unentp           Int,
    uninfj           Int,
    uninfp           Int,
    unenfj           Int,
    unenfp           Int,
    unistj           Int,
    unisfj           Int,
    unestj           Int,
    unesfj           Int,
    unistp           Int,
    unisfp           Int,
    unestp           Int,
    unesfp           Int,
    PRIMARY KEY (type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

create table xgroup
(
    group_id BIGINT NOT NULL AUTO_INCREMENT,
    post_id BIGINT,
    name varchar(20),
    number varchar(20),
    qr_code varchar(100),
    introduction TINYTEXT,
    report_num INT,
    PRIMARY KEY(group_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8;

create table xgattitude
(
    user_id BIGINT,
    group_id BIGINT,
    report_attitude MEDIUMINT,
    report_text TINYTEXT
)ENGINE=InnoDB DEFAULT CHARSET=utf8;



# xnovel table only records the latest list details
# xnovelall table only records the recommendation information of all novels that have appeared
create table xnovel
(
    type             varchar(255),
    choice           TINYTEXT,
    crawltime        TINYTEXT,
    ranks            TINYINT,
    name             TINYTEXT,
    author           TINYTEXT,
    novelurl         TINYTEXT,
    image            TINYTEXT,
    category         TINYTEXT,
    subcategory      TINYTEXT,
    completionstatus TINYTEXT,
    updatedchapter   TINYTEXT,
    myattitude       INT,
    introduction     TEXT,
    recommendtotal   Int,
    unrecommendtotal Int,
    intj             Int,
    intp             Int,
    entj             Int,
    entp             Int,
    infj             Int,
    infp             Int,
    enfj             Int,
    enfp             Int,
    istj             Int,
    isfj             Int,
    estj             Int,
    esfj             Int,
    istp             Int,
    isfp             Int,
    estp             Int,
    esfp             Int,
    unintj           Int,
    unintp           Int,
    unentj           Int,
    unentp           Int,
    uninfj           Int,
    uninfp           Int,
    unenfj           Int,
    unenfp           Int,
    unistj           Int,
    unisfj           Int,
    unestj           Int,
    unesfj           Int,
    unistp           Int,
    unisfp           Int,
    unestp           Int,
    unesfp           Int,
    PRIMARY KEY (type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

create table xnovelall
(
    type             varchar(255),
    crawltime        TINYTEXT,
    name             TINYTEXT,
    author           TINYTEXT,
    novelurl         TINYTEXT,
    image            TINYTEXT,
    category         TINYTEXT,
    subcategory      TINYTEXT,
    completionstatus TINYTEXT,
    updatedchapter   TINYTEXT,
    introduction     TEXT,
    recommendtotal   Int,
    unrecommendtotal Int,
    intj             Int,
    intp             Int,
    entj             Int,
    entp             Int,
    infj             Int,
    infp             Int,
    enfj             Int,
    enfp             Int,
    istj             Int,
    isfj             Int,
    estj             Int,
    esfj             Int,
    istp             Int,
    isfp             Int,
    estp             Int,
    esfp             Int,
    unintj           Int,
    unintp           Int,
    unentj           Int,
    unentp           Int,
    uninfj           Int,
    uninfp           Int,
    unenfj           Int,
    unenfp           Int,
    unistj           Int,
    unisfj           Int,
    unestj           Int,
    unesfj           Int,
    unistp           Int,
    unisfp           Int,
    unestp           Int,
    unesfp           Int,
    PRIMARY KEY (type)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

create table xtv
(
    name             varchar(255),
    crawltime        TINYTEXT,
    type             TINYTEXT,
    ranks            DOUBLE,
    detailpage       TINYTEXT,
    image            TINYTEXT,
    info             TEXT,
    description      TEXT,
    myattitude       INT,
    recommendtotal   Int,
    unrecommendtotal Int,
    intj             Int,
    intp             Int,
    entj             Int,
    entp             Int,
    infj             Int,
    infp             Int,
    enfj             Int,
    enfp             Int,
    istj             Int,
    isfj             Int,
    estj             Int,
    esfj             Int,
    istp             Int,
    isfp             Int,
    estp             Int,
    esfp             Int,
    unintj           Int,
    unintp           Int,
    unentj           Int,
    unentp           Int,
    uninfj           Int,
    uninfp           Int,
    unenfj           Int,
    unenfp           Int,
    unistj           Int,
    unisfj           Int,
    unestj           Int,
    unesfj           Int,
    unistp           Int,
    unisfp           Int,
    unestp           Int,
    unesfp           Int,
    PRIMARY KEY (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

create table xtvall
(
    name             varchar(255),
    crawltime        TINYTEXT,
    type             TINYTEXT,
    ranks            DOUBLE,
    detailpage       TINYTEXT,
    image            TINYTEXT,
    info             TEXT,
    description      TEXT,
    recommendtotal   Int,
    unrecommendtotal Int,
    intj             Int,
    intp             Int,
    entj             Int,
    entp             Int,
    infj             Int,
    infp             Int,
    enfj             Int,
    enfp             Int,
    istj             Int,
    isfj             Int,
    estj             Int,
    esfj             Int,
    istp             Int,
    isfp             Int,
    estp             Int,
    esfp             Int,
    unintj           Int,
    unintp           Int,
    unentj           Int,
    unentp           Int,
    uninfj           Int,
    uninfp           Int,
    unenfj           Int,
    unenfp           Int,
    unistj           Int,
    unisfj           Int,
    unestj           Int,
    unesfj           Int,
    unistp           Int,
    unisfp           Int,
    unestp           Int,
    unesfp           Int,
    PRIMARY KEY (name)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;


create table novelattitude
(
    wechatid TEXT,
    name     TINYTEXT,
    author   TINYTEXT,
    attitude TINYTEXT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

create table movieattitude
(
    wechatid TEXT,
    name     TINYTEXT,
    ranks    DOUBLE,
    attitude TINYTEXT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

create table tvattitude
(
    wechatid TEXT,
    name     TINYTEXT,
    ranks    DOUBLE,
    attitude TINYTEXT
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;

