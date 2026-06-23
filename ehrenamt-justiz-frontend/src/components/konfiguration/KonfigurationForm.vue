<template>
  <div>
    <v-form
      ref="form"
      v-model="formValid"
      :disabled="konfiguration.action === BEARBEIGUNGS_MODUS.DISPLAY_MODUS"
      @submit.prevent="speichern"
    >
      <v-row>
        <v-col>
          <p>{{ t("components.konfigurationForm.hint") }}</p>
        </v-col>
        <v-col class="text-right">
          <v-btn
            variant="text"
            :to="{ name: '/konfiguration/konfigurationindex' }"
            class="ml-auto mr-2"
          >
            {{ t("components.konfigurationForm.buttons.abbrechen") }}
          </v-btn>
          <v-btn
            v-if="konfiguration.action === BEARBEIGUNGS_MODUS.EDIT_MODUS"
            color="accent"
            class="mr-2"
            @click="felderLeeren"
          >
            {{ t("components.konfigurationForm.buttons.felderLeeren") }}
          </v-btn>
          <v-btn
            v-if="konfiguration.action === BEARBEIGUNGS_MODUS.EDIT_MODUS"
            color="green"
            :loading="isAnimation"
            type="submit"
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
      <v-row>
        <v-col class="col">
          <v-file-upload
            v-model="konfiguration.vorlageBestaetigungverfassungstreue_file"
            :show-size="true"
            clearable
            :rules="[rules.RULE_FILE_VERFASSUNGSTREUE]"
          >
            <template #default>
              <v-file-upload-dropzone
                density="comfortable"
                :title="t('components.konfigurationForm.dateiAblegen')"
                :subtitle="t('components.konfigurationForm.dateiDurchsuchen')"
              ></v-file-upload-dropzone>

              <v-file-upload-list class="upload-list">
                <template #default="{ files, onClickRemove }">
                  <v-file-upload-item
                    v-for="(file, index) in files"
                    :key="file.name"
                    :file="file"
                    clearable
                    show-size
                    @click:remove="onClickRemove(index)"
                    @click="musterVerfassungstrueAnzeigen(file)"
                  />
                </template>
              </v-file-upload-list>
            </template>
          </v-file-upload>
        </v-col>
      </v-row>
    </v-form>
  </div>
</template>

<script setup lang="ts">
import type KonfigurationFormData from "@/types/KonfigurationFormData";

import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import {
  VBtn,
  VCol,
  VFileUpload,
  VFileUploadDropzone,
  VFileUploadItem,
  VFileUploadList,
  VForm,
  VRow,
  VSelect,
  VTextField,
} from "vuetify/components";

import { useRules } from "@/composables/rules";
import { BEARBEIGUNGS_MODUS, STATUS_INDICATORS } from "@/Constants";
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

const form = ref<VForm | null>(null);

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

async function speichern(): Promise<void> {
  const result = await form.value?.validate();

  const valid = typeof result === "boolean" ? result : (result?.valid ?? false);

  if (!valid) {
    snackbarStore.push({
      color: STATUS_INDICATORS.ERROR,
      text: t("components.konfigurationForm.messages.fehler"),
    });
    return;
  }
  emits("save");
}

function musterVerfassungstrueAnzeigen(file: File) {
  const url = URL.createObjectURL(file);
  window.open(url, "_blank", "noopener,noreferrer");
  setTimeout(() => URL.revokeObjectURL(url), 10_000);
}

const konfiguration = computed({
  get: () => props.modelValue,
  set: (v) => emits("update:modelValue", v),
});
</script>
