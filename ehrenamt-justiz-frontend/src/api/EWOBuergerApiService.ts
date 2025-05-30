import type EWOBuergerData from "@/types/EWOBuergerData";
import type EWOBuergerSuche from "@/types/EWOBuergerSuche";
import type PersonData from "@/types/PersonData";

import { ApiError } from "@/api/ApiError";
import EntityApiService from "@/api/EntityApiService";
import { getGETConfig, getPOSTConfig } from "@/api/FetchUtils";
import { STATUS_INDICATORS } from "@/Constants.ts";
import HttpMethod from "@/types/base/HttpMethod";

class EWOBuergerApiServiceClass extends EntityApiService<EWOBuergerData> {
  constructor() {
    super("ewobuerger", "ewobuergers");
  }

  public async getEwoSuche(
    ewoBuergerSuche: EWOBuergerSuche
  ): Promise<EWOBuergerData[]> {
    return await new Promise<EWOBuergerData[]>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/ehrenamtjustiz/ewoSuche`,
        getPOSTConfig(ewoBuergerSuche)
      )
        .then((res) => {
          if (res.status != 200) {
            const errormessage = res.headers.get("errormessage");
            if (errormessage != null) {
              throw new ApiError({
                level: STATUS_INDICATORS.ERROR,
                message: `${errormessage.substring(
                  errormessage.indexOf("message") + 10,
                  errormessage.lastIndexOf("}") - 1
                )} - HTTP Status Code: ${res.status}`,
              });
            } else {
              EntityApiService.handleWrongResponse(HttpMethod.POST, res);
            }
            return reject();
          }
          res
            .json()
            .then((createdInstances) => {
              if (res.ok) return resolve(createdInstances);
              EntityApiService.handleWrongResponse(HttpMethod.POST, res);
              reject();
            })
            .catch((reason) => reject(EntityApiService.handleError(reason)));
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }

  public async ewoSucheMitOM(om: string): Promise<EWOBuergerData> {
    return await new Promise<EWOBuergerData>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/ehrenamtjustiz/ewoSucheMitOM?om=${om}`,
        getGETConfig()
      )
        .then((res) => {
          if (res.ok) {
            res
              .json()
              .then((createdInstance) => {
                return resolve(createdInstance);
              })
              .catch((reason) => reject(EntityApiService.handleError(reason)));
          } else {
            EntityApiService.handleWrongResponse(HttpMethod.GET, res);
            reject();
          }
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }

  public async pruefenNeuePerson(
    eWOBuergerData: EWOBuergerData
  ): Promise<PersonData | null> {
    return await new Promise<PersonData | null>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/ehrenamtjustiz/pruefenNeuePerson`,
        getPOSTConfig(eWOBuergerData)
      )
        .then((res) => {
          if (res.status == 404) {
            resolve(null);
          } else {
            res
              .json()
              .then((createdInstance) => {
                if (res.ok) return resolve(createdInstance);
                EntityApiService.handleWrongResponse(HttpMethod.POST, res);
                reject();
              })
              .catch((reason) => reject(EntityApiService.handleError(reason)));
          }
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }

  public async vorbereitenUndSpeichernPerson(
    eWOBuergerData: EWOBuergerData
  ): Promise<PersonData> {
    return await new Promise<PersonData>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/ehrenamtjustiz/vorbereitenUndSpeichernPerson`,
        getPOSTConfig(eWOBuergerData)
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

  public async checkEwoEaiStatus(): Promise<string> {
    return await new Promise<string>((resolve, reject) => {
      fetch(
        `${EntityApiService.getBaseUrl()}/ehrenamtjustiz/ewoEaiStatus`,
        getGETConfig()
      )
        .then(async (res) => {
          await res
            .text()
            .then(async (createdInstance) => {
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

export const EWOBuergerApiService = new EWOBuergerApiServiceClass();
