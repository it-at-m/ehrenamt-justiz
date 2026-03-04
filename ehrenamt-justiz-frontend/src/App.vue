<template>
  <v-app>
    <the-snackbar />
    <the-app-bar @clicked-nav-icon="toggleNavigation" />
    <the-navigation-drawer v-model="isNavigationShown" />
    <v-main>
      <v-container fluid>
        <v-progress-circular
          v-if="!isConfigLoaded"
          indeterminate
        ></v-progress-circular>
        <router-view
          v-slot="{ Component }"
          v-else
        >
          <v-fade-transition mode="out-in">
            <component :is="Component" />
          </v-fade-transition>
        </router-view>
      </v-container>
    </v-main>
  </v-app>
</template>

<script setup lang="ts">
import { useToggle } from "@vueuse/core";
import { computed, onMounted } from "vue";
import {
  VApp,
  VContainer,
  VFadeTransition,
  VMain,
  VProgressCircular,
} from "vuetify/components";

import TheAppBar from "@/components/TheAppBar.vue";
import TheNavigationDrawer from "@/components/TheNavigationDrawer.vue";
import TheSnackbar from "@/components/TheSnackbar.vue";
import { useGlobalSettingsStore } from "@/stores/globalsettings.ts";
import { useUserStore } from "@/stores/user.ts";

const globalSettingsStore = useGlobalSettingsStore();
const userStore = useUserStore();
const [isNavigationShown, toggleNavigation] = useToggle();
const isConfigLoaded = computed(() => {
  return (
    userStore.getUser && globalSettingsStore.isKonfigurationLoadingAttempt()
  );
});

onMounted(() => {
  // display drawer at once
  toggleNavigation();
});
</script>

<style>
.main {
  background-color: white;
}
.UP {
  color: limegreen;
}

.DOWN {
  color: lightcoral;
}
</style>
