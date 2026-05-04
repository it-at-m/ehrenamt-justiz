<template>
  <v-container fluid>
    <v-form
      ref="form"
      class="m-form m-form--default"
      @submit="nextStep"
      @keydown.enter.prevent="nextStep"
    >
      <muc-callout type="info">
        <template #content>
          {{ t("mainView.info") }}
          <muc-link
            label="hier"
            href="https://stadt.muenchen.de/service/info/hauptabteilung-ii-buergerangelegenheiten/1080614/"
          />
        </template>
      </muc-callout>
      <muc-callout type="info">
        <template #header>{{
          t("mainView.datenschutzErklaerung.header")
        }}</template>
        <template #content>
          {{ t("mainView.datenschutzErklaerung.content1") }}
          <br /><br />
          {{ t("mainView.datenschutzErklaerung.content2") }}
          <br /><br />
          {{ t("mainView.datenschutzErklaerung.content3") }}
          <br />
          {{ t("mainView.datenschutzErklaerung.content4") }}
          <br />
          {{ t("mainView.datenschutzErklaerung.content5") }}
          <br /><br />
          {{ t("mainView.datenschutzErklaerung.content6") }}
          <muc-link
            :label="t('mainView.datenschutzErklaerung.links.datenschutz')"
            href="https://stadt.muenchen.de/infos/impressum-datenschutz.html#datenschutz"
          />
          {{ t("mainView.datenschutzErklaerung.content7") }}
          <br /><br />
          <muc-link
            :label="
              t(
                'mainView.datenschutzErklaerung.links.elektronischeKommunikation'
              )
            "
            href="https://stadt.muenchen.de/infos/elektronische-kommunikation.html"
          />
          <br /><br />
          {{ t("mainView.datenschutzErklaerung.content8") }}
          <br /><br />
          {{ t("mainView.datenschutzErklaerung.content9") }}
        </template>
      </muc-callout>
    </v-form>
  </v-container>
  <muc-divider />
  <v-row class="button-row">
    <v-col
      class="button-wrapper"
      xs="2"
      sm="2"
      md="2"
      lg="2"
      xl="2"
    >
      <muc-button
        variant="primary"
        @click="nextStep"
      >
        {{ t("allgemeineInfo.buttons.weiter") }}
        <svg class="m-button__icon">
          <use xlink:href="#icon-arrow-right"></use>
        </svg>
      </muc-button>
    </v-col>
    <v-col
      class="button-wrapper"
      xs="10"
      sm="10"
      md="10"
      lg="10"
      xl="10"
    >
    </v-col>
  </v-row>
</template>

<script setup lang="ts">
import {
  MucButton,
  MucCallout,
  MucDivider,
  MucLink,
} from "@muenchen/muc-patternlab-vue";
import { useI18n } from "vue-i18n";
import { VCol, VForm, VRow } from "vuetify/components";

const { t } = useI18n();
const emits = defineEmits<{
  next: [];
}>();
const nextStep = () => emits("next");
</script>
