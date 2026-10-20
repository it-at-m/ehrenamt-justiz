import { defineConfig } from "vitepress";
import { withMermaid } from "vitepress-plugin-mermaid";

// https://vitepress.dev/reference/site-config
const vitepressConfig = defineConfig({
  base: "/ehrenamt-justiz/", // needs to be changed if delivered via sub path (e.g. "/docs/" for example.com/docs)
  title: "Ehrenamt-Justiz",
  description: "Documentation for the Ehrenamt-Justiz",
  head: [
    [
      "link",
      {
        rel: "icon",
        href: `https://assets.muenchen.de/logos/lhm/icon-lhm-muenchen-32.png`,
      },
    ],
  ],
  lastUpdated: true,
  themeConfig: {
    // https://vitepress.dev/reference/default-theme-config
    nav: [
      { text: "Home", link: "/" },
      {
        text: "Docs",
        items: [
          { text: "Getting Started", link: "/getting-started" },
          { text: "Develop", link: "/develop" },
          { text: "Document", link: "/document" },
          { text: "Organize", link: "/organize" },
        ],
      },
      { text: "Contribute", link: "/contribute" },
    ],
    sidebar: [
      { text: "Getting Started", link: "/getting-started" },
      { text: "Develop", link: "/develop" },
      { text: "Document", link: "/document" },
      { text: "Organize", link: "/organize" },
    ],
    socialLinks: [
      { icon: "github", link: "https://github.com/it-at-m/refarch-templates" },
    ],
    editLink: {
      pattern:
          "https://github.com/it-at-m/refarch-templates/blob/main/docs/:path",
      text: "View this page on GitHub",
    },
    footer: {
      message: `<a href="https://opensource.muenchen.de/impress.html">Impress and Contact</a>`,
    },
    outline: {
      level: "deep",
    },
    search: {
      provider: "local",
    },
  },
  markdown: {
    image: {
      lazyLoading: true,
    },
  },
  // see https://github.com/emersonbottero/vitepress-plugin-mermaid/issues/98 and https://github.com/vitejs/devtools/pull/567
  vite: {
    optimizeDeps: {
      include: ["fastdom", "fastdom/extensions/fastdom-promised.js"],
    },
  },
});

export default withMermaid(vitepressConfig);