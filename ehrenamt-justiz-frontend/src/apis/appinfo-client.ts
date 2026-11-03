import type { AppInfo } from "@/types/AppInfo";

import { defaultResponseHandler, getGETConfig } from "@/apis/FetchUtils";

export function getAppInfo(): Promise<AppInfo> {
  return fetch("/actuator/info", getGETConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
