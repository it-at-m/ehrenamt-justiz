import type KonfigurationData from "@/types/KonfigurationData";

import EntityApiService from "@/api/EntityApiService";
import { getPOSTConfig } from "@/api/FetchUtils";
import HttpMethod from "@/types/base/HttpMethod";

class KonfigurationApiServiceClass extends EntityApiService<KonfigurationData> {
  constructor() {
    super("konfiguration", "konfigurationen");
  }

  public updateKonfiguration(
    konfiguration: KonfigurationData
  ): Promise<KonfigurationData> {
    return new Promise<KonfigurationData>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/konfiguration/updateKonfiguration`,
        getPOSTConfig(konfiguration)
      )
        .then((res) => {
          res
            .json()
            .then((createdInstance) => {
              if (res.ok) return resolve(createdInstance);
              EntityApiService.handleWrongResponse(HttpMethod.POST, res);
              reject();
            })
            .catch((reason) => reject(EntityApiService.handleError(reason)));
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }

  public setActive(
    konfiguration: KonfigurationData
  ): Promise<KonfigurationData> {
    return new Promise<KonfigurationData>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/konfiguration/setActive`,
        getPOSTConfig(konfiguration)
      )
        .then((res) => {
          res
            .json()
            .then((createdInstance) => {
              if (res.ok) return resolve(createdInstance);
              EntityApiService.handleWrongResponse(HttpMethod.POST, res);
              reject();
            })
            .catch((reason) => reject(EntityApiService.handleError(reason)));
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }

  public getAktiveKonfiguration(): Promise<KonfigurationData> {
    return this.getData("/konfiguration/getAktiveKonfiguration");
  }
}
export const KonfigurationApiService = new KonfigurationApiServiceClass();
