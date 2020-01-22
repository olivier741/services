/*
   DDL of Receiver
*/
CREATE TABLE command_request
(   command_id                     INTEGER UNIQUE NOT NULL,
    command_code                   VARCHAR(50) UNIQUE NOT NULL,
    param_pattern_separate         VARCHAR(10),
    channel                        VARCHAR(15) NOT NULL);
    
ALTER TABLE command_request ADD CONSTRAINT cd_pk PRIMARY KEY (command_id);


CREATE TABLE comamd_param
(   param_id                       INTEGER UNIQUE NOT NULL,
    param_name                     VARCHAR(100) UNIQUE NOT NULL,
    param_length                   INTEGER NOT NULL,
    param_pattern                  VARCHAR(1024) NOT NULL);
    

ALTER TABLE comamd_param ADD CONSTRAINT pm_pk PRIMARY KEY (param_id);    

CREATE TABLE command_action
(   action_id                      INTEGER UNIQUE NOT NULL,
    action_type                    VARCHAR(100),
    description                    VARCHAR(1000));
    

ALTER TABLE command_action ADD CONSTRAINT ct_pk PRIMARY KEY (action_id);



CREATE TABLE command_syntax
(   syntax_id                      INTEGER UNIQUE NOT NULL,
    command_id                     INTEGER NOT NULL,
    param_id					   INTEGER,
    action_id                      INTEGER,
	product_name                   VARCHAR(20) NOT NULL);
    

ALTER TABLE command_syntax ADD CONSTRAINT sx_pk PRIMARY KEY (syntax_id);

-- Foreign Key
ALTER TABLE command_syntax ADD CONSTRAINT sx_cd_fk FOREIGN KEY (command_id) REFERENCES command_request (command_id);

ALTER TABLE command_syntax ADD CONSTRAINT sx_pm_fk FOREIGN KEY (param_id) REFERENCES comamd_param (param_id);

ALTER TABLE command_syntax ADD CONSTRAINT sx_ac_fk FOREIGN KEY (action_id) REFERENCES command_action (action_id);



CREATE TABLE mo_his(
	mo_his_id                      INTEGER UNIQUE NOT NULL,
    msisdn                         VARCHAR(25),
	content_receive				   VARCHAR(1024),
    channel                        VARCHAR(25),
    command                        VARCHAR(100),
    param                          VARCHAR(1024),
    receive_time                   DATE,
    action_type                    VARCHAR(25),
    process_time                   DATE,
    err_code                       VARCHAR(100),
    fee                            INTEGER,
    Processing_Bolt				   VARCHAR(25)
    );



-- Constraints for MO_HIS

ALTER TABLE mo_his ADD CONSTRAINT mohs1_pk_4 PRIMARY KEY (mo_his_id);
ALTER TABLE mo_his CHANGE COLUMN `mo_his_id` `mo_his_id` INT(11)  AUTO_INCREMENT ;


CREATE TABLE mt_his(
	mt_his_id                      INTEGER UNIQUE NOT NULL,
    msisdn                         VARCHAR(25),
	content_sent				   VARCHAR(1024),
    channel                        VARCHAR(25),
    sent_time                      DATE
    );
ALTER TABLE mt_his ADD CONSTRAINT mths1_pk_4 PRIMARY KEY (mt_his_id);    
    

CREATE TABLE product_config (
    product_id					   INTEGER UNIQUE NOT NULL,
	product_name                   VARCHAR(20) UNIQUE NOT NULL,
    reg_fee                        INTEGER,
    start_time                     Date,    	 -- 2019-04-16 23:00:01-07:00:00  this promotion will launch  the 2019-04-16 at 11PM 
    end_time                       DATE,         -- 2050-04-16 23:00:01-07:00:00  this promotion will end  the 2050-04-16 at 11PM 
    register_time				   VARCHAR(50),  -- 23:00:01-07:00:00   frame time in one day where customer allow to register
    restrict_product	           VARCHAR(100),
    register_day                   VARCHAR(10),  -- 'Day allow register: 0-Sunday, 1-Monday, 2-Tuesday, 3-Wednesday, ... not information mean registration every day'
    isextend					   INTEGER DEFAULT 1,		 -- 0 
    extend_fee                     INTEGER,
    list_allow                     VARCHAR(50),  --  name of table which content the list of phone number allow to register on that offer
    pending_time				   VARCHAR(10),  --  D30 mean customer pending 30 Day on this offert, he is cancel (system will not try to extend) 
    validity					   VARCHAR(10)   --  D1 mean customer must have this offer for one Day
    );

