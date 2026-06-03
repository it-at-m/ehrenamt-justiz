export default interface PersonenTableData {
  id: string;
  familienname: string;
  vorname: string;
  geburtsdatum: string;
  konfliktfeld: string[];
  auskunftssperre: string[];
  derzeitausgeuebterberuf: string;
  arbeitgeber: string;
  mailadresse: string;
  ausgeuebteehrenaemter: string;
  dateiVerfassungstreue: boolean;
  status: string;
}
