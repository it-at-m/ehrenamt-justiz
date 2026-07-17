<template>
  <v-container fluid>
    <v-row density="compact">
      <muc-callout
        type="info"
        class="w-100"
      >
        <template #content>
          <p>
            {{ t("verfassungstreue.anleitung.herunterladen") }}
          </p>
          <muc-button
            variant="primary"
            @click="erstellenVerfassungstreueMuster"
          >
            {{ t("verfassungstreue.buttons.musterHerunterladen") }}
            <svg class="m-button__icon">
              <use href="#icon-download"></use>
            </svg>
          </muc-button>
          <p>
            {{ t("verfassungstreue.anleitung.unterschreiben") }}
          </p>
          <p>
            {{ t("verfassungstreue.anleitung.hochladen") }}
          </p>
        </template>
      </muc-callout>
    </v-row>

    <v-row density="compact">
      <muc-file-dropzone
        v-model="onlineBewerbungFormData.dateiVerfassungstreue"
        :additional-information="
          t('verfassungstreue.additionalInformation', {
            maxFilesize: maxFileSize,
          })
        "
        :button-text="t('verfassungstreue.buttonText')"
        :invalid-amount-warning="t('verfassungstreue.invalidAmountWarning')"
        :max-file-size="maxFileSize"
        :multiple="false"
        :max-file-size-warning="t('verfassungstreue.maxFileSizeWarning')"
        @files="handleFiles"
        @warning="clearFiles"
      />
    </v-row>
    <v-row density="compact">
      <div
        class="w-100"
        @click="dateiVerfassungstreueAnzeigen"
      >
        <muc-input
          v-if="
            dateiNameVerfassungstreue ||
            zeigeLeerenDateiNameVerfassungstreue == true
          "
          id="dateiVerfassungstreue"
          v-model="dateiNameVerfassungstreue"
          class="mb-5 no-pointer"
          :label="t('verfassungstreue.dateiname')"
          :required="true"
          :error-msg="errorMsgDateiVerfassungstreue"
          readonly
        />
      </div>
    </v-row>
    <muc-divider />
    <v-row class="button-row">
      <v-col
        class="button-wrapper"
        cols="12"
        sm="12"
        md="3"
        lg="2"
        xl="2"
      >
        <muc-button
          variant="primary"
          @click="previousStep"
        >
          {{ t("verfassungstreue.buttons.zurueck") }}
          <svg class="m-button__icon">
            <use href="#icon-arrow-left"></use>
          </svg>
        </muc-button>
      </v-col>
      <v-col
        class="button-wrapper"
        cols="12"
        sm="9"
        md="3"
        lg="2"
        xl="2"
      >
        <muc-button
          variant="primary"
          @click="save"
        >
          {{ t("verfassungstreue.buttons.speichern") }}
          <svg class="m-button__icon">
            <use href="#icon-floppy"></use>
          </svg>
        </muc-button>
      </v-col>
    </v-row>
  </v-container>
</template>
<script setup lang="ts">
import type OnlineBewerbungData from "@/types/OnlineBewerbungData";

import {
  MucButton,
  MucCallout,
  MucDivider,
  MucFileDropzone,
  MucInput,
} from "@muenchen/muc-patternlab-vue";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import { VCol, VContainer, VRow } from "vuetify/components";

import { useActiveKonfigurationStore } from "@/stores/activeconfig.ts";

const { t } = useI18n();

const zeigeLeerenDateiNameVerfassungstreue = ref(false);
const errorMsgDateiVerfassungstreue = ref("");
const dateiNameVerfassungstreue = computed(() => {
  if (onlineBewerbungFormData.value.dateiVerfassungstreue) {
    // eslint-disable-next-line vue/no-side-effects-in-computed-properties
    errorMsgDateiVerfassungstreue.value = "";
    return (
      onlineBewerbungFormData.value.dateiVerfassungstreue.name +
      " (" +
      t("verfassungstreue.dateigroesse") +
      ": " +
      onlineBewerbungFormData.value.dateiVerfassungstreue.size +
      " Bytes)"
    );
  } else {
    return null;
  }
});

const maxFileSize = computed(
  () =>
    useActiveKonfigurationStore().getTechnischeKonfiguration
      ?.bestaetigungVerfassungstreueMaxSize
);

const previousStep = () => {
  emits("previousStep");
};
const props = defineProps<{
  modelValue: OnlineBewerbungData;
}>();
const emits = defineEmits<{
  "update:modelValue": [v: OnlineBewerbungData];
  previousStep: [];
  save: [];
  erstellenVerfassungstreueMuster: [];
}>();
const onlineBewerbungFormData = computed({
  get: () => props.modelValue,
  set: (v) => emits("update:modelValue", v),
});

const erstellenVerfassungstreueMuster = async () => {
  emits("erstellenVerfassungstreueMuster");
};

function handleFiles(files: File[]) {
  onlineBewerbungFormData.value.dateiVerfassungstreue = files?.[0] ?? null;
}

function clearFiles() {
  onlineBewerbungFormData.value.dateiVerfassungstreue = null;
}

const save = () => {
  if (isValid()) {
    emits("save");
  }
};

function isValid() {
  return validateDateiVerfassungstreue();
}

function validateDateiVerfassungstreue() {
  let isValid = true;
  // Datei Verfassungstreue ist ein Pflichtfeld
  errorMsgDateiVerfassungstreue.value = "";
  zeigeLeerenDateiNameVerfassungstreue.value = false;
  if (!onlineBewerbungFormData.value.dateiVerfassungstreue) {
    errorMsgDateiVerfassungstreue.value = t("verfassungstreue.invalide");
    zeigeLeerenDateiNameVerfassungstreue.value = true;
    isValid = false;
  } else {
    const bestaetigungVerfassungstreueFileExtension =
      useActiveKonfigurationStore().getTechnischeKonfiguration
        ?.bestaetigungVerfassungstreueFileExtension;
    const dateiEndungKorrekt =
      hatBestaetigungVerfassungstreueFileErlaubteEndung(
        onlineBewerbungFormData.value.dateiVerfassungstreue.name,
        bestaetigungVerfassungstreueFileExtension
      );
    if (!dateiEndungKorrekt) {
      errorMsgDateiVerfassungstreue.value = t(
        "verfassungstreue.anhangKeinGueltigeDateiendung",
        { dateiTypen: bestaetigungVerfassungstreueFileExtension }
      );
      isValid = false;
    }
  }
  return isValid;
}

function dateiVerfassungstreueAnzeigen() {
  if (!onlineBewerbungFormData.value.dateiVerfassungstreue) {
    return;
  }
  const link = document.createElement("a");
  link.href = window.URL.createObjectURL(
    onlineBewerbungFormData.value.dateiVerfassungstreue
  );
  link.download = onlineBewerbungFormData.value.dateiVerfassungstreue.name;
  link.click();
  setTimeout(() => URL.revokeObjectURL(link.href), 10_000);
}

function hatBestaetigungVerfassungstreueFileErlaubteEndung(
  dateiname: string,
  endungenString: string | undefined
): boolean {
  if (!endungenString) {
    return false;
  }
  const endungen = endungenString
    .split(",")
    .map((e) => e.trim().toLowerCase())
    .filter((e) => e.length > 0);
  const dateinameLower = dateiname.toLowerCase();
  return endungen.some((endung) => dateinameLower.endsWith(`.${endung}`));
}
</script>
<style scoped>
.m-form-group {
  width: 100%;
}
.no-pointer {
  pointer-events: none;
}
</style>
