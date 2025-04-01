<template>
  <v-container>
    <v-row class="text-center">
      <v-hover
        v-slot="{ isHovering, props }"
        :disabled="
          !user || !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN')
        "
      >
        <v-col
          v-bind="props"
          :class="isHovering ? 'person-hover-color' : 'person-bewerbung-color'"
          cols="3"
          @click="router.push({ name: 'bewerbung.index' })"
        >
          <h2>
            {{
              ehrenamtJustizStatus
                ? ehrenamtJustizStatus.anzahlBewerbungen
                : "?"
            }}
            Bewerbungen
          </h2>
          <h2>{{ textAnzahlBewerbungen }}</h2>
        </v-col>
      </v-hover>
      <v-hover
        v-slot="{ isHovering, props }"
        :disabled="
          !user || !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN')
        "
      >
        <v-col
          v-bind="props"
          :class="isHovering ? 'person-hover-color' : 'person-konflikte-color'"
          cols="3"
          @click="router.push({ name: 'konflikte.index' })"
        >
          <h2>
            {{
              ehrenamtJustizStatus ? ehrenamtJustizStatus.anzahlKonflikte : "?"
            }}
            Konflikte
          </h2>
          <h2>{{ textAnzahlKonflikte }}</h2> </v-col
        >
      </v-hover>
      <v-hover
        v-slot="{ isHovering, props }"
        :disabled="
          !user || !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN')
        "
      >
        <v-col
          v-bind="props"
          :class="
            isHovering ? 'person-hover-color' : 'person-vorschlaege-color'
          "
          cols="3"
          @click="router.push({ name: 'vorschlaege.index' })"
        >
          <h2>
            {{
              ehrenamtJustizStatus
                ? ehrenamtJustizStatus.anzahlVorschlaege
                : "?"
            }}
            Vorschläge
          </h2>
          <h2>{{ textAnzahlVorschlaege }}</h2>
        </v-col>
      </v-hover>
      <v-col
        class="person-vorschlaege-color"
        cols="3"
      >
        <h2>
          {{
            ehrenamtJustizStatus
              ? ehrenamtJustizStatus.anzahlVorschlaegeNeu
              : "?"
          }}
          neue Vorschläge
        </h2>
        <h2>{{ textAnzahlNeueVorschlaege }}</h2>
        <v-btn
          :disabled="
            !user ||
            !user.authorities.includes(
              'READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE'
            )
          "
          @click="datenHerunterladen"
          >Daten herunterladen
        </v-btn>

        <v-btn
          :disabled="
            !user ||
            !user.authorities.includes(
              'READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE'
            )
          "
          @click="alsBenachrichtigtMarkierenBestaetigen"
          >Als benachrichtigt markieren
        </v-btn>
      </v-col>
    </v-row>
    <yes-no-dialog
      v-model="yesNoDialogVisible"
      dialogtitle="Als benachrichtigt markieren"
      dialogtext="Möchten Sie wirklich alle neuen Vorschläge als benachrichtigt markieren? Wenn Sie diese Aktion durchführen werden alle neuen Vorschläge als Benachrichtigt markiert. Ein erneutes Exportieren der Daten ist nur durch manuelles Selektieren möglich. Dies kann nicht Rückgängig gemacht werden."
      :is-animation="benachrichtigtMarkierenAnimationAktiv"
      @no="alsBenachrichtigtMarkierenNo"
      @yes="alsBenachrichtigtMarkierenYes"
    />
    <online-help-dialog-component
      component="Das ist die Onlinehilfe für den Starter (Under Construction)."
    />
  </v-container>
</template>

<script setup lang="ts">
import type EhrenamtJustizStatus from "@/types/EhrenamtJustizStatus";

import { onMounted, ref } from "vue";
import { useRouter } from "vue-router";
import { VBtn, VCol, VContainer, VHover, VRow } from "vuetify/components";

import { EhrenamtJustizService } from "@/api/EhrenamtJustizService";
import { PersonApiService } from "@/api/PersonApiService";
import YesNoDialog from "@/components/common/YesNoDialog.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
import { STATUS_INDICATORS } from "@/Constants.ts";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useSnackbarStore } from "@/stores/snackbar";
import { useUserStore } from "@/stores/user";

