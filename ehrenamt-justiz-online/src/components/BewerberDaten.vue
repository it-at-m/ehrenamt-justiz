<template>
  <v-container fluid>
    <v-form
      ref="form"
      class="m-form m-form--default"
      @submit="checkAndNextStep"
      @keydown.enter.prevent="checkAndNextStep"
    >
      <v-row dense>
        <muc-input
          v-model="onlineBewerbungFormData.vorname"
          :placeholder="t('bewerberDaten.vorname.label')"
          :label="t('bewerberDaten.vorname.label')"
          :required="true"
          :error-msg="errorMsgVorname"
          max="255"
        />
      </v-row>
      <v-row dense>
        <muc-input
          v-model="onlineBewerbungFormData.nachname"
          :placeholder="t('bewerberDaten.nachname.label')"
          :label="t('bewerberDaten.nachname.label')"
          :required="true"
          :error-msg="errorMsgNachname"
          max="300"
        />
      </v-row>
      <v-row dense>
        <muc-input
          v-model="onlineBewerbungFormData.geburtsdatum"
          :placeholder="t('bewerberDaten.geburtsdatum.label')"
          :label="t('bewerberDaten.geburtsdatum.label')"
          :required="true"
          type="date"
          :error-msg="errorMsgGeburtsdatum"
          @keydown.ctrl.v="zwischenablageEinfuegen()"
        />
      </v-row>
      <v-row dense>
        <muc-input
          v-model="onlineBewerbungFormData.beruf"
          :placeholder="t('bewerberDaten.beruf.label')"
          :label="t('bewerberDaten.beruf.label')"
          :required="true"
          :error-msg="errorMsgBeruf"
          max="255"
        />
      </v-row>
      <v-row dense>
        <muc-input
          v-model="onlineBewerbungFormData.telefonnummer"
          :placeholder="t('bewerberDaten.telefonnummer.label')"
          :label="t('bewerberDaten.telefonnummer.label')"
          :error-msg="errorMsgTelefonnummer"
          max="255"
        />
      </v-row>
      <v-row dense>
        <muc-input
          v-model="onlineBewerbungFormData.mail"
          :placeholder="t('bewerberDaten.mail.placeholder')"
          :label="t('bewerberDaten.mail.label')"
          :required="true"
          :error-msg="errorMsgMail"
          max="150"
        />
      </v-row>
    </v-form>
  </v-container>
  <muc-divider />
  <v-row class="button-row">
    <v-col
      class="button-wrapper"
      xs="12"
      sm="12"
      md="3"
      lg="3"
      xl="3"
    >
      <muc-button
        variant="primary"
        @click="previousStep"
      >
        {{ t("bewerberDaten.buttons.zurueck") }}
        <svg class="m-button__icon">
          <use xlink:href="#icon-arrow-left"></use>
        </svg>
      </muc-button>
    </v-col>
    <v-col
      class="button-wrapper"
      xs="12"
      sm="12"
      md="3"
      lg="3"
      xl="3"
    >
      <muc-button
        variant="primary"
        @click="checkAndNextStep"
      >
        {{ t("bewerberDaten.buttons.weiter") }}
        <svg class="m-button__icon">
          <use xlink:href="#icon-arrow-right"></use>
        </svg>
      </muc-button>
    </v-col>
    <v-col
      class="button-wrapper"
      xs="12"
      sm="12"
      md="3"
      lg="3"
      xl="3"
    >
      <muc-button
        variant="secondary"
        @click="clearInputs()"
        >{{ t("bewerberDaten.buttons.leeren") }}
        <svg class="m-button__icon">
          <use xlink:href="#icon-close"></use>
        </svg>
      </muc-button>
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import type OnlineBewerbungData from "@/types/OnlineBewerbungData.ts";

import { MucButton, MucDivider, MucInput } from "@muenchen/muc-patternlab-vue";
import moment from "moment";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import { VCol, VForm, VRow } from "vuetify/components";

import { useActiveKonfigurationStore } from "@/stores/activeconfig.ts";

const form = ref();
const errorMsgVorname = ref("");
const errorMsgNachname = ref("");
const errorMsgGeburtsdatum = ref("");
const errorMsgBeruf = ref("");
const errorMsgTelefonnummer = ref("");
const errorMsgMail = ref("");
const REGEXP_TELEFON =
  /^$|^((\+|00)[1-9]\d{0,3}|0 ?[1-9]|\(00? ?[1-9][\d ]*\))[\d\-/ ]*$/;
const REGEX_MAIL =
  /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[\d{1,3}\.\d{1,3}\.\d{1,3}\.\d{1,3}])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
const { t } = useI18n();

const props = defineProps<{
  modelValue: OnlineBewerbungData;
}>();
const emits = defineEmits<{
  "update:modelValue": [v: OnlineBewerbungData];
  previousStep: [];
  checkAndNextStep: [];
}>();
const onlineBewerbungFormData = computed({
  get: () => props.modelValue,
  set: (v) => emits("update:modelValue", v),
});

const previousStep = () => {
  emits("previousStep");
};

const checkAndNextStep = () => {
  if (isValid()) {
    emits("checkAndNextStep");
  }
};

