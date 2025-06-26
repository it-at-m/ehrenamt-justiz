<template>
  <muc-card-container class="d-flex align-center flex-column">
    <muenchen-banner />
    <muc-card
      v-if="useActiveKonfigurationStore().getKonfiguration"
      class="d-flex align-center flex-column"
      title=""
    >
      <template #headerPrefix
        ><div id="headerOnlineBewerbung">
          <h2>{{ t("mainView.header") }}</h2>
          <logo-l-h-m />
        </div>
      </template>
      <template #content>
        <div ref="hinweise">
          <muc-banner
            v-if="bewerbunggespeichertergebbnis == 'OK'"
            :title="t('mainView.hinweise.ok.title')"
            type="success"
          >
            <div>
              {{ t("mainView.hinweise.ok.text") }}
            </div>
          </muc-banner>
          <muc-banner
            v-if="bewerbunggespeichertergebbnis == 'ERROR'"
            type="emergency"
          >
            <div>
              {{ t("mainView.hinweise.error.text") }}
            </div>
          </muc-banner>
          <muc-banner
            v-if="bewerbunggespeichertergebbnis == 'TOO_MANY_REQUESTS'"
            type="emergency"
          >
            <div>
              {{ t("mainView.hinweise.toManyRequests.text") }}
            </div>
          </muc-banner>
          <muc-banner
            v-if="bewerbunggespeichertergebbnis == 'EINGABEFEHLER'"
            type="emergency"
          >
            <div>{{ t("mainView.hinweise.eingabeFehler.text") }}</div>
          </muc-banner>
          <muc-banner
            v-if="technischerfehler != ''"
            type="emergency"
          >
            <div>
              {{
                t("mainView.hinweise.technischerFehler.text", {
                  cause: technischerfehler,
                })
              }}
            </div>
          </muc-banner>
        </div>
        <v-container class="v-card-main-container">
          <muc-callout type="info">
            <template #content>
              {{ t("mainView.info") }}
              <muc-link
                label="hier"
                href="https://stadt.muenchen.de/service/info/hauptabteilung-ii-buergerangelegenheiten/1080614/"
              />
            </template>
          </muc-callout>
          <v-container fluid>
            <v-form
              ref="form"
              class="m-form m-form--default"
              @keydown.enter.prevent="speichern"
            >
              <v-row dense>
                <muc-input
                  v-model="onlineBewerbungFormData.vorname"
                  :placeholder="t('mainView.form.vorname.label')"
                  :label="t('mainView.form.vorname.label')"
                  :required="true"
                  :error-msg="errorMsgVorname"
                  max="255"
                />
              </v-row>
              <v-row dense>
                <muc-input
                  v-model="onlineBewerbungFormData.nachname"
                  :placeholder="t('mainView.form.nachname.label')"
                  :label="t('mainView.form.nachname.label')"
                  :required="true"
                  :error-msg="errorMsgNachname"
                  max="300"
                />
              </v-row>
              <v-row dense>
                <muc-input
                  v-model="onlineBewerbungFormData.geburtsdatum"
                  :placeholder="t('mainView.form.geburtsdatum.label')"
                  :label="t('mainView.form.geburtsdatum.label')"
                  :required="true"
                  type="date"
                  :error-msg="errorMsgGeburtsdatum"
                  @keydown.ctrl.v="zwischenablageEinfuegen()"
                />
              </v-row>
              <v-row dense>
                <muc-input
                  v-model="onlineBewerbungFormData.beruf"
                  :placeholder="t('mainView.form.beruf.label')"
                  :label="t('mainView.form.beruf.label')"
                  :required="true"
                  :error-msg="errorMsgBeruf"
                  max="255"
                />
              </v-row>
              <v-row dense>
                <muc-input
                  v-model="onlineBewerbungFormData.telefonnummer"
                  :placeholder="t('mainView.form.telefonnummer.label')"
                  :label="t('mainView.form.telefonnummer.label')"
                  :error-msg="errorMsgTelefonnummer"
                  max="255"
                />
              </v-row>
              <v-row dense>
                <muc-input
                  v-model="onlineBewerbungFormData.mail"
                  :placeholder="t('mainView.form.mail.placeholder')"
                  :label="t('mainView.form.mail.label')"
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
              sm="2"
              md="2"
              lg="2"
              xl="2"
            >
              <muc-button
                variant="primary"
                @click="speichern()"
              >
                {{ t("mainView.buttons.bewerben") }}
                <svg class="m-button__icon">
                  <use xlink:href="#icon-floppy"></use>
                </svg>
              </muc-button>
            </v-col>
            <v-col
              class="button-wrapper"
              sm="2"
              md="2"
              lg="2"
              xl="2"
            >
              <muc-button
                variant="secondary"
                @click="clearInputs()"
                >{{ t("mainView.buttons.leeren") }}
                <svg class="m-button__icon">
                  <use xlink:href="#icon-close"></use>
                </svg>
              </muc-button>
            </v-col>
            <v-col
              class="button-wrapper"
              sm="2"
              md="2"
              lg="2"
              xl="2"
            >
              <muc-percentage-spinner
                v-if="isSavingAnimation"
                size="40"
              />
            </v-col>
          </v-row>
          <muc-callout type="info">
            <template #header>{{
              t("mainView.datenschutzErklaerung.header")
            }}</template>
            <template #content>
              {{ t("mainView.datenschutzErklaerung.content1") }}
              <br /><br />
              {{ t("mainView.datenschutzErklaerung.content2") }}
              <br /><br />
              {{ t("mainView.datenschutzErklaerung.content3") }}
              <br />
              {{ t("mainView.datenschutzErklaerung.content4") }}
              <br />
              {{ t("mainView.datenschutzErklaerung.content5") }}
              <br /><br />
              {{ t("mainView.datenschutzErklaerung.content6") }}
              <muc-link
                :label="t('mainView.datenschutzErklaerung.links.datenschutz')"
                href="https://stadt.muenchen.de/infos/impressum-datenschutz.html#datenschutz"
              />
              {{ t("mainView.datenschutzErklaerung.content7") }}
              <br /><br />
              <muc-link
                :label="
                  t(
                    'mainView.datenschutzErklaerung.links.elektronischeKommunikation'
                  )
                "
                href="https://stadt.muenchen.de/infos/elektronische-kommunikation.html"
              />
              <br /><br />
              {{ t("mainView.datenschutzErklaerung.content8") }}
              <br /><br />
              {{ t("mainView.datenschutzErklaerung.content9") }}
            </template>
          </muc-callout>
        </v-container>
      </template>
    </muc-card>
    <muc-percentage-spinner
      v-else
      size="100"
    />
  </muc-card-container>
