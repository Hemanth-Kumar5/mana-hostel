import axios from 'axios';

// Ternary-based environment detection: local vs production
const isLocal = typeof window !== 'undefined'
  ? ['localhost', '127.0.0.1'].includes(window.location.hostname)
  : import.meta.env.DEV;

const defaultUrl = isLocal 
  ? 'http://localhost:8081' 
  : 'https://Manahostels.onrender.com';

const rawApi = import.meta.env.VITE_API_URL || defaultUrl;
const api = rawApi.replace(/\/+$/, '');

axios.interceptors.request.use((config) => {
  const publicRoute = /\/api\/(auth\/(login|register|refresh)|students\/(studentlogin|studentregistration))$/.test(config.url || '');
  if (publicRoute) return config;
  const token = localStorage.getItem('Token');
  if (token) {
    config.headers = { ...config.headers, Authorization: `Bearer ${token}` };
  }
  return config;
});

export default api;