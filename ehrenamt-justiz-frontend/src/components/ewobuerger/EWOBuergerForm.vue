<template>
  <div>
    <v-form
      ref="form"
      validate-on="lazy"
      @submit.prevent="inEWOSuchen"
    >
      <v-row>
        <v-col
          class="text-left"
          cols="3"
        >
          <div class="text-headline-small">
            {{ t("components.eWOBuergerForm.header") }}
          </div>
        </v-col>
        <v-col
          class="text-right"
          cols="9"
        >
          <v-btn
            color="accent"
            class="mr-2"
            data-test="ewobuergerform-felderleeren-button"
            @click="felderLeeren"
          >
            {{ t("components.eWOBuergerForm.buttons.felderLeeren") }}
          </v-btn>
          <v-btn
            color="action"
            :loading="isAnimation"
            type="submit"
            data-test="ewobuergerform-inewosuchen-button"
          >
            {{ t("components.eWOBuergerForm.buttons.inEWOSuchen") }}
          </v-btn>
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col">
          <v-text-field
            v-model="ewobuergerdata.familienname"
            :rules="[rules.required(t('composables.rules.required'))]"
            :label="t('components.eWOBuergerForm.familienname')"
            persistent-placeholder
            density="compact"
            variant="outlined"
            autofocus
            maxlength="300"
            data-test="ewobuergerform-familienname-textfield"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col">
          <v-text-field
            v-model="ewobuergerdata.vorname"
            :rules="[rules.required(t('composables.rules.required'))]"
            :label="t('components.eWOBuergerForm.vorname')"
            persistent-placeholder
            density="compact"
            variant="outlined"
            maxlength="255"
            data-test="ewobuergerform-vorname-textfield"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col">
          <v-text-field
            v-model="ewobuergerdata.geburtsdatum"
            :label="t('components.eWOBuergerForm.geburtsdatum')"
            persistent-placeholder
            :rules="
              ewobuergerdata.validierungdeaktivieren
                ? [rules.required(t('composables.rules.required'))]
                : [
                    rules.required(t('composables.rules.required')),
                    rules.birthDay!(
                      konfiguration?.amtsperiodevon,
                      konfiguration?.altervon,
                      konfiguration?.alterbis,
                      t('composables.rules.geburtsdatum', {
                        alterVon: konfiguration?.altervon,
                        alterBis: konfiguration?.alterbis,
                      })
                    ),
                  ]
            "
            type="date"
            density="compact"
            variant="outlined"
            data-test="ewobuergerform-geburtsdatum-textfield"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col class="col">
          <v-checkbox
            v-if="
              AuthService.checkAuth(
                'READ_EHRENAMTJUSTIZDATEN_AUSKUNFTSSPERRE',
                t
              )
            "
            v-model="ewobuergerdata.validierungdeaktivieren"
            data-test="ewobuergerform-validierungdeaktivieren-checkbox"
            :label="t('components.eWOBuergerForm.validierungDeaktivieren')"
          />
        </v-col>
      </v-row>
      <v-row>
        <v-col>
          <p>
            {{ t("components.eWOBuergerForm.hint") }}
          </p>
        </v-col>
      </v-row>
    </v-form>
  </div>
</template>

<script setup lang="ts">
import type EWOBuergerSuche from "@/types/EWOBuergerSuche";

import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import { useRules } from "vuetify";
import {
  VBtn,
  VCheckbox,
  VCol,
  VForm,
  VRow,
  VTextField,
} from "vuetify/components";

import AuthService from "@/api/AuthService";
import { STATUS_INDICATORS } from "@/Constants";
import { useGlobalSettingsStore } from "@/stores/globalsettings.ts";
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
const konfiguration = useGlobalSettingsStore().getKonfiguration;
const snackbarStore = useSnackbarStore();
const form = ref();

const { t } = useI18n();

function inEWOSuchen(): void {
  form.value?.validate().then((validation: { valid: boolean }) => {
    if (!validation.valid) {
      snackbarStore.push({
        color: STATUS_INDICATORS.ERROR,
        text: t("components.eWOBuergerCreate.messages.fehler"),
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
