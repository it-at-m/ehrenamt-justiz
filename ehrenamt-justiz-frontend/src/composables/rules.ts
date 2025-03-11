/**
 * rules stellt generelle Rules für die Vuetify-Validierung bereit.
 *
 * Die Texte für Validierungsfehler sind deshalb allgemein und nicht auf die
 * Felder bezogen. Im Fall, dass der Text nicht passt, kann in der Komponente
 * eine extra Rule erzeugt werden (z.B. `if (typeof RULE_MAIL(value) == 'string') {...}`).
 */

import moment from "moment";

import { useGlobalSettingsStore } from "@/stores/globalsettings";

export function useRules() {
  return {
    /* eslint-disable @typescript-eslint/no-explicit-any */
    RULE_REQUIRED: (v: any): boolean | string => {
      return !!v || "Feld ist erforderlich";
    },
    /* eslint-enable @typescript-eslint/no-explicit-any */

    RULE_MAIL: (v: string): boolean | string => {
      return (
        !v ||
        /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(
          v
        ) ||
        "Valide E-Mail angeben"
      );
    },

    RULE_GEBURTSDATUM: (v: Date): boolean | string => {
      const alter = moment(
        useGlobalSettingsStore().getKonfiguration?.amtsperiodevon
      ).diff(v, "years");

      const altervon = useGlobalSettingsStore().getKonfiguration?.altervon;
      const alterbis = useGlobalSettingsStore().getKonfiguration?.alterbis;
      return (alterbis && alter > alterbis) || (altervon && alter < altervon)
        ? "Das Alter muss zwischen " +
            altervon +
            " und " +
            alterbis +
            " Jahren liegen."
        : true;
    },
    RULE_STAATSANGEHOERIGKEIT: (v: string[]): boolean | string => {
      const staatsangehoerigkeitAusKonfiguration =
        useGlobalSettingsStore().getKonfiguration?.staatsangehoerigkeit;

      return staatsangehoerigkeitAusKonfiguration &&
        v.indexOf(staatsangehoerigkeitAusKonfiguration) > -1
        ? true
        : "Die Staatsangehörigkeit enhält nicht " +
            staatsangehoerigkeitAusKonfiguration;
    },
    RULE_WOHNSITZ: (v: string): boolean | string => {
      const wohnsitzAusKonfiguration =
        useGlobalSettingsStore().getKonfiguration?.wohnsitz;

      return wohnsitzAusKonfiguration && v === wohnsitzAusKonfiguration
        ? true
        : "Kein Wohnsitz ist in " + wohnsitzAusKonfiguration;
    },
  };
}
