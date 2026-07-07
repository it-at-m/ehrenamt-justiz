<template>
  <v-menu
    location="bottom"
    origin="center center"
    transition="scale-transition"
  >
    <template #activator="{ props }">
      <v-btn
        variant="text"
        icon
        v-bind="props"
        @click.stop
      >
        <v-icon :icon="mdiDotsVertical" />
      </v-btn>
    </template>
    <v-list>
      <v-list-item
        v-if="props.showOpen"
        @click.stop="open"
      >
        <template #title>
          <v-icon
            class="mr-2"
            :icon="mdiEye"
          />
          <span>{{ t("components.listItemActions.anzeigen") }}</span>
        </template>
      </v-list-item>
      <v-list-item
        v-if="props.showEdit"
        @click.stop="edit"
      >
        <template #title>
          <v-icon
            class="mr-2"
            :icon="mdiPencil"
          />
          <span>{{ t("components.listItemActions.bearbeiten") }}</span>
        </template>
      </v-list-item>
      <v-list-item
        v-if="props.showDelete"
        @click.stop="deletes"
      >
        <template #title>
          <v-icon
            class="mr-2"
            :icon="mdiDelete"
          />
          <span>{{ t("components.listItemActions.loeschen") }}</span>
        </template>
      </v-list-item>
    </v-list>
  </v-menu>
</template>

<script setup lang="ts">
import { mdiDelete, mdiDotsVertical, mdiEye, mdiPencil } from "@mdi/js";
import { useI18n } from "vue-i18n";
import { VBtn, VIcon, VList, VListItem, VMenu } from "vuetify/components";

export interface Props {
  showOpen: boolean;
  showEdit: boolean;
  showDelete: boolean;
  showSetActive: boolean;
}

const props = withDefaults(defineProps<Props>(), {
  showOpen: true,
  showEdit: true,
  showDelete: true,
  showSetActive: true,
});

const emits = defineEmits<{
  edit: [];
  delete: [];
  open: [];
}>();

const { t } = useI18n();

function edit(): void {
  emits("edit");
}
function open(): void {
  emits("open");
}
function deletes(): void {
  emits("delete");
}
</script>
