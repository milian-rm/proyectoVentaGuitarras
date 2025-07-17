drop database if exists DBconexionGuitarras;
create database DBconexionGuitarras;
use DBconexionGuitarras;

-- --------------********** TABLAS INDEPENDIENTES **********-------------------

Create table Usuarios(
    idUsuario int auto_increment,
    nombreUsuario varchar(50) not null,
    apellidoUsuario varchar (50) not null,
    emailUsuario varchar(100) not null unique,
    telefonoUsuario varchar(20),
    direccionUsuario varchar(255),
    fechaRegistro datetime not null,
    contrasena varchar(255) not null,
    constraint pk_usuarios primary key (idUsuario)
);

Create table Productos(
    idProducto int auto_increment,
    nombreProducto varchar(255) not null,
    descripcionProducto text,
    precio decimal(10,2) not null,
    stock int not null,
    categoria varchar(100),
    marca varchar(100),
    fechaCreacion datetime not null,
    constraint pk_productos primary key (idProducto)
);

-- -----------------********* TABLAS DEPENDIENTES ****************---------------

Create table Compras(
    idOrden int auto_increment,
    idUsuario int not null, 
    fechaOrden datetime not null,
    totalOrden decimal(10,2) not null,
    estadoOrden varchar(50) not null,
    constraint pk_ordenes primary key (idOrden),
    constraint fk_ordenes_usuarios foreign key (idUsuario) 
        references Usuarios(idUsuario)
        on delete cascade
);

Create table DetalleCompra(
    idDetalleOrden int auto_increment,
    idOrden int not null,
    idProducto int not null,
    cantidad int not null,
    precioUnitario decimal(10,2) not null,
    constraint pk_detalleordenes primary key (idDetalleOrden),
    constraint fk_detalleordenes_ordenes foreign key (idOrden)
        references Compras(idOrden)
        on delete cascade,
    constraint fk_detalleordenes_productos foreign key (idProducto)
        references Productos(idProducto)
        on delete cascade
);

-- *******************PROCEDIMIENTOS ALMACENADOS****************************


-- -------------USUARIOS


DELIMITER $$

create procedure sp_agregarUsuario(
    in p_nombre varchar(50),
    in p_apellido varchar(50),
    in p_email varchar(100),
    in p_telefono varchar(20),
    in p_direccion varchar(255),
    in p_fechaRegistro datetime,
    in p_contrasena varchar(255))
begin
    insert into Usuarios(nombreUsuario, apellidoUsuario, emailUsuario, telefonoUsuario, direccionUsuario, fechaRegistro, contrasena)
    values(p_nombre, p_apellido, p_email, p_telefono, p_direccion, p_fechaRegistro, p_contrasena);
end $$

DELIMITER ;

call sp_agregarUsuario('Juan', 'Perez', 'juan.perez@example.com', '555-1234', 'Calle Falsa 123', '2025-01-15 10:00:00', 'pass123');
call sp_agregarUsuario('Maria', 'Gomez', 'maria.gomez@example.com', '555-5678', 'Avenida Siempre Viva 742', '2025-01-15 11:30:00', 'securepass');
call sp_agregarUsuario('Pedro', 'Ramirez', 'pedro.r@example.com', '555-8765', 'Boulevard del Sol 45', '2025-01-16 09:15:00', 'clave_secreta');
call sp_agregarUsuario('Laura', 'Diaz', 'laura.d@example.com', '555-4321', 'Residencial Las Flores', '2025-01-16 14:00:00', 'password456');
call sp_agregarUsuario('Carlos', 'Sanchez', 'carlos.s@example.com', '555-9876', 'Zona 1, Casa 10', '2025-01-17 10:45:00', 'guitar_lover');
call sp_agregarUsuario('Ana', 'Fernandez', 'ana.f@example.com', '555-1111', 'Calle Luna 22', '2025-01-17 16:20:00', 'ana_pass');
call sp_agregarUsuario('Luis', 'Torres', 'luis.t@example.com', '555-2222', 'Av. Central 33', '2025-01-18 08:00:00', 'luis_seguro');
call sp_agregarUsuario('Sofia', 'Mendoza', 'sofia.m@example.com', '555-3333', 'Col. Vista Hermosa 44', '2025-01-18 13:00:00', 'sofia_key');
call sp_agregarUsuario('Daniel', 'Herrera', 'daniel.h@example.com', '555-4444', 'Pasaje Los Robles 55', '2025-01-19 09:30:00', 'daniel_safe');
call sp_agregarUsuario('Valeria', 'Ruiz', 'valeria.r@example.com', '555-5555', 'Blvd. Las Palmas 66', '2025-01-19 15:00:00', 'valeria_pass');
call sp_agregarUsuario('Gabriel', 'Castro', 'gabriel.c@example.com', '555-6666', 'Callejon del Gato 77', '2025-01-20 10:00:00', 'gabriel_secret');
call sp_agregarUsuario('Isabella', 'Silva', 'isabella.s@example.com', '555-7777', 'Zona Colonial 88', '2025-01-20 12:45:00', 'isabella_strong');
call sp_agregarUsuario('Mateo', 'Ortiz', 'mateo.o@example.com', '555-8888', 'Plaza Mayor 99', '2025-01-21 09:00:00', 'mateo_safe_pwd');
call sp_agregarUsuario('Camila', 'Vargas', 'camila.v@example.com', '555-9999', 'Sector Bosque 101', '2025-01-21 14:10:00', 'camila_secure');
call sp_agregarUsuario('Fernando', 'Navarro', 'fernando.n@example.com', '555-0000', 'Barrio Obrero 110', '2025-01-22 11:00:00', 'fernando_pass');

