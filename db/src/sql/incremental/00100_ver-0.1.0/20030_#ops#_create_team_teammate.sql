create table ops.team_teammate
  ( team_id BIGINT not null
  , teammate_id BIGINT not null);

alter table ops.team_teammate add constraint FK_ttm_to_teammate foreign key (teammate_id) references ops.teammate;

alter table ops.team_teammate add constraint FK1_ttm_to_team foreign key (team_id) references ops.team;