ALTER TABLE product_config ADD CONSTRAINT prod_pk PRIMARY KEY (product_id);



create table notification_config(
   notification_id 				INTEGER UNIQUE NOT NULL,
   language						VARCHAR(100),
   param_name 					VARCHAR(500),
   param_value					VARCHAR(2000)
   );

ALTER TABLE notification_config ADD CONSTRAINT nof_pk PRIMARY KEY (notification_id);


CREATE TABLE register (
	id                             INTEGER UNIQUE NOT NULL,
    msisdn                         VARCHAR(15) NOT NULL,
    product_name                   VARCHAR(20),
    fee							   INTEGER DEFAULT 0 NOT NULL,
    register_time                  DATE,
	expire_time                    DATE,    		-- end time of product
    start_time                     DATE,    		-- start time of product
    end_time                       DATE,    		-- end_time of product_name
    paid_time                      DATE  NOT NULL,  -- time of registration
    delete_time					   DATE,
    validity					   VARCHAR(10),
    status                         INTEGER DEFAULT 1 NOT NULL,
    auto_extend                    INTEGER DEFAULT 1 NOT NULL,
    restrict_product               VARCHAR(100)
    );
    
ALTER TABLE register ADD CONSTRAINT reg_pk PRIMARY KEY (id);    

-- ###################################################



-- get all the command and syntax

select a.command_id,
	   a.command_code,
       a.param_pattern_separate,
       a.channel,
       b.product_name,
       b.syntax_id,
       c.action_id,
       c.action_type,
       c.description,
       d.param_id,
       d.param_name,
       d.param_length,
       d.param_pattern
from command_request a 
left join command_syntax b on a.command_id=b.command_id
left join command_action c on b.action_id=c.action_id
left join comamd_param d on b.param_id=d.param_id;


-- get all command configured

SELECT command_id,command_code, param_pattern_separate, channel FROM command_request ;


--  get all parameter 

select param_id,param_name,param_length,param_pattern from comamd_param;

-- insert in mo_his
insert into mo_his (msisdn,channel,command,param,receive_time,action_type,process_time,err_code,fee,Processing_Bolt) values (?,?,?,?,?,?,sysdate(),?,?,?);

-- insert notification

insert into notification_config (param_name,param_value) values ('RECEIVER-WRONG-SYNTAX','Commande Erroné, le service sollicité n''existe pas');
-- get product config

select product_id,product_name,reg_fee,register_time,refuse_offer,register_day,extend_fee,list_allow,pending_time,validity from product_config order by product_id;

-- insert register

insert into register (msisdn,product_name,start_time,expire_time,end_time,paid_time,validity,status,auto_extend,restrict_product) values (?,?,?,?,?,sysdate(),?,?,?,?);

-- select current register package

select id,msisdn,product_name,start_time,expire_time,end_time,paid_time,validity,status,auto_extend,restrict_product from register where status = 1 and msisdn = ? order by id;

--  check in list allow 
select msisdn from list_allow_value where msisdn = ?;

DELETE FROM register where msisdn = ? and product_name = ?;

select id,msisdn,product_name,start_time,expire_time,end_time,paid_time,validity,status,auto_extend,restrict_product from register where status = 1 and msisdn = ? ;

-- get notification

select notification_id, param_name, param_value from notification_config;

-- insert in mt_his

insert into mt_his (msisdn,content_sent,channel,sent_time) values (?,?,?,?);


-- ####################################################################################


insert into Product(product_code,reg_fee,restrict_product, register_day,start_date,end_date,start_reg_time,end_reg_time,isextend,extend_fee,list_allow,validity,pending_duration)
			Values ('CAN1',50,null,null,'2019/05/01','2019/07/01',null,null,1,50,null,'D30','D30');

insert into Action(action_name, action_type,action_description,product_id) values ('Registration','REG',null,1);


insert into Command (command_code,split_separator,channel,action_id) values ('CAN1',null,'9696',1);

insert into PARAM (param_name, param_pattern, param_length,paramDescription,command_id) values ('param_can','INFO|GOAL|TEAM',1,null,1); 