async function zwischenablageEinfuegen(): Promise<void> {
  // Funktioniert in Firefox (getestet mit Version 115.17.0esr (64-Bit), wenn Property "dom.events.asyncClipboard.readText" auf true gesetzt wird (Vorher im Adressfeld "about:config" eingeben!))
  // in Edge (getestet mit Version 131.0.2903.51 ) wird um Erlaubnis gefragt
  // bei Crome (getestet mit Version Version 130.0.6723.117) funktioniert es generell
  // Datum in der Zwischenablage muss im Format tt.mm.jjjj sein
  try {
    const text = await navigator.clipboard.readText();
    const parts = text.split(".");

    onlineBewerbungFormData.value.geburtsdatum =
      parts[2] + "-" + parts[1] + "-" + parts[0];
  } catch (err) {
    /* eslint-disable no-console */
    console.log("Failed to read clipboard contents: ", err);
    /* eslint-enable no-console */
  }
}

function clearInputs(): void {
  onlineBewerbungFormData.value.nachname = "";
  onlineBewerbungFormData.value.vorname = "";
  onlineBewerbungFormData.value.beruf = "";
  onlineBewerbungFormData.value.geburtsdatum = "";
  onlineBewerbungFormData.value.mail = "";
  onlineBewerbungFormData.value.telefonnummer = "";
  onlineBewerbungFormData.value.dateiVerfassungstreue = null;
}

function isValid() {
  let isValid = true;

  isValid = validateVorname(isValid);

  isValid = validateNachname(isValid);

  isValid = validateGeburtsdatum(isValid);

  isValid = validateBeruf(isValid);

  isValid = validateTelefonnummer(isValid);

  isValid = validateMail(isValid);

  return isValid;
}

function validateVorname(isValid: boolean) {
  // Vorname ist ein Pflichtfeld
  errorMsgVorname.value = "";
  if (!onlineBewerbungFormData.value.vorname) {
    errorMsgVorname.value = t("bewerberDaten.vorname.pflichtfeld");
    isValid = false;
  } else if (onlineBewerbungFormData.value.vorname.length > 255) {
    errorMsgVorname.value = t("bewerberDaten.vorname.maxLaenge");
    isValid = false;
  }
  return isValid;
}

function validateNachname(isValid: boolean) {
  // Nachname ist ein Pflichtfeld
  errorMsgNachname.value = "";
  if (!onlineBewerbungFormData.value.nachname) {
    errorMsgNachname.value = t("bewerberDaten.nachname.pflichtfeld");
    isValid = false;
  } else if (onlineBewerbungFormData.value.nachname.length > 300) {
    errorMsgNachname.value = t("bewerberDaten.nachname.maxLaenge");
    isValid = false;
  }
  return isValid;
}

function validateGeburtsdatum(isValid: boolean) {
  // Geburtsdatum ist ein Pflichtfeld und Alter prüfen
  errorMsgGeburtsdatum.value = "";
  if (!onlineBewerbungFormData.value.geburtsdatum) {
    errorMsgGeburtsdatum.value = t("bewerberDaten.geburtsdatum.pflichtfeld");
    isValid = false;
  } else {
    const alter = moment(
      useActiveKonfigurationStore().getKonfiguration?.amtsperiodevon
    ).diff(onlineBewerbungFormData.value.geburtsdatum, "years");

    const altervon = useActiveKonfigurationStore().getKonfiguration?.altervon;
    const alterbis = useActiveKonfigurationStore().getKonfiguration?.alterbis;
    if ((alterbis && alter > alterbis) || (altervon && alter < altervon)) {
      isValid = false;
      errorMsgGeburtsdatum.value = t("bewerberDaten.geburtsdatum.invalide", {
        altervon: altervon,
        alterbis: alterbis,
      });
    }
  }
  return isValid;
}

function validateBeruf(isValid: boolean) {
  // Beruf ist ein Pflichtfeld
  errorMsgBeruf.value = "";
  if (!onlineBewerbungFormData.value.beruf) {
    errorMsgBeruf.value = t("bewerberDaten.beruf.pflichtfeld");
    isValid = false;
  } else if (onlineBewerbungFormData.value.beruf.length > 255) {
    errorMsgBeruf.value = t("bewerberDaten.beruf.maxLaenge");
    isValid = false;
  }
  return isValid;
}

function validateTelefonnummer(isValid: boolean) {
  // Telefonnummer ist ein Pflichtfeld und Format prüfen
  errorMsgTelefonnummer.value = "";

  if (
    onlineBewerbungFormData.value.telefonnummer &&
    !REGEXP_TELEFON.test(onlineBewerbungFormData.value.telefonnummer)
  ) {
    errorMsgTelefonnummer.value = t("bewerberDaten.telefonnummer.invalide");
    isValid = false;
  }
  return isValid;
}

function validateMail(isValid: boolean) {
  // Mail ist ein Pflichtfeld und Format prüfen
  errorMsgMail.value = "";
  if (!onlineBewerbungFormData.value.mail) {
    errorMsgMail.value = t("bewerberDaten.mail.pflichtfeld");
    isValid = false;
  } else if (!REGEX_MAIL.test(onlineBewerbungFormData.value.mail)) {
    errorMsgMail.value = t("bewerberDaten.mail.invalide");
    isValid = false;
  }
  return isValid;
}
</script>

<style scoped>
.m-form-group {
  width: 100%;
}
</style>
