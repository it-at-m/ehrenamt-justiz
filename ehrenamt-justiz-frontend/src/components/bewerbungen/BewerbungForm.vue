<template>
  <v-form
    ref="form"
    class="form"
    :disabled="bewerbung.action == BEARBEIGUNGS_MODUS.DISPLAY_MODUS"
    @submit="speichern"
    @keydown.enter.prevent="speichern"
  >
    <v-row>
      <v-col
        class="text-left"
        cols="4"
      >
        <div class="text-h5">
          {{
            bewerbung.status == PERSONENSTATUS.STATUS_VORSCHLAG
              ? "Vorschlag"
              : "Bewerbung"
          }}
          {{
            bewerbung.action == BEARBEIGUNGS_MODUS.EDIT_MODUS
              ? "bearbeiten"
              : "anzeigen"
          }}
        </div>
      </v-col>
      <v-col cols="2">
        <div
          v-if="bewerbung.ewo_auskunftssperre.length > 0"
          class="text-h5 auskunftssperre"
        >
          Auskunftssperre
        </div>
      </v-col>
      <v-col
        class="text-right"
        cols="6"
      >
        <v-btn
          variant="text"
          exact
          class="ml-auto"
          @click="abbruch"
        >
          Abbrechen
        </v-btn>
        <v-btn
          v-if="bewerbung.action == BEARBEIGUNGS_MODUS.EDIT_MODUS"
          color="accent"
          @click="felderLeeren"
        >
          Felder leeren
        </v-btn>
        <v-btn
          v-if="bewerbung.action == BEARBEIGUNGS_MODUS.EDIT_MODUS"
          color="green"
          :loading="isAnimation"
          @click="speichern"
        >
          Speichern
        </v-btn>
      </v-col>
    </v-row>
    <v-row>
      <v-col class="text-left">
        <v-checkbox
          v-if="
            bewerbung.action == BEARBEIGUNGS_MODUS.EDIT_MODUS &&
            AuthService.checkAuth('READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE')
          "
          v-model="bewerbung.validierungdeaktivieren"
          label="Validierung deaktivieren (Alter, Wohnort und Staatsangehörigkeit)"
          :onchange="validieren"
        />
      </v-col>
    </v-row>
    <v-tabs v-model="active_tab">
      <v-tab value="ewo"> Aus EWO</v-tab>
      <v-tab value="bewerber"> Bewerber Zusatzangaben</v-tab>
    </v-tabs>
    <v-tabs-window v-model="active_tab">
      <v-tabs-window-item
        :key="1"
        style="padding: 1em"
        value="ewo"
        eager
      >
        <v-card
          class="scroll vcard"
          disabled
        >
          <v-card-text>
            <v-row>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.ewo_vorname"
                  :rules="[rules.RULE_REQUIRED]"
                  label="Vorname*"
                  persistent-placeholder
                  density="compact"
                  variant="outlined"
                />
              </v-col>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.ewo_familienname"
                  :rules="[rules.RULE_REQUIRED]"
                  label="Familienname*"
                  persistent-placeholder
                  density="compact"
                  variant="outlined"
                />
              </v-col>
            </v-row>
            <v-sheet
              :hidden="
                bewerbung.ewo_auskunftssperre.length > 0 &&
                !AuthService.checkAuth(
                  'READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE'
                )
              "
            >
              <!--Hidden and no v-if: guarantee of execution of rules-->
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_geburtsdatum"
                    :rules="
                      bewerbung.validierungdeaktivieren
                        ? [rules.RULE_REQUIRED]
                        : [rules.RULE_REQUIRED, rules.RULE_GEBURTSDATUM]
                    "
                    label="Geburtsdatum*"
                    persistent-placeholder
                    type="date"
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_geburtsname"
                    label="Geburtsname"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-select
                    v-model="bewerbung.ewo_geschlecht"
                    :items="geschlechtswerte"
                    :rules="[rules.RULE_REQUIRED]"
                    label="Geschlecht*"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewoid"
                    :rules="[rules.RULE_REQUIRED]"
                    label="Ordnungsmerkmal*"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_akademischergrad"
                    label="Akademischer Grad"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_geburtsort"
                    :rules="[rules.RULE_REQUIRED]"
                    label="Geburtsort*"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_geburtsland"
                    :rules="[rules.RULE_REQUIRED]"
                    label="Geburtsland*"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_familienstand"
                    :rules="[rules.RULE_REQUIRED]"
                    label="Familienstand*"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-textarea
                    v-model="bewerbung.ewo_staatsangehoerigkeit"
                    :rules="
                      bewerbung.validierungdeaktivieren
                        ? []
                        : [rules.RULE_STAATSANGEHOERIGKEIT]
                    "
                    label="Staatsangehörigkeit"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-textarea
                    v-if="bewerbung.status != PERSONENSTATUS.STATUS_INERFASSUNG"
                    v-model="bewerbung.ewo_konfliktfeld"
                    label="Konfliktfelder"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_wohnungsgeber"
                    label="Wohnungsgeber (c/o)"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_strasse"
                    label="Strasse*"
                    persistent-placeholder
                    :rules="[rules.RULE_REQUIRED]"
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_hausnummer"
                    label="Hausnummer*"
                    persistent-placeholder
                    :rules="[rules.RULE_REQUIRED]"
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_buchstabehausnummer"
                    label="Buchstabe Hausnummer"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_appartmentnummer"
                    label="Appartment-Nummer"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_stockwerk"
                    label="Stockwerk"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_teilnummerhausnummer"
                    label="Teilnummer Hausnummer"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_adresszusatz"
                    label="Adresszusatz"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_postleitzahl"
                    label="Postleitzahl*"
                    persistent-placeholder
                    :rules="[rules.RULE_REQUIRED]"
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_ort"
                    :rules="
                      bewerbung.validierungdeaktivieren
                        ? [rules.RULE_REQUIRED]
                        : [rules.RULE_REQUIRED, rules.RULE_WOHNSITZ]
                    "
                    label="Ort*"
                    persistent-placeholder
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-text-field
                    v-model="bewerbung.ewo_inmuenchenseit"
                    label="In München Wohnhaft seit*"
                    persistent-placeholder
                    :rules="[rules.RULE_REQUIRED]"
                    type="date"
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
              <v-row>
                <v-col class="col">
                  <v-select
                    v-model="bewerbung.ewo_wohnungsstatus"
                    :items="wohnungsstatus"
                    label="Wohnungsstatus*"
                    persistent-placeholder
                    :rules="[rules.RULE_REQUIRED]"
                    density="compact"
                    variant="outlined"
                  />
                </v-col>
              </v-row>
            </v-sheet>
          </v-card-text>
        </v-card>
      </v-tabs-window-item>
      <v-tabs-window-item
        :key="2"
        style="padding: 1em"
        value="bewerber"
        eager
      >
        <v-card class="scroll vcard">
          <v-card-text>
            <v-row>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.derzeitausgeuebterberuf"
                  label="Derzeitiger Beruf"
                  persistent-placeholder
                  density="compact"
                  variant="outlined"
                  autofocus
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.arbeitgeber"
                  label="Arbeitgeber"
                  persistent-placeholder
                  density="compact"
                  variant="outlined"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.ausgeuebteehrenaemter"
                  label="Ausgeübte Ehrenämter"
                  persistent-placeholder
                  density="compact"
                  variant="outlined"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.telefonnummer"
                  label="Telefonnummer (privat)"
                  persistent-placeholder
                  density="compact"
                  variant="outlined"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.telefongesch"
                  label="Telefonnummer (dienstlich)"
                  persistent-placeholder
                  density="compact"
                  variant="outlined"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.telefonmobil"
                  label="Telefonnummer (mobil)"
                  persistent-placeholder
                  density="compact"
                  variant="outlined"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.mailadresse"
                  :rules="[rules.RULE_MAIL]"
                  label="Mailadresse"
                  persistent-placeholder
                  type="mail"
                  density="compact"
                  variant="outlined"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.bewerbungvom"
                  label="Bewerbung vom*"
                  persistent-placeholder
                  :rules="[rules.RULE_REQUIRED]"
                  type="date"
                  density="compact"
                  variant="outlined"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col class="col">
                <v-select
                  v-model="bewerbung.status"
                  :items="statuswerte"
                  label="Status*"
                  persistent-placeholder
                  :rules="[rules.RULE_REQUIRED]"
                  density="compact"
                  variant="outlined"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col class="col">
                <v-checkbox
                  v-model="bewerbung.warbereitstaetigals"
                  :label="labelWarBereitsTaetigAls"
                  variant="outlined"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col class="col">
                <v-checkbox
                  v-model="bewerbung.warbereitstaetigalsvorvorperiode"
                  :label="labelWarBereitsTaetigAlsVorVorPeriode"
                  variant="outlined"
                />
              </v-col>
            </v-row>
          </v-card-text>
        </v-card>
      </v-tabs-window-item>
    </v-tabs-window>
  </v-form>
  <yes-no-dialog
    v-model="saveLeaveDialog"
    dialogtitle="Es wird gerade eine neue Bewerbung erfasst"
    dialogtext="Erfassung der Bewerbung fortsetzen?"
    @no="abbruch"
    @yes="cancel"
  />
