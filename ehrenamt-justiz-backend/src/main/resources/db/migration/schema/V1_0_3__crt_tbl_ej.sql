-- Additional data of person: warbereitstaetigalsvorvorperiode
alter table Person add column if not exists warbereitstaetigalsvorvorperiode boolean DEFAULT FALSE;