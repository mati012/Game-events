document.addEventListener('DOMContentLoaded', function () {
    const form = document.querySelector('form');
    const password = document.getElementById('password');
    const confirmPassword = document.getElementById('confirmPassword');
    const errorDiv = document.createElement('div'); // Div para mensajes de error (fallback para navegadores antiguos)

    // Configuración inicial del div de error
    errorDiv.id = 'password-error';
    errorDiv.style.color = 'red';
    errorDiv.style.display = 'none'; // Oculto por defecto
    confirmPassword.parentElement.appendChild(errorDiv);

    // Validación del formulario en el evento "submit"
    form.addEventListener('submit', function (event) {
        if (!validatePasswords()) {
            event.preventDefault(); // Evita que el formulario se envíe si las contraseñas no coinciden
            confirmPassword.reportValidity();
        }
    });

    // Validación continua en el campo de confirmación de contraseña
    confirmPassword.addEventListener('input', function () {
        validatePasswords();
    });

    // Función para validar las contraseñas
    function validatePasswords() {
        if (password.value.length > 0 && confirmPassword.value.length > 0) {
            if (password.value !== confirmPassword.value) {
                confirmPassword.setCustomValidity("Las contraseñas no coinciden");
                errorDiv.textContent = "Las contraseñas no coinciden.";
                errorDiv.style.display = "block";
                return false;
            } else {
                confirmPassword.setCustomValidity("");
                errorDiv.textContent = "";
                errorDiv.style.display = "none";
                return true;
            }
        } else {
            confirmPassword.setCustomValidity(""); // Resetea el mensaje de error si los campos están vacíos
            errorDiv.textContent = "";
            errorDiv.style.display = "none";
            return true;
        }
    }
});