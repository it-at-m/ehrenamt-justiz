export default interface EWOBuergerDaten {
  id: string | undefined;
  familienname: string;
  geburtsname: string;
  vorname: string;
  geburtsdatum: string;
  geschlecht: string;
  ordnungsmerkmal: string;
  akademischergrad: string;
  geburtsort: string;
  geburtsland: string;
  familienstand: string;
  staatsangehoerigkeit: string[];
  wohnungsgeber: string;
  strasse: string;
  hausnummer: string;
  appartmentnummer: string;
  buchstabehausnummer: string;
  stockwerk: string;
  teilnummerhausnummer: string;
  adresszusatz: string;
  konfliktfeld: string[];
  postleitzahl: string;
  ort: string;
  inmuenchenseit: string;
  wohnungsstatus: string;
  auskunftssperre: string[];
  ewo_id_bereitserfasst: boolean;
}