</template>

<script setup lang="ts">
import type OnlineBewerbungData from "@/types/OnlineBewerbungData";

import {
  MucBanner,
  MucButton,
  MucCallout,
  MucCard,
  MucCardContainer,
  MucDivider,
  MucInput,
  MucLink,
  MucPercentageSpinner,
} from "@muenchen/muc-patternlab-vue";
import moment from "moment/moment";
import { ref } from "vue";
import { useI18n } from "vue-i18n";
import { VCol, VContainer, VForm, VRow } from "vuetify/components";

import { EhrenamtJustizOnlineServiceClass } from "@/api/EhrenamtJustizOnlineService";
import LogoLHM from "@/components/LogoLHM.vue";
import MuenchenBanner from "@/components/MuenchenBanner.vue";
import { useActiveKonfigurationStore } from "@/stores/activeconfig";

const form = ref();
const { t } = useI18n();
const ehrenamtJustizOnlineService = ref(
  new EhrenamtJustizOnlineServiceClass(t)
);
const hinweise = ref<HTMLInputElement | null>(null);
const isSavingAnimation = ref(false);
const bewerbunggespeichertergebbnis = ref("");
const technischerfehler = ref("");

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

const onlineBewerbungFormData = ref<OnlineBewerbungData>({
  nachname: "",
  vorname: "",
  beruf: "",
  geburtsdatum: "",
  mail: "",
  telefonnummer: "",
});