</template>
<script setup lang="ts">
import type BewerbungFormData from "@/types/BewerbungFormData";

import { computed, ref } from "vue";
import {
  VBtn,
  VCard,
  VCardText,
  VCheckbox,
  VCol,
  VForm,
  VRow,
  VSelect,
  VSheet,
  VTab,
  VTabs,
  VTabsWindow,
  VTabsWindowItem,
  VTextarea,
  VTextField,
} from "vuetify/components";

import AuthService from "@/api/AuthService";
import YesNoDialog from "@/components/common/YesNoDialog.vue";
import { useRules } from "@/composables/rules";
import { useSaveLeave } from "@/composables/saveLeave";
import {
  BEARBEIGUNGS_MODUS,
  PERSONENSTATUS,
  STATUS_INDICATORS,
} from "@/Constants.ts";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useSnackbarStore } from "@/stores/snackbar";

const props = defineProps<{
  modelValue: BewerbungFormData;
  isAnimation: boolean;
}>();
const emits = defineEmits<{
  "update:modelValue": [v: BewerbungFormData];
  save: [];
  cancel: [];
}>();
const rules = useRules();
const snackbarStore = useSnackbarStore();

const form = ref();

const active_tab = ref("bewerber");

const tab1: string[] = [
  "Familienname",
  "Vorname",
  "Geburtsname",
  "Geburtsdatum",
  "Geschlecht",
  "Ordnungsmerkmal",
  "Akademischer Grad",
  "Geburtsort",
  "Geburtsland",
  "Familienstand",
  "Staatsangehörigkeit",
  "Konfliktfelder",
  "Wohnungsgeber (c/o)",
  "Strasse",
  "Hausnummer",
  "Appartment-Nummer",
  "Buchstabe Hausnummer",
  "Stockwerk",
  "Teilnummer Hausnummer",
  "Adresszusatz",
  "Postleitzahl",
  "Ort",
  "In München Wohnhaft seit",
  "Wohnungsstatus",
];

