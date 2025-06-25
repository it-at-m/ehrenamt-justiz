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
  const help = document.cookie.match(
    "(^|;)\\s*" + "XSRF-TOKEN" + "\\s*=\\s*([^;]+)"
  );
  return (help ? help.pop() : "") as string;
}
