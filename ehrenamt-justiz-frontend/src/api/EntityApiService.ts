import type PagedEntity from "@/types/base/PagedEntity";

import { API_BASE, STATUS_INDICATORS } from "@/Constants.ts";
import HttpMethod from "@/types/base/HttpMethod";
import { ApiError } from "./ApiError";
import {
  getDELETEConfig,
  getGETConfig,
  getPOSTConfig,
  getPUTConfig,
} from "./FetchUtils";

const BASE_URL = `${API_BASE}/api/ej-app-backend-service`;

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

const DEFAULT_ERROR_MESSAGE =
  "Ein unbekannter Fehler ist aufgetreten, bitte den Administrator informieren.";

type Id = string | undefined;

interface Idable {
  id: Id;
}

/**
 *
 */
export default class EntityApiService<T extends Idable> {
  private readonly entitySingular: string;
  protected readonly entityPlural: string;
  protected readonly entityBaseUrl: string;

  constructor(entitySingular: string, entityPlural: string) {
    this.entitySingular = entitySingular;
    this.entityPlural = entityPlural;
    this.entityBaseUrl = `${BASE_URL}/${entityPlural}`;
  }

  public getBaseUrl(): string {
    return `${BASE_URL}`;
  }

  public static getBaseUrl(): string {
    return `${BASE_URL}`;
  }

  /**
   * Get Request für ein Item mit gegebener Id
   * @param id
   * @param loadRelations Namen der Relationen der Enität, welche ebenfalls geladen werden sollen.
   */
  public get(id: Id, loadRelations: string[] = []): Promise<T> {
    return new Promise<T>((resolve, reject) => {
      fetch(`${this.entityBaseUrl}/${id}`, getGETConfig())
        .then((res) => {
          if (res.status != 200) {
            EntityApiService.handleWrongResponse(HttpMethod.GET, res);
            reject();
          }
          res
            .json()
            .then((entity) => {
              const relationPromises = loadRelations.map(
                (relationName) =>
                  new Promise<void>((resolveRelation, rejectRelation) => {
                    fetch(
                      EntityApiService.removeProjectionsFromUrl(
                        entity["_links"][relationName]["href"]
                      ),
                      getGETConfig()
                    ).then((res) => {
                      if (res.ok) {
                        res
                          .json()
                          .then((relationData) => {
                            if ("_embedded" in relationData) {
                              const entityType = Object.keys(
                                relationData["_embedded"]
                              )[0];
                              relationData =
                                entityType !== undefined
                                  ? relationData["_embedded"]?.[entityType]
                                  : undefined;
                            }
                            entity[relationName] = relationData;
                            resolveRelation();
                          })
                          .catch((err) =>
                            rejectRelation(EntityApiService.handleError(err))
                          );
                      } else if (res.status == 404) {
                        entity[relationName] = null;
                        resolveRelation();
                      } else {
                        EntityApiService.handleWrongResponse(
                          HttpMethod.GET,
                          res
                        );
                        rejectRelation();
                      }
                    });
                  })
              );
              Promise.all(relationPromises)
                .then(() => {
                  resolve(entity);
                })
                .catch((err) => reject(EntityApiService.handleError(err)));
            })
            .catch((err) => reject(EntityApiService.handleError(err)));
        })
        .catch((err) => reject(EntityApiService.handleError(err)));
    });
  }

  /**
   * Get Request für alle Items. Paginiert, deswegen Seite mit angeben.
   * @param pageIndex Bei 0 beginnend.
   * @param pageSize
   * @param projection
   */
  public async getAllPaged(
    pageIndex: number,
    pageSize = 20,
    projection: string | null = null
  ): Promise<PagedEntity<T>> {
    return await new Promise<PagedEntity<T>>((resolve, reject) => {
      let url = `${this.entityBaseUrl}?page=${pageIndex}&size=${pageSize}&sort=amtsperiodevon,asc`;
      if (projection !== null) url = `${url}&projection=${projection}`;
      fetch(url, getGETConfig())
        .then((res) => {
          if (res.status != 200) {
            EntityApiService.handleWrongResponse(HttpMethod.GET, res);
            return reject();
          }
          res
            .json()
            .then((json) => {
              const data: T[] = json["_embedded"][this.entityPlural];
              resolve({
                data: data,
                page: json["page"]["number"],
                pageSize: json["page"]["size"],
                totalElements: json["page"]["totalElements"],
                totalPages: json["page"]["totalPages"],
              });
            })
            .catch((err) => reject(err));
        })
        .catch((err) => reject(EntityApiService.handleError(err)));
    });
  }

