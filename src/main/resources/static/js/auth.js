class AuthService {
    constructor() {
        this.baseURL = '/api/auth';
        this.token = localStorage.getItem('authToken');
        this.user = JSON.parse(localStorage.getItem('user') || 'null');
        
        console.log('🔧 AuthService initialized');
        console.log('🔑 Token from storage:', this.token ? 'Present' : 'Not found');
        console.log('👤 User from storage:', this.user);
    }

    async login(email, password, twoFactorCode = null) {
        try {
            console.log('🔐 Attempting login for:', email);
            
            const loginData = { email, password };
            if (twoFactorCode) {
                loginData.twoFactorCode = twoFactorCode;
            }
            
            const response = await fetch(`${this.baseURL}/login`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(loginData)
            });

            console.log('📡 Login response status:', response.status);
            
            const data = await response.json();
            console.log('📨 Login response data:', data);

            if (data.success) {
                if (data.requiresTwoFactor) {
                    console.log('🔐 Two-factor authentication required');
                    return { success: true, requiresTwoFactor: true };
                }

                // Verificar que tenemos los datos necesarios
                if (!data.data || !data.data.token || !data.data.user) {
                    console.error('❌ Invalid response structure:', data);
                    return { success: false, message: 'Respuesta del servidor inválida' };
                }

                this.token = data.data.token;
                this.user = data.data.user;
                
                localStorage.setItem('authToken', this.token);
                localStorage.setItem('user', JSON.stringify(this.user));
                
                console.log('✅ Login successful, token saved');
                console.log('🔑 Token:', this.token.substring(0, 20) + '...');
                console.log('👤 User:', this.user);
                
                return { success: true };
            }

            console.log('❌ Login failed:', data.message);
            return { success: false, message: data.message };
            
        } catch (error) {
            console.error('❌ Login error:', error);
            return { success: false, message: 'Error de conexión' };
        }
    }

    async forgotPassword(email) {
        try {
            console.log('🔄 Requesting password reset for:', email);
            
            const response = await fetch(`${this.baseURL}/forgot-password`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email })
            });

            const data = await response.json();
            console.log('📨 Password reset response:', data);
            return data;
        } catch (error) {
            console.error('❌ Forgot password error:', error);
            return { success: false, message: 'Error de conexión' };
        }
    }

    async resetPassword(token, newPassword) {
        try {
            console.log('🔄 Resetting password with token');
            
            const response = await fetch(`${this.baseURL}/reset-password?token=${token}&newPassword=${newPassword}`, {
                method: 'POST'
            });

            const data = await response.json();
            console.log('📨 Reset password response:', data);
            return data;
        } catch (error) {
            console.error('❌ Reset password error:', error);
            return { success: false, message: 'Error de conexión' };
        }
    }

    logout() {
        console.log('🚪 Logging out user');
        this.token = null;
        this.user = null;
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
        window.location.href = '/login';
    }

    isAuthenticated() {
        const hasToken = !!this.token;
        const hasUser = !!this.user;
        
        console.log('🔍 Checking authentication:');
        console.log('  - Has token:', hasToken);
        console.log('  - Has user:', hasUser);
        
        if (!hasToken || !hasUser) {
            console.log('❌ Not authenticated - missing token or user');
            return false;
        }

        // Verificar si el token ha expirado
        try {
            const payload = JSON.parse(atob(this.token.split('.')[1]));
            const currentTime = Date.now() / 1000;
            
            if (payload.exp < currentTime) {
                console.log('❌ Token has expired');
                this.logout();
                return false;
            }
            
            console.log('✅ User is authenticated');
            return true;
        } catch (error) {
            console.log('❌ Invalid token format:', error);
            // No hacer logout aquí para evitar loops, solo limpiar
            this.token = null;
            this.user = null;
            localStorage.removeItem('authToken');
            localStorage.removeItem('user');
            return false;
        }
    }

    getAuthHeaders() {
        const headers = {
            'Content-Type': 'application/json'
        };
        
        if (this.token) {
            headers['Authorization'] = `Bearer ${this.token}`;
            console.log('🔑 Adding Authorization header');
        } else {
            console.log('⚠️ No token available for Authorization header');
        }
        
        return headers;
    }

    redirectIfNotAuthenticated() {
        if (!this.isAuthenticated()) {
            console.log('🚫 Not authenticated, redirecting to login');
            window.location.href = '/login';
            return false;
        }
        console.log('✅ User authenticated, allowing access');
        return true;
    }

    redirectIfAuthenticated() {
        if (this.isAuthenticated()) {
            console.log('✅ Already authenticated, redirecting to dashboard');
            window.location.href = '/dashboard';
            return false;
        }
        console.log('❌ Not authenticated, staying on current page');
        return true;
    }

    hasRole(role) {
        const userHasRole = this.user && this.user.role === role;
        console.log(`🔍 Checking role ${role}:`, userHasRole);
        return userHasRole;
    }

    getUser() {
        return this.user;
    }
}

