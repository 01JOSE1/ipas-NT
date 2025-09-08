// policies.js
// Mostrar la opción Usuarios en el menú lateral si el usuario es administrador


document.addEventListener('DOMContentLoaded', function() {
    if (typeof authService !== 'undefined') {
        const user = authService.getUser();
        console.log('[POLICIES] Rol del usuario:', user.role);
        if (user && user.role === 'ADMINISTRADOR') {
            document.getElementById('usersNav').style.display = 'block';
            // Oculta la tabla y controles de pólizas para ADMINISTRADOR
            const card = document.querySelector('.card');
            if (card) card.style.display = 'none';
            const pageHeader = document.querySelector('.page-header');
            if (pageHeader) pageHeader.style.display = 'none';
            return;
        }
        // Si es REGISTRADO, mostrar solo pantalla de espera
        if (user && user.role === 'REGISTRADO') {
            const mainContent = document.querySelector('.main-content');
            if (mainContent) {
                mainContent.innerHTML = '<div class="waiting-confirmation"><h2>Esperando confirmación del administrador...</h2><p>Tu cuenta está pendiente de aprobación. Por favor, espera a que un administrador confirme tu acceso.</p></div>';
            }
            return;
        }
        // El resto de roles (ASESOR) pueden ver y gestionar pólizas normalmente
    }
});
