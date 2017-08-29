delete from T_USER_ROLE;
delete from T_ROLE;
delete from T_USER;

insert into T_ROLE values(1, 'ROLE_USER', '일반 롤');
insert into T_ROLE values(2, 'ROLE_ADMIN', '관리 롤');

insert into T_USER values(1, 'user', '$2a$10$nodSZsM7Lyi34l3/YEg61uTVDRIgH.DkM/WF4AM0BKTCGINBOnFOm', '사용자1', '개발부');
insert into T_USER values(2, 'admin', '$2a$10$0VRu6ZC4zcuXZiS34AaPF.gDcbq25Z5lX01gnf97iBNdl4WeCbCXC', '사용자2', '관리부');

insert into T_USER_ROLE values(1, 1);
insert into T_USER_ROLE values(2, 2);
