<template>
  <v-form
    ref="form"
    class="form"
    :disabled="bewerbung.action == BEARBEIGUNGS_MODUS.DISPLAY_MODUS"
    @keydown.enter.exact.prevent="speichern"
  >
    <v-row>
      <v-col
        class="text-left"
        cols="4"
      >
        <div class="text-h5">
          {{
            bewerbung.status == PERSONENSTATUS.STATUS_VORSCHLAG
              ? t("components.bewerbungForm.header.vorschlag")
              : t("components.bewerbungForm.header.bewerbung")
          }}
          {{
            bewerbung.action == BEARBEIGUNGS_MODUS.EDIT_MODUS
              ? t("components.bewerbungForm.header.bearbeiten")
              : t("components.bewerbungForm.header.anzeigen")
          }}
        </div>
      </v-col>
      <v-col cols="2">
        <div
          v-if="bewerbung.ewo_auskunftssperre.length > 0"
          class="text-h5 auskunftssperre"
        >
          {{ t("components.bewerbungForm.header.auskunftssperre") }}
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
          {{ t("components.bewerbungForm.buttons.abbrechen") }}
        </v-btn>
        <v-btn
          v-if="bewerbung.action == BEARBEIGUNGS_MODUS.EDIT_MODUS"
          color="accent"
          @click="felderLeeren"
        >
          {{ t("components.bewerbungForm.buttons.felderLeeren") }}
        </v-btn>
        <v-btn
          v-if="bewerbung.action == BEARBEIGUNGS_MODUS.EDIT_MODUS"
          color="green"
          :loading="isAnimation"
          @click="speichern"
        >
          {{ t("components.bewerbungForm.buttons.speichern") }}
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
          :label="t('components.bewerbungForm.header.validierungDeaktivieren')"
          @change="validieren"
        />
      </v-col>
    </v-row>
    <v-tabs v-model="active_tab">
      <v-tab value="ewo">{{ t("components.bewerbungForm.tabs.ausEwo") }}</v-tab>
      <v-tab value="bewerber"
        >{{ t("components.bewerbungForm.tabs.bewerberZusatzangaben") }}
      </v-tab>
    </v-tabs>
    <v-tabs-window v-model="active_tab">
      <v-tabs-window-item
        :key="1"
        style="padding: 1em"
        value="ewo"
        eager
      >
        <v-card>
          <v-card-text>
            <v-expansion-panels
              v-model="activeExpansionPanel"
              multiple
            >
              <v-expansion-panel
                :title="
                  t('components.bewerbungForm.tabEwo.expansionPanel.name')
                "
              >
                <v-expansion-panel-text>
                  <v-row>
                    <v-col class="col">
                      <v-text-field
                        v-model="bewerbung.ewo_vorname"
                        :rules="[rules.RULE_REQUIRED]"
                        :label="t('components.bewerbungForm.tabEwo.vorname')"
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                    <v-col class="col">
                      <v-text-field
                        v-model="bewerbung.ewo_familienname"
                        :rules="[rules.RULE_REQUIRED]"
                        :label="
                          t('components.bewerbungForm.tabEwo.familienname')
                        "
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                    <v-col
                      class="col"
                      :hidden="
                        bewerbung.ewo_auskunftssperre.length > 0 &&
                        !AuthService.checkAuth(
                          'READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE'
                        )
                      "
                    >
                      <v-text-field
                        v-model="bewerbung.ewo_geburtsname"
                        :label="
                          t('components.bewerbungForm.tabEwo.geburtsname')
                        "
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                  </v-row>
                </v-expansion-panel-text>
              </v-expansion-panel>
              <v-expansion-panel
                :title="
                  t(
                    'components.bewerbungForm.tabEwo.expansionPanel.personenDaten'
                  )
                "
                :hidden="
                  bewerbung.ewo_auskunftssperre.length > 0 &&
                  !AuthService.checkAuth(
                    'READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE'
                  )
                "
              >
                <v-expansion-panel-text>
                  <v-row>
                    <v-col class="col">
                      <v-text-field
                        v-model="bewerbung.ewoid"
                        :rules="[rules.RULE_REQUIRED]"
                        :label="
                          t('components.bewerbungForm.tabEwo.ordnungsmerkmal')
                        "
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                    <v-col class="col">
                      <v-text-field
                        v-model="bewerbung.ewo_akademischergrad"
                        :label="
                          t('components.bewerbungForm.tabEwo.akademischerGrad')
                        "
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                    <v-col class="col">
                      <v-select
                        v-model="bewerbung.ewo_geschlecht"
                        :items="geschlechtswerte"
                        :rules="[rules.RULE_REQUIRED]"
                        :label="t('components.bewerbungForm.tabEwo.geschlecht')"
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                    <v-col class="col">
                      <v-text-field
                        v-model="bewerbung.ewo_familienstand"
                        :rules="[rules.RULE_REQUIRED]"
                        :label="
                          t('components.bewerbungForm.tabEwo.familienStand')
                        "
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                  </v-row>

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
                        :label="
                          t('components.bewerbungForm.tabEwo.geburtsdatum')
                        "
                        persistent-placeholder
                        type="date"
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                    <v-col class="col">
                      <v-text-field
                        v-model="bewerbung.ewo_geburtsort"
                        :rules="[rules.RULE_REQUIRED]"
                        :label="t('components.bewerbungForm.tabEwo.geburtsOrt')"
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                    <v-col class="col">
                      <v-text-field
                        v-model="bewerbung.ewo_geburtsland"
                        :rules="[rules.RULE_REQUIRED]"
                        :label="
                          t('components.bewerbungForm.tabEwo.geburtsLand')
                        "
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                    <v-col class="col">
                      <v-textarea
                        v-model="bewerbung.ewo_staatsangehoerigkeit"
                        :rules="
                          bewerbung.validierungdeaktivieren
                            ? []
                            : [rules.RULE_STAATSANGEHOERIGKEIT]
                        "
                        :label="
                          t(
                            'components.bewerbungForm.tabEwo.staatsangehoerigkeit'
                          )
                        "
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                  </v-row>
                </v-expansion-panel-text>
              </v-expansion-panel>

              <v-expansion-panel
                :title="
                  t('components.bewerbungForm.tabEwo.expansionPanel.adresse')
                "
                :hidden="
                  bewerbung.ewo_auskunftssperre.length > 0 &&
                  !AuthService.checkAuth(
                    'READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE'
                  )
                "
              >
                <v-expansion-panel-text>
                  <v-row>
                    <v-col
                      class="col"
                      md="4"
                    >
                      <v-text-field
                        v-model="bewerbung.ewo_strasse"
                        :label="t('components.bewerbungForm.tabEwo.strasse')"
                        persistent-placeholder
                        :rules="[rules.RULE_REQUIRED]"
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                    <v-col cols="8">
                      <v-row>
                        <v-col
                          class="col"
                          md="3"
                        >
                          <v-text-field
                            v-model="bewerbung.ewo_hausnummer"
                            :label="
                              t('components.bewerbungForm.tabEwo.hausnummer')
                            "
                            persistent-placeholder
                            :rules="[rules.RULE_REQUIRED]"
                            density="compact"
                            variant="outlined"
                            disabled
                          />
                        </v-col>
                        <v-col
                          class="col"
                          md="2"
                        >
                          <v-text-field
                            v-model="bewerbung.ewo_buchstabehausnummer"
                            :label="
                              t(
                                'components.bewerbungForm.tabEwo.buchstabeHausnummer'
                              )
                            "
                            persistent-placeholder
                            density="compact"
                            variant="outlined"
                            disabled
                          />
                        </v-col>
                        <v-col
                          class="col"
                          md="2"
                        >
                          <v-text-field
                            v-model="bewerbung.ewo_appartmentnummer"
                            :label="
                              t(
                                'components.bewerbungForm.tabEwo.appartmentNummer'
                              )
                            "
                            persistent-placeholder
                            density="compact"
                            variant="outlined"
                            disabled
                          />
                        </v-col>
                        <v-col
                          class="col"
                          md="2"
                        >
                          <v-text-field
                            v-model="bewerbung.ewo_stockwerk"
                            :label="
                              t('components.bewerbungForm.tabEwo.stockwerk')
                            "
                            persistent-placeholder
                            density="compact"
                            variant="outlined"
                            disabled
                          />
                        </v-col>
                        <v-col
                          class="col"
                          md="2"
                        >
                          <v-text-field
                            v-model="bewerbung.ewo_teilnummerhausnummer"
                            :label="
                              t(
                                'components.bewerbungForm.tabEwo.teilnummerHausnummer'
                              )
                            "
                            persistent-placeholder
                            density="compact"
                            variant="outlined"
                            disabled
                          />
                        </v-col>
                      </v-row>
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="col">
                      <v-text-field
                        v-model="bewerbung.ewo_adresszusatz"
                        :label="
                          t('components.bewerbungForm.tabEwo.adressZusatz')
                        "
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                    <v-col class="col">
                      <v-text-field
                        v-model="bewerbung.ewo_wohnungsgeber"
                        :label="
                          t('components.bewerbungForm.tabEwo.wohnungsgeber')
                        "
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                    <v-col class="col">
                      <v-select
                        v-model="bewerbung.ewo_wohnungsstatus"
                        :items="WOHNUNGSSTATUS_ARTEN"
                        :label="
                          t('components.bewerbungForm.tabEwo.wohnungsstatus')
                        "
                        persistent-placeholder
                        :rules="[rules.RULE_REQUIRED]"
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                  </v-row>
                  <v-row>
                    <v-col class="col">
                      <v-text-field
                        v-model="bewerbung.ewo_postleitzahl"
                        :label="
                          t('components.bewerbungForm.tabEwo.postleitzahl')
                        "
                        persistent-placeholder
                        :rules="[rules.RULE_REQUIRED]"
                        density="compact"
                        variant="outlined"
                        disabled
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
                        :label="t('components.bewerbungForm.tabEwo.ort')"
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                    <v-col class="col">
                      <v-text-field
                        v-model="bewerbung.ewo_inmuenchenseit"
                        :label="
                          t(
                            'components.bewerbungForm.tabEwo.inMuenchenWohnhaftSeit'
                          )
                        "
                        persistent-placeholder
                        :rules="[rules.RULE_REQUIRED]"
                        type="date"
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                  </v-row>
                </v-expansion-panel-text>
              </v-expansion-panel>
              <v-expansion-panel
                :title="
                  t('components.bewerbungForm.tabEwo.expansionPanel.sonstiges')
                "
                :hidden="
                  bewerbung.ewo_auskunftssperre.length > 0 &&
                  !AuthService.checkAuth(
                    'READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE'
                  )
                "
              >
                <v-expansion-panel-text>
                  <v-row>
                    <v-col class="col">
                      <v-textarea
                        v-if="
                          bewerbung.status != PERSONENSTATUS.STATUS_INERFASSUNG
                        "
                        v-model="bewerbung.ewo_konfliktfeld"
                        :label="
                          t('components.bewerbungForm.tabEwo.konfliktfelder')
                        "
                        persistent-placeholder
                        density="compact"
                        variant="outlined"
                        disabled
                      />
                    </v-col>
                  </v-row>
                </v-expansion-panel-text>
              </v-expansion-panel>
            </v-expansion-panels>
          </v-card-text>
        </v-card>
      </v-tabs-window-item>
      <v-tabs-window-item
        :key="2"
        style="padding: 1em"
        value="bewerber"
        eager
      >
        <v-card>
          <v-card-text>
            <v-row>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.derzeitausgeuebterberuf"
                  :label="
                    t(
                      'components.bewerbungForm.tabBewerberZusatzangaben.derzeitigerBeruf'
                    )
                  "
                  persistent-placeholder
                  maxlength="255"
                  density="compact"
                  variant="outlined"
                  autofocus
                />
              </v-col>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.arbeitgeber"
                  :label="
                    t(
                      'components.bewerbungForm.tabBewerberZusatzangaben.arbeitgeber'
                    )
                  "
                  persistent-placeholder
                  maxlength="255"
                  density="compact"
                  variant="outlined"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col class="col">
                <v-textarea
                  v-model="bewerbung.ausgeuebteehrenaemter"
                  :label="
                    t(
                      'components.bewerbungForm.tabBewerberZusatzangaben.ausgeuebteEhrenaemter'
                    )
                  "
                  persistent-placeholder
                  maxlength="4000"
                  density="compact"
                  variant="outlined"
                  clearable
                  rows="2"
                  auto-grow
                  @keydown.ctrl.enter.prevent="handleKeydown"
                />
              </v-col>
            </v-row>
            <v-row>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.telefonnummer"
                  :label="
                    t(
                      'components.bewerbungForm.tabBewerberZusatzangaben.telefonnummerPrivat'
                    )
                  "
                  persistent-placeholder
                  maxlength="255"
                  density="compact"
                  variant="outlined"
                />
              </v-col>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.telefongesch"
                  :label="
                    t(
                      'components.bewerbungForm.tabBewerberZusatzangaben.telefonnummerDienstlich'
                    )
                  "
                  persistent-placeholder
                  maxlength="255"
                  density="compact"
                  variant="outlined"
                />
              </v-col>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.telefonmobil"
                  :label="
                    t(
                      'components.bewerbungForm.tabBewerberZusatzangaben.telefonnummerMobil'
                    )
                  "
                  persistent-placeholder
                  maxlength="255"
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
                  :label="
                    t(
                      'components.bewerbungForm.tabBewerberZusatzangaben.mailAdresse'
                    )
                  "
                  persistent-placeholder
                  maxlength="150"
                  type="mail"
                  density="compact"
                  variant="outlined"
                />
              </v-col>
              <v-col class="col">
                <v-text-field
                  v-model="bewerbung.bewerbungvom"
                  :label="
                    t(
                      'components.bewerbungForm.tabBewerberZusatzangaben.bewerbungVom'
                    )
                  "
                  persistent-placeholder
                  :rules="[rules.RULE_REQUIRED]"
                  type="date"
                  density="compact"
                  variant="outlined"
                />
              </v-col>
              <v-col class="col">
                <v-select
                  v-model="bewerbung.status"
                  :items="statuswerte"
                  :label="
                    t(
                      'components.bewerbungForm.tabBewerberZusatzangaben.status'
                    )
                  "
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
                  :label="
                    t(
                      'components.bewerbungForm.tabBewerberZusatzangaben.warBereitsAlsVerwaltungsrichterTaetig',
                      {
                        artEhrenamtJustiz: formattedEhrenamtjustizart(
                          t,
                          useGlobalSettingsStore().getKonfiguration
                            ?.ehrenamtjustizart,
                          1
                        ),
                      }
                    )
                  "
                  variant="outlined"
                />
              </v-col>
              <v-col class="col">
                <v-checkbox
                  v-model="bewerbung.warbereitstaetigalsvorvorperiode"
                  :label="
                    t(
                      'components.bewerbungForm.tabBewerberZusatzangaben.warBereitsInVorperiodeAlsVerwaltungsrichterTaetig',
                      {
                        artEhrenamtJustiz: formattedEhrenamtjustizart(
                          t,
                          useGlobalSettingsStore().getKonfiguration
                            ?.ehrenamtjustizart,
                          1
                        ),
                      }
                    )
                  "
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
    :dialogtitle="t('components.bewerbungForm.saveLeaveDialog.dialogtitle')"
    :dialogtext="t('components.bewerbungForm.saveLeaveDialog.dialogtext')"
    @no="abbruch"
    @yes="cancel"
  />
