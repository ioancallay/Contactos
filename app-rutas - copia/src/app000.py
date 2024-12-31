from flask import Flask, render_template, request, send_file, jsonify
import numpy as np
import networkx as nx
import matplotlib.pyplot as plt
import io
from PIL import Image
import mysql.connector

app = Flask(__name__)



# Configuración de los parámetros gamma y alfa para el Q-Learning
gamma = 0.75
alpha = 0.9

# Definición de los estados desde A hasta L con estado desde 0 hasta 11
location_to_state = {
    "A": 0,
    "B": 1,
    "C": 2,
    "D": 3,
    "E": 4,
    "F": 5,
    "G": 6,
    "H": 7,
    "I": 8,
    "J": 9,
    "K": 10,
    "L": 11,
}
state_to_location = {state: location for location, state in location_to_state.items()}

# Definición de las acciones con un arreglo desde 0 hasta 12 como los estados
# actions = {0,1,2,3,4,5,6,7,8,9,10,11,12}
# Definición de las acciones con un arreglo desde 0 hasta 12 como los estados usando la funcion range
actions = list(range(12))

# Definición de las recompensas de acuerdo al modelo utilizado para este ejercicio
R = np.array(
    [
        [0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0],
        [1, 0, 1, 0, 0, 1, 0, 0, 0, 0, 0, 0],
        [0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0],
        [0, 1, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0],
        [0, 0, 1, 0, 0, 0, 1, 1, 0, 0, 0, 0],
        [0, 0, 0, 1, 0, 0, 1, 0, 0, 0, 0, 1],
        [0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0],
        [0, 0, 0, 0, 0, 1, 0, 0, 1, 0, 1, 0],
        [0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 1],
        [0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 1, 0],
    ]
)


# Configuración de la conexión a MySQL en servidor remoto
def get_db_connection():
    return mysql.connector.connect(
        host="204.93.224.136",
        # user="root",
        user="ioasyste_iaproject",
        # password="",
        password="d,!4{bTrbYzz",
        # database="warehouse_db",
        database="ioasyste_warehouse_db",
    )


# Obtención del inventario de productos desde la base de datos en el servidor remoto
def get_inventory():
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT * FROM inventory")
    inventory = {row["state"]: row["product"] for row in cursor.fetchall()}
    conn.close()
    return inventory


# Obtención de los estados desde la tabla inventory
def get_states():
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT state FROM inventory")
    states = [row["state"] for row in cursor.fetchall()]
    conn.close()
    return states


# Obtener todos los productos ingresados en la base de datos
def get_products():
    conn = get_db_connection()
    cursor = conn.cursor(dictionary=True)
    cursor.execute("SELECT name, location, stock FROM products")
    products = [
        {"name": row["name"], "location": row["location"], "stock": row["stock"]}
        for row in cursor.fetchall()
    ]
    conn.close()
    return products


def route(starting_location, ending_location):
    R_new = np.copy(R)
    ending_state = location_to_state[ending_location]
    R_new[ending_state, ending_state] = 1000
    Q = np.array(np.zeros([12, 12]))
    for i in range(1000):
        current_state = np.random.randint(0, 12)
        playable_actions = []
        for j in range(12):
            if R_new[current_state, j] > 0:
                playable_actions.append(j)
        next_state = np.random.choice(playable_actions)
        TD = (
            R_new[current_state, next_state]
            + gamma * Q[next_state, np.argmax(Q[next_state,])]
            - Q[current_state, next_state]
        )
        Q[current_state, next_state] = Q[current_state, next_state] + alpha * TD
    route = [starting_location]
    next_location = starting_location
    while next_location != ending_location:
        starting_state = location_to_state[starting_location]
        next_state = np.argmax(Q[starting_state,])
        next_location = state_to_location[next_state]
        route.append(next_location)
        starting_location = next_location
    return route


def create_route_graph(route):
    G = nx.DiGraph()

    # Posiciones de los nodos de acuerdo a la distribucion de la bodega
    pos = {
        "A": (0, 4),
        "B": (2, 4),
        "C": (4, 4),
        "D": (6, 4),
        "E": (0, 2),
        "F": (2, 2),
        "G": (4, 2),
        "H": (6, 2),
        "I": (0, 0),
        "J": (2, 0),
        "K": (4, 0),
        "L": (6, 0),
    }

    for i in range(len(route) - 1):
        G.add_edge(route[i], route[i + 1])

    fig, ax = plt.subplots()

    # Cargar la imagen de la distribucion del almacen para determinar la ruta
    img = Image.open("imagen/image.png")
    ax.imshow(img, extent=[-1, 7, -1, 5])

    nx.draw(
        G,
        pos,
        with_labels=True,
        node_color="skyblue",
        node_size=3000,
        font_size=10,
        font_weight="bold",
        ax=ax,
    )

    # Etiquetas para la entrada y el ubicacion del producto
    ax.text(
        pos[route[0]][0],
        pos[route[0]][1] + 0.3,
        "Entrada",
        horizontalalignment="center",
        fontsize=12,
        color="red",
    )
    ax.text(
        pos[route[-1]][0],
        pos[route[-1]][1] + 0.3,
        "Producto",
        horizontalalignment="center",
        fontsize=12,
        color="green",
    )

    img_buf = io.BytesIO()
    plt.savefig(img_buf, format="png")
    img_buf.seek(0)
    return img_buf


@app.route("/")
def index():
    states = get_states()
    products = get_products()
    return render_template("index.html", states=states, products=products)


@app.route("/mdp_graph", methods=["POST"])
def mdp_graph():
    start = request.form["start"]
    end = request.form["end"]
    quantity = int(request.form["quantity"])

    products = get_products()
    for product in products:
        if product["location"] == end:
            if quantity > product["stock"]:
                return "Cantidad seleccionada excede el stock disponible.", 400

    best_route = route(start, end)
    img_buf = create_route_graph(best_route)
    return send_file(img_buf, mimetype="image/png")


# if __name__ == "__main__":
#     app.run(debug=True)
if __name__:
    app.run(host="0.0.0.0", port="5000", debug=True)