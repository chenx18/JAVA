create table department_log (
  id int primary key auto_increment,
  department_id int not null,
  operation varchar(50) not null,
  content varchar(255),
  create_time datetime not null
);
