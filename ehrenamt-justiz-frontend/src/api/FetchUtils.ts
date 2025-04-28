import { ApiError } from "@/api/ApiError";
import { STATUS_INDICATORS } from "@/Constants.ts";

/**
 * Returns a default GET-Config for fetch
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
 * Returns a default POST-Config for fetch
 * @param body Optional zu übertragender Body
 */
// eslint-disable-next-line
export function getPOSTConfig(body: any): RequestInit {
  const requestMethod = "POST";
  return {
    method: requestMethod,
    body: body ? JSON.stringify(body) : undefined,
    headers: getHeaders(),
    mode: "cors",
    credentials: "same-origin",
    redirect: "manual",
  };
}
/**
 * Returns a default DELETE-Config for fetch
 */
export function getDELETEConfig(): RequestInit {
  const requestMethod = "DELETE";
  return {
    method: requestMethod,
    headers: getHeaders(),
    mode: "cors",
    credentials: "same-origin",
    redirect: "manual",
  };
}
/**
 * Returns a default PUT-Config for fetch
 * In this configuration, if available, the version of the entity to be updated is also set as an “If-Match” header.
 * @param body Optional Body
 */
// eslint-disable-next-line
export function getPUTConfig(body: any): RequestInit {
  const requestMethod = "PUT";
  const headers = getHeaders();

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
 * Returns a default PATCH-Config for fetch
 * In this configuration, if available, the version of the entity to be updated is also set as an “If-Match” header.
 * @param body Optional body
 */
// eslint-disable-next-line
export function getPATCHConfig(body: any): RequestInit {
  const requestMethod = "PATCH";
  const headers = getHeaders();

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
 * Covers the default handling of a response. This includes:
 *
 * - Error with missing authorizations --> HTTP 403
 * - Reload of the App if Session-Timeout --> HTTP 3xx
 * - Default-Error if HTTP-Code !2xx
 *
 * @param response response of fetch-Befehl
 * @param errorMessage The errormessage, if HTTP-Code != 2xx
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
 * Default Catch-Handler
 * Throws at the moment an ApiError
 * @param error Errormessage caused by fetch
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
 *  Creates header
 * @returns {Headers}
 */
function getHeaders(): Headers {
  const headers = new Headers({
    "Content-Type": "application/json",
  });

  const csrfCookie = _getXSRFToken();
  if (csrfCookie !== "") {
    headers.append("X-XSRF-TOKEN", csrfCookie);
  }
  return headers;
}

/**
 * Get the XSRF-TOKEN
 * @returns {string|string}
 */
function _getXSRFToken(): string {
  const help = document.cookie.match(
    "(^|;)\\s*" + "XSRF-TOKEN" + "\\s*=\\s*([^;]+)"
  );
  return (help ? help.pop() : "") as string;
}
