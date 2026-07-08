<template>
  <v-navigation-drawer v-model="showDrawer">
    <v-list>
      <v-list-item
        :to="{ name: '/getstarted' }"
        :title="t('app.navigation.getStarted')"
      />
      <v-list-item
        :to="{ name: '/ewobuerger/ewobuergercreate' }"
        :disabled="
          !user ||
          !user.authorities.includes('READ_EWOBUERGER') ||
          !globalSettingsStore.getKonfiguration
        "
        :title="t('app.navigation.bewerbungErstellen')"
      />
      <v-list-item
        :to="{ name: '/bewerbungen/bewerbungenindex' }"
        :disabled="
          !user ||
          !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN') ||
          !globalSettingsStore.getKonfiguration
        "
        :title="t('app.navigation.bewerbungen')"
      />
      <v-list-item
        :to="{ name: '/konflikte/konflikteindex' }"
        :disabled="
          !user ||
          !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN') ||
          !globalSettingsStore.getKonfiguration
        "
        :title="t('app.navigation.konflikte')"
      />
      <v-list-item
        :to="{ name: '/vorschlaege/vorschlaegeindex' }"
        :disabled="
          !user || !user.authorities.includes('READ_EHRENAMTJUSTIZDATEN')
        "
        :title="t('app.navigation.vorschlaege')"
      />
      <v-list-item
        :to="{ name: '/konfiguration/konfigurationindex' }"
        :disabled="!user || !user.authorities.includes('READ_KONFIGURATION')"
        :title="t('app.navigation.konfigurationen')"
      />
    </v-list>
  </v-navigation-drawer>
</template>
<script setup lang="ts">
import { computed } from "vue";
import { useI18n } from "vue-i18n";
import { VList, VListItem, VNavigationDrawer } from "vuetify/components";

import { useGlobalSettingsStore } from "@/stores/globalsettings";
import { useUserInfoStore } from "@/stores/userinfo";

const showDrawer = defineModel<boolean>({ required: true });
const userInfoStore = useUserInfoStore();
const { t } = useI18n();
const globalSettingsStore = useGlobalSettingsStore();
const user = computed(() => userInfoStore.userInfo);
</script>
