<template>
  <v-container>
    <v-progress-linear
      v-if="isLoading"
      indeterminate
    />
    <template v-else>
      <konflikte-loesen-form
        v-model="konfliktLoesenFormData"
        :is-saving-animation="isSavingAnimation"
        @save="save"
      />
    </template>
  </v-container>
</template>

<script setup lang="ts">
import type KonfliktLoesenFormData from "@/types/KonfliktLoesenFormData";

import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { VContainer, VProgressLinear } from "vuetify/components";

import { EWOBuergerApiService } from "@/api/EWOBuergerApiService";
import { PersonApiService } from "@/api/PersonApiService";
import KonflikteLoesenForm from "@/components/konflikte/KonflikteLoesenForm.vue";
import { STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar";

const route = useRoute();
const router = useRouter();
const snackbarStore = useSnackbarStore();

const konfliktLoesenFormData = ref<KonfliktLoesenFormData>({
  person_familienname: "",
  person_geburtsname: "",
  person_vorname: "",
  person_geburtsdatum: "",
  person_geschlecht: "",
  person_ewoid: "",
  person_akademischergrad: "",
  person_geburtsort: "",
  person_geburtsland: "",
  person_familienstand: "",
  person_staatsangehoerigkeit: [],
  person_wohnungsgeber: "",
  person_strasse: "",
  person_hausnummer: "",
  person_appartmentnummer: "",
  person_buchstabehausnummer: "",
  person_stockwerk: "",
  person_teilnummerhausnummer: "",
  person_adresszusatz: "",
  person_konfliktfeld: [],
  person_postleitzahl: "",
  person_ort: "",
  person_inmuenchenseit: "",
  person_wohnungsstatus: "",
  person_auskunftssperre: [],
  person_derzeitausgeuebterberuf: "",
  person_arbeitgeber: "",
  person_telefonnummer: "",
  person_telefongesch: "",
  person_telefonmobil: "",
  person_mailadresse: "",
  person_ausgeuebteehrenaemter: "",
  person_onlinebewerbung: "",
  person_neuervorschlag: "",
  person_warbereitstaetigals: "",
  person_warbereitstaetigalsvorvorperiode: "",
  person_bewerbungvom: "",
  person_konfigurationid: "",
  person_status: "",
  person_validierungdeaktivieren: false,
  ewo_familienname: "",
  ewo_geburtsname: "",
  ewo_vorname: "",
  ewo_geburtsdatum: "",
  ewo_geschlecht: "unbekannt",
  ewo_ewoid: "",
  ewo_akademischergrad: "",
  ewo_geburtsort: "",
  ewo_geburtsland: "",
  ewo_familienstand: "",
  ewo_wohnungsgeber: "",
  ewo_strasse: "",
  ewo_hausnummer: "",
  ewo_appartmentnummer: "",
  ewo_buchstabehausnummer: "",
  ewo_stockwerk: "",
  ewo_teilnummerhausnummer: "",
  ewo_adresszusatz: "",
  ewo_konfliktfeld: [""],
  ewo_postleitzahl: "",
  ewo_ort: "",
  ewo_inmuenchenseit: "",
  ewo_wohnungsstatus: "Hauptwohnung",
  ewo_auskunftssperre: [""],
  ewo_staatsangehoerigkeit: [""],
});

const isLoading = ref(true);
const isSavingAnimation = ref(false);
const personenDatenId = ref("");

onMounted(() => {
  personenDatenId.value = route.params.id as string;
  loadPerson();
});

async function loadPerson(): Promise<void> {
  // Get Data from Person
  await PersonApiService.get(personenDatenId.value)
    .then((personenDaten) => {
      konfliktLoesenFormData.value.person_familienname =
        personenDaten.familienname;
      konfliktLoesenFormData.value.person_geburtsname =
        personenDaten.geburtsname;
      konfliktLoesenFormData.value.person_vorname = personenDaten.vorname;
      konfliktLoesenFormData.value.person_geburtsdatum =
        personenDaten.geburtsdatum;
      konfliktLoesenFormData.value.person_geschlecht = personenDaten.geschlecht;
      konfliktLoesenFormData.value.person_ewoid = personenDaten.ewoid;
      konfliktLoesenFormData.value.person_akademischergrad =
        personenDaten.akademischergrad;
      konfliktLoesenFormData.value.person_geburtsort = personenDaten.geburtsort;
      konfliktLoesenFormData.value.person_geburtsland =
        personenDaten.geburtsland;
      konfliktLoesenFormData.value.person_familienstand =
        personenDaten.familienstand;
      konfliktLoesenFormData.value.person_staatsangehoerigkeit =
        personenDaten.staatsangehoerigkeit;
      konfliktLoesenFormData.value.person_wohnungsgeber =
        personenDaten.wohnungsgeber;
      konfliktLoesenFormData.value.person_strasse = personenDaten.strasse;
      konfliktLoesenFormData.value.person_hausnummer = personenDaten.hausnummer;
      konfliktLoesenFormData.value.person_appartmentnummer =
        personenDaten.appartmentnummer;
      konfliktLoesenFormData.value.person_buchstabehausnummer =
        personenDaten.buchstabehausnummer;
      konfliktLoesenFormData.value.person_stockwerk = personenDaten.stockwerk;
      konfliktLoesenFormData.value.person_teilnummerhausnummer =
        personenDaten.teilnummerhausnummer;
      konfliktLoesenFormData.value.person_adresszusatz =
        personenDaten.adresszusatz;
      konfliktLoesenFormData.value.person_konfliktfeld =
        personenDaten.konfliktfeld;
      konfliktLoesenFormData.value.person_postleitzahl =
        personenDaten.postleitzahl;
      konfliktLoesenFormData.value.person_ort = personenDaten.ort;
      konfliktLoesenFormData.value.person_inmuenchenseit =
        personenDaten.inmuenchenseit;
      konfliktLoesenFormData.value.person_wohnungsstatus =
        personenDaten.wohnungsstatus;
      konfliktLoesenFormData.value.person_auskunftssperre =
        personenDaten.auskunftssperre;
      konfliktLoesenFormData.value.person_derzeitausgeuebterberuf =
        personenDaten.derzeitausgeuebterberuf;
      konfliktLoesenFormData.value.person_arbeitgeber =
        personenDaten.arbeitgeber;
      konfliktLoesenFormData.value.person_telefonnummer =
        personenDaten.telefonnummer;
      konfliktLoesenFormData.value.person_telefongesch =
        personenDaten.telefongesch;
      konfliktLoesenFormData.value.person_telefonmobil =
        personenDaten.telefonmobil;
      konfliktLoesenFormData.value.person_mailadresse =
        personenDaten.mailadresse;
      konfliktLoesenFormData.value.person_ausgeuebteehrenaemter =
        personenDaten.ausgeuebteehrenaemter;
      konfliktLoesenFormData.value.person_onlinebewerbung =
        personenDaten.onlinebewerbung;
      konfliktLoesenFormData.value.person_neuervorschlag =
        personenDaten.neuervorschlag;
      konfliktLoesenFormData.value.person_warbereitstaetigals =
        personenDaten.warbereitstaetigals;
      konfliktLoesenFormData.value.person_warbereitstaetigalsvorvorperiode =
        personenDaten.warbereitstaetigalsvorvorperiode;
      konfliktLoesenFormData.value.person_bewerbungvom =
        personenDaten.bewerbungvom;
      konfliktLoesenFormData.value.person_konfigurationid =
        personenDaten.konfigurationid;
      konfliktLoesenFormData.value.person_status = personenDaten.status;

      // Get data from EWO
      EWOBuergerApiService.ewoSucheMitOM(personenDaten.ewoid)
        .then((eWOBuerger) => {
          konfliktLoesenFormData.value.ewo_familienname =
            eWOBuerger.familienname;
          konfliktLoesenFormData.value.ewo_geburtsname = eWOBuerger.geburtsname;
          konfliktLoesenFormData.value.ewo_vorname = eWOBuerger.vorname;
          konfliktLoesenFormData.value.ewo_geburtsdatum =
            eWOBuerger.geburtsdatum;
          konfliktLoesenFormData.value.ewo_geschlecht = eWOBuerger.geschlecht;
          konfliktLoesenFormData.value.ewo_ewoid = eWOBuerger.ordnungsmerkmal;
          konfliktLoesenFormData.value.ewo_akademischergrad =
            eWOBuerger.akademischergrad;
          konfliktLoesenFormData.value.ewo_geburtsort = eWOBuerger.geburtsort;
          konfliktLoesenFormData.value.ewo_geburtsland = eWOBuerger.geburtsland;
          konfliktLoesenFormData.value.ewo_familienstand =
            eWOBuerger.familienstand;
          konfliktLoesenFormData.value.ewo_staatsangehoerigkeit =
            eWOBuerger.staatsangehoerigkeit;
          konfliktLoesenFormData.value.ewo_wohnungsgeber =
            eWOBuerger.wohnungsgeber;
          konfliktLoesenFormData.value.ewo_strasse = eWOBuerger.strasse;
          konfliktLoesenFormData.value.ewo_hausnummer = eWOBuerger.hausnummer;
          konfliktLoesenFormData.value.ewo_appartmentnummer =
            eWOBuerger.appartmentnummer;
          konfliktLoesenFormData.value.ewo_buchstabehausnummer =
            eWOBuerger.buchstabehausnummer;
          konfliktLoesenFormData.value.ewo_stockwerk = eWOBuerger.stockwerk;
          konfliktLoesenFormData.value.ewo_teilnummerhausnummer =
            eWOBuerger.teilnummerhausnummer;
          konfliktLoesenFormData.value.ewo_adresszusatz =
            eWOBuerger.adresszusatz;
          konfliktLoesenFormData.value.ewo_konfliktfeld =
            eWOBuerger.konfliktfeld;
          konfliktLoesenFormData.value.ewo_postleitzahl =
            eWOBuerger.postleitzahl;
          konfliktLoesenFormData.value.ewo_ort = eWOBuerger.ort;
          konfliktLoesenFormData.value.ewo_inmuenchenseit =
            eWOBuerger.inmuenchenseit;
          konfliktLoesenFormData.value.ewo_wohnungsstatus =
            eWOBuerger.wohnungsstatus;
          konfliktLoesenFormData.value.ewo_auskunftssperre =
            eWOBuerger.auskunftssperre;
          isLoading.value = false;
        })
        .catch(() => {
          snackbarStore.showMessage({
            message:
              "Für die Person " +
              personenDaten.vorname +
              " " +
              personenDaten.familienname +
              " mit OM " +
              personenDaten.ewoid +
              " wurden keine Daten in EWO gefunden, Womöglich ist diese verzogen oder verstorben. Bitte prüfen Sie dies im Einwohnermeldesystem.",
            level: STATUS_INDICATORS.ERROR,
          });
          router.push({
            name: "konflikte.index",
            params: {
              id: personenDatenId.value,
            },
          });
        });
    })
    .catch((err) => {
      snackbarStore.showMessage(err);
      router.push({
        name: "bewerbung.index",
        params: {
          id: personenDatenId.value,
        },
      });
    });
}

function save(): void {
  isSavingAnimation.value = true;
  PersonApiService.updatePerson({
    id: personenDatenId.value,
    familienname: konfliktLoesenFormData.value.person_familienname,
    geburtsname: konfliktLoesenFormData.value.person_geburtsname,
    vorname: konfliktLoesenFormData.value.person_vorname,
    geburtsdatum: konfliktLoesenFormData.value.person_geburtsdatum,
    geschlecht: konfliktLoesenFormData.value.person_geschlecht,
    ewoid: konfliktLoesenFormData.value.person_ewoid,
    akademischergrad: konfliktLoesenFormData.value.person_akademischergrad,
    geburtsort: konfliktLoesenFormData.value.person_geburtsort,
    geburtsland: konfliktLoesenFormData.value.person_geburtsland,
    familienstand: konfliktLoesenFormData.value.person_familienstand,
    staatsangehoerigkeit:
      konfliktLoesenFormData.value.person_staatsangehoerigkeit,
    wohnungsgeber: konfliktLoesenFormData.value.person_wohnungsgeber,
    strasse: konfliktLoesenFormData.value.person_strasse,
    hausnummer: konfliktLoesenFormData.value.person_hausnummer,
    appartmentnummer: konfliktLoesenFormData.value.person_appartmentnummer,
    buchstabehausnummer:
      konfliktLoesenFormData.value.person_buchstabehausnummer,
    stockwerk: konfliktLoesenFormData.value.person_stockwerk,
    teilnummerhausnummer:
      konfliktLoesenFormData.value.person_teilnummerhausnummer,
    adresszusatz: konfliktLoesenFormData.value.person_adresszusatz,
    konfliktfeld: konfliktLoesenFormData.value.person_konfliktfeld,
    postleitzahl: konfliktLoesenFormData.value.person_postleitzahl,
    ort: konfliktLoesenFormData.value.person_ort,
    inmuenchenseit: konfliktLoesenFormData.value.person_inmuenchenseit,
    wohnungsstatus: konfliktLoesenFormData.value.person_wohnungsstatus,
    auskunftssperre: konfliktLoesenFormData.value.person_auskunftssperre,
    derzeitausgeuebterberuf:
      konfliktLoesenFormData.value.person_derzeitausgeuebterberuf,
    arbeitgeber: konfliktLoesenFormData.value.person_arbeitgeber,
    telefonnummer: konfliktLoesenFormData.value.person_telefonnummer,
    telefongesch: konfliktLoesenFormData.value.person_telefongesch,
    telefonmobil: konfliktLoesenFormData.value.person_telefonmobil,
    mailadresse: konfliktLoesenFormData.value.person_mailadresse,
    ausgeuebteehrenaemter:
      konfliktLoesenFormData.value.person_ausgeuebteehrenaemter,
    onlinebewerbung: konfliktLoesenFormData.value.person_onlinebewerbung,
    neuervorschlag: konfliktLoesenFormData.value.person_neuervorschlag,
    warbereitstaetigals:
      konfliktLoesenFormData.value.person_warbereitstaetigals,
    warbereitstaetigalsvorvorperiode:
      konfliktLoesenFormData.value.person_warbereitstaetigalsvorvorperiode,
    bewerbungvom: konfliktLoesenFormData.value.person_bewerbungvom,
    konfigurationid: konfliktLoesenFormData.value.person_konfigurationid,
    status: konfliktLoesenFormData.value.person_status,
  })
    .then(() => {
      router.push({
        name: "konflikte.index",
      });
    })
    .catch((err) =>
      snackbarStore.showMessage({
        level: STATUS_INDICATORS.ERROR,
        message: err,
      })
    )
    .finally(() => (isSavingAnimation.value = false));
}
</script>

<style scoped>
.auskunftssperre {
  color: red;
}
</style>
