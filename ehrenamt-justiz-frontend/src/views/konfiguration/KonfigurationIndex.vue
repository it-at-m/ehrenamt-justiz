<template>
  <v-container>
    <v-card flat>
      <v-card-title>
        <v-row>
          <v-col class="text-left"> Konfigurationen </v-col>

          <v-col class="text-right">
            <v-btn
              v-if="AuthService.checkAuth('WRITE_KONFIGURATION')"
              id="create-konfiguration"
              location="bottom right"
              fixed
              color="accent"
              :to="{ path: '/configuration/create' }"
            >
              Neue Konfiguration
            </v-btn>
          </v-col>
        </v-row>
      </v-card-title>
      <konfiguration-list />
    </v-card>
    <online-help-dialog-component
      component="Das ist die Onlinehilfe für die Übersicht der Konfigurationen (Under Construction)"
    />
  </v-container>
</template>

<script setup lang="ts">
import {
  VBtn,
  VCard,
  VCardTitle,
  VCol,
  VContainer,
  VRow,
} from "vuetify/components";

import AuthService from "@/api/AuthService";
import KonfigurationList from "@/components/konfiguration/KonfigurationList.vue";
import OnlineHelpDialogComponent from "@/components/online-help/OnlineHelpDialogComponent.vue";
</script>
