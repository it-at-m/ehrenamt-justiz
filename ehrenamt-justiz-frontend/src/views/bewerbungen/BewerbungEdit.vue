<template>
  <v-container>
    <v-progress-linear
      v-if="isLoading"
      indeterminate
    />
    <bewerbung-form
      v-else
      v-model="bewerbungFormData"
      :is-animation="animationAktiv"
      @save="save"
      @cancel="cancel"
    />
    <online-help-dialog-component component="views.applicant.edit.onlinehelp" />
  </v-container>
</template>

<script setup lang="ts">
import type BewerbungFormData from "@/types/BewerbungFormData";

import { onMounted, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import { VContainer, VProgressLinear } from "vuetify/components";

import { PersonApiService } from "@/api/PersonApiService";
import BewerbungForm from "@/components/bewerbungen/BewerbungForm.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
import { PERSONENSTATUS, STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar";

const route = useRoute();
const router = useRouter();
const snackbarStore = useSnackbarStore();

const bewerbungFormData = ref<BewerbungFormData>({
  ewo_familienname: "",
  ewo_geburtsname: "",
  ewo_vorname: "",
  ewo_geburtsdatum: "",
  ewo_geschlecht: "",
  ewoid: "",
  ewo_akademischergrad: "",
  ewo_geburtsort: "",
  ewo_geburtsland: "",
  ewo_familienstand: "",
  ewo_staatsangehoerigkeit: [],
  ewo_wohnungsgeber: "",
  ewo_strasse: "",
  ewo_hausnummer: "",
  ewo_appartmentnummer: "",
  ewo_buchstabehausnummer: "",
  ewo_stockwerk: "",
  ewo_teilnummerhausnummer: "",
  ewo_adresszusatz: "",
  ewo_konfliktfeld: [],
  ewo_postleitzahl: "",
  ewo_ort: "",
  ewo_inmuenchenseit: "",
  ewo_wohnungsstatus: "",
  ewo_auskunftssperre: [],
  derzeitausgeuebterberuf: "",
  arbeitgeber: "",
  telefonnummer: "",
  telefongesch: "",
  telefonmobil: "",
  mailadresse: "",
  ausgeuebteehrenaemter: "",
  onlinebewerbung: "",
  neuervorschlag: "",
  warbereitstaetigals: "",
  warbereitstaetigalsvorvorperiode: "",
  bewerbungvom: "",
  konfigurationid: "",
  status: "",
  validierungdeaktivieren: false,
  action: "",
});
const isLoading = ref(true);
const animationAktiv = ref(false);
const personId = ref("");
const action = ref("");

onMounted(() => {
  personId.value = route.params.id as string;
  action.value = route.params.action as string;
  loadBewerbung();
});

function loadBewerbung(): void {
  PersonApiService.get(personId.value)
    .then((person) => {
      bewerbungFormData.value = {
        ewo_familienname: person.familienname,
        ewo_geburtsname: person.geburtsname,
        ewo_vorname: person.vorname,
        ewo_geburtsdatum: person.geburtsdatum,
        ewo_geschlecht: person.geschlecht,
        ewoid: person.ewoid,
        ewo_akademischergrad: person.akademischergrad,
        ewo_geburtsort: person.geburtsort,
        ewo_geburtsland: person.geburtsland,
        ewo_familienstand: person.familienstand,
        ewo_staatsangehoerigkeit: person.staatsangehoerigkeit,
        ewo_wohnungsgeber: person.wohnungsgeber,
        ewo_strasse: person.strasse,
        ewo_hausnummer: person.hausnummer,
        ewo_appartmentnummer: person.appartmentnummer,
        ewo_buchstabehausnummer: person.buchstabehausnummer,
        ewo_stockwerk: person.stockwerk,
        ewo_teilnummerhausnummer: person.teilnummerhausnummer,
        ewo_adresszusatz: person.adresszusatz,
        ewo_konfliktfeld: person.konfliktfeld,
        ewo_postleitzahl: person.postleitzahl,
        ewo_ort: person.ort,
        ewo_inmuenchenseit: person.inmuenchenseit,
        ewo_wohnungsstatus: person.wohnungsstatus,
        ewo_auskunftssperre: person.auskunftssperre,
        derzeitausgeuebterberuf: person.derzeitausgeuebterberuf,
        arbeitgeber: person.arbeitgeber,
        telefonnummer: person.telefonnummer,
        telefongesch: person.telefongesch,
        telefonmobil: person.telefonmobil,
        mailadresse: person.mailadresse,
        ausgeuebteehrenaemter: person.ausgeuebteehrenaemter,
        onlinebewerbung: person.onlinebewerbung,
        neuervorschlag: person.neuervorschlag,
        warbereitstaetigals: person.warbereitstaetigals,
        warbereitstaetigalsvorvorperiode:
          person.warbereitstaetigalsvorvorperiode,
        bewerbungvom: person.bewerbungvom,
        konfigurationid: person.konfigurationid,
        status: person.status,
        validierungdeaktivieren: false,
        action: action.value,
      };
      isLoading.value = false;
    })
    .catch((err) => {
      snackbarStore.showMessage(err);
      router.push({
        name: "bewerbung.index",
        params: {
          id: personId.value,
        },
      });
    });
}

function getPerson() {
  return {
    id: personId.value,
    familienname: bewerbungFormData.value.ewo_familienname,
    geburtsname: bewerbungFormData.value.ewo_geburtsname,
    vorname: bewerbungFormData.value.ewo_vorname,
    geburtsdatum: bewerbungFormData.value.ewo_geburtsdatum,
    geschlecht: bewerbungFormData.value.ewo_geschlecht,
    ewoid: bewerbungFormData.value.ewoid,
    akademischergrad: bewerbungFormData.value.ewo_akademischergrad,
    geburtsort: bewerbungFormData.value.ewo_geburtsort,
    geburtsland: bewerbungFormData.value.ewo_geburtsland,
    familienstand: bewerbungFormData.value.ewo_familienstand,
    staatsangehoerigkeit: bewerbungFormData.value.ewo_staatsangehoerigkeit,
    wohnungsgeber: bewerbungFormData.value.ewo_wohnungsgeber,
    strasse: bewerbungFormData.value.ewo_strasse,
    hausnummer: bewerbungFormData.value.ewo_hausnummer,
    appartmentnummer: bewerbungFormData.value.ewo_appartmentnummer,
    buchstabehausnummer: bewerbungFormData.value.ewo_buchstabehausnummer,
    stockwerk: bewerbungFormData.value.ewo_stockwerk,
    teilnummerhausnummer: bewerbungFormData.value.ewo_teilnummerhausnummer,
    adresszusatz: bewerbungFormData.value.ewo_adresszusatz,
    konfliktfeld: bewerbungFormData.value.ewo_konfliktfeld,
    postleitzahl: bewerbungFormData.value.ewo_postleitzahl,
    ort: bewerbungFormData.value.ewo_ort,
    inmuenchenseit: bewerbungFormData.value.ewo_inmuenchenseit,
    wohnungsstatus: bewerbungFormData.value.ewo_wohnungsstatus,
    auskunftssperre: bewerbungFormData.value.ewo_auskunftssperre,
    derzeitausgeuebterberuf: bewerbungFormData.value.derzeitausgeuebterberuf,
    arbeitgeber: bewerbungFormData.value.arbeitgeber,
    telefonnummer: bewerbungFormData.value.telefonnummer,
    telefongesch: bewerbungFormData.value.telefongesch,
    telefonmobil: bewerbungFormData.value.telefonmobil,
    mailadresse: bewerbungFormData.value.mailadresse,
    ausgeuebteehrenaemter: bewerbungFormData.value.ausgeuebteehrenaemter,
    onlinebewerbung: bewerbungFormData.value.onlinebewerbung,
    neuervorschlag: bewerbungFormData.value.neuervorschlag,
    warbereitstaetigals: bewerbungFormData.value.warbereitstaetigals,
    warbereitstaetigalsvorvorperiode:
      bewerbungFormData.value.warbereitstaetigalsvorvorperiode,
    bewerbungvom: bewerbungFormData.value.bewerbungvom,
    konfigurationid: bewerbungFormData.value.konfigurationid,
    status: bewerbungFormData.value.status,
  };
}

function save(): void {
  animationAktiv.value = true;
  PersonApiService.updatePerson(getPerson())
    .then((updatedBewerber) => {
      let nextRoute = "";
      switch (updatedBewerber.status) {
        case PERSONENSTATUS.STATUS_BEWERBUNG: {
          nextRoute = "bewerbung.index";
          break;
        }
        case PERSONENSTATUS.STATUS_KONFLIKT: {
          nextRoute = "konflikte.index";
          break;
        }
        case PERSONENSTATUS.STATUS_VORSCHLAG: {
          nextRoute = "vorschlaege.index";
          break;
        }
      }
      router.push({
        name: nextRoute,
      });
    })
    .catch((err) =>
      snackbarStore.showMessage({
        level: STATUS_INDICATORS.ERROR,
        message: err,
      })
    )
    .finally(() => (animationAktiv.value = false));
}

function cancel(): void {
  animationAktiv.value = true;
  PersonApiService.cancelBewerbung(getPerson())
    .then((canceledBewerber) => {
      let nextRoute = "";
      switch (canceledBewerber.status) {
        case PERSONENSTATUS.STATUS_BEWERBUNG: {
          nextRoute = "bewerbung.index";
          break;
        }
        case PERSONENSTATUS.STATUS_INERFASSUNG: {
          nextRoute = "bewerbung.index";
          break;
        }
        case PERSONENSTATUS.STATUS_KONFLIKT: {
          nextRoute = "konflikte.index";
          break;
        }
        case PERSONENSTATUS.STATUS_VORSCHLAG: {
          nextRoute = "vorschlaege.index";
          break;
        }
      }
      router.push({
        name: nextRoute,
      });
    })
    .catch((err) =>
      snackbarStore.showMessage({
        level: STATUS_INDICATORS.ERROR,
        message: err,
      })
    )
    .finally(() => (animationAktiv.value = false));
}
</script>
