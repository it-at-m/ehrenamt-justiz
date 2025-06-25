import type KonfigurationData from "@/types/KonfigurationData";
import type OnlineBewerbungData from "@/types/OnlineBewerbungData";

import { getGETConfig, getPOSTConfig } from "@/api/FetchUtils";
import { API_BASE } from "@/Constants";
import HttpMethod from "@/types/base/HttpMethod";
import { ApiError, Levels } from "./error";

let t: (key: string) => string;

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

export class EhrenamtJustizOnlineServiceClass {
  constructor(tc: (key: string) => string) {
    t = tc;
  }

  private static ERROR_MESSAGES: (
    t: (key: string) => string
  ) => ErrorMessageDefinition[] = (t) => [
    {
      methods: ALL_HTTP_METHODS,
      statusCode: 404,
      message: t("ehrenamtJustizOnlineService.fehlermeldungen.http404"),
    },
    {
      methods: [HttpMethod.DELETE],
      statusCode: 409,
      message: t("ehrenamtJustizOnlineService.fehlermeldungen.http409"),
    },
    {
      methods: [HttpMethod.POST],
      statusCode: 500,
      message: t("ehrenamtJustizOnlineService.fehlermeldungen.http500"),
    },
  ];

  public static getBaseUrl(): string {
    return `${API_BASE}/public/backend`;
  }

  public getAktiveKonfiguration(): Promise<KonfigurationData> {
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
              reject(
                new Error(
                  t(
                    "ehrenamtJustizOnlineService.fehlermeldungen.fehlerBeiMethodeGetAktiveKonfiguration"
                  )
                )
              );
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

  public async bewerbungSpeichern(
    onlineBewerbungFormData: OnlineBewerbungData
  ): Promise<string> {
    return await new Promise<string>((resolve, reject) => {
      fetch(
        `${EhrenamtJustizOnlineServiceClass.getBaseUrl()}/onlinebewerbung/bewerbungSpeichern`,
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
          reject(
            new Error(
              t(
                "ehrenamtJustizOnlineService.fehlermeldungen.fehlerBeiMethodeBewerbungSpeichern"
              )
            )
          );
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
    for (const errorMessage of EhrenamtJustizOnlineServiceClass.ERROR_MESSAGES(
      t
    )) {
      if (
        errorMessage.methods.includes(httpMethod) &&
        res.status == errorMessage.statusCode
      )
        throw new ApiError(Levels.ERROR, errorMessage.message);
    }
    throw new ApiError(
      Levels.ERROR,
      `${t("ehrenamtJustizOnlineService.fehlermeldungen.default")} - HTTP Status Code: ${res.status}`
    );
  }

  public static handleError(err: ApiError): Error {
    if (err.level !== undefined)
      // check for already existing ApiError
      return err;
    return new ApiError(
      Levels.ERROR,
      t("ehrenamtJustizOnlineService.fehlermeldungen.default")
    );
  }
}
