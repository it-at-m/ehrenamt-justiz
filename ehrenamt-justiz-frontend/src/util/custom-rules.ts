import moment from "moment/moment";

/**
 * Custom type required as CustomValidationRuleBuilder type from Vuetify is not accessible.
 */
type CustomRule<RuleArgs extends unknown[], RuleValue> = (
  ...args: RuleArgs
) => (value: RuleValue | null | undefined) => string | boolean;

/**
 * Custom type for use in {@link uniqueRule}.
 */
type UniqueRule = <T>(
  values: T[],
  initialValue?: T | null | undefined,
  err?: string
) => (value: T | null | undefined) => string | boolean;

const KB_IN_BYTES = 1024;

/**
 * Creates a validation rule that ensures a value is not below a minimum.
 *
 * @param minNumber - Minimum allowed value
 * @param exclusive - Sets `value` to be strictly greater than `minNumber`
 * @param err - Optional custom error message
 * @returns A validation function returning `true` for valid values or an error
 *   message when the minimum is not met.
 */
const minRule: CustomRule<[number, boolean?, string?], number> = (
  minNumber,
  exclusive = false,
  err
) => {
  return (v) =>
    v == null ||
    (exclusive ? v > minNumber : v >= minNumber) ||
    err ||
    (exclusive
      ? `Der Wert muss größer als ${minNumber} sein.`
      : `Der Wert muss mindestens ${minNumber} betragen.`);
};

/**
 * Creates a validation rule that ensures a value does not exceed a maximum.
 *
 * @param maxNumber - Maximum allowed value
 * @param exclusive - Sets `value` to be strictly smaller than `maxNumber`
 * @param err - Optional custom error message
 * @returns A validation function returning `true` for valid values or an error
 *   message when the maximum is exceeded.
 */
const maxRule: CustomRule<[number, boolean?, string?], number> = (
  maxNumber,
  exclusive = false,
  err?
) => {
  return (v) =>
    v == null ||
    (exclusive ? v < maxNumber : v <= maxNumber) ||
    err ||
    (exclusive
      ? `Der Wert muss kleiner als ${maxNumber} sein.`
      : `Der Wert darf höchstens ${maxNumber} betragen.`);
};

/**
 * Creates a validation rule that ensures a value is unique within a array.
 *
 * The `initialValue` is treated as valid even if it already exists in `values`.
 * This is useful when editing an existing entity, where its current value
 * naturally appears in the list of existing values.
 *
 * @param values - Values against which the value is checked for uniqueness
 * @param initialValue - The current value of the entity being edited
 * @param err - Optional custom error message
 * @returns A validation function that returns `true` when the value is unique,
 *   or an error message when it is already present.
 */
const uniqueRule: UniqueRule = (values, initialValue = undefined, err?) => {
  return (v) =>
    v == null ||
    (initialValue != null && v === initialValue) ||
    !values.includes(v) ||
    err ||
    `Der Wert ${v} ist bereits vorhanden.`;
};

/**
 * Creates a validation rule that ensures that the applicant’s age matches the configuration parameters “ageFrom” and “ageTo”
 *
 * @param amtsperiodevonConf
 * @param alterVonConf
 * @param alterBisConf
 * @param err error message
 * @returns A validation function that returns `true` when the applicant’s age is ok,
 *   or an error message.
 */
const birthDayRule: CustomRule<[string, number, number, string], Date> = (
  amtsperiodevonConf,
  alterVonConf,
  alterBisConf,
  err
) => {
  return (v) => {
    if (v == null) return err;
    const alter = moment(amtsperiodevonConf).diff(v, "years");
    const ausserhalb =
      (alterBisConf && alter > alterBisConf) ||
      (alterVonConf && alter < alterVonConf);
    return ausserhalb ? err : true;
  };
};

/**
 * Creates a validation rule that ensures that the applicant’s nationality matches the configuration parameter "nationality"
 *
 * @param staatsangehoerigkeitConf
 * @param err error message
 * @returns A validation function that returns `true` when the applicant’s nationality is ok,
 *   or an error message.
 */
const nationalityRule: CustomRule<[string, string], string[]> = (
  staatsangehoerigkeitConf,
  err
) => {
  return (v) =>
    v != null &&
    staatsangehoerigkeitConf &&
    v.indexOf(staatsangehoerigkeitConf) > -1
      ? true
      : err;
};

/**
 * Creates a validation rule that ensures that the applicant’s place of residence matches the configuration parameter "place of residence"
 *
 * @param wohnsitzConf
 * @param err error message
 * @returns A validation function that returns `true` when the applicant’s place of residence is ok,
 *   or an error message.
 */
const placeOfResidenceRule: CustomRule<[string, string], string> = (
  wohnsitzConf,
  err
) => {
  return (v) => (wohnsitzConf && v === wohnsitzConf ? true : err);
};

/**
 * Creates a validation rule that ensures that the applicant’s commitment to the constitution exists und file size is ok
 *
 * @param bestaetigungVerfassungstreueMaxSizeConf
 * @param bestaetigungVerfassungstreueFileExtensionConf
 * @param errZuGross
 * @param errFalscheExtension
 * @returns A validation function that returns `true` when the applicant’s nationality is ok,
 *   or an error message.
 */
const commitmentToTheConstitutionRule: CustomRule<
  [number, string, string, string],
  File
> = (
  bestaetigungVerfassungstreueMaxSizeConf,
  bestaetigungVerfassungstreueFileExtensionConf,
  errZuGross,
  errFalscheExtension
) => {
  return (v) => {
    if (v == undefined) {
      return true;
    }

    const dateiEndungKorrekt =
      hatBestaetigungVerfassungstreueFileErlaubteEndung(
        v.name,
        bestaetigungVerfassungstreueFileExtensionConf
      );

    return v.size >
      bestaetigungVerfassungstreueMaxSizeConf * KB_IN_BYTES * KB_IN_BYTES
      ? errZuGross
      : !dateiEndungKorrekt
        ? errFalscheExtension
        : true;
  };
};

function hatBestaetigungVerfassungstreueFileErlaubteEndung(
  dateiname: string,
  bestaetigungVerfassungstreueFileExtensionConf: string | undefined
): boolean {
  if (!bestaetigungVerfassungstreueFileExtensionConf) {
    return false;
  }
  const endungen = bestaetigungVerfassungstreueFileExtensionConf
    .split(",")
    .map((e) => e.trim().toLowerCase())
    .filter((e) => e.length > 0);
  const dateinameLower = dateiname.toLowerCase();
  return endungen.some((endung) => dateinameLower.endsWith(`.${endung}`));
}

export {
  maxRule,
  minRule,
  uniqueRule,
  birthDayRule,
  nationalityRule,
  placeOfResidenceRule,
  commitmentToTheConstitutionRule,
};