DELIMITER $$

create procedure sp_listarUsuarios()
begin
    select
        U.idUsuario as ID,
        U.nombreUsuario as NOMBRE,
        U.apellidoUsuario as APELLIDO,
        U.emailUsuario as EMAIL,
        U.telefonoUsuario as TELEFONO,
        U.direccionUsuario as DIRECCION,
        U.fechaRegistro as FECHA_REGISTRO,
		U.contrasena AS CONTRASENA
    from Usuarios U;
end $$

DELIMITER ;

call sp_listarUsuarios();

-- editar


DELIMITER $$

create procedure sp_editarUsuario(
    in p_idUsuario int,
    in p_nombre varchar(50),
    in p_apellido varchar(50),
    in p_email varchar(100),
    in p_telefono varchar(20),
    in p_direccion varchar(255),
    in p_fechaRegistro datetime,
    in p_contrasena varchar(255))
begin
    update Usuarios U
    set
        U.nombreUsuario = p_nombre,
        U.apellidoUsuario = p_apellido,
        U.emailUsuario = p_email,
        U.telefonoUsuario = p_telefono,
        U.direccionUsuario = p_direccion,
        U.fechaRegistro = p_fechaRegistro,
        U.contrasena = p_contrasena
    where U.idUsuario = p_idUsuario;
end $$

DELIMITER ;

-- eliminar


DELIMITER $$

create procedure sp_eliminarUsuario(in p_idUsuario int)
begin
    delete from Usuarios where idUsuario = p_idUsuario;
end $$

DELIMITER ;


-- buscar

DELIMITER $$

create procedure sp_buscarUsuario(in p_idUsuario int)
begin
    select
        U.idUsuario as ID,
        U.nombreUsuario as NOMBRE,
        U.apellidoUsuario as APELLIDO,
        U.emailUsuario as EMAIL,
        U.telefonoUsuario as TELEFONO,
        U.direccionUsuario as DIRECCION,
        U.fechaRegistro as FECHA_REGISTRO,
        U.contrasena as CONTRASENA
    from Usuarios U
    where U.idUsuario = p_idUsuario;
end $$

DELIMITER ;


-- -------------PRODUCTOS


DELIMITER $$

create procedure sp_agregarProducto(
    in p_nombre varchar(255),
    in p_descripcion text,
    in p_precio decimal(10,2),
    in p_stock int,
    in p_categoria varchar(100),
    in p_marca varchar(100),
    in p_fechaCreacion datetime)
begin
    insert into Productos(nombreProducto, descripcionProducto, precio, stock, categoria, marca, fechaCreacion)
    values(p_nombre, p_descripcion, p_precio, p_stock, p_categoria, p_marca, p_fechaCreacion);
end $$

DELIMITER ;

