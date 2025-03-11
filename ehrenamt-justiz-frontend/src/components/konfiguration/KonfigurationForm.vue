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
          <p>Alle mit Sternchen* gekennzeichneten Felder sind Pflichtfelder.</p>
        </v-col>
        <v-col class="text-right">
          <v-btn
            variant="text"
            exact
            :to="{ name: 'konfiguration.index' }"
            class="ml-auto"
          >
            Abbrechen
          </v-btn>
          <v-btn
            v-if="konfiguration.action == BEARBEIGUNGS_MODUS.EDIT_MODUS"
            color="accent"
            @click="felderLeeren"
          >
            Felder leeren
          </v-btn>
          <v-btn
            v-if="konfiguration.action == BEARBEIGUNGS_MODUS.EDIT_MODUS"
            color="green"
            :loading="isAnimation"
            @click="speichern"
          >
            Speichern
          </v-btn>
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col">
          <v-select
            v-model="konfiguration.ehrenamtjustizart"
            :items="ehrenamtJusitzArt"
            label="Art Ehrenamtjusitz*"
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
            label="Bezeichnung"
            persistent-placeholder
            :maxlength="255"
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
            label="Amtsperiode Von*"
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
            label="Amtsperiode Bis*"
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
            label="Mindestalter"
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
            label="Höchstalter"
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
            label="Staatsangehörigkeit*"
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
            label="Wohnsitz*"
            persistent-placeholder
            :maxlength="255"
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

import Vue, { computed, ref } from "vue";
import {
  VBtn,
  VCol,
  VForm,
  VRow,
  VSelect,
  VTextField,
} from "vuetify/components";

import { useRules } from "@/composables/rules";
import { BEARBEIGUNGS_MODUS } from "@/Constants";
import { STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar";

const props = defineProps<{
  modelValue: KonfigurationFormData;
  isAnimation: boolean;
}>();
const emits = defineEmits<{
  (e: "update:modelValue", v: KonfigurationFormData): void;
  (e: "save"): void;
}>();
const rules = useRules();
const snackbarStore = useSnackbarStore();

const form = ref<(Vue & { validate: () => void }) | null>(null);

const formValid = ref(false);

const ehrenamtJusitzArt: string[] = ["VERWALTUNGSRICHTER", "SCHOEFFEN"];

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
      level: STATUS_INDICATORS.WARNING,
      message: "Das Formular ist nicht richtig ausgefüllt.",
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
