import { createApp } from "vue";

import App from "@/App.vue";
import { registerPlugins } from "@/plugins";

import "unfonts.css";
import "@/styles/main.scss";

const app = createApp(App);

registerPlugins(app);

app.mount("#app");
