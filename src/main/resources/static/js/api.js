class ApiService {
    constructor() {
        this.baseURL = '/api';
        // console.log('🔧 ApiService initialized with baseURL:', this.baseURL);
    }

    async request(endpoint, options = {}) {
        const url = `${this.baseURL}${endpoint}`;
        const config = {
            ...options,
            headers: {
                ...authService.getAuthHeaders(),
                ...options.headers
            }
        };

        // console.log(`📡 Making ${config.method || 'GET'} request to:`, url);
        // console.log('🔑 Request headers:', config.headers);
        
        if (config.body) {
            console.log('📦 Request body:', config.body);
        }

        try {
            const response = await fetch(url, config);
            // console.log(`📨 Response status: ${response.status} ${response.statusText}`);
            
            // Check if response is ok
            if (!response.ok) {
                console.log(`❌ HTTP Error: ${response.status}`);
                
                if (response.status === 401) {
                    console.log('🚫 Unauthorized - redirecting to login');
                    authService.logout();
                    return null;
                }
                
                if (response.status === 403) {
                    console.log('🚫 Forbidden - insufficient permissions');
                    throw new Error('No tienes permisos para realizar esta acción');
                }
                
                if (response.status === 404) {
                    console.log('🔍 Not found');
                    throw new Error('Recurso no encontrado');
                }
                
                if (response.status >= 500) {
                    console.log('🔥 Server error');
                    throw new Error('Error interno del servidor');
                }
            }
            
            const contentType = response.headers.get('content-type');
            let data;
            
            if (contentType && contentType.includes('application/json')) {
                data = await response.json();
                // console.log('📨 Response data:', data);
            } else {
                console.log('⚠️ Non-JSON response, getting text');
                data = await response.text();
                console.log('📨 Response text:', data);
            }
            
            return data;
            
        } catch (error) {
            console.error(`❌ API Error for ${endpoint}:`, error);
            
            // Network error
            if (error instanceof TypeError && error.message.includes('fetch')) {
                throw new Error('Error de conexión. Verifica tu conexión a internet.');
            }
            
            // Re-throw other errors
            throw error;
        }
    }

    // Client endpoints
    async getClients() {
        console.log('👥 Fetching clients...');
        return this.request('/clients');
    }

    async getClient(id) {
        console.log('👤 Fetching client:', id);
        return this.request(`/clients/${id}`);
    }

    async createClient(clientData) {
        console.log('➕ Creating client:', clientData);
        return this.request('/clients', {
            method: 'POST',
            body: JSON.stringify(clientData)
        });
    }

    async updateClient(id, clientData) {
        console.log('✏️ Updating client:', id, clientData);
        return this.request(`/clients/${id}`, {
            method: 'PUT',
            body: JSON.stringify(clientData)
        });
    }

    async deleteClient(id) {
        console.log('🗑️ Deleting client:', id);
        return this.request(`/clients/${id}`, {
            method: 'DELETE'
        });
    }

    async searchClients(query) {
        console.log('🔍 Searching clients:', query);
        return this.request(`/clients/search?q=${encodeURIComponent(query)}`);
    }

    // Policy endpoints
    async getPolicies() {
        console.log('📋 Fetching policies...');
        return this.request('/policies');
    }

    async getPolicy(id) {
        console.log('📄 Fetching policy:', id);
        return this.request(`/policies/${id}`);
    }

    async createPolicy(policyData) {
        console.log('➕ Creating policy:', policyData);
        return this.request('/policies', {
            method: 'POST',
            body: JSON.stringify(policyData)
        });
    }

    async updatePolicy(id, policyData) {
        console.log('✏️ Updating policy:', id, policyData);
        return this.request(`/policies/${id}`, {
            method: 'PUT',
            body: JSON.stringify(policyData)
        });
    }

    async deletePolicy(id) {
        console.log('🗑️ Deleting policy:', id);
        return this.request(`/policies/${id}`, {
            method: 'DELETE'
        });
    }

    async searchPolicies(query) {
        console.log('🔍 Searching policies:', query);
        return this.request(`/policies/search?q=${encodeURIComponent(query)}`);
    }

    // User endpoints (admin only)
    async getUsers() {
        // console.log('👥 Fetching users...');
        return this.request('/users');
    }

    async getUser(id) {
        console.log('👤 Fetching user:', id);
        return this.request(`/users/${id}`);
    }

    async createUser(userData) {
        console.log('➕ Creating user:', userData);
        return this.request('/users', {
            method: 'POST',
            body: JSON.stringify(userData)
        });
    }

    async updateUser(id, userData) {
        console.log('✏️ [api.js] Enviando actualización de usuario:', id, userData);
        return this.request(`/users/${id}`, {
            method: 'PUT',
            body: JSON.stringify(userData)
        });
    }

    async deleteUser(id) {
        console.log('🗑️ Deleting user:', id);
        return this.request(`/users/${id}`, {
            method: 'DELETE'
        });
    }

    async searchUsers(query) {
        console.log('🔍 Searching users:', query);
        return this.request(`/users/search?q=${encodeURIComponent(query)}`);
    }

    // Profile endpoints
    async getProfile() {
        console.log('👤 Fetching profile...');
        return this.request('/profile');
    }

    async updateProfile(profileData) {
        console.log('✏️ Updating profile:', profileData);
        return this.request('/profile', {
            method: 'PUT',
            body: JSON.stringify(profileData)
        });
    }

    async changePassword(userId, passwordData) {
        console.log('🔒 Changing password for user:', userId);
        return this.request(`/users/${userId}/password`, {
            method: 'PUT',
            body: JSON.stringify(passwordData)
        });
    }

    // Dashboard endpoints
    async getDashboardStats() {
        console.log('📊 Fetching dashboard stats...');
        return this.request('/dashboard/stats');
    }

    // Reports endpoints
    async getReports(params = {}) {
        console.log('📈 Fetching reports with params:', params);
        const queryString = new URLSearchParams(params).toString();
        return this.request(`/reports${queryString ? '?' + queryString : ''}`);
    }

    // Health check endpoint (useful for debugging)
    async healthCheck() {
        console.log('💓 Checking API health...');
        try {
            const response = await fetch(`${this.baseURL}/health`, {
                method: 'GET',
                headers: authService.getAuthHeaders()
            });
            console.log('💓 Health check response:', response.status);
            return response.ok;
        } catch (error) {
            console.error('💔 Health check failed:', error);
            return false;
        }
    }
}

// Global API service instance
const apiService = new ApiService();

// Debug helper - remove in production
window.apiDebug = {
    testConnection: async () => {
        console.log('🧪 Testing API connection...');
        try {
            const isHealthy = await apiService.healthCheck();
            console.log('Health check result:', isHealthy);
            
            // Test a simple endpoint
            const result = await apiService.getClients();
            console.log('Test API call result:', result);
            return result;
        } catch (error) {
            console.error('API test failed:', error);
            return null;
        }
    },
    
    testAuth: () => {
        const headers = authService.getAuthHeaders();
        console.log('Auth headers:', headers);
        return headers;
    },
    
    makeTestRequest: async (endpoint) => {
        console.log('🧪 Making test request to:', endpoint);
        return await apiService.request(endpoint);
    }
};

// console.log('🚀 API service loaded. Use apiDebug in console for debugging.');