call sp_agregarProducto('Fender Stratocaster', 'Guitarra eléctrica icónica, sonido versátil.', 1200.00, 10, 'Guitarra Eléctrica', 'Fender', '2025-02-01 09:00:00');
call sp_agregarProducto('Gibson Les Paul', 'Guitarra eléctrica con tono cálido y sustain.', 1800.00, 5, 'Guitarra Eléctrica', 'Gibson', '2025-02-01 10:30:00');
call sp_agregarProducto('Martin D-28', 'Guitarra acústica dreadnought, sonido potente.', 2500.00, 3, 'Guitarra Acústica', 'Martin', '2025-02-02 11:00:00');
call sp_agregarProducto('Yamaha FG800', 'Guitarra acústica para principiantes, excelente relación calidad-precio.', 300.00, 20, 'Guitarra Acústica', 'Yamaha', '2025-02-02 14:00:00');
call sp_agregarProducto('Marshall DSL40CR', 'Amplificador de guitarra a válvulas, 40W.', 700.00, 7, 'Amplificador', 'Marshall', '2025-02-03 09:45:00');
call sp_agregarProducto('Boss Katana-50 MkII', 'Amplificador de modelado, 50W.', 250.00, 15, 'Amplificador', 'Boss', '2025-02-03 12:15:00');
call sp_agregarProducto('Cuerdas Ernie Ball Slinky', 'Juego de cuerdas para guitarra eléctrica.', 8.50, 100, 'Accesorios', 'Ernie Ball', '2025-02-04 10:00:00');
call sp_agregarProducto('Afinador Korg Pitchblack', 'Afinador cromático de pedal.', 60.00, 30, 'Accesorios', 'Korg', '2025-02-04 11:30:00');
call sp_agregarProducto('Taylor 114ce', 'Guitarra electroacústica, gran comodidad y tono.', 1000.00, 8, 'Guitarra Electroacústica', 'Taylor', '2025-02-05 09:00:00');
call sp_agregarProducto('Ibanez RG550', 'Guitarra eléctrica de alto rendimiento para metal.', 950.00, 6, 'Guitarra Eléctrica', 'Ibanez', '2025-02-05 13:00:00');
call sp_agregarProducto('Pedal de Distorsión MXR', 'Pedal de efectos para guitarra eléctrica.', 120.00, 25, 'Pedal de Efectos', 'MXR', '2025-02-06 10:00:00');
call sp_agregarProducto('Funda para Guitarra Acústica', 'Funda acolchada para protección de guitarra acústica.', 45.00, 50, 'Accesorios', 'Generica', '2025-02-06 14:00:00');
call sp_agregarProducto('Púas Dunlop Variety Pack', 'Paquete de púas de diferentes grosores.', 7.00, 75, 'Accesorios', 'Dunlop', '2025-02-07 09:30:00');
call sp_agregarProducto('Correa de Guitarra Levy''s', 'Correa de cuero para guitarra, ajustable.', 35.00, 40, 'Accesorios', 'Levy''s', '2025-02-07 11:00:00');
call sp_agregarProducto('Capo Shubb C1', 'Capo de alta calidad para guitarras acústicas y eléctricas.', 25.00, 60, 'Accesorios', 'Shubb', '2025-02-08 10:15:00');


DELIMITER $$

create procedure sp_listarProductos()
begin
    select
        P.idProducto as ID,
        P.nombreProducto as NOMBRE,
        P.descripcionProducto as DESCRIPCION,
        P.precio as PRECIO,
        P.stock as STOCK,
        P.categoria as CATEGORIA,
        P.marca as MARCA,
        P.fechaCreacion as FECHA_CREACION
    from Productos P;
end $$

DELIMITER ;

call sp_listarProductos();

-- editar


DELIMITER $$

create procedure sp_editarProducto(
    in p_idProducto int,
    in p_nombre varchar(255),
    in p_descripcion text,
    in p_precio decimal(10,2),
    in p_stock int,
    in p_categoria varchar(100),
    in p_marca varchar(100),
    in p_fechaCreacion datetime)
begin
    update Productos P
    set
        P.nombreProducto = p_nombre,
        P.descripcionProducto = p_descripcion,
        P.precio = p_precio,
        P.stock = p_stock,
        P.categoria = p_categoria,
        P.marca = p_marca,
        P.fechaCreacion = p_fechaCreacion
    where P.idProducto = p_idProducto;
end $$

DELIMITER ;

-- eliminar


DELIMITER $$

create procedure sp_eliminarProducto(in p_idProducto int)
begin
    delete from Productos where idProducto = p_idProducto;
end $$

DELIMITER ;


-- buscar


DELIMITER $$

create procedure sp_buscarProducto(in p_idProducto int)
begin
    select
        P.idProducto as ID,
        P.nombreProducto as NOMBRE,
        P.descripcionProducto as DESCRIPCION,
        P.precio as PRECIO,
        P.stock as STOCK,
        P.categoria as CATEGORIA,
        P.marca as MARCA,
        P.fechaCreacion as FECHA_CREACION
    from Productos P
    where P.idProducto = p_idProducto;
