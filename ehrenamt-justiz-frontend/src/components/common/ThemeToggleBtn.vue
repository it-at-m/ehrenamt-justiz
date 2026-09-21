<template>
  <v-icon-btn
    variant="text"
    :icon="theme.global.current.value.dark ? mdiWeatherSunny : mdiWeatherNight"
    @click="toggleTheme"
  />
</template>

<script setup lang="ts">
import { mdiWeatherNight, mdiWeatherSunny } from "@mdi/js";
import { useTheme } from "vuetify";
import { VIconBtn } from "vuetify/components";

const theme = useTheme();

function toggleTheme(e: MouseEvent) {
  theme.setTransitionOrigin(e.currentTarget as HTMLElement);
  theme.toggle();
}
</script>