const statuswerte: string[] = [
  PERSONENSTATUS.STATUS_BEWERBUNG,
  PERSONENSTATUS.STATUS_VORSCHLAG,
  PERSONENSTATUS.STATUS_KONFLIKT,
];

const geschlechtswerte: string[] = [
  "männlich",
  "weiblich",
  "divers",
  "unbekannt",
];

const wohnungsstatus: string[] = ["Hauptwohnung", "Nebenwohnung"];

const abbruchOderSpeichern = ref(false);
/* eslint-disable @typescript-eslint/no-unused-vars */
const { cancel, leave, saveLeaveDialog } = useSaveLeave(isDirty);
/* eslint-enable @typescript-eslint/no-unused-vars */
const labelWarBereitsTaetigAls = ref(
  "War bereits als " +
    useGlobalSettingsStore().getKonfiguration?.ehrenamtjustizart +
    " tätig"
);

const labelWarBereitsTaetigAlsVorVorPeriode = ref(
  "War bereits in Vorvorperiode als " +
    useGlobalSettingsStore().getKonfiguration?.ehrenamtjustizart +
    " tätig"
);

function isDirty(): boolean {
  // Switching the menu during the new-entry of a person must be prevented, as otherwise
  // an application remains in the status “INERFASSUNG”
  return (
    props.modelValue.status == PERSONENSTATUS.STATUS_INERFASSUNG &&
    !abbruchOderSpeichern.value
  );
}

function validieren(): void {
  form.value?.validate();
}

function abbruch(): void {
  abbruchOderSpeichern.value = true;
  emits("cancel");
}

function speichern(): void {
  form.value?.validate().then((validation: { valid: boolean }) => {
    if (!validation.valid) {
      snackbarStore.showMessage({
        level: STATUS_INDICATORS.WARNING,
        message: "Das Formular ist nicht richtig ausgefüllt.",
        show: true,
      });
      setFocusAufFehler();
    } else {
      abbruchOderSpeichern.value = true;
      emits("save");
    }
  });
}

function felderLeeren(): void {
  bewerbung.value.derzeitausgeuebterberuf = "";
  bewerbung.value.arbeitgeber = "";
  bewerbung.value.telefonnummer = "";
  bewerbung.value.telefongesch = "";
  bewerbung.value.telefonmobil = "";
  bewerbung.value.mailadresse = "";
  bewerbung.value.ausgeuebteehrenaemter = "";
  bewerbung.value.neuervorschlag = "";
  bewerbung.value.warbereitstaetigals = "";
  bewerbung.value.warbereitstaetigalsvorvorperiode = "";
}

function setFocusAufFehler() {
  const v_inputs = document.getElementsByClassName("v-input");

  for (const v_input of v_inputs) {
    const v_messages = v_input.getElementsByClassName("v-messages__message");

    // Element with: error
    if (v_messages.length > 0) {
      let istAufTab1 = false;
      for (const value of tab1) {
        if (v_input.innerHTML.indexOf(value) > 0) {
          istAufTab1 = true;
          break;
        }
      }

      if (istAufTab1) {
        // Switch to tab "ewo":
        active_tab.value = "ewo";
      } else {
        // Switch to tab "bewerber":
        active_tab.value = "bewerber";
      }
      // Timeout 300, otherwise you will have to click on “Save” twice:
      setTimeout(() => {
        v_input.scrollIntoView({
          behavior: "smooth",
          block: "nearest",
          inline: "start",
        });
      }, 400);
      break;
    }
  }
}

const bewerbung = computed({
  get: () => props.modelValue,
  set: (v) => emits("update:modelValue", v),
});
</script>
<style>
/*Damit die Fehlermeldung bei den disabled-Felder auch rot angezeigt wird:*/
.v-input__details * {
  color: red !important;
  font-weight: bold !important;
}
</style>

<style scoped>
.scroll {
  overflow-y: scroll;
}

.auskunftssperre {
  color: red;
}
</style>
