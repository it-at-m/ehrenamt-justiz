import type EhrenamtJustizStatus from "@/types/EhrenamtJustizStatus";
import type PersonCSV from "@/types/PersonCSV";

import { asBlob, generateCsv, mkConfig } from "export-to-csv";

import EntityApiService from "@/api/EntityApiService";
import FetchUtils from "@/api/FetchUtils";
import { PersonApiService } from "@/api/PersonApiService";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import HttpMethod from "@/types/base/HttpMethod";

class EhrenamtJustizServiceClass {
  public async pruefeGeburtsdatum(geburtsdatum: Date): Promise<string> {
    return await new Promise<string>((resolve, reject) => {
      fetch(
        `${EntityApiService.getBaseUrl()}/ehrenamtjustiz/pruefeGeburtsdatum`,
        FetchUtils.getPOSTConfig(geburtsdatum)
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
        FetchUtils.getPOSTConfig(staatsangehoerigkeit)
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
        FetchUtils.getPOSTConfig(ort)
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
        FetchUtils.getGETConfig()
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
      fetch(
        `${EntityApiService.getBaseUrl()}/actuator/health`,
        FetchUtils.getGETConfig()
      )
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
    selectedUUIDs: string[],
    dateiName: string,
    status: string
  ) {
    await PersonApiService.lesenPersonenCSV(selectedUUIDs, status).then(
      (personenCSV) => {
        EhrenamtJustizServiceClass.convertToCSVFile(personenCSV, dateiName);
      }
    );
  }

  public async convertToCSVFileByPersonCSV(
    personenCSV: PersonCSV[],
    dateiName: string
  ) {
    await EhrenamtJustizServiceClass.convertToCSVFile(personenCSV, dateiName);
  }

  private static async convertToCSVFile(
    personenCSV: PersonCSV[],
    dateiName: string
  ) {
    const globalSettingsStore = useGlobalSettingsStore();

    const csvConfig = mkConfig({
      fieldSeparator: ";",
      quoteStrings: false,
      columnHeaders: [
        {
          key: "familienname",
          displayLabel: "Familienname",
        },
        {
          key: "geburtsname",
          displayLabel: "Geburtsname",
        },
        {
          key: "vorname",
          displayLabel: "Vorname",
        },
        {
          key: "geburtsdatum",
          displayLabel: "Geburtsdatum",
        },
        {
          key: "geschlecht",
          displayLabel: "Geschlecht",
        },
        {
          key: "ewo_id",
          displayLabel: "Ordnungsmerkmal",
        },
        {
          key: "akademischergrad",
          displayLabel: "Akademischer Grad",
        },
        {
          key: "geburtsort",
          displayLabel: "Geburtsort",
        },
        {
          key: "geburtsland",
          displayLabel: "Geburtsland",
        },
        {
          key: "familienstand",
          displayLabel: "Familienstand",
        },
        {
          key: "staatsangehoerigkeit",
          displayLabel: "Staatsangehörigkeit",
        },
        {
          key: "wohnungsgeber",
          displayLabel: "Wohnungsgeber",
        },
        {
          key: "strasse",
          displayLabel: "Straße",
        },
        {
          key: "hausnummer",
          displayLabel: "Hausnummer",
        },
        {
          key: "appartmentnummer",
          displayLabel: "Appartmentnummer",
        },
        {
          key: "buchstabehausnummer",
          displayLabel: "Buchstabe Hausnummer",
        },
        {
          key: "stockwerk",
          displayLabel: "Stockwerk",
        },
        {
          key: "teilnummerhausnummer",
          displayLabel: "Teilnummer Hausnummer",
        },
        {
          key: "adresszusatz",
          displayLabel: "Adresszusatz",
        },
        {
          key: "postleitzahl",
          displayLabel: "Postleitzahl",
        },
        {
          key: "ort",
          displayLabel: "Ort",
        },
        {
          key: "inmuenchenseit",
          displayLabel: "In München seit",
        },
        {
          key: "wohnungsstatus",
          displayLabel: "Wohnungsstatus",
        },
        {
          key: "auskunftssperre",
          displayLabel: "Auskunftssperren",
        },
        {
          key: "derzeitausgeuebterberuf",
          displayLabel: "Derzeit ausgeübter Beruf",
        },
        {
          key: "arbeitgeber",
          displayLabel: "Arbeitgeber",
        },
        {
          key: "ausgeuebteehrenaemter",
          displayLabel: "Ausgeübte Ehrenämter",
        },
        {
          key: "telefonnummer",
          displayLabel: "Telefonnummer (privat)",
        },
        {
          key: "telefongesch",
          displayLabel: "Telefonnummer (dienstlich)",
        },
        {
          key: "telefonmobil",
          displayLabel: "Telefonnummer (mobil)",
        },
        {
          key: "mailadresse",
          displayLabel: "Mailadresse",
        },
        {
          key: "onlinebewerbung",
          displayLabel: "Online Bewerbung",
        },
        {
          key: "warbereitstaetigals",
          displayLabel:
            "War bereits als " +
            globalSettingsStore.getKonfiguration?.ehrenamtjustizart +
            " tätig?",
        },
        {
          key: "bewerbungvom",
          displayLabel: "Bewerbung vom",
        },
      ],
    });

    // Konvertiert das Array(personenCSV) in einen CsvOutput-String basierend auf der Konfiguration:
    const csv = generateCsv(csvConfig)(personenCSV);

    // Blob erzeugen:
    const blob = asBlob(csvConfig)(csv);

    // CSV-Datei ausgeben:
    const link = document.createElement("a");
    link.href = window.URL.createObjectURL(blob);
    const date = new Date();
    link.download = dateiName + date.toISOString().substring(0, 10) + ".csv";
    link.click();
  }
}

export const EhrenamtJustizService = new EhrenamtJustizServiceClass();
