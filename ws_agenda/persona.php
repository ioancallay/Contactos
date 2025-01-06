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

// Acciones según el valor de 'accion'
switch ($post['accion']) {
    case 'consultar':
        $sql = "SELECT * FROM persona";
        $query = mysqli_query($conn, $sql);

        if ($query && mysqli_num_rows($query) > 0) {
            while ($row = mysqli_fetch_assoc($query)) {
                $data[] = [
                    'codigo' => $row['cod_persona'],
                    'nombre' => $row['nom_persona'],
                    'apellido' => $row['ape_persona'],
                    'cedula' => $row['ci_persona'],
                    'correo' => $row['correo_persona'],
                    'clave' => $row['clave_persona']
                ];
            }
            $respuesta = ['code' => 200, 'response' => 'Data fetched successfully', 'estado' => true, 'data' => $data];
        } else {
            $respuesta = ['code' => 400, 'response' => 'No data found', 'estado' => false];
        }
        break;

    case 'insertar':
        $hashedPassword = password_hash($post['clave'], PASSWORD_DEFAULT);
        $sql = sprintf(
            "INSERT INTO persona (nom_persona, ape_persona, ci_persona, correo_persona, clave_persona) 
            VALUES ('%s', '%s', '%s', '%s', '%s')",
            mysqli_real_escape_string($conn, $post['nombre']),
            mysqli_real_escape_string($conn, $post['apellido']),
            mysqli_real_escape_string($conn, $post['cedula']),
            mysqli_real_escape_string($conn, $post['correo']),
            mysqli_real_escape_string($conn, $hashedPassword)
        );
        $query = mysqli_query($conn, $sql);

        $respuesta = $query
            ? ['code' => 200, 'response' => 'Data inserted successfully', 'estado' => true]
            : ['code' => 400, 'response' => 'Failed to insert data', 'estado' => false];
        break;

    case 'actualizar':
        $sql = sprintf(
            "UPDATE persona 
            SET nom_persona='%s', ape_persona='%s', ci_persona='%s', correo_persona='%s', clave_persona='%s' 
            WHERE cod_persona='%s'",
            mysqli_real_escape_string($conn, $post['nombre']),
            mysqli_real_escape_string($conn, $post['apellido']),
            mysqli_real_escape_string($conn, $post['cedula']),
            mysqli_real_escape_string($conn, $post['correo']),
            mysqli_real_escape_string($conn, $post['clave']),
            mysqli_real_escape_string($conn, $post['codigo'])
        );
        $query = mysqli_query($conn, $sql);

        $respuesta = $query
            ? ['code' => 200, 'response' => 'Data updated successfully', 'estado' => true]
            : ['code' => 400, 'response' => 'Failed to update data', 'estado' => false];
        break;

    case 'eliminar':
        $sql = sprintf(
            "DELETE FROM persona WHERE cod_persona='%s'",
            mysqli_real_escape_string($conn, $post['codigo'])
        );
        $query = mysqli_query($conn, $sql);

        $respuesta = $query
            ? ['code' => 200, 'response' => 'Data deleted successfully', 'estado' => true]
            : ['code' => 400, 'response' => 'Failed to delete data', 'estado' => false];
        break;

    case 'verificar_cedula':
        $sql = sprintf(
            "SELECT * FROM persona WHERE ci_persona ='%s'",
            mysqli_real_escape_string($conn, $post['cedula'])
        );
        $query = mysqli_query($conn, $sql);

        if ($query && mysqli_num_rows($query) > 0) {
            $respuesta = ['code' => 200, 'response' => 'Cedula already exist', 'estado' => true];
        } else {
            $respuesta = ['code' => 400, 'response' => 'No data found', 'estado' => false];
        }
        break;

    case 'verificar_correo':
        $sql = sprintf(
            "SELECT * FROM persona WHERE correo_persona ='%s'",
            mysqli_real_escape_string($conn, $post['correo'])
        );
        $query = mysqli_query($conn, $sql);

        if ($query && mysqli_num_rows($query) > 0) {
            $respuesta = ['code' => 200, 'response' => 'Email already exist', 'estado' => true];
        } else {
            $respuesta = ['code' => 400, 'response' => 'No data found', 'estado' => false];
        }
        break;

    case 'dato':
        $sql = sprintf(
            "SELECT * FROM persona WHERE cod_persona='%s'",
            mysqli_real_escape_string($conn, $post['codigo'])
        );
        $query = mysqli_query($conn, $sql);

        if ($query && mysqli_num_rows($query) > 0) {
            while ($row = mysqli_fetch_assoc($query)) {
                $data[] = [
                    'codigo' => $row['cod_persona'],
                    'nombre' => $row['nom_persona'],
                    'apellido' => $row['ape_persona'],
                    'cedula' => $row['ci_persona'],
                    'correo' => $row['correo_persona'],
                    'clave' => $row['clave_persona']
                ];
            }
            $respuesta = ['code' => 200, 'response' => 'Data fetched successfully', 'estado' => true, 'data' => $data];
        } else {
            $respuesta = ['code' => 400, 'response' => 'No data found', 'estado' => false];
        }
        break;

    default:
        $respuesta = ['code' => 400, 'response' => 'Invalid action', 'estado' => false];
        break;
}

// Devolver la respuesta en formato JSON
echo json_encode($respuesta);
