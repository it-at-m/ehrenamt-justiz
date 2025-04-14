// Composables

import { createRouter, createWebHashHistory } from "vue-router";

import { ROUTES_HOME } from "@/Constants";
import MainView from "@/views/MainView.vue";

const routes = [
  {
    path: "/",
    name: ROUTES_HOME,
    component: MainView,
  },
  { path: "/:catchAll(.*)*", redirect: "/" }, // CatchAll route
];

const router = createRouter({
  history: createWebHashHistory(),
  routes,
});
router.beforeEach((to, _from, next) => {
  if (to.name === "home") {
    //home ist immer erlaubt
    next();
  } else {
    next({ name: "home" });
  }
});
export default router;