function speichern(): void {
  if (!isValid()) {
    bewerbunggespeichertergebbnis.value = "EINGABEFEHLER";
    technischerfehler.value = "";
    if (hinweise.value) {
      hinweise.value.scrollIntoView();
    }
  } else {
    isSavingAnimation.value = true;
    ehrenamtJustizOnlineService.value
      .bewerbungSpeichern({
        vorname: onlineBewerbungFormData.value.vorname,
        nachname: onlineBewerbungFormData.value.nachname,
        geburtsdatum: onlineBewerbungFormData.value.geburtsdatum,
        telefonnummer: onlineBewerbungFormData.value.telefonnummer,
        beruf: onlineBewerbungFormData.value.beruf,
        mail: onlineBewerbungFormData.value.mail,
      })
      .then((bewerbunggespeichert) => {
        bewerbunggespeichertergebbnis.value = bewerbunggespeichert;
        technischerfehler.value = "";
        if (bewerbunggespeichert == "OK") {
          clearInputs();
        }
      })
      .catch((err) => (technischerfehler.value = err.toString()))
      .finally(() => {
        isSavingAnimation.value = false;
        if (hinweise.value) {
          hinweise.value.scrollIntoView();
        }
      });
  }
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
    errorMsgVorname.value = t("mainView.form.vorname.pflichtfeld");
    isValid = false;
  } else if (onlineBewerbungFormData.value.vorname.length > 255) {
    errorMsgVorname.value = t("mainView.form.vorname.maxLaenge");
    isValid = false;
  }
  return isValid;
}

function validateNachname(isValid: boolean) {
  // Nachname ist ein Pflichtfeld
  errorMsgNachname.value = "";
  if (!onlineBewerbungFormData.value.nachname) {
    errorMsgNachname.value = t("mainView.form.nachname.pflichtfeld");
    isValid = false;
  } else if (onlineBewerbungFormData.value.nachname.length > 300) {
    errorMsgNachname.value = t("mainView.form.nachname.maxLaenge");
    isValid = false;
  }
  return isValid;
}

function validateGeburtsdatum(isValid: boolean) {
  // Geburtsdatum ist ein Pflichtfeld und Alter prüfen
  errorMsgGeburtsdatum.value = "";
  if (!onlineBewerbungFormData.value.geburtsdatum) {
    errorMsgGeburtsdatum.value = t("mainView.form.geburtsdatum.pflichtfeld");
    isValid = false;
  } else {
    const alter = moment(
      useActiveKonfigurationStore().getKonfiguration?.amtsperiodevon
    ).diff(onlineBewerbungFormData.value.geburtsdatum, "years");

    const altervon = useActiveKonfigurationStore().getKonfiguration?.altervon;
    const alterbis = useActiveKonfigurationStore().getKonfiguration?.alterbis;
    if ((alterbis && alter > alterbis) || (altervon && alter < altervon)) {
      isValid = false;
      errorMsgGeburtsdatum.value = t("mainView.form.geburtsdatum.invalide", {
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
    errorMsgBeruf.value = t("mainView.form.beruf.pflichtfeld");
    isValid = false;
  } else if (onlineBewerbungFormData.value.beruf.length > 255) {
    errorMsgBeruf.value = t("mainView.form.beruf.maxLaenge");
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
    errorMsgTelefonnummer.value = t("mainView.form.telefonnummer.invalide");
    isValid = false;
  }
  return isValid;
}

function validateMail(isValid: boolean) {
  // Mail ist ein Pflichtfeld und Format prüfen
  errorMsgMail.value = "";
  if (!onlineBewerbungFormData.value.mail) {
    errorMsgMail.value = t("mainView.form.mail.pflichtfeld");
    isValid = false;
  } else if (!REGEX_MAIL.test(onlineBewerbungFormData.value.mail)) {
    errorMsgMail.value = t("mainView.form.mail.invalide");
    isValid = false;
  }
  return isValid;
}

function clearInputs(): void {
  onlineBewerbungFormData.value.nachname = "";
  onlineBewerbungFormData.value.vorname = "";
  onlineBewerbungFormData.value.beruf = "";
  onlineBewerbungFormData.value.geburtsdatum = "";
  onlineBewerbungFormData.value.mail = "";
  onlineBewerbungFormData.value.telefonnummer = "";
}

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
</script>

<style scoped>
.v-card-main-container {
  padding-left: 3%;
  padding-right: 3%;
}

.red-star {
  color: red;
  font-size: 20px;
}

.button-row {
  padding-top: 15px;
  padding-left: 15px;
  padding-bottom: 50px;
}

.button-wrapper {
  margin: 0;
  margin-top: 0;
}
#headerOnlineBewerbung {
  display: flex;
  justify-content: space-between;
  width: 100%;
}
</style>
