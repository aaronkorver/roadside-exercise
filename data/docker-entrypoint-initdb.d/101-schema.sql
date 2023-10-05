create table temp1
(
    id        text,
    esp_name  text,
    address   text,
    city      text,
    county    text,
    state     text,
    zipcode   text,
    latitude  text,
    longitude text,
    esp_score text
);

alter table temp1
    owner to myuser;

create table assistant
(
    id       uuid default gen_random_uuid() not null
        constraint assistant_pk
            primary key,
    name     text,
    location geography(Point, 4326),
    lat text,
    lon text
);

alter table assistant
    owner to myuser;

create table assistant_transactions (
                                       id uuid primary key default gen_random_uuid() not null,
                                       assistant_id uuid not null references assistant(id),
                                       customer_id uuid not null,
                                       to_state text not null,
                                       most_recent boolean not null,
                                       created_at timestamptz not null default now(),
                                       updated_at timestamptz not null default now()
);

alter table assistant_transactions
    owner to myuser;

create unique index idx_assistant_transactions_by_parent_most_recent
    on assistant_transactions
        using btree(assistant_id, most_recent)
    where most_recent;