import type Konfiguration from "@/types/Konfiguration";
import type OnlineBewerbungDaten from "@/types/OnlineBewerbungDaten";

import { getGETConfig, getPOSTConfig } from "@/api/FetchUtils";
import { API_BASE } from "@/Constants";
import HttpMethod from "@/types/base/HttpMethod";
import { ApiError, Levels } from "./error";

const DEFAULT_ERROR_MESSAGE =
  "Ein unbekannter Fehler ist aufgetreten, bitte den Administrator informieren.";

const ALL_HTTP_METHODS = [
  HttpMethod.GET,
  HttpMethod.POST,
  HttpMethod.PUT,
  HttpMethod.PATCH,
  HttpMethod.DELETE,
];

interface ErrorMessageDefinition {
  methods: HttpMethod[];
  statusCode: number;
  message: string;
}

const ERROR_MESSAGES: ErrorMessageDefinition[] = [
  {
    methods: ALL_HTTP_METHODS,
    statusCode: 404,
    message: "Dieser Datensatz wurde nicht gefunden.",
  },
  {
    methods: [HttpMethod.DELETE],
    statusCode: 409,
    message:
      "Dieser Datensatz kann nicht gelöscht werden, da er von anderen Teilen des Programms noch benötigt wird.",
  },
  {
    methods: [HttpMethod.POST],
    statusCode: 500,
    message: "Interner Serverfehler. Es können keine Daten ermittelt werden.",
  },
];

class EhrenamtJustizOnlineServiceClass {
  public static getBaseUrl(): string {
    return `${API_BASE}/public/backend`;
  }

  public getAktiveKonfiguration(): Promise<Konfiguration> {
    return new Promise((resolve, reject) => {
      fetch(
        `${EhrenamtJustizOnlineServiceClass.getBaseUrl()}/konfiguration/getAktiveKonfiguration`,
        getGETConfig()
      )
        .then((res) => {
          res
            .json()
            .then((createdInstance) => {
              if (res.ok) return resolve(createdInstance);
              EhrenamtJustizOnlineServiceClass.handleWrongResponse(
                HttpMethod.GET,
                res
              );
              reject(new Error("Fehler bei getAktiveKonfiguration"));
            })
            .catch((reason) =>
              reject(EhrenamtJustizOnlineServiceClass.handleError(reason))
            );
        })
        .catch((reason) =>
          reject(EhrenamtJustizOnlineServiceClass.handleError(reason))
        );
    });
  }

  public async bewerbungspeichern(
    onlineBewerbungFormData: OnlineBewerbungDaten
  ): Promise<string> {
    return await new Promise<string>((resolve, reject) => {
      fetch(
        `${EhrenamtJustizOnlineServiceClass.getBaseUrl()}/onlinebewerbung/bewerbungspeichern`,
        getPOSTConfig(onlineBewerbungFormData)
      )
        .then(async (res) => {
          if (res.ok) {
            await res.text().then(async (createdInstance) => {
              resolve(createdInstance);
            });
          }
          EhrenamtJustizOnlineServiceClass.handleWrongResponse(
            HttpMethod.POST,
            res
          );
          reject(new Error("Fehler bei bewerbungSpeichern"));
        })
        .catch((reason) =>
          reject(EhrenamtJustizOnlineServiceClass.handleError(reason))
        );
    });
  }

  public static handleWrongResponse(
    httpMethod: HttpMethod,
    res: Response
  ): void {
    for (const errorMessage of ERROR_MESSAGES) {
      if (
        errorMessage.methods.includes(httpMethod) &&
        res.status == errorMessage.statusCode
      )
        throw new ApiError({
          level: Levels.ERROR,
          message: errorMessage.message,
        });
    }
    throw new ApiError({
      level: Levels.ERROR,
      message: `${DEFAULT_ERROR_MESSAGE} - HTTP Status Code: ${res.status}`,
    });
  }

  public static handleError(err: ApiError): Error {
    if (err.level !== undefined)
      // check for already existing ApiError
      return err;
    return new ApiError({
      message: DEFAULT_ERROR_MESSAGE,
    });
  }
}
export const EhrenamtJustizOnlineService =
  new EhrenamtJustizOnlineServiceClass();