</template>
<script setup lang="ts">
import type BewerbungFormData from "@/types/BewerbungFormData";

import { computed, nextTick, ref } from "vue";
import { useI18n } from "vue-i18n";
import {
  VBtn,
  VCard,
  VCardText,
  VCheckbox,
  VCol,
  VExpansionPanel,
  VExpansionPanels,
  VExpansionPanelText,
  VForm,
  VRow,
  VSelect,
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
  WOHNUNGSSTATUS_ARTEN,
} from "@/Constants.ts";
import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useSnackbarStore } from "@/stores/snackbar";
import { formattedEhrenamtjustizart } from "@/tools/Helper";

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

const activeExpansionPanel = ref([0, 1, 2, 3]);

const active_tab = ref("bewerber");

const { t } = useI18n();

const tab1: string[] = [
  t("components.bewerbungForm.tabEwo.familienname"),
  t("components.bewerbungForm.tabEwo.vorname"),
  t("components.bewerbungForm.tabEwo.geburtsname"),
  t("components.bewerbungForm.tabEwo.geburtsdatum"),
  t("components.bewerbungForm.tabEwo.geschlecht"),
  t("components.bewerbungForm.tabEwo.ordnungsmerkmal"),
  t("components.bewerbungForm.tabEwo.akademischerGrad"),
  t("components.bewerbungForm.tabEwo.geburtsOrt"),
  t("components.bewerbungForm.tabEwo.geburtsLand"),
  t("components.bewerbungForm.tabEwo.familienStand"),
  t("components.bewerbungForm.tabEwo.staatsangehoerigkeit"),
  t("components.bewerbungForm.tabEwo.konfliktfelder"),
  t("components.bewerbungForm.tabEwo.wohnungsgeber"),
  t("components.bewerbungForm.tabEwo.strasse"),
  t("components.bewerbungForm.tabEwo.hausnummer"),
  t("components.bewerbungForm.tabEwo.appartmentNummer"),
  t("components.bewerbungForm.tabEwo.buchstabeHausnummer"),
  t("components.bewerbungForm.tabEwo.stockwerk"),
  t("components.bewerbungForm.tabEwo.teilnummerHausnummer"),
  t("components.bewerbungForm.tabEwo.adressZusatz"),
  t("components.bewerbungForm.tabEwo.postleitzahl"),
  t("components.bewerbungForm.tabEwo.ort"),
  t("components.bewerbungForm.tabEwo.inMuenchenWohnhaftSeit"),
  t("components.bewerbungForm.tabEwo.wohnungsstatus"),
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

const abbruchOderSpeichern = ref(false);
/* eslint-disable @typescript-eslint/no-unused-vars */
const { cancel, leave, saveLeaveDialog } = useSaveLeave(isDirty);

/* eslint-enable @typescript-eslint/no-unused-vars */

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
        level: STATUS_INDICATORS.ERROR,
        message: t("components.bewerbungForm.speichern.errorMessage"),
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

function handleKeydown(e: KeyboardEvent): void {
  const textarea = e.target as HTMLTextAreaElement;
  const start = textarea.selectionStart;
  const end = textarea.selectionEnd;
  const value = bewerbung.value.ausgeuebteehrenaemter || "";

  bewerbung.value.ausgeuebteehrenaemter =
    value.substring(0, start) + "\n" + value.substring(end);

  // Use nextTick to ensure DOM update before setting cursor position
  nextTick(() => {
    textarea.selectionStart = textarea.selectionEnd = start + 1;
  });
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
