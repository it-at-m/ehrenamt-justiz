<template>
  <div>
    <v-form
      ref="form"
      v-model="formValid"
      :disabled="konfiguration.action == BEARBEIGUNGS_MODUS.DISPLAY_MODUS"
      @submit="speichern"
      @keydown.enter.prevent="speichern"
    >
      <v-row>
        <v-col>
          <p>{{ t("components.konfigurationForm.hint") }}</p>
        </v-col>
        <v-col class="text-right">
          <v-btn
            variant="text"
            exact
            :to="{ name: 'konfiguration.index' }"
            class="ml-auto"
          >
            {{ t("components.konfigurationForm.buttons.abbrechen") }}
          </v-btn>
          <v-btn
            v-if="konfiguration.action == BEARBEIGUNGS_MODUS.EDIT_MODUS"
            color="accent"
            @click="felderLeeren"
          >
            {{ t("components.konfigurationForm.buttons.felderLeeren") }}
          </v-btn>
          <v-btn
            v-if="konfiguration.action == BEARBEIGUNGS_MODUS.EDIT_MODUS"
            color="green"
            :loading="isAnimation"
            @click="speichern"
          >
            {{ t("components.konfigurationForm.buttons.speichern") }}
          </v-btn>
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col">
          <v-select
            v-model="konfiguration.ehrenamtjustizart"
            :items="ehrenamtJustizArt"
            :label="t('components.konfigurationForm.artEhrenamtJustiz')"
            persistent-placeholder
            :rules="[rules.RULE_REQUIRED]"
            density="compact"
            variant="outlined"
            autofocus
          />
        </v-col>
        <v-col class="col">
          <v-text-field
            v-model="konfiguration.bezeichnung"
            :label="t('components.konfigurationForm.bezeichnung')"
            persistent-placeholder
            maxlength="255"
            density="compact"
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          cols="6"
        >
          <v-text-field
            v-model="konfiguration.amtsperiodevon"
            :rules="[rules.RULE_REQUIRED]"
            :label="t('components.konfigurationForm.amtsperiodeVon')"
            persistent-placeholder
            type="date"
            density="compact"
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          cols="6"
        >
          <v-text-field
            v-model="konfiguration.amtsperiodebis"
            :rules="[rules.RULE_REQUIRED]"
            :label="t('components.konfigurationForm.amtsperiodeBis')"
            persistent-placeholder
            type="date"
            density="compact"
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col
          class="col"
          cols="6"
        >
          <v-text-field
            v-model="konfiguration.altervon"
            :rules="[rules.RULE_REQUIRED, rules.RULE_NUMERISCH]"
            :label="t('components.konfigurationForm.mindestalter')"
            persistent-placeholder
            density="compact"
            variant="outlined"
          />
        </v-col>
        <v-col
          class="col"
          cols="6"
        >
          <v-text-field
            v-model="konfiguration.alterbis"
            :rules="[rules.RULE_REQUIRED, rules.RULE_NUMERISCH]"
            :label="t('components.konfigurationForm.hoechstalter')"
            persistent-placeholder
            density="compact"
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col">
          <v-text-field
            v-model="konfiguration.staatsangehoerigkeit"
            :rules="[rules.RULE_REQUIRED]"
            :label="t('components.konfigurationForm.staatsangehoerigkeit')"
            persistent-placeholder
            density="compact"
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col">
          <v-text-field
            v-model="konfiguration.wohnsitz"
            :rules="[rules.RULE_REQUIRED]"
            :label="t('components.konfigurationForm.wohnsitz')"
            persistent-placeholder
            maxlength="255"
            density="compact"
            variant="outlined"
          />
        </v-col>
      </v-row>
    </v-form>
  </div>
</template>

<script setup lang="ts">
import type KonfigurationFormData from "@/types/KonfigurationFormData";

import { computed, createApp, ref } from "vue";
import { useI18n } from "vue-i18n";
import {
  VBtn,
  VCol,
  VForm,
  VRow,
  VSelect,
  VTextField,
} from "vuetify/components";

import App from "@/App.vue";
import { useRules } from "@/composables/rules";
import { BEARBEIGUNGS_MODUS } from "@/Constants";
import { STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar";

const props = defineProps<{
  modelValue: KonfigurationFormData;
  isAnimation: boolean;
}>();
const emits = defineEmits<{
  "update:modelValue": [v: KonfigurationFormData];
  save: [];
}>();
const rules = useRules();
const snackbarStore = useSnackbarStore();
/* eslint-disable @typescript-eslint/no-unused-vars */
const myV3App = createApp(App);
const form = ref<(typeof myV3App & { validate: () => void }) | null>(null);

const formValid = ref(false);

const ehrenamtJustizArt: string[] = ["VERWALTUNGSRICHTER", "SCHOEFFEN"];
const { t } = useI18n();

function felderLeeren(): void {
  konfiguration.value.ehrenamtjustizart = "";
  konfiguration.value.bezeichnung = "";
  konfiguration.value.amtsperiodevon = "";
  konfiguration.value.amtsperiodebis = "";
  konfiguration.value.altervon = 0;
  konfiguration.value.alterbis = 0;
  konfiguration.value.staatsangehoerigkeit = "";
  konfiguration.value.wohnsitz = "";
}

function speichern(): void {
  form.value?.validate();
  if (!formValid.value) {
    snackbarStore.showMessage({
      level: STATUS_INDICATORS.ERROR,
      message: t("components.konfigurationForm.messages.fehler"),
      show: true,
    });
    return;
  }
  emits("save");
}

const konfiguration = computed({
  get: () => props.modelValue,
  set: (v) => emits("update:modelValue", v),
});
</script>
