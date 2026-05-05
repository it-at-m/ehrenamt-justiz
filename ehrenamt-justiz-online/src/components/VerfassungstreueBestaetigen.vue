<template>
  <v-container fluid>
    <v-row dense>
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
              <use xlink:href="#icon-download"></use>
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

    <v-row dense>
      <muc-file-dropzone
        v-model="onlineBewerbungFormData.dateiVerfassungstreue"
        :additional-information="t('verfassungstreue.additionalInformation')"
        :button-text="t('verfassungstreue.buttonText')"
        :invalid-amount-warning="t('verfassungstreue.invalidAmountWarning')"
        :max-file-size="1"
        :multiple="false"
        :max-file-size-warning="t('verfassungstreue.maxFileSizeWarning')"
        @files="handleFiles"
      />
    </v-row>
    <v-row dense>
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
    </v-row>
    <muc-divider />
    <v-row class="button-row">
      <v-col
        class="button-wrapper"
        xs="12"
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
            <use xlink:href="#icon-arrow-left"></use>
          </svg>
        </muc-button>
      </v-col>
      <v-col
        class="button-wrapper"
        xs="12"
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
            <use xlink:href="#icon-floppy"></use>
          </svg>
        </muc-button>
      </v-col>
    </v-row>
  </v-container>
</template>
<script setup lang="ts">
import type OnlineBewerbungData from "@/types/OnlineBewerbungData.ts";

import {
  MucButton,
  MucCallout,
  MucDivider,
  MucFileDropzone,
  MucInput,
} from "@muenchen/muc-patternlab-vue";
import { computed, ref } from "vue";
import { useI18n } from "vue-i18n";
import { VCol, VRow } from "vuetify/components";

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

const save = () => {
  if (isValid()) {
    emits("save");
  }
};

function isValid() {
  let isValid = true;

  isValid = validateDateiVerfassungstreue(isValid);

  return isValid;
}

function validateDateiVerfassungstreue(isValid: boolean) {
  // Datei Verfassungstreue ist ein Pflichtfeld
  errorMsgDateiVerfassungstreue.value = "";
  zeigeLeerenDateiNameVerfassungstreue.value = false;
  if (!onlineBewerbungFormData.value.dateiVerfassungstreue) {
    errorMsgDateiVerfassungstreue.value = t("verfassungstreue.invalide");
    zeigeLeerenDateiNameVerfassungstreue.value = true;
    isValid = false;
  } else if (
    !onlineBewerbungFormData.value.dateiVerfassungstreue.name
      .toLowerCase()
      .endsWith(".pdf")
  ) {
    errorMsgDateiVerfassungstreue.value = t("verfassungstreue.keinPDF");
    isValid = false;
  }
  return isValid;
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
