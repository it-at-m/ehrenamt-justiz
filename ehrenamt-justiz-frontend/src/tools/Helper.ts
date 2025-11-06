/**
 * return the type of honorary justice in formatted plain text
 * @param t use for i18n
 * @param ehrenamtjustizart The type of honorary justice
 * @param count This parameter evaluates whether the value 1 or a multiple is transferred (for the determination of the text for single or multiple)
 * @return honorary justice in formatted plain text
 */
export function formattedEhrenamtjustizart(
  t: (key: string, options?: { justiceType: string; count: number }) => string, // justiceType not used here
  ehrenamtjustizart: string | undefined,
  count: number
): string {
  if (ehrenamtjustizart === "SCHOEFFEN") {
    return t("general.schoeffen", { justiceType: "", count: count });
  } else {
    return t("general.ehrenamtlicheVerwaltungsrichter", {
      justiceType: "",
      count: count,
    });
  }
}
