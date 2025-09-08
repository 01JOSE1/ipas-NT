// policies.js
// Mostrar la opción Usuarios en el menú lateral si el usuario es administrador

document.addEventListener('DOMContentLoaded', () => {
    // Mostrar la opción Usuarios en el menú lateral si el usuario es administrador
    if (typeof authService !== 'undefined') {
        const user = authService.getUser();
        if (user && user.role === 'ADMINISTRADOR') {
            document.getElementById('usersNav').style.display = 'block';
        }
    }

    // Detectar clientId en la URL
    const urlParams = new URLSearchParams(window.location.search);
    const clientId = urlParams.get('clientId');
    if (clientId) {
        loadPoliciesByClient(clientId);
    } else {
        loadPolicies();
    }

    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            const query = e.target.value.trim();
            if (query.length >= 2) {
                searchPolicies(query);
            } else if (query.length === 0) {
                if (clientId) {
                    loadPoliciesByClient(clientId);
                } else {
                    loadPolicies();
                }
            }
        });
    }
});
// Cargar pólizas solo de un cliente específico
async function loadPoliciesByClient(clientId) {
    try {
        const response = await apiService.getPoliciesByClient(clientId);
        if (response && response.success) {
            renderPoliciesTable(response.data);
        } else {
            renderPoliciesTable([]);
        }
    } catch (error) {
        renderPoliciesTable([]);
    }
}

async function loadPolicies() {
    try {
        const response = await apiService.getPolicies();
        if (response && response.success) {
            renderPoliciesTable(response.data);
        } else {
            renderPoliciesTable([]);
        }
    } catch (error) {
        renderPoliciesTable([]);
    }
}

async function searchPolicies(query) {
    try {
        const response = await apiService.searchPolicies(query);
        if (response && response.success) {
            renderPoliciesTable(response.data);
        } else {
            renderPoliciesTable([]);
        }
    } catch (error) {
        renderPoliciesTable([]);
    }
}

// Renderizar la tabla de pólizas
function renderPoliciesTable(policies) {
    const tbody = document.getElementById('policiesTable');
    if (!tbody) return;
    if (!policies || policies.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center">No se encontraron pólizas</td></tr>`;
        return;
    }
    tbody.innerHTML = policies.map(policy => `
        <tr>
            <td>${policy.policyNumber}</td>
            <td>${policy.policyType}</td>
            <td>${policy.clientName || '-'}</td>
            <td>${formatCurrency ? formatCurrency(policy.premiumAmount) : policy.premiumAmount}</td>
            <td>
                <span class="badge badge-${getPolicyStatusColor ? getPolicyStatusColor(policy.status) : 'secondary'}">
                    ${getPolicyStatusText ? getPolicyStatusText(policy.status) : policy.status}
                </span>
            </td>
            <td>${formatDate ? formatDate(policy.endDate) : policy.endDate}</td>
            <td>
                <div class="actions">
                    <button class="action-button" onclick="editPolicy(${policy.id})" title="Editar">
                        ✏️
                    </button>
                    <button class="action-button" onclick="viewPolicyDetails(${policy.id})" title="Ver Detalles">
                        👁️
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}


function setupPolicyEventListeners() {
    const searchInput = document.getElementById('searchInput');
    if (searchInput) {
        searchInput.addEventListener('input', (e) => {
            const query = e.target.value.trim();
            if (query.length >= 2) {
                searchPolicies(query);
            } else if (query.length === 0) {
                loadPolicies();
            }
        });
    }
}

async function loadPolicies() {
    try {
        const response = await apiService.getPolicies();
        if (response && response.success) {
            renderPoliciesTable(response.data);
        } else {
            renderPoliciesTable([]);
        }
    } catch (error) {
        renderPoliciesTable([]);
    }
}

async function searchPolicies(query) {
    try {
        const response = await apiService.searchPolicies(query);
        if (response && response.success) {
            renderPoliciesTable(response.data);
        } else {
            renderPoliciesTable([]);
        }
    } catch (error) {
        renderPoliciesTable([]);
    }
}

// Renderizar la tabla de pólizas
function renderPoliciesTable(policies) {
    const tbody = document.getElementById('policiesTable');
    if (!tbody) return;
    if (!policies || policies.length === 0) {
        tbody.innerHTML = `<tr><td colspan="7" class="text-center">No se encontraron pólizas</td></tr>`;
        return;
    }
    tbody.innerHTML = policies.map(policy => `
        <tr>
            <td>${policy.policyNumber}</td>
            <td>${policy.policyType}</td>
            <td>${policy.clientName || '-'}</td>
            <td>${formatCurrency ? formatCurrency(policy.premiumAmount) : policy.premiumAmount}</td>
            <td>
                <span class="badge badge-${getPolicyStatusColor ? getPolicyStatusColor(policy.status) : 'secondary'}">
                    ${getPolicyStatusText ? getPolicyStatusText(policy.status) : policy.status}
                </span>
            </td>
            <td>${formatDate ? formatDate(policy.endDate) : policy.endDate}</td>
            <td>
                <div class="actions">
                    <button class="action-button" onclick="editPolicy(${policy.id})" title="Editar">
                        ✏️
                    </button>
                    <button class="action-button" onclick="viewPolicyDetails(${policy.id})" title="Ver Detalles">
                        👁️
                    </button>
                </div>
            </td>
        </tr>
    `).join('');
}
// ...existing code...
