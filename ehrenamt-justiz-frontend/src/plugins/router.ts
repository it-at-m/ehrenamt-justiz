// Composables
import { createRouter, createWebHistory } from "vue-router";
import {
  routes as fileBasedRoutes,
  handleHotUpdate,
} from "vue-router/auto-routes";

import { useUserInfoStore } from "@/stores/userinfo";

const routes = [
  ...fileBasedRoutes,
  { path: "/:catchAll(.*)*", redirect: "/" }, // CatchAll route
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

router.beforeEach(async () => {
  const userInfoStore = useUserInfoStore();
  if (!userInfoStore.userInfo) {
    await userInfoStore.fetchUserInfo();
  }
  return true;
});

if (import.meta.hot) {
  handleHotUpdate(router);
}

export default router;