  /**
   * Delete by id
   * @param id
   * @returns Promise<void>
   */
  /* eslint-disable no-async-promise-executor */
  public async delete(id: Id): Promise<void> {
    return await new Promise<void>(async (resolve, reject) => {
      await fetch(`${this.entityBaseUrl}/${id}`, getDELETEConfig())
        .then((res) => {
          if (res.ok) return resolve();
          EntityApiService.handleWrongResponse(HttpMethod.DELETE, res);
        })
        .catch((err) => reject(EntityApiService.handleError(err)));
    });
  }
  /* eslint-enable no-async-promise-executor */

  /**
   * Creates an instance of the model in the backend and returns it as an object in a Promise.
   * @param instance
   * @returns Promise
   */
  public create(instance: T): Promise<T> {
    return new Promise<T>((resolve, reject) => {
      fetch(`${this.entityBaseUrl}`, getPOSTConfig(instance))
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

  /**
   *
   * @param entity
   * @param relation
   * @param foreignEntityName Name der Entität zu welcher eine relation abgebildet werden soll.
   * @param foreignEntityId
   */
  public createRelation(
    entity: T,
    relation: string,
    foreignEntityName: string,
    foreignEntityId: string
  ): Promise<void> {
    const config = getPUTConfig("");
    (config.headers as Headers).set("Content-Type", "text/uri-list");
    config.body = `${BASE_URL}/${foreignEntityName}/${foreignEntityId}`;
    return new Promise<void>((resolve, reject) => {
      fetch(`${this.entityBaseUrl}/${entity.id}/${relation}`, config).then(
        (res) => {
          if (res.ok) return resolve();
          EntityApiService.handleWrongResponse(HttpMethod.PUT, res);
          reject();
        }
      );
    });
  }

  /**
   * Updates the data of an instance of a model in the backend, overwriting all data with the data from the
   * transferred object.
   *
   * @param instance instance of the model with the current data
   * @returns instance of the model with the current status from the server
   */
  public update(instance: T): Promise<T> {
    return new Promise<T>((resolve, reject) => {
      fetch(
        `${this.entityBaseUrl}/${instance.id?.toString()}`,
        getPUTConfig(instance)
      )
        .then((res) => {
          res
            .json()
            .then((newInstance) => {
              if (res.ok) return resolve(newInstance);
              EntityApiService.handleWrongResponse(HttpMethod.PUT, res);
              reject();
            })
            .catch((reason) => reject(EntityApiService.handleError(reason)));
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }

  /**
   * Handles unsuccessful status codes and converts them into non-technical messages for the user.
   *
   * @param httpMethod
   * @param res
   */
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
          level: STATUS_INDICATORS.ERROR,
          message: errorMessage.message,
        });
    }
    throw new ApiError({
      level: STATUS_INDICATORS.ERROR,
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

  private static removeProjectionsFromUrl(url: string): string {
    const regex = /{?[^{}]+}/gm;
    while (url.indexOf("{?") >= 0) {
      url = url.replace(regex, "");
    }
    return url;
  }

  public async postData(instance: T, path: string): Promise<T> {
    return await new Promise<T>((resolve, reject) => {
      fetch(`${this.getBaseUrl()}${path}`, getPOSTConfig(instance))
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

  public getArrayData(path: string): Promise<T[]> {
    return new Promise<T[]>((resolve, reject) => {
      fetch(`${this.getBaseUrl()}${path}`, getGETConfig())
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

  public getData(path: string): Promise<T> {
    return new Promise<T>((resolve, reject) => {
      fetch(`${this.getBaseUrl()}${path}`, getGETConfig())
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
