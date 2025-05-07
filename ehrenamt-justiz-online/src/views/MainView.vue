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
          <h2>
            {{
              "Schöffen Onlinebewerbung"
            }}
          </h2>
          <logo-l-h-m />
        </div>
      </template>
      <template #content>
        <div ref="hinweise">
          <muc-banner
            v-if="bewerbunggespeichertergebbnis == 'OK'"
            title="Erfolgreich übertragen"
            type="success"
          >
            <div>
              Die Bewerbung wurde erfolgreich übertragen. Sie erhalten von uns
              eine Nachricht, sobald Ihre Angaben überprüft wurden.
            </div>
          </muc-banner>
          <muc-banner
            v-if="bewerbunggespeichertergebbnis == 'ERROR'"
            type="emergency"
          >
            <div>
              Bei der Übertragung der Bewerbung ist ein Fehler aufgetreten.
              Bitte überprüfen Sie Ihre Angaben und setzen sich ggf. mit dem
              Bürgerbüro während der Öffnungszeiten unter Telefon 089/233-44443
              in Verbindung.
            </div>
          </muc-banner>
          <muc-banner
            v-if="bewerbunggespeichertergebbnis == 'TOO_MANY_REQUESTS'"
            type="emergency"
          >
            <div>
              Sie haben zu viele Fehlversuche produziert. Bitte versuchen Sie es
              zu einem späteren Zeitpunkt erneut.
            </div>
          </muc-banner>
          <muc-banner
            v-if="bewerbunggespeichertergebbnis == 'EINGABEFEHLER'"
            type="emergency"
          >
            <div>Das Formular ist nicht richtig ausgefüllt.</div>
          </muc-banner>
          <muc-banner
            v-if="technischerfehler != ''"
            type="emergency"
          >
            <div>Technischer Fehler: {{ technischerfehler }}</div>
          </muc-banner>
        </div>
        <v-container class="v-card-main-container">
          <muc-callout type="info">
            <template #content>
              Auf dieser Seite haben Sie die Möglichkeit sich über das unten
              stehende Formular als Schöffe zu bewerben. Zusätzliche Infos zum Amt und den Aufgaben eines
              Schöffen finden Sie
              <muc-link
                label="hier"
                href="https://stadt.muenchen.de/service/info/hauptabteilung-ii-buergerangelegenheiten/1080614/"
              />
            </template>
          </muc-callout>
          <v-container fluid>
            <v-form
              ref="form"
              class="form"
              @keydown.enter.prevent="speichern"
            >
              <v-row dense>
                <muc-input
                  v-model="onlineBewerbungFormData.vorname"
                  placeholder="Vorname"
                  label="Vorname"
                  :required="true"
                  :error-msg="errorMsgVorname"
                />
              </v-row>
              <v-row dense>
                <muc-input
                  v-model="onlineBewerbungFormData.nachname"
                  placeholder="Nachname"
                  label="Nachname"
                  :required="true"
                  :error-msg="errorMsgNachname"
                />
              </v-row>
              <v-row dense>
                <muc-input
                  v-model="onlineBewerbungFormData.geburtsdatum"
                  placeholder="Geburtsdatum"
                  label="Geburtsdatum"
                  :required="true"
                  type="date"
                  :error-msg="errorMsgGeburtsdatum"
                  @keydown.ctrl.v="zwischenablageEinfuegen()"
                />
              </v-row>
              <v-row dense>
                <muc-input
                  v-model="onlineBewerbungFormData.beruf"
                  placeholder="Beruf"
                  label="Beruf"
                  :required="true"
                  :error-msg="errorMsgBeruf"
                />
              </v-row>
              <v-row dense>
                <muc-input
                  v-model="onlineBewerbungFormData.telefonnummer"
                  placeholder="Telefonnummer"
                  label="Telefonnummer"
                  :error-msg="errorMsgTelefonnummer"
                />
              </v-row>
              <v-row dense>
                <muc-input
                  v-model="onlineBewerbungFormData.mail"
                  placeholder="Mail-Adresse"
                  label="Mail"
                  :required="true"
                  :error-msg="errorMsgMail"
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
                >bewerben
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
                >leeren
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
            <template #header>Datenschutzerklärung</template>
            <template #content>
              Bewerbung für das Schöffenamt<br /><br />
              Die personenbezogenen Daten, die Sie im Rahmen Ihrer Bewerbung für
              das Schöffenamt an uns übermitteln, werden nur zum Zwecke der
              Bewerbung als Schöffin, bzw. Schöffe gespeichert. Rechtsgrundlage
              für die Verarbeitung ist das Gerichtsverfassungsgesetz,
              insbesondere § 36 bis § 38 GVG. Die Daten erheben wir nur im
              notwendigen Umfang.<br /><br />
              Ihre Daten werden verschlüsselt an das Kreisverwaltungsreferat,
              Hauptabteilung II, Einwohnerwesen – Bürgerbüro, Abteilung II/212
              Auskünfte/Sperren der Landeshauptstadt München übermittelt. Ihre
              Daten werden geprüft und - bei erfolgter Aufnahme in die
              Vorschlagsliste und Zustimmung des Stadtrates der Landeshauptstadt
              München - an das für die Wahl der Schöffinnen bzw. Schöffen
              zuständige Amtsgericht München weitergegeben.<br />
              Die Daten werden für die Dauer einer Schöffenperiode beim
              Kreisverwaltungsreferat der Landeshauptstadt München aufbewahrt
              und anschließend gelöscht.<br />
              Durch Ihre weitere Nutzung erklären Sie sich damit
              einverstanden.<br /><br />
              Bitte beachten Sie auch die
              <muc-link
                label="Hinweise zum Datenschutz"
                href="https://stadt.muenchen.de/infos/impressum-datenschutz.html#datenschutz"
              />
              im Impressum der Landeshauptstadt München.<br /><br />
              <muc-link
                label="Weitere Informationen zur elektronischen Kommunikation finden sie hier"
                href="https://stadt.muenchen.de/infos/elektronische-kommunikation.html"
              />
              <br /><br />
              Mit dem Absenden der Bewerbung bestätige ich, die deutsche, oder
              die deutsche und eine weitere Staatsangehörigkeit zu besitzen<br /><br />
              Mit <span class="red-star"> * </span> markierte Felder sind
              Pflichtfelder
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
import { VCol, VContainer, VForm, VRow } from "vuetify/components";

