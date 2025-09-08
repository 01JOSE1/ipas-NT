// Cargar y mostrar usuarios para el administrador
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

console.log('🟢 INICIO users.js');

// ...existing code...

// Registrar el listener al final, cuando todas las funciones ya están declaradas
document.addEventListener('DOMContentLoaded', function() {
    console.log('🟠 [users.js] DOMContentLoaded fired');
    // Ocultar pestañas de clientes y pólizas si es ADMINISTRADOR en todas las páginas con menú
    try {
        const user = authService.getUser();
        if (user && user.role === 'ADMINISTRADOR') {
            const clientsTab = document.querySelector('a[href="/clients"]');
            const policiesTab = document.querySelector('a[href="/policies"]');
            if (clientsTab) clientsTab.style.display = 'none';
            if (policiesTab) policiesTab.style.display = 'none';
        }
    } catch (e) { console.log('Error ocultando pestañas para admin:', e); }

    // Ocultar pestañas de clientes y pólizas si es ADMINISTRADOR
    const user = authService.getUser();
    if (user && user.role === 'ADMINISTRADOR') {
        const clientsTab = document.querySelector('a[href="/clients"]');
        const policiesTab = document.querySelector('a[href="/policies"]');
        if (clientsTab) clientsTab.style.display = 'none';
        if (policiesTab) policiesTab.style.display = 'none';
    }
    const editUserForm = document.getElementById('editUserForm');
    console.log('🟡 [users.js] Buscando editUserForm:', editUserForm);
    if (editUserForm) {
        console.log('🟢 [users.js] Se encontró el formulario editUserForm y se agregará el eventListener');
        editUserForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            console.log('🟡 [users.js] submit editUserForm');
            if (!window.editingUserData) {
                console.log('🔴 [users.js] No editingUserData');
                return;
            }
            const newRole = document.getElementById('editRole').value;
            const newStatus = document.getElementById('editStatus').value;
            console.log('🟢 [users.js] Valores seleccionados para actualizar:', { role: newRole, status: newStatus });
            try {
                const response = await apiService.updateUser(window.editingUserData.id, { role: newRole, status: newStatus });
                console.log('🟢 [users.js] Respuesta updateUser:', typeof response, response);
                if ((response && response.success) || (response && response.id)) {
                    showAlert('Usuario actualizado', 'success');
                    window.closeEditUserModal();
                    loadUsers();
                } else {
                    showAlert('Error al actualizar usuario', 'error');
                    console.warn('⚠️ [users.js] Respuesta inesperada updateUser:', response);
                }
            } catch (error) {
                console.error('🔴 [users.js] Error updateUser:', error);
                showAlert('Error de conexión al actualizar usuario', 'error');
            }
        });
    } else {
        console.error('🔴 [users.js] No se encontró el formulario editUserForm al cargar DOMContentLoaded');
    }
});

// ...existing code...

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
// Modal editar usuario
window.editingUserData = null;
window.openEditUserModal = function(user) {
    window.editingUserData = user;
    document.getElementById('editRole').value = user.role;
    document.getElementById('editStatus').value = user.status;
    document.getElementById('editUserModal').classList.add('show');
}

window.closeEditUserModal = function() {
    window.editingUserData = null;
    document.getElementById('editUserModal').classList.remove('show');
// (fin de archivo)

document.addEventListener('DOMContentLoaded', function() {
    console.log('🟠 [users.js] DOMContentLoaded fired');
    const editUserForm = document.getElementById('editUserForm');
    console.log('🟡 [users.js] Buscando editUserForm:', editUserForm);
    if (editUserForm) {
        console.log('🟢 [users.js] Se encontró el formulario editUserForm y se agregará el eventListener');
        editUserForm.addEventListener('submit', async function(e) {
            e.preventDefault();
            console.log('🟡 [users.js] submit editUserForm');
            if (!window.editingUserData) {
                console.log('🔴 [users.js] No editingUserData');
                return;
            }
            const newRole = document.getElementById('editRole').value;
            const newStatus = document.getElementById('editStatus').value;
            console.log('🟢 [users.js] Valores seleccionados para actualizar:', { role: newRole, status: newStatus });
            try {
                const response = await apiService.updateUser(window.editingUserData.id, { role: newRole, status: newStatus });
                console.log('🟢 [users.js] Respuesta updateUser:', typeof response, response);
                if ((response && response.success) || (response && response.id)) {
                    showAlert('Usuario actualizado', 'success');
                    window.closeEditUserModal();
                    loadUsers();
                } else {
                    showAlert('Error al actualizar usuario', 'error');
                    console.warn('⚠️ [users.js] Respuesta inesperada updateUser:', response);
                }
            } catch (error) {
                console.error('🔴 [users.js] Error updateUser:', error);
                showAlert('Error de conexión al actualizar usuario', 'error');
            }
        });
    } else {
        console.error('🔴 [users.js] No se encontró el formulario editUserForm al cargar DOMContentLoaded');
    }
});
}

// Cambiar rol del usuario
async function changeUserRole(userId, newRole) {
    try {
        const response = await apiService.updateUser(userId, { role: newRole });
        if (response && response.success) {
            showAlert('Rol actualizado', 'success');
            loadUsers();
        } else {
            showAlert('Error al actualizar rol', 'error');
        }
    } catch (error) {
        showAlert('Error de conexión al actualizar rol', 'error');
    }
}

// Cambiar estado del usuario
async function changeUserStatus(userId, newStatus) {
    try {
        const response = await apiService.updateUser(userId, { status: newStatus });
        if (response && response.success) {
            showAlert('Estado actualizado', 'success');
            loadUsers();
        } else {
            showAlert('Error al actualizar estado', 'error');
        }
    } catch (error) {
        showAlert('Error de conexión al actualizar estado', 'error');
    }
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

function formatDate(dateString) {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleString('es-CO');
}
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
}

async function editUser(userId) {
    const currentUser = authService.getUser();
    if (!currentUser || currentUser.role !== 'ADMINISTRADOR') {
        showAlert('Solo el administrador puede editar usuarios.', 'error');
        return;
    }
    try {
        const response = await apiService.getUser(userId);
        if (response && response.success) {
            openEditUserModal(response.data);
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


// (fin de archivo)