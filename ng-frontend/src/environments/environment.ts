// environment.ts
export const environment = {
    production: true,
    // sbServerUrl: 'https://tart-throat-production.up.railway.app', // Railway
    sbServerUrl: 'http://sb-backend-service.default.svc.cluster.local', // Kubernetes
    api: '/api/v1',
    auth: '/api/v1/auth'
};