// Composables
import { createRouter, createWebHashHistory } from "vue-router";

import { ROUTES_GETSTARTED, ROUTES_HOME } from "@/Constants.ts";
import BewerbungEdit from "@/views/bewerbungen/BewerbungEdit.vue";
import BewerbungenIndex from "@/views/bewerbungen/BewerbungenIndex.vue";
import EWOBuergerCreate from "@/views/ewobuerger/EWOBuergerCreate.vue";
import GetStartedView from "@/views/GetStartedView.vue";
import HomeView from "@/views/HomeView.vue";
import KonfigurationCreate from "@/views/konfiguration/KonfigurationCreate.vue";
import KonfigurationEdit from "@/views/konfiguration/KonfigurationEdit.vue";
import KonfigurationIndex from "@/views/konfiguration/KonfigurationIndex.vue";
import KonflikteIndex from "@/views/konflikte/KonflikteIndex.vue";
import KonfliktLoesenEdit from "@/views/konflikte/KonfliktLoesenEdit.vue";
import VorschlaegeIndex from "@/views/vorschlaege/VorschlaegeIndex.vue";

const routes = [
  {
    path: "/",
    name: ROUTES_HOME,
    component: HomeView,
    meta: {},
  },
  {
    path: "/getstarted",
    name: ROUTES_GETSTARTED,
    component: GetStartedView,
  },
  {
    path: "/bewerbung/create",
    name: "bewerbung.create",
    component: EWOBuergerCreate,
    meta: { authority: "READ_EWOBUERGER" },
  },
  {
    path: "/bewerbung/index",
    name: "bewerbung.index",
    component: BewerbungenIndex,
    meta: { authority: "READ_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/bewerbung/:id/:action",
    name: "bewerbung.edit",
    component: BewerbungEdit,
    meta: { authority: "WRITE_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/bewerbung/:id/:action",
    name: "bewerbung.display",
    component: BewerbungEdit,
    meta: { authority: "READ_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/konflikte/index",
    name: "konflikte.index",
    component: KonflikteIndex,
    meta: { authority: "READ_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/konfliktloesen/:id/edit",
    name: "konfliktloesen.edit",
    component: KonfliktLoesenEdit,
    meta: { authority: "WRITE_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/vorschlaege/index",
    name: "vorschlaege.index",
    component: VorschlaegeIndex,
    meta: { authority: "READ_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/configuration",
    name: "konfiguration.index",
    component: KonfigurationIndex,
    meta: { authority: "READ_KONFIGURATION" },
  },
  {
    path: "/configuration/create",
    name: "konfiguration.create",
    component: KonfigurationCreate,
    meta: { authority: "WRITE_KONFIGURATION" },
  },
  {
    path: "/configuration/:id/:action",
    name: "konfiguration.edit",
    component: KonfigurationEdit,
    meta: { authority: "WRITE_KONFIGURATION" },
  },
  {
    path: "/configuration/:id/:action",
    name: "konfiguration.display",
    component: KonfigurationEdit,
    meta: { authority: "READ_KONFIGURATION" },
  },
  { path: "/:catchAll(.*)*", redirect: "/" }, // CatchAll route
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
  scrollBehavior() {
    return {
      top: 0,
      left: 0,
    };
  },
});

export default router;
