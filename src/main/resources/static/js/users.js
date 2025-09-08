// Check authentication and admin role
if (authService.redirectIfNotAuthenticated()) {
    const user = authService.getUser();
    
    if (!authService.hasRole('ADMINISTRADOR')) {
        window.location.href = '/dashboard';
    }
    
    // Setup user info
    document.getElementById('userName').textContent = `${user.firstName} ${user.lastName}`;
    document.getElementById('userAvatar').textContent = user.firstName.charAt(0).toUpperCase();
    
    // Mobile menu toggle
    document.getElementById('menuToggle').addEventListener('click', () => {
        document.getElementById('sidebar').classList.toggle('open');
    });
    
    // Global variables
    let users = [];
    let editingUserId = null;

    // Initialize
    loadUsers();
    setupEventListeners();
}

function setupEventListeners() {
    // Search functionality
    document.getElementById('searchInput').addEventListener('input', (e) => {
        const query = e.target.value.trim();
        if (query.length >= 2) {
            searchUsers(query);
        } else if (query.length === 0) {
            loadUsers();
        }
    });

    // User form submission
    document.getElementById('userForm').addEventListener('submit', handleUserSubmit);
}

async function loadUsers() {
    try {
        const response = await apiService.getUsers();
        
        if (response && response.success) {
            users = response.data;
            renderUsersTable(users);
        } else {
            showAlert('Error al cargar usuarios');
        }
    } catch (error) {
        console.error('Error loading users:', error);
        showAlert('Error de conexión al cargar usuarios');
    }
}

async function searchUsers(query) {
    try {
        const response = await apiService.searchUsers(query);
        
        if (response && response.success) {
            renderUsersTable(response.data);
        } else {
            showAlert('Error en la búsqueda');
        }
    } catch (error) {
        console.error('Error searching users:', error);
        showAlert('Error de conexión en la búsqueda');
    }
}

function renderUsersTable(userList) {
    const tbody = document.getElementById('usersTable');
    if (userList.length === 0) {
        tbody.innerHTML = '<tr><td colspan="6" class="text-center">No se encontraron usuarios</td></tr>';
        return;
    }
    const currentUser = authService.getUser();
    tbody.innerHTML = userList.map(user => {
        const canEdit = currentUser && currentUser.role === 'ADMINISTRADOR';
        return `
        <tr>
            <td>${user.firstName} ${user.lastName}</td>
            <td>${user.email}</td>
            <td>
                <span class="badge badge-${getRoleColor(user.role)}">
                    ${getRoleText(user.role)}
                </span>
            </td>
            <td>
                <span class="badge badge-${getStatusColor(user.status)}">
                    ${getStatusText(user.status)}
                </span>
            </td>
            <td>${formatDate(user.lastLogin)}</td>
            <td>
                <div class="actions">
                    <button class="action-button" onclick="editUser(${user.id})" title="Editar" ${canEdit ? '' : 'disabled'}>
                        ✏️
                    </button>
                    <button class="action-button" onclick="deleteUser(${user.id})" title="Eliminar" ${canEdit ? '' : 'disabled'}>
                        🗑️
                    </button>
                </div>
            </td>
        </tr>
        `;
    }).join('');
}


function getRoleColor(role) {
    switch (role) {
        case 'ADMINISTRADOR': return 'error';
        case 'ASESOR': return 'secondary';
        default: return 'secondary';
    }
}

