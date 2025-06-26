/**
 * KonfigurationData is a structure that holds the data of the active configuration
 */
export default interface KonfigurationData {
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
