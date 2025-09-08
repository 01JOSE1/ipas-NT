// Check authentication
document.addEventListener('DOMContentLoaded', () => {
    if (authService.redirectIfNotAuthenticated()) {
        const user = authService.getUser();
        // Setup user info
        document.getElementById('userName').textContent = `${user.firstName} ${user.lastName}`;
        document.getElementById('userAvatar').textContent = user.firstName.charAt(0).toUpperCase();
        // Show admin-only elements
        if (user.role === 'ADMINISTRADOR') {
            document.getElementById('usersNav').style.display = 'block';
        }
        // Mobile menu toggle
        document.getElementById('menuToggle').addEventListener('click', () => {
            document.getElementById('sidebar').classList.toggle('open');
        });
        // Global variables
        let clients = [];
        let editingClientId = null;
        // Initialize
        loadClients();
        setupEventListeners();
    }
});

function setupEventListeners() {
    // Search functionality
    document.getElementById('searchInput').addEventListener('input', (e) => {
        const query = e.target.value.trim();
        if (query.length >= 2) {
            searchClients(query);
        } else if (query.length === 0) {
            loadClients();
        }
    });

    // Client form submission
    document.getElementById('clientForm').addEventListener('submit', handleClientSubmit);
}

async function loadClients() {
    try {
        const response = await apiService.getClients();
        
        if (response && response.success) {
            clients = response.data;
            renderClientsTable(clients);
        } else {
            showAlert('Error al cargar clientes');
        }
    } catch (error) {
        console.error('Error loading clients:', error);
        showAlert('Error de conexión al cargar clientes');
    }
}

async function searchClients(query) {
    try {
        const response = await apiService.searchClients(query);
        
        if (response && response.success) {
            renderClientsTable(response.data);
        } else {
            showAlert('Error en la búsqueda');
        }
    } catch (error) {
        console.error('Error searching clients:', error);
        showAlert('Error de conexión en la búsqueda');
    }
}

function renderClientsTable(clientList) {
    const tbody = document.getElementById('clientsTable');
    if (clientList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="7" class="text-center">No se encontraron clientes</td></tr>';
        return;
    }
    tbody.innerHTML = clientList.map(client => {
        // Debug: log address for each client
        console.log('Render address:', client.address);
        return `
        <tr>
            <td>${client.documentNumber}</td>
            <td>${client.firstName} ${client.lastName}</td>
            <td>${client.email || '-'}</td>
            <td>${client.phoneNumber || '-'}</td>
            <td>${client.address && client.address.trim() !== '' ? client.address : '-'}</td>
            <td>
                <span class="badge badge-${getStatusColor(client.status)}">
                    ${typeof client.status === 'string' ? getStatusText(client.status) : 'Activo'}
                </span>
            </td>
            <td>
                <div class="actions">
                    <button class="action-button" onclick="editClient(${client.id})" title="Editar">
                        ✏️
                    </button>
                    <button class="action-button" onclick="viewClientPolicies(${client.id})" title="Ver Pólizas">
                        📋
                    </button>
                </div>
            </td>
        </tr>
        `;
    }).join('');
}

function getStatusColor(status) {
    switch (status) {
        case 'ACTIVE': return 'success';
        case 'INACTIVE': return 'warning';
        case 'SUSPENDED': return 'error';
        default: return 'secondary';
    }
}

function getStatusText(status) {
    switch (status) {
        case 'ACTIVE': return 'Activo';
        case 'INACTIVE': return 'Inactivo';
        case 'SUSPENDED': return 'Suspendido';
        default: return status;
    }
}

function openClientModal(clientData = null) {
    const modal = document.getElementById('clientModal');
    const form = document.getElementById('clientForm');
    const title = document.getElementById('modalTitle');
    
    if (clientData) {
        title.textContent = 'Editar Cliente';
        fillClientForm(clientData);
        editingClientId = clientData.id;
    } else {
        title.textContent = 'Nuevo Cliente';
        form.reset();
        editingClientId = null;
    }
    
    modal.classList.add('show');
}

function closeClientModal() {
    const modal = document.getElementById('clientModal');
    modal.classList.remove('show');
    editingClientId = null;
}

function fillClientForm(client) {
    document.getElementById('documentType').value = client.documentType;
    document.getElementById('documentNumber').value = client.documentNumber;
    document.getElementById('firstName').value = client.firstName;
    document.getElementById('lastName').value = client.lastName;
    document.getElementById('email').value = client.email || '';
    document.getElementById('phoneNumber').value = client.phoneNumber || '';
    document.getElementById('birthDate').value = client.birthDate || '';
    document.getElementById('occupation').value = client.occupation || '';
    document.getElementById('address').value = client.address || '';
}

async function handleClientSubmit(e) {
    e.preventDefault();
    
    const saveBtn = document.getElementById('saveClientBtn');
    showLoading(saveBtn, true);
    
    const formData = new FormData(e.target);
    const clientData = {
        documentType: formData.get('documentType'),
        documentNumber: formData.get('documentNumber'),
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        email: formData.get('email') || null,
        phoneNumber: formData.get('phoneNumber') || null,
        birthDate: formData.get('birthDate') || null,
        occupation: formData.get('occupation') || null,
        address: formData.get('address') || null
    };
    
    try {
        let response;
        if (editingClientId) {
            response = await apiService.updateClient(editingClientId, clientData);
        } else {
            response = await apiService.createClient(clientData);
        }
        
        showLoading(saveBtn, false);
        
        if (response && response.success) {
            showAlert(response.message || 'Cliente guardado exitosamente', 'success');
            closeClientModal();
            loadClients();
        } else {
            showAlert(response?.message || 'Error al guardar cliente');
        }
    } catch (error) {
        showLoading(saveBtn, false);
        console.error('Error saving client:', error);
        showAlert('Error de conexión al guardar cliente');
    }
}

async function editClient(clientId) {
    try {
        const response = await apiService.getClient(clientId);
        
        if (response && response.success) {
            openClientModal(response.data);
        } else {
            showAlert('Error al cargar cliente');
        }
    } catch (error) {
        console.error('Error loading client for edit:', error);
        showAlert('Error de conexión al cargar cliente');
    }
}

function viewClientPolicies(clientId) {
    window.location.href = `/policies?clientId=${clientId}`;
}

// Close modal on outside click
document.getElementById('clientModal').addEventListener('click', (e) => {
    if (e.target.id === 'clientModal') {
        closeClientModal();
    }
});