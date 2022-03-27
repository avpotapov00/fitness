create sequence user_id_seq;
create table users (
	id bigint primary key default nextval('user_id_seq')
);

create table membership_events(
	user_id bigint references users(id),
	expiry_date date not null,
	primary key(user_id, expiry_date)
);

create type visit_type as enum ('enter', 'exit');

create table visit_events (
	user_id bigint references users(id),
	type visit_type not null,
	event_time timestamp not null,
	primary key(user_id, event_time)
);
