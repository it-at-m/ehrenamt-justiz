import { ApiError, Levels } from "@/api/error";

/**
 * Liefert eine default GET-Config für fetch
 */
export function getGETConfig(): RequestInit {
  const requestMethod = "GET";
  return {
    headers: getHeaders(requestMethod),
    mode: "cors",
    credentials: "same-origin",
    redirect: "manual",
  };
}

/**
 * Liefert eine default POST-Config für fetch
 * @param body Optional zu übertragender Body
 */
// eslint-disable-next-line
export function getPOSTConfig(body: any): RequestInit {
  const requestMethod = "POST";
  return {
    method: requestMethod,
    body: body ? JSON.stringify(body) : undefined,
    headers: getHeaders(requestMethod),
    mode: "cors",
    credentials: "same-origin",
    redirect: "manual",
  };
}

/**
 * Deckt das Default-Handling einer Response ab. Dazu zählt:
 *
 * - Fehler bei fehlenden Berechtigungen --> HTTP 403
 * - Reload der App bei Session-Timeout --> HTTP 3xx
 * - Default-Fehler bei allen HTTP-Codes !2xx
 *
 * @param response Die response aus fetch-Befehl die geprüft werden soll.
 * @param errorMessage Die Fehlermeldung, welche bei einem HTTP-Code != 2xx angezeigt werden soll.
 */
export function defaultResponseHandler(
  response: Response,
  errorMessage = "Es ist ein unbekannter Fehler aufgetreten."
): void {
  if (!response.ok) {
    if (response.status === 403) {
      throw new ApiError({
        level: Levels.ERROR,
        message: `Sie haben nicht die nötigen Rechte um diese Aktion durchzuführen.`,
      });
    } else if (response.type === "opaqueredirect") {
      location.reload();
    }
    throw new ApiError({
      level: Levels.WARNING,
      message: errorMessage,
    });
  }
}

/**
 * Default Catch-Handler für alle Anfragen des Service.
 * Schmeißt derzeit nur einen ApiError
 * @param error die Fehlermeldung aus fetch-Befehl
 * @param errorMessage Fehler-Text
 */
export function defaultCatchHandler(
  error: Error,
  errorMessage = "Es ist ein unbekannter Fehler aufgetreten."
): PromiseLike<never> {
  throw new ApiError({
    level: Levels.WARNING,
    message: errorMessage,
  });
}

/**
 *  Baut den Header fuer den Request auf
 * @returns {Headers}
 */
export function getHeaders(requestMethod = "GET"): Headers {
  const headers = new Headers();
  if (requestMethod !== "GET") {
    headers.append("Content-Type", "application/json");
  }
  const csrfCookie = _getXSRFToken();
  if (csrfCookie !== "") {
    headers.append("X-XSRF-TOKEN", csrfCookie);
  }
  return headers;
}

/**
 * Liefert den XSRF-TOKEN zurück.
 * @returns {string|string}
 */
function _getXSRFToken(): string {
  const help = /(^|;)\\s*" + "XSRF-TOKEN" + "\\s*=\\s*([^;]+)/.exec(
    document.cookie
  );
  return (help ? help.pop() : "") as string;
}