end $$

DELIMITER ;



-- -------------COMPRAS


DELIMITER $$

create procedure sp_agregarCompra(
    in p_idUsuario int,
    in p_fechaOrden datetime,
    in p_totalOrden decimal(10,2),
    in p_estadoOrden varchar(50))
begin
    insert into Compras(idUsuario, fechaOrden, totalOrden, estadoOrden)
    values(p_idUsuario, p_fechaOrden, p_totalOrden, p_estadoOrden);
end $$

DELIMITER ;

call sp_agregarCompra(1, '2025-03-01 10:00:00', 1200.00, 'Completada');
call sp_agregarCompra(2, '2025-03-01 11:00:00', 2500.00, 'Pendiente');
call sp_agregarCompra(1, '2025-03-02 09:30:00', 700.00, 'Enviada');
call sp_agregarCompra(3, '2025-03-02 14:00:00', 300.00, 'Completada');
call sp_agregarCompra(4, '2025-03-03 10:15:00', 1800.00, 'Cancelada');
call sp_agregarCompra(5, '2025-03-03 15:00:00', 1000.00, 'Completada');
call sp_agregarCompra(6, '2025-03-04 09:00:00', 250.00, 'Pendiente');
call sp_agregarCompra(7, '2025-03-04 11:45:00', 8.50, 'Enviada');
call sp_agregarCompra(8, '2025-03-05 10:00:00', 60.00, 'Completada');
call sp_agregarCompra(9, '2025-03-05 13:30:00', 950.00, 'Pendiente');
call sp_agregarCompra(10, '2025-03-06 10:00:00', 120.00, 'Completada');
call sp_agregarCompra(11, '2025-03-06 14:00:00', 45.00, 'Enviada');
call sp_agregarCompra(12, '2025-03-07 09:15:00', 7.00, 'Completada');
call sp_agregarCompra(13, '2025-03-07 12:30:00', 35.00, 'Pendiente');
call sp_agregarCompra(14, '2025-03-08 10:00:00', 25.00, 'Completada');


DELIMITER $$

create procedure sp_listarCompras()
begin
    select
        C.idOrden as ID_ORDEN,
        C.idUsuario as ID_USUARIO,
        U.nombreUsuario as NOMBRE_USUARIO,
        U.apellidoUsuario as APELLIDO_USUARIO,
        C.fechaOrden as FECHA_ORDEN,
        C.totalOrden as TOTAL_ORDEN,
        C.estadoOrden as ESTADO_ORDEN
    from Compras C
    join Usuarios U on C.idUsuario = U.idUsuario;
end $$

DELIMITER ;

call sp_listarCompras();

-- editar


DELIMITER $$

create procedure sp_editarCompra(
    in p_idOrden int,
    in p_idUsuario int,
    in p_fechaOrden datetime,
    in p_totalOrden decimal(10,2),
    in p_estadoOrden varchar(50))
begin
    update Compras C
    set
        C.idUsuario = p_idUsuario,
        C.fechaOrden = p_fechaOrden,
        C.totalOrden = p_totalOrden,
        C.estadoOrden = p_estadoOrden
    where C.idOrden = p_idOrden;
end $$

DELIMITER ;

-- eliminar

DELIMITER $$

create procedure sp_eliminarCompra(in p_idOrden int)
begin
    delete from Compras where idOrden = p_idOrden;
end $$

DELIMITER ;

-- buscar


DELIMITER $$

create procedure sp_buscarCompra(in p_idOrden int)
begin
    select
        C.idOrden as ID_ORDEN,
        C.idUsuario as ID_USUARIO,
        U.nombreUsuario as NOMBRE_USUARIO,
        U.apellidoUsuario as APELLIDO_USUARIO,
        C.fechaOrden as FECHA_ORDEN,
        C.totalOrden as TOTAL_ORDEN,
        C.estadoOrden as ESTADO_ORDEN
    from Compras C
    join Usuarios U on C.idUsuario = U.idUsuario
    where C.idOrden = p_idOrden;
end $$

DELIMITER ;


-- -------------DETALLE COMPRA


