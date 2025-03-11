import { ApiError } from "@/api/ApiError";
import { STATUS_INDICATORS } from "@/Constants.ts";

/**
 * Liefert eine default GET-Config für fetch
 */
export function getGETConfig(): RequestInit {
  return {
    headers: getHeaders(),
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
 * Liefert eine default DELETE-Config für fetch
 */
export function getDELETEConfig(): RequestInit {
  const requestMethod = "DELETE";
  return {
    method: requestMethod,
    headers: getHeaders(requestMethod),
    mode: "cors",
    credentials: "same-origin",
    redirect: "manual",
  };
}
/**
 * Liefert eine default PUT-Config für fetch
 * In dieser wird, wenn vorhanden, die Version der zu aktualisierenden Entität
 * als "If-Match"-Header mitgesetzt.
 * @param body Optional zu übertragender Body
 */
// eslint-disable-next-line
export function getPUTConfig(body: any): RequestInit {
  const requestMethod = "PUT";
  const headers = getHeaders(requestMethod);

  if (body.version) {
    headers.append("If-Match", body.version);
  }
  return {
    method: requestMethod,
    body: body ? JSON.stringify(body) : undefined,
    headers,
    mode: "cors",
    credentials: "same-origin",
    redirect: "manual",
  };
}

/**
 * Liefert eine default PATCH-Config für fetch
 * In dieser wird, wenn vorhanden, die Version der zu aktualisierenden Entität
 * als "If-Match"-Header mitgesetzt.
 * @param body Optional zu übertragender Body
 */
// eslint-disable-next-line
export function getPATCHConfig(body: any): RequestInit {
  const requestMethod = "PATCH";
  const headers = getHeaders(requestMethod);

  if (body.version !== undefined) {
    headers.append("If-Match", body.version);
  }
  return {
    method: requestMethod,
    body: body ? JSON.stringify(body) : undefined,
    headers,
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
        level: STATUS_INDICATORS.ERROR,
        message: `Sie haben nicht die nötigen Rechte um diese Aktion durchzuführen.`,
      });
    } else if (response.type === "opaqueredirect") {
      location.reload();
    }
    throw new ApiError({
      level: STATUS_INDICATORS.WARNING,
      message: errorMessage,
    });
  }
}

/**
 * Default Catch-Handler für alle Anfragen des Service.
 * Schmeißt derzeit nur einen ApiError
 * @param error die Fehlermeldung aus fetch-Befehl
 * @param errorMessage
 */
export function defaultCatchHandler(
  error: Error,
  errorMessage = "Es ist ein unbekannter Fehler aufgetreten."
): PromiseLike<never> {
  throw new ApiError({
    level: STATUS_INDICATORS.WARNING,
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
export function _getXSRFToken(): string {
  const help = document.cookie.match(
    "(^|;)\\s*" + "XSRF-TOKEN" + "\\s*=\\s*([^;]+)"
  );
  return (help ? help.pop() : "") as string;
}
