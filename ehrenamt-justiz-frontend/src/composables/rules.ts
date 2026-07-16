/**
 * rules offers validation-checks
 *
 * The texts for validation errors are therefore general and not related to the fields.
 * If the text does not match, an extra rule can be created in the component
 * (example: `if (typeof RULE_MAIL(value) == 'string') {...}`).
 */

import moment from "moment";
import { useI18n } from "vue-i18n";

import { useGlobalSettingsStore } from "@/stores/globalsettings";

const KB_IN_BYTES = 1024;

export function useRules() {
  const { t } = useI18n();
  return {
    /* eslint-disable @typescript-eslint/no-explicit-any */
    RULE_REQUIRED: (v: any): boolean | string => {
      return !!v || t("composables.rules.required");
    },
    /* eslint-enable @typescript-eslint/no-explicit-any */

    /**
     * validate mail
     * @param v
     * @constructor
     */
    RULE_MAIL: (v: string): boolean | string => {
      return (
        !v ||
        /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(
          v
        ) ||
        t("composables.rules.mail")
      );
    },

    /**
     * validate file of Verfassungstreue
     */
    RULE_FILE_VERFASSUNGSTREUE: (v: File | undefined): boolean | string => {
      if (v == undefined) {
        return true;
      }
      const maxSize =
        useGlobalSettingsStore().getTechnischeKonfiguration
          ?.bestaetigungVerfassungstreueMaxSize ?? 0;
      const dateiEndungKorrekt = hatBestaetigungVerfassungstreueFileErlaubteEndung(
        v.name,
        useGlobalSettingsStore().getTechnischeKonfiguration
          ?.bestaetigungVerfassungstreueFileExtension
      );

      return v.size > maxSize * KB_IN_BYTES * KB_IN_BYTES
        ? t("composables.rules.anhangZuGross", { maxSize })
        : !dateiEndungKorrekt
          ? t("composables.rules.anhangKeinGueltigeDateiendung", {
              dateiTypen:
                useGlobalSettingsStore().getTechnischeKonfiguration
                  ?.bestaetigungVerfassungstreueFileExtension,
            })
          : true;
    },

    /**
     * validate birthday
     * @param v
     * @constructor
     */
    RULE_GEBURTSDATUM: (v: Date): boolean | string => {
      const alter = moment(
        useGlobalSettingsStore().getKonfiguration?.amtsperiodevon
      ).diff(v, "years");

      const altervon = useGlobalSettingsStore().getKonfiguration?.altervon;
      const alterbis = useGlobalSettingsStore().getKonfiguration?.alterbis;
      return (alterbis && alter > alterbis) || (altervon && alter < altervon)
        ? t("composables.rules.geburtsdatum", {
            alterVon: altervon,
            alterBis: alterbis,
          })
        : true;
    },

    /**
     * validate nationality
     * @param v
     * @constructor
     */
    RULE_STAATSANGEHOERIGKEIT: (v: string[]): boolean | string => {
      const staatsangehoerigkeitAusKonfiguration =
        useGlobalSettingsStore().getKonfiguration?.staatsangehoerigkeit;

      return staatsangehoerigkeitAusKonfiguration &&
        v.indexOf(staatsangehoerigkeitAusKonfiguration) > -1
        ? true
        : t("composables.rules.staatsangehoerigkeit", {
            staatsangehoerigkeitAusKonfiguration:
              staatsangehoerigkeitAusKonfiguration,
          });
    },

    /**
     * validate residence
     * @param v
     * @constructor
     */
    RULE_WOHNSITZ: (v: string): boolean | string => {
      const wohnsitzAusKonfiguration =
        useGlobalSettingsStore().getKonfiguration?.wohnsitz;

      return wohnsitzAusKonfiguration && v === wohnsitzAusKonfiguration
        ? true
        : t("composables.rules.wohnsitz", {
            wohnsitzAusKonfiguration: wohnsitzAusKonfiguration,
          });
    },

    /**
     * validate numeric value
     * @param v
     * @constructor
     */
    RULE_NUMERISCH: (v: string): boolean | string => {
      return !v || /^\d+$/.test(v) || t("composables.rules.numerisch");
    },
  };

  function hatBestaetigungVerfassungstreueFileErlaubteEndung(
    dateiname: string,
    bestaetigungVerfassungstreueFileExtension: string | undefined
  ): boolean {
    if (!bestaetigungVerfassungstreueFileExtension) {
      return false;
    }
    const endungen = bestaetigungVerfassungstreueFileExtension
      .split(",")
      .map((e) => e.trim().toLowerCase())
      .filter((e) => e.length > 0);
    const dateinameLower = dateiname.toLowerCase();
    return endungen.some((endung) => dateinameLower.endsWith(`.${endung}`));
  }
}