DELIMITER $$
create procedure sp_agregarDetalleCompra(
    in p_idOrden int,
    in p_idProducto int,
    in p_cantidad int,
    in p_precioUnitario decimal(10,2),
    out p_idDetalleOrden int
)
begin
    insert into DetalleCompra(idOrden, idProducto, cantidad, precioUnitario)
    values(p_idOrden, p_idProducto, p_cantidad, p_precioUnitario);
    
    set p_idDetalleOrden = LAST_INSERT_ID();
end $$
DELIMITER ;

set @idDetalleGenerado = 0;

call sp_agregarDetalleCompra(1, 1, 1, 1200.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 2, 3, 2500.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 3, 5, 700.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 4, 4, 300.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 5, 2, 1800.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 1, 7, 8.50, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 3, 8, 60.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 6, 9, 1000.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 7, 6, 250.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 8, 7, 8.50, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 9, 10, 950.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 10, 11, 120.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 11, 12, 45.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 12, 13, 7.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 13, 14, 35.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 14, 15, 25.00, @idDetalleGenerado);
call sp_agregarDetalleCompra(1, 15, 1, 1200.00, @idDetalleGenerado);


DELIMITER $$
create procedure sp_listarDetalleCompra()
begin
    select
        DC.idDetalleOrden as ID_DETALLE,
        DC.idOrden as ID_ORDEN,
        DC.idProducto as ID_PRODUCTO, 
        P.nombreProducto as NOMBRE_PRODUCTO, 
        DC.cantidad as CANTIDAD,
        DC.precioUnitario as PRECIO_UNITARIO,
        (DC.cantidad * DC.precioUnitario) as SUBTOTAL
    from DetalleCompra DC
    join Productos P on DC.idProducto = P.idProducto;
end $$
DELIMITER ;

call sp_listarDetalleCompra();

-- editar


DELIMITER $$

create procedure sp_editarDetalleCompra(
    in p_idDetalleOrden int,
    in p_idOrden int,
    in p_idProducto int,
    in p_cantidad int,
    in p_precioUnitario decimal(10,2))
begin
    update DetalleCompra DC
    set
        DC.idOrden = p_idOrden,
        DC.idProducto = p_idProducto,
        DC.cantidad = p_cantidad,
        DC.precioUnitario = p_precioUnitario
    where DC.idDetalleOrden = p_idDetalleOrden;
end $$

DELIMITER ;

-- eliminar

DELIMITER $$

create procedure sp_eliminarDetalleCompra(in p_idDetalleOrden int)
begin
    delete from DetalleCompra where idDetalleOrden = p_idDetalleOrden;
end $$

DELIMITER ;

-- buscar

DELIMITER $$

create procedure sp_buscarDetalleCompra(in p_idDetalleOrden int)
begin
    select
        DC.idDetalleOrden as ID_DETALLE,
        DC.idOrden as ID_ORDEN,
        P.nombreProducto as PRODUCTO,
        DC.cantidad as CANTIDAD,
        DC.precioUnitario as PRECIO_UNITARIO,
        (DC.cantidad * DC.precioUnitario) as SUBTOTAL
    from DetalleCompra DC
    join Productos P on DC.idProducto = P.idProducto
    where DC.idDetalleOrden = p_idDetalleOrden;
end $$

DELIMITER ;

delimiter //

	create procedure sp_ValidarUsuario(
		in p_correo varchar(100),
		in p_contrasena varchar(100))
		begin
			select * from Usuarios U
			where U.emailUsuario = p_correo and U.contrasena = p_contrasena;
		end //
delimiter ;


delimiter //
	create procedure sp_listarDetalleCompraEspecifico(in p_idOrden int)
		begin
			select
				DC.idDetalleOrden as ID_DETALLE,
				DC.idOrden as ID_ORDEN,
				DC.idProducto as ID_PRODUCTO, 
				DC.cantidad as CANTIDAD,
				DC.precioUnitario as PRECIO_UNITARIO,
				(DC.cantidad * DC.precioUnitario) as SUBTOTAL
			from DetalleCompra DC
			where DC.idOrden = p_idOrden;
        end//
delimiter ;

-- finnn
call sp_listarDetalleCompraEspecifico(1);


call sp_ListarDetalleCompra();

delimiter $$
create procedure sp_crearOrdenVacia(
    in p_idUsuario int,
    out p_idOrdenGenerada int
)
begin
    insert into Compras (idUsuario, fechaOrden, totalOrden, estadoOrden)
    values (p_idUsuario, NOW(), 0.00, 'Pendiente');
    
    set p_idOrdenGenerada = LAST_INSERT_ID();
end $$
delimiter ;
