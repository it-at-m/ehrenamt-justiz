import { defineCustomElement } from "vue";

import EhrenamtJustizHelloWorldVueComponent from "@/ehrenamt-justiz-hello-world-webcomponent.ce.vue";

// convert into custom element constructor
const RefarchHelloWorldWebComponent = defineCustomElement(
    EhrenamtJustizHelloWorldVueComponent
);

// register
customElements.define(
  "refarch-hello-world-webcomponent",
  RefarchHelloWorldWebComponent
);
