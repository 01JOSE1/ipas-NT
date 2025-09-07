// policies.js
// Mostrar la opción Usuarios en el menú lateral si el usuario es administrador
window.addEventListener('DOMContentLoaded', () => {
    if (typeof authService !== 'undefined') {
        const user = authService.getUser();
        if (user && user.role === 'ADMINISTRADOR') {
            document.getElementById('usersNav').style.display = 'block';
        }
    }
});
