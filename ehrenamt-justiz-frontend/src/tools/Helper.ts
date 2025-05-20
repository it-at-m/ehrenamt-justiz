/**
 * return the type of honorary justice in formatted plain text
 * @param ehrenamtjustizart The type of honorary justice
 * @return honorary justice in formatted plain text
 */
export function formattedEhrenamtjustizart(
  ehrenamtjustizart: string | undefined
): string {
  if (ehrenamtjustizart === "SCHOEFFEN") {
    return "Schöffen";
  } else {
    return "Verwaltungsrichter";
  }
}