const ehrenamtJustizStatus = ref<EhrenamtJustizStatus | null>(null);
const textAnzahlBewerbungen = ref();
const textAnzahlKonflikte = ref();
const textAnzahlVorschlaege = ref();
const textAnzahlNeueVorschlaege = ref();
const snackbarStore = useSnackbarStore();
const router = useRouter();
const HUNDRET_UNICODE = 0x1f4af;
const X_UNICODE = 0x00d7;
const PERSON_UNICODE = 0x1f9cd;
const yesNoDialogVisible = ref(false);
const benachrichtigtMarkierenAnimationAktiv = ref(false);
const userStore = useUserStore();
const user = userStore.getUser;

onMounted(() => {
  load();
});

function load(): void {
  EhrenamtJustizService.getEhrenamtJustizStatus()
    .then((p) => {
      ehrenamtJustizStatus.value = p;

      textAnzahlBewerbungen.value = getAnzahlEhrenamtjustiz(
        ehrenamtJustizStatus.value.anzahlBewerbungen
      );
      textAnzahlKonflikte.value = getAnzahlEhrenamtjustiz(
        ehrenamtJustizStatus.value.anzahlKonflikte
      );
      textAnzahlVorschlaege.value = getAnzahlEhrenamtjustiz(
        ehrenamtJustizStatus.value.anzahlVorschlaege
      );
      textAnzahlNeueVorschlaege.value = getAnzahlEhrenamtjustiz(
        ehrenamtJustizStatus.value.anzahlVorschlaegeNeu
      );
    })
    .catch((error) => {
      snackbarStore.showMessage({
        level: STATUS_INDICATORS.ERROR,
        message: error,
      });
      router.back();
    });
}

function getAnzahlEhrenamtjustiz(anzahlBuerger: number): string {
  let many = false;
  let loop = anzahlBuerger;
  if (loop > 100) {
    many = true;
    loop = loop / 100;
  }
  let anzahlEhrenamtjustiz = "";
  if (many) {
    anzahlEhrenamtjustiz = anzahlEhrenamtjustiz.concat(
      String.fromCodePoint(HUNDRET_UNICODE)
    );
    anzahlEhrenamtjustiz = anzahlEhrenamtjustiz.concat(
      String.fromCodePoint(X_UNICODE)
    );
  }

  for (let i = 0; i < loop; i++) {
    anzahlEhrenamtjustiz = anzahlEhrenamtjustiz.concat(
      String.fromCodePoint(PERSON_UNICODE)
    );
  }
  return anzahlEhrenamtjustiz;
}

function datenHerunterladen() {
  PersonApiService.getNeueVorschlaege()
    .then((neueVorschlaege) => {
      const globalSettingsStore = useGlobalSettingsStore();
      EhrenamtJustizService.convertToCSVFileByPersonCSV(
        neueVorschlaege,
        globalSettingsStore.getKonfiguration?.ehrenamtjustizart +
          "_NEUE_VORSCHLAEGE_"
      );
    })
    .catch((error) => {
      snackbarStore.showMessage({
        level: STATUS_INDICATORS.ERROR,
        message: error,
      });
      router.back();
    });
}

function alsBenachrichtigtMarkierenBestaetigen() {
  yesNoDialogVisible.value = true;
}

function alsBenachrichtigtMarkierenNo() {
  yesNoDialogVisible.value = false;
}

async function alsBenachrichtigtMarkierenYes() {
  benachrichtigtMarkierenAnimationAktiv.value = true;
  await PersonApiService.alsBenachrichtigtMarkieren()
    .then(() => {
      load();
    })
    .catch((error) => {
      snackbarStore.showMessage({
        level: STATUS_INDICATORS.ERROR,
        message: error,
      });
    })
    .finally(() => {
      benachrichtigtMarkierenAnimationAktiv.value = false;
      yesNoDialogVisible.value = false;
    });
}
</script>

<style scoped>
.person-bewerbung-color {
  color: steelblue;
  border: 1px solid;
  padding: 10px;
  box-shadow: 5px 5px lightgray;
}

.person-konflikte-color {
  border: 1px solid;
  padding: 10px;
  color: crimson;
  box-shadow: 5px 5px lightgray;
}

.person-vorschlaege-color {
  border: 1px solid;
  padding: 10px;
  color: seagreen;
  box-shadow: 5px 5px lightgray;
}

.person-hover-color {
  cursor: pointer;
  color: rgb(var(--v-theme-secondary));
  border: 1px solid;
  padding: 10px;
  box-shadow: 10px 10px lightgray;
}
</style>
