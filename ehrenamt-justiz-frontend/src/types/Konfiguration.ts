export default interface Konfiguration {
  id: string | undefined;
  ehrenamtjustizart: string;
  bezeichnung: string;
  aktiv: boolean;
  amtsperiodevon: string;
  amtsperiodebis: string;
  altervon: number;
  alterbis: number;
  staatsangehoerigkeit: string;
  wohnsitz: string;
}
