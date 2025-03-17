<template>
  <v-container>
    <v-row class="text-center">
      <v-col cols="12">
        <v-img
          src="@/assets/logo.png"
          class="my-3"
          height="200"
        />
      </v-col>

      <v-col class="mb-4">
        <h1 class="text-h3 font-weight-bold mb-3">
          Willkommen bei Ehrenamtjustiz
        </h1>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup lang="ts">
import { VCol, VContainer, VImg, VRow } from "vuetify/components";
</script>

<style scoped>
.UP {
  color: limegreen;
}

.DOWN {
  color: lightcoral;
}
</style>
