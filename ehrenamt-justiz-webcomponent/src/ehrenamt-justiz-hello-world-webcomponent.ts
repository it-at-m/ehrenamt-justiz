import { defineCustomElement } from "vue";

import EhrenamtJustizHelloWorldVueComponent from "@/ehrenamt-justiz-hello-world-webcomponent.ce.vue";

// convert into custom element constructor
const EhrenamtJustizHelloWorldWebComponent = defineCustomElement(
  EhrenamtJustizHelloWorldVueComponent
);

// register
customElements.define(
  "ehrenamt-justiz-hello-world-webcomponent",
  EhrenamtJustizHelloWorldWebComponent
);
