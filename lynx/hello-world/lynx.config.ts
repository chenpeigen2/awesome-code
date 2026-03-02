import { defineConfig } from "@lynx-js/rspeedy";

import { pluginQRCode } from "@lynx-js/qrcode-rsbuild-plugin";
import { pluginReactLynx } from "@lynx-js/react-rsbuild-plugin";
import { pluginTypeCheck } from "@rsbuild/plugin-type-check";

export default defineConfig({
  plugins: [
    pluginQRCode(),
    pluginReactLynx(),
    pluginTypeCheck({
      enable: false,
    }),
  ],
  output: {
    dataUriLimit: Infinity,
    filename: "[name].[platform].bundle",
  },
  source: {
    exclude: [/__tests__/, /\.test\.(ts|tsx)$/, /\.spec\.(ts|tsx)$/],
  },
  environments: {
    web: {},
    lynx: {},
  },
});
