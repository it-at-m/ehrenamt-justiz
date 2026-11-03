import type { HealthState } from "@/types/HealthState";

import { defaultResponseHandler, getGETConfig } from "@/apis/FetchUtils";
import { API_BASE } from "@/Constants";

export function checkHealth(): Promise<HealthState> {
  return fetch(`${API_BASE}/actuator/health`, getGETConfig())
    .then((response) => {
      defaultResponseHandler(response);
      return response.json();
    })
    .catch((err) => {
      defaultResponseHandler(err);
    });
}