import { EhrenamtJustizOnlineService } from "@/api/EhrenamtJustizOnlineService";
import LogoLHM from "@/components/LogoLHM.vue";
import MuenchenBanner from "@/components/MuenchenBanner.vue";
import { useActiveKonfigurationStore } from "@/stores/activeconfig";

const form = ref();
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
    EhrenamtJustizOnlineService.bewerbungSpeichern({
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
    errorMsgVorname.value = "Vorname ist ein Pflichtfeld!";
    isValid = false;
  } else if (onlineBewerbungFormData.value.vorname.length > 255) {
    errorMsgVorname.value = "Vorname darf maximal 255 Zeichen lang sein!";
    isValid = false;
  }
  return isValid;
}

function validateNachname(isValid: boolean) {
  // Nachname ist ein Pflichtfeld
  errorMsgNachname.value = "";
  if (!onlineBewerbungFormData.value.nachname) {
    errorMsgNachname.value = "Nachname ist ein Pflichtfeld!";
    isValid = false;
  } else if (onlineBewerbungFormData.value.nachname.length > 255) {
    errorMsgNachname.value = "Nachname darf maximal 255 Zeichen lang sein!";
    isValid = false;
  }
  return isValid;
}

function validateGeburtsdatum(isValid: boolean) {
  // Geburtsdatum ist ein Pflichtfeld und Alter prüfen
  errorMsgGeburtsdatum.value = "";
  if (!onlineBewerbungFormData.value.geburtsdatum) {
    errorMsgGeburtsdatum.value = "Geburtsdatum ist ein Pflichtfeld!";
    isValid = false;
  } else {
    const alter = moment(
      useActiveKonfigurationStore().getKonfiguration?.amtsperiodevon
    ).diff(onlineBewerbungFormData.value.geburtsdatum, "years");

    const altervon = useActiveKonfigurationStore().getKonfiguration?.altervon;
    const alterbis = useActiveKonfigurationStore().getKonfiguration?.alterbis;
    if ((alterbis && alter > alterbis) || (altervon && alter < altervon)) {
      isValid = false;
      errorMsgGeburtsdatum.value =
        "Das Alter muss zwischen " +
        altervon +
        " und " +
        alterbis +
        " Jahren liegen.";
    }
  }
  return isValid;
}

function validateBeruf(isValid: boolean) {
  // Beruf ist ein Pflichtfeld
  errorMsgBeruf.value = "";
  if (!onlineBewerbungFormData.value.beruf) {
    errorMsgBeruf.value = "Beruf ist ein Pflichtfeld!";
    isValid = false;
  } else if (onlineBewerbungFormData.value.beruf.length > 255) {
    errorMsgBeruf.value = "Beruf darf maximal 255 Zeichen lang sein!";
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
    errorMsgTelefonnummer.value = "Valide Telefonnummer eingeben!";
    isValid = false;
  }
  return isValid;
}

function validateMail(isValid: boolean) {
  // Mail ist ein Pflichtfeld und Format prüfen
  errorMsgMail.value = "";
  if (!onlineBewerbungFormData.value.mail) {
    errorMsgMail.value = "Mail ist ein Pflichtfeld!";
    isValid = false;
  } else if (!REGEX_MAIL.test(onlineBewerbungFormData.value.mail)) {
    errorMsgMail.value = "Valide Mail eingeben!";
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
