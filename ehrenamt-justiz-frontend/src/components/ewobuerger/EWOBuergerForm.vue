<template>
  <div>
    <v-form
      ref="form"
      :model-value="formValid"
      validate-on="lazy"
      @submit="inEWOSuchen"
      @keydown.enter.prevent="inEWOSuchen"
    >
      <v-row>
        <v-col
          class="text-left"
          cols="3"
        >
          <div class="text-h5">Bewerbung erstellen</div>
        </v-col>
        <v-col
          class="text-right"
          cols="9"
        >
          <v-btn
            color="accent"
            @click="felderLeeren"
          >
            Felder leeren
          </v-btn>
          <v-btn
            color="green"
            :loading="isAnimation"
            @click="inEWOSuchen"
          >
            In EWO suchen
          </v-btn>
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col">
          <v-text-field
            v-model="ewobuergerdata.familienname"
            :rules="[rules.RULE_REQUIRED]"
            label="Familienname*"
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
            v-model="ewobuergerdata.vorname"
            :rules="[rules.RULE_REQUIRED]"
            label="Vorname*"
            persistent-placeholder
            density="compact"
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col">
          <v-text-field
            v-model="ewobuergerdata.geburtsdatum"
            label="Geburtsdatum*"
            persistent-placeholder
            :rules="
              ewobuergerdata.validierungdeaktivieren
                ? [rules.RULE_REQUIRED]
                : [rules.RULE_REQUIRED, rules.RULE_GEBURTSDATUM]
            "
            type="date"
            density="compact"
            variant="outlined"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col">
          <v-checkbox
            v-if="
              AuthService.checkAuth('READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE')
            "
            v-model="ewobuergerdata.validierungdeaktivieren"
            label="Validierung deaktivieren (Altersprüfung)"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <p>
            Alle mit Sternchen* gekennzeichneten Felder sind Pflichtfelder.
            Wildcard '*' mit mindestens 3 führenden Zeichen.
          </p>
        </v-col>
      </v-row>
    </v-form>
  </div>
</template>

<script setup lang="ts">
import type EWOBuergerSuche from "@/types/EWOBuergerSuche";

import { computed, ref } from "vue";
import {
  VBtn,
  VCheckbox,
  VCol,
  VForm,
  VRow,
  VTextField,
} from "vuetify/components";

import AuthService from "@/api/AuthService";
import { useRules } from "@/composables/rules";
import { STATUS_INDICATORS } from "@/Constants.ts";
import { useSnackbarStore } from "@/stores/snackbar";

const props = defineProps<{
  modelValue: EWOBuergerSuche;
  isAnimation: boolean;
}>();
const emits = defineEmits<{
  "update:modelValue": [v: EWOBuergerSuche];
  save: [];
}>();
const rules = useRules();
const snackbarStore = useSnackbarStore();
const form = ref();

const formValid = ref(false);

function inEWOSuchen(): void {
  form.value?.validate().then((validation: { valid: boolean }) => {
    if (!validation.valid) {
      snackbarStore.showMessage({
        level: STATUS_INDICATORS.WARNING,
        message: "Das Formular ist nicht richtig ausgefüllt.",
        show: true,
      });
    } else {
      emits("save");
    }
  });
}

function felderLeeren(): void {
  ewobuergerdata.value.familienname = "";
  ewobuergerdata.value.vorname = "";
  ewobuergerdata.value.geburtsdatum = "";
}

const ewobuergerdata = computed({
  get: () => props.modelValue,
  set: (v) => emits("update:modelValue", v),
});
</script>
