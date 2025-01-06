<?php
// Definir constantes para los detalles de conexión
define('DB_HOST', 'www.ioasystem.com');
define('DB_USER', 'ioasyste_agenda');
define('DB_PASS', 'Agenda2425');
define('DB_NAME', 'ioasyste_agenda');

// Crear conexión
$conn = mysqli_connect(DB_HOST, DB_USER, DB_PASS, DB_NAME);

// Verificar conexión
// if (!$conn) {
//     die("Connection failed: " . mysqli_connect_error());
// } else {
//     echo "Conexión exitosa";
// }
