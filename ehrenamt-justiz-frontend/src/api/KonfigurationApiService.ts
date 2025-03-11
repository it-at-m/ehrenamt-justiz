import type Konfiguration from "@/types/Konfiguration";

import EntityApiService from "@/api/EntityApiService";
import { getPOSTConfig } from "@/api/FetchUtils";
import HttpMethod from "@/types/base/HttpMethod";

class KonfigurationApiServiceClass extends EntityApiService<Konfiguration> {
  constructor() {
    super("konfiguration", "konfigurationen");
  }

  public updateKonfiguration(
    konfiguration: Konfiguration
  ): Promise<Konfiguration> {
    return new Promise<Konfiguration>((resolve, reject) => {
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

  public setActive(konfiguration: Konfiguration): Promise<Konfiguration> {
    return new Promise<Konfiguration>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/konfiguration/setactive`,
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

  public getAktiveKonfiguration(): Promise<Konfiguration> {
    return this.getData("/konfiguration/getAktiveKonfiguration");
  }
}
export const KonfigurationApiService = new KonfigurationApiServiceClass();
