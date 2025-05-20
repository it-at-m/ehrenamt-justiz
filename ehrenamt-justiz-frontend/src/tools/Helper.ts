/**
 * return the type of honorary justice in plain language
 * @param ehrenamtjustizart The type of honorary justice
 */
export function getEhrenamtjustizart(
  ehrenamtjustizart: string | undefined
): string {
  if (ehrenamtjustizart == "SCHOEFFEN") {
    return "Schöffen";
  } else {
    return "Verwaltungsrichter";
  }
}
