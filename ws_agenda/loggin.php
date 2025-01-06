<?php

include('config.php');

// Configurar encabezados
header('Access-Control-Allow-Origin: *');
header('Access-Control-Allow-Credentials: true');
header('Access-Control-Allow-Methods: GET, POST, PUT, DELETE, OPTIONS');
header('Access-Control-Allow-Headers: Content-Type, Authorization, X-Requested-With, Origin, Accept');
header('Content-Type: application/json; charset=utf-8');

// Leer el cuerpo de la solicitud
$post = json_decode(file_get_contents('php://input'), true);

// Verificar si hay acción definida
if (!isset($post['accion'])) {
    echo json_encode(['code' => 400, 'response' => 'No action provided', 'estado' => false]);
    exit;
}

// Variables globales
$respuesta = [];
$data = [];

switch ($post['accion']) {
    case 'login':
        $email = $post['email'];
        //$hashedPassword = password_hash($post['password'], PASSWORD_BCRYPT);
        //$hashedPassword = md5($post['password']);
        $hashedPassword = $post['password'];

        // Verificar si el usuario existe
        $sql = sprintf(
            "SELECT * FROM persona WHERE correo_persona='%s'",
            mysqli_real_escape_string($conn, $post['email'])
        );
        $query = mysqli_query($conn, $sql);

        if ($query->num_rows > 0) {
            $row = $query->fetch_assoc();
            // Verificar la contraseña
            if (password_verify($hashedPassword, $row['clave_persona'])) {
                $data = $row;
                $respuesta = ['code' => 200, 'response' => 'Login successful', 'estado' => true];
            } else {
                $respuesta = ['code' => 400, 'response' => 'Invalid credentials', 'estado' => false];
            }
        } else {
            $respuesta = ['code' => 400, 'response' => 'User not found', 'estado' => false];
        }
        break;

    default:
        $respuesta = ['code' => 400, 'response' => 'Invalid action', 'estado' => false];
        break;
}
echo json_encode(['respuesta' => $respuesta, 'data' => $data]);
