// environment.ts
export const environment = {
    production: true,
    // sbServerUrl: 'https://tart-throat-production.up.railway.app', // Railway
    sbServerUrl: '', // Kubernetes, using Ingress
    api: '/api/v1',
    auth: '/api/v1/auth'
};