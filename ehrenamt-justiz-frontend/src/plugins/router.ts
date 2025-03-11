// Composables
import { createRouter, createWebHashHistory } from "vue-router";

import { ROUTES_GETSTARTED, ROUTES_HOME } from "@/Constants.ts";
import GetStartedView from "@/views/GetStartedView.vue";
import HomeView from "@/views/HomeView.vue";
import KonfigurationCreate from "@/views/konfiguration/KonfigurationCreate.vue";
import KonfigurationEdit from "@/views/konfiguration/KonfigurationEdit.vue";
import KonfigurationIndex from "@/views/konfiguration/KonfigurationIndex.vue";

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
    component: GetStartedView,
    meta: { authority: "READ_EWOBUERGER" },
  },
  {
    path: "/bewerbung/index",
    name: "bewerbung.index",
    component: GetStartedView,
    meta: { authority: "READ_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/bewerbung/:id:action",
    name: "bewerbung.edit",
    component: GetStartedView,
    meta: { authority: "WRITE_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/bewerbung/:id:action",
    name: "bewerbung.display",
    component: GetStartedView,
    meta: { authority: "READ_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/konflikte/index",
    name: "konflikte.index",
    component: GetStartedView,
    meta: { authority: "READ_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/konfliktloesen/:id/edit",
    name: "konfliktloesen.edit",
    component: GetStartedView,
    meta: { authority: "WRITE_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/konflikte/index",
    name: "konflikte.index",
    component: GetStartedView,
    meta: { authority: "READ_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/konfliktloesen/:id/edit",
    name: "konfliktloesen.edit",
    component: GetStartedView,
    meta: { authority: "WRITE_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/vorschlaege/index",
    name: "vorschlaege.index",
    component: GetStartedView,
    meta: { authority: "READ_EHRENAMTJUSTIZDATEN" },
  },
  {
    path: "/configuration",
    name: "konfiguration.index",
    component: KonfigurationIndex,
    // meta: { authority: "READ_KONFIGURATION" },
  },
  {
    path: "/configuration/create",
    name: "konfiguration.create",
    component: KonfigurationCreate,
    // meta: { authority: "WRITE_KONFIGURATION" },
  },
  {
    path: "/configuration/:id:action",
    name: "konfiguration.edit",
    component: KonfigurationEdit,
    // meta: { authority: "WRITE_KONFIGURATION" },
  },
  {
    path: "/configuration/:id:action",
    name: "konfiguration.display",
    component: KonfigurationEdit,
    // meta: { authority: "READ_KONFIGURATION" },
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
