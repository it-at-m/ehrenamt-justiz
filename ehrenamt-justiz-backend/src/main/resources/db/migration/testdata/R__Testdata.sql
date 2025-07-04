
INSERT INTO konfiguration (id, ehrenamtjustizart, bezeichnung, aktiv, amtsperiodevon, amtsperiodebis, altervon, alterbis, staatsangehoerigkeit, wohnsitz) VALUES('2f3d0a19-dc99-40bc-af55-0c72bf8bf82f', 'SCHOEFFEN', '2029 - 2033', TRUE, '2029-01-01', '2033-12-31', 25, 120, 'deutsch', 'München');
INSERT INTO konfiguration (id, ehrenamtjustizart, bezeichnung, aktiv, amtsperiodevon, amtsperiodebis, altervon, alterbis, staatsangehoerigkeit, wohnsitz) VALUES('2f3d0a19-dc99-40bc-af55-0c72bf8bf82e', 'VERWALTUNGSRICHTER', '2030 - 2035', FALSE, '2030-04-01', '2035-03-31', 25, 120, 'deutsch', 'München');

INSERT INTO Person (id, konfigurationid, familienname, vorname, geburtsdatum, geschlecht, ewoid, akademischergrad, geburtsort, geburtsland, familienstand, strasse, hausnummer, postleitzahl, ort, inmuenchenseit, wohnungsstatus, neuervorschlag, onlinebewerbung, warbereitstaetigals, warbereitstaetigalsvorvorperiode, status)
VALUES('2f3d0a19-dc99-40bc-af55-0c72bf8bf82f', '2f3d0a19-dc99-40bc-af55-0c72bf8bf82f',  'Huber', 'Gabi', '1981-01-01', 'WEIBLICH', '4714', 'Dr.', 'Bremen', 'Deutschland', 'ledig', 'Bachstr.', '1', '80630', 'München', '1999-01-01', 'HAUPTWOHNUNG', true, false, false, false, 'BEWERBUNG');

INSERT INTO Staatsangehoerigkeit (personid, staatsangehoerigkeit_text, lfdnr)
VALUES('2f3d0a19-dc99-40bc-af55-0c72bf8bf82f', 'deutsch', 0);
INSERT INTO Staatsangehoerigkeit (personid, staatsangehoerigkeit_text, lfdnr)
VALUES('2f3d0a19-dc99-40bc-af55-0c72bf8bf82f', 'italienisch', 1);
INSERT INTO Auskunftssperre (personid, sperrentyp, lfdnr)
VALUES('2f3d0a19-dc99-40bc-af55-0c72bf8bf82f', 'S', 0);
INSERT INTO KonfliktFeld (personid, person_attribut, lfdnr)
VALUES('2f3d0a19-dc99-40bc-af55-0c72bf8bf82f', 'strasse', 0);
INSERT INTO KonfliktFeld (personid, person_attribut, lfdnr)
VALUES('2f3d0a19-dc99-40bc-af55-0c72bf8bf82f', 'vorname', 1);
DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM pg_extension WHERE extname = 'pgcrypto') THEN
        CREATE EXTENSION pgcrypto;
END IF;
END $$;
do $$
begin
for i in 1..1000 BY 3 loop
        with personid as (
            INSERT INTO Person (id, konfigurationid, familienname, vorname, geburtsdatum, geschlecht, ewoid, akademischergrad, geburtsort, geburtsland, familienstand, strasse, hausnummer, postleitzahl, ort, inmuenchenseit, wohnungsstatus, neuervorschlag, onlinebewerbung, warbereitstaetigals, warbereitstaetigalsvorvorperiode, status)
            VALUES(gen_random_uuid(), '2f3d0a19-dc99-40bc-af55-0c72bf8bf82f',  'Huber', 'Petra', '1981-01-01', 'WEIBLICH', CEIL(random() * 10000000), 'Dr.', 'Hamburg', 'Deutschland', 'ledig', 'Ludwigstr.', '1', '80634', 'München', '1999-01-01', 'HAUPTWOHNUNG', true, false, false, false, 'BEWERBUNG')
            RETURNING id
        )
        INSERT INTO Staatsangehoerigkeit (personid, staatsangehoerigkeit_text, lfdnr)
        select id, 'deutsch', 0 from personid;

        i=i+1;
        with personid as (
            INSERT INTO Person (id, konfigurationid, familienname, vorname, geburtsdatum, geschlecht, ewoid, akademischergrad, geburtsort, geburtsland, familienstand, strasse, hausnummer, postleitzahl, ort, inmuenchenseit, wohnungsstatus, neuervorschlag, onlinebewerbung, warbereitstaetigals, warbereitstaetigalsvorvorperiode, status)
            VALUES(gen_random_uuid(), '2f3d0a19-dc99-40bc-af55-0c72bf8bf82f', 'Müller', 'Hans', '1982-01-01', 'MAENNLICH', CEIL(random() * 10000000), 'Dr.', 'München', 'Deutschland', 'ledig', 'Berliner Str.', '7', '80635', 'München', '1999-01-01', 'HAUPTWOHNUNG', true, false, false, false, 'BEWERBUNG')
            RETURNING id
        )
        INSERT INTO Staatsangehoerigkeit (personid, staatsangehoerigkeit_text, lfdnr)
        select id, 'deutsch', 0 from personid;

        i=i+1;
        with personid as (
            INSERT INTO Person (id, konfigurationid, familienname, vorname, geburtsdatum, geschlecht, ewoid, akademischergrad, geburtsort, geburtsland, familienstand, strasse, hausnummer, postleitzahl, ort, inmuenchenseit, wohnungsstatus, neuervorschlag, onlinebewerbung, warbereitstaetigals, warbereitstaetigalsvorvorperiode, status)
            VALUES(gen_random_uuid(), '2f3d0a19-dc99-40bc-af55-0c72bf8bf82f', 'Bauer', 'Sonja', '1987-01-01', 'WEIBLICH', CEIL(random() * 10000000), 'Prof.', 'Stuttgart', 'Deutschland', 'ledig', 'Talstr.', '1', '80634', 'München', '1999-01-01', 'HAUPTWOHNUNG', true, false, true, true, 'BEWERBUNG')
            RETURNING id
        )
        INSERT INTO Staatsangehoerigkeit (personid, staatsangehoerigkeit_text, lfdnr)
        select id, 'deutsch', 0 from personid;
end loop;
end; $$

