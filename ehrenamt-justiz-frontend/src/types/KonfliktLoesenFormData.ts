export default interface KonfliktLoesenFormData {
  person_familienname: string;
  person_geburtsname: string;
  person_vorname: string;
  person_geburtsdatum: string;
  person_geschlecht: string;
  person_ewoid: string;
  person_akademischergrad: string;
  person_geburtsort: string;
  person_geburtsland: string;
  person_familienstand: string;
  person_staatsangehoerigkeit: string[];
  person_wohnungsgeber: string;
  person_strasse: string;
  person_hausnummer: string;
  person_appartmentnummer: string;
  person_buchstabehausnummer: string;
  person_stockwerk: string;
  person_teilnummerhausnummer: string;
  person_adresszusatz: string;
  person_konfliktfeld: string[];
  person_postleitzahl: string;
  person_ort: string;
  person_inmuenchenseit: string;
  person_wohnungsstatus: string;
  person_auskunftssperre: string[];
  person_derzeitausgeuebterberuf: string;
  person_arbeitgeber: string;
  person_telefonnummer: string;
  person_telefongesch: string;
  person_telefonmobil: string;
  person_mailadresse: string;
  person_ausgeuebteehrenaemter: string;
  person_onlinebewerbung: string;
  person_neuervorschlag: string;
  person_warbereitstaetigals: string;
  person_bewerbungvom: string;
  person_konfigurationid: string;
  person_status: string;
  person_validierungdeaktivieren: boolean;

  ewo_familienname: string;
  ewo_geburtsname: string;
  ewo_vorname: string;
  ewo_geburtsdatum: string;
  ewo_geschlecht: string;
  ewo_ewoid: string;
  ewo_akademischergrad: string;
  ewo_geburtsort: string;
  ewo_geburtsland: string;
  ewo_familienstand: string;
  ewo_staatsangehoerigkeit: string[];
  ewo_wohnungsgeber: string;
  ewo_strasse: string;
  ewo_hausnummer: string;
  ewo_appartmentnummer: string;
  ewo_buchstabehausnummer: string;
  ewo_stockwerk: string;
  ewo_teilnummerhausnummer: string;
  ewo_adresszusatz: string;
  ewo_konfliktfeld: string[];
  ewo_postleitzahl: string;
  ewo_ort: string;
  ewo_inmuenchenseit: string;
  ewo_wohnungsstatus: string;
  ewo_auskunftssperre: string[];
}
