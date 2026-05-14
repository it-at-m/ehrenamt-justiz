import type DocumentData from "@/types/DocumentData.ts";
import type KonfigurationData from "@/types/KonfigurationData";

import EntityApiService from "@/api/EntityApiService";
import { getGETConfig, getPOSTConfig } from "@/api/FetchUtils";
import HttpMethod from "@/types/base/HttpMethod";

class KonfigurationApiServiceClass extends EntityApiService<KonfigurationData> {
  constructor() {
    super("konfiguration", "konfigurationen");
  }

  public updateKonfiguration(
    konfiguration: KonfigurationData,
    bestaetigungverfassungstreue_file: File | File[] | undefined
  ): Promise<KonfigurationData> {
    return this.postData(
      konfiguration,
      bestaetigungverfassungstreue_file,
      "/konfiguration/updateKonfiguration"
    );
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

  public async getDocumentByKonfigurationId(
    uuid: string
  ): Promise<DocumentData> {
    return await new Promise<DocumentData>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/document/getDocumentByKonfigurationId/${uuid}`,
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
export const KonfigurationApiService = new KonfigurationApiServiceClass();
