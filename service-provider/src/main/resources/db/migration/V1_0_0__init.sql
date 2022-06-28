create table audio
(
    id  uuid not null,
    url text not null,
    constraint audio_pk primary key (id)
);

create table audio_characteristics
(
    id        uuid    not null,
    name      text    not null,
    is_unique boolean not null,
    value     text,
    audio_id  uuid    not null,
    constraint audio_characteristics_pk primary key (id),
    constraint audio_fk foreign key (audio_id) references audio (id)
);

create table video
(
    id  uuid not null,
    url text not null,
    constraint video_pk primary key (id)
);

create table video_characteristics
(
    id        uuid    not null,
    name      text    not null,
    is_unique boolean not null,
    value     text,
    video_id  uuid    not null,
    constraint video_characteristics_pk primary key (id),
    constraint video_fk foreign key (video_id) references video (id)
);

create table images
(
    id  uuid not null,
    url text not null,
    constraint images_pk primary key (id)
);

create table image_characteristics
(
    id        uuid    not null,
    name      text    not null,
    is_unique boolean not null,
    value     text,
    image_id  uuid    not null,
    constraint image_characteristics_pk primary key (id),
    constraint images_fk foreign key (image_id) references images (id)
);

create table users
(
    id uuid not null,
    constraint users_pk primary key (id)
);

create table user_characteristics
(
    id        uuid    not null,
    name      text    not null,
    is_unique boolean not null,
    value     text,
    user_id   uuid    not null,
    constraint user_characteristics_pk primary key (id),
    constraint users_fk foreign key (user_id) references users (id)
);
