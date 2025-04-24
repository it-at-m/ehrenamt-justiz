import { defaultResponseHandler, getGETConfig } from "@/api/FetchUtils.ts";

export interface Info {
  application: Application;
}

export interface Application {
  name: string;
  version: string;
}

export function getInfo(): Promise<Info> {
  return fetch("actuator/info", getGETConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