// Global auth service instance
const authService = new AuthService();

// Utility functions (mejoradas)
function showAlert(message, type = 'error') {
    console.log(`🔔 Showing ${type} alert:`, message);
    
    // Remover alert anterior si existe
    const existingAlert = document.querySelector('.alert');
    if (existingAlert) {
        existingAlert.remove();
    }

    const alertElement = document.createElement('div');
    alertElement.className = `alert alert-${type}`;
    alertElement.style.cssText = `
        position: fixed;
        top: 20px;
        right: 20px;
        padding: 12px 20px;
        border-radius: 8px;
        color: white;
        font-weight: 500;
        z-index: 1000;
        max-width: 350px;
        box-shadow: 0 4px 12px rgba(0,0,0,0.15);
        ${type === 'error' ? 'background: linear-gradient(135deg, #ef4444, #dc2626);' : 'background: linear-gradient(135deg, #10b981, #059669);'}
        animation: slideIn 0.3s ease-out;
    `;
    alertElement.textContent = message;
    
    // Add animation styles if not already added
    if (!document.querySelector('#alert-styles')) {
        const styles = document.createElement('style');
        styles.id = 'alert-styles';
        styles.textContent = `
            @keyframes slideIn {
                from { transform: translateX(100%); opacity: 0; }
                to { transform: translateX(0); opacity: 1; }
            }
            @keyframes slideOut {
                from { transform: translateX(0); opacity: 1; }
                to { transform: translateX(100%); opacity: 0; }
            }
        `;
        document.head.appendChild(styles);
    }
    
    document.body.appendChild(alertElement);
    
    // Auto-remove after 5 seconds
    setTimeout(() => {
        if (alertElement.parentNode) {
            alertElement.style.animation = 'slideOut 0.3s ease-in';
            setTimeout(() => alertElement.remove(), 300);
        }
    }, 5000);
}

function showLoading(element, loading = true) {
    if (loading) {
        console.log('⏳ Showing loading state');
        element.disabled = true;
        element.dataset.originalText = element.textContent;
        element.innerHTML = `
            <span style="display: inline-flex; align-items: center; gap: 8px;">
                <div style="width: 16px; height: 16px; border: 2px solid transparent; border-top: 2px solid currentColor; border-radius: 50%; animation: spin 1s linear infinite;"></div>
                Cargando...
            </span>
        `;
        
        // Add spinner animation if not already added
        if (!document.querySelector('#spinner-styles')) {
            const styles = document.createElement('style');
            styles.id = 'spinner-styles';
            styles.textContent = `
                @keyframes spin {
                    0% { transform: rotate(0deg); }
                    100% { transform: rotate(360deg); }
                }
            `;
            document.head.appendChild(styles);
        }
    } else {
        console.log('✅ Hiding loading state');
        element.disabled = false;
        element.textContent = element.dataset.originalText || 'Enviar';
    }
}

function formatDate(dateString) {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleDateString('es-ES');
}

function formatCurrency(amount) {
    if (!amount) return '-';
    return new Intl.NumberFormat('es-ES', {
        style: 'currency',
        currency: 'PEN'
    }).format(amount);
}

// Debug helper - remove in production
window.authDebug = {
    getToken: () => authService.token,
    getUser: () => authService.user,
    checkAuth: () => authService.isAuthenticated(),
    clearStorage: () => {
        localStorage.removeItem('authToken');
        localStorage.removeItem('user');
        console.log('🗑️ Storage cleared');
    }
};

console.log('🚀 Auth service loaded. Use authDebug in console for debugging.');