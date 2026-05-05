import type KonfigurationData from "@/types/KonfigurationData";
import type OnlineBewerbungData from "@/types/OnlineBewerbungData";

import { getGETConfig, getPOSTConfig } from "@/api/FetchUtils";
import { API_BASE } from "@/Constants";
import HttpMethod from "@/types/base/HttpMethod";
import { ApiError, Levels } from "./error";

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
  private t: (key: string) => string;

  constructor(t: (key: string) => string) {
    this.t = t;
  }

  private getErrorMessages(): ErrorMessageDefinition[] {
    return [
      {
        methods: ALL_HTTP_METHODS,
        statusCode: 404,
        message: this.t("ehrenamtJustizOnlineService.fehlermeldungen.http404"),
      },
      {
        methods: [HttpMethod.DELETE],
        statusCode: 409,
        message: this.t("ehrenamtJustizOnlineService.fehlermeldungen.http409"),
      },
      {
        methods: [HttpMethod.POST],
        statusCode: 500,
        message: this.t("ehrenamtJustizOnlineService.fehlermeldungen.http500"),
      },
    ];
  }

  public static getBaseUrl(): string {
    return `${API_BASE}/public/backend`;
  }

  /**
   * Determines the active configuration
   * @returns Promise<KonfigurationData>
   */
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
              this.handleWrongResponse(HttpMethod.GET, res);
              reject(
                new ApiError(
                  Levels.ERROR,
                  this.t(
                    "ehrenamtJustizOnlineService.fehlermeldungen.fehlerBeiMethodeGetAktiveKonfiguration"
                  )
                )
              );
            })
            .catch((reason) => reject(this.handleError(reason)));
        })
        .catch((reason) => reject(this.handleError(reason)));
    });
  }
  /**
   * Gets the Pattern for 'Verfassungstreue'
   * @returns Promise<byte[]>
   */
  public lesenVerfassungstreueMuster(): Promise<string> {
    return new Promise((resolve, reject) => {
      fetch(
        `${EhrenamtJustizOnlineServiceClass.getBaseUrl()}/onlinebewerbung/lesenVerfassungstreueMuster`,
        getGETConfig()
      )
        .then((res) => {
          res
            .text()
            .then((createdInstance) => {
              if (res.ok) return resolve(createdInstance);
              this.handleWrongResponse(HttpMethod.GET, res);
              reject(
                new ApiError(
                  Levels.ERROR,
                  this.t(
                    "ehrenamtJustizOnlineService.fehlermeldungen.fehlerBeiMethodeLesenVerfassungstreueMuster"
                  )
                )
              );
            })
            .catch((reason) => reject(this.handleError(reason)));
        })
        .catch((reason) => reject(this.handleError(reason)));
    });
  }

  /**
   * Check Data
   * @returns Promise<KonfigurationData>
   * @param onlineBewerbungFormData
   */
  public async pruefen(
    onlineBewerbungFormData: OnlineBewerbungData
  ): Promise<string> {
    return await new Promise<string>((resolve, reject) => {
      const formData = new FormData();
      formData.append("vorname", onlineBewerbungFormData.vorname);
      formData.append("nachname", onlineBewerbungFormData.nachname);
      formData.append("beruf", onlineBewerbungFormData.beruf);
      formData.append("geburtsdatum", onlineBewerbungFormData.geburtsdatum);
      formData.append("mail", onlineBewerbungFormData.mail);
      formData.append("telefonnummer", onlineBewerbungFormData.telefonnummer);
      fetch(
        `${EhrenamtJustizOnlineServiceClass.getBaseUrl()}/onlinebewerbung/pruefen`,
        getPOSTConfig(formData)
      )
        .then(async (res) => {
          if (res.ok) {
            await res.text().then(async (createdInstance) => {
              resolve(createdInstance);
            });
          }
          this.handleWrongResponse(HttpMethod.POST, res);
          reject(
            new ApiError(
              Levels.ERROR,
              this.t(
                "ehrenamtJustizOnlineService.fehlermeldungen.fehlerBeiMethodePruefen"
              )
            )
          );
        })
        .catch((reason) => reject(this.handleError(reason)));
    });
  }
  /**
   * Saves an online application in the database
   * @returns Promise<KonfigurationData>
   * @param onlineBewerbungFormData
   */
  public async bewerbungSpeichern(
    onlineBewerbungFormData: OnlineBewerbungData
  ): Promise<string> {
    const formData = new FormData();
    if (onlineBewerbungFormData.dateiVerfassungstreue) {
      formData.append(
        "dateiVerfassungstreue",
        onlineBewerbungFormData.dateiVerfassungstreue
      );
    }
    formData.append("vorname", onlineBewerbungFormData.vorname);
    formData.append("nachname", onlineBewerbungFormData.nachname);
    formData.append("beruf", onlineBewerbungFormData.beruf);
    formData.append("geburtsdatum", onlineBewerbungFormData.geburtsdatum);
    formData.append("mail", onlineBewerbungFormData.mail);
    formData.append("telefonnummer", onlineBewerbungFormData.telefonnummer);
    return await new Promise<string>((resolve, reject) => {
      fetch(
        `${EhrenamtJustizOnlineServiceClass.getBaseUrl()}/onlinebewerbung/bewerbungSpeichern`,
        getPOSTConfig(formData)
      )
        .then(async (res) => {
          if (res.ok) {
            await res.text().then(async (createdInstance) => {
              resolve(createdInstance);
            });
          }
          this.handleWrongResponse(HttpMethod.POST, res);
          reject(
            new ApiError(
              Levels.ERROR,
              this.t(
                "ehrenamtJustizOnlineService.fehlermeldungen.fehlerBeiMethodeBewerbungSpeichern"
              )
            )
          );
        })
        .catch((reason) => reject(this.handleError(reason)));
    });
  }

  /**
   * Handles errors such as http 500
   * @returns void
   * @param httpMethod
   * @param res
   */
  public handleWrongResponse(httpMethod: HttpMethod, res: Response): void {
    for (const errorMessage of this.getErrorMessages()) {
      if (
        errorMessage.methods.includes(httpMethod) &&
        res.status == errorMessage.statusCode
      )
        throw new ApiError(Levels.ERROR, errorMessage.message);
    }
    throw new ApiError(
      Levels.ERROR,
      `${this.t("ehrenamtJustizOnlineService.fehlermeldungen.default")} - HTTP Status Code: ${res.status}`
    );
  }

  /**
   * Handles errors
   * @returns Error
   * @param err
   */
  public handleError(err: ApiError): Error {
    if (err.level !== undefined)
      // check for already existing ApiError
      return err;
    return new ApiError(
      Levels.ERROR,
      this.t("ehrenamtJustizOnlineService.fehlermeldungen.default")
    );
  }
}
