import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'

export default defineConfig({
  plugins: [react(), tailwindcss()],
  build: {
    rollupOptions: {
      output: {
        manualChunks(id) {
          if (!id.includes('node_modules')) return
          if (/node_modules\/(react|react-dom|scheduler)\//.test(id)) return 'react-vendor'
          if (id.includes('node_modules/react-router')) return 'router-vendor'
          if (id.includes('node_modules/@tanstack')) return 'query-vendor'
          if (/node_modules\/(react-hook-form|@hookform|zod)\//.test(id)) return 'form-vendor'
          if (id.includes('node_modules/axios/')) return 'http-vendor'
          if (id.includes('node_modules/recharts/')) return 'charts-vendor'
          if (id.includes('node_modules/lucide-react/')) return 'icons-vendor'
        },
      },
    },
  },
  server: {
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true,
      },
    },
  },
})
