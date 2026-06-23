<template>
  <button
    class="lhmlogo"
    onclick="location.href = 'https://www.muenchen.de'"
  >
    <img
      class="lhmimg w-100"
      src="@/assets/logo-lhm-muenchen.svg"
      alt="Logo Landeshauptstadt München"
    />
  </button>
</template>

<script setup lang="ts"></script>
<style>
.lhmlogo {
  cursor: pointer;
}
.lhmimg {
  width: 144px;
  height: 24px;
}
</style>
