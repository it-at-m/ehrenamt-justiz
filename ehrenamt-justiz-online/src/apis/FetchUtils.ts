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
 * @param body Optional body to be transferred
 */
// eslint-disable-next-line
export function getPOSTConfig(body: any): RequestInit {
  const requestMethod = "POST";
  return {
    method: requestMethod,
    body: body,
    headers: getHeaders(),
    mode: "cors",
    credentials: "same-origin",
    redirect: "manual",
  };
}

/**
 *  Builds the header for the request
 * @returns {Headers}
 */
export function getHeaders(): Headers {
  const headers = new Headers();

  const csrfCookie = _getXSRFToken();
  if (csrfCookie !== "") {
    headers.append("X-XSRF-TOKEN", csrfCookie);
  }
  return headers;
}

/**
 * Returns the XSRF-TOKEN.
 * @returns {string|string}
 */
function _getXSRFToken(): string {
  const help = document.cookie.match(
    "(^|;)\\s*" + "XSRF-TOKEN" + "\\s*=\\s*([^;]+)"
  );
  return (help ? help.pop() : "") as string;
}
