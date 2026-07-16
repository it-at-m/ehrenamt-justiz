import type TechnischeKonfigurationData from "@/types/TechnischeKonfigurationData";

import EntityApiService from "@/api/EntityApiService.ts";
import { getGETConfig } from "@/api/FetchUtils.ts";
import HttpMethod from "@/types/base/HttpMethod.ts";

class TechnischeKonfigurationApiServiceClass {
  public getTechnischeKonfiguration(): Promise<TechnischeKonfigurationData> {
    return new Promise<TechnischeKonfigurationData>((resolve, reject) => {
      fetch(
        `${EntityApiService.getBaseUrl()}/technischeKonfiguration/getTechnischeKonfiguration`,
        getGETConfig()
      )
        .then(async (res) => {
          if (!res.ok) {
            EntityApiService.handleWrongResponse(HttpMethod.GET, res);
            return reject(new Error(`HTTP error! status: ${res.status}`));
          }
          const createdInstance = await res.json();
          resolve(createdInstance);
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }
}

export const TechnischeKonfigurationApiService =
  new TechnischeKonfigurationApiServiceClass();
