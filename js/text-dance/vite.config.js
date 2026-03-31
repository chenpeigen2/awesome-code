import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    port: 3344,
    open: true,
    watch: {
      usePolling: true,
      interval: 1000,
    },
  },
});
