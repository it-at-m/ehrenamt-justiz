import type TechnischeKonfigurationData from "@/types/TechnischeKonfigurationData.ts";

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
        .then((res) => {
          res
            .json()
            .then((createdInstance) => {
              if (res.ok) return resolve(createdInstance);
              EntityApiService.handleWrongResponse(HttpMethod.GET, res);
              reject();
            })
            .catch((reason) => reject(EntityApiService.handleError(reason)));
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }
}

export const TechnischeKonfigurationApiService =
  new TechnischeKonfigurationApiServiceClass();
