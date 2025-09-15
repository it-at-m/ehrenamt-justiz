import type PagedEntity from "@/types/base/PagedEntity";
import type PersonCSV from "@/types/PersonCSV";
import type PersonData from "@/types/PersonData";
import type PersonenTableData from "@/types/PersonenTableData";

import EntityApiService from "@/api/EntityApiService";
import { getGETConfig, getPOSTConfig } from "@/api/FetchUtils";
import HttpMethod from "@/types/base/HttpMethod";

interface SortItem {
  key: string;
  order?: boolean | "asc" | "desc";
}

class PersonenApiServiceClass extends EntityApiService<PersonData> {
  constructor() {
    super("person", "personen");
  }

  public getSelectionPaged(
    search: string,
    pageIndex: number,
    pageSize: number,
    status: string,
    sortBy: SortItem[],
    projection: string | null = null
  ): Promise<PagedEntity<PersonenTableData>> {
    return new Promise<PagedEntity<PersonenTableData>>((resolve, reject) => {
      // Sort to "feldname1/sortierung,feldname2/sortierung..." (for example "familienname/asc,vorname/asc,geburtsdatum/desc") bringen
      let sortString = "";
      for (let i = 0; i < sortBy.length; i++) {
        sortString = sortString + sortBy[i]?.key + "/" + sortBy[i]?.order;
        if (i + 1 < sortBy.length) {
          sortString = sortString + ",";
        }
      }
      let url = `${this.entityBaseUrl}/findPersonen?search=${search}&sortby=${sortString}&page=${pageIndex}&size=${pageSize}&status=${status}`;
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
              const data: PersonenTableData[] = json["content"];
              resolve({
                data: data,
                page: json["number"],
                pageSize: json["size"],
                totalElements: json["totalElements"],
                totalPages: json["totalPages"],
              });
            })
            .catch((err) => reject(err));
        })
        .catch((err) => reject(EntityApiService.handleError(err)));
    });
  }

  /**
   * Delete persons
   * @returns Promise<void>
   * @param uuids
   */
  public async deletePersons(uuids: string[]): Promise<void> {
    return await new Promise<void>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/personen/deletePersonen`,
        getPOSTConfig(uuids)
      )
        .then((res) => {
          if (res.ok) return resolve();
          EntityApiService.handleWrongResponse(HttpMethod.POST, res);
          reject();
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }

  public updatePerson(person: PersonData): Promise<PersonData> {
    return this.postData(person, "/personen/updatePerson");
  }

  public cancelBewerbung(person: PersonData): Promise<PersonData> {
    return this.postData(person, "/personen/cancelBewerbung");
  }

  public async validiereAufVorschlagslisteSetzen(
    uuids: string[]
  ): Promise<PersonData[]> {
    return await new Promise<PersonData[]>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/personen/validiereAufVorschlagslisteSetzen`,
        getPOSTConfig(uuids)
      )
        .then((res) => {
          if (res.ok) {
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

  public async aufVorschlagslisteSetzen(uuids: string[]): Promise<void> {
    return await new Promise<void>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/personen/aufVorschlagslisteSetzen`,
        getPOSTConfig(uuids)
      )
        .then((res) => {
          if (res.ok) return resolve();
          EntityApiService.handleWrongResponse(HttpMethod.POST, res);
          reject();
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }

  public async aufBewerberlisteSetzen(uuids: string[]): Promise<void> {
    return await new Promise<void>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/personen/aufBewerberlisteSetzen`,
        getPOSTConfig(uuids)
      )
        .then((res) => {
          if (res.ok) return resolve();
          EntityApiService.handleWrongResponse(HttpMethod.POST, res);
          reject();
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }

  public async lesenPersonenCSV(
    uuids: string[],
    status: string
  ): Promise<PersonCSV[]> {
    return await new Promise<PersonCSV[]>((resolve, reject) => {
      fetch(
        `${this.getBaseUrl()}/personen/lesenPersonenCSV`,
        getPOSTConfig({ uuids, status })
      )
        .then((res) => {
          if (res.ok) {
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

  public async getNeueVorschlaege(): Promise<PersonCSV[]> {
    return await new Promise<PersonCSV[]>((resolve, reject) => {
      fetch(
        `${EntityApiService.getBaseUrl()}/personen/getNeueVorschlaege`,
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

  public async alsBenachrichtigtMarkieren() {
    return await new Promise<void>((resolve, reject) => {
      fetch(
        `${EntityApiService.getBaseUrl()}/personen/alsBenachrichtigtMarkieren`,
        getPOSTConfig(null)
      )
        .then((res) => {
          if (res.ok) return resolve();
          EntityApiService.handleWrongResponse(HttpMethod.POST, res);
          reject();
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }
}

export const PersonApiService = new PersonenApiServiceClass();
