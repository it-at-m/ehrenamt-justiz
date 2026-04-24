<template>
  <v-navigation-drawer v-model="showDrawer">
    <v-list>
      <v-list-item :to="{ name: '/getstarted' }">
        <v-list-item-title>{{
          t("app.navigation.getStarted")
        }}</v-list-item-title>
      </v-list-item>
      <v-list-item
        :to="{ name: '/ewobuerger/ewobuergercreate' }"
        :disabled="
          !user ||
          !user.authorities.includes('READ_EWOBUERGER') ||
          !globalSettingsStore.getKonfiguration
        "
      >
        <v-list-item-title>{{
          t("app.navigation.bewerbungErstellen")
        }}</v-list-item-title>
      </v-list-item>
      <v-list-item
        :to="{ name: '/bewerbungen/bewerbungenindex' }"
        :disabled="
          !user ||
          !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN') ||
          !globalSettingsStore.getKonfiguration
        "
      >
        <v-list-item-title>{{
          t("app.navigation.bewerbungen")
        }}</v-list-item-title>
      </v-list-item>
      <v-list-item
        :to="{ name: '/konflikte/konflikteindex' }"
        :disabled="
          !user ||
          !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN') ||
          !globalSettingsStore.getKonfiguration
        "
      >
        <v-list-item-title>{{
          t("app.navigation.konflikte")
        }}</v-list-item-title>
      </v-list-item>
      <v-list-item
        :to="{ name: '/vorschlaege/vorschlaegeindex' }"
        :disabled="
          !user || !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN')
        "
      >
        <v-list-item-title>{{
          t("app.navigation.vorschlaege")
        }}</v-list-item-title>
      </v-list-item>
      <v-list-item
        :to="{ name: '/konfiguration/konfigurationindex' }"
        :disabled="!user || !user.authorities.includes('READ_KONFIGURATION')"
      >
        <v-list-item-title>{{
          t("app.navigation.konfigurationen")
        }}</v-list-item-title>
      </v-list-item>
    </v-list>
  </v-navigation-drawer>
</template>
<script setup lang="ts">
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import {
  VList,
  VListItem,
  VListItemTitle,
  VNavigationDrawer,
} from "vuetify/components";

import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useUserInfoStore } from "@/stores/userinfo";

const showDrawer = defineModel<boolean>({ required: true });
const userInfoStore = useUserInfoStore();
const { t } = useI18n();
const globalSettingsStore = useGlobalSettingsStore();
const user = computed(() => userInfoStore.getUserInfo);
</script>
