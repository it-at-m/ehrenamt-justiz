import type EhrenamtJustizStatus from "@/types/EhrenamtJustizStatus";
import type PersonCSV from "@/types/PersonCSV";

import { asBlob, generateCsv, mkConfig } from "export-to-csv";

import EntityApiService from "@/api/EntityApiService";
import { getGETConfig, getPOSTConfig } from "@/api/FetchUtils";
import { PersonApiService } from "@/api/PersonApiService";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { formattedEhrenamtjustizart } from "@/tools/Helper";
import HttpMethod from "@/types/base/HttpMethod";

class EhrenamtJustizServiceClass {
  public async pruefeGeburtsdatum(geburtsdatum: Date): Promise<string> {
    return await new Promise<string>((resolve, reject) => {
      fetch(
        `${EntityApiService.getBaseUrl()}/ehrenamtjustiz/pruefeGeburtsdatum`,
        getPOSTConfig(geburtsdatum)
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

  public async pruefeStaatsangehoerigkeit(
    staatsangehoerigkeit: string[]
  ): Promise<string> {
    return await new Promise<string>((resolve, reject) => {
      fetch(
        `${EntityApiService.getBaseUrl()}/ehrenamtjustiz/pruefeStaatsangehoerigkeit`,
        getPOSTConfig(staatsangehoerigkeit)
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

  public async pruefeWohnsitz(ort: string): Promise<string> {
    return await new Promise<string>((resolve, reject) => {
      fetch(
        `${EntityApiService.getBaseUrl()}/ehrenamtjustiz/pruefeWohnsitz`,
        getPOSTConfig(ort)
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

  public getEhrenamtJustizStatus(): Promise<EhrenamtJustizStatus> {
    return new Promise<EhrenamtJustizStatus>((resolve, reject) => {
      fetch(
        `${EntityApiService.getBaseUrl()}/ehrenamtjustiz/ehrenamtJustizStatus`,
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

  public async checkBackendStatus(): Promise<string> {
    return await new Promise<string>((resolve, reject) => {
      fetch(`${EntityApiService.getBaseUrl()}/actuator/health`, getGETConfig())
        .then(async (res) => {
          await res
            .text()
            .then(async () => {
              if (res.ok) return resolve("UP");
              EntityApiService.handleWrongResponse(HttpMethod.GET, res);
              reject();
            })
            .catch((reason) => reject(EntityApiService.handleError(reason)));
        })
        .catch((reason) => reject(EntityApiService.handleError(reason)));
    });
  }

  public async convertToCSVFile(
    t: (
      key: string,
      options?: { justiceType: string; count: number }
    ) => string,
    selectedUUIDs: string[],
    dateiName: string,
    status: string
  ) {
    await PersonApiService.lesenPersonenCSV(selectedUUIDs, status).then(
      (personenCSV) => {
        EhrenamtJustizServiceClass.convertToCSVFile(t, personenCSV, dateiName);
      }
    );
  }

  public async convertToCSVFileByPersonCSV(
    t: (
      key: string,
      options?: { justiceType: string; count: number }
    ) => string,
    personenCSV: PersonCSV[],
    dateiName: string
  ) {
    await EhrenamtJustizServiceClass.convertToCSVFile(
      t,
      personenCSV,
      dateiName
    );
  }

  private static async convertToCSVFile(
    t: (
      key: string,
      options?: { justiceType: string; count: number }
    ) => string,
    personenCSV: PersonCSV[],
    dateiName: string
  ) {
    const globalSettingsStore = useGlobalSettingsStore();
    const justiceType = formattedEhrenamtjustizart(
      t,
      globalSettingsStore.getKonfiguration?.ehrenamtjustizart,
      1
    );
    const csvConfig = mkConfig({
      fieldSeparator: ";",
      quoteStrings: false,
      columnHeaders: [
        {
          key: "familienname",
          displayLabel: t("api.csvHeader.familienname"),
        },
        {
          key: "geburtsname",
          displayLabel: t("api.csvHeader.geburtsname"),
        },
        {
          key: "vorname",
          displayLabel: t("api.csvHeader.vorname"),
        },
        {
          key: "geburtsdatum",
          displayLabel: t("api.csvHeader.geburtsdatum"),
        },
        {
          key: "geschlecht",
          displayLabel: t("api.csvHeader.geschlecht"),
        },
        {
          key: "ewoid",
          displayLabel: t("api.csvHeader.ewoid"),
        },
        {
          key: "akademischergrad",
          displayLabel: t("api.csvHeader.akademischergrad"),
        },
        {
          key: "geburtsort",
          displayLabel: t("api.csvHeader.geburtsort"),
        },
        {
          key: "geburtsland",
          displayLabel: t("api.csvHeader.geburtsland"),
        },
        {
          key: "familienstand",
          displayLabel: t("api.csvHeader.familienstand"),
        },
        {
          key: "staatsangehoerigkeit",
          displayLabel: t("api.csvHeader.staatsangehoerigkeit"),
        },
        {
          key: "wohnungsgeber",
          displayLabel: t("api.csvHeader.wohnungsgeber"),
        },
        {
          key: "strasse",
          displayLabel: t("api.csvHeader.strasse"),
        },
        {
          key: "hausnummer",
          displayLabel: t("api.csvHeader.hausnummer"),
        },
        {
          key: "appartmentnummer",
          displayLabel: t("api.csvHeader.appartmentnummer"),
        },
        {
          key: "buchstabehausnummer",
          displayLabel: t("api.csvHeader.buchstabehausnummer"),
        },
        {
          key: "stockwerk",
          displayLabel: t("api.csvHeader.stockwerk"),
        },
        {
          key: "teilnummerhausnummer",
          displayLabel: t("api.csvHeader.teilnummerhausnummer"),
        },
        {
          key: "adresszusatz",
          displayLabel: t("api.csvHeader.adresszusatz"),
        },
        {
          key: "postleitzahl",
          displayLabel: t("api.csvHeader.postleitzahl"),
        },
        {
          key: "ort",
          displayLabel: t("api.csvHeader.ort"),
        },
        {
          key: "inmuenchenseit",
          displayLabel: t("api.csvHeader.inmuenchenseit"),
        },
        {
          key: "wohnungsstatus",
          displayLabel: t("api.csvHeader.wohnungsstatus"),
        },
        {
          key: "auskunftssperre",
          displayLabel: t("api.csvHeader.auskunftssperre"),
        },
        {
          key: "derzeitausgeuebterberuf",
          displayLabel: t("api.csvHeader.derzeitausgeuebterberuf"),
        },
        {
          key: "arbeitgeber",
          displayLabel: t("api.csvHeader.arbeitgeber"),
        },
        {
          key: "ausgeuebteehrenaemter",
          displayLabel: t("api.csvHeader.ausgeuebteehrenaemter"),
        },
        {
          key: "telefonnummer",
          displayLabel: t("api.csvHeader.telefonnummer"),
        },
        {
          key: "telefongesch",
          displayLabel: t("api.csvHeader.telefongesch"),
        },
        {
          key: "telefonmobil",
          displayLabel: t("api.csvHeader.telefonmobil"),
        },
        {
          key: "mailadresse",
          displayLabel: t("api.csvHeader.mailadresse"),
        },
        {
          key: "onlinebewerbung",
          displayLabel: t("api.csvHeader.onlinebewerbung"),
        },
        {
          key: "warbereitstaetigals",
          displayLabel: t("api.csvHeader.warbereitstaetigals", {
            justiceType: justiceType,
            count: 0,
          }),
        },
        {
          key: "warbereitstaetigalsvorvorperiode",
          displayLabel: t("api.csvHeader.warbereitstaetigalsvorvorperiode", {
            justiceType: justiceType,
            count: 0,
          }),
        },
        {
          key: "bewerbungvom",
          displayLabel: t("api.csvHeader.bewerbungvom"),
        },
      ],
    });

    // Convert Array(personenCSV) --> CsvOutput-String
    const csv = generateCsv(csvConfig)(personenCSV);

    // create blob:
    const blob = asBlob(csvConfig)(csv);

    // Write CSV-file:
    const link = document.createElement("a");
    link.href = window.URL.createObjectURL(blob);
    const date = new Date();
    link.download = dateiName + date.toISOString().substring(0, 10) + ".csv";
    link.click();
  }
}

export const EhrenamtJustizService = new EhrenamtJustizServiceClass();
