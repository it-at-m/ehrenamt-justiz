<template>
  <v-container>
    <v-card flat>
      <v-card-title>
        <v-row>
          <v-col class="text-left">
            {{ t("routes.konfigurationindex.header") }}
          </v-col>

          <v-col class="text-right">
            <v-btn
              v-if="AuthService.checkAuth('WRITE_KONFIGURATION', t)"
              id="create-konfiguration"
              location="bottom right"
              fixed
              color="accent"
              :to="{ path: '/konfiguration/konfigurationcreate' }"
            >
              {{ t("routes.konfigurationindex.createKonfiguration") }}
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>
      <konfiguration-list />
    </v-card>
    <online-help-dialog-component
      :helptext="t('routes.konfigurationindex.onlineHelp')"
    />
  </v-container>
</template>

<script setup lang="ts">
import { useI18n } from "vue-i18n";
import {
  VBtn,
  VCard,
  VCardTitle,
  VCol,
  VContainer,
  VRow,
} from "vuetify/components";

import AuthService from "@/api/AuthService.ts";
import KonfigurationList from "@/components/konfiguration/KonfigurationList.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";

const { t } = useI18n();
</script>
