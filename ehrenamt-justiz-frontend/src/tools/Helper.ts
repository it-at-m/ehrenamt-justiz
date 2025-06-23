/**
 * return the type of honorary justice in formatted plain text
 * @param t use for i18n
 * @param ehrenamtjustizart The type of honorary justice
 * @return honorary justice in formatted plain text
 */
export function formattedEhrenamtjustizart(
  t: (key: string) => string,
  ehrenamtjustizart: string | undefined
): string {
  if (ehrenamtjustizart === "SCHOEFFEN") {
    return t("general.schoeffen");
  } else {
    return t("general.ehrenamtlicheVerwaltungsrichter");
  }
}
