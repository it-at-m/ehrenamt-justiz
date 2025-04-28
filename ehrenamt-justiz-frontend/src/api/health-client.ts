import { defaultResponseHandler, getGETConfig } from "@/api/FetchUtils.ts";
import HealthState from "@/types/HealthState";

export function checkHealth(): Promise<HealthState> {
  return fetch("actuator/health", getGETConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