function getRoleText(role) {
    switch (role) {
        case 'ADMINISTRADOR': return 'Administrador';
        case 'ASESOR': return 'Asesor';
        default: return role;
    }
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

function openUserModal(userData = null) {
    const modal = document.getElementById('userModal');
    const form = document.getElementById('userForm');
    const title = document.getElementById('modalTitle');
    const passwordGroup = document.getElementById('passwordGroup');
    
    if (userData) {
        title.textContent = 'Editar Usuario';
        fillUserForm(userData);
        editingUserId = userData.id;
        passwordGroup.style.display = 'none';
        document.getElementById('password').required = false;
    } else {
        title.textContent = 'Nuevo Usuario';
        form.reset();
        editingUserId = null;
        passwordGroup.style.display = 'block';
        document.getElementById('password').required = true;
    }
    
    modal.classList.add('show');
}

function closeUserModal() {
    const modal = document.getElementById('userModal');
    modal.classList.remove('show');
    editingUserId = null;
}

function fillUserForm(user) {
    document.getElementById('firstName').value = user.firstName;
    document.getElementById('lastName').value = user.lastName;
    document.getElementById('email').value = user.email;
    document.getElementById('phoneNumber').value = user.phoneNumber || '';
    document.getElementById('role').value = user.role;
}

async function handleUserSubmit(e) {
    e.preventDefault();
    
    const saveBtn = document.getElementById('saveUserBtn');
    showLoading(saveBtn, true);
    
    const formData = new FormData(e.target);
    const userData = {
        firstName: formData.get('firstName'),
        lastName: formData.get('lastName'),
        email: formData.get('email'),
        phoneNumber: formData.get('phoneNumber') || null,
        role: formData.get('role')
    };
    
    if (!editingUserId) {
        userData.password = formData.get('password');
    }
    
    try {
        let response;
        if (editingUserId) {
            response = await apiService.updateUser(editingUserId, userData);
        } else {
            response = await apiService.createUser(userData);
        }
        
        showLoading(saveBtn, false);
        
        if (response && response.success) {
            showAlert(response.message || 'Usuario guardado exitosamente', 'success');
            closeUserModal();
            loadUsers();
        } else if (response && response.message) {
            showAlert(response.message, 'error');
        } else {
            showAlert('Error al guardar usuario', 'error');
        }
    } catch (error) {
        showLoading(saveBtn, false);
        console.error('Error saving user:', error);
        if (error && error.message) {
            showAlert(error.message, 'error');
        } else {
            showAlert('Error de conexión al guardar usuario', 'error');
        }
    }
}


// Editar usuario y mostrar pólizas asociadas a sus clientes
async function editUser(userId) {
    const currentUser = authService.getUser();
    if (!currentUser || currentUser.role !== 'ADMINISTRADOR') {
        showAlert('Solo el administrador puede editar usuarios.', 'error');
        return;
    }
    try {
        const response = await apiService.getUser(userId);
        if (response && response.success) {
            openUserModal(response.data);
            // Obtener pólizas asociadas a los clientes de este usuario
            const policiesResponse = await apiService.getPoliciesByUser(userId);
            if (policiesResponse && policiesResponse.success) {
                // Aquí puedes mostrar las pólizas en el UI, por ejemplo en un modal o sección aparte
                // Por ahora, solo las mostramos en consola
                console.log('Pólizas asociadas a los clientes del usuario:', policiesResponse.data);
                // Si tienes un elemento en el HTML para mostrar las pólizas, puedes renderizarlas aquí
                // renderPoliciesForUser(policiesResponse.data);
            }
        } else {
            showAlert('Error al cargar usuario');
        }
    } catch (error) {
        console.error('Error loading user for edit:', error);
        showAlert('Error de conexión al cargar usuario');
    }
}

async function deleteUser(userId) {
    if (!confirm('¿Está seguro de que desea eliminar este usuario?')) {
        return;
    }
    
    try {
        const response = await apiService.deleteUser(userId);
        
        if (response && response.success) {
            showAlert('Usuario eliminado exitosamente', 'success');
            loadUsers();
        } else {
            showAlert(response?.message || 'Error al eliminar usuario');
        }
    } catch (error) {
        console.error('Error deleting user:', error);
        showAlert('Error de conexión al eliminar usuario');
    }
}

// Close modal on outside click
document.getElementById('userModal').addEventListener('click', (e) => {
    if (e.target.id === 'userModal') {
        closeUserModal();
    }
});